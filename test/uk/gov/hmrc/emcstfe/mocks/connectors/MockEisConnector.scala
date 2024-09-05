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

package uk.gov.hmrc.emcstfe.mocks.connectors

import org.scalamock.handlers.{CallHandler4, CallHandler5}
import org.scalamock.scalatest.MockFactory
import play.api.libs.json.Reads
import uk.gov.hmrc.emcstfe.connectors.EisConnector
import uk.gov.hmrc.emcstfe.models.request._
import uk.gov.hmrc.emcstfe.models.request.eis.EisSubmissionRequest
import uk.gov.hmrc.emcstfe.models.request.eis.preValidate.PreValidateRequest
import uk.gov.hmrc.emcstfe.models.response._
import uk.gov.hmrc.emcstfe.models.response.getMessages.GetMessagesResponse
import uk.gov.hmrc.emcstfe.models.response.getMovement.GetMovementListResponse
import uk.gov.hmrc.emcstfe.models.response.getMovementHistoryEvents.GetMovementHistoryEventsResponse
import uk.gov.hmrc.emcstfe.models.response.getSubmissionFailureMessage.GetSubmissionFailureMessageResponse
import uk.gov.hmrc.emcstfe.models.response.prevalidate.PreValidateTraderApiResponse
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockEisConnector extends MockFactory {

  lazy val mockEisConnector: EisConnector = mock[EisConnector]

  object MockEisConnector {

    def submit[A](eisRequest: EisSubmissionRequest): CallHandler5[EisSubmissionRequest, String, HeaderCarrier, ExecutionContext, Reads[A], Future[Either[ErrorResponse, A]]] = {
      (mockEisConnector.submit[A](_: EisSubmissionRequest, _: String)(_: HeaderCarrier, _: ExecutionContext, _: Reads[A]))
        .expects(eisRequest, *, *, *, *)
    }

    def getMessages(request: GetMessagesRequest): CallHandler4[GetMessagesRequest, HeaderCarrier, ExecutionContext, Reads[GetMessagesResponse], Future[Either[ErrorResponse, GetMessagesResponse]]] = {
      (mockEisConnector.getMessages(_: GetMessagesRequest)(_: HeaderCarrier, _: ExecutionContext, _: Reads[GetMessagesResponse]))
        .expects(request, *, *, *)
    }

    def getMessageStatistics(request: GetMessageStatisticsRequest): CallHandler4[GetMessageStatisticsRequest, HeaderCarrier, ExecutionContext, Reads[GetMessageStatisticsResponse], Future[Either[ErrorResponse, GetMessageStatisticsResponse]]] = {
      (mockEisConnector.getMessageStatistics(_: GetMessageStatisticsRequest)(_: HeaderCarrier, _: ExecutionContext, _: Reads[GetMessageStatisticsResponse]))
        .expects(request, *, *, *)
    }

    def getSubmissionFailureMessage(request: GetSubmissionFailureMessageRequest): CallHandler4[GetSubmissionFailureMessageRequest, HeaderCarrier, ExecutionContext, Reads[GetSubmissionFailureMessageResponse], Future[Either[ErrorResponse, GetSubmissionFailureMessageResponse]]] = {
      (mockEisConnector.getSubmissionFailureMessage(_: GetSubmissionFailureMessageRequest)(_: HeaderCarrier, _: ExecutionContext, _: Reads[GetSubmissionFailureMessageResponse]))
        .expects(request, *, *, *)
    }

    def markMessageAsRead(request: MarkMessageAsReadRequest): CallHandler4[MarkMessageAsReadRequest, HeaderCarrier, ExecutionContext, Reads[MarkMessageAsReadResponse], Future[Either[ErrorResponse, MarkMessageAsReadResponse]]] = {
      (mockEisConnector.markMessageAsRead(_: MarkMessageAsReadRequest)(_: HeaderCarrier, _: ExecutionContext, _: Reads[MarkMessageAsReadResponse]))
        .expects(request, *, *, *)
    }

    def setMessageAsLogicallyDeleted(request: SetMessageAsLogicallyDeletedRequest): CallHandler4[SetMessageAsLogicallyDeletedRequest, HeaderCarrier, ExecutionContext, Reads[SetMessageAsLogicallyDeletedResponse], Future[Either[ErrorResponse, SetMessageAsLogicallyDeletedResponse]]] = {
      (mockEisConnector.setMessageAsLogicallyDeleted(_: SetMessageAsLogicallyDeletedRequest)(_: HeaderCarrier, _: ExecutionContext, _: Reads[SetMessageAsLogicallyDeletedResponse]))
        .expects(request, *, *, *)
    }

    def getRawMovement(request: GetMovementRequest): CallHandler4[GetMovementRequest, HeaderCarrier, ExecutionContext, Reads[RawGetMovementResponse], Future[Either[ErrorResponse, RawGetMovementResponse]]] = {
      (mockEisConnector.getRawMovement(_: GetMovementRequest)(_: HeaderCarrier, _: ExecutionContext, _: Reads[RawGetMovementResponse]))
        .expects(request, *, *, *)
    }

    def getMovementList(request: GetMovementListRequest): CallHandler4[GetMovementListRequest, HeaderCarrier, ExecutionContext, Reads[GetMovementListResponse], Future[Either[ErrorResponse, GetMovementListResponse]]] = {
      (mockEisConnector.getMovementList(_: GetMovementListRequest)(_: HeaderCarrier, _: ExecutionContext, _: Reads[GetMovementListResponse]))
        .expects(request, *, *, *)
    }

    def getMovementHistoryEvents(request: GetMovementHistoryEventsRequest): CallHandler4[GetMovementHistoryEventsRequest, HeaderCarrier, ExecutionContext, Reads[GetMovementHistoryEventsResponse], Future[Either[ErrorResponse, GetMovementHistoryEventsResponse]]] = {
      (mockEisConnector.getMovementHistoryEvents(_: GetMovementHistoryEventsRequest)(_: HeaderCarrier, _: ExecutionContext, _: Reads[GetMovementHistoryEventsResponse]))
        .expects(request, *, *, *)
    }

    def preValidateTrader(request: PreValidateRequest): CallHandler4[PreValidateRequest, HeaderCarrier, ExecutionContext, Reads[PreValidateTraderApiResponse], Future[Either[ErrorResponse, PreValidateTraderApiResponse]]] = {
      (mockEisConnector.preValidateTrader(_: PreValidateRequest)(_: HeaderCarrier, _: ExecutionContext, _: Reads[PreValidateTraderApiResponse]))
        .expects(request, *, *, *)
    }

  }
}
