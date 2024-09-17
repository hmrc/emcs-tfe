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

package uk.gov.hmrc.emcstfe.controllers

import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.emcstfe.controllers.actions.FakeAuthAction
import uk.gov.hmrc.emcstfe.fixtures.TraderKnownFactsFixtures
import uk.gov.hmrc.emcstfe.mocks.services.MockTraderKnownFactsService
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.UnexpectedDownstreamResponseError
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.concurrent.Future

class TraderKnownFactsControllerSpec extends TestBaseSpec with MockTraderKnownFactsService with TraderKnownFactsFixtures with FakeAuthAction {

  private val fakeRequest = FakeRequest("GET", "/")
  private val controller  = new TraderKnownFactsController(Helpers.stubControllerComponents(), mockService, FakeSuccessAuthAction)

  "GET /trader-known-facts" should {
    "return 200" when {
      "service returns a Right(Some(_))" in {

        MockService.getTraderKnownFacts(testErn).returns(Future.successful(Right(Some(testTraderKnownFactsModel))))

        val result = controller.getTraderKnownFacts(testErn)(fakeRequest)

        status(result) shouldBe Status.OK
        contentAsJson(result) shouldBe Json.parse(testTraderKnownFactsJson)
      }
    }
    "return 204" when {
      "service returns a Right(None)" in {

        MockService.getTraderKnownFacts(testErn).returns(Future.successful(Right(None)))

        val result = controller.getTraderKnownFacts(testErn)(fakeRequest)

        status(result) shouldBe Status.NO_CONTENT
      }
    }
    "return 500" when {
      "service returns a Left" in {

        MockService.getTraderKnownFacts(testErn).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        val result = controller.getTraderKnownFacts(testErn)(fakeRequest)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.obj("message" -> UnexpectedDownstreamResponseError.message)
      }
    }
  }

}
