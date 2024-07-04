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

import uk.gov.hmrc.emcstfe.fixtures.SubmitChangeDestinationFixtures
import uk.gov.hmrc.emcstfe.mocks.config.MockAppConfig
import uk.gov.hmrc.emcstfe.mocks.connectors.{MockChrisConnector, MockEisConnector}
import uk.gov.hmrc.emcstfe.mocks.repository.MockChangeDestinationUserAnswersRepository
import uk.gov.hmrc.emcstfe.models.request.SubmitChangeDestinationRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{ChRISRIMValidationError, EISBusinessError, EISRIMValidationError, EISUnknownError, XmlValidationError}
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.concurrent.Future

class SubmitChangeDestinationServiceSpec extends TestBaseSpec
  with SubmitChangeDestinationFixtures
  with MockAppConfig
  with MockChangeDestinationUserAnswersRepository {

  import SubmitChangeDestinationFixtures.submitChangeDestinationModelMax

  class Test(useFS41SchemaVersion: Boolean) extends MockChrisConnector with MockEisConnector {
    val submitChangeDestinationRequest: SubmitChangeDestinationRequest = SubmitChangeDestinationRequest(submitChangeDestinationModelMax, useFS41SchemaVersion = useFS41SchemaVersion)
    val service: SubmitChangeDestinationService = new SubmitChangeDestinationService(mockChrisConnector, mockEisConnector, mockChangeDestinationUserAnswersRepository, mockAppConfig)
  }

  "SubmitChangeDestinationService" when {
    Seq(true, false).foreach { useFS41SchemaVersion =>
      s"useFS41SchemaVersion is $useFS41SchemaVersion" should {

        "when calling submit" must {
          "return a Right" when {
            "connector call is successful and XML is the correct format" in new Test(useFS41SchemaVersion) {

              MockChrisConnector.submitChangeDestinationChrisSOAPRequest(submitChangeDestinationRequest).returns(
                Future.successful(Right(chrisSuccessResponse))
              )

              MockChangeDestinationUserAnswersRepository.setValidationErrorMessagesForDraftMovement(testErn, testArc, Seq.empty)
                .returns(Future.successful(true))

              await(service.submit(submitChangeDestinationRequest)) shouldBe Right(chrisSuccessResponse)
            }
          }
          "return a Left" when {

            "ChRIS returns a 200 response but with RIM validation errors (inserting the errors into Mongo)" in new Test(useFS41SchemaVersion) {

              MockChrisConnector.submitChangeDestinationChrisSOAPRequest(submitChangeDestinationRequest).returns(
                Future.successful(Left(ChRISRIMValidationError(chrisRIMValidationErrorResponse)))
              )

              MockChangeDestinationUserAnswersRepository.setValidationErrorMessagesForDraftMovement(testErn, testArc, chrisRIMValidationErrorResponse.rimValidationErrors)
                .returns(Future.successful(true))

              await(service.submit(submitChangeDestinationRequest)) shouldBe Left(ChRISRIMValidationError(chrisRIMValidationErrorResponse))
            }

            "connector call is unsuccessful" in new Test(useFS41SchemaVersion) {

              MockChrisConnector.submitChangeDestinationChrisSOAPRequest(submitChangeDestinationRequest).returns(
                Future.successful(Left(XmlValidationError))
              )

              await(service.submit(submitChangeDestinationRequest)) shouldBe Left(XmlValidationError)
            }
          }
        }

        "when calling submitViaEIS" must {
          "return a Right" when {
            "connector call is successful and Json is the correct format" in new Test(useFS41SchemaVersion) {

              MockEisConnector.submit(submitChangeDestinationRequest).returns(
                Future.successful(Right(eisSuccessResponse))
              )

              MockChangeDestinationUserAnswersRepository.setValidationErrorMessagesForDraftMovement(testErn, testArc, Seq.empty)
                .returns(Future.successful(true))

              await(service.submitViaEIS(submitChangeDestinationRequest)) shouldBe Right(eisSuccessResponse)
            }
          }

          "return a Left" when {

            "EIS returns a 422 error with RIM validation errors (inserting the errors into Mongo)" in new Test(useFS41SchemaVersion) {

              MockEisConnector.submit(submitChangeDestinationRequest).returns(
                Future.successful(Left(EISRIMValidationError(eisRimValidationResponse)))
              )

              MockChangeDestinationUserAnswersRepository.setValidationErrorMessagesForDraftMovement(testErn, testArc, eisRimValidationResponse.validatorResults.get)
                .returns(Future.successful(true))

              await(service.submitViaEIS(submitChangeDestinationRequest)) shouldBe Left(EISRIMValidationError(eisRimValidationResponse))
            }

            "EIS returns a 422 error without RIM validation errors" in new Test(useFS41SchemaVersion) {

              MockEisConnector.submit(submitChangeDestinationRequest).returns(
                Future.successful(Left(EISBusinessError("wrong")))
              )

              await(service.submitViaEIS(submitChangeDestinationRequest)) shouldBe Left(EISBusinessError("wrong"))
            }

            "connector call is unsuccessful" in new Test(useFS41SchemaVersion) {

              MockEisConnector.submit(submitChangeDestinationRequest).returns(
                Future.successful(Left(EISUnknownError("Downstream failed to respond")))
              )

              await(service.submitViaEIS(submitChangeDestinationRequest)) shouldBe Left(EISUnknownError("Downstream failed to respond"))
            }
          }
        }
      }
    }
  }
}
