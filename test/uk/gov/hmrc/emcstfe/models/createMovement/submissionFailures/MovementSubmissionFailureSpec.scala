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

package uk.gov.hmrc.emcstfe.models.createMovement.submissionFailures

import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.emcstfe.fixtures.{BaseFixtures, MovementSubmissionFailureFixtures}
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

class MovementSubmissionFailureSpec extends TestBaseSpec with BaseFixtures with MovementSubmissionFailureFixtures {

  "MovementSubmissionFailure" should {

    "serialise to JSON as expected" in {
      Json.toJson(movementSubmissionFailureModel) shouldBe movementSubmissionFailureJson
    }

    "de-serialise from JSON as expected" in {
      movementSubmissionFailureJson.as[MovementSubmissionFailure] shouldBe movementSubmissionFailureModel
    }

    "default 'hasFixed' to false when not provided in a JSON payload" in {

      (movementSubmissionFailureJson.as[JsObject] - "hasFixed").as[MovementSubmissionFailure] shouldBe movementSubmissionFailureModel.copy(hasFixed = false)
    }
  }

}
