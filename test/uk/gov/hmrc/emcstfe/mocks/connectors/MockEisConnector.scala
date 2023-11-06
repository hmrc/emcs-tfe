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
import uk.gov.hmrc.emcstfe.models.request.{GetMessagesRequest, MarkMessageAsReadRequest, SetMessageAsLogicallyDeletedRequest}
import uk.gov.hmrc.emcstfe.models.request.eis.EisSubmissionRequest
import uk.gov.hmrc.emcstfe.models.response.{ErrorResponse, MarkMessageAsReadResponse, SetMessageAsLogicallyDeletedResponse}
import uk.gov.hmrc.emcstfe.models.response.getMessages.GetMessagesResponse
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

    def markMessageAsRead(request: MarkMessageAsReadRequest): CallHandler4[MarkMessageAsReadRequest, HeaderCarrier, ExecutionContext, Reads[MarkMessageAsReadResponse], Future[Either[ErrorResponse, MarkMessageAsReadResponse]]] = {
      (mockEisConnector.markMessageAsRead(_: MarkMessageAsReadRequest)(_: HeaderCarrier, _: ExecutionContext, _: Reads[MarkMessageAsReadResponse]))
        .expects(request, *, *, *)
    }

    def setMessageAsLogicallyDeleted(request: SetMessageAsLogicallyDeletedRequest): CallHandler4[SetMessageAsLogicallyDeletedRequest, HeaderCarrier, ExecutionContext, Reads[SetMessageAsLogicallyDeletedResponse], Future[Either[ErrorResponse, SetMessageAsLogicallyDeletedResponse]]] = {
      (mockEisConnector.setMessageAsLogicallyDeleted(_: SetMessageAsLogicallyDeletedRequest)(_: HeaderCarrier, _: ExecutionContext, _: Reads[SetMessageAsLogicallyDeletedResponse]))
        .expects(request, *, *, *)
    }

  }
}
