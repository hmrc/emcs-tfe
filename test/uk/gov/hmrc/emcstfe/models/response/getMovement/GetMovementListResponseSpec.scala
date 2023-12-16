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

package uk.gov.hmrc.emcstfe.models.response.getMovement

import play.api.libs.json.{JsResult, JsSuccess, Json}
import uk.gov.hmrc.emcstfe.fixtures.GetMovementListFixture
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

class GetMovementListResponseSpec extends TestBaseSpec with GetMovementListFixture {

  "reads" should {

    "successfully read a list of movements" when {

      "all fields are valid (and message is base64 encoded)" in {
        GetMovementListResponse.reads.reads(getMovementListJsonResponse) shouldBe JsSuccess(getMovementListResponse)
      }
    }

    "return an error" when {
      "the ERN is not defined" in {
        GetMovementListResponse.reads.reads(getMovementListJsonResponse - "exciseRegistrationNumber").isError shouldBe true
      }

      "the date time is not defined" in {
        GetMovementListResponse.reads.reads(getMovementListJsonResponse - "dateTime").isError shouldBe true
      }

      "the message is not base64 encoded" in {
        val response = Json.obj(
          "exciseRegistrationNumber" -> testErn,
          "dateTime" -> "2023-09-07T12:39:20.354Z",
          "message" -> getMovementListXMLResponseBody
        )
        intercept[JsResult.Exception](GetMovementListResponse.reads.reads(response))
      }

      "the message is base 64 encoded but the decoded value can't be parsed to XML" in {
        val response = Json.obj(
          "exciseRegistrationNumber" -> testErn,
          "dateTime" -> "2023-09-07T12:39:20.354Z",
          "message" -> "PHZhbHVlPm5ldmVyIGdvbm5hIGdpdmUgeW91IHVwPC92YWx1ZT4="
        )
        intercept[JsResult.Exception](GetMovementListResponse.reads.reads(response))
      }
    }
  }
}
