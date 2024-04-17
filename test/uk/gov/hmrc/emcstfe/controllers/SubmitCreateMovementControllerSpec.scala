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
import uk.gov.hmrc.emcstfe.featureswitch.core.config.{DefaultDraftMovementCorrelationId, EnableNRS, SendToEIS, ValidateUsingFS41Schema}
import uk.gov.hmrc.emcstfe.fixtures.{CreateMovementFixtures, NRSBrokerFixtures}
import uk.gov.hmrc.emcstfe.mocks.config.MockAppConfig
import uk.gov.hmrc.emcstfe.mocks.services.{MockNRSBrokerService, MockSubmitCreateMovementService}
import uk.gov.hmrc.emcstfe.models.nrs.createMovement.CreateMovementNRSSubmission
import uk.gov.hmrc.emcstfe.models.request.SubmitCreateMovementRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{EISServiceUnavailableError, UnexpectedDownstreamResponseError}
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.concurrent.Future

class SubmitCreateMovementControllerSpec extends TestBaseSpec
  with MockSubmitCreateMovementService
  with CreateMovementFixtures
  with MockAppConfig
  with MockNRSBrokerService
  with NRSBrokerFixtures
  with FakeAuthAction {

  class Fixture(authAction: AuthAction, optIsNRSEnabled: Option[Boolean] = Some(true)) {
    optIsNRSEnabled.foreach { isNRSEnabled =>
      MockedAppConfig.getFeatureSwitchValue(EnableNRS).returns(isNRSEnabled)

      if (isNRSEnabled) {
        MockNRSBrokerService.submitPayload(CreateMovementNRSSubmission(testErn, CreateMovementFixtures.createMovementModelMax), testErn)
          .returns(Future.successful(Right(nrsBrokerResponseModel)))
      }
    }

    val fakeRequest = FakeRequest("POST", "/create-movement").withBody(Json.toJson(CreateMovementFixtures.createMovementModelMax))
    val controller = new SubmitCreateMovementController(Helpers.stubControllerComponents(), mockService, mockNRSBrokerService, mockAppConfig, authAction)
  }

  s"POST ${routes.SubmitCreateMovementController.submit(testErn, testDraftId)}" when {

    Seq(true, false).foreach { nrsEnabled =>

      s"when NRS Enabled is '$nrsEnabled'" when {

        "user is authorised" must {

          "when calling ChRIS" should {

            val requestModel: SubmitCreateMovementRequest = SubmitCreateMovementRequest(CreateMovementFixtures.createMovementModelMax, testDraftId, useFS41SchemaVersion = true, isChRISSubmission = true)

            s"return ${Status.OK} (OK)" when {
              "service returns a Right" in new Fixture(FakeSuccessAuthAction, Some(nrsEnabled)) {

                MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(false)

                MockedAppConfig.getFeatureSwitchValue(ValidateUsingFS41Schema).returns(true)

                MockedAppConfig.getFeatureSwitchValue(DefaultDraftMovementCorrelationId).returns(false)

                MockService.submit(requestModel).returns(Future.successful(Right(chrisSuccessResponse)))

                MockService.setSubmittedDraftId(testErn, testDraftId, requestModel.legacyCorrelationUUID).returns(Future.successful(true))

                val result = controller.submit(testErn, testDraftId)(fakeRequest)

                status(result) shouldBe Status.OK
                contentAsJson(result) shouldBe chrisSuccessJson(withSubmittedDraftId = true)
              }
            }
            s"return ${Status.INTERNAL_SERVER_ERROR} (ISE)" when {
              "service returns a Left" in new Fixture(FakeSuccessAuthAction, Some(nrsEnabled)) {

                MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(false)

                MockedAppConfig.getFeatureSwitchValue(ValidateUsingFS41Schema).returns(true)

                MockedAppConfig.getFeatureSwitchValue(DefaultDraftMovementCorrelationId).returns(false)

                MockService.submit(requestModel).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

                val result = controller.submit(testErn, testDraftId)(fakeRequest)

                status(result) shouldBe Status.INTERNAL_SERVER_ERROR
                contentAsJson(result) shouldBe Json.obj("message" -> UnexpectedDownstreamResponseError.message)
              }
            }
          }

          "when calling EIS" should {

            val requestModel: SubmitCreateMovementRequest = SubmitCreateMovementRequest(CreateMovementFixtures.createMovementModelMax, testDraftId, useFS41SchemaVersion = true, isChRISSubmission = false)

            s"return ${Status.OK} (OK)" when {
              "service returns a Right" in new Fixture(FakeSuccessAuthAction, Some(nrsEnabled)) {

                MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(true)

                MockedAppConfig.getFeatureSwitchValue(ValidateUsingFS41Schema).returns(true)

                MockedAppConfig.getFeatureSwitchValue(DefaultDraftMovementCorrelationId).returns(false)

                MockService.submitViaEIS(requestModel).returns(Future.successful(Right(eisSuccessResponse)))

                MockService.setSubmittedDraftId(testErn, testDraftId, requestModel.correlationUUID).returns(Future.successful(true))

                val result = controller.submit(testErn, testDraftId)(fakeRequest)

                status(result) shouldBe Status.OK
                contentAsJson(result) shouldBe eisSuccessJson(withSubmittedDraftId = true)
              }
            }

            s"return ${Status.INTERNAL_SERVER_ERROR} (ISE)" when {
              "service returns a Left" in new Fixture(FakeSuccessAuthAction, Some(nrsEnabled)) {

                MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(true)

                MockedAppConfig.getFeatureSwitchValue(ValidateUsingFS41Schema).returns(true)

                MockedAppConfig.getFeatureSwitchValue(DefaultDraftMovementCorrelationId).returns(false)

                MockService.submitViaEIS(requestModel).returns(Future.successful(Left(EISServiceUnavailableError("SERVICE_UNAVAILABLE"))))

                val result = controller.submit(testErn, testDraftId)(fakeRequest)

                status(result) shouldBe Status.INTERNAL_SERVER_ERROR
                contentAsJson(result) shouldBe Json.obj("message" -> EISServiceUnavailableError("SERVICE_UNAVAILABLE").message)
              }
            }
          }

          "default the correlation ID" when {

            val requestModel: SubmitCreateMovementRequest = SubmitCreateMovementRequest(CreateMovementFixtures.createMovementModelMax, testDraftId, useFS41SchemaVersion = true, isChRISSubmission = false)

            "the DefaultDraftMovementCorrelationId is enabled" in new Fixture(FakeSuccessAuthAction, Some(nrsEnabled)) {

              MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(true)

              MockedAppConfig.getFeatureSwitchValue(ValidateUsingFS41Schema).returns(true)

              MockedAppConfig.getFeatureSwitchValue(DefaultDraftMovementCorrelationId).returns(true)

              MockService.submitViaEIS(requestModel).returns(Future.successful(Right(eisSuccessResponse)))

              MockService.setSubmittedDraftId(testErn, testDraftId, "PORTAL123").returns(Future.successful(true))

              val result = controller.submit(testErn, testDraftId)(fakeRequest)

              status(result) shouldBe Status.OK
              contentAsJson(result) shouldBe eisSuccessJson(withSubmittedDraftId = true, submittedDraftId = Some("PORTAL123"))
            }
          }
        }
      }
    }

    "user is NOT authorised" must {
      s"return ${Status.FORBIDDEN} (FORBIDDEN)" in new Fixture(FakeFailedAuthAction, None) {

        val result = controller.submit(testErn, testDraftId)(fakeRequest)

        status(result) shouldBe Status.FORBIDDEN
      }
    }
  }
}
