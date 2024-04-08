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
import uk.gov.hmrc.emcstfe.featureswitch.core.config.{EnableNRS, SendToEIS}
import uk.gov.hmrc.emcstfe.fixtures.{NRSBrokerFixtures, SubmitExplainShortageExcessFixtures}
import uk.gov.hmrc.emcstfe.mocks.config.MockAppConfig
import uk.gov.hmrc.emcstfe.mocks.services.{MockNRSBrokerService, MockSubmitExplainShortageExcessService}
import uk.gov.hmrc.emcstfe.models.common.SubmitterType.Consignor
import uk.gov.hmrc.emcstfe.models.nrs.NotableEvent.ExplainShortageOrExcessNotableEvent
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.UnexpectedDownstreamResponseError
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.concurrent.Future

class SubmitExplainShortageExcessControllerSpec extends TestBaseSpec
  with MockSubmitExplainShortageExcessService
  with SubmitExplainShortageExcessFixtures
  with FakeAuthAction
  with MockAppConfig
  with MockNRSBrokerService
  with NRSBrokerFixtures {

  import SubmitExplainShortageExcessFixtures.submitExplainShortageExcessModelMax

  class Fixture(authAction: AuthAction, optIsNRSEnabled: Option[Boolean] = Some(true)) {

    optIsNRSEnabled.map { isNRSEnabled =>
      MockedAppConfig.getFeatureSwitchValue(EnableNRS).returns(isNRSEnabled)

      if (isNRSEnabled) {
        MockNRSBrokerService.submitPayload(explainShortageExcessNRSSubmission(Consignor, testErn), testErn, ExplainShortageOrExcessNotableEvent).returns(Future.successful(Right(nrsBrokerResponseModel)))
      }
    }

    val fakeRequest = FakeRequest("POST", "/explain-shortage-excess").withBody(Json.toJson(submitExplainShortageExcessModelMax(Consignor)))
    val controller = new SubmitExplainShortageExcessController(Helpers.stubControllerComponents(), mockService, mockNRSBrokerService, authAction, mockAppConfig)
  }

  s"POST ${routes.SubmitExplainShortageExcessController.submit(testErn, testArc)}" when {

    "user is authorised" must {

      Seq(true, false).foreach { nrsEnabled =>
        s"when calling NRS is $nrsEnabled" when {

          "the SendToEIS feature switch is disabled" must {
            s"return ${Status.OK} (OK)" when {
              "service returns a Right" in new Fixture(FakeSuccessAuthAction, Some(nrsEnabled)) {
                MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(false)

                MockService.submit(submitExplainShortageExcessModelMax(Consignor)).returns(Future.successful(Right(chrisSuccessResponse)))

                val result = controller.submit(testErn, testArc)(fakeRequest)

                status(result) shouldBe Status.OK
                contentAsJson(result) shouldBe chrisSuccessJson()
              }
            }
            s"return ${Status.INTERNAL_SERVER_ERROR} (ISE)" when {
              "service returns a Left" in new Fixture(FakeSuccessAuthAction, Some(nrsEnabled)) {
                MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(false)

                MockService.submit(submitExplainShortageExcessModelMax(Consignor)).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

                val result = controller.submit(testErn, testArc)(fakeRequest)

                status(result) shouldBe Status.INTERNAL_SERVER_ERROR
                contentAsJson(result) shouldBe Json.obj("message" -> UnexpectedDownstreamResponseError.message)
              }
            }
          }

          "the SendToEIS feature switch is enabled" must {
            s"return ${Status.OK} (OK)" when {
              "service returns a Right" in new Fixture(FakeSuccessAuthAction, Some(nrsEnabled)) {
                MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(true)

                MockService.submitViaEIS(submitExplainShortageExcessModelMax(Consignor)).returns(Future.successful(Right(eisSuccessResponse)))

                val result = controller.submit(testErn, testArc)(fakeRequest)

                status(result) shouldBe Status.OK
                contentAsJson(result) shouldBe eisSuccessJson()
              }
            }
            s"return ${Status.INTERNAL_SERVER_ERROR} (ISE)" when {
              "service returns a Left" in new Fixture(FakeSuccessAuthAction, Some(nrsEnabled)) {
                MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(true)

                MockService.submitViaEIS(submitExplainShortageExcessModelMax(Consignor)).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

                val result = controller.submit(testErn, testArc)(fakeRequest)

                status(result) shouldBe Status.INTERNAL_SERVER_ERROR
                contentAsJson(result) shouldBe Json.obj("message" -> UnexpectedDownstreamResponseError.message)
              }
            }
          }
        }
      }
    }

    "user is NOT authorised" must {
      s"return ${Status.FORBIDDEN} (FORBIDDEN)" in new Fixture(FakeFailedAuthAction, optIsNRSEnabled = None) {

        val result = controller.submit(testErn, testArc)(fakeRequest)

        status(result) shouldBe Status.FORBIDDEN
      }
    }
  }
}
