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

class GetMessagesRequestSpec extends TestBaseSpec {

  "forming a request model" should {

    "succeed" when {
      GetMessagesRequest.validSortFields.foreach(
        sortField =>
          GetMessagesRequest.validSortOrders.foreach(
            sortOrder => {

              val request = GetMessagesRequest(testErn, sortField, sortOrder, 1)

              s"sortField is $sortField and sortOrder is $sortOrder" should {

                s"apply successfully without failing the require rules" in {
                  request shouldBe GetMessagesRequest(testErn, sortField, sortOrder, 1)
                }

                "generate the correct request XML" in {

                  val xml = XML.loadString(request.requestBody)

                  (xml \\ "Envelope" \\ "Header" \\ "VersionNo").text shouldBe "2.1"
                  (xml \\ "Envelope" \\ "Body" \\ "Control" \\ "MetaData" \\ "Source").text shouldBe "emcs_tfe"
                  (xml \\ "Envelope" \\ "Body" \\ "Control" \\ "MetaData" \\ "Identity").text shouldBe "portal"
                  (xml \\ "Envelope" \\ "Body" \\ "Control" \\ "MetaData" \\ "Partner").text shouldBe "UK"
                  (xml \\ "Envelope" \\ "Body" \\ "Control" \\ "OperationRequest" \\ "Parameters" \\ "Parameter").filter(el => (el \ "@Name").text == "ExciseRegistrationNumber").text shouldBe testErn
                  (xml \\ "Envelope" \\ "Body" \\ "Control" \\ "OperationRequest" \\ "Parameters" \\ "Parameter").filter(el => (el \ "@Name").text == "SortField").text shouldBe GetMessagesRequest.toChRISSortField(request.sortField)
                  (xml \\ "Envelope" \\ "Body" \\ "Control" \\ "OperationRequest" \\ "Parameters" \\ "Parameter").filter(el => (el \ "@Name").text == "SortOrder").text shouldBe request.sortOrder
                  (xml \\ "Envelope" \\ "Body" \\ "Control" \\ "OperationRequest" \\ "Parameters" \\ "Parameter").filter(el => (el \ "@Name").text == "StartPosition").text shouldBe "0"
                  (xml \\ "Envelope" \\ "Body" \\ "Control" \\ "OperationRequest" \\ "Parameters" \\ "Parameter").filter(el => (el \ "@Name").text == "MaxNoToReturn").text shouldBe "10"
                }
              }
            }
          )
      )
    }

    "fail" when {
      "sortField is invalid" in {
        val result = intercept[IllegalArgumentException](GetMessagesRequest(testErn, "beans", "A", 1))

        result.getMessage shouldBe s"requirement failed: sortField of beans is invalid. Valid sort fields: ${GetMessagesRequest.validSortFields}"
      }
      "sortOrder is invalid" in {
        val result = intercept[IllegalArgumentException](GetMessagesRequest(testErn, "arc", "beans", 1))

        result.getMessage shouldBe s"requirement failed: sortOrder of beans is invalid. Valid sort orders: ${GetMessagesRequest.validSortOrders}"
      }
      "page is < 0" in {
        val result = intercept[IllegalArgumentException](GetMessagesRequest(testErn, "arc", "A", 0))

        result.getMessage shouldBe "requirement failed: page cannot be less than 1"
      }
    }
  }

  ".toChRISSortField" should {
    "must return the correct ChRIS sort field names" in {
      GetMessagesRequest.toChRISSortField("messagetype") shouldBe "MessageType"
      GetMessagesRequest.toChRISSortField("datereceived") shouldBe "DateReceived"
      GetMessagesRequest.toChRISSortField("arc") shouldBe "ARC"
      GetMessagesRequest.toChRISSortField("readindicator") shouldBe "ReadIndicator"
    }
  }

  ".shouldExtract" should {
    "from SOAP must be true" in {
      GetMessagesRequest(testErn, "messagetype", "A", 1).shouldExtractFromSoap shouldBe true
    }
  }

  ".action" should {
    "be http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/GetMessages" in {
      GetMessagesRequest(testErn, "messagetype", "A", 1).action shouldBe "http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/GetMessages"
    }
  }
}
