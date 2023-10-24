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

import play.api.Play.materializer
import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.emcstfe.controllers.actions.{AuthAction, FakeAuthAction}
import uk.gov.hmrc.emcstfe.fixtures.CreateMovementFixtures
import uk.gov.hmrc.emcstfe.mocks.services.MockSubmitCreateMovementService
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.UnexpectedDownstreamResponseError
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.concurrent.Future

class SubmitCreateMovementControllerSpec extends TestBaseSpec with MockSubmitCreateMovementService with CreateMovementFixtures with FakeAuthAction {

  class Fixture(authAction: AuthAction) {
    val fakeRequest = FakeRequest("POST", "/create-movement").withBody(Json.toJson(CreateMovementFixtures.createMovementModelMax))
    val controller = new SubmitCreateMovementController(Helpers.stubControllerComponents(), mockService, authAction)
  }

  s"POST ${routes.SubmitCreateMovementController.submit(testErn, testDraftId)}" when {

    "user is authorised" must {
      s"return ${Status.OK} (OK)" when {
        "service returns a Right" in new Fixture(FakeSuccessAuthAction) {

          MockService.submit(CreateMovementFixtures.createMovementModelMax).returns(Future.successful(Right(chrisSuccessResponse)))

          val result = controller.submit(testErn, testArc)(fakeRequest)

          status(result) shouldBe Status.OK
          contentAsJson(result) shouldBe chrisSuccessJson
        }
      }
      s"return ${Status.INTERNAL_SERVER_ERROR} (ISE)" when {
        "service returns a Left" in new Fixture(FakeSuccessAuthAction) {

          MockService.submit(CreateMovementFixtures.createMovementModelMax).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

          val result = controller.submit(testErn, testArc)(fakeRequest)

          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
          contentAsJson(result) shouldBe Json.obj("message" -> UnexpectedDownstreamResponseError.message)
        }
      }
    }

    "user is NOT authorised" must {
      s"return ${Status.FORBIDDEN} (FORBIDDEN)" in new Fixture(FakeFailedAuthAction) {

        val result = controller.submit(testErn, testArc)(fakeRequest)

        status(result) shouldBe Status.FORBIDDEN
      }
    }
  }
}
