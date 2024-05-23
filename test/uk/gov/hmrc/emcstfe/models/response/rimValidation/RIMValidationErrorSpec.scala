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

package uk.gov.hmrc.emcstfe.models.response.rimValidation

import play.api.libs.json.Json
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

class RIMValidationErrorSpec extends TestBaseSpec {

  val model: RIMValidationError = RIMValidationError(errorCategory = Some("business"), errorType = Some(1), errorReason = Some("some reason"), errorLocation = Some("original value"))

  ".jsonWrites" should {

    "return the error reason" when {

      Seq(12, 13).foreach { errorType =>
        s"the error type is: $errorType" in {

          Json.toJson(model.copy(errorType = Some(errorType))) shouldBe Json.obj(
            "errorCategory" -> model.errorCategory,
            "errorType" -> errorType,
            "errorReason" -> model.errorReason,
            "errorLocation" -> model.errorLocation
          )
        }
      }
    }

    "set the error reason to None" when {
      "the error type is not 12 or 13" in {

        Json.toJson(model) shouldBe Json.obj(
          "errorCategory" -> model.errorCategory,
          "errorType" -> model.errorType,
          "errorLocation" -> model.errorLocation
        )
      }
    }
  }
}
