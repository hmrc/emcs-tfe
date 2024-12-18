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
import uk.gov.hmrc.emcstfe.featureswitch.core.config.DefaultDraftMovementCorrelationId
import uk.gov.hmrc.emcstfe.fixtures.{CreateMovementFixtures, EISResponsesFixture}
import uk.gov.hmrc.emcstfe.mocks.config.MockAppConfig
import uk.gov.hmrc.emcstfe.mocks.services.MockSubmitCreateMovementService
import uk.gov.hmrc.emcstfe.models.request.SubmitCreateMovementRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{EISBusinessError, EISRIMValidationError, EISServiceUnavailableError}
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.concurrent.Future

class SubmitCreateMovementControllerSpec extends TestBaseSpec with MockSubmitCreateMovementService with CreateMovementFixtures with MockAppConfig with FakeAuthAction with EISResponsesFixture {

  class Fixture(authAction: AuthAction) {
    val fakeRequest = FakeRequest("POST", "/create-movement").withBody(Json.toJson(CreateMovementFixtures.createMovementModelMax))
    val controller  = new SubmitCreateMovementController(Helpers.stubControllerComponents(), mockService, mockAppConfig, authAction)
  }

  s"POST ${routes.SubmitCreateMovementController.submit(testErn, testDraftId)}" when {

    "user is authorised" must {

      val requestModel: SubmitCreateMovementRequest = SubmitCreateMovementRequest(CreateMovementFixtures.createMovementModelMax, testDraftId)

      s"return ${Status.OK} (OK)" when {
        "service returns a Right" in new Fixture(FakeSuccessAuthAction) {

          MockedAppConfig.getFeatureSwitchValue(DefaultDraftMovementCorrelationId).returns(false)

          MockService.submitViaEIS(requestModel).returns(Future.successful(Right(eisSuccessResponse)))

          MockService.setSubmittedDraftId(testErn, testDraftId, requestModel.correlationUUID).returns(Future.successful(true))

          val result = controller.submit(testErn, testDraftId)(fakeRequest)

          status(result) shouldBe Status.OK
          contentAsJson(result) shouldBe eisSuccessJson(withSubmittedDraftId = true)
        }
      }

      s"return ${Status.UNPROCESSABLE_ENTITY} (UNPROCESSABLE_ENTITY)" when {
        "service returns a Left(EISRIMValidationError) - when it is a RIM Validation error" in new Fixture(FakeSuccessAuthAction) {

          MockedAppConfig.getFeatureSwitchValue(DefaultDraftMovementCorrelationId).returns(false)

          MockService.submitViaEIS(requestModel).returns(Future.successful(Left(EISRIMValidationError(eisRimValidationResponse))))

          val result = controller.submit(testErn, testDraftId)(fakeRequest)

          status(result) shouldBe Status.UNPROCESSABLE_ENTITY
          contentAsJson(result) shouldBe Json.obj("message" -> EISRIMValidationError(eisRimValidationResponse).message)
        }

        "service returns a Left(EISBusinessError) - when it is not a RIM Validation error" in new Fixture(FakeSuccessAuthAction) {

          MockedAppConfig.getFeatureSwitchValue(DefaultDraftMovementCorrelationId).returns(false)

          MockService.submitViaEIS(requestModel).returns(Future.successful(Left(EISBusinessError("foobar"))))

          val result = controller.submit(testErn, testDraftId)(fakeRequest)

          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
          contentAsJson(result) shouldBe Json.obj("message" -> EISBusinessError("foobar").message)
        }
      }

      s"return ${Status.INTERNAL_SERVER_ERROR} (ISE)" when {
        "service returns a Left" in new Fixture(FakeSuccessAuthAction) {

          MockedAppConfig.getFeatureSwitchValue(DefaultDraftMovementCorrelationId).returns(false)

          MockService.submitViaEIS(requestModel).returns(Future.successful(Left(EISServiceUnavailableError("SERVICE_UNAVAILABLE"))))

          val result = controller.submit(testErn, testDraftId)(fakeRequest)

          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
          contentAsJson(result) shouldBe Json.obj("message" -> EISServiceUnavailableError("SERVICE_UNAVAILABLE").message)
        }
      }

      "default the correlation ID" when {

        val requestModel: SubmitCreateMovementRequest = SubmitCreateMovementRequest(CreateMovementFixtures.createMovementModelMax, testDraftId)

        "the DefaultDraftMovementCorrelationId is enabled" in new Fixture(FakeSuccessAuthAction) {

          MockedAppConfig.getFeatureSwitchValue(DefaultDraftMovementCorrelationId).returns(true)

          MockService.submitViaEIS(requestModel).returns(Future.successful(Right(eisSuccessResponse)))

          MockService.setSubmittedDraftId(testErn, testDraftId, "PORTAL123").returns(Future.successful(true))

          val result = controller.submit(testErn, testDraftId)(fakeRequest)

          status(result) shouldBe Status.OK
          contentAsJson(result) shouldBe eisSuccessJson(withSubmittedDraftId = true, submittedDraftId = Some("PORTAL123"))
        }
      }
    }

    "user is NOT authorised" must {
      s"return ${Status.FORBIDDEN} (FORBIDDEN)" in new Fixture(FakeFailedAuthAction) {

        val result = controller.submit(testErn, testDraftId)(fakeRequest)

        status(result) shouldBe Status.FORBIDDEN
      }
    }
  }

}
