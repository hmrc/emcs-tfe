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

import uk.gov.hmrc.emcstfe.models.common.DestinationType.{Export, TaxWarehouse, TemporaryRegisteredConsignee}
import uk.gov.hmrc.emcstfe.models.common.{Ascending, Descending}
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import java.time.LocalDate

class GetDraftMovementSearchOptionsSpec extends TestBaseSpec {

  "GetDraftMovementSearchOptions" must {

    "construct with default options" in {

      GetDraftMovementSearchOptions() shouldBe GetDraftMovementSearchOptions(
        sortField = LastUpdatedDate,
        sortOrder = Descending,
        startPosition = 0,
        maxRows = 10,
        searchTerm = None,
        draftHasErrors = None,
        destinationTypes = None,
        dateOfDispatchFrom = None,
        dateOfDispatchTo = None,
        exciseProductCode = None
      )
    }

    "be able to be constructed by a QueryStringBinder" when {

      "no query params are supplied" in {
        GetDraftMovementSearchOptions.queryStringBinder.bind("search", Map()) shouldBe
          Some(Right(GetDraftMovementSearchOptions()))
      }

      "all query parameters are supplied" in {
        GetDraftMovementSearchOptions.queryStringBinder.bind("search", Map(
          "search.sortField" -> Seq("lrn"),
          "search.sortOrder" -> Seq("A"),
          "search.startPosition" -> Seq("10"),
          "search.maxRows" -> Seq("99"),
          "search.searchTerm" -> Seq("FooBar"),
          "search.draftHasErrors" -> Seq("true"),
          "search.destinationType" -> Seq("1", "3", "6"),
          "search.dateOfDispatchFrom" -> Seq("2020-07-06"),
          "search.dateOfDispatchTo" -> Seq("2020-07-07"),
          "search.exciseProductCode" -> Seq("6000")
        )) shouldBe
          Some(Right(GetDraftMovementSearchOptions(
            sortField = LRN,
            sortOrder = Ascending,
            startPosition = 10,
            maxRows = 99,
            searchTerm = Some("FooBar"),
            draftHasErrors = Some(true),
            destinationTypes = Some(Seq(TaxWarehouse, TemporaryRegisteredConsignee, Export)),
            dateOfDispatchFrom = Some(LocalDate.of(2020,7,6)),
            dateOfDispatchTo = Some(LocalDate.of(2020,7,7)),
            exciseProductCode = Some("6000")
          )))
      }

      "some query parameters are supplied" in {
        GetDraftMovementSearchOptions.queryStringBinder.bind("search", Map(
          "search.sortOrder" -> Seq("A"),
          "search.startPosition" -> Seq("10")
        )) shouldBe
          Some(Right(GetDraftMovementSearchOptions(
            sortOrder = Ascending,
            startPosition = 10,
          )))
      }
    }

    "unbind QueryString to URL format" in {
      GetDraftMovementSearchOptions.queryStringBinder.unbind("search", GetDraftMovementSearchOptions(
        sortField = LRN,
        sortOrder = Ascending,
        startPosition = 10,
        maxRows = 99,
        searchTerm = Some("FooBar"),
        draftHasErrors = Some(true),
        destinationTypes = Some(Seq(TaxWarehouse, TemporaryRegisteredConsignee, Export)),
        dateOfDispatchFrom = Some(LocalDate.of(2020, 7, 6)),
        dateOfDispatchTo = Some(LocalDate.of(2020, 7, 7)),
        exciseProductCode = Some("6000")
      )) shouldBe
          "search.sortField=lrn" +
          "&search.sortOrder=A" +
          "&search.startPosition=10" +
          "&search.maxRows=99" +
          "&search.searchTerm=FooBar" +
          "&search.draftHasErrors=true" +
          "&search.destinationType=1" +
          "&search.destinationType=3" +
          "&search.destinationType=6" +
          "&search.dateOfDispatchFrom=2020-07-06" +
          "&search.dateOfDispatchTo=2020-07-07" +
          "&search.exciseProductCode=6000"
    }
  }
}
