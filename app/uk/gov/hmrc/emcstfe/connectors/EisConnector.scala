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
import uk.gov.hmrc.emcstfe.models.request._
import uk.gov.hmrc.emcstfe.models.request.eis.preValidate.{PreValidateETDS12Request, PreValidateRequest}
import uk.gov.hmrc.emcstfe.models.request.eis.{EisConsumptionRequest, EisSubmissionRequest, TraderKnownFactsETDS18Request}
import uk.gov.hmrc.emcstfe.models.response._
import uk.gov.hmrc.emcstfe.models.response.getMessages.GetMessagesResponse
import uk.gov.hmrc.emcstfe.models.response.getMovement.GetMovementListResponse
import uk.gov.hmrc.emcstfe.models.response.getMovementHistoryEvents.GetMovementHistoryEventsResponse
import uk.gov.hmrc.emcstfe.models.response.getSubmissionFailureMessage.GetSubmissionFailureMessageResponse
import uk.gov.hmrc.emcstfe.models.response.prevalidate.{PreValidateTraderApiResponse, PreValidateTraderETDSResponse}
import uk.gov.hmrc.emcstfe.services.MetricsService
import uk.gov.hmrc.emcstfe.utils.Logging
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class EisConnector @Inject()(val http: HttpClient,
                             appConfig: AppConfig,
                             override val metricsService: MetricsService
                            ) extends BaseEisConnector with EisJsonHttpParser with Logging {

  private def prepareJsonAndSubmit[A](url: String, request: EisSubmissionRequest, callingMethod: String, bearerToken: String = appConfig.eisSubmitBearerToken)
                                     (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, jsonReads: Reads[A]): Future[Either[ErrorResponse, A]] = {
    logger.debug(s"[$callingMethod] Sending to URL: $url")
    logger.debug(s"[$callingMethod] Sending body: ${request.toJson}")
    postJson(http, url, request.toJson, request, bearerToken)(ec, headerCarrier, modelFromJsonHttpReads, appConfig)
  }

  private def prepareGetRequestAndSubmit[A](url: String, request: EisConsumptionRequest, callingMethod: String, bearerToken: String)
                                           (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, jsonReads: Reads[A]): Future[Either[ErrorResponse, A]] = {
    logger.debug(s"[$callingMethod] Sending to URL: $url")
    get(http, url, request, bearerToken)(ec, headerCarrier, modelFromJsonHttpReads, appConfig)
  }

  private def prepareEmptyPutRequestAndSubmit[A](url: String, request: EisConsumptionRequest, callingMethod: String, bearerToken: String)
                                                (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, jsonReads: Reads[A]): Future[Either[ErrorResponse, A]] = {
    logger.debug(s"[$callingMethod] Sending to URL: $url")
    putEmpty(http, url, request, bearerToken)(ec, headerCarrier, modelFromJsonHttpReads, appConfig)
  }

  private def prepareDeleteRequestAndSubmit[A](url: String, request: EisConsumptionRequest, callingMethod: String, bearerToken: String)
                                              (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, jsonReads: Reads[A]): Future[Either[ErrorResponse, A]] = {
    logger.debug(s"[$callingMethod] Sending to URL: $url")
    delete(http, url, request, bearerToken)(ec, headerCarrier, modelFromJsonHttpReads, appConfig)
  }

  def submit[A](request: EisSubmissionRequest, callingMethod: String)
               (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, jsonReads: Reads[A]): Future[Either[ErrorResponse, A]] =
    prepareJsonAndSubmit(appConfig.eisSubmissionsUrl(), request, callingMethod)

  def getMessages(request: GetMessagesRequest)
                 (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, jsonReads: Reads[GetMessagesResponse]): Future[Either[ErrorResponse, GetMessagesResponse]] =
    prepareGetRequestAndSubmit(appConfig.eisGetMessagesUrl(), request, "getMessages", appConfig.eisMessagesBearerToken)

  def getMessageStatistics(request: GetMessageStatisticsRequest)
                          (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, jsonReads: Reads[GetMessageStatisticsResponse]): Future[Either[ErrorResponse, GetMessageStatisticsResponse]] = {
    prepareGetRequestAndSubmit(appConfig.eisGetMessageStatisticsUrl(), request, "getMessageStatistics", appConfig.eisMessagesBearerToken)
  }

  def getRawMovement(request: GetMovementRequest)
                    (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, jsonReads: Reads[RawGetMovementResponse]): Future[Either[ErrorResponse, RawGetMovementResponse]] = {
    prepareGetRequestAndSubmit(appConfig.eisGetMovementUrl(), request, "getMovement", appConfig.eisMovementsBearerToken)
  }

  def getMovementList(request: GetMovementListRequest)
                    (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, jsonReads: Reads[GetMovementListResponse]): Future[Either[ErrorResponse, GetMovementListResponse]] = {
    prepareGetRequestAndSubmit(appConfig.eisGetMovementsUrl(), request, "getMovementList", appConfig.eisMovementsBearerToken)
  }

  def getMovementHistoryEvents(request: GetMovementHistoryEventsRequest)
                              (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, jsonReads: Reads[GetMovementHistoryEventsResponse]): Future[Either[ErrorResponse, GetMovementHistoryEventsResponse]] = {
    prepareGetRequestAndSubmit(appConfig.eisGetMovementHistoryEventsUrl(), request, "getMovementHistoryEvents", appConfig.eisMovementsBearerToken)
  }

  def getSubmissionFailureMessage(request: GetSubmissionFailureMessageRequest)
                                 (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, jsonReads: Reads[GetSubmissionFailureMessageResponse]): Future[Either[ErrorResponse, GetSubmissionFailureMessageResponse]] =
    prepareGetRequestAndSubmit(appConfig.eisGetSubmissionFailureMessageUrl(), request, "getSubmissionFailureMessage", appConfig.eisMessagesBearerToken)

  def markMessageAsRead(request: MarkMessageAsReadRequest)
                       (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, jsonReads: Reads[MarkMessageAsReadResponse]): Future[Either[ErrorResponse, MarkMessageAsReadResponse]] = {
    val url = appConfig.eisMessageUrl(request.exciseRegistrationNumber, request.messageId)
    prepareEmptyPutRequestAndSubmit(url, request, "markMessageAsRead", appConfig.eisMessagesBearerToken)
  }

  def setMessageAsLogicallyDeleted(request: SetMessageAsLogicallyDeletedRequest)
                                  (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, jsonReads: Reads[SetMessageAsLogicallyDeletedResponse]): Future[Either[ErrorResponse, SetMessageAsLogicallyDeletedResponse]] = {
    val url = appConfig.eisMessageUrl(request.exciseRegistrationNumber, request.messageId)
    prepareDeleteRequestAndSubmit(url, request, "setMessageAsLogicallyDeleted", appConfig.eisMessagesBearerToken)
  }

  def preValidateTrader(request: PreValidateRequest)
                       (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, jsonReads: Reads[PreValidateTraderApiResponse]): Future[Either[ErrorResponse, PreValidateTraderApiResponse]] = {
    val url = appConfig.eisPreValidateTraderUrl()
    prepareJsonAndSubmit(url, request, "preValidateTrader", appConfig.eisPrevalidateBearerToken)
  }

  def preValidateTraderViaETDS12(request: PreValidateETDS12Request)
                       (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, jsonReads: Reads[PreValidateTraderETDSResponse]): Future[Either[ErrorResponse, PreValidateTraderETDSResponse]] = {
    val url = appConfig.eisPreValidateTraderViaETDS12Url()
    prepareJsonAndSubmit(url, request, "preValidateTraderViaETDS12", appConfig.eisPrevalidateETDS12BearerToken)
  }

  def getTraderKnownFactsViaETDS18(request: TraderKnownFactsETDS18Request)
                       (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, jsonReads: Reads[TraderKnownFacts]): Future[Either[ErrorResponse, Option[TraderKnownFacts]]] = {
    val url = appConfig.knownFactsEtdsUrl(request.exciseRegistrationNumber)
    prepareGetRequestAndSubmit(url, request, "getKnownFactsViaETDS18", appConfig.eisTraderKnownFactsETDS18BearerToken).map(_.map(Some(_)))
  }
}
