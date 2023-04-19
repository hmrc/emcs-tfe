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
import uk.gov.hmrc.emcstfe.models.request.SubmitDraftMovementRequest
import uk.gov.hmrc.emcstfe.models.response.{ErrorResponse, ChRISSuccessResponse}
import uk.gov.hmrc.emcstfe.services.SubmitDraftMovementService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}
import scala.xml.XML

trait MockSubmitDraftMovementService extends MockFactory  {
  lazy val mockService: SubmitDraftMovementService = mock[SubmitDraftMovementService]

  object MockService extends Matchers {
    def submitDraftMovement(submitDraftMovementRequest: SubmitDraftMovementRequest): CallHandler3[SubmitDraftMovementRequest, HeaderCarrier, ExecutionContext, Future[Either[ErrorResponse, ChRISSuccessResponse]]] = {
      (mockService.submitDraftMovement(_: SubmitDraftMovementRequest)(_: HeaderCarrier, _: ExecutionContext))
        .expects(assertArgs {
          (actualSubmitDraftMovementRequest: SubmitDraftMovementRequest, _, _) => {
            actualSubmitDraftMovementRequest.exciseRegistrationNumber shouldBe submitDraftMovementRequest.exciseRegistrationNumber
            actualSubmitDraftMovementRequest.arc shouldBe submitDraftMovementRequest.arc
            XML.loadString(actualSubmitDraftMovementRequest.requestBody) shouldBe XML.loadString(submitDraftMovementRequest.requestBody)
          }
        })
    }
  }
}


