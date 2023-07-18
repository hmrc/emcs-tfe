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

import play.api.http.HeaderNames
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.models.request.ChrisRequest
import uk.gov.hmrc.emcstfe.services.MetricsService
import uk.gov.hmrc.emcstfe.utils.Logging
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads}

import scala.concurrent.{ExecutionContext, Future}

trait BaseChrisConnector extends Logging {

  def appConfig: AppConfig
  def metricsService: MetricsService

  private def chrisHeaders(action: String): Seq[(String, String)] = Seq(
    HeaderNames.ACCEPT -> "application/soap+xml",
    HeaderNames.CONTENT_TYPE -> s"""application/soap+xml; charset=UTF-8; action="$action""""
  )

  private def withTimer[T](chrisRequest: ChrisRequest)(f: => Future[T])(implicit ec: ExecutionContext): Future[T] =
    metricsService.processWithTimer(metricsService.chrisTimer(chrisRequest.metricName).time())(f)

  def postString[A, B](http: HttpClient, uri: String, body: String, request: ChrisRequest)
                      (implicit ec: ExecutionContext, hc: HeaderCarrier, rds: HttpReads[Either[A,B]]): Future[Either[A, B]] = {
    withTimer(request) {
      logger.debug(s"[postString] POST to $uri being made with body:\n\n$body")
      http.POSTString[Either[A, B]](uri, body, chrisHeaders(request.action))
    }
  }
}
