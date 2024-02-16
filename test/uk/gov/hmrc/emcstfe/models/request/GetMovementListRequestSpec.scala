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

import scala.xml.{Elem, XML}

class GetMovementListRequestSpec extends TestBaseSpec {

  val request = GetMovementListRequest(exciseRegistrationNumber = "My ERN", GetMovementListSearchOptions(), isEISFeatureEnabled = false)

  private def paramFromXml(xml: Elem, paramName: String): String = {
    (xml \\ "Envelope" \\ "Body" \\ "Control" \\ "OperationRequest" \\ "Parameters" \\ "Parameter").filter(el => (el \ "@Name").text == paramName).text
  }

  "requestBody" should {
    "generate the correct request XML, if search options are defined" in {
      val requestWithParams =
        request.copy(
          searchOptions = GetMovementListSearchOptions().copy(
            arc = Some(testArc),
            otherTraderId = Some("GB123456789"),
            lrn = Some(testLrn),
            dateOfDispatchFrom = Some("06/07/2020"),
            dateOfDispatchTo = Some("07/07/2020"),
            dateOfReceiptFrom = Some("08/07/2020"),
            dateOfReceiptTo = Some("09/07/2020"),
            countryOfOrigin = Some("GB"),
            movementStatus = Some("e-AD Manually Closed"),
            transporterTraderName = Some("Trader 1"),
            undischargedMovements = Some("Accepted"),
            exciseProductCode = Some("6000")
          )
        )

      val xml = XML.loadString(requestWithParams.requestBody)

      (xml \\ "Envelope" \\ "Header" \\ "VersionNo").text shouldBe "2.1"
      (xml \\ "Envelope" \\ "Body" \\ "Control" \\ "MetaData" \\ "Source").text shouldBe "emcs_tfe"
      (xml \\ "Envelope" \\ "Body" \\ "Control" \\ "MetaData" \\ "Identity").text shouldBe "portal"
      (xml \\ "Envelope" \\ "Body" \\ "Control" \\ "MetaData" \\ "Partner").text shouldBe "UK"
      paramFromXml(xml, "ExciseRegistrationNumber") shouldBe "My ERN"
      paramFromXml(xml, "TraderRole") shouldBe GetMovementListSearchOptions.DEFAULT_TRADER_ROLE
      paramFromXml(xml, "SortField") shouldBe GetMovementListSearchOptions.DEFAULT_SORT_FIELD
      paramFromXml(xml, "SortOrder") shouldBe GetMovementListSearchOptions.DEFAULT_SORT_ORDER
      paramFromXml(xml, "StartPosition") shouldBe GetMovementListSearchOptions.DEFAULT_START_POSITION.toString
      paramFromXml(xml, "MaxNoToReturn") shouldBe GetMovementListSearchOptions.DEFAULT_MAX_ROWS.toString
      paramFromXml(xml, "ARC") shouldBe testArc
      paramFromXml(xml, "OtherTraderId") shouldBe "GB123456789"
      paramFromXml(xml, "LocalReferenceNumber") shouldBe testLrn
      paramFromXml(xml, "DateOfReceiptFrom") shouldBe "08/07/2020"
      paramFromXml(xml, "DateOfReceiptTo") shouldBe "09/07/2020"
      paramFromXml(xml, "DateOfDispatchFrom") shouldBe "06/07/2020"
      paramFromXml(xml, "DateOfDispatchTo") shouldBe "07/07/2020"
      paramFromXml(xml, "CountryOfOrigin") shouldBe "GB"
      paramFromXml(xml, "MovementStatus") shouldBe "e-AD Manually Closed"
      paramFromXml(xml, "TransporterTraderName") shouldBe "Trader 1"
      paramFromXml(xml, "UndischargedMovements") shouldBe "Accepted"
      paramFromXml(xml, "ExciseProductCode") shouldBe "6000"
    }

    "generate the correct request XML, if search options are not defined" in {

      val xml = XML.loadString(request.requestBody)

      (xml \\ "Envelope" \\ "Header" \\ "VersionNo").text shouldBe "2.1"
      (xml \\ "Envelope" \\ "Body" \\ "Control" \\ "MetaData" \\ "Source").text shouldBe "emcs_tfe"
      (xml \\ "Envelope" \\ "Body" \\ "Control" \\ "MetaData" \\ "Identity").text shouldBe "portal"
      (xml \\ "Envelope" \\ "Body" \\ "Control" \\ "MetaData" \\ "Partner").text shouldBe "UK"
      paramFromXml(xml, "ExciseRegistrationNumber") shouldBe "My ERN"
      paramFromXml(xml, "TraderRole") shouldBe GetMovementListSearchOptions.DEFAULT_TRADER_ROLE
      paramFromXml(xml, "SortField") shouldBe GetMovementListSearchOptions.DEFAULT_SORT_FIELD
      paramFromXml(xml, "SortOrder") shouldBe GetMovementListSearchOptions.DEFAULT_SORT_ORDER
      paramFromXml(xml, "StartPosition") shouldBe GetMovementListSearchOptions.DEFAULT_START_POSITION.toString
      paramFromXml(xml, "MaxNoToReturn") shouldBe GetMovementListSearchOptions.DEFAULT_MAX_ROWS.toString
      paramFromXml(xml, "ARC") shouldBe ""
      paramFromXml(xml, "OtherTraderId") shouldBe ""
      paramFromXml(xml, "LocalReferenceNumber") shouldBe ""
      paramFromXml(xml, "DateOfReceiptFrom") shouldBe ""
      paramFromXml(xml, "DateOfReceiptTo") shouldBe ""
      paramFromXml(xml, "DateOfDispatchFrom") shouldBe ""
      paramFromXml(xml, "DateOfDispatchTo") shouldBe ""
      paramFromXml(xml, "CountryOfOrigin") shouldBe ""
      paramFromXml(xml, "MovementStatus") shouldBe ""
      paramFromXml(xml, "TransporterTraderName") shouldBe ""
      paramFromXml(xml, "UndischargedMovements") shouldBe ""
      paramFromXml(xml, "ExciseProductCode") shouldBe ""
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
