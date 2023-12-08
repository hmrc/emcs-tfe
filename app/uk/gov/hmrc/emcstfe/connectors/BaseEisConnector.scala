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

import play.api.libs.json.JsValue
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.models.request.eis.{EisConsumptionRequest, EisHeaders, EisSubmissionRequest}
import uk.gov.hmrc.emcstfe.services.MetricsService
import uk.gov.hmrc.emcstfe.utils.Logging
import uk.gov.hmrc.http.{Authorization, HeaderCarrier, HttpClient, HttpReads}

import java.time.Instant
import java.time.temporal.ChronoUnit
import scala.concurrent.{ExecutionContext, Future}

trait BaseEisConnector extends Logging {

  def metricsService: MetricsService

  def bearer(token: String) = s"Bearer $token"

  private def eisSubmissionHeaders(correlationId: String, forwardedHost: String, token: String): Seq[(String, String)] = Seq(
    EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
    EisHeaders.correlationId -> correlationId,
    EisHeaders.forwardedHost -> forwardedHost,
    EisHeaders.source -> "TFE",
    EisHeaders.contentType -> "application/json",
    EisHeaders.accept -> "application/json",
    EisHeaders.authorization -> bearer(token)
  )

  private def eisConsumptionHeaders(correlationId: String, forwardedHost: String, token: String): Seq[(String, String)] = Seq(
    EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
    EisHeaders.correlationId -> correlationId,
    EisHeaders.forwardedHost -> forwardedHost,
    EisHeaders.source -> "TFE",
    EisHeaders.authorization -> bearer(token)
  )

  private def withTimer[T](metricName: String)(f: => Future[T])(implicit ec: ExecutionContext): Future[T] = {
    val timer = metricsService.requestTimer(metricName).time()
    metricsService.processWithTimer(timer)(f)
  }

  private def headerCarrierWithBearerTokenOverride(hc: HeaderCarrier, bearerToken: String): HeaderCarrier = {
    hc.copy(authorization = Some(Authorization(bearer(bearerToken))))
  }

  def postJson[A, B](http: HttpClient, uri: String, body: JsValue, request: EisSubmissionRequest, bearerToken: String)
                    (implicit ec: ExecutionContext, hc: HeaderCarrier, rds: HttpReads[Either[A, B]], appConfig: AppConfig): Future[Either[A, B]] = {
    withTimer(request.metricName) {
      val forwardedHost = appConfig.eisForwardedHost()
      logger.debug(s"[postJson] POST to $uri being made with body:\n\n$body")
      val newHC = headerCarrierWithBearerTokenOverride(hc, bearerToken)
      http.POST[JsValue, Either[A, B]](uri, body, eisSubmissionHeaders(request.correlationUUID, forwardedHost, bearerToken))(implicitly, rds, newHC, ec)
    }
  }

  def get[A, B](http: HttpClient, uri: String, request: EisConsumptionRequest, bearerToken: String)
               (implicit ec: ExecutionContext, hc: HeaderCarrier, rds: HttpReads[Either[A, B]], appConfig: AppConfig): Future[Either[A, B]] = {
    withTimer(request.metricName) {
      val forwardedHost = appConfig.eisForwardedHost()
      logger.debug(s"[get] GET to $uri being made with query params ${request.queryParams}")
      val newHC = headerCarrierWithBearerTokenOverride(hc, bearerToken)
      http.GET[Either[A, B]](uri, request.queryParams, eisConsumptionHeaders(request.correlationUUID, forwardedHost, bearerToken))(rds, newHC, ec)
    }
  }

  def putEmpty[A, B](http: HttpClient, uri: String, request: EisConsumptionRequest, bearerToken: String)
               (implicit ec: ExecutionContext, hc: HeaderCarrier, rds: HttpReads[Either[A, B]], appConfig: AppConfig): Future[Either[A, B]] = {
    withTimer(request.metricName) {
      val forwardedHost = appConfig.eisForwardedHost()
      logger.debug(s"[putEmpty] PUT to $uri being made with empty body")
      val newHC = headerCarrierWithBearerTokenOverride(hc, bearerToken)
      http.PUTString[Either[A, B]](uri, "", eisConsumptionHeaders(request.correlationUUID, forwardedHost, bearerToken))(rds, newHC, ec)
    }
  }

  def delete[A, B](http: HttpClient, uri: String, request: EisConsumptionRequest, bearerToken: String)
               (implicit ec: ExecutionContext, hc: HeaderCarrier, rds: HttpReads[Either[A, B]], appConfig: AppConfig): Future[Either[A, B]] = {
    withTimer(request.metricName) {
      val forwardedHost = appConfig.eisForwardedHost()
      logger.debug(s"[delete] DELETE to $uri being made")
      val newHC = headerCarrierWithBearerTokenOverride(hc, bearerToken)
      http.DELETE[Either[A, B]](uri, eisConsumptionHeaders(request.correlationUUID, forwardedHost, bearerToken))(rds, newHC, ec)
    }
  }
}
