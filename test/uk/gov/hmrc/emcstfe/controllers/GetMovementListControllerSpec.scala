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
import uk.gov.hmrc.emcstfe.featureswitch.core.config.SendToEIS
import uk.gov.hmrc.emcstfe.fixtures.GetMovementListFixture
import uk.gov.hmrc.emcstfe.mocks.config.MockAppConfig
import uk.gov.hmrc.emcstfe.mocks.services.MockGetMovementListService
import uk.gov.hmrc.emcstfe.models.request.{GetMovementListRequest, GetMovementListSearchOptions}
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.UnexpectedDownstreamResponseError
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.concurrent.Future

class GetMovementListControllerSpec extends TestBaseSpec
  with MockGetMovementListService
  with GetMovementListFixture
  with FakeAuthAction
  with MockAppConfig {

  private val fakeRequest = FakeRequest("GET", "/movement/:ern/:arc")
  private val controller = new GetMovementListController(Helpers.stubControllerComponents(), mockService, FakeSuccessAuthAction, mockAppConfig)

  private val searchOptions = GetMovementListSearchOptions()
  private val getMovementListRequest = GetMovementListRequest(testErn, searchOptions, isEISFeatureEnabled = false)

  "GET /movements/:ern" should {


    "return 200" when {
      "service returns a Right" in {

        MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(false)

        MockService.getMovementList(getMovementListRequest).returns(Future.successful(Right(getMovementListResponse)))

        val result = controller.getMovementList(testErn, searchOptions)(fakeRequest)

        status(result) shouldBe Status.OK
        contentAsJson(result) shouldBe getMovementListJson
      }

      "service returns a Right (setting EIS request model parameter to true when SendToEIS feature is enabled)" in {

        MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(true)

        MockService.getMovementList(getMovementListRequest.copy(isEISFeatureEnabled = true))
          .returns(Future.successful(Right(getMovementListResponse)))

        val result = controller.getMovementList(testErn, searchOptions)(fakeRequest)

        status(result) shouldBe Status.OK
        contentAsJson(result) shouldBe getMovementListJson
      }
    }
    "return 500" when {
      "service returns a Left" in {

        MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(false)

        MockService.getMovementList(getMovementListRequest).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        val result = controller.getMovementList(testErn, searchOptions)(fakeRequest)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.obj("message" -> UnexpectedDownstreamResponseError.message)
      }
    }
  }
}
