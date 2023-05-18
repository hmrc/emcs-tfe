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
import uk.gov.hmrc.emcstfe.controllers.actions.{FakeAuthAction, FakeUserAllowListAction}
import uk.gov.hmrc.emcstfe.fixtures.GetMovementListFixture
import uk.gov.hmrc.emcstfe.mocks.services.MockGetMovementListService
import uk.gov.hmrc.emcstfe.models.request.{GetMovementListRequest, GetMovementListSearchOptions}
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.UnexpectedDownstreamResponseError
import uk.gov.hmrc.emcstfe.support.UnitSpec

import scala.concurrent.Future

class GetMovementListControllerSpec extends UnitSpec with MockGetMovementListService with GetMovementListFixture with FakeAuthAction {

  private val fakeRequest = FakeRequest("GET", "/movement/:ern/:arc")
  private val controller = new GetMovementListController(Helpers.stubControllerComponents(), mockService, FakeSuccessAuthAction, FakeUserAllowListAction)

  private val searchOptions = GetMovementListSearchOptions()
  private val getMovementListRequest = GetMovementListRequest(testErn, searchOptions)

  "GET /movements/:ern" should {
    "return 200" when {
      "service returns a Right" in {

        MockService.getMovementList(getMovementListRequest).returns(Future.successful(Right(getMovementListResponse)))

        val result = controller.getMovementList(testErn, searchOptions)(fakeRequest)

        status(result) shouldBe Status.OK
        contentAsJson(result) shouldBe getMovementListJson
      }
    }
    "return 500" when {
      "service returns a Left" in {

        MockService.getMovementList(getMovementListRequest).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        val result = controller.getMovementList(testErn, searchOptions)(fakeRequest)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.obj("message" -> UnexpectedDownstreamResponseError.message)
      }
    }
  }
}
