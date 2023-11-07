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
import uk.gov.hmrc.emcstfe.models.request.eis.{EisConsumptionRequest, EisSubmissionRequest}
import uk.gov.hmrc.emcstfe.models.request.{GetMessagesRequest, MarkMessageAsReadRequest, SetMessageAsLogicallyDeletedRequest}
import uk.gov.hmrc.emcstfe.models.response.getMessages.GetMessagesResponse
import uk.gov.hmrc.emcstfe.models.response.{ErrorResponse, MarkMessageAsReadResponse, SetMessageAsLogicallyDeletedResponse}
import uk.gov.hmrc.emcstfe.services.MetricsService
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class EisConnector @Inject()(val http: HttpClient,
                             appConfig: AppConfig,
                             override val metricsService: MetricsService,
                             httpParser: EisJsonHttpParser
                            ) extends BaseEisConnector {

  private def prepareJsonAndSubmit[A](url: String, request: EisSubmissionRequest, callingMethod: String)
                                     (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, jsonReads: Reads[A]): Future[Either[ErrorResponse, A]] = {
    logger.debug(s"[$callingMethod] Sending to URL: $url")
    logger.debug(s"[$callingMethod] Sending body: ${request.toJson}")
    postJson(http, url, request.toJson, request)(ec, headerCarrier, httpParser.modelFromJsonHttpReads, appConfig)
  }

  private def prepareGetRequestAndSubmit[A](url: String, request: EisConsumptionRequest, callingMethod: String)
                                           (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, jsonReads: Reads[A]): Future[Either[ErrorResponse, A]] = {
    logger.debug(s"[$callingMethod] Sending to URL: $url")
    get(http, url, request)(ec, headerCarrier, httpParser.modelFromJsonHttpReads, appConfig)
  }

  private def prepareEmptyPutRequestAndSubmit[A](url: String, request: EisConsumptionRequest, callingMethod: String)
                                                (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, jsonReads: Reads[A]): Future[Either[ErrorResponse, A]] = {
    logger.debug(s"[$callingMethod] Sending to URL: $url")
    putEmpty(http, url, request)(ec, headerCarrier, httpParser.modelFromJsonHttpReads, appConfig)
  }

  private def prepareDeleteRequestAndSubmit[A](url: String, request: EisConsumptionRequest, callingMethod: String)
                                                (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, jsonReads: Reads[A]): Future[Either[ErrorResponse, A]] = {
    logger.debug(s"[$callingMethod] Sending to URL: $url")
    delete(http, url, request)(ec, headerCarrier, httpParser.modelFromJsonHttpReads, appConfig)
  }

  def submit[A](request: EisSubmissionRequest, callingMethod: String)
               (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, jsonReads: Reads[A]): Future[Either[ErrorResponse, A]] =
    prepareJsonAndSubmit(appConfig.eisSubmissionsUrl(), request, callingMethod)

  def getMessages(request: GetMessagesRequest)
                 (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, jsonReads: Reads[GetMessagesResponse]): Future[Either[ErrorResponse, GetMessagesResponse]] =
    prepareGetRequestAndSubmit(appConfig.eisGetMessagesUrl(), request, "getMessages")

  def markMessageAsRead(request: MarkMessageAsReadRequest)
                       (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, jsonReads: Reads[MarkMessageAsReadResponse]): Future[Either[ErrorResponse, MarkMessageAsReadResponse]] = {
    val url = appConfig.eisMessageUrl(request.exciseRegistrationNumber, request.messageId)
    prepareEmptyPutRequestAndSubmit(url, request, "markMessageAsRead")
  }

  def setMessageAsLogicallyDeleted(request: SetMessageAsLogicallyDeletedRequest)
                       (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, jsonReads: Reads[SetMessageAsLogicallyDeletedResponse]): Future[Either[ErrorResponse, SetMessageAsLogicallyDeletedResponse]] = {
    val url = appConfig.eisMessageUrl(request.exciseRegistrationNumber, request.messageId)
    prepareDeleteRequestAndSubmit(url, request, "setMessageAsLogicallyDeleted")
  }
}
