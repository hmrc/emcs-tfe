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

package uk.gov.hmrc.emcstfe.controllers.userAnswers

import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.ControllerComponents
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.emcstfe.controllers.actions.{AuthAction, FakeAuthAction}
import uk.gov.hmrc.emcstfe.mocks.services.MockUserAnswersService
import uk.gov.hmrc.emcstfe.models.mongo.UserAnswers
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.MongoError
import uk.gov.hmrc.emcstfe.services.userAnswers.BaseUserAnswersService
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import java.time.Instant
import java.time.temporal.ChronoUnit
import scala.concurrent.{ExecutionContext, Future}

class BaseUserAnswersControllerSpec extends TestBaseSpec
  with MockUserAnswersService
  with FakeAuthAction {

  val route: String = "/test/route"
  val _ec = ec

  val controller: BaseUserAnswersController = new BaseUserAnswersController {
    override val controllerComponents: ControllerComponents = Helpers.stubControllerComponents()
    override val userAnswersService: BaseUserAnswersService = mockService
    override val auth: AuthAction = FakeSuccessAuthAction
    override implicit val ec: ExecutionContext = _ec
  }

  private val fakeRequest = FakeRequest("GET", route)

  val userAnswers = UserAnswers(testErn, testArc, Json.obj(), Instant.now().truncatedTo(ChronoUnit.MILLIS))

  s"GET $route" should {
    s"return $OK (OK)" when {
      "service returns a Right(Some(answers))" in {

        MockUserAnswers.get(testErn, testArc).returns(Future.successful(Right(Some(userAnswers))))

        val result = controller.get(testErn, testArc)(fakeRequest)

        status(result) shouldBe Status.OK
        contentAsJson(result) shouldBe Json.toJson(userAnswers)
      }
    }

    s"return $NO_CONTENT (NO_CONTENT)" when {
      "service returns a Right(None)" in {

        MockUserAnswers.get(testErn, testArc).returns(Future.successful(Right(None)))

        val result = controller.get(testErn, testArc)(fakeRequest)

        status(result) shouldBe Status.NO_CONTENT
        contentAsString(result) shouldBe ""
      }
    }
    s"return $INTERNAL_SERVER_ERROR (ISE)" when {
      "service returns a Left" in {

        MockUserAnswers.get(testErn, testArc).returns(Future.successful(Left(MongoError("errMsg"))))

        val result = controller.get(testErn, testArc)(fakeRequest)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.toJson(MongoError("errMsg"))
      }
    }
  }

  s"PUT $route" should {
    s"return $OK (OK)" when {
      "service stores the new model returns a Right(answers)" in {

        MockUserAnswers.set(userAnswers).returns(Future.successful(Right(userAnswers)))

        val result = controller.set(testErn, testArc)(fakeRequest.withBody(Json.toJson(userAnswers)))

        status(result) shouldBe Status.OK
        contentAsJson(result) shouldBe Json.toJson(userAnswers)
      }
    }
    s"return $BAD_REQUEST (BAD_REQUEST)" when {
      "Received JSON cannot be parsed to AlertRejectionUserAnswers" in {

        val result = controller.set(testErn, testArc)(fakeRequest.withBody(Json.obj()))

        status(result) shouldBe Status.BAD_REQUEST
        contentAsString(result) should include("Invalid UserAnswers payload")
      }
    }
    s"return $INTERNAL_SERVER_ERROR (ISE)" when {
      "service returns a Left" in {

        MockUserAnswers.set(userAnswers).returns(Future.successful(Left(MongoError("errMsg"))))

        val result = controller.set(testErn, testArc)(fakeRequest.withBody(Json.toJson(userAnswers)))

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.toJson(MongoError("errMsg"))
      }
    }
  }

  s"DELETE $route" should {
    s"return $NO_CONTENT (NO_CONTENT)" when {
      "service deletes the answers successfully" in {

        MockUserAnswers.clear(testErn, testArc).returns(Future.successful(Right(true)))

        val result = controller.clear(testErn, testArc)(fakeRequest)

        status(result) shouldBe Status.NO_CONTENT
        contentAsString(result) shouldBe ""
      }
    }
    s"return $INTERNAL_SERVER_ERROR (ISE)" when {
      "service returns a Left" in {

        MockUserAnswers.clear(testErn, testArc).returns(Future.successful(Left(MongoError("errMsg"))))

        val result = controller.clear(testErn, testArc)(fakeRequest)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.toJson(MongoError("errMsg"))
      }
    }
  }
}
