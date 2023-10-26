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
import uk.gov.hmrc.emcstfe.models.request.eis.{EisHeaders, EisRequest}
import uk.gov.hmrc.emcstfe.services.MetricsService
import uk.gov.hmrc.emcstfe.utils.Logging
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads}

import java.time.Instant
import java.time.temporal.ChronoUnit
import scala.concurrent.{ExecutionContext, Future}

trait BaseEisConnector extends Logging {

  def metricsService: MetricsService

  private def eisHeaders(correlationId: String, forwardedHost: String): Seq[(String, String)] = Seq(
    EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
    EisHeaders.correlationId -> correlationId,
    EisHeaders.forwardedHost -> forwardedHost,
    EisHeaders.source -> "TFE",
    EisHeaders.contentType -> "application/json",
    EisHeaders.accept -> "application/json"
  )

  private def withTimer[T](eisRequest: EisRequest)(f: => Future[T])(implicit ec: ExecutionContext): Future[T] = {
    val timer = metricsService.requestTimer(eisRequest.metricName).time()
    metricsService.processWithTimer(timer)(f)
  }

  def postJson[A, B](http: HttpClient, uri: String, body: JsValue, request: EisRequest)
                      (implicit ec: ExecutionContext, hc: HeaderCarrier, rds: HttpReads[Either[A, B]], appConfig: AppConfig): Future[Either[A, B]] = {
    withTimer(request) {
      val forwardedHost = appConfig.eisForwardedHost()
      logger.debug(s"[postJson] POST to $uri being made with body:\n\n$body")
      http.POST[JsValue, Either[A, B]](uri, body, eisHeaders(request.correlationUUID.toString, forwardedHost))
    }
  }
}
