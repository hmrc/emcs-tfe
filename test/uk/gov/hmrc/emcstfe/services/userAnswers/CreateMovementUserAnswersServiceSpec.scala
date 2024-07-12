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

package uk.gov.hmrc.emcstfe.services.userAnswers

import org.specs2.mock.Mockito.theStubbed
import play.api.libs.json.Json
import uk.gov.hmrc.emcstfe.fixtures.{GetMovementListFixture, MovementSubmissionFailureFixtures}
import uk.gov.hmrc.emcstfe.mocks.repository.MockCreateMovementUserAnswersRepository
import uk.gov.hmrc.emcstfe.mocks.utils.MockUUIDGenerator
import uk.gov.hmrc.emcstfe.models.createMovement.submissionFailures.MovementSubmissionFailure
import uk.gov.hmrc.emcstfe.models.mongo.CreateMovementUserAnswers
import uk.gov.hmrc.emcstfe.models.request.GetDraftMovementSearchOptions
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.MongoError
import uk.gov.hmrc.emcstfe.models.response.SearchDraftMovementsResponse
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import java.time.Instant
import scala.concurrent.Future

class CreateMovementUserAnswersServiceSpec extends TestBaseSpec with GetMovementListFixture with MovementSubmissionFailureFixtures with MockUUIDGenerator {
  trait Test extends MockCreateMovementUserAnswersRepository {
    val service: CreateMovementUserAnswersService = new CreateMovementUserAnswersService(mockCreateMovementUserAnswersRepository, mockUUIDGenerator)
  }

  val userAnswers: CreateMovementUserAnswers =
    CreateMovementUserAnswers(testErn, testDraftId, data = Json.obj(), submissionFailures = Seq(movementSubmissionFailureModel), validationErrors = Seq.empty, Instant.now(), hasBeenSubmitted = true, submittedDraftId = Some(testDraftId))

  ".get" should {
    "return a Right(Some(answers))" when {
      "UserAnswers are successfully returned from Mongo" in new Test {

        MockCreateMovementUserAnswersRepository.get(testErn, testDraftId).returns(Future.successful(Some(userAnswers)))
        await(service.get(testErn, testDraftId)) shouldBe Right(Some(userAnswers))
      }
    }

    "return a Right(None)" when {
      "UserAnswers are not found in Mongo" in new Test {

        MockCreateMovementUserAnswersRepository.get(testErn, testDraftId).returns(Future.successful(None))
        await(service.get(testErn, testDraftId)) shouldBe Right(None)
      }
    }
    "return a Left" when {
      "mongo error is returned" in new Test {

        MockCreateMovementUserAnswersRepository.get(testErn, testDraftId).returns(Future.failed(new Exception("bang")))
        await(service.get(testErn, testDraftId)) shouldBe Left(MongoError("bang"))
      }
    }
  }

  ".set" should {
    "return a Right(boolean)" when {
      "UserAnswers are successfully saved/updated in Mongo" in new Test {

        MockCreateMovementUserAnswersRepository.set(userAnswers).returns(Future.successful(true))
        await(service.set(userAnswers)) shouldBe Right(userAnswers)
      }
    }
    "return a Left" when {
      "mongo error is returned" in new Test {

        MockCreateMovementUserAnswersRepository.set(userAnswers).returns(Future.failed(new Exception("bang")))
        await(service.set(userAnswers)) shouldBe Left(MongoError("bang"))
      }
    }
  }

  ".clear" should {
    "return a Right(boolean)" when {
      "UserAnswers are successfully saved/updated in Mongo" in new Test {

        MockCreateMovementUserAnswersRepository.clear(testErn, testDraftId).returns(Future.successful(true))
        await(service.clear(testErn, testDraftId)) shouldBe Right(true)
      }
    }
    "return a Left" when {
      "mongo error is returned" in new Test {

        MockCreateMovementUserAnswersRepository.clear(testErn, testDraftId).returns(Future.failed(new Exception("bang")))
        await(service.clear(testErn, testDraftId)) shouldBe Left(MongoError("bang"))
      }
    }
  }

  ".checkForExistingLrn" should {
    "return a Right(true)" when {
      "there is already a draft with the ERN and LRN" in new Test {
        MockCreateMovementUserAnswersRepository.checkForExistingLrn(testErn, testLrn).returns(Future.successful(true))
        await(service.checkForExistingLrn(testErn, testLrn)) shouldBe Right(true)
      }
    }

    "return a Right(false)" when {
      "there isn't a draft with the ERN and LRN" in new Test {
        MockCreateMovementUserAnswersRepository.checkForExistingLrn(testErn, testLrn).returns(Future.successful(false))
        await(service.checkForExistingLrn(testErn, testLrn)) shouldBe Right(false)
      }
    }

    "return a Left" when {
      "mongo error is returned" in new Test {

        MockCreateMovementUserAnswersRepository.checkForExistingLrn(testErn, testLrn).returns(Future.failed(new Exception("bang")))
        await(service.checkForExistingLrn(testErn, testLrn)) shouldBe Left(MongoError("bang"))
      }
    }
  }

  ".markDraftAsUnsubmitted" should {

    "return a Right(true)" when {
      "there is already a draft with the ERN and draft ID" in new Test {
        MockCreateMovementUserAnswersRepository.markDraftAsUnsubmitted(testErn, testDraftId).returns(Future.successful(true))
        await(service.markDraftAsUnsubmitted(testErn, testDraftId)) shouldBe Right(true)
      }
    }

    "return a Right(false)" when {
      "there isn't a draft with the ERN and draft ID" in new Test {
        MockCreateMovementUserAnswersRepository.markDraftAsUnsubmitted(testErn, testDraftId).returns(Future.successful(false))
        await(service.markDraftAsUnsubmitted(testErn, testDraftId)) shouldBe Right(false)
      }
    }

    "return a Left" when {
      "mongo error is returned" in new Test {

        MockCreateMovementUserAnswersRepository.markDraftAsUnsubmitted(testErn, testDraftId).returns(Future.failed(new Exception("bang")))
        await(service.markDraftAsUnsubmitted(testErn, testDraftId)) shouldBe Left(MongoError("bang"))
      }
    }
  }

  ".setErrorMessagesForDraftMovement" should {

    val movementSubmissionFailures: Seq[MovementSubmissionFailure] = Seq(movementSubmissionFailureModel)

    "return a Right(Some(_))" when {
      "there is a draft with the ERN and LRN and it's updated successfully" in new Test {
        MockUUIDGenerator.randomUUID.returns(testNewDraftId)
        MockCreateMovementUserAnswersRepository.setErrorMessagesForDraftMovement(testErn, testLrn, testNewDraftId, movementSubmissionFailures).returns(Future.successful(Some(testNewDraftId)))
        await(service.setErrorMessagesForDraftMovement(testErn, testLrn, movementSubmissionFailures)) shouldBe Right(Some(testNewDraftId))
      }
    }

    "return a Right(None)" when {
      "there isn't a draft with the ERN and LRN" in new Test {
        MockUUIDGenerator.randomUUID.returns(testNewDraftId)
        MockCreateMovementUserAnswersRepository.setErrorMessagesForDraftMovement(testErn, testLrn, testNewDraftId, movementSubmissionFailures).returns(Future.successful(None))
        await(service.setErrorMessagesForDraftMovement(testErn, testLrn, movementSubmissionFailures)) shouldBe Right(None)
      }
    }

    "return a Left" when {
      "mongo error is returned" in new Test {
        MockUUIDGenerator.randomUUID.returns(testNewDraftId)
        MockCreateMovementUserAnswersRepository.setErrorMessagesForDraftMovement(testErn, testLrn, testNewDraftId, movementSubmissionFailures).returns(Future.failed(new Exception("bang")))
        await(service.setErrorMessagesForDraftMovement(testErn, testLrn, movementSubmissionFailures)) shouldBe Left(MongoError("bang"))
      }
    }
  }

  ".search" should {
    "return a Right(SearchDraftMovementsResponse)" when {
      "UserAnswers are successfully found matching the search in Mongo" in new Test {

        val searchOptions = GetDraftMovementSearchOptions()
        val response = SearchDraftMovementsResponse(1, Seq(userAnswers))

        MockCreateMovementUserAnswersRepository.searchDrafts(testErn, searchOptions)
          .returns(Future.successful(response))

        await(service.searchDrafts(testErn, searchOptions)) shouldBe Right(response)
      }
    }

    "return a Right(SearchDraftMovementsResponse)" when {
      "UserAnswers are not found matching the search in Mongo" in new Test {

        val searchOptions = GetDraftMovementSearchOptions()
        val response = SearchDraftMovementsResponse(0, Seq.empty)

        MockCreateMovementUserAnswersRepository.searchDrafts(testErn, searchOptions)
          .returns(Future.successful(response))

        await(service.searchDrafts(testErn, searchOptions)) shouldBe Right(response)
      }
    }

    "return a Left" when {
      "mongo error is returned" in new Test {

        val searchOptions = GetDraftMovementSearchOptions()

        MockCreateMovementUserAnswersRepository.searchDrafts(testErn, searchOptions)
          .returns(Future.failed(new Exception("bang")))
        await(service.searchDrafts(testErn, searchOptions)) shouldBe Left(MongoError("bang"))
      }
    }
  }
}
