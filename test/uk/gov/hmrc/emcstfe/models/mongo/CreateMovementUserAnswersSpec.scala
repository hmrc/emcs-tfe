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

package uk.gov.hmrc.emcstfe.models.mongo

import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.emcstfe.fixtures.{BaseFixtures, MovementSubmissionFailureFixtures}
import uk.gov.hmrc.emcstfe.support.TestBaseSpec
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.Instant
import java.time.temporal.ChronoUnit

class CreateMovementUserAnswersSpec extends TestBaseSpec with BaseFixtures with MovementSubmissionFailureFixtures {

  val instant: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

  val userAnswersModel: CreateMovementUserAnswers = CreateMovementUserAnswers(
    ern = testErn,
    draftId = testDraftId,
    data = Json.obj(
      "foo" -> "bar"
    ),
    submissionFailures = Seq(movementSubmissionFailureModel),
    lastUpdated = instant,
    hasBeenSubmitted = true,
    submittedDraftId = Some(testDraftId)
  )

  val userAnswersJson: JsObject = Json.obj(
    "ern" -> testErn,
    "draftId" -> testDraftId,
    "data" -> Json.obj(
      "foo" -> "bar"
    ),
    "submissionFailures" -> Json.arr(movementSubmissionFailureJson),
    "lastUpdated" -> Json.toJson(instant)(MongoJavatimeFormats.instantWrites),
    "hasBeenSubmitted" -> true,
    "submittedDraftId" -> testDraftId
  )

  "CreateMovementUserAnswers" should {

    "serialise to JSON as expected" in {
      Json.toJson(userAnswersModel) shouldBe userAnswersJson
    }

    "de-serialise from JSON as expected" in {
      userAnswersJson.as[CreateMovementUserAnswers] shouldBe userAnswersModel
    }
  }
}
