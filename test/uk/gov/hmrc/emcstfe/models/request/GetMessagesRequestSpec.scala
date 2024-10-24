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

package uk.gov.hmrc.emcstfe.models.request

import uk.gov.hmrc.emcstfe.support.TestBaseSpec

class GetMessagesRequestSpec extends TestBaseSpec {

  "forming a request model" should {

    "succeed" when {
      GetMessagesRequest.validSortFields.foreach(
        sortField =>
          GetMessagesRequest.validSortOrders.foreach(
            sortOrder => {

              val request = GetMessagesRequest(testErn, sortField, sortOrder, 1)

              s"sortField is $sortField and sortOrder is $sortOrder" should {

                s"apply successfully without failing the require rules" in {
                  request shouldBe GetMessagesRequest(testErn, sortField, sortOrder, 1)
                }

                "generate the correct request queryParams" in {

                  request.queryParams shouldBe Seq(
                   "exciseregistrationnumber" -> testErn,
                   "sortfield" -> request.sortField,
                   "sortorder" -> request.sortOrder,
                   "startposition" -> "0",
                   "maxnotoreturn" -> "10"
                  )
                }
              }
            }
          )
      )
    }

    "fail" when {
      "sortField is invalid" in {
        val result = intercept[IllegalArgumentException](GetMessagesRequest(testErn, "beans", "A", 1))

        result.getMessage shouldBe s"requirement failed: sortField of beans is invalid. Valid sort fields: ${GetMessagesRequest.validSortFields}"
      }
      "sortOrder is invalid" in {
        val result = intercept[IllegalArgumentException](GetMessagesRequest(testErn, "arc", "beans", 1))

        result.getMessage shouldBe s"requirement failed: sortOrder of beans is invalid. Valid sort orders: ${GetMessagesRequest.validSortOrders}"
      }
      "page is < 0" in {
        val result = intercept[IllegalArgumentException](GetMessagesRequest(testErn, "arc", "A", 0))

        result.getMessage shouldBe "requirement failed: page cannot be less than 1"
      }
    }
  }
}
