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

import cats.implicits.catsSyntaxApplicativeId
import uk.gov.hmrc.emcstfe.featureswitch.core.config.SendToEIS
import uk.gov.hmrc.emcstfe.fixtures.GetMovementHistoryEventsFixture
import uk.gov.hmrc.emcstfe.mocks.config.MockAppConfig
import uk.gov.hmrc.emcstfe.mocks.connectors.{MockChrisConnector, MockEisConnector}
import uk.gov.hmrc.emcstfe.models.request.GetMovementHistoryEventsRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{SoapExtractionError, XmlValidationError}
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.concurrent.Future

class GetMovementHistoryEventsServiceSpec extends TestBaseSpec with GetMovementHistoryEventsFixture {

  trait Test extends MockEisConnector with MockChrisConnector with MockAppConfig {
    val movementHistoryEventsRequest: GetMovementHistoryEventsRequest = GetMovementHistoryEventsRequest(testErn, testArc)
    val service: GetMovementHistoryEventsService = new GetMovementHistoryEventsService(mockEisConnector, mockChrisConnector, mockAppConfig)
  }

  "getMovementHistoryEvent" should {

    "when calling ChRIS" must {
      "return Right" when {
        "request is valid and connector call succeeds" in new Test {

          MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(false)

          MockChrisConnector.postChrisSOAPRequestAndExtractToModel(movementHistoryEventsRequest)
            .returns(Right(getMovementHistoryEvents).pure[Future])

          await(service.getMovementHistoryEvent(movementHistoryEventsRequest)) shouldBe Right(getMovementHistoryEvents)
        }
      }

      "return a Left" when {
        "connector call is unsuccessful" in new Test {

          MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(false)

          MockChrisConnector
            .postChrisSOAPRequestAndExtractToModel(movementHistoryEventsRequest)
            .returns(Future.successful(Left(XmlValidationError)))

          await(service.getMovementHistoryEvent(movementHistoryEventsRequest)) shouldBe Left(XmlValidationError)
        }

        "connector call response cannot be extracted" in new Test {

          MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(false)

          MockChrisConnector
            .postChrisSOAPRequestAndExtractToModel(movementHistoryEventsRequest)
            .returns(Future.successful(Left(SoapExtractionError)))

          await(service.getMovementHistoryEvent(movementHistoryEventsRequest)) shouldBe Left(SoapExtractionError)
        }
      }
    }

    "when calling EIS" must {
      "return Right" when {
        "request is valid and connector call succeeds" in new Test {

          MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(true)

          MockEisConnector.getMovementHistoryEvents(movementHistoryEventsRequest)
            .returns(Right(getMovementHistoryEventsResponseModel).pure[Future])

          await(service.getMovementHistoryEvent(movementHistoryEventsRequest)) shouldBe Right(getMovementHistoryEvents)
        }
      }

      "return Left" when {
        "request is valid and connector call fails" in new Test {

          MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(true)

          MockEisConnector.getMovementHistoryEvents(movementHistoryEventsRequest)
            .returns(Left(ErrorResponse.UnexpectedDownstreamResponseError).pure[Future])

          await(service.getMovementHistoryEvent(movementHistoryEventsRequest)) shouldBe Left(ErrorResponse.UnexpectedDownstreamResponseError)
        }

        "request is invalid" in new Test {

          MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(true)

          MockEisConnector.getMovementHistoryEvents(movementHistoryEventsRequest)
            .returns(Left(ErrorResponse.UnexpectedDownstreamResponseError).pure[Future])

          await(service.getMovementHistoryEvent(movementHistoryEventsRequest)) shouldBe Left(ErrorResponse.UnexpectedDownstreamResponseError)
        }
      }
    }
  }
}
