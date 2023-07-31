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

package uk.gov.hmrc.emcstfe.fixtures

import uk.gov.hmrc.emcstfe.models.common.DestinationType.Export
import uk.gov.hmrc.emcstfe.models.common._
import uk.gov.hmrc.emcstfe.models.response.{GetMovementResponse, MovementItem, Packaging, WineProduct}

trait GetMovementIfChangedFixture extends BaseFixtures with TraderModelFixtures {
  lazy val getMovementIfChangedNoChangeSoapWrapper: String = """<tns:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:tns="http://www.w3.org/2003/05/soap-envelope">
                                                               |  <tns:Body>
                                                               |    <con:Control xmlns:con="http://www.govtalk.gov.uk/taxation/InternationalTrade/Common/ControlDocument">
                                                               |      <con:MetaData>
                                                               |        <con:MessageId>String</con:MessageId>
                                                               |        <con:Source>String</con:Source>
                                                               |        <con:Identity>String</con:Identity>
                                                               |        <con:Partner>String</con:Partner>
                                                               |        <con:CorrelationId>String</con:CorrelationId>
                                                               |        <con:BusinessKey>String</con:BusinessKey>
                                                               |        <con:MessageDescriptor>String</con:MessageDescriptor>
                                                               |        <con:QualityOfService>String</con:QualityOfService>
                                                               |        <con:Destination>String</con:Destination>
                                                               |        <con:Priority>0</con:Priority>
                                                               |      </con:MetaData>
                                                               |      <con:OperationResponse>
                                                               |        <con:Results/>
                                                               |      </con:OperationResponse>
                                                               |    </con:Control>
                                                               |  </tns:Body>
                                                               |</tns:Envelope>""".stripMargin

  lazy val getMovementIfChangedResponseBody: String = s"""<mov:movementView xsi:schemaLocation="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3 movementView.xsd" xmlns:mov="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3">
                                               |    <mov:currentMovement>
                                               |      <mov:status>Beans</mov:status>
                                               |      <mov:version_transaction_ref>008</mov:version_transaction_ref>
                                               |      <urn:IE801 xmlns:body="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE801:V3.01">
                                               |        <urn:Header xmlns:head="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:TMS:V2.02">
                                               |          <head:MessageSender>NDEA.FR</head:MessageSender>
                                               |          <head:MessageRecipient>NDEA.GB</head:MessageRecipient>
                                               |          <head:DateOfPreparation>2008-09-04</head:DateOfPreparation>
                                               |          <head:TimeOfPreparation>10:22:50</head:TimeOfPreparation>
                                               |          <head:MessageIdentifier>Message identifier</head:MessageIdentifier>
                                               |        </urn:Header>
                                               |        <urn:Body>
                                               |          <urn:EADESADContainer>
                                               |            <urn:ConsigneeTrader language="en">
                                               |              ${maxTraderModelXML(ConsigneeTrader)}
                                               |            </urn:ConsigneeTrader>
                                               |            <urn:ExciseMovement>
                                               |              <urn:AdministrativeReferenceCode>13AB7778889991ABCDEF9</urn:AdministrativeReferenceCode>
                                               |              <urn:DateAndTimeOfValidationOfEadEsad>2008-09-04T10:22:50</urn:DateAndTimeOfValidationOfEadEsad>
                                               |            </urn:ExciseMovement>
                                               |            <urn:ConsignorTrader language="en">
                                               |              ${maxTraderModelXML(ConsignorTrader)}
                                               |            </urn:ConsignorTrader>
                                               |            <urn:PlaceOfDispatchTrader language="en">
                                               |              ${maxTraderModelXML(PlaceOfDispatchTrader)}
                                               |            </urn:PlaceOfDispatchTrader>
                                               |            <urn:DeliveryPlaceTrader language="en">
                                               |              ${maxTraderModelXML(DeliveryPlaceTrader)}
                                               |            </urn:DeliveryPlaceTrader>
                                               |            <urn:DeliveryPlaceCustomsOffice>
                                               |              <urn:ReferenceNumber>FR000003</urn:ReferenceNumber>
                                               |            </urn:DeliveryPlaceCustomsOffice>
                                               |            <urn:CompetentAuthorityDispatchOffice>
                                               |              <urn:ReferenceNumber>GB000002</urn:ReferenceNumber>
                                               |            </urn:CompetentAuthorityDispatchOffice>
                                               |            <urn:FirstTransporterTrader language="en">
                                               |              ${maxTraderModelXML(TransportTrader)}
                                               |            </urn:FirstTransporterTrader>
                                               |            <urn:DocumentCertificate>
                                               |              <urn:DocumentDescription language="en">Test</urn:DocumentDescription>
                                               |              <urn:ReferenceOfDocument language="en">AB123</urn:ReferenceOfDocument>
                                               |            </urn:DocumentCertificate>
                                               |            <urn:EadEsad>
                                               |              <urn:LocalReferenceNumber>EN</urn:LocalReferenceNumber>
                                               |              <urn:InvoiceNumber>IN777888999</urn:InvoiceNumber>
                                               |              <urn:InvoiceDate>2008-09-04</urn:InvoiceDate>
                                               |              <urn:OriginTypeCode>1</urn:OriginTypeCode>
                                               |              <urn:DateOfDispatch>2008-11-20</urn:DateOfDispatch>
                                               |              <urn:TimeOfDispatch>10:00:00</urn:TimeOfDispatch>
                                               |            </urn:EadEsad>
                                               |            <urn:HeaderEadEsad>
                                               |              <urn:SequenceNumber>1</urn:SequenceNumber>
                                               |              <urn:DateAndTimeOfUpdateValidation>2008-09-04T10:22:50</urn:DateAndTimeOfUpdateValidation>
                                               |              <urn:DestinationTypeCode>6</urn:DestinationTypeCode>
                                               |              <urn:JourneyTime>D20</urn:JourneyTime>
                                               |              <urn:TransportArrangement>1</urn:TransportArrangement>
                                               |            </urn:HeaderEadEsad>
                                               |            <urn:TransportMode>
                                               |              <urn:TransportModeCode>1</urn:TransportModeCode>
                                               |            </urn:TransportMode>
                                               |            <urn:MovementGuarantee>
                                               |              <urn:GuarantorTypeCode>2</urn:GuarantorTypeCode>
                                               |            </urn:MovementGuarantee>
                                               |            <urn:BodyEadEsad>
                                               |              <urn:BodyRecordUniqueReference>1</urn:BodyRecordUniqueReference>
                                               |              <urn:ExciseProductCode>W200</urn:ExciseProductCode>
                                               |              <urn:CnCode>22041011</urn:CnCode>
                                               |              <urn:Quantity>500</urn:Quantity>
                                               |              <urn:GrossMass>900</urn:GrossMass>
                                               |              <urn:NetMass>375</urn:NetMass>
                                               |              <urn:FiscalMark language="en">FM564789 Fiscal Mark</urn:FiscalMark>
                                               |              <urn:FiscalMarkUsedFlag>1</urn:FiscalMarkUsedFlag>
                                               |              <urn:DesignationOfOrigin language="en">Designation of Origin</urn:DesignationOfOrigin>
                                               |              <urn:SizeOfProducer>20000</urn:SizeOfProducer>
                                               |              <urn:CommercialDescription language="en">Retsina</urn:CommercialDescription>
                                               |              <urn:BrandNameOfProducts language="en">MALAMATINA</urn:BrandNameOfProducts>
                                               |              <urn:Package>
                                               |                <urn:KindOfPackages>BO</urn:KindOfPackages>
                                               |                <urn:NumberOfPackages>125</urn:NumberOfPackages>
                                               |                <urn:CommercialSealIdentification>SEAL456789321</urn:CommercialSealIdentification>
                                               |                <urn:SealInformation language="en">Red Strip</urn:SealInformation>
                                               |              </urn:Package>
                                               |              <urn:WineProduct>
                                               |                <urn:WineProductCategory>4</urn:WineProductCategory>
                                               |                <urn:ThirdCountryOfOrigin>FJ</urn:ThirdCountryOfOrigin>
                                               |                <urn:OtherInformation language="en">Not available</urn:OtherInformation>
                                               |                <urn:WineOperation>
                                               |                  <urn:WineOperationCode>4</urn:WineOperationCode>
                                               |                </urn:WineOperation>
                                               |                <urn:WineOperation>
                                               |                  <urn:WineOperationCode>5</urn:WineOperationCode>
                                               |                </urn:WineOperation>
                                               |              </urn:WineProduct>
                                               |            </urn:BodyEadEsad>
                                               |            <urn:BodyEadEsad>
                                               |              <urn:BodyRecordUniqueReference>2</urn:BodyRecordUniqueReference>
                                               |              <urn:ExciseProductCode>W300</urn:ExciseProductCode>
                                               |              <urn:CnCode>27111901</urn:CnCode>
                                               |              <urn:Quantity>501</urn:Quantity>
                                               |              <urn:GrossMass>901</urn:GrossMass>
                                               |              <urn:NetMass>475</urn:NetMass>
                                               |              <urn:AlcoholicStrengthByVolumeInPercentage>12.7</urn:AlcoholicStrengthByVolumeInPercentage>
                                               |              <urn:FiscalMark language="en">FM564790 Fiscal Mark</urn:FiscalMark>
                                               |              <urn:FiscalMarkUsedFlag>1</urn:FiscalMarkUsedFlag>
                                               |              <urn:DesignationOfOrigin language="en">Designation of Origin</urn:DesignationOfOrigin>
                                               |              <urn:SizeOfProducer>20000</urn:SizeOfProducer>
                                               |              <urn:CommercialDescription language="en">Retsina</urn:CommercialDescription>
                                               |              <urn:BrandNameOfProducts language="en">BrandName</urn:BrandNameOfProducts>
                                               |              <urn:Package>
                                               |                <urn:KindOfPackages>BO</urn:KindOfPackages>
                                               |                <urn:NumberOfPackages>125</urn:NumberOfPackages>
                                               |                <urn:CommercialSealIdentification>SEAL456789321</urn:CommercialSealIdentification>
                                               |                <urn:SealInformation language="en">Red Strip</urn:SealInformation>
                                               |              </urn:Package>
                                               |              <urn:Package>
                                               |                <urn:KindOfPackages>HG</urn:KindOfPackages>
                                               |                <urn:NumberOfPackages>7</urn:NumberOfPackages>
                                               |                <urn:CommercialSealIdentification>SEAL77</urn:CommercialSealIdentification>
                                               |                <urn:SealInformation language="en">Cork</urn:SealInformation>
                                               |              </urn:Package>
                                               |              <urn:WineProduct>
                                               |                <urn:WineProductCategory>3</urn:WineProductCategory>
                                               |                <urn:ThirdCountryOfOrigin>FJ</urn:ThirdCountryOfOrigin>
                                               |                <urn:OtherInformation language="en">Not available</urn:OtherInformation>
                                               |                <urn:WineOperation>
                                               |                  <urn:WineOperationCode>0</urn:WineOperationCode>
                                               |                </urn:WineOperation>
                                               |                <urn:WineOperation>
                                               |                  <urn:WineOperationCode>1</urn:WineOperationCode>
                                               |                </urn:WineOperation>
                                               |              </urn:WineProduct>
                                               |            </urn:BodyEadEsad>
                                               |            <urn:TransportDetails>
                                               |              <urn:TransportUnitCode>1</urn:TransportUnitCode>
                                               |              <urn:IdentityOfTransportUnits>Bottles</urn:IdentityOfTransportUnits>
                                               |              <urn:CommercialSealIdentification>SID13245678</urn:CommercialSealIdentification>
                                               |              <urn:ComplementaryInformation language="en">Bottles of Restina</urn:ComplementaryInformation>
                                               |              <urn:SealInformation language="en">Sealed with red strip</urn:SealInformation>
                                               |            </urn:TransportDetails>
                                               |            <urn:TransportDetails>
                                               |              <urn:TransportUnitCode>2</urn:TransportUnitCode>
                                               |              <urn:IdentityOfTransportUnits>Cans</urn:IdentityOfTransportUnits>
                                               |              <urn:CommercialSealIdentification>SID132987</urn:CommercialSealIdentification>
                                               |              <urn:ComplementaryInformation language="en">Cans</urn:ComplementaryInformation>
                                               |              <urn:SealInformation language="en">Seal info</urn:SealInformation>
                                               |            </urn:TransportDetails>
                                               |          </urn:EADESADContainer>
                                               |        </urn:Body>
                                               |      </urn:IE801>
                                               |    </mov:currentMovement>
                                               |    <mov:eventHistory>
                                               |      <urn:IE801 xmlns:body="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE801:V3.01">
                                               |        <urn:Header xmlns:head="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:TMS:V2.02">
                                               |          <head:MessageSender>NDEA.FR</head:MessageSender>
                                               |          <head:MessageRecipient>NDEA.GB</head:MessageRecipient>
                                               |          <head:DateOfPreparation>2008-09-04</head:DateOfPreparation>
                                               |          <head:TimeOfPreparation>10:22:50</head:TimeOfPreparation>
                                               |          <head:MessageIdentifier>Message identifier</head:MessageIdentifier>
                                               |        </urn:Header>
                                               |        <urn:Body>
                                               |          <urn:EADESADContainer>
                                               |            <urn:ConsigneeTrader language="en">
                                               |              ${maxTraderModelXML(ConsigneeTrader)}
                                               |            </urn:ConsigneeTrader>
                                               |            <urn:ExciseMovement>
                                               |              <urn:AdministrativeReferenceCode>13AB7778889991ABCDEF9</urn:AdministrativeReferenceCode>
                                               |              <urn:DateAndTimeOfValidationOfEadEsad>2008-09-04T10:22:50</urn:DateAndTimeOfValidationOfEadEsad>
                                               |            </urn:ExciseMovement>
                                               |            <urn:ConsignorTrader language="en">
                                               |              ${maxTraderModelXML(ConsignorTrader)}
                                               |            </urn:ConsignorTrader>
                                               |            <urn:PlaceOfDispatchTrader language="en">
                                               |              ${maxTraderModelXML(PlaceOfDispatchTrader)}
                                               |            </urn:PlaceOfDispatchTrader>
                                               |            <urn:DeliveryPlaceCustomsOffice>
                                               |              <urn:ReferenceNumber>FR000003</urn:ReferenceNumber>
                                               |            </urn:DeliveryPlaceCustomsOffice>
                                               |            <urn:CompetentAuthorityDispatchOffice>
                                               |              <urn:ReferenceNumber>GB000002</urn:ReferenceNumber>
                                               |            </urn:CompetentAuthorityDispatchOffice>
                                               |            <urn:FirstTransporterTrader language="en">
                                               |              ${maxTraderModelXML(TransportTrader)}
                                               |            </urn:FirstTransporterTrader>
                                               |            <urn:DocumentCertificate>
                                               |              <urn:DocumentDescription language="en">Test</urn:DocumentDescription>
                                               |              <urn:ReferenceOfDocument language="en">AB123</urn:ReferenceOfDocument>
                                               |            </urn:DocumentCertificate>
                                               |            <urn:EadEsad>
                                               |              <urn:LocalReferenceNumber>EN</urn:LocalReferenceNumber>
                                               |              <urn:InvoiceNumber>IN777888999</urn:InvoiceNumber>
                                               |              <urn:InvoiceDate>2008-09-04</urn:InvoiceDate>
                                               |              <urn:OriginTypeCode>1</urn:OriginTypeCode>
                                               |              <urn:DateOfDispatch>2008-11-20</urn:DateOfDispatch>
                                               |              <urn:TimeOfDispatch>10:00:00</urn:TimeOfDispatch>
                                               |            </urn:EadEsad>
                                               |            <urn:HeaderEadEsad>
                                               |              <urn:SequenceNumber>1</urn:SequenceNumber>
                                               |              <urn:DateAndTimeOfUpdateValidation>2008-09-04T10:22:50</urn:DateAndTimeOfUpdateValidation>
                                               |              <urn:DestinationTypeCode>6</urn:DestinationTypeCode>
                                               |              <urn:JourneyTime>D20</urn:JourneyTime>
                                               |              <urn:TransportArrangement>1</urn:TransportArrangement>
                                               |            </urn:HeaderEadEsad>
                                               |            <urn:TransportMode>
                                               |              <urn:TransportModeCode>1</urn:TransportModeCode>
                                               |            </urn:TransportMode>
                                               |            <urn:MovementGuarantee>
                                               |              <urn:GuarantorTypeCode>2</urn:GuarantorTypeCode>
                                               |            </urn:MovementGuarantee>
                                               |            <urn:BodyEadEsad>
                                               |              <urn:BodyRecordUniqueReference>1</urn:BodyRecordUniqueReference>
                                               |              <urn:ExciseProductCode>W200</urn:ExciseProductCode>
                                               |              <urn:CnCode>27111900</urn:CnCode>
                                               |              <urn:Quantity>500</urn:Quantity>
                                               |              <urn:GrossMass>900</urn:GrossMass>
                                               |              <urn:NetMass>375</urn:NetMass>
                                               |              <urn:FiscalMark language="en">FM564789 Fiscal Mark</urn:FiscalMark>
                                               |              <urn:FiscalMarkUsedFlag>1</urn:FiscalMarkUsedFlag>
                                               |              <urn:DesignationOfOrigin language="en">Designation of Origin</urn:DesignationOfOrigin>
                                               |              <urn:SizeOfProducer>20000</urn:SizeOfProducer>
                                               |              <urn:CommercialDescription language="en">Retsina</urn:CommercialDescription>
                                               |              <urn:BrandNameOfProducts language="en">MALAMATINA</urn:BrandNameOfProducts>
                                               |              <urn:Package>
                                               |                <urn:KindOfPackages>GB</urn:KindOfPackages>
                                               |                <urn:NumberOfPackages>125</urn:NumberOfPackages>
                                               |                <urn:CommercialSealIdentification>SEAL456789321</urn:CommercialSealIdentification>
                                               |                <urn:SealInformation language="en">Red Strip</urn:SealInformation>
                                               |              </urn:Package>
                                               |              <urn:WineProduct>
                                               |                <urn:WineProductCategory>4</urn:WineProductCategory>
                                               |                <urn:ThirdCountryOfOrigin>FJ</urn:ThirdCountryOfOrigin>
                                               |                <urn:OtherInformation language="en">Not available</urn:OtherInformation>
                                               |                <urn:WineOperation>
                                               |                  <urn:WineOperationCode>4</urn:WineOperationCode>
                                               |                </urn:WineOperation>
                                               |              </urn:WineProduct>
                                               |            </urn:BodyEadEsad>
                                               |            <urn:BodyEadEsad>
                                               |              <urn:BodyRecordUniqueReference>2</urn:BodyRecordUniqueReference>
                                               |              <urn:ExciseProductCode>W300</urn:ExciseProductCode>
                                               |              <urn:CnCode>27111901</urn:CnCode>
                                               |              <urn:Quantity>501</urn:Quantity>
                                               |              <urn:GrossMass>901</urn:GrossMass>
                                               |              <urn:NetMass>475</urn:NetMass>
                                               |              <urn:AlcoholicStrengthByVolumeInPercentage>12.7</urn:AlcoholicStrengthByVolumeInPercentage>
                                               |              <urn:FiscalMark language="en">FM564790 Fiscal Mark</urn:FiscalMark>
                                               |              <urn:FiscalMarkUsedFlag>1</urn:FiscalMarkUsedFlag>
                                               |              <urn:DesignationOfOrigin language="en">Designation of Origin</urn:DesignationOfOrigin>
                                               |              <urn:SizeOfProducer>20000</urn:SizeOfProducer>
                                               |              <urn:CommercialDescription language="en">Retsina</urn:CommercialDescription>
                                               |              <urn:BrandNameOfProducts language="en">BrandName</urn:BrandNameOfProducts>
                                               |              <urn:Package>
                                               |                <urn:KindOfPackages>GB</urn:KindOfPackages>
                                               |                <urn:NumberOfPackages>125</urn:NumberOfPackages>
                                               |                <urn:CommercialSealIdentification>SEAL456789321</urn:CommercialSealIdentification>
                                               |                <urn:SealInformation language="en">Red Strip</urn:SealInformation>
                                               |              </urn:Package>
                                               |              <urn:WineProduct>
                                               |                <urn:WineProductCategory>4</urn:WineProductCategory>
                                               |                <urn:ThirdCountryOfOrigin>FJ</urn:ThirdCountryOfOrigin>
                                               |                <urn:OtherInformation language="en">Not available</urn:OtherInformation>
                                               |                <urn:WineOperation>
                                               |                  <urn:WineOperationCode>4 5</urn:WineOperationCode>
                                               |                </urn:WineOperation>
                                               |              </urn:WineProduct>
                                               |            </urn:BodyEadEsad>
                                               |            <urn:TransportDetails>
                                               |              <urn:TransportUnitCode>1</urn:TransportUnitCode>
                                               |              <urn:IdentityOfTransportUnits>Bottles</urn:IdentityOfTransportUnits>
                                               |              <urn:CommercialSealIdentification>SID13245678</urn:CommercialSealIdentification>
                                               |              <urn:ComplementaryInformation language="en">Bottles of Restina</urn:ComplementaryInformation>
                                               |              <urn:SealInformation language="en">Sealed with red strip</urn:SealInformation>
                                               |            </urn:TransportDetails>
                                               |            <urn:TransportDetails>
                                               |              <urn:TransportUnitCode>2</urn:TransportUnitCode>
                                               |              <urn:IdentityOfTransportUnits>Cans</urn:IdentityOfTransportUnits>
                                               |              <urn:CommercialSealIdentification>SID132987</urn:CommercialSealIdentification>
                                               |              <urn:ComplementaryInformation language="en">Cans</urn:ComplementaryInformation>
                                               |              <urn:SealInformation language="en">Seal info</urn:SealInformation>
                                               |            </urn:TransportDetails>
                                               |          </urn:EADESADContainer>
                                               |        </urn:Body>
                                               |      </urn:IE801>
                                               |      <urn:IE810 xmlns:body="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:IE810:V2.02">
                                               |        <urn:Header xmlns:head="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:TMS:V2.02">
                                               |          <head:MessageSender>token</head:MessageSender>
                                               |          <head:MessageRecipient>token</head:MessageRecipient>
                                               |          <head:DateOfPreparation>1967-08-13</head:DateOfPreparation>
                                               |          <head:TimeOfPreparation>14:20:0Z</head:TimeOfPreparation>
                                               |          <head:MessageIdentifier>token</head:MessageIdentifier>
                                               |          <head:CorrelationIdentifier>token</head:CorrelationIdentifier>
                                               |        </urn:Header>
                                               |        <urn:Body>
                                               |          <urn:CancellationOfEAD>
                                               |            <urn:Attributes>
                                               |              <urn:DateAndTimeOfValidationOfCancellation>2008-09-04T10:22:53</urn:DateAndTimeOfValidationOfCancellation>
                                               |            </urn:Attributes>
                                               |            <urn:ExciseMovement>
                                               |              <urn:AdministrativeReferenceCode>13AB7778889991ABCDEF9</urn:AdministrativeReferenceCode>
                                               |            </urn:ExciseMovement>
                                               |            <urn:Cancellation>
                                               |              <urn:CancellationReasonCode>1</urn:CancellationReasonCode>
                                               |            </urn:Cancellation>
                                               |          </urn:CancellationOfEAD>
                                               |        </urn:Body>
                                               |      </urn:IE810>
                                               |      <urn:IE802 xmlns:body="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:IE802:V2.02">
                                               |        <urn:Header xmlns:head="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:TMS:V2.02">
                                               |          <head:MessageSender>token</head:MessageSender>
                                               |          <head:MessageRecipient>token</head:MessageRecipient>
                                               |          <head:DateOfPreparation>1967-08-13</head:DateOfPreparation>
                                               |          <head:TimeOfPreparation>14:20:0Z</head:TimeOfPreparation>
                                               |          <head:MessageIdentifier>token</head:MessageIdentifier>
                                               |          <head:CorrelationIdentifier>token</head:CorrelationIdentifier>
                                               |        </urn:Header>
                                               |        <urn:Body>
                                               |          <urn:ReminderMessageForExciseMovement>
                                               |            <urn:Attributes>
                                               |              <urn:DateAndTimeOfIssuanceOfReminder>2008-09-04T10:22:53</urn:DateAndTimeOfIssuanceOfReminder>
                                               |              <urn:ReminderInformation language="en">To be completed by this date</urn:ReminderInformation>
                                               |              <urn:LimitDateAndTime>2008-09-04T10:22:53</urn:LimitDateAndTime>
                                               |              <urn:ReminderMessageType>1</urn:ReminderMessageType>
                                               |            </urn:Attributes>
                                               |            <urn:ExciseMovement>
                                               |              <urn:AdministrativeReferenceCode>13AB7778889991ABCDEF9</urn:AdministrativeReferenceCode>
                                               |              <urn:SequenceNumber>1</urn:SequenceNumber>
                                               |            </urn:ExciseMovement>
                                               |          </urn:ReminderMessageForExciseMovement>
                                               |        </urn:Body>
                                               |      </urn:IE802>
                                               |      <urn:IE803 xmlns:body="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:IE803:V2.02">
                                               |        <urn:Header xmlns:head="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:TMS:V2.02">
                                               |          <head:MessageSender>token</head:MessageSender>
                                               |          <head:MessageRecipient>token</head:MessageRecipient>
                                               |          <head:DateOfPreparation>1967-08-13</head:DateOfPreparation>
                                               |          <head:TimeOfPreparation>14:20:0Z</head:TimeOfPreparation>
                                               |          <head:MessageIdentifier>token</head:MessageIdentifier>
                                               |          <head:CorrelationIdentifier>token</head:CorrelationIdentifier>
                                               |        </urn:Header>
                                               |        <urn:Body>
                                               |          <urn:NotificationOfDivertedEADESAD>
                                               |            <urn:ExciseNotification>
                                               |              <urn:NotificationType>1</urn:NotificationType>
                                               |              <urn:NotificationDateAndTime>2001-12-17T09:30:47.0Z</urn:NotificationDateAndTime>
                                               |              <urn:AdministrativeReferenceCode>13AB1234567891ABCDEF9</urn:AdministrativeReferenceCode>
                                               |              <urn:SequenceNumber>1</urn:SequenceNumber>
                                               |            </urn:ExciseNotification>
                                               |          </urn:NotificationOfDivertedEADESAD>
                                               |        </urn:Body>
                                               |      </urn:IE803>
                                               |      <urn:IE837 xmlns:body="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:IE837:V2.02">
                                               |        <urn:Header xmlns:head="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:TMS:V2.02">
                                               |          <head:MessageSender>token</head:MessageSender>
                                               |          <head:MessageRecipient>token</head:MessageRecipient>
                                               |          <head:DateOfPreparation>1967-08-13</head:DateOfPreparation>
                                               |          <head:TimeOfPreparation>14:20:0Z</head:TimeOfPreparation>
                                               |          <head:MessageIdentifier>token</head:MessageIdentifier>
                                               |          <head:CorrelationIdentifier>token</head:CorrelationIdentifier>
                                               |        </urn:Header>
                                               |        <urn:Body>
                                               |          <urn:ExplanationOnDelayForDelivery>
                                               |            <urn:Attributes>
                                               |              <urn:SubmitterIdentification>837Submitter</urn:SubmitterIdentification>
                                               |              <urn:SubmitterType>1</urn:SubmitterType>
                                               |              <urn:ExplanationCode>1</urn:ExplanationCode>
                                               |              <urn:ComplementaryInformation language="to">837 complementory info</urn:ComplementaryInformation>
                                               |              <urn:MessageRole>1</urn:MessageRole>
                                               |              <urn:DateAndTimeOfValidationOfExplanationOnDelay>2001-12-17T09:30:47.0Z</urn:DateAndTimeOfValidationOfExplanationOnDelay>
                                               |            </urn:Attributes>
                                               |            <urn:ExciseMovement>
                                               |              <urn:AdministrativeReferenceCode>13AB1234567891ABCDEF9</urn:AdministrativeReferenceCode>
                                               |              <urn:SequenceNumber/>
                                               |            </urn:ExciseMovement>
                                               |          </urn:ExplanationOnDelayForDelivery>
                                               |        </urn:Body>
                                               |      </urn:IE837>
                                               |    </mov:eventHistory>
                                               |  </mov:movementView>""".stripMargin

  lazy val getMovementIfChangedWithChangeSoapWrapper: String = s"""<tns:Envelope
                                               |	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                                               |	xmlns:tns="http://www.w3.org/2003/05/soap-envelope">
                                               |	<tns:Body>
                                               |		<con:Control
                                               |			xmlns:con="http://www.govtalk.gov.uk/taxation/InternationalTrade/Common/ControlDocument">
                                               |			<con:MetaData>
                                               |				<con:MessageId>String</con:MessageId>
                                               |				<con:Source>String</con:Source>
                                               |				<con:Identity>String</con:Identity>
                                               |				<con:Partner>String</con:Partner>
                                               |				<con:CorrelationId>String</con:CorrelationId>
                                               |				<con:BusinessKey>String</con:BusinessKey>
                                               |				<con:MessageDescriptor>String</con:MessageDescriptor>
                                               |				<con:QualityOfService>String</con:QualityOfService>
                                               |				<con:Destination>String</con:Destination>
                                               |				<con:Priority>0</con:Priority>
                                               |			</con:MetaData>
                                               |			<con:OperationResponse>
                                               |				<con:Results>
                                               |					<con:Result Name="">
                                               |						<![CDATA[$getMovementIfChangedResponseBody]]>
                                               |					</con:Result>
                                               |				</con:Results>
                                               |			</con:OperationResponse>
                                               |		</con:Control>
                                               |	</tns:Body>
                                               |</tns:Envelope>""".stripMargin

  lazy val getMovementIfChangedResponse: GetMovementResponse = GetMovementResponse(
    arc = "13AB7778889991ABCDEF9",
    sequenceNumber = 1,
    destinationType = Export,
    consigneeTrader = Some(maxTraderModel(ConsigneeTrader)),
    deliveryPlaceTrader = Some(maxTraderModel(DeliveryPlaceTrader)),
    localReferenceNumber = "EN",
    eadStatus = "Beans",
    consignorTrader = maxTraderModel(ConsignorTrader),
    dateOfDispatch = "2008-11-20",
    journeyTime = "20 days",
    items = Seq(
      MovementItem(
        itemUniqueReference = 1,
        productCode = "W200",
        cnCode = "22041011",
        quantity = BigDecimal(500),
        grossMass = BigDecimal(900),
        netMass = BigDecimal(375),
        alcoholicStrength = None,
        degreePlato = None,
        fiscalMark = Some("FM564789 Fiscal Mark"),
        designationOfOrigin = Some("Designation of Origin"),
        sizeOfProducer = Some("20000"),
        density = None,
        commercialDescription = Some("Retsina"),
        brandNameOfProduct = Some("MALAMATINA"),
        maturationAge = None,
        packaging = Seq(
          Packaging(
            typeOfPackage = "BO",
            quantity = Some(125),
            shippingMarks = None,
            identityOfCommercialSeal = Some("SEAL456789321"),
            sealInformation = Some("Red Strip")
          )
        ),
        wineProduct = Some(
          WineProduct(
            wineProductCategory = "4",
            wineGrowingZoneCode = None,
            thirdCountryOfOrigin = Some("FJ"),
            otherInformation = Some("Not available"),
            wineOperations = Some(Seq("4", "5"))
          )
        )
      ),
      MovementItem(
        itemUniqueReference = 2,
        productCode = "W300",
        cnCode = "27111901",
        quantity = BigDecimal(501),
        grossMass = BigDecimal(901),
        netMass = BigDecimal(475),
        alcoholicStrength = Some(BigDecimal(12.7)),
        degreePlato = None,
        fiscalMark = Some("FM564790 Fiscal Mark"),
        designationOfOrigin = Some("Designation of Origin"),
        sizeOfProducer = Some("20000"),
        density = None,
        commercialDescription = Some("Retsina"),
        brandNameOfProduct = Some("BrandName"),
        maturationAge = None,
        packaging = Seq(
          Packaging(
            typeOfPackage = "BO",
            quantity = Some(125),
            shippingMarks = None,
            identityOfCommercialSeal = Some("SEAL456789321"),
            sealInformation = Some("Red Strip")
          ),
          Packaging(
            typeOfPackage = "HG",
            quantity = Some(7),
            shippingMarks = None,
            identityOfCommercialSeal = Some("SEAL77"),
            sealInformation = Some("Cork")
          )
        ),
        wineProduct = Some(
          WineProduct(
            wineProductCategory = "3",
            wineGrowingZoneCode = None,
            thirdCountryOfOrigin = Some("FJ"),
            otherInformation = Some("Not available"),
            wineOperations = Some(Seq("0", "1"))
          )
        )
      )
    ),
    numberOfItems = 2
  )

}
