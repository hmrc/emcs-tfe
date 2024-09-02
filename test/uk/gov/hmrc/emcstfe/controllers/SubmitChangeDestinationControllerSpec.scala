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
import play.api.libs.json.{JsValue, Json}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.emcstfe.controllers.actions.{AuthAction, FakeAuthAction}
import uk.gov.hmrc.emcstfe.fixtures.{GetMovementFixture, SubmitChangeDestinationFixtures}
import uk.gov.hmrc.emcstfe.mocks.services.{MockGetMovementService, MockSubmitChangeDestinationService}
import uk.gov.hmrc.emcstfe.models.request.{GetMovementRequest, SubmitChangeDestinationRequest}
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{EISBusinessError, EISRIMValidationError, EISServiceUnavailableError, UnexpectedDownstreamResponseError}
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.concurrent.Future

class SubmitChangeDestinationControllerSpec extends TestBaseSpec with MockSubmitChangeDestinationService with SubmitChangeDestinationFixtures with MockGetMovementService with GetMovementFixture with FakeAuthAction {

  import SubmitChangeDestinationFixtures.submitChangeDestinationModelMax

  class Fixture(authAction: AuthAction) {
    val fakeRequest: FakeRequest[JsValue]             = FakeRequest("POST", "/change-destination").withBody(Json.toJson(submitChangeDestinationModelMax))
    val controller: SubmitChangeDestinationController = new SubmitChangeDestinationController(Helpers.stubControllerComponents(), mockSubmitChangeDestinationService, mockGetMovementService, authAction)
    val requestModel: SubmitChangeDestinationRequest  = SubmitChangeDestinationRequest(submitChangeDestinationModelMax, getMovementResponse())
  }

  s"POST ${routes.SubmitChangeDestinationController.submit(testErn, testArc)}" when {

    "user is NOT authorised" must {
      s"return ${Status.FORBIDDEN} (FORBIDDEN)" in new Fixture(FakeFailedAuthAction) {

        val result = controller.submit(testErn, testArc)(fakeRequest)

        status(result) shouldBe Status.FORBIDDEN
      }
    }

    "user is authorised" must {

      "return a 500 (InternalServerError)" when {
        "unable to fetch the movement" in new Fixture(FakeSuccessAuthAction) {

          MockGetMovementService.getMovement(GetMovementRequest(testErn, testArc, None), forceFetchNew = true).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

          val result = controller.submit(testErn, testArc)(fakeRequest)

          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
          contentAsJson(result) shouldBe Json.obj("message" -> UnexpectedDownstreamResponseError.message)
        }
      }

      s"return ${Status.OK} (OK)" when {
        "service returns a Right" in new Fixture(FakeSuccessAuthAction) {

          MockGetMovementService.getMovement(GetMovementRequest(testErn, testArc, None), forceFetchNew = true).returns(Future.successful(Right(getMovementResponse())))

          MockSubmitChangeDestinationService.submitViaEIS(requestModel).returns(Future.successful(Right(eisSuccessResponse)))

          val result = controller.submit(testErn, testArc)(fakeRequest)

          status(result) shouldBe Status.OK
          contentAsJson(result) shouldBe eisSuccessJson()
        }
      }

      s"return ${Status.UNPROCESSABLE_ENTITY} (UNPROCESSABLE_ENTITY)" when {
        "service returns a Left(EISRIMValidationError) - when it is a RIM Validation error" in new Fixture(FakeSuccessAuthAction) {

          MockGetMovementService.getMovement(GetMovementRequest(testErn, testArc, None), forceFetchNew = true).returns(Future.successful(Right(getMovementResponse())))

          MockSubmitChangeDestinationService.submitViaEIS(requestModel).returns(Future.successful(Left(EISRIMValidationError(eisRimValidationResponse))))

          val result = controller.submit(testErn, testArc)(fakeRequest)

          status(result) shouldBe Status.UNPROCESSABLE_ENTITY
          contentAsJson(result) shouldBe Json.obj("message" -> EISRIMValidationError(eisRimValidationResponse).message)
        }

        "service returns a Left(EISBusinessError) - when it is not a RIM Validation error" in new Fixture(FakeSuccessAuthAction) {

          MockGetMovementService.getMovement(GetMovementRequest(testErn, testArc, None), forceFetchNew = true).returns(Future.successful(Right(getMovementResponse())))

          MockSubmitChangeDestinationService.submitViaEIS(requestModel).returns(Future.successful(Left(EISBusinessError("foobar"))))

          val result = controller.submit(testErn, testArc)(fakeRequest)

          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
          contentAsJson(result) shouldBe Json.obj("message" -> EISBusinessError("foobar").message)
        }
      }

      s"return ${Status.INTERNAL_SERVER_ERROR} (ISE)" when {
        "service returns a Left" in new Fixture(FakeSuccessAuthAction) {

          MockGetMovementService.getMovement(GetMovementRequest(testErn, testArc, None), forceFetchNew = true).returns(Future.successful(Right(getMovementResponse())))

          MockSubmitChangeDestinationService.submitViaEIS(requestModel).returns(Future.successful(Left(EISServiceUnavailableError("SERVICE_UNAVAILABLE"))))

          val result = controller.submit(testErn, testArc)(fakeRequest)

          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
          contentAsJson(result) shouldBe Json.obj("message" -> EISServiceUnavailableError("SERVICE_UNAVAILABLE").message)
        }
      }
    }
  }

}
