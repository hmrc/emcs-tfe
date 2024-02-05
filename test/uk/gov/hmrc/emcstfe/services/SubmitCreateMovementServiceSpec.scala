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

import uk.gov.hmrc.emcstfe.featureswitch.core.config.ValidateUsingFS41Schema
import uk.gov.hmrc.emcstfe.fixtures.CreateMovementFixtures
import uk.gov.hmrc.emcstfe.mocks.config.MockAppConfig
import uk.gov.hmrc.emcstfe.mocks.connectors.{MockChrisConnector, MockEisConnector}
import uk.gov.hmrc.emcstfe.models.request.SubmitCreateMovementRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{EISUnknownError, XmlValidationError}
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.concurrent.Future

class SubmitCreateMovementServiceSpec extends TestBaseSpec with CreateMovementFixtures with MockAppConfig {
  class Test(useFS41SchemaVersion: Boolean) extends MockChrisConnector with MockEisConnector {
    val submitCreateMovementRequest: SubmitCreateMovementRequest = SubmitCreateMovementRequest(CreateMovementFixtures.createMovementModelMax, testDraftId, useFS41SchemaVersion = useFS41SchemaVersion)
    val service: SubmitCreateMovementService = new SubmitCreateMovementService(mockChrisConnector, mockEisConnector, mockAppConfig)
    MockedAppConfig.getFeatureSwitchValue(ValidateUsingFS41Schema).returns(useFS41SchemaVersion)
  }

  "SubmitCreateMovementService" when {
    Seq(true, false).foreach { useFS41SchemaVersion =>
      s"useFS41SchemaVersion is $useFS41SchemaVersion" should {
        "when calling submit" must {

          "return a Right" when {
            "connector call is successful and XML is the correct format" in new Test(useFS41SchemaVersion) {

              MockChrisConnector.submitCreateMovementChrisSOAPRequest(submitCreateMovementRequest).returns(
                Future.successful(Right(chrisSuccessResponse))
              )

              await(service.submit(CreateMovementFixtures.createMovementModelMax, testDraftId)) shouldBe Right(chrisSuccessResponse)
            }
          }
          "return a Left" when {
            "connector call is unsuccessful" in new Test(useFS41SchemaVersion) {

              MockChrisConnector.submitCreateMovementChrisSOAPRequest(submitCreateMovementRequest).returns(
                Future.successful(Left(XmlValidationError))
              )

              await(service.submit(CreateMovementFixtures.createMovementModelMax, testDraftId)) shouldBe Left(XmlValidationError)
            }
          }
        }

        "when calling submitViaEIS" must {
          "return a Right" when {
            "connector call is successful and Json is the correct format" in new Test(useFS41SchemaVersion) {

              MockEisConnector.submit(submitCreateMovementRequest).returns(
                Future.successful(Right(eisSuccessResponse))
              )

              await(service.submitViaEIS(CreateMovementFixtures.createMovementModelMax, testDraftId)) shouldBe Right(eisSuccessResponse)
            }
          }

          "return a Left" when {
            "connector call is unsuccessful" in new Test(useFS41SchemaVersion) {

              MockEisConnector.submit(submitCreateMovementRequest).returns(
                Future.successful(Left(EISUnknownError("Downstream failed to respond")))
              )

              await(service.submitViaEIS(CreateMovementFixtures.createMovementModelMax, testDraftId)) shouldBe Left(EISUnknownError("Downstream failed to respond"))
            }
          }
        }
      }
    }
  }
}
