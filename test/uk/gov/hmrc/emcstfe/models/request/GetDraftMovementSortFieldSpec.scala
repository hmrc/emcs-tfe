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

package uk.gov.hmrc.emcstfe.models.request

import uk.gov.hmrc.emcstfe.support.TestBaseSpec

class GetDraftMovementSortFieldSpec extends TestBaseSpec {

  "GetDraftMovementSortField" should {

    "have the correct codes" in {
      LRN.toString shouldBe "lrn"
      LastUpdatedDate.toString shouldBe "lastUpdated"
    }

    "be able to be constructed by a QueryStringBinder" when {

      "no query param is supplied" in {
        GetDraftMovementSortField.queryStringBinder.bind("sortField", Map()) shouldBe None
      }

      "valid query param is supplied (lrn=LRN)" in {
        GetDraftMovementSortField.queryStringBinder.bind("sortField", Map(
          "sortField" -> Seq("lrn")
        )) shouldBe Some(Right(LRN))
      }

      "valid query param is supplied (lastUpdated=LastUpdatedDate)" in {
        GetDraftMovementSortField.queryStringBinder.bind("sortField", Map(
          "sortField" -> Seq("lastUpdated")
        )) shouldBe Some(Right(LastUpdatedDate))
      }

      "invalid query param is supplied should fail" in {
        GetDraftMovementSortField.queryStringBinder.bind("sortField", Map(
          "sortField" -> Seq("X")
        )) shouldBe Some(Left("'X' is not a valid sort field. Must be 'lrn' or 'lastUpdated'"))
      }
    }

    "unbind QueryString to URL format" in {
      GetDraftMovementSortField.queryStringBinder.unbind("sortField", LastUpdatedDate) shouldBe "sortField=lastUpdated"
    }
  }
}
