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

import uk.gov.hmrc.emcstfe.fixtures.SubmitReportOfReceiptFixtures
import uk.gov.hmrc.emcstfe.support.UnitSpec

import scala.xml.Utility.trim
import scala.xml.XML

class SubmitReportOfReceiptRequestSpec extends UnitSpec with SubmitReportOfReceiptFixtures {

  val request = SubmitReportOfReceiptRequest(exciseRegistrationNumber = "ERN", maxSubmitReportOfReceiptModel)

  "requestBody" should {

    "generate the correct request XML" in {

      val expectedSoapRequest =
        <soapenv:Envelope xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope">
          <soapenv:Header>
            <ns:Info xmlns:ns="http://www.hmrc.gov.uk/ws/info-header/1">
              <ns:VendorName>EMCS_PORTAL_TFE</ns:VendorName>
              <ns:VendorID>1259</ns:VendorID>
              <ns:VendorProduct Version="2.0">HMRC Portal</ns:VendorProduct>
              <ns:ServiceID>1138</ns:ServiceID>
              <ns:ServiceMessageType>HMRC-EMCS-IE815-DIRECT</ns:ServiceMessageType>
            </ns:Info>
          </soapenv:Header>
          <soapenv:Body>
            <urn:IE818 xmlns:urn="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE818:V3.01" xmlns:urn1="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:TMS:V3.01">
              <urn:Header>
                <urn1:MessageSender>NDEA.GB</urn1:MessageSender>
                <urn1:MessageRecipient>NDEA.GB</urn1:MessageRecipient>
                <urn1:DateOfPreparation>
                  {request.preparedDate.toString}
                </urn1:DateOfPreparation>
                <urn1:TimeOfPreparation>
                  {request.preparedTime.toString}
                </urn1:TimeOfPreparation>
                <urn1:MessageIdentifier>
                  {request.messageUUID}
                </urn1:MessageIdentifier>
                <urn1:CorrelationIdentifier>
                  {request.correlationUUID}
                </urn1:CorrelationIdentifier>
              </urn:Header>
              <urn:Body>
                {maxSubmitReportOfReceiptModelXML}
              </urn:Body>
            </urn:IE818>
          </soapenv:Body>
        </soapenv:Envelope>

      trim(XML.loadString(request.requestBody)).toString shouldBe trim(expectedSoapRequest).toString
    }
  }

  "action" should {
    "be correct" in {
      request.action shouldBe "http://www.hmrc.gov.uk/emcs/submitreportofreceiptportal"
    }
  }

  "shouldExtractFromSoap" should {
    "be correct" in {
      request.shouldExtractFromSoap shouldBe false
    }
  }
}
