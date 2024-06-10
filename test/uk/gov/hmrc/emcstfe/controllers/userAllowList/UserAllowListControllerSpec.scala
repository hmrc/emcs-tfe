/*
 * Copyright 2024 HM Revenue & Customs
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

package uk.gov.hmrc.emcstfe.controllers.userAllowList

import play.api.test.{FakeRequest, Helpers}
import play.api.test.Helpers._
import uk.gov.hmrc.emcstfe.controllers.actions.FakeAuthAction
import uk.gov.hmrc.emcstfe.mocks.services.MockUserAllowListService
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.UnexpectedDownstreamResponseError
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.concurrent.Future

class UserAllowListControllerSpec extends TestBaseSpec with FakeAuthAction {

  trait Test extends MockUserAllowListService {
    val controller = new UserAllowListController(Helpers.stubControllerComponents(), mockUserAllowListService, FakeSuccessAuthAction)
  }

  "GET /beta/eligibility/:ern/:serviceName" should {

    "return OK" when {

      "the ERN is in a beta phase" in new Test {

        MockedUserAllowListService.isEligible(testErn, "service").returns(Future.successful(Right(true)))
        val result = controller.checkEligibility(testErn, "service")(FakeRequest())
        status(result) shouldBe OK
      }
    }

    "return NO_CONTENT" when {

      "the ERN is NOT in a beta phase" in new Test {

        MockedUserAllowListService.isEligible(testErn, "service").returns(Future.successful(Right(false)))
        val result = controller.checkEligibility(testErn, "service")(FakeRequest())
        status(result) shouldBe NO_CONTENT
      }
    }

    "return ISE" when {

      "a downstream call failed" in new Test {

        MockedUserAllowListService.isEligible(testErn, "service").returns(Future.successful(Left(UnexpectedDownstreamResponseError)))
        val result = controller.checkEligibility(testErn, "service")(FakeRequest())
        status(result) shouldBe INTERNAL_SERVER_ERROR
      }
    }
  }
}
