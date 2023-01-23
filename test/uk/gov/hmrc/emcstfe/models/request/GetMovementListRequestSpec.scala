/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.models.request

import uk.gov.hmrc.emcstfe.support.UnitSpec

import scala.xml.XML

class GetMovementListRequestSpec extends UnitSpec {

  val request = GetMovementListRequest(exciseRegistrationNumber = "My ERN", GetMovementListSearchOptions())

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
}
