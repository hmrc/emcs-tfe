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
import uk.gov.hmrc.emcstfe.mocks.config.MockAppConfig
import uk.gov.hmrc.emcstfe.mocks.connectors.MockHttpClient
import uk.gov.hmrc.emcstfe.models.request.userAllowList.CheckUserAllowListRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.UnexpectedDownstreamResponseError
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.concurrent.Future

class UserAllowListConnectorSpec extends TestBaseSpec
  with Status with MimeTypes with HeaderNames with MockHttpClient with BeforeAndAfterAll {

  val internalAuthBearerToken = "value"
  val fakeUrl = "http://localhost:99999/user-allow-list"
  val checkRequest: CheckUserAllowListRequest = CheckUserAllowListRequest(testErn)

  trait Test extends MockAppConfig {
    MockedAppConfig.internalAuthToken.returns(internalAuthBearerToken)
    MockedAppConfig.userAllowListBaseUrl.returns(fakeUrl)
    lazy val connector = new UserAllowListConnector(mockHttpClient, mockAppConfig)
  }

  "check" should {

    "return a successful response" when {

      "downstream call is successful" in new Test {

        MockHttpClient.post(
          url = s"$fakeUrl/emcs-tfe/navHub/check",
          body = checkRequest
        ).returns(Future.successful(Right(true)))

        await(connector.check("navHub", checkRequest)) shouldBe Right(true)
      }
    }

    "return an error response" when {

      "downstream call fails" in new Test {

        MockHttpClient.post(
          url = s"$fakeUrl/emcs-tfe/createMovement/check",
          body = checkRequest
        ).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        await(connector.check("createMovement", checkRequest)) shouldBe Left(UnexpectedDownstreamResponseError)
      }
    }
  }
}
