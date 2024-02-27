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
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.emcstfe.controllers.actions.FakeAuthAction
import uk.gov.hmrc.emcstfe.mocks.services.MockCreateMovementUserAnswersService
import uk.gov.hmrc.emcstfe.models.mongo.CreateMovementUserAnswers
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.MongoError
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import java.time.Instant
import java.time.temporal.ChronoUnit
import scala.concurrent.Future

class CreateMovementUserAnswersControllerSpec extends TestBaseSpec with MockCreateMovementUserAnswersService with FakeAuthAction {

  private val fakeRequest = FakeRequest("GET", "/user-answers/create-movement/:ern/:lrn")
  private val controller = new CreateMovementUserAnswersController(
    Helpers.stubControllerComponents(),
    mockService,
    FakeSuccessAuthAction
  )

  val userAnswers: CreateMovementUserAnswers =
    CreateMovementUserAnswers(testErn, testDraftId, Json.obj(), Instant.now().truncatedTo(ChronoUnit.MILLIS), hasBeenSubmitted = true)

  "GET /user-answers/create-movement/:ern/:lrn" should {
    s"return $OK (OK)" when {
      "service returns a Right(Some(answers))" in {

        MockUserAnswers.get(testErn, testDraftId).returns(Future.successful(Right(Some(userAnswers))))

        val result = controller.get(testErn, testDraftId)(fakeRequest)

        status(result) shouldBe Status.OK
        contentAsJson(result) shouldBe Json.toJson(userAnswers)
      }
    }

    s"return $NO_CONTENT (NO_CONTENT)" when {
      "service returns a Right(None)" in {

        MockUserAnswers.get(testErn, testDraftId).returns(Future.successful(Right(None)))

        val result = controller.get(testErn, testDraftId)(fakeRequest)

        status(result) shouldBe Status.NO_CONTENT
        contentAsString(result) shouldBe ""
      }
    }
    s"return $INTERNAL_SERVER_ERROR (ISE)" when {
      "service returns a Left" in {

        MockUserAnswers.get(testErn, testDraftId).returns(Future.successful(Left(MongoError("errMsg"))))

        val result = controller.get(testErn, testDraftId)(fakeRequest)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.toJson(MongoError("errMsg"))
      }
    }
  }

  "GET /user-answers/create-movement/trader/:ern/lrn/:lrn" should {
    s"return $OK (OK)" when {

      "service returns a Right(true)" in {
        MockUserAnswers.checkForExistingLrn(testErn, testLrn).returns(Future.successful(Right(true)))

        val result = controller.checkForExistingLrn(testErn, testLrn)(fakeRequest)

        status(result) shouldBe Status.OK
        contentAsJson(result) shouldBe Json.toJson(true)
      }

      "service returns a Right(false)" in {
        MockUserAnswers.checkForExistingLrn(testErn, testLrn).returns(Future.successful(Right(false)))

        val result = controller.checkForExistingLrn(testErn, testLrn)(fakeRequest)

        status(result) shouldBe Status.OK
        contentAsJson(result) shouldBe Json.toJson(false)
      }
    }

    s"return $INTERNAL_SERVER_ERROR (ISE)" when {
      "service returns a Left" in {

        MockUserAnswers.checkForExistingLrn(testErn, testLrn).returns(Future.successful(Left(MongoError("errMsg"))))

        val result = controller.checkForExistingLrn(testErn, testLrn)(fakeRequest)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.toJson(MongoError("errMsg"))
      }
    }
  }

  "GET /user-answers/create-movement/draft/:ern/:messageId" should {
    s"return $OK (OK)" when {

      "service returns a Right(None)" in {
        MockUserAnswers.get(testErn, testDraftId).returns(Future.successful(Right(None)))

        val result = controller.checkForExistingDraft(testErn, testDraftId)(fakeRequest)

        status(result) shouldBe Status.OK
        contentAsJson(result) shouldBe Json.obj("draftExists" -> false)
      }

      "service returns a Right(Some(UserAnswers))" in {
        MockUserAnswers.get(testErn, testDraftId).returns(Future.successful(Right(Some(userAnswers))))

        val result = controller.checkForExistingDraft(testErn, testDraftId)(fakeRequest)

        status(result) shouldBe Status.OK
        contentAsJson(result) shouldBe Json.obj("draftExists" -> true)
      }
    }

    s"return $INTERNAL_SERVER_ERROR (ISE)" when {
      "service returns a Left" in {

        MockUserAnswers.get(testErn, testDraftId).returns(Future.successful(Left(MongoError("errMsg"))))

        val result = controller.checkForExistingDraft(testErn, testDraftId)(fakeRequest)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.toJson(MongoError("errMsg"))
      }
    }
  }

  "PUT /user-answers/report-receipt/:ern/:lrn" should {
    s"return $OK (OK)" when {
      "service stores the new model returns a Right(answers)" in {

        MockUserAnswers.set(userAnswers).returns(Future.successful(Right(userAnswers)))

        val result = controller.set(testErn, testDraftId)(fakeRequest.withBody(Json.toJson(userAnswers)))

        status(result) shouldBe Status.OK
        contentAsJson(result) shouldBe Json.toJson(userAnswers)
      }
    }
    s"return $BAD_REQUEST (BAD_REQUEST)" when {
      "Received JSON cannot be parsed to CreateMovementUserAnswers" in {

        val result = controller.set(testErn, testDraftId)(fakeRequest.withBody(Json.obj()))

        status(result) shouldBe Status.BAD_REQUEST
        contentAsString(result) should include("Invalid CreateMovementUserAnswers payload")
      }
    }
    s"return $INTERNAL_SERVER_ERROR (ISE)" when {
      "service returns a Left" in {

        MockUserAnswers.set(userAnswers).returns(Future.successful(Left(MongoError("errMsg"))))

        val result = controller.set(testErn, testDraftId)(fakeRequest.withBody(Json.toJson(userAnswers)))

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.toJson(MongoError("errMsg"))
      }
    }
  }

  "DELETE /user-answers/report-receipt/:ern/:lrn" should {
    s"return $NO_CONTENT (NO_CONTENT)" when {
      "service deletes the answers successfully" in {

        MockUserAnswers.clear(testErn, testDraftId).returns(Future.successful(Right(true)))

        val result = controller.clear(testErn, testDraftId)(fakeRequest)

        status(result) shouldBe Status.NO_CONTENT
        contentAsString(result) shouldBe ""
      }
    }
    s"return $INTERNAL_SERVER_ERROR (ISE)" when {
      "service returns a Left" in {

        MockUserAnswers.clear(testErn, testDraftId).returns(Future.successful(Left(MongoError("errMsg"))))

        val result = controller.clear(testErn, testDraftId)(fakeRequest)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.toJson(MongoError("errMsg"))
      }
    }
  }
}
