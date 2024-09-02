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
import uk.gov.hmrc.emcstfe.models.request.SubmitChangeDestinationRequest
import uk.gov.hmrc.emcstfe.models.response.{EISSubmissionSuccessResponse, ErrorResponse}
import uk.gov.hmrc.emcstfe.services.SubmitChangeDestinationService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockSubmitChangeDestinationService extends MockFactory  {

  lazy val mockSubmitChangeDestinationService: SubmitChangeDestinationService = mock[SubmitChangeDestinationService]

  object MockSubmitChangeDestinationService extends Matchers {
    def submitViaEIS(request: SubmitChangeDestinationRequest): CallHandler3[SubmitChangeDestinationRequest, HeaderCarrier, ExecutionContext, Future[Either[ErrorResponse, EISSubmissionSuccessResponse]]] =
      (mockSubmitChangeDestinationService.submitViaEIS(_: SubmitChangeDestinationRequest)(_: HeaderCarrier, _: ExecutionContext))
        .expects(request, *, *)
  }
}


