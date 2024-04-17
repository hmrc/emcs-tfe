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

package uk.gov.hmrc.emcstfe.models.common

import uk.gov.hmrc.emcstfe.support.TestBaseSpec

class SortOrderingSpec extends TestBaseSpec {

  "SortOrdering" should {

    "have the correct codes" in {
      Ascending.toString shouldBe "A"
      Descending.toString shouldBe "D"
    }

    "be able to be constructed by a QueryStringBinder" when {

      "no query param is supplied" in {
        SortOrdering.queryStringBinder.bind("sort", Map()) shouldBe None
      }

      "valid query param is supplied (A=Ascending)" in {
        SortOrdering.queryStringBinder.bind("sort", Map(
          "sort" -> Seq("A")
        )) shouldBe Some(Right(Ascending))
      }

      "valid query param is supplied (D=Descending)" in {
        SortOrdering.queryStringBinder.bind("sort", Map(
          "sort" -> Seq("D")
        )) shouldBe Some(Right(Descending))
      }

      "invalid query param is supplied should fail" in {
        SortOrdering.queryStringBinder.bind("sort", Map(
          "sort" -> Seq("X")
        )) shouldBe Some(Left("'X' is not a valid sort order. Must be 'A' or 'D'"))
      }
    }

    "unbind QueryString to URL format" in {
      SortOrdering.queryStringBinder.unbind("sort", Ascending) shouldBe "sort=A"
    }
  }
}
