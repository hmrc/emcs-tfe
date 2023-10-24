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

import play.api.libs.json.Reads
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.connectors.httpParsers.EisJsonHttpParser
import uk.gov.hmrc.emcstfe.models.request.eis.EisRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.services.MetricsService
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class EisConnector @Inject()(val http: HttpClient,
                             appConfig: AppConfig,
                             override val metricsService: MetricsService,
                             httpParser: EisJsonHttpParser
                            ) extends BaseEisConnector {

  private def prepareJsonAndSubmit[A](url: String, request: EisRequest, callingMethod: String)
                                     (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, jsonReads: Reads[A]): Future[Either[ErrorResponse, A]] = {
    logger.debug(s"[$callingMethod] Sending to URL: $url")
    logger.debug(s"[$callingMethod] Sending body: ${request.toJson}")
    postJson(http, url, request.toJson, request)(ec, headerCarrier, httpParser.modelFromJsonHttpReads, appConfig)
  }

  def submitReportOfReceiptEISRequest[A](request: EisRequest)
                                        (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, jsonReads: Reads[A]): Future[Either[ErrorResponse, A]] =
    prepareJsonAndSubmit(appConfig.urlSubmitReportOfReceiptEis(), request, "submitReportOfReceiptEISRequest")
}
