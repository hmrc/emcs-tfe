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

import scala.xml.XML

class MarkMessageAsReadRequestSpec extends TestBaseSpec {

  val request = MarkMessageAsReadRequest(testErn, "message1")

  "requestBody" should {
    "generate the correct request XML" in {
      val xml = XML.loadString(request.requestBody)


      (xml \\ "Envelope" \\ "Header" \\ "VersionNo").text shouldBe "2.1"
      (xml \\ "Envelope" \\ "Body" \\ "Control" \\ "MetaData" \\ "Source").text shouldBe "emcs_tfe"
      (xml \\ "Envelope" \\ "Body" \\ "Control" \\ "MetaData" \\ "Identity").text shouldBe "portal"
      (xml \\ "Envelope" \\ "Body" \\ "Control" \\ "MetaData" \\ "Partner").text shouldBe "UK"
      (xml \\ "Envelope" \\ "Body" \\ "Control" \\ "OperationRequest" \\ "Parameters" \\ "Parameter").filter(el => (el \ "@Name").text == "ExciseRegistrationNumber").text shouldBe testErn
      (xml \\ "Envelope" \\ "Body" \\ "Control" \\ "OperationRequest" \\ "Parameters" \\ "Parameter").filter(el => (el \ "@Name").text == "UniqueMessageId").text shouldBe "message1"
    }
  }

  "action" should {
    "be correct" in {
      request.action shouldBe "http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/MarkMessagesAsRead"
    }
  }

  "shouldExtractFromSoap" should {
    "be correct" in {
      request.shouldExtractFromSoap shouldBe true
    }
  }
}
