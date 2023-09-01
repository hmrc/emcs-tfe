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

class GetMovementListSearchOptionsSpec extends TestBaseSpec {

  val request = GetMovementListRequest(exciseRegistrationNumber = "My ERN", GetMovementListSearchOptions())

  "GetMovementListSearchOptions" must {

    "construct with default options" in {

      GetMovementListSearchOptions() shouldBe GetMovementListSearchOptions(
        traderRole = "Consignor and/or Consignee",
        sortField = "DateReceived",
        sortOrder = "D",
        startPosition = 1,
        maxRows = 30
      )
    }

    "be able to be constructed by a QueryStringBinder" when {

      "no query params are supplied" in {
        GetMovementListSearchOptions.queryStringBinder.bind("search", Map()) shouldBe
          Some(Right(GetMovementListSearchOptions()))
      }

      "all query parameters are supplied" in {
        GetMovementListSearchOptions.queryStringBinder.bind("search", Map(
          "search.traderRole" -> Seq("foo"),
          "search.sortField" -> Seq("bar"),
          "search.sortOrder" -> Seq("wizz"),
          "search.startPosition" -> Seq("10"),
          "search.maxRows" -> Seq("99")
        )) shouldBe
          Some(Right(GetMovementListSearchOptions(
            traderRole = "foo",
            sortField = "bar",
            sortOrder = "wizz",
            startPosition = 10,
            maxRows = 99
          )))
      }

      "some query parameters are supplied" in {
        GetMovementListSearchOptions.queryStringBinder.bind("search", Map(
          "search.traderRole" -> Seq("foo"),
          "search.startPosition" -> Seq("10"),
        )) shouldBe
          Some(Right(GetMovementListSearchOptions(
            traderRole = "foo",
            sortField = GetMovementListSearchOptions.DEFAULT_SORT_FIELD,
            sortOrder = GetMovementListSearchOptions.DEFAULT_SORT_ORDER,
            startPosition = 10,
            maxRows = GetMovementListSearchOptions.DEFAULT_MAX_ROWS
          )))
      }
    }

    "unbind QueryString to URL format" in {
      GetMovementListSearchOptions.queryStringBinder.unbind("search", GetMovementListSearchOptions()) shouldBe
        "search.traderRole=Consignor+and%2For+Consignee" +
          "&search.sortField=DateReceived" +
          "&search.sortOrder=D" +
          "&search.startPosition=1" +
          "&search.maxRows=30"
    }
  }
}
