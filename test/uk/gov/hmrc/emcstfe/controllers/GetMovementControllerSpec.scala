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
import uk.gov.hmrc.emcstfe.fixtures.GetMovementFixture
import uk.gov.hmrc.emcstfe.mocks.services.MockGetMovementService
import uk.gov.hmrc.emcstfe.models.request.GetMovementRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.UnexpectedDownstreamResponseError
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.concurrent.Future

class GetMovementControllerSpec extends TestBaseSpec with MockGetMovementService with GetMovementFixture with FakeAuthAction {

  private val fakeRequest = FakeRequest("GET", "/movement/:ern/:arc")
  private val controller = new GetMovementController(Helpers.stubControllerComponents(), mockGetMovementService, FakeSuccessAuthAction)

  "GET /movement/:ern/:arc" should {
    "return 200" when {
      "service returns a Right" in {

        val getMovementRequest = GetMovementRequest(testErn, testArc, Some(1))

        MockGetMovementService.getMovement(getMovementRequest, forceFetchNew = false).returns(Future.successful(Right(getMovementResponse())))

        val result = controller.getMovement(testErn, testArc, sequenceNumber = Some(1))(fakeRequest)

        status(result) shouldBe Status.OK
        contentAsJson(result) shouldBe getMovementJson()
      }
    }
    "return 500" when {
      "service returns a Left" in {

        val getMovementRequest = GetMovementRequest(testErn, testArc)

        MockGetMovementService.getMovement(getMovementRequest, forceFetchNew = true).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        val result = controller.getMovement(testErn, testArc, forceFetchNew = true)(fakeRequest)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.obj("message" -> UnexpectedDownstreamResponseError.message)
      }
    }
  }
}
