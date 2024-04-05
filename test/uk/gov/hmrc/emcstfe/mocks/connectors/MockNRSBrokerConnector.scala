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

package uk.gov.hmrc.emcstfe.mocks.connectors

import org.scalamock.handlers.CallHandler4
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.emcstfe.connectors.NRSBrokerConnector
import uk.gov.hmrc.emcstfe.models.nrs.NRSPayload
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.models.response.nrsBroker.NRSBrokerInsertPayloadResponse
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockNRSBrokerConnector extends MockFactory {

  lazy val mockNRSBrokerConnector: NRSBrokerConnector = mock[NRSBrokerConnector]

  object MockNRSBrokerConnector {

    def submitPayload(nrsPayload: NRSPayload, ern: String): CallHandler4[NRSPayload, String, HeaderCarrier, ExecutionContext, Future[Either[ErrorResponse, NRSBrokerInsertPayloadResponse]]] =
      (mockNRSBrokerConnector.submitPayload(_: NRSPayload, _: String)(_: HeaderCarrier, _: ExecutionContext))
        .expects(nrsPayload, ern, *, *)
  }
}
