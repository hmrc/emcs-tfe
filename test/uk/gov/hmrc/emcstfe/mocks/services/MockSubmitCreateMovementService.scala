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

package uk.gov.hmrc.emcstfe.mocks.services

import org.scalamock.handlers.CallHandler3
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.emcstfe.models.request.SubmitCreateMovementRequest
import uk.gov.hmrc.emcstfe.models.response.{EISSubmissionSuccessResponse, ErrorResponse}
import uk.gov.hmrc.emcstfe.services.SubmitCreateMovementService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockSubmitCreateMovementService extends MockFactory  {

  lazy val mockService: SubmitCreateMovementService = mock[SubmitCreateMovementService]

  object MockService extends Matchers {
    def submitViaEIS(requestModel: SubmitCreateMovementRequest): CallHandler3[SubmitCreateMovementRequest, HeaderCarrier, ExecutionContext, Future[Either[ErrorResponse, EISSubmissionSuccessResponse]]] =
      (mockService.submitViaEIS(_: SubmitCreateMovementRequest)(_: HeaderCarrier, _: ExecutionContext))
        .expects(requestModel, *, *)

    def setSubmittedDraftId(ern: String, draftId: String, submittedDraftId: String): CallHandler3[String, String, String, Future[Boolean]] =
      (mockService.setSubmittedDraftId(_: String, _: String, _: String))
        .expects(ern, draftId, submittedDraftId)
  }
}


