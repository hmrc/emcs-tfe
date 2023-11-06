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
import uk.gov.hmrc.emcstfe.fixtures.GetMessagesFixtures
import uk.gov.hmrc.emcstfe.mocks.services.MockGetMessagesService
import uk.gov.hmrc.emcstfe.models.request.GetMessagesRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{QueryParameterError, UnexpectedDownstreamResponseError}
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.concurrent.Future

class GetMessagesControllerSpec extends TestBaseSpec with MockGetMessagesService with GetMessagesFixtures with FakeAuthAction {

  import GetMessagesResponseFixtures.{getMessagesResponseModel, getMessagesResponseJson}

  private val fakeRequest = FakeRequest("GET", "/messages/:ern")
  private val controller = new GetMessagesController(Helpers.stubControllerComponents(), mockService, FakeSuccessAuthAction)

  private val getMessagesRequest = GetMessagesRequest(testErn, "arc", "A", 1)


  "GET /movement/:ern/:arc" should {
    "return 200" when {
      "service returns a Right" in {

        MockService.getMessages(getMessagesRequest).returns(Future.successful(Right(getMessagesResponseModel)))

        val result = controller.getMessages(testErn, "arc", "A", 1)(fakeRequest)

        status(result) shouldBe Status.OK
        contentAsJson(result) shouldBe getMessagesResponseJson
      }
    }
    "return 500" when {
      "query parameters are invalid" in {
        val result = controller.getMessages(testErn, "beans", "beans", 0)(fakeRequest)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.obj("message" -> QueryParameterError(Seq("sortField" -> "beans", "sortOrder" -> "beans", "page" -> "0")).message)
      }
      "service returns a Left" in {

        MockService.getMessages(getMessagesRequest).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        val result = controller.getMessages(testErn, "arc", "A", 1)(fakeRequest)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.obj("message" -> UnexpectedDownstreamResponseError.message)
      }
    }
  }
}
