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

package uk.gov.hmrc.emcstfe.services

import uk.gov.hmrc.emcstfe.fixtures.SubmitDraftMovementFixture
import uk.gov.hmrc.emcstfe.mocks.config.MockAppConfig
import uk.gov.hmrc.emcstfe.mocks.connectors.MockChrisConnector
import uk.gov.hmrc.emcstfe.models.request.SubmitDraftMovementRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{NoLrnError, XmlValidationError}
import uk.gov.hmrc.emcstfe.support.UnitSpec

import scala.concurrent.Future

class SubmitDraftMovementServiceSpec extends UnitSpec with SubmitDraftMovementFixture {
  trait Test extends MockChrisConnector with MockAppConfig {
    val submitDraftMovementRequest: SubmitDraftMovementRequest = SubmitDraftMovementRequest(exciseRegistrationNumber = "My ERN", arc = "My ARC", requestBody = submitDraftMovementRequestBody)
    val service: SubmitDraftMovementService = new SubmitDraftMovementService(mockConnector, mockAppConfig)
  }

  "submitDraftMovement" should {
    "return a Right" when {
      "connector call is successful and XML is the correct format" in new Test {

        MockConnector
          .submitDraftMovementChrisSOAPRequest(submitDraftMovementRequest)
          .returns(Future.successful(Right(chrisSuccessResponse)))

        await(service.submitDraftMovement(submitDraftMovementRequest)) shouldBe Right(chrisSuccessResponse)
      }
    }
    "return a Left" when {
      "connector call is unsuccessful" in new Test {

        MockConnector
          .submitDraftMovementChrisSOAPRequest(submitDraftMovementRequest)
          .returns(Future.successful(Left(XmlValidationError)))

        await(service.submitDraftMovement(submitDraftMovementRequest)) shouldBe Left(XmlValidationError)
      }
      "request body doesn't contain an LRN" in new Test {
        await(service.submitDraftMovement(submitDraftMovementRequest.copy(requestBody = "<Something />"))) shouldBe Left(NoLrnError)
      }
    }
  }
}
