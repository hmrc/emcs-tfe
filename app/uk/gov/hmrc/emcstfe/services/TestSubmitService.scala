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

package uk.gov.hmrc.emcstfe.services

import com.google.inject.{Inject, Singleton}
import uk.gov.hmrc.emcstfe.connectors.ChrisConnector
import uk.gov.hmrc.emcstfe.models.request.ChrisRequest
import uk.gov.hmrc.emcstfe.models.response.{ErrorResponse, GetMovementResponse}
import uk.gov.hmrc.emcstfe.utils.Logging
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TestSubmitService @Inject()(connector: ChrisConnector) extends Logging {
  case class TestRequest(exciseRegistrationNumber: String, mark: String) extends ChrisRequest {
    override def requestBody: String =
      s"""<?xml version='1.0' encoding='UTF-8'?>
         |<soapenv:Envelope xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope">
         |  <soapenv:Header>
         |    <ns:Info xmlns:ns="http://www.hmrc.gov.uk/ws/info-header/1">
         |      <ns:VendorName>EMCS_PORTAL_TFE</ns:VendorName>
         |      <ns:VendorID>1259</ns:VendorID>
         |      <ns:VendorProduct Version="2.0">HMRC Portal</ns:VendorProduct>
         |      <ns:ServiceID>1138</ns:ServiceID>
         |      <ns:ServiceMessageType>HMRC-EMCS-IE815-DIRECT</ns:ServiceMessageType>
         |    </ns:Info>
         |    <Security
         |      xmlns="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">
         |      <BinarySecurityToken ValueType="http://www.hmrc.gov.uk#MarkToken">$mark</BinarySecurityToken>
         |    </Security>
         |    <MetaData xmlns="http://www.hmrc.gov.uk/ChRIS/SOAP/MetaData/1">
         |      <CredentialID>0000001284781216</CredentialID>
         |      <Identifier>$exciseRegistrationNumber</Identifier>
         |    </MetaData>
         |  </soapenv:Header>
         |  <soapenv:Body>
         |    <urn:IE815 xmlns:urn="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE815:V3.01"
         |      xmlns:urn1="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:TMS:V3.01">
         |      <urn:Header>
         |        <urn1:MessageSender>NDEA.GB</urn1:MessageSender>
         |        <urn1:MessageRecipient>NDEA.GB</urn1:MessageRecipient>
         |        <urn1:DateOfPreparation>2023-01-27</urn1:DateOfPreparation>
         |        <urn1:TimeOfPreparation>15:48:03</urn1:TimeOfPreparation>
         |        <urn1:MessageIdentifier>2d1c38722e7b404084b80df1367484e3</urn1:MessageIdentifier>
         |        <urn1:CorrelationIdentifier>PORTAL2d1c38722e7b404084b80df1367484e3</urn1:CorrelationIdentifier>
         |      </urn:Header>
         |      <urn:Body>
         |        <urn:SubmittedDraftOfEADESAD>
         |          <urn:Attributes>
         |            <urn:SubmissionMessageType>1</urn:SubmissionMessageType>
         |          </urn:Attributes>
         |          <urn:ConsigneeTrader language="en">
         |            <urn:Traderid>GBWKI53VE5SVK</urn:Traderid>
         |            <urn:TraderName>0zsUjoy3</urn:TraderName>
         |            <urn:StreetName>JQ7OcSCtBK</urn:StreetName>
         |            <urn:StreetNumber>04</urn:StreetNumber>
         |            <urn:Postcode>A1 1AA</urn:Postcode>
         |            <urn:City>8alIApQA</urn:City>
         |          </urn:ConsigneeTrader>
         |          <urn:ConsignorTrader language="en">
         |            <urn:TraderExciseNumber>GBWK001234569</urn:TraderExciseNumber>
         |            <urn:TraderName>Acceptance Test Great Britian Warehouse Keeper LTD</urn:TraderName>
         |            <urn:StreetName>tmZPLzNHru</urn:StreetName>
         |            <urn:StreetNumber>60</urn:StreetNumber>
         |            <urn:Postcode>A1 1AA</urn:Postcode>
         |            <urn:City>tUOqgAQT</urn:City>
         |          </urn:ConsignorTrader>
         |          <urn:PlaceOfDispatchTrader language="en">
         |            <urn:ReferenceOfTaxWarehouse>GB00NURWK3ZEN</urn:ReferenceOfTaxWarehouse>
         |            <urn:TraderName>T1clXsou</urn:TraderName>
         |            <urn:StreetName>FxIDfXn0BD</urn:StreetName>
         |            <urn:StreetNumber>06</urn:StreetNumber>
         |            <urn:Postcode>A1 1AA</urn:Postcode>
         |            <urn:City>kyUGc2oE</urn:City>
         |          </urn:PlaceOfDispatchTrader>
         |          <urn:DeliveryPlaceTrader language="en">
         |            <urn:Traderid>GB008OQQP57TM</urn:Traderid>
         |            <urn:TraderName>n6PyoF2p</urn:TraderName>
         |            <urn:StreetName>kH3u7sMSrY</urn:StreetName>
         |            <urn:StreetNumber>15</urn:StreetNumber>
         |            <urn:Postcode>A1 1AA</urn:Postcode>
         |            <urn:City>KgTpwBD1</urn:City>
         |          </urn:DeliveryPlaceTrader>
         |          <urn:CompetentAuthorityDispatchOffice>
         |            <urn:ReferenceNumber>GB004098</urn:ReferenceNumber>
         |          </urn:CompetentAuthorityDispatchOffice>
         |          <urn:FirstTransporterTrader language="en">
         |            <urn:VatNumber>123798354</urn:VatNumber>
         |            <urn:TraderName>Mr Delivery place trader 4</urn:TraderName>
         |            <urn:StreetName>Delplace Avenue</urn:StreetName>
         |            <urn:StreetNumber>05</urn:StreetNumber>
         |            <urn:Postcode>FR5 4RN</urn:Postcode>
         |            <urn:City>Delville</urn:City>
         |          </urn:FirstTransporterTrader>
         |          <urn:DocumentCertificate>
         |            <urn:DocumentType>3</urn:DocumentType>
         |            <urn:DocumentReference>AeAgkIFN4k5bW3pBnY3W4KWLKXlijmn7eFR</urn:DocumentReference>
         |          </urn:DocumentCertificate>
         |          <urn:HeaderEadEsad>
         |            <urn:DestinationTypeCode>1</urn:DestinationTypeCode>
         |            <urn:JourneyTime>D07</urn:JourneyTime>
         |            <urn:TransportArrangement>1</urn:TransportArrangement>
         |          </urn:HeaderEadEsad>
         |          <urn:TransportMode>
         |            <urn:TransportModeCode>3</urn:TransportModeCode>
         |          </urn:TransportMode>
         |          <urn:MovementGuarantee>
         |            <urn:GuarantorTypeCode>1</urn:GuarantorTypeCode>
         |          </urn:MovementGuarantee>
         |          <urn:BodyEadEsad>
         |            <urn:BodyRecordUniqueReference>1</urn:BodyRecordUniqueReference>
         |            <urn:ExciseProductCode>B000</urn:ExciseProductCode>
         |            <urn:CnCode>22030001</urn:CnCode>
         |            <urn:Quantity>2000</urn:Quantity>
         |            <urn:GrossMass>20000</urn:GrossMass>
         |            <urn:NetMass>19999</urn:NetMass>
         |            <urn:AlcoholicStrengthByVolumeInPercentage>0.5</urn:AlcoholicStrengthByVolumeInPercentage>
         |            <urn:FiscalMarkUsedFlag>0</urn:FiscalMarkUsedFlag>
         |            <urn:Package>
         |              <urn:KindOfPackages>BA</urn:KindOfPackages>
         |              <urn:NumberOfPackages>2</urn:NumberOfPackages>
         |            </urn:Package>
         |          </urn:BodyEadEsad>
         |          <urn:EadEsadDraft>
         |            <urn:LocalReferenceNumber>LRNQA20230127154745</urn:LocalReferenceNumber>
         |            <urn:InvoiceNumber>Test123</urn:InvoiceNumber>
         |            <urn:InvoiceDate>2023-01-27</urn:InvoiceDate>
         |            <urn:OriginTypeCode>1</urn:OriginTypeCode>
         |            <urn:DateOfDispatch>2023-01-27</urn:DateOfDispatch>
         |            <urn:TimeOfDispatch>12:00:00</urn:TimeOfDispatch>
         |          </urn:EadEsadDraft>
         |          <urn:TransportDetails>
         |            <urn:TransportUnitCode>1</urn:TransportUnitCode>
         |            <urn:IdentityOfTransportUnits>100</urn:IdentityOfTransportUnits>
         |          </urn:TransportDetails>
         |        </urn:SubmittedDraftOfEADESAD>
         |      </urn:Body>
         |    </urn:IE815>
         |  </soapenv:Body>
         |</soapenv:Envelope>""".stripMargin

    override def action: String = "http://www.hmrc.gov.uk/emcs/submitdraftmovementportal"
  }
  def submitMovement()(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, GetMovementResponse]] = {
    val testRequest: TestRequest = TestRequest(exciseRegistrationNumber = "GBWK240176600", mark = "PsAfeyWqa1k865R7GuLZ5uQeDt8=")
    connector.submitChrisSOAPRequest[GetMovementResponse](testRequest)
  }
}
