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

  val request = GetMovementListRequest(exciseRegistrationNumber = "My ERN", GetMovementListSearchOptions(), isEISFeatureEnabled = false)

  "GetMovementListSearchOptions" must {

    "construct with default options" in {

      GetMovementListSearchOptions() shouldBe GetMovementListSearchOptions(
        traderRole = None,
        sortField = None,
        sortOrder = "D",
        startPosition = None,
        maxRows = 30,
        arc = None,
        otherTraderId = None,
        lrn = None,
        dateOfDispatchFrom = None,
        dateOfDispatchTo = None,
        dateOfReceiptFrom = None,
        dateOfReceiptTo = None,
        countryOfOrigin = None,
        movementStatus = None,
        transporterTraderName = None,
        undischargedMovements = None,
        exciseProductCode = None
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
          "search.maxRows" -> Seq("99"),
          "search.arc" -> Seq("1234"),
          "search.otherTraderId" -> Seq("GB123456789"),
          "search.lrn" -> Seq("LRN1234"),
          "search.dateOfDispatchFrom" -> Seq("06/07/2020"),
          "search.dateOfDispatchTo" -> Seq("07/07/2020"),
          "search.dateOfReceiptFrom" -> Seq("08/07/2020"),
          "search.dateOfReceiptTo" -> Seq("09/07/2020"),
          "search.countryOfOrigin" -> Seq("GB"),
          "search.movementStatus" -> Seq("e-AD Manually Closed"),
          "search.transporterTraderName" -> Seq("Trader 1"),
          "search.undischargedMovements" -> Seq("Accepted"),
          "search.exciseProductCode" -> Seq("6000")
        )) shouldBe
          Some(Right(GetMovementListSearchOptions(
            traderRole = Some("foo"),
            sortField = Some("bar"),
            sortOrder = "wizz",
            startPosition = Some(10),
            maxRows = 99,
            arc = Some("1234"),
            otherTraderId = Some("GB123456789"),
            lrn = Some("LRN1234"),
            dateOfDispatchFrom = Some("06/07/2020"),
            dateOfDispatchTo = Some("07/07/2020"),
            dateOfReceiptFrom = Some("08/07/2020"),
            dateOfReceiptTo = Some("09/07/2020"),
            countryOfOrigin = Some("GB"),
            movementStatus = Some("e-AD Manually Closed"),
            transporterTraderName = Some("Trader 1"),
            undischargedMovements = Some("Accepted"),
            exciseProductCode = Some("6000")
          )))
      }

      "some query parameters are supplied" in {
        GetMovementListSearchOptions.queryStringBinder.bind("search", Map(
          "search.traderRole" -> Seq("foo"),
          "search.startPosition" -> Seq("10"),
        )) shouldBe
          Some(Right(GetMovementListSearchOptions(
            traderRole = Some("foo"),
            startPosition = Some(10)
          )))
      }
    }

    "unbind QueryString to URL format" in {
      GetMovementListSearchOptions.queryStringBinder.unbind("search", GetMovementListSearchOptions(
        countryOfOrigin = Some("FR")
      )) shouldBe
          "search.sortOrder=D" +
          "&search.maxRows=30" +
          "&search.countryOfOrigin=FR"
    }
  }
}
