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

import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.emcstfe.fixtures.CreateMovementFixtures
import uk.gov.hmrc.emcstfe.mocks.connectors.{MockChrisConnector, MockEisConnector}
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.request.SubmitCreateMovementRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{EISUnknownError, XmlValidationError}
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.concurrent.Future

class SubmitCreateMovementServiceSpec extends TestBaseSpec with CreateMovementFixtures {
  trait Test extends MockChrisConnector with MockEisConnector {
    implicit val request: UserRequest[AnyContentAsEmpty.type] = UserRequest(FakeRequest(), testErn, testInternalId, testCredId)
    val submitCreateMovementRequest: SubmitCreateMovementRequest = SubmitCreateMovementRequest(CreateMovementFixtures.createMovementModelMax, testDraftId)
    val service: SubmitCreateMovementService = new SubmitCreateMovementService(mockChrisConnector, mockEisConnector)
  }

  "submit" should {

      "return a Right" when {
        "connector call is successful and XML is the correct format" in new Test {

          MockChrisConnector.submitCreateMovementChrisSOAPRequest(submitCreateMovementRequest).returns(
            Future.successful(Right(chrisSuccessResponse))
          )

          await(service.submit(CreateMovementFixtures.createMovementModelMax, testDraftId)) shouldBe Right(chrisSuccessResponse)
        }
      }
      "return a Left" when {
        "connector call is unsuccessful" in new Test {

          MockChrisConnector.submitCreateMovementChrisSOAPRequest(submitCreateMovementRequest).returns(
            Future.successful(Left(XmlValidationError))
          )

          await(service.submit(CreateMovementFixtures.createMovementModelMax, testDraftId)) shouldBe Left(XmlValidationError)
        }
      }
    }

  "submitViaEIS" should {
    "return a Right" when {
      "connector call is successful and Json is the correct format" in new Test {

        MockEisConnector.submit(submitCreateMovementRequest).returns(
          Future.successful(Right(eisSuccessResponse))
        )

        await(service.submitViaEIS(CreateMovementFixtures.createMovementModelMax, testDraftId)) shouldBe Right(eisSuccessResponse)
      }
    }

    "return a Left" when {
      "connector call is unsuccessful" in new Test {

        MockEisConnector.submit(submitCreateMovementRequest).returns(
          Future.successful(Left(EISUnknownError("Downstream failed to respond")))
        )

        await(service.submitViaEIS(CreateMovementFixtures.createMovementModelMax, testDraftId)) shouldBe Left(EISUnknownError("Downstream failed to respond"))
      }
    }
  }
}
