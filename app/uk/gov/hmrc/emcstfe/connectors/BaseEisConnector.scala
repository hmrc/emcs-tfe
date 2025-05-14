/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.emcstfe.connectors

import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.models.request.eis.{EisConsumptionRequest, EisHeaders, EisSubmissionRequest, Source}
import uk.gov.hmrc.emcstfe.services.MetricsService
import uk.gov.hmrc.emcstfe.utils.{RequestHelper, Logging}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{Authorization, HeaderCarrier, HttpReads, StringContextOps}

import java.time.Instant
import java.time.format.{DateTimeFormatter, DateTimeFormatterBuilder}
import scala.concurrent.{ExecutionContext, Future}

trait BaseEisConnector extends Logging with RequestHelper {

  def metricsService: MetricsService

  def bearer(token: String) = s"Bearer $token"

  def now: Instant = Instant.now

  //Note: required to always output 3 fractional digits to include .000Z (by default, .000 would be removed)
  val dateTimeFormatter: DateTimeFormatter = new DateTimeFormatterBuilder()
    .parseCaseInsensitive()
    .appendInstant(3)
    .toFormatter

  private def eisSubmissionHeaders(correlationId: String, forwardedHost: String, token: String, source: Source): Seq[(String, String)] = Seq(
    EisHeaders.dateTime -> dateTimeFormatter.format(now),
    EisHeaders.correlationId -> correlationId,
    EisHeaders.forwardedHost -> forwardedHost,
    EisHeaders.source -> source.toString,
    EisHeaders.contentType -> "application/json",
    EisHeaders.accept -> "application/json",
    EisHeaders.authorization -> bearer(token)
  )

  private def eisConsumptionHeaders(correlationId: String, forwardedHost: String, token: String, source: Source, extraHeaders: Seq[(String, String)]): Seq[(String, String)] = Seq(
    EisHeaders.dateTime -> dateTimeFormatter.format(now),
    EisHeaders.correlationId -> correlationId,
    EisHeaders.forwardedHost -> forwardedHost,
    EisHeaders.source -> source.toString,
    EisHeaders.authorization -> bearer(token)
  ) ++ extraHeaders

  private def withTimer[T](metricName: String)(f: => Future[T])(implicit ec: ExecutionContext): Future[T] = {
    val timer = metricsService.requestTimer(metricName).time()
    metricsService.processWithTimer(timer)(f)
  }

  private def headerCarrierWithBearerTokenOverride(hc: HeaderCarrier, bearerToken: String): HeaderCarrier = {
    hc.copy(authorization = Some(Authorization(bearer(bearerToken))))
  }

  def postJson[A, B](http: HttpClientV2, uri: String, body: JsValue, request: EisSubmissionRequest, bearerToken: String)
                    (implicit ec: ExecutionContext, hc: HeaderCarrier, rds: HttpReads[Either[A, B]], appConfig: AppConfig): Future[Either[A, B]] = {
    withTimer(request.metricName) {
      val forwardedHost = appConfig.eisForwardedHost()
      logger.debug(s"[postJson] POST to $uri being made with body:\n\n$body")
      val newHC = headerCarrierWithBearerTokenOverride(hc, bearerToken)
      http
        .post(url"$uri")(newHC)
        .withBody(Json.toJson(body))
        .setHeader(eisSubmissionHeaders(request.correlationUUID, forwardedHost, bearerToken, request.source): _*)
        .execute[Either[A, B]]
    }
  }

  def get[A, B](http: HttpClientV2, uri: String, request: EisConsumptionRequest, bearerToken: String)
               (implicit ec: ExecutionContext, hc: HeaderCarrier, rds: HttpReads[Either[A, B]], appConfig: AppConfig): Future[Either[A, B]] = {
    withTimer(request.metricName) {
      val forwardedHost = appConfig.eisForwardedHost()
      logger.debug(s"[get] GET to $uri being made with query params ${request.queryParams}")
      val newHC = headerCarrierWithBearerTokenOverride(hc, bearerToken)
      val urlWithQuery = uri + makeQueryString(request.queryParams)
      http
        .get(url"$urlWithQuery")(newHC)
        .setHeader(eisConsumptionHeaders(request.correlationUUID, forwardedHost, bearerToken, request.source, request.extraHeaders): _*)
        .execute[Either[A, B]]
    }
  }

  def putEmpty[A, B](http: HttpClientV2, uri: String, request: EisConsumptionRequest, bearerToken: String)
               (implicit ec: ExecutionContext, hc: HeaderCarrier, rds: HttpReads[Either[A, B]], appConfig: AppConfig): Future[Either[A, B]] = {
    withTimer(request.metricName) {
      val forwardedHost = appConfig.eisForwardedHost()
      logger.debug(s"[putEmpty] PUT to $uri being made with empty body")
      val newHC = headerCarrierWithBearerTokenOverride(hc, bearerToken)
      http
        .put(url"$uri")(newHC)
        .withBody(Json.toJson(""))
        .setHeader(eisConsumptionHeaders(request.correlationUUID, forwardedHost, bearerToken, request.source, request.extraHeaders): _*)
        .execute[Either[A, B]]
    }
  }

  def delete[A, B](http: HttpClientV2, uri: String, request: EisConsumptionRequest, bearerToken: String)
               (implicit ec: ExecutionContext, hc: HeaderCarrier, rds: HttpReads[Either[A, B]], appConfig: AppConfig): Future[Either[A, B]] = {
    withTimer(request.metricName) {
      val forwardedHost = appConfig.eisForwardedHost()
      logger.debug(s"[delete] DELETE to $uri being made")
      val newHC = headerCarrierWithBearerTokenOverride(hc, bearerToken)
      http
        .delete(url"$uri")(newHC)
        .setHeader(eisConsumptionHeaders(request.correlationUUID, forwardedHost, bearerToken, request.source, request.extraHeaders): _*)
        .execute[Either[A, B]]
    }
  }
}
