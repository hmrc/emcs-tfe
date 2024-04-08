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

package uk.gov.hmrc.emcstfe.services

import uk.gov.hmrc.auth.core.UnsupportedAuthProvider
import uk.gov.hmrc.auth.core.authorise.EmptyPredicate
import uk.gov.hmrc.emcstfe.fixtures.{NRSBrokerFixtures, SubmitAlertOrRejectionFixtures}
import uk.gov.hmrc.emcstfe.mocks.connectors.{MockAuthConnector, MockNRSBrokerConnector}
import uk.gov.hmrc.emcstfe.models.nrs.NotableEvent.{AlertRejectNotableEvent, CreateMovementNotableEvent}
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{IdentityDataException, UnexpectedDownstreamResponseError}
import uk.gov.hmrc.emcstfe.services.nrs.NRSBrokerService
import uk.gov.hmrc.emcstfe.services.nrs.NRSBrokerService.retrievals
import uk.gov.hmrc.emcstfe.support.TestBaseSpec
import uk.gov.hmrc.emcstfe.utils.TimeMachine
import uk.gov.hmrc.http.{Authorization, HeaderCarrier}

import java.time.Instant
import scala.concurrent.Future

class NRSBrokerServiceSpec extends TestBaseSpec with MockNRSBrokerConnector with MockAuthConnector with NRSBrokerFixtures with SubmitAlertOrRejectionFixtures {

  val instantNow: Instant = Instant.now()
  private val timeMachine: TimeMachine = () => Instant.ofEpochMilli(1L)

  implicit lazy val headerCarrierWithAuthToken: HeaderCarrier = hc.copy(authorization = Some(Authorization("Bearer token")))

  class Setup {

    val service = new NRSBrokerService(mockNRSBrokerConnector, mockAuthConnector, timeMachine)
  }

  ".submitPayload" should {

    "return a Left" when {

      "retrieving the identity data throws an exception" in new Setup {

        MockAuthConnector.authorise(EmptyPredicate, retrievals).returns(Future.failed(UnsupportedAuthProvider("Game over!")))

        await(service.submitPayload(nrsSubmission, testErn, CreateMovementNotableEvent)) shouldBe Left(IdentityDataException("UnsupportedAuthProvider"))
      }

      "the broker connector returns a Left" in new Setup {

        MockAuthConnector.authorise(EmptyPredicate, retrievals).returns(Future.successful(predicateRetrieval))

        MockNRSBrokerConnector.submitPayload(alertRejectNRSPayload, testErn).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        await(service.submitPayload(nrsSubmission, testErn, AlertRejectNotableEvent)) shouldBe Left(UnexpectedDownstreamResponseError)
      }

      "the broker connector returns an unhandled future exception" in new Setup {

        MockAuthConnector.authorise(EmptyPredicate, retrievals).returns(Future.successful(predicateRetrieval))

        MockNRSBrokerConnector.submitPayload(alertRejectNRSPayload, testErn).returns(Future.failed(new Exception("Game over!")))

        await(service.submitPayload(nrsSubmission, testErn, AlertRejectNotableEvent)) shouldBe Left(UnexpectedDownstreamResponseError)
      }
    }

    "return a Right" when {

      "the identity data is retrieved successfully and the payload is submitted to the broker correctly" in new Setup {

        MockAuthConnector.authorise(EmptyPredicate, retrievals).returns(Future.successful(predicateRetrieval))

        MockNRSBrokerConnector.submitPayload(alertRejectNRSPayload, testErn).returns(Future.successful(Right(nrsBrokerResponseModel)))

        await(service.submitPayload(nrsSubmission, testErn, AlertRejectNotableEvent)) shouldBe Right(nrsBrokerResponseModel)
      }
    }
  }
}
