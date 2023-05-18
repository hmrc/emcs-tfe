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

package uk.gov.hmrc.emcstfe.controllers.actions

import org.scalamock.scalatest.MockFactory
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.mvc.Results.Ok
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.mocks.connectors.MockUserAllowListConnector
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.request.CheckUserAllowListRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{UnexpectedDownstreamResponseError, UserAllowListError}
import uk.gov.hmrc.emcstfe.support.UnitSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserAllowListActionSpec extends UnitSpec with MockFactory with MockUserAllowListConnector {
  implicit lazy val request = UserRequest(FakeRequest(), testErn, testInternalId, testCredId)
  lazy val mockAppConfig = mock[AppConfig]

  lazy val userAllowListAction = new UserAllowListActionImpl(
    userAllowListConnector = mockUserAllowListConnector,
    config = mockAppConfig
  )(global)

  class Harness(enabled: Boolean, connectorResponse: Either[ErrorResponse, Boolean]) {

    (() => mockAppConfig.allowListEnabled).expects().returns(enabled).anyNumberOfTimes()

    if(enabled) {
      MockUserAllowListConnector.check(CheckUserAllowListRequest(testErn))
        .returns(Future.successful(connectorResponse))
    }

    val result: Future[Result] = userAllowListAction.invokeBlock(request, { _: UserRequest[_] =>
      Future.successful(Ok)
    })
  }

  "UserAllowListAction" when {

    "the allow list feature is enabled" when {

      "the connector returns true (on the list)" must {

        "execute the supplied block" in new Harness(enabled = true, connectorResponse = Right(true)) {
          status(result) shouldBe OK
        }
      }

      "the connector returns false (NOT on the list)" must {

        "execute the supplied block" in new Harness(enabled = true, connectorResponse = Right(false)) {
          status(result) shouldBe INTERNAL_SERVER_ERROR
          contentAsJson(result) shouldBe Json.toJson(UserAllowListError(testErn))
        }
      }

      "the connector returns a Left" must {
        "execute the supplied block" in new Harness(enabled = true, connectorResponse = Left(UnexpectedDownstreamResponseError)) {
          status(result) shouldBe INTERNAL_SERVER_ERROR
          contentAsJson(result) shouldBe Json.toJson(UnexpectedDownstreamResponseError)
        }
      }
    }

    "the allow list feature is disabled" must {

      "execute the supplied block" in new Harness(enabled = false, connectorResponse = Right(false)) {
        status(result) shouldBe OK
      }
    }
  }
}
