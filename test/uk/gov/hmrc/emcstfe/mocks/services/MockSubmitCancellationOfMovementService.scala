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

import org.scalamock.handlers.CallHandler4
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.cancellationOfMovement.SubmitCancellationOfMovementModel
import uk.gov.hmrc.emcstfe.models.response.{ChRISSuccessResponse, EISSuccessResponse, ErrorResponse}
import uk.gov.hmrc.emcstfe.services.SubmitCancellationOfMovementService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockSubmitCancellationOfMovementService extends MockFactory  {

  lazy val mockService: SubmitCancellationOfMovementService = mock[SubmitCancellationOfMovementService]

  object MockService extends Matchers {
    def submit(submission: SubmitCancellationOfMovementModel): CallHandler4[SubmitCancellationOfMovementModel, HeaderCarrier, ExecutionContext, UserRequest[_], Future[Either[ErrorResponse, ChRISSuccessResponse]]] =
      (mockService.submit(_: SubmitCancellationOfMovementModel)(_: HeaderCarrier, _: ExecutionContext, _: UserRequest[_]))
        .expects(submission, *, *, *)

    def submitViaEIS(submission: SubmitCancellationOfMovementModel): CallHandler4[SubmitCancellationOfMovementModel, HeaderCarrier, ExecutionContext, UserRequest[_], Future[Either[ErrorResponse, EISSuccessResponse]]] =
      (mockService.submitViaEIS(_: SubmitCancellationOfMovementModel)(_: HeaderCarrier, _: ExecutionContext, _: UserRequest[_]))
        .expects(submission, *, *, *)
  }
}


