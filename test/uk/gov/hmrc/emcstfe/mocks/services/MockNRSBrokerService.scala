/*
 * Copyright 2024 HM Revenue & Customs
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

import org.scalamock.handlers.CallHandler7
import org.scalamock.scalatest.MockFactory
import play.api.libs.json.Writes
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.nrs.NotableEvent
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.models.response.nrsBroker.NRSBrokerInsertPayloadResponse
import uk.gov.hmrc.emcstfe.services.nrs.NRSBrokerService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockNRSBrokerService extends MockFactory {

  val mockNRSBrokerService: NRSBrokerService = mock[NRSBrokerService]

  object MockNRSBrokerService {

    def submitPayload[A](submission: A, ern: String, notableEvent: NotableEvent): CallHandler7[A, String, NotableEvent, HeaderCarrier, ExecutionContext, UserRequest[_], Writes[A], Future[Either[ErrorResponse, NRSBrokerInsertPayloadResponse]]] = {
      (mockNRSBrokerService.submitPayload(_: A, _: String, _: NotableEvent)(_: HeaderCarrier, _: ExecutionContext, _: UserRequest[_], _: Writes[A]))
        .expects(submission, ern, notableEvent, *, *, *, *)
    }
  }

}
