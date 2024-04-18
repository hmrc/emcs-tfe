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

package uk.gov.hmrc.emcstfe.models.response

import play.api.libs.json.Json
import uk.gov.hmrc.emcstfe.fixtures.GetMovementListFixture
import uk.gov.hmrc.emcstfe.models.mongo.CreateMovementUserAnswers
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import java.time.Instant

class SearchDraftMovementsResponseSpec extends TestBaseSpec with GetMovementListFixture {

  val lastUpdated = Instant.ofEpochMilli(1)

  val model = SearchDraftMovementsResponse(
    count = 1,
    paginatedDrafts = Seq(CreateMovementUserAnswers(
      ern = testErn,
      draftId = testDraftId,
      data = Json.obj(),
      submissionFailures = Seq(),
      lastUpdated = lastUpdated,
      hasBeenSubmitted = true,
      submittedDraftId = Some("submission1234")
    ))
  )

  ".reads" should {
    "deserialise to model (taking into account the MongoJavatimeReads for the Instant)" in {
      Json.obj(
        "count" -> 1,
        "paginatedDrafts" -> Json.arr(
          Json.obj(
            "ern" -> testErn,
            "draftId" -> testDraftId,
            "data" -> Json.obj(),
            "submissionFailures" -> Json.arr(),
            "lastUpdated" -> Json.obj(
              "$date" -> Json.obj(
                "$numberLong" -> "1"
              )
            ),
            "hasBeenSubmitted" -> true,
            "submittedDraftId" -> "submission1234"
          )
        )
      ).as[SearchDraftMovementsResponse] shouldBe model

      Json.toJson(getMovementList) shouldBe getMovementListJson
    }
  }

  ".writes" should {
    "serialise model (writing out a standard Instant format)" in {

      Json.toJson(model) shouldBe Json.obj(
        "count" -> 1,
        "paginatedDrafts" -> Json.arr(
          Json.obj(
            "ern" -> testErn,
            "draftId" -> testDraftId,
            "data" -> Json.obj(),
            "submissionFailures" -> Json.arr(),
            "lastUpdated" -> "1970-01-01T00:00:00.001Z",
            "hasBeenSubmitted" -> true,
            "submittedDraftId" -> "submission1234"
          )
        )
      )
    }
  }
}
