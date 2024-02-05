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
import uk.gov.hmrc.emcstfe.models.request.GetMovementHistoryEventsRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.models.response.getMovementHistoryEvents.MovementHistoryEvent
import uk.gov.hmrc.emcstfe.services.GetMovementHistoryEventsService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockGetMovementHistoryEventsService extends MockFactory {
  lazy val mockService: GetMovementHistoryEventsService = mock[GetMovementHistoryEventsService]

  object MockService {
    def getMovementHistoryEvent(getMovementHistoryEventsRequest: GetMovementHistoryEventsRequest): CallHandler3[GetMovementHistoryEventsRequest, HeaderCarrier, ExecutionContext, Future[Either[ErrorResponse, Seq[MovementHistoryEvent]]]] = {
      (mockService.getMovementHistoryEvent(_: GetMovementHistoryEventsRequest)(_: HeaderCarrier, _: ExecutionContext))
        .expects(getMovementHistoryEventsRequest, *, *)
    }
  }
}


