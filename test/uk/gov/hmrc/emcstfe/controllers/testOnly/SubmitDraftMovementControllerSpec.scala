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

package uk.gov.hmrc.emcstfe.controllers.testOnly

import play.api.Play.materializer
import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.emcstfe.controllers.actions.FakeAuthAction
import uk.gov.hmrc.emcstfe.fixtures.SubmitDraftMovementFixture
import uk.gov.hmrc.emcstfe.mocks.services.MockSubmitDraftMovementService
import uk.gov.hmrc.emcstfe.models.request.SubmitDraftMovementRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.UnexpectedDownstreamResponseError
import uk.gov.hmrc.emcstfe.support.UnitSpec

import scala.concurrent.Future

class SubmitDraftMovementControllerSpec extends UnitSpec with MockSubmitDraftMovementService with SubmitDraftMovementFixture with FakeAuthAction {

  private val fakeRequest = FakeRequest("POST", "/test-only/submit").withBody(scala.xml.XML.loadString(submitDraftMovementRequestBody))
  private val controller = new SubmitDraftMovementController(Helpers.stubControllerComponents(), mockService, FakeSuccessAuthAction)

  private val submitDraftMovementRequest = SubmitDraftMovementRequest("", "", submitDraftMovementRequestBody)


  "POST /test-only/submit" should {
    "return 200" when {
      "service returns a Right" in {

        MockService.submitDraftMovement(submitDraftMovementRequest).returns(Future.successful(Right(chrisSuccessResponse)))

        val result = controller.submitDraftMovement()(fakeRequest)

        status(result) shouldBe Status.OK
        contentAsJson(result) shouldBe chrisSuccessJson
      }
    }
    "return 500" when {
      "service returns a Left" in {

        MockService.submitDraftMovement(submitDraftMovementRequest).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        val result = controller.submitDraftMovement()(fakeRequest)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.obj("message" -> UnexpectedDownstreamResponseError.message)
      }
    }
  }
}
