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

package uk.gov.hmrc.emcstfe.connectors

import org.scalatest.BeforeAndAfterAll
import play.api.http.{HeaderNames, MimeTypes, Status}
import uk.gov.hmrc.emcstfe.fixtures.NRSBrokerFixtures
import uk.gov.hmrc.emcstfe.mocks.config.MockAppConfig
import uk.gov.hmrc.emcstfe.mocks.connectors.MockHttpClient
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.UnexpectedDownstreamResponseError
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.concurrent.Future

class NRSBrokerConnectorSpec extends TestBaseSpec
  with Status
  with MimeTypes
  with HeaderNames
  with MockHttpClient
  with BeforeAndAfterAll
  with NRSBrokerFixtures {

  val fakeUrl = "http://localhost:99999/emcs-tfe-nrs-message-broker"

  trait Test extends MockAppConfig {
    MockedAppConfig.nrsBrokerBaseUrl.returns(fakeUrl)
    lazy val connector = new NRSBrokerConnector(mockHttpClient, mockAppConfig)
  }

  ".submitPayload" should {

    "return a successful response" when {

      "downstream call is successful" in new Test {

        MockHttpClient.put(
          url = s"$fakeUrl/trader/$testErn/nrs/submission",
          body = nrsPayloadModel
        ).returns(Future.successful(Right(nrsBrokerResponseModel)))

        await(connector.submitPayload(nrsPayloadModel, testErn)) shouldBe Right(nrsBrokerResponseModel)
      }
    }

    "return an error response" when {

      "downstream call fails" in new Test {

        MockHttpClient.put(
          url = s"$fakeUrl/trader/$testErn/nrs/submission",
          body = nrsPayloadModel
        ).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        await(connector.submitPayload(nrsPayloadModel, testErn)) shouldBe Left(UnexpectedDownstreamResponseError)
      }
    }
  }

}
