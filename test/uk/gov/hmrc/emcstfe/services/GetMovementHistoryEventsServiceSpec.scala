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
import uk.gov.hmrc.emcstfe.fixtures.GetMovementHistoryEventsFixture
import uk.gov.hmrc.emcstfe.mocks.connectors.MockEisConnector
import uk.gov.hmrc.emcstfe.models.request.GetMovementHistoryEventsRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.concurrent.Future

class GetMovementHistoryEventsServiceSpec extends TestBaseSpec with GetMovementHistoryEventsFixture {

  trait Test extends MockEisConnector {
    val movementHistoryEventsRequest: GetMovementHistoryEventsRequest = GetMovementHistoryEventsRequest(testErn, testArc)
    val service: GetMovementHistoryEventsService = new GetMovementHistoryEventsService(mockEisConnector)
  }

  "getMovementHistoryEvent" should {
    "return Right" when {
      "request is valid and connector call succeeds" in new Test {

        MockEisConnector.getMovementHistoryEvents(movementHistoryEventsRequest)
          .returns(Right(getMovementHistoryEventsResponseModel).pure[Future])

        await(service.getMovementHistoryEvent(movementHistoryEventsRequest)) shouldBe Right(getMovementHistoryEvents)
      }
    }

    "return Left" when {
      "request is valid and connector call fails" in new Test {

        MockEisConnector.getMovementHistoryEvents(movementHistoryEventsRequest)
          .returns(Left(ErrorResponse.UnexpectedDownstreamResponseError).pure[Future])

        await(service.getMovementHistoryEvent(movementHistoryEventsRequest)) shouldBe Left(ErrorResponse.UnexpectedDownstreamResponseError)
      }

      "request is invalid" in new Test {

        MockEisConnector.getMovementHistoryEvents(movementHistoryEventsRequest)
          .returns(Left(ErrorResponse.UnexpectedDownstreamResponseError).pure[Future])

        await(service.getMovementHistoryEvent(movementHistoryEventsRequest)) shouldBe Left(ErrorResponse.UnexpectedDownstreamResponseError)
      }
    }
  }
}
