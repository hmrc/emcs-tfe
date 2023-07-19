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

import play.api.test.FakeRequest
import uk.gov.hmrc.emcstfe.fixtures.{SubmitExplainShortageExcessFixtures, TraderModelFixtures}
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.common.{SubmitterType, TraderModel}
import uk.gov.hmrc.emcstfe.models.explainShortageExcess.AttributesModel
import uk.gov.hmrc.emcstfe.support.UnitSpec

import scala.xml.Utility.trim
import scala.xml.XML

class SubmitExplainShortageExcessRequestSpec extends UnitSpec with SubmitExplainShortageExcessFixtures with TraderModelFixtures {

  import SubmitExplainShortageExcessFixtures._

  implicit val userRequest = UserRequest(FakeRequest(), testErn, testInternalId, testCredId)
  val request = SubmitExplainShortageExcessRequest(submitExplainShortageExcessModelMax)

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
              <ns:ServiceMessageType>HMRC-EMCS-IE871-DIRECT</ns:ServiceMessageType>
            </ns:Info>
            <MetaData xmlns="http://www.hmrc.gov.uk/ChRIS/SOAP/MetaData/1">
              <CredentialID>{testCredId}</CredentialID>
              <Identifier>{testErn}</Identifier>
            </MetaData>
          </soapenv:Header>
          <soapenv:Body>
            <urn:IE871 xmlns:urn="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE871:V3.01" xmlns:urn1="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:TMS:V3.01">
              <urn:Header>
                <urn1:MessageSender>{request.messageSender}</urn1:MessageSender>
                <urn1:MessageRecipient>{request.messageRecipient}</urn1:MessageRecipient>
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
                {submitExplainShortageExcessXmlMax}
              </urn:Body>
            </urn:IE871>
          </soapenv:Body>
        </soapenv:Envelope>

      trim(XML.loadString(request.requestBody)).toString shouldBe trim(expectedSoapRequest).toString
    }

    "for the MessageSender and MessageRecipient headers" when {

      val idErn = "FR0000123456"
      val idArc = "01DE0000012345"
      val defaultConsigneeTraderModel = ConsigneeTraderFixtures.consigneeTraderModel.copy(traderId = Some(idErn))

      def model(submitterType: SubmitterType, consigneeTrader: Option[TraderModel] = Some(defaultConsigneeTraderModel)) =
        submitExplainShortageExcessModelMax
          .copy(attributes = AttributesModel(submitterType))
          .copy(
            exciseMovement = ExciseMovementFixtures.exciseMovementModel.copy(arc = idArc),
            consigneeTrader = consigneeTrader
          )

      "SubmitterType.Consignor" when {
        "generating MessageSender" should {
          "use the country code from the ARC" in {
            val request = SubmitExplainShortageExcessRequest(model(SubmitterType.Consignor))
            request.messageSender shouldBe "NDEA.DE"
          }
        }
        "generating MessageRecipient" should {
          "use the country code from the ConsigneeTrader Traderid" when {
            "ConsigneeTrader Traderid is defined" in {
              val request = SubmitExplainShortageExcessRequest(model(SubmitterType.Consignor))
              request.messageRecipient shouldBe "NDEA.FR"
            }
          }
          "use GB" when {
            "ConsigneeTrader is not defined" in {
              val request = SubmitExplainShortageExcessRequest(model(SubmitterType.Consignor, consigneeTrader = None))
              request.messageRecipient shouldBe "NDEA.GB"
            }
            "ConsigneeTrader Traderid is not defined" in {
              val request = SubmitExplainShortageExcessRequest(model(SubmitterType.Consignor, consigneeTrader = Some(defaultConsigneeTraderModel.copy(traderId = None))))
              request.messageRecipient shouldBe "NDEA.GB"
            }
          }
        }
      }

      "SubmitterType.Consignee" when {
        "generating MessageSender" should {
          "use the country code from the ConsigneeTrader Traderid" when {
            "ConsigneeTrader Traderid is defined" in {
              val request = SubmitExplainShortageExcessRequest(model(SubmitterType.Consignee))
              request.messageSender shouldBe "NDEA.FR"
            }
          }
          "use GB" when {
            "ConsigneeTrader is not defined" in {
              val request = SubmitExplainShortageExcessRequest(model(SubmitterType.Consignee, consigneeTrader = None))
              request.messageSender shouldBe "NDEA.GB"
            }
            "ConsigneeTrader Traderid is not defined" in {
              val request = SubmitExplainShortageExcessRequest(model(SubmitterType.Consignee, consigneeTrader = Some(defaultConsigneeTraderModel.copy(traderId = None))))
              request.messageSender shouldBe "NDEA.GB"
            }
          }
        }
        "generating MessageRecipient" should {
          "use the country code from the ARC" in {
            val request = SubmitExplainShortageExcessRequest(model(SubmitterType.Consignee))
            request.messageRecipient shouldBe "NDEA.DE"
          }
        }
      }
    }
  }

  "action" should {
    "be correct" in {
      request.action shouldBe "http://www.hmrc.gov.uk/emcs/submitreasonforshortageportal"
    }
  }

  "shouldExtractFromSoap" should {
    "be correct" in {
      request.shouldExtractFromSoap shouldBe false
    }
  }
}