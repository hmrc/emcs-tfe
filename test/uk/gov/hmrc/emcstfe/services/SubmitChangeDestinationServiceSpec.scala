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

import uk.gov.hmrc.emcstfe.fixtures.{GetMovementFixture, SubmitChangeDestinationFixtures}
import uk.gov.hmrc.emcstfe.mocks.config.MockAppConfig
import uk.gov.hmrc.emcstfe.mocks.connectors.MockEisConnector
import uk.gov.hmrc.emcstfe.mocks.repository.MockChangeDestinationUserAnswersRepository
import uk.gov.hmrc.emcstfe.models.request.SubmitChangeDestinationRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{EISBusinessError, EISRIMValidationError, EISUnknownError}
import uk.gov.hmrc.emcstfe.models.response.rimValidation.RIMValidationError
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.concurrent.Future

class SubmitChangeDestinationServiceSpec extends TestBaseSpec
  with SubmitChangeDestinationFixtures
  with GetMovementFixture
  with MockAppConfig
  with MockChangeDestinationUserAnswersRepository {

  import SubmitChangeDestinationFixtures.submitChangeDestinationModelMax

  trait Test extends MockEisConnector {
    val submitChangeDestinationRequest: SubmitChangeDestinationRequest = SubmitChangeDestinationRequest(submitChangeDestinationModelMax, getMovementResponse())
    val service: SubmitChangeDestinationService = new SubmitChangeDestinationService(mockEisConnector, mockChangeDestinationUserAnswersRepository, mockAppConfig)
  }

  "SubmitChangeDestinationService" when {
    "calling submitViaEIS" must {
      "return a Right" when {
        "connector call is successful and Json is the correct format" in new Test {

          MockEisConnector.submit(submitChangeDestinationRequest).returns(
            Future.successful(Right(eisSuccessResponse))
          )

          MockChangeDestinationUserAnswersRepository.setValidationErrorMessagesForDraftMovement(testErn, testArc, Seq.empty)
            .returns(Future.successful(true))

          await(service.submitViaEIS(submitChangeDestinationRequest)) shouldBe Right(eisSuccessResponse)
        }
      }

      "return a Left" when {

        "EIS returns a 422 error with RIM validation errors (inserting the errors into Mongo)" in new Test {

          MockEisConnector.submit(submitChangeDestinationRequest).returns(
            Future.successful(Left(EISRIMValidationError(eisRimValidationResponse)))
          )

          MockChangeDestinationUserAnswersRepository.setValidationErrorMessagesForDraftMovement(testErn, testArc, eisRimValidationResponse.validatorResults.get)
            .returns(Future.successful(true))

          await(service.submitViaEIS(submitChangeDestinationRequest)) shouldBe Left(EISRIMValidationError(eisRimValidationResponse))
        }

        "EIS returns a 422 error without RIM validation errors" in new Test {

          MockEisConnector.submit(submitChangeDestinationRequest).returns(
            Future.successful(Left(EISBusinessError("wrong")))
          )

          await(service.submitViaEIS(submitChangeDestinationRequest)) shouldBe Left(EISBusinessError("wrong"))
        }

        "connector call is unsuccessful" in new Test {

          MockEisConnector.submit(submitChangeDestinationRequest).returns(
            Future.successful(Left(EISUnknownError("Downstream failed to respond")))
          )

          await(service.submitViaEIS(submitChangeDestinationRequest)) shouldBe Left(EISUnknownError("Downstream failed to respond"))
        }
      }
    }

    "formatErrorForLogging" must {
      "return the errorType if the errorType is not 12 or 13" in new Test {
        val validationError: RIMValidationError = RIMValidationError(errorCategory = Some("business"), errorType = Some(1), errorReason = Some("some error"), errorLocation = Some("some location"))
        service.formatErrorForLogging(validationError) shouldBe "Some(1)"
      }

      "return Some(errorType) (errorReason: reason) if the errorType is 12 or 13" in new Test {
        val validationError12: RIMValidationError = RIMValidationError(errorCategory = Some("business"), errorType = Some(12), errorReason = Some("some error"), errorLocation = Some("some location"))
        val validationError13: RIMValidationError = RIMValidationError(errorCategory = Some("business"), errorType = Some(13), errorReason = Some("some error"), errorLocation = Some("some location"))

        service.formatErrorForLogging(validationError12) shouldBe "Some(12) (errorReason: Some(some error))"
        service.formatErrorForLogging(validationError13) shouldBe "Some(13) (errorReason: Some(some error))"
      }
    }
  }
}
