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

import uk.gov.hmrc.emcstfe.featureswitch.core.config.SendToEIS
import uk.gov.hmrc.emcstfe.fixtures.GetSubmissionFailureMessageFixtures
import uk.gov.hmrc.emcstfe.mocks.config.MockAppConfig
import uk.gov.hmrc.emcstfe.mocks.connectors.{MockChrisConnector, MockEisConnector}
import uk.gov.hmrc.emcstfe.models.request.GetSubmissionFailureMessageRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.EISUnknownError
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.concurrent.Future

class GetSubmissionFailureMessageServiceSpec extends TestBaseSpec with GetSubmissionFailureMessageFixtures with MockEisConnector with MockChrisConnector with MockAppConfig {

  import GetSubmissionFailureMessageResponseFixtures.getSubmissionFailureMessageResponseModel

  lazy val getSubmissionFailureMessageRequest: GetSubmissionFailureMessageRequest = GetSubmissionFailureMessageRequest(testErn, testMessageId)
  lazy val service: GetSubmissionFailureMessageService = new GetSubmissionFailureMessageService(mockEisConnector, mockChrisConnector, mockAppConfig)

  "getSubmissionFailureMessage" when {
    "send to EIS is switched on" should {
      "return a Right" when {
        "connector call is successful and XML is the correct format" in {

          MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(true)

          MockEisConnector.getSubmissionFailureMessage(getSubmissionFailureMessageRequest).returns(
            Future.successful(Right(getSubmissionFailureMessageResponseModel))
          )

          await(service.getSubmissionFailureMessage(getSubmissionFailureMessageRequest)) shouldBe Right(getSubmissionFailureMessageResponseModel)
        }
      }
      "return a Left" when {
        "connector call is unsuccessful" in {

          MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(true)

          MockEisConnector.getSubmissionFailureMessage(getSubmissionFailureMessageRequest).returns(
            Future.successful(Left(EISUnknownError("Downstream failed to respond")))
          )

          await(service.getSubmissionFailureMessage(getSubmissionFailureMessageRequest)) shouldBe Left(EISUnknownError("Downstream failed to respond"))
        }
      }
    }

    "send to EIS is switched off (send to ChRIS)" should {
      "return a Right" when {
        "connector call is successful and XML is the correct format" in {

          MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(false)

          MockChrisConnector.postChrisSOAPRequestAndExtractToModel(getSubmissionFailureMessageRequest).returns(
            Future.successful(Right(getSubmissionFailureMessageResponseModel))
          )

          await(service.getSubmissionFailureMessage(getSubmissionFailureMessageRequest)) shouldBe Right(getSubmissionFailureMessageResponseModel)
        }
      }
      "return a Left" when {
        "connector call is unsuccessful" in {

          MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(false)

          MockChrisConnector.postChrisSOAPRequestAndExtractToModel(getSubmissionFailureMessageRequest).returns(
            Future.successful(Left(EISUnknownError("Downstream failed to respond")))
          )

          await(service.getSubmissionFailureMessage(getSubmissionFailureMessageRequest)) shouldBe Left(EISUnknownError("Downstream failed to respond"))
        }
      }
    }
  }
}
