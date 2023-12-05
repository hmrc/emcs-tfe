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

import scala.xml.XML

class GetMovementListRequestSpec extends TestBaseSpec {

  val request = GetMovementListRequest(exciseRegistrationNumber = "My ERN", GetMovementListSearchOptions(), isEISFeatureEnabled = false)

  "requestBody" should {
    "generate the correct request XML" in {
      val xml = XML.loadString(request.requestBody)


      (xml \\ "Envelope" \\ "Header" \\ "VersionNo").text shouldBe "2.1"
      (xml \\ "Envelope" \\ "Body" \\ "Control" \\"MetaData" \\ "Source").text shouldBe "emcs_tfe"
      (xml \\ "Envelope" \\ "Body" \\ "Control" \\"MetaData" \\ "Identity").text shouldBe "portal"
      (xml \\ "Envelope" \\ "Body" \\ "Control" \\"MetaData" \\ "Partner").text shouldBe "UK"
      (xml \\ "Envelope" \\ "Body" \\ "Control" \\"OperationRequest" \\ "Parameters" \\ "Parameter").filter(el => (el \ "@Name").text == "ExciseRegistrationNumber").text shouldBe "My ERN"
      (xml \\ "Envelope" \\ "Body" \\ "Control" \\"OperationRequest" \\ "Parameters" \\ "Parameter").filter(el => (el \ "@Name").text == "TraderRole").text shouldBe GetMovementListSearchOptions.DEFAULT_TRADER_ROLE
      (xml \\ "Envelope" \\ "Body" \\ "Control" \\"OperationRequest" \\ "Parameters" \\ "Parameter").filter(el => (el \ "@Name").text == "SortField").text shouldBe GetMovementListSearchOptions.DEFAULT_SORT_FIELD
      (xml \\ "Envelope" \\ "Body" \\ "Control" \\"OperationRequest" \\ "Parameters" \\ "Parameter").filter(el => (el \ "@Name").text == "SortOrder").text shouldBe GetMovementListSearchOptions.DEFAULT_SORT_ORDER
      (xml \\ "Envelope" \\ "Body" \\ "Control" \\"OperationRequest" \\ "Parameters" \\ "Parameter").filter(el => (el \ "@Name").text == "StartPosition").text shouldBe GetMovementListSearchOptions.DEFAULT_START_POSITION.toString
      (xml \\ "Envelope" \\ "Body" \\ "Control" \\"OperationRequest" \\ "Parameters" \\ "Parameter").filter(el => (el \ "@Name").text == "MaxNoToReturn").text shouldBe GetMovementListSearchOptions.DEFAULT_MAX_ROWS.toString
    }
  }

  "action" should {
    "be correct" in {
      request.action shouldBe "http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/GetMovementList"
    }
  }

  "shouldExtractFromSoap" should {
    "be correct" in {
      request.shouldExtractFromSoap shouldBe true
    }
  }

  "queryParams" should {
    "return only the defined query params" in {
      val requestWithSomeDefinedQueryParams = request.copy(searchOptions = GetMovementListSearchOptions(
        dateOfDispatchFrom = Some("01/01/2023"),
        dateOfDispatchTo = Some("02/01/2023"),
        transporterTraderName = Some("Trader 1")
      ), isEISFeatureEnabled = false)
      requestWithSomeDefinedQueryParams.queryParams shouldBe Seq(
        "exciseregistrationnumber" -> requestWithSomeDefinedQueryParams.exciseRegistrationNumber,
        "traderrole" -> "Consignor and/or Consignee",
        "sortfield" -> "DateReceived",
        "sortorder" -> "D",
        "startposition" -> "1",
        "maxnotoreturn" -> "30",
        "dateofdispatchfrom" -> "01/01/2023",
        "dateofdispatchto" -> "02/01/2023",
        "transportertradername" -> "Trader 1"
      )
    }

    "return only the defined query params (when EIS feature switch is enabled)" in {
      val requestWithSomeDefinedQueryParams = request.copy(searchOptions = GetMovementListSearchOptions(
        dateOfDispatchFrom = Some("01/01/2023"),
        dateOfDispatchTo = Some("02/01/2023"),
        transporterTraderName = Some("Trader 1")
      ), isEISFeatureEnabled = true)
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
