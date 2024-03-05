

package uk.gov.hmrc.emcstfe.controllers.testOnly

import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.emcstfe.controllers.actions.FakeAuthAction
import uk.gov.hmrc.emcstfe.mocks.services.MockCreateMovementUserAnswersService
import uk.gov.hmrc.emcstfe.models.mongo.CreateMovementUserAnswers
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.MongoError
import uk.gov.hmrc.emcstfe.support.TestBaseSpec
import uk.gov.hmrc.emcstfe.testOnly.controllers.InsertCreateMovementUserAnswersController

import java.time.Instant
import java.time.temporal.ChronoUnit
import scala.concurrent.Future

class InsertCreateMovementUserAnswersControllerSpec extends TestBaseSpec with MockCreateMovementUserAnswersService with FakeAuthAction {

  private val fakeRequest = FakeRequest("GET", "/test-only/user-answers/create-movement/:ern/:draftId")
  private val controller = new InsertCreateMovementUserAnswersController(
    Helpers.stubControllerComponents(),
    mockService,
    FakeSuccessAuthAction
  )

  val userAnswers: CreateMovementUserAnswers =
    CreateMovementUserAnswers(ern = testErn, draftId = testDraftId, data = Json.obj(), submissionFailures = Seq.empty, lastUpdated = Instant.now().truncatedTo(ChronoUnit.MILLIS), hasBeenSubmitted = true, submittedDraftId = Some(testDraftId))

  "PUT /test-only/user-answers/create-movement/:ern/:draftId" should {
    s"return $OK (OK)" when {
      "service stores the new model returns a Right(answers)" in {

        MockCreateMovementUserAnswersService.set(userAnswers).returns(Future.successful(Right(userAnswers)))

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

        MockCreateMovementUserAnswersService.set(userAnswers).returns(Future.successful(Left(MongoError("errMsg"))))

        val result = controller.set(testErn, testDraftId)(fakeRequest.withBody(Json.toJson(userAnswers)))

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.toJson(MongoError("errMsg"))
      }
    }
  }
}
