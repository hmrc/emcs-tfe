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
import uk.gov.hmrc.emcstfe.fixtures.{GetSubmissionFailureMessageFixtures, MovementSubmissionFailureFixtures}
import uk.gov.hmrc.emcstfe.mocks.repository.MockCreateMovementUserAnswersRepository
import uk.gov.hmrc.emcstfe.mocks.services.MockGetSubmissionFailureMessageService
import uk.gov.hmrc.emcstfe.models.mongo.CreateMovementUserAnswers
import uk.gov.hmrc.emcstfe.models.request.GetSubmissionFailureMessageRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.UnexpectedDownstreamResponseError
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import java.time.Instant
import java.time.temporal.ChronoUnit
import scala.concurrent.Future

class GetSubmissionFailureMessageControllerSpec extends TestBaseSpec
  with MockGetSubmissionFailureMessageService
  with GetSubmissionFailureMessageFixtures
  with MockCreateMovementUserAnswersRepository
  with MovementSubmissionFailureFixtures
  with FakeAuthAction {

  import GetSubmissionFailureMessageResponseFixtures._

  private val fakeRequest = FakeRequest("GET", "/movement/:ern/:arc")
  private val controller = new GetSubmissionFailureMessageController(Helpers.stubControllerComponents(), mockService, mockCreateMovementUserAnswersRepository, FakeSuccessAuthAction)

  private val getSubmissionFailureMessageRequest = GetSubmissionFailureMessageRequest(testErn, testArc)


  "getSubmissionFailureMessage" should {
    "return 200" when {
      "service returns a Right (not calling repository when correlation ID starts with PORTAL - isTFESubmission = true)" in {

        val response = getSubmissionFailureMessageResponseModel.copy(
          ie704 = IE704ModelFixtures.ie704ModelModel.copy(
            header = IE704HeaderFixtures.ie704HeaderModel.copy(
              correlationIdentifier = Some("PORTAL123")
            )
          )
        )

        MockService.getSubmissionFailureMessage(getSubmissionFailureMessageRequest)
          .returns(Future.successful(Right(response)))

        val result = controller.getSubmissionFailureMessage(testErn, testArc)(fakeRequest)

        status(result) shouldBe Status.OK
        contentAsJson(result) shouldBe Json.obj(
          "ie704" -> response.ie704,
          "relatedMessageType" -> "IE815",
          "isTFESubmission" -> true
        )
      }

      "service returns a Right (calling repository when correlation ID does not start with PORTAL - isTFESubmission = true)" in {

        val userAnswers: CreateMovementUserAnswers =
          CreateMovementUserAnswers(testErn, testDraftId, data = Json.obj(), submissionFailures = Seq(movementSubmissionFailureModel), Instant.now().truncatedTo(ChronoUnit.MILLIS), hasBeenSubmitted = true, submittedDraftId = Some(testDraftId))

        MockService.getSubmissionFailureMessage(getSubmissionFailureMessageRequest).returns(Future.successful(Right(getSubmissionFailureMessageResponseModel)))

        MockCreateMovementUserAnswersRepository.get(testErn, IE704ModelFixtures.ie704ModelModel.header.correlationIdentifier.get)
          .returns(Future.successful(Some(userAnswers)))

        val result = controller.getSubmissionFailureMessage(testErn, testArc)(fakeRequest)

        status(result) shouldBe Status.OK
        contentAsJson(result) shouldBe getSubmissionFailureMessageResponseJson(isTFESubmission = true)
      }

      "service returns a Right (calling repository when correlation ID does not start with PORTAL - isTFESubmission = false)" in {

        MockService.getSubmissionFailureMessage(getSubmissionFailureMessageRequest).returns(Future.successful(Right(getSubmissionFailureMessageResponseModel)))

        MockCreateMovementUserAnswersRepository.get(testErn, IE704ModelFixtures.ie704ModelModel.header.correlationIdentifier.get)
          .returns(Future.successful(None))

        val result = controller.getSubmissionFailureMessage(testErn, testArc)(fakeRequest)

        status(result) shouldBe Status.OK
        contentAsJson(result) shouldBe getSubmissionFailureMessageResponseJson(isTFESubmission = false)
      }
    }
    "return 500" when {
      "service returns a Left" in {

        MockService.getSubmissionFailureMessage(getSubmissionFailureMessageRequest).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        val result = controller.getSubmissionFailureMessage(testErn, testArc)(fakeRequest)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.obj("message" -> UnexpectedDownstreamResponseError.message)
      }
    }
  }
}
