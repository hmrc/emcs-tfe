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
import uk.gov.hmrc.emcstfe.models.response.getMessages.{GetMessagesResponse, RawGetMessagesResponse}
import uk.gov.hmrc.emcstfe.models.response.getSubmissionFailureMessage.{GetSubmissionFailureMessageResponse, RawGetSubmissionFailureMessageResponse}
import uk.gov.hmrc.emcstfe.models.response.{ErrorResponse, GetMessageStatisticsResponse, MarkMessageAsReadResponse, RawGetMovementResponse, SetMessageAsLogicallyDeletedResponse}
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

    def getRawMessages(request: GetMessagesRequest): CallHandler4[GetMessagesRequest, HeaderCarrier, ExecutionContext, Reads[RawGetMessagesResponse], Future[Either[ErrorResponse, RawGetMessagesResponse]]] = {
      (mockEisConnector.getRawMessages(_: GetMessagesRequest)(_: HeaderCarrier, _: ExecutionContext, _: Reads[RawGetMessagesResponse]))
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

    def getRawSubmissionFailureMessage(request: GetSubmissionFailureMessageRequest): CallHandler4[GetSubmissionFailureMessageRequest, HeaderCarrier, ExecutionContext, Reads[RawGetSubmissionFailureMessageResponse], Future[Either[ErrorResponse, RawGetSubmissionFailureMessageResponse]]] = {
      (mockEisConnector.getRawSubmissionFailureMessage(_: GetSubmissionFailureMessageRequest)(_: HeaderCarrier, _: ExecutionContext, _: Reads[RawGetSubmissionFailureMessageResponse]))
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

  }
}
