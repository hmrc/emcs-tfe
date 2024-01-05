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

import play.api.libs.json.Json
import uk.gov.hmrc.emcstfe.fixtures.SubmitReportOfReceiptFixtures
import uk.gov.hmrc.emcstfe.models.common.ConsigneeTrader
import uk.gov.hmrc.emcstfe.models.common.DestinationType.{DirectDelivery, Export, RegisteredConsignee, TaxWarehouse, TemporaryRegisteredConsignee}
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import java.util.Base64
import scala.xml.Utility.trim
import scala.xml.XML

class SubmitReportOfReceiptRequestSpec extends TestBaseSpec with SubmitReportOfReceiptFixtures {

  implicit val request: SubmitReportOfReceiptRequest = SubmitReportOfReceiptRequest(maxSubmitReportOfReceiptModel, useFS41SchemaVersion = false)

  "requestBody" when {

    "useFS41SchemaVersion is enabled" should {

      implicit val request = SubmitReportOfReceiptRequest(maxSubmitReportOfReceiptModel, useFS41SchemaVersion = true)

      "generate the correct request XML" in {

        val expectedSoapRequest =
          <soapenv:Envelope xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope">
            <soapenv:Header>
              <ns:Info xmlns:ns="http://www.hmrc.gov.uk/ws/info-header/1">
                <ns:VendorName>EMCS_PORTAL_TFE</ns:VendorName>
                <ns:VendorID>1259</ns:VendorID>
                <ns:VendorProduct Version="2.0">HMRC Portal</ns:VendorProduct>
                <ns:ServiceID>1138</ns:ServiceID>
                <ns:ServiceMessageType>HMRC-EMCS-IE818-DIRECT</ns:ServiceMessageType>
              </ns:Info>
              <MetaData xmlns="http://www.hmrc.gov.uk/ChRIS/SOAP/MetaData/1">
                <CredentialID>
                  {testCredId}
                </CredentialID>
                <Identifier>
                  {testErn}
                </Identifier>
              </MetaData>
            </soapenv:Header>
            <soapenv:Body>
              <urn:IE818 xmlns:urn1="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:TMS:V3.13" xmlns:urn="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE818:V3.13">
                <urn:Header>
                  <urn1:MessageSender>
                    {request.messageSender}
                  </urn1:MessageSender>
                  <urn1:MessageRecipient>
                    {request.messageRecipient}
                  </urn1:MessageRecipient>
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
                    {request.legacyCorrelationUUID}
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

    "useFS41SchemaVersion is disabled" should {

      "generate the correct request XML" in {

        val expectedSoapRequest =
          <soapenv:Envelope xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope">
            <soapenv:Header>
              <ns:Info xmlns:ns="http://www.hmrc.gov.uk/ws/info-header/1">
                <ns:VendorName>EMCS_PORTAL_TFE</ns:VendorName>
                <ns:VendorID>1259</ns:VendorID>
                <ns:VendorProduct Version="2.0">HMRC Portal</ns:VendorProduct>
                <ns:ServiceID>1138</ns:ServiceID>
                <ns:ServiceMessageType>HMRC-EMCS-IE818-DIRECT</ns:ServiceMessageType>
              </ns:Info>
              <MetaData xmlns="http://www.hmrc.gov.uk/ChRIS/SOAP/MetaData/1">
                <CredentialID>
                  {testCredId}
                </CredentialID>
                <Identifier>
                  {testErn}
                </Identifier>
              </MetaData>
            </soapenv:Header>
            <soapenv:Body>
              <urn:IE818 xmlns:urn1="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:TMS:V3.01" xmlns:urn="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE818:V3.01">
                <urn:Header>
                  <urn1:MessageSender>
                    {request.messageSender}
                  </urn1:MessageSender>
                  <urn1:MessageRecipient>
                    {request.messageRecipient}
                  </urn1:MessageRecipient>
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
                    {request.legacyCorrelationUUID}
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

    "for the MessageSender and MessageRecipient headers" when {

      val model =
        maxSubmitReportOfReceiptModel
          .copy(arc = "01DE0000012345")
          .copy(consigneeTrader = Some(maxTraderModel(ConsigneeTrader).copy(traderExciseNumber = Some("FR0000123456"))))
          .copy(deliveryPlaceTrader = Some(maxTraderModel(ConsigneeTrader).copy(traderExciseNumber = Some("IT0000123456"))))

      "have the correct MessageRecipient" when {

        "destination type is DirectDelivery" should {

          "use the Consignee Trader ID for the Country Code" in {

            val request = SubmitReportOfReceiptRequest(model.copy(destinationType = Some(DirectDelivery)), useFS41SchemaVersion = true)
            request.messageRecipient shouldBe "NDEA.FR"
          }

          "use GB as default if the Consignee Trader ID does not exist" in {

            val request = SubmitReportOfReceiptRequest(model.copy(destinationType = Some(DirectDelivery), consigneeTrader = None), useFS41SchemaVersion = true)
            request.messageRecipient shouldBe "NDEA.GB"
          }
        }

        "destination type is anything other than DirectDelivery" should {

          "use the ARC for the Country Code" in {

            val request = SubmitReportOfReceiptRequest(model.copy(destinationType = Some(TaxWarehouse)), useFS41SchemaVersion = true)
            request.messageRecipient shouldBe "NDEA.DE"
          }
        }
      }

      "have the correct MessageSender" when {

        "destination type is TaxWarehouse" should {

          "use the Delivery Place Trader ID for the Country Code" in {

            val request = SubmitReportOfReceiptRequest(model.copy(destinationType = Some(TaxWarehouse)), useFS41SchemaVersion = true)
            request.messageSender shouldBe "NDEA.IT"
          }

          "use GB as default when Delivery Place Trader ID does NOT exist" in {

            val request = SubmitReportOfReceiptRequest(model.copy(destinationType = Some(TaxWarehouse), deliveryPlaceTrader = None), useFS41SchemaVersion = true)
            request.messageSender shouldBe "NDEA.GB"
          }
        }

        "destination type is TemporaryRegisteredConsignee" should {

          "use the Consignee Trader Id for the Country Code when it exists" in {

            val request = SubmitReportOfReceiptRequest(model.copy(destinationType = Some(TemporaryRegisteredConsignee)), useFS41SchemaVersion = true)
            request.messageSender shouldBe "NDEA.FR"
          }

          "use GB as default when consignee trader ID does NOT exist" in {

            val request = SubmitReportOfReceiptRequest(model.copy(destinationType = Some(TemporaryRegisteredConsignee), consigneeTrader = None), useFS41SchemaVersion = true)
            request.messageSender shouldBe "NDEA.GB"
          }
        }

        "destination type is RegisteredConsignee" should {

          "use the Consignee Trader Id for the Country Code when it exists" in {

            val request = SubmitReportOfReceiptRequest(model.copy(destinationType = Some(RegisteredConsignee)), useFS41SchemaVersion = true)
            request.messageSender shouldBe "NDEA.FR"
          }

          "use GB as default when consignee trader ID does NOT exist" in {

            val request = SubmitReportOfReceiptRequest(model.copy(destinationType = Some(RegisteredConsignee), consigneeTrader = None), useFS41SchemaVersion = true)
            request.messageSender shouldBe "NDEA.GB"
          }
        }

        "destination type is DirectDelivery" should {

          "use the ARC for the Country Code" in {

            val request = SubmitReportOfReceiptRequest(model.copy(destinationType = Some(DirectDelivery)), useFS41SchemaVersion = true)
            request.messageSender shouldBe "NDEA.DE"
          }
        }

        "destination type is anything else" should {

          "use GB" in {

            val request = SubmitReportOfReceiptRequest(model.copy(destinationType = Some(Export)), useFS41SchemaVersion = true)
            request.messageSender shouldBe "NDEA.GB"
          }
        }
      }
    }
  }

  ".action" should {
    "be correct" in {
      request.action shouldBe "http://www.hmrc.gov.uk/emcs/submitreportofreceiptportal"
    }
  }

  ".shouldExtractFromSoap" should {
    "be correct" in {
      request.shouldExtractFromSoap shouldBe false
    }
  }

  ".exciseRegistrationNumber" should {
    "be correct" in {
      request.exciseRegistrationNumber shouldBe testErn
    }
  }

  ".eisXMLBody" when {

    "useFS41SchemaVersion is enabled" should {

      "generate the correct XML body" in {

        implicit val request = SubmitReportOfReceiptRequest(maxSubmitReportOfReceiptModel, useFS41SchemaVersion = true)

        val expectedRequest = {
          wrapInControlDoc(
            <urn:IE818 xmlns:urn1="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:TMS:V3.13" xmlns:urn="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE818:V3.13">
              <urn:Header>
                <urn1:MessageSender>
                  {request.messageSender}
                </urn1:MessageSender>
                <urn1:MessageRecipient>
                  {request.messageRecipient}
                </urn1:MessageRecipient>
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
            </urn:IE818>)
        }


        val requestXml = XML.loadString(request.eisXMLBody())
        val expectedXml = trim(expectedRequest)

        requestXml.getControlDocWithoutMessage.toString() shouldEqual expectedXml.getControlDocWithoutMessage.toString()
        requestXml.getMessageBody.toString() shouldEqual expectedXml.getMessageBody.toString()
      }
    }

    "useFS41SchemaVersion is disabled" should {

      "generate the correct XML body" in {

        val expectedRequest = {
          wrapInControlDoc(
            <urn:IE818 xmlns:urn1="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:TMS:V3.01" xmlns:urn="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE818:V3.01">
              <urn:Header>
                <urn1:MessageSender>
                  {request.messageSender}
                </urn1:MessageSender>
                <urn1:MessageRecipient>
                  {request.messageRecipient}
                </urn1:MessageRecipient>
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
            </urn:IE818>)
        }


        val requestXml = XML.loadString(request.eisXMLBody())
        val expectedXml = trim(expectedRequest)

        requestXml.getControlDocWithoutMessage.toString() shouldEqual expectedXml.getControlDocWithoutMessage.toString()
        requestXml.getMessageBody.toString() shouldEqual expectedXml.getMessageBody.toString()
      }
    }
  }

  ".toJson" should {
    "create the correct JSON body" in {
      val expectedResult = Json.obj(
        "user" -> testErn,
        "messageType" -> "IE818",
        "message" -> Base64.getEncoder.encodeToString(XML.loadString(request.eisXMLBody()).toString().getBytes)
      )
      request.toJson shouldBe expectedResult
    }
  }
}
