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

class GetMovementListRequestSpec extends TestBaseSpec {

  val request = GetMovementListRequest(exciseRegistrationNumber = "My ERN", GetMovementListSearchOptions())

  "queryParams" should {

    "return only the defined query params" in {
      val requestWithSomeDefinedQueryParams = request.copy(searchOptions = GetMovementListSearchOptions(
        dateOfDispatchFrom = Some("01/01/2023"),
        dateOfDispatchTo = Some("02/01/2023"),
        transporterTraderName = Some("Trader 1")
      ))
      requestWithSomeDefinedQueryParams.queryParams shouldBe Seq(
        "exciseregistrationnumber" -> requestWithSomeDefinedQueryParams.exciseRegistrationNumber,
        "traderrole" -> "both",
        "sortfield" -> "dateofdispatch",
        "sortorder" -> "D",
        "startposition" -> "0",
        "maxnotoreturn" -> "30",
        "dateofdispatchfrom" -> "01/01/2023",
        "dateofdispatchto" -> "02/01/2023",
        "transportertradername" -> "Trader 1"
      )
    }

    "return all query params if they are all defined" in {
      val requestWithSomeDefinedQueryParams = request.copy(searchOptions = GetMovementListSearchOptions(
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
      ))
      requestWithSomeDefinedQueryParams.queryParams shouldBe Seq(
        "exciseregistrationnumber" -> requestWithSomeDefinedQueryParams.exciseRegistrationNumber,
        "traderrole" -> "foo",
        "sortfield" -> "bar",
        "sortorder" -> "wizz",
        "startposition" -> "10",
        "maxnotoreturn" -> "99",
        "arc" -> "1234",
        "othertraderid" -> "GB123456789",
        "localreferencenumber" -> "LRN1234",
        "dateofdispatchfrom" -> "06/07/2020",
        "dateofdispatchto" -> "07/07/2020",
        "dateofreceiptfrom" -> "08/07/2020",
        "dateofreceiptto" -> "09/07/2020",
        "countryoforigin" -> "GB",
        "movementstatus" -> "e-AD Manually Closed",
        "transportertradername" -> "Trader 1",
        "undischargedmovements" -> "Accepted",
        "exciseproductcode" -> "6000"
      )
    }
  }
}
