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

import uk.gov.hmrc.emcstfe.fixtures.CreateMovementFixtures
import uk.gov.hmrc.emcstfe.mocks.config.MockAppConfig
import uk.gov.hmrc.emcstfe.mocks.connectors.MockEisConnector
import uk.gov.hmrc.emcstfe.mocks.repository.MockCreateMovementUserAnswersRepository
import uk.gov.hmrc.emcstfe.models.request.SubmitCreateMovementRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{EISBusinessError, EISRIMValidationError, EISUnknownError}
import uk.gov.hmrc.emcstfe.models.response.rimValidation.RIMValidationError
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.concurrent.Future

class SubmitCreateMovementServiceSpec extends TestBaseSpec with CreateMovementFixtures with MockAppConfig with MockCreateMovementUserAnswersRepository {

  class Test(useFS41SchemaVersion: Boolean) extends MockEisConnector {
    val submitCreateMovementRequest: SubmitCreateMovementRequest = SubmitCreateMovementRequest(CreateMovementFixtures.createMovementModelMax, testDraftId, useFS41SchemaVersion = useFS41SchemaVersion)
    val service: SubmitCreateMovementService = new SubmitCreateMovementService(mockEisConnector, mockCreateMovementUserAnswersRepository, mockAppConfig)
  }

  "SubmitCreateMovementService" when {
    Seq(true, false).foreach { useFS41SchemaVersion =>
      s"useFS41SchemaVersion is $useFS41SchemaVersion" should {
        "when calling submitViaEIS" must {
          "return a Right" when {
            "connector call is successful and Json is the correct format" in new Test(useFS41SchemaVersion) {

              MockEisConnector.submit(submitCreateMovementRequest).returns(
                Future.successful(Right(eisSuccessResponse))
              )

              MockCreateMovementUserAnswersRepository.setValidationErrorMessagesForDraftMovement(testErn, testDraftId, Seq.empty)
                .returns(Future.successful(true))

              await(service.submitViaEIS(submitCreateMovementRequest)) shouldBe Right(eisSuccessResponse)
            }
          }

          "return a Left" when {

            "EIS returns a 422 error with RIM validation errors (inserting the errors into Mongo)" in new Test(useFS41SchemaVersion) {

              MockEisConnector.submit(submitCreateMovementRequest).returns(
                Future.successful(Left(EISRIMValidationError(eisRimValidationResponse)))
              )

              MockCreateMovementUserAnswersRepository.setValidationErrorMessagesForDraftMovement(testErn, testDraftId, eisRimValidationResponse.validatorResults.get)
                .returns(Future.successful(true))

              await(service.submitViaEIS(submitCreateMovementRequest)) shouldBe Left(EISRIMValidationError(eisRimValidationResponse))
            }

            "EIS returns a 422 error without RIM validation errors" in new Test(useFS41SchemaVersion) {

              MockEisConnector.submit(submitCreateMovementRequest).returns(
                Future.successful(Left(EISBusinessError("wrong")))
              )

              await(service.submitViaEIS(submitCreateMovementRequest)) shouldBe Left(EISBusinessError("wrong"))
            }

            "connector call is unsuccessful" in new Test(useFS41SchemaVersion) {

              MockEisConnector.submit(submitCreateMovementRequest).returns(
                Future.successful(Left(EISUnknownError("Downstream failed to respond")))
              )

              await(service.submitViaEIS(submitCreateMovementRequest)) shouldBe Left(EISUnknownError("Downstream failed to respond"))
            }
          }
        }
      }
    }

    "when calling setSubmittedDraftId" should {

      val submittedDraftId = s"PORTAL$testDraftId"

      "return false" when {

        "the repository returns false" in new Test(useFS41SchemaVersion = true) {

          MockCreateMovementUserAnswersRepository.setSubmittedDraftId(testErn, testDraftId, submittedDraftId)
            .returns(Future.successful(false))

          await(service.setSubmittedDraftId(testErn, testDraftId, submittedDraftId)) shouldBe false
        }
      }

      "return true" when {

        "the repository returns true" in new Test(useFS41SchemaVersion = true) {

          MockCreateMovementUserAnswersRepository.setSubmittedDraftId(testErn, testDraftId, submittedDraftId)
            .returns(Future.successful(true))

          await(service.setSubmittedDraftId(testErn, testDraftId, submittedDraftId)) shouldBe true
        }
      }
    }

    "when calling formatErrorForLogging" should {
      "return the error code and reason" when {
        "error code is 12" in new Test(true) {
          service.formatErrorForLogging(RIMValidationError(None, Some(12), Some("reason"), None)) shouldBe "Some(12) (errorReason: Some(reason))"
          service.formatErrorForLogging(RIMValidationError(None, Some(12), None, None)) shouldBe "Some(12) (errorReason: None)"
        }
        "error code is 13" in new Test(true) {
          service.formatErrorForLogging(RIMValidationError(None, Some(13), Some("reason"), None)) shouldBe "Some(13) (errorReason: Some(reason))"
          service.formatErrorForLogging(RIMValidationError(None, Some(13), None, None)) shouldBe "Some(13) (errorReason: None)"
        }
      }
      "return only the error code" when {
        "error code is not 12 or 13" in new Test(true) {
            service.formatErrorForLogging(RIMValidationError(None, Some(1000), None, None)) shouldBe "Some(1000)"
            service.formatErrorForLogging(RIMValidationError(None, None, None, None)) shouldBe "None"
        }
      }
    }
  }
}
