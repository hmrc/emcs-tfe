/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.models.request

import uk.gov.hmrc.emcstfe.support.UnitSpec

import scala.xml.XML

class GetMovementRequestSpec extends UnitSpec {
  val request = GetMovementRequest("My ERN", "My ARC")

  "requestBody" should {
    "generate the correct request XML" in {
      val xml = XML.loadString(request.requestBody)


      (xml \\ "Envelope" \\ "Header" \\ "VersionNo").text shouldBe "2.1"
      (xml \\ "Envelope" \\ "Body" \\ "Control" \\"MetaData" \\ "Source").text shouldBe "emcs_tfe"
      (xml \\ "Envelope" \\ "Body" \\ "Control" \\"MetaData" \\ "Identity").text shouldBe "portal"
      (xml \\ "Envelope" \\ "Body" \\ "Control" \\"MetaData" \\ "Partner").text shouldBe "UK"
      (xml \\ "Envelope" \\ "Body" \\ "Control" \\"OperationRequest" \\ "Parameters" \\ "Parameter").map(_.text) shouldBe Seq("My ERN", "My ARC")
    }
  }

  "action" should {
    "be correct" in {
      request.action shouldBe "http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/GetMovement"
    }
  }
}
