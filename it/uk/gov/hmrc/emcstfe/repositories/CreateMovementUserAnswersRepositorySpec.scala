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

package uk.gov.hmrc.emcstfe.repositories

import org.mongodb.scala.Document
import org.mongodb.scala.model.Filters
import play.api.libs.json.Json
import uk.gov.hmrc.emcstfe.fixtures.MovementSubmissionFailureFixtures
import uk.gov.hmrc.emcstfe.models.mongo.CreateMovementUserAnswers
import uk.gov.hmrc.emcstfe.utils.TimeMachine

import java.time.Instant
import java.time.temporal.ChronoUnit

class CreateMovementUserAnswersRepositorySpec extends RepositoryBaseSpec[CreateMovementUserAnswers] with MovementSubmissionFailureFixtures {

  private val instantNow = Instant.now.truncatedTo(ChronoUnit.MILLIS)
  private val timeMachine: TimeMachine = () => instantNow

  private val userAnswers = CreateMovementUserAnswers(testErn, testDraftId, Json.obj("foo" -> "bar"), submissionFailures = Seq.empty, Instant.ofEpochSecond(1), hasBeenSubmitted = true, submittedDraftId = Some(testDraftId))

  protected override val repository = new CreateMovementUserAnswersRepositoryImpl(
    mongoComponent = mongoComponent,
    appConfig = mockAppConfig,
    time = timeMachine
  )

  override protected def afterEach(): Unit = {
    super.afterEach()
    repository.collection.deleteMany(Document()).toFuture().futureValue
  }

  ".set" must {

    "set the last updated time on the supplied user answers to `now`, and save them" in {

      val expectedResult = userAnswers copy (lastUpdated = instantNow)

      val setResult = repository.set(userAnswers).futureValue
      val updatedRecord = find(
        Filters.and(
          Filters.equal("ern", userAnswers.ern),
          Filters.equal("draftId", userAnswers.draftId)
        )
      ).futureValue.headOption.value

      setResult shouldBe true
      updatedRecord shouldBe expectedResult
    }
  }

  ".get" when {

    "there is a record for this draftId" must {

      "update the lastUpdated time and get the record" in {

        insert(userAnswers).futureValue

        val result = repository.get(userAnswers.ern, userAnswers.draftId).futureValue
        val expectedResult = userAnswers copy (lastUpdated = instantNow)

        result.value shouldBe expectedResult
      }
    }

    "there is a record for this submittedDraftId" must {

      "update the lastUpdated time and get the record" in {

        val submissionId = "submission123456"

        insert(userAnswers.copy(submittedDraftId = Some(submissionId))).futureValue

        val result = repository.get(userAnswers.ern, submissionId).futureValue
        val expectedResult = userAnswers.copy(lastUpdated = instantNow, submittedDraftId = Some(submissionId))

        result.value shouldBe expectedResult
      }
    }

    "there is no record for this draftId or submittedDraftId" must {

      "return None" in {

        repository.get(userAnswers.ern, "wrongLrn").futureValue shouldBe None
      }
    }
  }

  ".clear" must {

    "remove a record" in {

      insert(userAnswers).futureValue

      val result = repository.clear(userAnswers.ern, userAnswers.draftId).futureValue

      result shouldBe true
      repository.get(userAnswers.ern, userAnswers.draftId).futureValue shouldBe None
    }

    "return true when there is no record to remove" in {
      val result = repository.clear(userAnswers.ern, userAnswers.draftId).futureValue

      result shouldBe true
    }
  }

  ".keepAlive" when {

    "there is a record for this id" must {

      "update its lastUpdated to `now` and return true" in {

        insert(userAnswers).futureValue

        val result = repository.keepAlive(userAnswers.ern, userAnswers.draftId).futureValue

        val expectedUpdatedAnswers = userAnswers copy (lastUpdated = instantNow)

        result shouldBe true
        val updatedAnswers = find(
          Filters.and(
            Filters.equal("ern", userAnswers.ern),
            Filters.equal("draftId", userAnswers.draftId)
          )
        ).futureValue.headOption.value
        updatedAnswers shouldBe expectedUpdatedAnswers
      }
    }

    "there is no record for this id" must {

      "return true" in {

        repository.keepAlive(userAnswers.ern, "wrongLrn").futureValue shouldBe true
      }
    }
  }

  ".checkForExistingLrn" must {

    "there is a record with this ern and lrn" in {
      val lrnEntry = userAnswers.copy(data = Json.obj("info" -> Json.obj("localReferenceNumber" -> "LRN1234")))
      insert(lrnEntry).futureValue
      repository.checkForExistingLrn(userAnswers.ern, "LRN1234").futureValue shouldBe true
    }

    "there is no record with this ern and lrn" in {
      val lrnEntry = userAnswers.copy(data = Json.obj("info" -> Json.obj("localReferenceNumber" -> "LRN1234")))
      insert(lrnEntry).futureValue
      repository.checkForExistingLrn(userAnswers.ern, "ABC1234").futureValue shouldBe false
    }

  }

  "markDraftAsUnsubmitted" must {

    "return true" when {

      "the update has completed successfully" in {

        insert(userAnswers.copy(hasBeenSubmitted = true)).futureValue

        val result = repository.markDraftAsUnsubmitted(userAnswers.ern, userAnswers.draftId).futureValue

        result shouldBe true
        val updatedAnswers = find(
          Filters.and(
            Filters.equal("ern", userAnswers.ern),
            Filters.equal("draftId", userAnswers.draftId)
          )
        ).futureValue.headOption.value
        updatedAnswers.lastUpdated.isAfter(userAnswers.lastUpdated) shouldBe true
        updatedAnswers.hasBeenSubmitted shouldBe false
      }
    }

    "return false" when {

      "there are no records in Mongo" in {

        val result = repository.markDraftAsUnsubmitted(userAnswers.ern, userAnswers.draftId).futureValue

        result shouldBe false
      }

      "the draft ID specified cannot be found" in {

        insert(userAnswers.copy(hasBeenSubmitted = true)).futureValue

        val result = repository.markDraftAsUnsubmitted(userAnswers.ern, "blah").futureValue

        result shouldBe false
      }

      "the ERN specified cannot be found" in {

        insert(userAnswers.copy(hasBeenSubmitted = true)).futureValue

        val result = repository.markDraftAsUnsubmitted("blah", userAnswers.draftId).futureValue

        result shouldBe false
      }
    }
  }

  ".setErrorMessagesForDraftMovement" must {

    "return Some(_)" when {

      "there is a record with this ern and lrn" in {
        val mongoEntry = userAnswers.copy(submittedDraftId = Some(testDraftId))
        insert(mongoEntry).futureValue
        repository.setErrorMessagesForDraftMovement(userAnswers.ern, testDraftId, Seq(movementSubmissionFailureModel)).futureValue shouldBe Some(testDraftId)
      }
    }


    "return None" when {

      "there is no record with this ern and lrn" in {
        val mongoEntry = userAnswers.copy(submittedDraftId = Some(testDraftId))
        insert(mongoEntry).futureValue
        repository.setErrorMessagesForDraftMovement(userAnswers.ern, "ABC1234", Seq(movementSubmissionFailureModel)).futureValue shouldBe None
      }
    }
  }

  ".setSubmittedDraftId" must {

    "update the record when the there is one" in {
      insert(userAnswers).futureValue
      repository.setSubmittedDraftId(userAnswers.ern, userAnswers.draftId, s"PORTAL$testDraftId").futureValue shouldBe true
      repository.get(userAnswers.ern, userAnswers.draftId).futureValue.get.submittedDraftId.get shouldBe s"PORTAL$testDraftId"
    }


    "not update any records when the search criteria doesn't match any" in {

      insert(userAnswers).futureValue
      repository.setErrorMessagesForDraftMovement(userAnswers.ern, "ABC1234", Seq(movementSubmissionFailureModel)).futureValue shouldBe None
    }
  }
}
