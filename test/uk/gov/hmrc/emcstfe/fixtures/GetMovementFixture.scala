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

import play.api.libs.json._
import uk.gov.hmrc.emcstfe.models.cancellationOfMovement.{CancellationReasonModel, CancellationReasonType}
import uk.gov.hmrc.emcstfe.models.alertOrRejection.AlertOrRejectionReasonType.{EADNotConcernRecipient, Other, ProductDoesNotMatchOrder, QuantityDoesNotMatchOrder}
import uk.gov.hmrc.emcstfe.models.alertOrRejection.AlertOrRejectionType.{Alert, Rejection}
import uk.gov.hmrc.emcstfe.models.common.AcceptMovement.Satisfactory
import uk.gov.hmrc.emcstfe.models.common.DestinationType._
import uk.gov.hmrc.emcstfe.models.common.WrongWithMovement.{Excess, Shortage}
import uk.gov.hmrc.emcstfe.models.common._
import uk.gov.hmrc.emcstfe.models.explainDelay.DelayReasonType
import uk.gov.hmrc.emcstfe.models.mongo.GetMovementMongoResponse
import uk.gov.hmrc.emcstfe.models.reportOfReceipt.{ReceiptedItemsModel, SubmitReportOfReceiptModel, UnsatisfactoryModel}
import uk.gov.hmrc.emcstfe.models.response.getMovement.NotificationOfDivertedMovementType.SplitMovement
import uk.gov.hmrc.emcstfe.models.response.getMovement._
import uk.gov.hmrc.emcstfe.models.response.{Packaging, RawGetMovementResponse, WineProduct}

import java.time.{LocalDate, LocalDateTime}
import java.util.Base64
import scala.xml.XML

trait GetMovementFixture extends BaseFixtures with TraderModelFixtures {

  def getMovementResponseBody(sequenceNumber: Int = 1): String =
    s"""<mov:movementView xsi:schemaLocation="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3 movementView.xsd" xmlns:mov="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3">
       |    <mov:currentMovement>
       |      <mov:status>Accepted</mov:status>
       |      <mov:version_transaction_ref>008</mov:version_transaction_ref>
       |      <body:IE801 xmlns:body="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE801:V3.01">
       |        <body:Header xmlns:head="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:TMS:V2.02">
       |          <head:MessageSender>NDEA.FR</head:MessageSender>
       |          <head:MessageRecipient>NDEA.GB</head:MessageRecipient>
       |          <head:DateOfPreparation>2008-09-04</head:DateOfPreparation>
       |          <head:TimeOfPreparation>10:22:50</head:TimeOfPreparation>
       |          <head:MessageIdentifier>Message identifier</head:MessageIdentifier>
       |        </body:Header>
       |        <body:Body>
       |          <body:EADESADContainer>
       |            <body:ConsigneeTrader language="en">
       |              ${maxTraderModelXML(ConsigneeTrader)}
       |            </body:ConsigneeTrader>
       |            <body:ComplementConsigneeTrader>
       |              <MemberStateCode>GB</MemberStateCode>
       |            </body:ComplementConsigneeTrader>
       |            <body:ExciseMovement>
       |              <body:AdministrativeReferenceCode>13AB7778889991ABCDEF9</body:AdministrativeReferenceCode>
       |              <body:DateAndTimeOfValidationOfEadEsad>2008-09-04T10:22:50</body:DateAndTimeOfValidationOfEadEsad>
       |            </body:ExciseMovement>
       |            <body:ConsignorTrader language="en">
       |              ${maxTraderModelXML(ConsignorTrader)}
       |            </body:ConsignorTrader>
       |            <body:PlaceOfDispatchTrader language="en">
       |              ${maxTraderModelXML(PlaceOfDispatchTrader)}
       |            </body:PlaceOfDispatchTrader>
       |            <body:DeliveryPlaceCustomsOffice>
       |              <body:ReferenceNumber>FR000003</body:ReferenceNumber>
       |            </body:DeliveryPlaceCustomsOffice>
       |            <body:CompetentAuthorityDispatchOffice>
       |              <body:ReferenceNumber>GB000002</body:ReferenceNumber>
       |            </body:CompetentAuthorityDispatchOffice>
       |            <body:FirstTransporterTrader language="en">
       |              ${maxTraderModelXML(TransportTrader)}
       |            </body:FirstTransporterTrader>
       |            <body:DocumentCertificate>
       |              <body:DocumentDescription language="en">Test</body:DocumentDescription>
       |              <body:ReferenceOfDocument language="en">AB123</body:ReferenceOfDocument>
       |            </body:DocumentCertificate>
       |            <body:EadEsad>
       |              <body:LocalReferenceNumber>EN</body:LocalReferenceNumber>
       |              <body:InvoiceNumber>IN777888999</body:InvoiceNumber>
       |              <body:InvoiceDate>2008-09-04</body:InvoiceDate>
       |              <body:OriginTypeCode>1</body:OriginTypeCode>
       |              <body:DateOfDispatch>2008-11-20</body:DateOfDispatch>
       |              <body:TimeOfDispatch>10:00:00</body:TimeOfDispatch>
       |            </body:EadEsad>
       |            <body:HeaderEadEsad>
       |              <body:SequenceNumber>$sequenceNumber</body:SequenceNumber>
       |              <body:DateAndTimeOfUpdateValidation>2008-09-04T10:22:50</body:DateAndTimeOfUpdateValidation>
       |              <body:DestinationTypeCode>6</body:DestinationTypeCode>
       |              <body:JourneyTime>D20</body:JourneyTime>
       |              <body:TransportArrangement>1</body:TransportArrangement>
       |            </body:HeaderEadEsad>
       |            <body:TransportMode>
       |              <body:TransportModeCode>1</body:TransportModeCode>
       |            </body:TransportMode>
       |            <body:MovementGuarantee>
       |              <body:GuarantorTypeCode>0</body:GuarantorTypeCode>
       |            </body:MovementGuarantee>
       |            <body:BodyEadEsad>
       |              <body:BodyRecordUniqueReference>1</body:BodyRecordUniqueReference>
       |              <body:ExciseProductCode>W200</body:ExciseProductCode>
       |              <body:CnCode>22041011</body:CnCode>
       |              <body:Quantity>500</body:Quantity>
       |              <body:GrossMass>900</body:GrossMass>
       |              <body:NetMass>375</body:NetMass>
       |              <body:FiscalMark language="en">FM564789 Fiscal Mark</body:FiscalMark>
       |              <body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
       |              <body:DegreePlato>1.2</body:DegreePlato>
       |              <body:MaturationPeriodOrAgeOfProducts language="EN">Maturation Period</body:MaturationPeriodOrAgeOfProducts>
       |              <body:IndependentSmallProducersDeclaration language="EN">Independent Small Producers Declaration</body:IndependentSmallProducersDeclaration>
       |              <body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
       |              <body:SizeOfProducer>20000</body:SizeOfProducer>
       |              <body:Density>880</body:Density>
       |              <body:CommercialDescription language="en">Retsina</body:CommercialDescription>
       |              <body:BrandNameOfProducts language="en">MALAMATINA</body:BrandNameOfProducts>
       |              <body:Package>
       |                <body:KindOfPackages>BO</body:KindOfPackages>
       |                <body:NumberOfPackages>125</body:NumberOfPackages>
       |                <body:ShippingMarks>MARKS</body:ShippingMarks>
       |                <body:CommercialSealIdentification>SEAL456789321</body:CommercialSealIdentification>
       |                <body:SealInformation language="en">Red Strip</body:SealInformation>
       |              </body:Package>
       |              <body:WineProduct>
       |                <body:WineProductCategory>4</body:WineProductCategory>
       |                <body:WineGrowingZoneCode>2</body:WineGrowingZoneCode>
       |                <body:ThirdCountryOfOrigin>FJ</body:ThirdCountryOfOrigin>
       |                <body:OtherInformation language="en">Not available</body:OtherInformation>
       |                <body:WineOperation>
       |                  <body:WineOperationCode>4</body:WineOperationCode>
       |                </body:WineOperation>
       |                <body:WineOperation>
       |                  <body:WineOperationCode>5</body:WineOperationCode>
       |                </body:WineOperation>
       |              </body:WineProduct>
       |            </body:BodyEadEsad>
       |            <body:BodyEadEsad>
       |              <body:BodyRecordUniqueReference>2</body:BodyRecordUniqueReference>
       |              <body:ExciseProductCode>W300</body:ExciseProductCode>
       |              <body:CnCode>27111901</body:CnCode>
       |              <body:Quantity>501</body:Quantity>
       |              <body:GrossMass>901</body:GrossMass>
       |              <body:NetMass>475</body:NetMass>
       |              <body:AlcoholicStrengthByVolumeInPercentage>12.7</body:AlcoholicStrengthByVolumeInPercentage>
       |              <body:FiscalMark language="en">FM564790 Fiscal Mark</body:FiscalMark>
       |              <body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
       |              <body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
       |              <body:SizeOfProducer>20000</body:SizeOfProducer>
       |              <body:CommercialDescription language="en">Retsina</body:CommercialDescription>
       |              <body:BrandNameOfProducts language="en">BrandName</body:BrandNameOfProducts>
       |              <body:Package>
       |                <body:KindOfPackages>BO</body:KindOfPackages>
       |                <body:NumberOfPackages>125</body:NumberOfPackages>
       |                <body:CommercialSealIdentification>SEAL456789321</body:CommercialSealIdentification>
       |                <body:SealInformation language="en">Red Strip</body:SealInformation>
       |              </body:Package>
       |              <body:Package>
       |                <body:KindOfPackages>HG</body:KindOfPackages>
       |                <body:NumberOfPackages>7</body:NumberOfPackages>
       |                <body:CommercialSealIdentification>SEAL77</body:CommercialSealIdentification>
       |                <body:SealInformation language="en">Cork</body:SealInformation>
       |              </body:Package>
       |              <body:WineProduct>
       |                <body:WineProductCategory>3</body:WineProductCategory>
       |                <body:ThirdCountryOfOrigin>FJ</body:ThirdCountryOfOrigin>
       |                <body:OtherInformation language="en">Not available</body:OtherInformation>
       |                <body:WineOperation>
       |                  <body:WineOperationCode>0</body:WineOperationCode>
       |                </body:WineOperation>
       |                <body:WineOperation>
       |                  <body:WineOperationCode>1</body:WineOperationCode>
       |                </body:WineOperation>
       |              </body:WineProduct>
       |            </body:BodyEadEsad>
       |            <body:TransportDetails>
       |              <body:TransportUnitCode>1</body:TransportUnitCode>
       |              <body:IdentityOfTransportUnits>Bottles</body:IdentityOfTransportUnits>
       |              <body:CommercialSealIdentification>SID13245678</body:CommercialSealIdentification>
       |              <body:ComplementaryInformation language="en">Bottles of Restina</body:ComplementaryInformation>
       |              <body:SealInformation language="en">Sealed with red strip</body:SealInformation>
       |            </body:TransportDetails>
       |            <body:TransportDetails>
       |              <body:TransportUnitCode>2</body:TransportUnitCode>
       |              <body:IdentityOfTransportUnits>Cans</body:IdentityOfTransportUnits>
       |              <body:CommercialSealIdentification>SID132987</body:CommercialSealIdentification>
       |              <body:ComplementaryInformation language="en">Cans</body:ComplementaryInformation>
       |              <body:SealInformation language="en">Seal</body:SealInformation>
       |            </body:TransportDetails>
       |          </body:EADESADContainer>
       |        </body:Body>
       |      </body:IE801>
       |    </mov:currentMovement>
       |    <mov:eventHistory>
       |      <body:IE801 xmlns:body="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE801:V3.01">
       |        <body:Header xmlns:head="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:TMS:V2.02">
       |          <head:MessageSender>NDEA.FR</head:MessageSender>
       |          <head:MessageRecipient>NDEA.GB</head:MessageRecipient>
       |          <head:DateOfPreparation>2008-09-04</head:DateOfPreparation>
       |          <head:TimeOfPreparation>10:22:50</head:TimeOfPreparation>
       |          <head:MessageIdentifier>Message identifier</head:MessageIdentifier>
       |        </body:Header>
       |        <body:Body>
       |          <body:EADESADContainer>
       |            <body:ConsigneeTrader language="en">
       |              ${maxTraderModelXML(ConsigneeTrader)}
       |            </body:ConsigneeTrader>
       |            <body:ExciseMovement>
       |              <body:AdministrativeReferenceCode>13AB7778889991ABCDEF9</body:AdministrativeReferenceCode>
       |              <body:DateAndTimeOfValidationOfEadEsad>2008-09-04T10:22:50</body:DateAndTimeOfValidationOfEadEsad>
       |            </body:ExciseMovement>
       |            <body:ConsignorTrader language="en">
       |              ${maxTraderModelXML(ConsignorTrader)}
       |            </body:ConsignorTrader>
       |            <body:PlaceOfDispatchTrader language="en">
       |              ${maxTraderModelXML(PlaceOfDispatchTrader)}
       |            </body:PlaceOfDispatchTrader>
       |            <body:DeliveryPlaceCustomsOffice>
       |              <body:ReferenceNumber>FR000003</body:ReferenceNumber>
       |            </body:DeliveryPlaceCustomsOffice>
       |            <body:CompetentAuthorityDispatchOffice>
       |              <body:ReferenceNumber>GB000002</body:ReferenceNumber>
       |            </body:CompetentAuthorityDispatchOffice>
       |            <body:FirstTransporterTrader language="en">
       |              ${maxTraderModelXML(TransportTrader)}
       |            </body:FirstTransporterTrader>
       |            <body:DocumentCertificate>
       |              <body:DocumentDescription language="en">Test</body:DocumentDescription>
       |              <body:ReferenceOfDocument language="en">AB123</body:ReferenceOfDocument>
       |            </body:DocumentCertificate>
       |            <body:EadEsad>
       |              <body:LocalReferenceNumber>EN</body:LocalReferenceNumber>
       |              <body:InvoiceNumber>IN777888999</body:InvoiceNumber>
       |              <body:InvoiceDate>2008-09-04</body:InvoiceDate>
       |              <body:OriginTypeCode>1</body:OriginTypeCode>
       |              <body:DateOfDispatch>2008-11-20</body:DateOfDispatch>
       |              <body:TimeOfDispatch>10:00:00</body:TimeOfDispatch>
       |            </body:EadEsad>
       |            <body:HeaderEadEsad>
       |              <body:SequenceNumber>1</body:SequenceNumber>
       |              <body:DateAndTimeOfUpdateValidation>2008-09-04T10:22:50</body:DateAndTimeOfUpdateValidation>
       |              <body:DestinationTypeCode>6</body:DestinationTypeCode>
       |              <body:JourneyTime>D20</body:JourneyTime>
       |              <body:TransportArrangement>1</body:TransportArrangement>
       |            </body:HeaderEadEsad>
       |            <body:TransportMode>
       |              <body:TransportModeCode>1</body:TransportModeCode>
       |            </body:TransportMode>
       |            <body:MovementGuarantee>
       |              <body:GuarantorTypeCode>0</body:GuarantorTypeCode>
       |            </body:MovementGuarantee>
       |            <body:BodyEadEsad>
       |              <body:BodyRecordUniqueReference>1</body:BodyRecordUniqueReference>
       |              <body:ExciseProductCode>W200</body:ExciseProductCode>
       |              <body:CnCode>27111900</body:CnCode>
       |              <body:Quantity>500</body:Quantity>
       |              <body:GrossMass>900</body:GrossMass>
       |              <body:NetMass>375</body:NetMass>
       |              <body:FiscalMark language="en">FM564789 Fiscal Mark</body:FiscalMark>
       |              <body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
       |              <body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
       |              <body:SizeOfProducer>20000</body:SizeOfProducer>
       |              <body:CommercialDescription language="en">Retsina</body:CommercialDescription>
       |              <body:BrandNameOfProducts language="en">MALAMATINA</body:BrandNameOfProducts>
       |              <body:Package>
       |                <body:KindOfPackages>GB</body:KindOfPackages>
       |                <body:NumberOfPackages>125</body:NumberOfPackages>
       |                <body:CommercialSealIdentification>SEAL456789321</body:CommercialSealIdentification>
       |                <body:SealInformation language="en">Red Strip</body:SealInformation>
       |              </body:Package>
       |              <body:WineProduct>
       |                <body:WineProductCategory>4</body:WineProductCategory>
       |                <body:ThirdCountryOfOrigin>FJ</body:ThirdCountryOfOrigin>
       |                <body:OtherInformation language="en">Not available</body:OtherInformation>
       |                <body:WineOperation>
       |                  <body:WineOperationCode>4</body:WineOperationCode>
       |                </body:WineOperation>
       |              </body:WineProduct>
       |            </body:BodyEadEsad>
       |            <body:BodyEadEsad>
       |              <body:BodyRecordUniqueReference>2</body:BodyRecordUniqueReference>
       |              <body:ExciseProductCode>W300</body:ExciseProductCode>
       |              <body:CnCode>27111901</body:CnCode>
       |              <body:Quantity>501</body:Quantity>
       |              <body:GrossMass>901</body:GrossMass>
       |              <body:NetMass>475</body:NetMass>
       |              <body:AlcoholicStrengthByVolumeInPercentage>12.7</body:AlcoholicStrengthByVolumeInPercentage>
       |              <body:FiscalMark language="en">FM564790 Fiscal Mark</body:FiscalMark>
       |              <body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
       |              <body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
       |              <body:SizeOfProducer>20000</body:SizeOfProducer>
       |              <body:CommercialDescription language="en">Retsina</body:CommercialDescription>
       |              <body:BrandNameOfProducts language="en">BrandName</body:BrandNameOfProducts>
       |              <body:Package>
       |                <body:KindOfPackages>GB</body:KindOfPackages>
       |                <body:NumberOfPackages>125</body:NumberOfPackages>
       |                <body:CommercialSealIdentification>SEAL456789321</body:CommercialSealIdentification>
       |                <body:SealInformation language="en">Red Strip</body:SealInformation>
       |              </body:Package>
       |              <body:WineProduct>
       |                <body:WineProductCategory>4</body:WineProductCategory>
       |                <body:ThirdCountryOfOrigin>FJ</body:ThirdCountryOfOrigin>
       |                <body:OtherInformation language="en">Not available</body:OtherInformation>
       |                <body:WineOperation>
       |                  <body:WineOperationCode>4 5</body:WineOperationCode>
       |                </body:WineOperation>
       |              </body:WineProduct>
       |            </body:BodyEadEsad>
       |            <body:TransportDetails>
       |              <body:TransportUnitCode>1</body:TransportUnitCode>
       |              <body:IdentityOfTransportUnits>Bottles</body:IdentityOfTransportUnits>
       |              <body:CommercialSealIdentification>SID13245678</body:CommercialSealIdentification>
       |              <body:ComplementaryInformation language="en">Bottles of Restina</body:ComplementaryInformation>
       |              <body:SealInformation language="en">Sealed with red strip</body:SealInformation>
       |            </body:TransportDetails>
       |            <body:TransportDetails>
       |              <body:TransportUnitCode>2</body:TransportUnitCode>
       |              <body:IdentityOfTransportUnits>Cans</body:IdentityOfTransportUnits>
       |              <body:CommercialSealIdentification>SID132987</body:CommercialSealIdentification>
       |              <body:ComplementaryInformation language="en">Cans</body:ComplementaryInformation>
       |              <body:SealInformation language="en">Seal info</body:SealInformation>
       |            </body:TransportDetails>
       |          </body:EADESADContainer>
       |        </body:Body>
       |      </body:IE801>
       |      <body:IE810 xmlns:body="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:IE810:V2.02">
       |        <body:Header xmlns:head="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:TMS:V2.02">
       |          <head:MessageSender>token</head:MessageSender>
       |          <head:MessageRecipient>token</head:MessageRecipient>
       |          <head:DateOfPreparation>1967-08-13</head:DateOfPreparation>
       |          <head:TimeOfPreparation>14:20:00</head:TimeOfPreparation>
       |          <head:MessageIdentifier>token</head:MessageIdentifier>
       |          <head:CorrelationIdentifier>token</head:CorrelationIdentifier>
       |        </body:Header>
       |        <body:Body>
       |          <body:CancellationOfEAD>
       |            <body:Attributes>
       |              <body:DateAndTimeOfValidationOfCancellation>2008-09-04T10:22:53</body:DateAndTimeOfValidationOfCancellation>
       |            </body:Attributes>
       |            <body:ExciseMovementEad>
       |              <body:AdministrativeReferenceCode>13AB7778889991ABCDEF9</body:AdministrativeReferenceCode>
       |            </body:ExciseMovementEad>
       |            <body:Cancellation>
       |              <body:CancellationReasonCode>0</body:CancellationReasonCode>
       |              <body:ComplementaryInformation>some info</body:ComplementaryInformation>
       |            </body:Cancellation>
       |          </body:CancellationOfEAD>
       |        </body:Body>
       |      </body:IE810>
       |      <body:IE802 xmlns:body="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:IE802:V2.02">
       |        <body:Header xmlns:head="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:TMS:V2.02">
       |          <head:MessageSender>token</head:MessageSender>
       |          <head:MessageRecipient>token</head:MessageRecipient>
       |          <head:DateOfPreparation>1967-08-13</head:DateOfPreparation>
       |          <head:TimeOfPreparation>14:20:00</head:TimeOfPreparation>
       |          <head:MessageIdentifier>token</head:MessageIdentifier>
       |          <head:CorrelationIdentifier>token</head:CorrelationIdentifier>
       |        </body:Header>
       |        <body:Body>
       |          <body:ReminderMessageForExciseMovement>
       |            <body:Attributes>
       |              <body:DateAndTimeOfIssuanceOfReminder>2008-09-04T10:22:53</body:DateAndTimeOfIssuanceOfReminder>
       |              <body:ReminderInformation language="en">To be completed by this date</body:ReminderInformation>
       |              <body:LimitDateAndTime>2008-09-04T10:22:53</body:LimitDateAndTime>
       |              <body:ReminderMessageType>1</body:ReminderMessageType>
       |            </body:Attributes>
       |            <body:ExciseMovement>
       |              <body:AdministrativeReferenceCode>13AB7778889991ABCDEF9</body:AdministrativeReferenceCode>
       |              <body:SequenceNumber>0</body:SequenceNumber>
       |            </body:ExciseMovement>
       |          </body:ReminderMessageForExciseMovement>
       |        </body:Body>
       |      </body:IE802>
       |      <urn:IE803 xmlns:ie803="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE803:V3.13">
       |        <urn:Header>
       |          <urn:MessageSender>NDEA.GB</urn:MessageSender>
       |          <urn:MessageRecipient>NDEA.GB</urn:MessageRecipient>
       |          <urn:DateOfPreparation>2020-12-03</urn:DateOfPreparation>
       |          <urn:TimeOfPreparation>13:36:43.326</urn:TimeOfPreparation>
       |          <urn:MessageIdentifier>GB100000000289576</urn:MessageIdentifier>
       |        </urn:Header>
       |        <urn:Body>
       |          <urn:NotificationOfDivertedEADESAD>
       |            <urn:ExciseNotification>
       |              <urn:NotificationType>2</urn:NotificationType>
       |              <urn:NotificationDateAndTime>2024-06-05T00:00:01</urn:NotificationDateAndTime>
       |              <urn:AdministrativeReferenceCode>20GB00000000000341760</urn:AdministrativeReferenceCode>
       |              <urn:SequenceNumber>1</urn:SequenceNumber>
       |            </urn:ExciseNotification>
       |            <urn:DownstreamArc>
       |              <urn:AdministrativeReferenceCode>$testArc</urn:AdministrativeReferenceCode>
       |            </urn:DownstreamArc>
       |            <urn:DownstreamArc>
       |              <urn:AdministrativeReferenceCode>${testArc.dropRight(1)}1</urn:AdministrativeReferenceCode>
       |            </urn:DownstreamArc>
       |          </urn:NotificationOfDivertedEADESAD>
       |        </urn:Body>
       |      </urn:IE803>
       |      <body:IE837 xmlns:body="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:IE837:V2.02">
       |        <body:Header xmlns:head="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:TMS:V2.02">
       |          <head:MessageSender>token</head:MessageSender>
       |          <head:MessageRecipient>token</head:MessageRecipient>
       |          <head:DateOfPreparation>1967-08-13</head:DateOfPreparation>
       |          <head:TimeOfPreparation>14:20:00</head:TimeOfPreparation>
       |          <head:MessageIdentifier>token</head:MessageIdentifier>
       |          <head:CorrelationIdentifier>token</head:CorrelationIdentifier>
       |        </body:Header>
       |        <body:Body>
       |          <body:ExplanationOnDelayForDelivery>
       |            <body:Attributes>
       |              <body:SubmitterIdentification>837Submitter</body:SubmitterIdentification>
       |              <body:SubmitterType>1</body:SubmitterType>
       |              <body:ExplanationCode>1</body:ExplanationCode>
       |              <body:ComplementaryInformation language="to">837 complementary info</body:ComplementaryInformation>
       |              <body:MessageRole>1</body:MessageRole>
       |              <body:DateAndTimeOfValidationOfExplanationOnDelay>2001-12-17T09:30:47.00</body:DateAndTimeOfValidationOfExplanationOnDelay>
       |            </body:Attributes>
       |            <body:ExciseMovement>
       |              <body:AdministrativeReferenceCode>13AB1234567891ABCDEF9</body:AdministrativeReferenceCode>
       |              <body:SequenceNumber/>
       |            </body:ExciseMovement>
       |          </body:ExplanationOnDelayForDelivery>
       |        </body:Body>
       |      </body:IE837>
       |      <urn:IE818 xmlns:urn="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE818:V3.01">
       |      <urn:Header>
       |        <urn1:MessageSender xmlns:urn1="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:TMS:V3.01">NDEA.XI</urn1:MessageSender>
       |        <urn1:MessageRecipient xmlns:urn1="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:TMS:V3.01">NDEA.GB</urn1:MessageRecipient>
       |        <urn1:DateOfPreparation xmlns:urn1="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:TMS:V3.01">2021-09-10</urn1:DateOfPreparation>
       |        <urn1:TimeOfPreparation xmlns:urn1="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:TMS:V3.01">11:11:09</urn1:TimeOfPreparation>
       |        <urn1:MessageIdentifier xmlns:urn1="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:TMS:V3.01">XI100000000291919</urn1:MessageIdentifier>
       |        <urn1:CorrelationIdentifier xmlns:urn1="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:TMS:V3.01">PORTAL5a1b930650c54fbca85cf509add5182e</urn1:CorrelationIdentifier>
       |      </urn:Header>
       |      <urn:Body>
       |        <urn:AcceptedOrRejectedReportOfReceiptExport>
       |          <urn:Attributes>
       |            <urn:DateAndTimeOfValidationOfReportOfReceiptExport>2021-09-10T11:11:12</urn:DateAndTimeOfValidationOfReportOfReceiptExport>
       |          </urn:Attributes>
       |          <urn:ConsigneeTrader language="en">
       |            <urn:Traderid>XIWK000000206</urn:Traderid>
       |            <urn:TraderName>SEED TRADER NI</urn:TraderName>
       |            <urn:StreetName>Catherdral</urn:StreetName>
       |            <urn:StreetNumber>1</urn:StreetNumber>
       |            <urn:Postcode>BT3 7BF</urn:Postcode>
       |            <urn:City>Salford</urn:City>
       |          </urn:ConsigneeTrader>
       |          <ie:ExciseMovement xmlns:ie="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE818:V3.01">
       |            <urn:AdministrativeReferenceCode>13AB7778889991ABCDEF9</urn:AdministrativeReferenceCode>
       |            <urn:SequenceNumber>2</urn:SequenceNumber>
       |          </ie:ExciseMovement>
       |          <urn:DeliveryPlaceTrader language="en">
       |            <urn:Traderid>XI00000000207</urn:Traderid>
       |            <urn:TraderName>SEED TRADER NI 2</urn:TraderName>
       |            <urn:StreetNumber>2</urn:StreetNumber>
       |            <urn:StreetName>Catherdral</urn:StreetName>
       |            <urn:Postcode>BT3 7BF</urn:Postcode>
       |            <urn:City>Salford</urn:City>
       |          </urn:DeliveryPlaceTrader>
       |          <urn:DestinationOffice>
       |            <urn:ReferenceNumber>XI004098</urn:ReferenceNumber>
       |          </urn:DestinationOffice>
       |          <urn:ReportOfReceiptExport>
       |            <urn:DateOfArrivalOfExciseProducts>2021-09-08</urn:DateOfArrivalOfExciseProducts>
       |            <urn:GlobalConclusionOfReceipt>1</urn:GlobalConclusionOfReceipt>
       |          </urn:ReportOfReceiptExport>
       |        </urn:AcceptedOrRejectedReportOfReceiptExport>
       |      </urn:Body>
       |    </urn:IE818>
       |    <!-- Alert Event -->
       |      <ie819:IE819 xmlns:ie819="ie819:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE819:V3.13">
       |        <ie819:Header>
       |          <urn:MessageSender>NDEA.GB</urn:MessageSender>
       |          <urn:MessageRecipient>NDEA.GB</urn:MessageRecipient>
       |          <urn:DateOfPreparation>2023-12-18</urn:DateOfPreparation>
       |          <urn:TimeOfPreparation>08:59:59.441503</urn:TimeOfPreparation>
       |          <urn:MessageIdentifier>9de3f13e-7559-4f4d-8851-b954b01210c0</urn:MessageIdentifier>
       |          <urn:CorrelationIdentifier>e8803427-c7e5-4539-83b7-d174f511e70c</urn:CorrelationIdentifier>
       |        </ie819:Header>
       |        <ie819:Body>
       |          <ie819:AlertOrRejectionOfEADESAD>
       |            <ie819:Attributes>
       |              <ie819:DateAndTimeOfValidationOfAlertRejection>2023-12-18T09:00:00</ie819:DateAndTimeOfValidationOfAlertRejection>
       |            </ie819:Attributes>
       |            <ie819:ConsigneeTrader language="en">
       |              <ie819:Traderid>GBWK123456789</ie819:Traderid>
       |              <ie819:TraderName>Bizz</ie819:TraderName>
       |              <ie819:StreetName>GRANGE CENTRAL</ie819:StreetName>
       |              <ie819:Postcode>tf3 4er</ie819:Postcode>
       |              <ie819:City>Shropshire</ie819:City>
       |            </ie819:ConsigneeTrader>
       |            <ie819:ExciseMovement>
       |              <ie819:AdministrativeReferenceCode>18GB00000000000232361</ie819:AdministrativeReferenceCode>
       |              <ie819:SequenceNumber>1</ie819:SequenceNumber>
       |            </ie819:ExciseMovement>
       |            <ie819:DestinationOffice>
       |              <ie819:ReferenceNumber>GB004098</ie819:ReferenceNumber>
       |            </ie819:DestinationOffice>
       |            <ie819:AlertOrRejection>
       |              <ie819:DateOfAlertOrRejection>2023-12-18</ie819:DateOfAlertOrRejection>
       |              <ie819:EadEsadRejectedFlag>0</ie819:EadEsadRejectedFlag>
       |            </ie819:AlertOrRejection>
       |            <ie819:AlertOrRejectionOfEadEsadReason>
       |              <ie819:AlertOrRejectionOfMovementReasonCode>2</ie819:AlertOrRejectionOfMovementReasonCode>
       |              <ie819:ComplementaryInformation>Info</ie819:ComplementaryInformation>
       |            </ie819:AlertOrRejectionOfEadEsadReason>
       |            <ie819:AlertOrRejectionOfEadEsadReason>
       |              <ie819:AlertOrRejectionOfMovementReasonCode>1</ie819:AlertOrRejectionOfMovementReasonCode>
       |              <ie819:ComplementaryInformation>Info</ie819:ComplementaryInformation>
       |            </ie819:AlertOrRejectionOfEadEsadReason>
       |            <ie819:AlertOrRejectionOfEadEsadReason>
       |              <ie819:AlertOrRejectionOfMovementReasonCode>0</ie819:AlertOrRejectionOfMovementReasonCode>
       |              <ie819:ComplementaryInformation>Info</ie819:ComplementaryInformation>
       |            </ie819:AlertOrRejectionOfEadEsadReason>
       |            <ie819:AlertOrRejectionOfEadEsadReason>
       |              <ie819:AlertOrRejectionOfMovementReasonCode>3</ie819:AlertOrRejectionOfMovementReasonCode>
       |              <ie819:ComplementaryInformation>Info</ie819:ComplementaryInformation>
       |            </ie819:AlertOrRejectionOfEadEsadReason>
       |          </ie819:AlertOrRejectionOfEADESAD>
       |        </ie819:Body>
       |      </ie819:IE819>
       |
       |      <!-- 2nd Alert Event -->
       |      <ie819:IE819 xmlns:ie819="ie819:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE819:V3.13">
       |        <ie819:Header>
       |          <urn:MessageSender>NDEA.GB</urn:MessageSender>
       |          <urn:MessageRecipient>NDEA.GB</urn:MessageRecipient>
       |          <urn:DateOfPreparation>2023-12-18</urn:DateOfPreparation>
       |          <urn:TimeOfPreparation>09:59:59.441503</urn:TimeOfPreparation>
       |          <urn:MessageIdentifier>9de3f13e-7559-4f4d-8851-b954b01210c0</urn:MessageIdentifier>
       |          <urn:CorrelationIdentifier>e8803427-c7e5-4539-83b7-d174f511e70c</urn:CorrelationIdentifier>
       |        </ie819:Header>
       |        <ie819:Body>
       |          <ie819:AlertOrRejectionOfEADESAD>
       |            <ie819:Attributes>
       |              <ie819:DateAndTimeOfValidationOfAlertRejection>2023-12-18T10:00:00</ie819:DateAndTimeOfValidationOfAlertRejection>
       |            </ie819:Attributes>
       |            <ie819:ConsigneeTrader language="en">
       |              <ie819:Traderid>GBWK123456789</ie819:Traderid>
       |              <ie819:TraderName>Bizz</ie819:TraderName>
       |              <ie819:StreetName>GRANGE CENTRAL</ie819:StreetName>
       |              <ie819:Postcode>tf3 4er</ie819:Postcode>
       |              <ie819:City>Shropshire</ie819:City>
       |            </ie819:ConsigneeTrader>
       |            <ie819:ExciseMovement>
       |              <ie819:AdministrativeReferenceCode>18GB00000000000232361</ie819:AdministrativeReferenceCode>
       |              <ie819:SequenceNumber>1</ie819:SequenceNumber>
       |            </ie819:ExciseMovement>
       |            <ie819:DestinationOffice>
       |              <ie819:ReferenceNumber>GB004098</ie819:ReferenceNumber>
       |            </ie819:DestinationOffice>
       |            <ie819:AlertOrRejection>
       |              <ie819:DateOfAlertOrRejection>2023-12-18</ie819:DateOfAlertOrRejection>
       |              <ie819:EadEsadRejectedFlag>0</ie819:EadEsadRejectedFlag>
       |            </ie819:AlertOrRejection>
       |            <ie819:AlertOrRejectionOfEadEsadReason>
       |              <ie819:AlertOrRejectionOfMovementReasonCode>1</ie819:AlertOrRejectionOfMovementReasonCode>
       |            </ie819:AlertOrRejectionOfEadEsadReason>
       |          </ie819:AlertOrRejectionOfEADESAD>
       |        </ie819:Body>
       |      </ie819:IE819>
       |
       |      <!-- Rejection Event -->
       |      <ie819:IE819 xmlns:ie819="ie819:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE819:V3.13">
       |        <ie819:Header>
       |          <urn:MessageSender>NDEA.GB</urn:MessageSender>
       |          <urn:MessageRecipient>NDEA.GB</urn:MessageRecipient>
       |          <urn:DateOfPreparation>2023-12-19</urn:DateOfPreparation>
       |          <urn:TimeOfPreparation>08:59:59.441503</urn:TimeOfPreparation>
       |          <urn:MessageIdentifier>9de3f13e-7559-4f4d-8851-b954b01210c0</urn:MessageIdentifier>
       |          <urn:CorrelationIdentifier>e8803427-c7e5-4539-83b7-d174f511e70c</urn:CorrelationIdentifier>
       |        </ie819:Header>
       |        <ie819:Body>
       |          <ie819:AlertOrRejectionOfEADESAD>
       |            <ie819:Attributes>
       |              <ie819:DateAndTimeOfValidationOfAlertRejection>2023-12-19T09:00:00</ie819:DateAndTimeOfValidationOfAlertRejection>
       |            </ie819:Attributes>
       |            <ie819:ConsigneeTrader language="en">
       |              <ie819:Traderid>GBWK123456789</ie819:Traderid>
       |              <ie819:TraderName>Bizz</ie819:TraderName>
       |              <ie819:StreetName>GRANGE CENTRAL</ie819:StreetName>
       |              <ie819:Postcode>tf3 4er</ie819:Postcode>
       |              <ie819:City>Shropshire</ie819:City>
       |            </ie819:ConsigneeTrader>
       |            <ie819:ExciseMovement>
       |              <ie819:AdministrativeReferenceCode>18GB00000000000232361</ie819:AdministrativeReferenceCode>
       |              <ie819:SequenceNumber>1</ie819:SequenceNumber>
       |            </ie819:ExciseMovement>
       |            <ie819:DestinationOffice>
       |              <ie819:ReferenceNumber>GB004098</ie819:ReferenceNumber>
       |            </ie819:DestinationOffice>
       |            <ie819:AlertOrRejection>
       |              <ie819:DateOfAlertOrRejection>2023-12-18</ie819:DateOfAlertOrRejection>
       |              <ie819:EadEsadRejectedFlag>1</ie819:EadEsadRejectedFlag>
       |            </ie819:AlertOrRejection>
       |            <ie819:AlertOrRejectionOfEadEsadReason>
       |              <ie819:AlertOrRejectionOfMovementReasonCode>3</ie819:AlertOrRejectionOfMovementReasonCode>
       |            </ie819:AlertOrRejectionOfEadEsadReason>
       |          </ie819:AlertOrRejectionOfEADESAD>
       |        </ie819:Body>
       |      </ie819:IE819>
       |
       |      <!-- Movement accepted by customs -->
       |      <ie829:IE829 xmlns:ie829="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE829:V3.13">
       |        <ie829:Header>
       |          <urn:MessageSender>NDEA.GB</urn:MessageSender>
       |          <urn:MessageRecipient>NDEA.GB</urn:MessageRecipient>
       |          <urn:DateOfPreparation>2023-12-19</urn:DateOfPreparation>
       |          <urn:TimeOfPreparation>08:59:59.441503</urn:TimeOfPreparation>
       |          <urn:MessageIdentifier>9de3f13e-7559-4f4d-8851-b954b01210c0</urn:MessageIdentifier>
       |          <urn:CorrelationIdentifier>e8803427-c7e5-4539-83b7-d174f511e70c</urn:CorrelationIdentifier>
       |        </ie829:Header>
       |        <ie829:Body>
       |          <ie829:NotificationOfAcceptedExport>
       |            <ie829:Attributes>
       |              <ie829:DateAndTimeOfIssuance>2023-12-19T09:00:00</ie829:DateAndTimeOfIssuance>
       |            </ie829:Attributes>
       |            <ie829:ConsigneeTrader>
       |              <ie829:Traderid>BE345345345</ie829:Traderid>
       |              <ie829:TraderName>PEAR Supermarket</ie829:TraderName>
       |              <ie829:StreetName>Angels Business Park</ie829:StreetName>
       |              <ie829:Postcode>BD1 3NN</ie829:Postcode>
       |              <ie829:City>Bradford</ie829:City>
       |              <ie829:EoriNumber>GB00000578901</ie829:EoriNumber>
       |            </ie829:ConsigneeTrader>
       |            <ie829:ExciseMovementEad>
       |              <ie829:AdministrativeReferenceCode>18GB00000000000232361</ie829:AdministrativeReferenceCode>
       |              <ie829:SequenceNumber>1</ie829:SequenceNumber>
       |              <ie829:ExportDeclarationAcceptanceOrGoodsReleasedForExport>1</ie829:ExportDeclarationAcceptanceOrGoodsReleasedForExport>
       |            </ie829:ExciseMovementEad>
       |            <ie829:ExportPlaceCustomsOffice>
       |              <ie829:ReferenceNumber>GB000383</ie829:ReferenceNumber>
       |            </ie829:ExportPlaceCustomsOffice>
       |            <ie829:ExportDeclarationAcceptanceRelease>
       |              <ie829:ReferenceNumberOfSenderCustomsOffice>GB000101</ie829:ReferenceNumberOfSenderCustomsOffice>
       |              <ie829:IdentificationOfSenderCustomsOfficer>John Doe</ie829:IdentificationOfSenderCustomsOfficer>
       |              <ie829:DateOfAcceptance>2024-02-05</ie829:DateOfAcceptance>
       |              <ie829:DateOfRelease>2024-02-06</ie829:DateOfRelease>
       |              <ie829:DocumentReferenceNumber>645564546</ie829:DocumentReferenceNumber>
       |            </ie829:ExportDeclarationAcceptanceRelease>
       |          </ie829:NotificationOfAcceptedExport>
       |        </ie829:Body>
       |      </ie829:IE829>
       |
       |
       |      <!-- Explanation of Delay to Report a Receipt (IE837) -->
       |      <ie837:IE837 xmlns:ie837="ie837:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE837:V3.13">
       |         <ie837:Header>
       |            <urn:MessageSender>NDEA.GB</urn:MessageSender>
       |            <urn:MessageRecipient>NDEA.GB</urn:MessageRecipient>
       |            <urn:DateOfPreparation>2024-06-18</urn:DateOfPreparation>
       |            <urn:TimeOfPreparation>07:11:31.898476</urn:TimeOfPreparation>
       |            <urn:MessageIdentifier>GB100000000305526</urn:MessageIdentifier>
       |            <urn:CorrelationIdentifier>PORTALb32df82fde8741b4beb3fb832a9cdb76</urn:CorrelationIdentifier>
       |         </ie837:Header>
       |         <ie837:Body>
       |            <ie837:ExplanationOnDelayForDelivery>
       |               <ie837:Attributes>
       |                  <ie837:SubmitterIdentification>GBWK001234569</ie837:SubmitterIdentification>
       |                  <ie837:SubmitterType>1</ie837:SubmitterType>
       |                  <ie837:ExplanationCode>6</ie837:ExplanationCode>
       |                  <ie837:ComplementaryInformation language="en">Lorry crashed off cliff</ie837:ComplementaryInformation>
       |                  <ie837:MessageRole>1</ie837:MessageRole>
       |                  <ie837:DateAndTimeOfValidationOfExplanationOnDelay>2024-06-18T08:11:33</ie837:DateAndTimeOfValidationOfExplanationOnDelay>
       |               </ie837:Attributes>
       |               <ie837:ExciseMovement>
       |                  <ie837:AdministrativeReferenceCode>18GB00000000000232361</ie837:AdministrativeReferenceCode>
       |                  <ie837:SequenceNumber>1</ie837:SequenceNumber>
       |               </ie837:ExciseMovement>
       |            </ie837:ExplanationOnDelayForDelivery>
       |         </ie837:Body>
       |      </ie837:IE837>
       |
       |      <!-- Explanation of Delay to Change of Destination (IE837) -->
       |      <ie837:IE837 xmlns:ie837="ie837:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE837:V3.13">
       |         <ie837:Header>
       |            <urn:MessageSender>NDEA.GB</urn:MessageSender>
       |            <urn:MessageRecipient>NDEA.GB</urn:MessageRecipient>
       |            <urn:DateOfPreparation>2024-06-18</urn:DateOfPreparation>
       |            <urn:TimeOfPreparation>07:18:54.852159</urn:TimeOfPreparation>
       |            <urn:MessageIdentifier>GB100000000305527</urn:MessageIdentifier>
       |            <urn:CorrelationIdentifier>PORTAL07498cf951004becbc3c73c14c103b13</urn:CorrelationIdentifier>
       |         </ie837:Header>
       |         <ie837:Body>
       |            <ie837:ExplanationOnDelayForDelivery>
       |               <ie837:Attributes>
       |                  <ie837:SubmitterIdentification>GBWK001234569</ie837:SubmitterIdentification>
       |                  <ie837:SubmitterType>1</ie837:SubmitterType>
       |                  <ie837:ExplanationCode>5</ie837:ExplanationCode>
       |                  <ie837:MessageRole>2</ie837:MessageRole>
       |                  <ie837:DateAndTimeOfValidationOfExplanationOnDelay>2024-06-18T08:18:56</ie837:DateAndTimeOfValidationOfExplanationOnDelay>
       |               </ie837:Attributes>
       |               <ie837:ExciseMovement>
       |                  <ie837:AdministrativeReferenceCode>18GB00000000000232361</ie837:AdministrativeReferenceCode>
       |                  <ie837:SequenceNumber>1</ie837:SequenceNumber>
       |               </ie837:ExciseMovement>
       |            </ie837:ExplanationOnDelayForDelivery>
       |         </ie837:Body>
       |      </ie837:IE837>
       |    </mov:eventHistory>
       |  </mov:movementView>""".stripMargin

  def getMovementSoapWrapper(sequenceNumber: Int = 1): String = s"""<tns:Envelope
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
                                                                   |						<![CDATA[${getMovementResponseBody(sequenceNumber)}]]>
                                                                   |					</con:Result>
                                                                   |				</con:Results>
                                                                   |			</con:OperationResponse>
                                                                   |		</con:Control>
                                                                   |	</tns:Body>
                                                                   |</tns:Envelope>""".stripMargin

  def getMovementResponse(journeyTimeValue: String = "20 days", sequenceNumber: Int = 1): GetMovementResponse = GetMovementResponse(
    arc = "13AB7778889991ABCDEF9",
    sequenceNumber,
    destinationType = Export,
    memberStateCode = Some("GB"),
    serialNumberOfCertificateOfExemption = None,
    consignorTrader = maxTraderModel(ConsignorTrader),
    consigneeTrader = Some(maxTraderModel(ConsigneeTrader)),
    deliveryPlaceTrader = None,
    placeOfDispatchTrader = Some(maxTraderModel(PlaceOfDispatchTrader)),
    transportArrangerTrader = None,
    firstTransporterTrader = Some(maxTraderModel(TransportTrader)),
    dispatchImportOfficeReferenceNumber = None,
    deliveryPlaceCustomsOfficeReferenceNumber = Some("FR000003"),
    competentAuthorityDispatchOfficeReferenceNumber = Some("GB000002"),
    localReferenceNumber = "EN",
    eadStatus = "Accepted",
    dateAndTimeOfValidationOfEadEsad = "2008-09-04T10:22:50",
    dateOfDispatch = "2008-11-20",
    journeyTime = journeyTimeValue,
    documentCertificate = Some(
      Seq(
        DocumentCertificateModel(
          documentType = None,
          documentReference = None,
          documentDescription = Some("Test"),
          referenceOfDocument = Some("AB123")
        )
      )),
    eadEsad = EadEsadModel(
      localReferenceNumber = "EN",
      invoiceNumber = "IN777888999",
      invoiceDate = Some("2008-09-04"),
      originTypeCode = OriginType.TaxWarehouse,
      dateOfDispatch = "2008-11-20",
      timeOfDispatch = Some("10:00:00"),
      upstreamArc = None,
      importSadNumber = None
    ),
    headerEadEsad = HeaderEadEsadModel(
      sequenceNumber = 1,
      dateAndTimeOfUpdateValidation = "2008-09-04T10:22:50",
      destinationType = DestinationType.Export,
      journeyTime = journeyTimeValue,
      transportArrangement = TransportArrangement.Consignor
    ),
    transportMode = TransportModeModel(
      transportModeCode = "1",
      complementaryInformation = None
    ),
    movementGuarantee = MovementGuaranteeModel(
      guarantorTypeCode = GuarantorType.GuarantorNotRequired,
      guarantorTrader = None
    ),
    items = Seq(
      MovementItem(
        itemUniqueReference = 1,
        productCode = "W200",
        cnCode = "22041011",
        quantity = BigDecimal(500),
        grossMass = BigDecimal(900),
        netMass = BigDecimal(375),
        alcoholicStrength = None,
        degreePlato = Some(1.2),
        fiscalMark = Some("FM564789 Fiscal Mark"),
        fiscalMarkUsedFlag = Some(true),
        designationOfOrigin = Some("Designation of Origin"),
        sizeOfProducer = Some("20000"),
        density = Some(880),
        commercialDescription = Some("Retsina"),
        brandNameOfProduct = Some("MALAMATINA"),
        maturationAge = Some("Maturation Period"),
        independentSmallProducersDeclaration = Some("Independent Small Producers Declaration"),
        packaging = Seq(
          Packaging(
            typeOfPackage = "BO",
            quantity = Some(125),
            shippingMarks = Some("MARKS"),
            identityOfCommercialSeal = Some("SEAL456789321"),
            sealInformation = Some("Red Strip")
          )
        ),
        wineProduct = Some(
          WineProduct(
            wineProductCategory = "4",
            wineGrowingZoneCode = Some("2"),
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
        fiscalMarkUsedFlag = Some(true),
        designationOfOrigin = Some("Designation of Origin"),
        sizeOfProducer = Some("20000"),
        density = None,
        commercialDescription = Some("Retsina"),
        brandNameOfProduct = Some("BrandName"),
        maturationAge = None,
        independentSmallProducersDeclaration = None,
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
    numberOfItems = 2,
    transportDetails = Seq(
      TransportDetailsModel(
        transportUnitCode = "1",
        identityOfTransportUnits = Some("Bottles"),
        commercialSealIdentification = Some("SID13245678"),
        complementaryInformation = Some("Bottles of Restina"),
        sealInformation = Some("Sealed with red strip")
      ),
      TransportDetailsModel(
        transportUnitCode = "2",
        identityOfTransportUnits = Some("Cans"),
        commercialSealIdentification = Some("SID132987"),
        complementaryInformation = Some("Cans"),
        sealInformation = Some("Seal")
      )
    ),
    movementViewHistoryAndExtraData = MovementViewHistoryAndExtraDataModel(
      arc = "13AB7778889991ABCDEF9",
      serialNumberOfCertificateOfExemption = None,
      dispatchImportOfficeReferenceNumber = None,
      deliveryPlaceCustomsOfficeReferenceNumber = Some("FR000003"),
      competentAuthorityDispatchOfficeReferenceNumber = Some("GB000002"),
      eadStatus = "Accepted",
      dateAndTimeOfValidationOfEadEsad = "2008-09-04T10:22:50",
      numberOfItems = 2,
      reportOfReceipt = Some(SubmitReportOfReceiptModel(
        arc = "13AB7778889991ABCDEF9",
        sequenceNumber = 2,
        dateAndTimeOfValidationOfReportOfReceiptExport = Some("2021-09-10T11:11:12"),
        consigneeTrader = Some(
          TraderModel(
            traderExciseNumber = Some("XIWK000000206"),
            traderName = Some("SEED TRADER NI"),
            address = Some(
              AddressModel(
                streetNumber = Some("1"),
                street = Some("Catherdral"),
                postcode = Some("BT3 7BF"),
                city = Some("Salford")
              )),
            vatNumber = None,
            eoriNumber = None
          )
        ),
        deliveryPlaceTrader = Some(
          TraderModel(
            traderExciseNumber = Some("XI00000000207"),
            traderName = Some("SEED TRADER NI 2"),
            address = Some(
              AddressModel(
                streetNumber = Some("2"),
                street = Some("Catherdral"),
                postcode = Some("BT3 7BF"),
                city = Some("Salford")
              )),
            vatNumber = None,
            eoriNumber = None
          )
        ),
        destinationOffice = "XI004098",
        dateOfArrival = LocalDate.parse("2021-09-08"),
        otherInformation = None,
        individualItems = Seq.empty,
        destinationType = None,
        acceptMovement = Satisfactory
      )),
      notificationOfDivertedMovement = Some(NotificationOfDivertedMovementModel(
        notificationType = SplitMovement,
        notificationDateAndTime = LocalDateTime.of(2024, 6, 5, 0, 0, 1),
        downstreamArcs = Seq(testArc, s"${testArc.dropRight(1)}1")
      )),
      notificationOfAlertOrRejection = Seq(
        NotificationOfAlertOrRejectionModel(
          notificationType = Alert,
          notificationDateAndTime = LocalDateTime.of(2023, 12, 18, 9, 0, 0),
          alertRejectReason = Seq(
            AlertOrRejectionReasonModel(
              reason = ProductDoesNotMatchOrder,
              additionalInformation = Some("Info")
            ),
            AlertOrRejectionReasonModel(
              reason = EADNotConcernRecipient,
              additionalInformation = Some("Info")
            ),
            AlertOrRejectionReasonModel(
              reason = Other,
              additionalInformation = Some("Info")
            ),
            AlertOrRejectionReasonModel(
              reason = QuantityDoesNotMatchOrder,
              additionalInformation = Some("Info")
            )
          )
        ),
        NotificationOfAlertOrRejectionModel(
          notificationType = Alert,
          notificationDateAndTime = LocalDateTime.of(2023, 12, 18, 10, 0, 0),
          alertRejectReason = Seq(AlertOrRejectionReasonModel(
            reason = EADNotConcernRecipient,
            additionalInformation = None
          ))
        ),
        NotificationOfAlertOrRejectionModel(
          notificationType = Rejection,
          notificationDateAndTime = LocalDateTime.of(2023, 12, 19, 9, 0, 0),
          alertRejectReason = Seq(AlertOrRejectionReasonModel(
            reason = QuantityDoesNotMatchOrder,
            additionalInformation = None
          ))
        )
      ),
      notificationOfAcceptedExport = Some(
        NotificationOfAcceptedExportModel(
          customsOfficeNumber = "GB000383",
          dateOfAcceptance = LocalDate.of(2024, 2, 5),
          referenceNumberOfSenderCustomsOffice = "GB000101",
          identificationOfSenderCustomsOfficer = "John Doe",
          documentReferenceNumber = "645564546",
          consigneeTrader = TraderModel(
            traderExciseNumber = Some("BE345345345"),
            traderName = Some("PEAR Supermarket"),
            address = Some(
              AddressModel(
                streetNumber = None,
                street = Some("Angels Business Park"),
                postcode = Some("BD1 3NN"),
                city = Some("Bradford")
              )),
            vatNumber = None,
            eoriNumber = Some("GB00000578901")
          )
        )
      ),
      notificationOfDelay = Seq(
        NotificationOfDelayModel(
          submitterIdentification = "837Submitter",
          submitterType = SubmitterType.Consignor,
          explanationCode = DelayReasonType.CancelledCommercialTransaction,
          complementaryInformation = Some("837 complementary info"),
          dateTime = LocalDateTime.parse("2001-12-17T09:30:47")
        ),
        NotificationOfDelayModel(
          submitterIdentification = "GBWK001234569",
          submitterType = SubmitterType.Consignor,
          explanationCode = DelayReasonType.Accident,
          complementaryInformation = Some("Lorry crashed off cliff"),
          dateTime = LocalDateTime.parse("2024-06-18T08:11:33")
        ),
        NotificationOfDelayModel(
          submitterIdentification = "GBWK001234569",
          submitterType = SubmitterType.Consignor,
          explanationCode = DelayReasonType.Strikes,
          complementaryInformation = None,
          dateTime = LocalDateTime.parse("2024-06-18T08:18:56")
        )
      ),
      cancelMovement = Some(CancellationReasonModel(CancellationReasonType.Other, Some("some info")))
    )
  )

  def getRawMovementResponse(sequenceNumber: Int = 1): RawGetMovementResponse = RawGetMovementResponse("dateTime", testErn, XML.loadString(getMovementResponseBody(sequenceNumber)))

  def getRawMovementJson(sequenceNumber: Int = 1): JsValue = Json.obj("dateTime" -> "dateTime", "exciseRegistrationNumber" -> testErn, "message" -> Base64.getEncoder.encodeToString(getMovementResponseBody(sequenceNumber).getBytes))
  def getRawMovementInvalidJson(sequenceNumber: Int = 1): JsValue = Json.obj("dateTime" -> "dateTime", "exciseRegistrationNumber" -> testErn, "message" -> getMovementResponseBody(sequenceNumber))

  def getMovementJson(sequenceNumber: Int = 1): JsValue = Json.obj(
    fields = "arc" -> "13AB7778889991ABCDEF9",
    "sequenceNumber"                                  -> sequenceNumber,
    "destinationType"                                 -> "6",
    "memberStateCode"                                 -> "GB",
    "consignorTrader"                                 -> maxTraderModelJson(ConsignorTrader),
    "consigneeTrader"                                 -> maxTraderModelJson(ConsigneeTrader),
    "placeOfDispatchTrader"                           -> maxTraderModelJson(PlaceOfDispatchTrader),
    "firstTransporterTrader"                          -> maxTraderModelJson(TransportTrader),
    "deliveryPlaceCustomsOfficeReferenceNumber"       -> "FR000003",
    "competentAuthorityDispatchOfficeReferenceNumber" -> "GB000002",
    "localReferenceNumber"                            -> "EN",
    "eadStatus"                                       -> "Accepted",
    "dateAndTimeOfValidationOfEadEsad"                -> "2008-09-04T10:22:50",
    "dateOfDispatch"                                  -> "2008-11-20",
    "journeyTime"                                     -> "20 days",
    "documentCertificate" -> Json.arr(
      Json.obj(
        "documentDescription" -> "Test",
        "referenceOfDocument" -> "AB123"
      )
    ),
    "eadEsad" -> Json.obj(
      "localReferenceNumber" -> "EN",
      "invoiceNumber"        -> "IN777888999",
      "invoiceDate"          -> "2008-09-04",
      "originTypeCode"       -> "1",
      "dateOfDispatch"       -> "2008-11-20",
      "timeOfDispatch"       -> "10:00:00"
    ),
    "headerEadEsad" -> Json.obj(
      "sequenceNumber"                -> 1,
      "dateAndTimeOfUpdateValidation" -> "2008-09-04T10:22:50",
      "destinationType"               -> "6",
      "journeyTime"                   -> "20 days",
      "transportArrangement"          -> "1"
    ),
    "transportMode" -> Json.obj(
      "transportModeCode" -> "1"
    ),
    "movementGuarantee" -> Json.obj(
      "guarantorTypeCode" -> "0"
    ),
    "items" -> Json.arr(
      Json.obj(
        fields = "itemUniqueReference" -> 1,
        "productCode"           -> "W200",
        "cnCode"                -> "22041011",
        "quantity"              -> 500,
        "grossMass"             -> 900,
        "netMass"               -> 375,
        "degreePlato"           -> 1.2,
        "fiscalMark"            -> "FM564789 Fiscal Mark",
        "fiscalMarkUsedFlag"    -> true,
        "designationOfOrigin"   -> "Designation of Origin",
        "sizeOfProducer"        -> "20000",
        "density"               -> 880,
        "commercialDescription" -> "Retsina",
        "brandNameOfProduct"    -> "MALAMATINA",
        "maturationAge"         -> "Maturation Period",
        "independentSmallProducersDeclaration" -> "Independent Small Producers Declaration",
        "packaging" -> Json.arr(
          Json.obj(fields = "typeOfPackage" -> "BO", "quantity" -> 125, "shippingMarks" -> "MARKS", "identityOfCommercialSeal" -> "SEAL456789321", "sealInformation" -> "Red Strip")
        ),
        "wineProduct" -> Json.obj(
          "wineProductCategory"  -> "4",
          "wineGrowingZoneCode"  -> "2",
          "thirdCountryOfOrigin" -> "FJ",
          "otherInformation"     -> "Not available",
          "wineOperations"       -> Json.arr("4", "5")
        )
      ),
      Json.obj(
        fields = "itemUniqueReference" -> 2,
        "productCode"           -> "W300",
        "cnCode"                -> "27111901",
        "quantity"              -> 501,
        "grossMass"             -> 901,
        "netMass"               -> 475,
        "alcoholicStrength"     -> 12.7,
        "fiscalMark"            -> "FM564790 Fiscal Mark",
        "fiscalMarkUsedFlag"    -> true,
        "designationOfOrigin"   -> "Designation of Origin",
        "sizeOfProducer"        -> "20000",
        "commercialDescription" -> "Retsina",
        "brandNameOfProduct"    -> "BrandName",
        "packaging" -> Json.arr(
          Json.obj(fields = "typeOfPackage" -> "BO", "quantity" -> 125, "identityOfCommercialSeal" -> "SEAL456789321", "sealInformation" -> "Red Strip"),
          Json.obj(fields = "typeOfPackage" -> "HG", "quantity" -> 7, "identityOfCommercialSeal"   -> "SEAL77", "sealInformation"        -> "Cork")
        ),
        "wineProduct" -> Json.obj(
          "wineProductCategory"  -> "3",
          "thirdCountryOfOrigin" -> "FJ",
          "otherInformation"     -> "Not available",
          "wineOperations"       -> Json.arr("0", "1")
        )
      )
    ),
    "numberOfItems" -> 2,
    "transportDetails" -> Json.arr(
      Json.obj(
        "transportUnitCode"            -> "1",
        "identityOfTransportUnits"     -> "Bottles",
        "commercialSealIdentification" -> "SID13245678",
        "complementaryInformation"     -> "Bottles of Restina",
        "sealInformation"              -> "Sealed with red strip"
      ),
      Json.obj(
        "transportUnitCode"            -> "2",
        "identityOfTransportUnits"     -> "Cans",
        "commercialSealIdentification" -> "SID132987",
        "complementaryInformation"     -> "Cans",
        "sealInformation"              -> "Seal"
      )
    ),
    "reportOfReceipt" -> Json.obj(
      "arc" -> "13AB7778889991ABCDEF9",
      "sequenceNumber" -> 2,
      "dateAndTimeOfValidationOfReportOfReceiptExport" -> "2021-09-10T11:11:12",
      "consigneeTrader" -> Json.obj(
        "traderExciseNumber" -> "XIWK000000206",
        "traderName"         -> "SEED TRADER NI",
        "address" -> Json.obj(
          "streetNumber" -> "1",
          "street"       -> "Catherdral",
          "postcode"     -> "BT3 7BF",
          "city"         -> "Salford"
        )
      ),
      "deliveryPlaceTrader" -> Json.obj(
        "traderExciseNumber" -> "XI00000000207",
        "traderName"         -> "SEED TRADER NI 2",
        "address" -> Json.obj(
          "streetNumber" -> "2",
          "street"       -> "Catherdral",
          "postcode"     -> "BT3 7BF",
          "city"         -> "Salford"
        )
      ),
      "destinationOffice" -> "XI004098",
      "dateOfArrival" -> "2021-09-08",
      "acceptMovement" -> "satisfactory",
      "individualItems" -> Json.arr()
    ),
    "notificationOfDivertedMovement" -> Json.obj(
      "notificationType" -> "2",
      "notificationDateAndTime" -> "2024-06-05T00:00:01",
      "downstreamArcs" -> Json.arr(
        testArc, s"${testArc.dropRight(1)}1"
      )
    ),
    "notificationOfAlertOrRejection" -> Json.arr(
      Json.obj(
        "notificationType" -> "0",
        "notificationDateAndTime" -> "2023-12-18T09:00:00",
        "alertRejectReason" -> Json.arr(
          Json.obj(
            "reason" -> "2",
            "additionalInformation" -> "Info"
          ),
          Json.obj(
            "reason" -> "1",
            "additionalInformation" -> "Info"
          ),
          Json.obj(
            "reason" -> "0",
            "additionalInformation" -> "Info"
          ),
          Json.obj(
            "reason" -> "3",
            "additionalInformation" -> "Info"
          )
        )
      ),
      Json.obj(
        "notificationType" -> "0",
        "notificationDateAndTime" -> "2023-12-18T10:00:00",
        "alertRejectReason" -> Json.arr(
          Json.obj(
            "reason" -> "1"
          )
        )
      ),
      Json.obj(
        "notificationType" -> "1",
        "notificationDateAndTime" -> "2023-12-19T09:00:00",
        "alertRejectReason" -> Json.arr(
          Json.obj(
            "reason" -> "3"
          )
        )
      )
    ),
    "notificationOfAcceptedExport" -> Json.obj(
      "customsOfficeNumber" -> "GB000383",
      "dateOfAcceptance" -> "2024-02-05",
      "referenceNumberOfSenderCustomsOffice" -> "GB000101",
      "identificationOfSenderCustomsOfficer" -> "John Doe",
      "documentReferenceNumber" -> "645564546",
      "consigneeTrader" -> Json.obj(
        "traderExciseNumber" -> "BE345345345",
        "traderName"         -> "PEAR Supermarket",
        "address" -> Json.obj(
          "street"   -> "Angels Business Park",
          "postcode" -> "BD1 3NN",
          "city"     -> "Bradford"
        ),
        "eoriNumber" -> "GB00000578901"
      )
    ),
    "notificationOfDelay" -> Json.arr(
      Json.obj(fields =
        "submitterIdentification"  -> "837Submitter",
        "submitterType"            -> "1",
        "explanationCode"          -> "1",
        "complementaryInformation" -> "837 complementary info",
        "dateTime"                 -> "2001-12-17T09:30:47"
      ),
      Json.obj(fields =
        "submitterIdentification"  -> "GBWK001234569",
        "submitterType"            -> "1",
        "explanationCode"          -> "6",
        "complementaryInformation" -> "Lorry crashed off cliff",
        "dateTime"                 -> "2024-06-18T08:11:33"
      ),
      Json.obj(fields =
        "submitterIdentification"  -> "GBWK001234569",
        "submitterType"            -> "1",
        "explanationCode"          -> "5",
        "dateTime"                 -> "2024-06-18T08:18:56"
      )
    ),
    "cancelMovement" -> Json.obj(
      "reason" -> "0",
      "complementaryInformation" -> "some info"
    )
  )

  def maxGetMovementResponseBody(sequenceNumber: Int = 1): String =
    s"""<mov:movementView xsi:schemaLocation="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3 movementView.xsd" xmlns:mov="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3">
       |    <mov:currentMovement>
       |      <mov:status>Beans</mov:status>
       |      <mov:version_transaction_ref>008</mov:version_transaction_ref>
       |      <body:IE801 xmlns:body="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE801:V3.01">
       |        <body:Header xmlns:head="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:TMS:V2.02">
       |          <head:MessageSender>NDEA.FR</head:MessageSender>
       |          <head:MessageRecipient>NDEA.GB</head:MessageRecipient>
       |          <head:DateOfPreparation>2008-09-04</head:DateOfPreparation>
       |          <head:TimeOfPreparation>10:22:50</head:TimeOfPreparation>
       |          <head:MessageIdentifier>Message identifier</head:MessageIdentifier>
       |        </body:Header>
       |        <body:Body>
       |          <body:EADESADContainer>
       |            <body:ConsigneeTrader language="en">
       |              <body:Traderid>ConsigneeTraderId</body:Traderid>
       |              <body:TraderName>ConsigneeTraderName</body:TraderName>
       |              <body:StreetName>ConsigneeTraderStreetName</body:StreetName>
       |              <body:StreetNumber>ConsigneeTraderStreetNumber</body:StreetNumber>
       |              <body:Postcode>ConsigneeTraderPostcode</body:Postcode>
       |              <body:City>ConsigneeTraderCity</body:City>
       |              <body:EoriNumber>ConsigneeTraderEori</body:EoriNumber>
       |            </body:ConsigneeTrader>
       |            <body:ExciseMovement>
       |               <body:AdministrativeReferenceCode>ExciseMovementArc</body:AdministrativeReferenceCode>
       |               <body:DateAndTimeOfValidationOfEadEsad>ExciseMovementDateTime</body:DateAndTimeOfValidationOfEadEsad>
       |            </body:ExciseMovement>
       |            <body:ConsignorTrader language="en">
       |              <body:TraderExciseNumber>ConsignorTraderExciseNumber</body:TraderExciseNumber>
       |              <body:TraderName>ConsignorTraderName</body:TraderName>
       |              <body:StreetName>ConsignorTraderStreetName</body:StreetName>
       |              <body:StreetNumber>ConsignorTraderStreetNumber</body:StreetNumber>
       |              <body:Postcode>ConsignorTraderPostcode</body:Postcode>
       |              <body:City>ConsignorTraderCity</body:City>
       |            </body:ConsignorTrader>
       |            <body:PlaceOfDispatchTrader language="en">
       |              <body:ReferenceOfTaxWarehouse>PlaceOfDispatchTraderReferenceOfTaxWarehouse</body:ReferenceOfTaxWarehouse>
       |              <body:TraderName>PlaceOfDispatchTraderName</body:TraderName>
       |              <body:StreetName>PlaceOfDispatchTraderStreetName</body:StreetName>
       |              <body:StreetNumber>PlaceOfDispatchTraderStreetNumber</body:StreetNumber>
       |              <body:Postcode>PlaceOfDispatchTraderPostcode</body:Postcode>
       |              <body:City>PlaceOfDispatchTraderCity</body:City>
       |            </body:PlaceOfDispatchTrader>
       |            <body:DispatchImportOffice>
       |              <body:ReferenceNumber>DispatchImportOfficeErn</body:ReferenceNumber>
       |            </body:DispatchImportOffice>
       |            <body:ComplementConsigneeTrader>
       |              <body:MemberStateCode>CCTMemberStateCode</body:MemberStateCode>
       |              <body:SerialNumberOfCertificateOfExemption>CCTSerialNumber</body:SerialNumberOfCertificateOfExemption>
       |            </body:ComplementConsigneeTrader>
       |            <body:DeliveryPlaceTrader language="en">
       |              <body:Traderid>DeliveryPlaceTraderId</body:Traderid>
       |              <body:TraderName>DeliveryPlaceTraderName</body:TraderName>
       |              <body:StreetName>DeliveryPlaceTraderStreetName</body:StreetName>
       |              <body:StreetNumber>DeliveryPlaceTraderStreetNumber</body:StreetNumber>
       |              <body:Postcode>DeliveryPlaceTraderPostcode</body:Postcode>
       |              <body:City>DeliveryPlaceTraderCity</body:City>
       |            </body:DeliveryPlaceTrader>
       |            <body:DeliveryPlaceCustomsOffice>
       |              <body:ReferenceNumber>DeliveryPlaceCustomsOfficeErn</body:ReferenceNumber>
       |            </body:DeliveryPlaceCustomsOffice>
       |            <body:CompetentAuthorityDispatchOffice>
       |              <body:ReferenceNumber>CompetentAuthorityDispatchOfficeErn</body:ReferenceNumber>
       |            </body:CompetentAuthorityDispatchOffice>
       |            <body:TransportArrangerTrader language="en">
       |              <body:VatNumber>TransportArrangerTraderVatNumber</body:VatNumber>
       |              <body:TraderName>TransportArrangerTraderName</body:TraderName>
       |              <body:StreetName>TransportArrangerTraderStreetName</body:StreetName>
       |              <body:StreetNumber>TransportArrangerTraderStreetNumber</body:StreetNumber>
       |              <body:Postcode>TransportArrangerTraderPostcode</body:Postcode>
       |              <body:City>TransportArrangerTraderCity</body:City>
       |            </body:TransportArrangerTrader>
       |            <body:FirstTransporterTrader language="en">
       |              <body:VatNumber>FirstTransporterTraderVatNumber</body:VatNumber>
       |              <body:TraderName>FirstTransporterTraderName</body:TraderName>
       |              <body:StreetName>FirstTransporterTraderStreetName</body:StreetName>
       |              <body:StreetNumber>FirstTransporterTraderStreetNumber</body:StreetNumber>
       |              <body:Postcode>FirstTransporterTraderPostcode</body:Postcode>
       |              <body:City>FirstTransporterTraderCity</body:City>
       |            </body:FirstTransporterTrader>
       |            <body:DocumentCertificate>
       |              <body:DocumentType>DocumentCertificateDocumentType1</body:DocumentType>
       |              <body:DocumentReference>DocumentCertificateDocumentReference1</body:DocumentReference>
       |              <body:DocumentDescription>DocumentCertificateDocumentDescription1</body:DocumentDescription>
       |              <body:ReferenceOfDocument>DocumentCertificateReferenceOfDocument1</body:ReferenceOfDocument>
       |            </body:DocumentCertificate>
       |            <body:DocumentCertificate>
       |              <body:DocumentType>DocumentCertificateDocumentType2</body:DocumentType>
       |              <body:DocumentReference>DocumentCertificateDocumentReference2</body:DocumentReference>
       |              <body:DocumentDescription>DocumentCertificateDocumentDescription2</body:DocumentDescription>
       |              <body:ReferenceOfDocument>DocumentCertificateReferenceOfDocument2</body:ReferenceOfDocument>
       |            </body:DocumentCertificate>
       |            <body:EadEsad>
       |              <body:LocalReferenceNumber>EadEsadLocalReferenceNumber</body:LocalReferenceNumber>
       |              <body:InvoiceNumber>EadEsadInvoiceNumber</body:InvoiceNumber>
       |              <body:InvoiceDate>EadEsadInvoiceDate</body:InvoiceDate>
       |              <body:OriginTypeCode>3</body:OriginTypeCode>
       |              <body:DateOfDispatch>EadEsadDateOfDispatch</body:DateOfDispatch>
       |              <body:TimeOfDispatch>EadEsadTimeOfDispatch</body:TimeOfDispatch>
       |              <body:UpstreamArc>EadEsadUpstreamArc</body:UpstreamArc>
       |              <body:ImportSad>
       |                <body:ImportSadNumber>ImportSadNumber1</body:ImportSadNumber>
       |              </body:ImportSad>
       |              <body:ImportSad>
       |                <body:ImportSadNumber>ImportSadNumber2</body:ImportSadNumber>
       |              </body:ImportSad>
       |            </body:EadEsad>
       |            <body:HeaderEadEsad>
       |              <body:SequenceNumber>$sequenceNumber</body:SequenceNumber>
       |              <body:DateAndTimeOfUpdateValidation>HeaderEadEsadDateTime</body:DateAndTimeOfUpdateValidation>
       |              <body:DestinationTypeCode>10</body:DestinationTypeCode>
       |              <body:JourneyTime>H10</body:JourneyTime>
       |              <body:TransportArrangement>2</body:TransportArrangement>
       |            </body:HeaderEadEsad>
       |            <body:TransportMode>
       |              <body:TransportModeCode>TransportModeTransportModeCode</body:TransportModeCode>
       |              <body:ComplementaryInformation>TransportModeComplementaryInformation</body:ComplementaryInformation>
       |            </body:TransportMode>
       |            <body:MovementGuarantee>
       |              <body:GuarantorTypeCode>123</body:GuarantorTypeCode>
       |              <body:GuarantorTrader>
       |                <body:TraderExciseNumber>GuarantorTraderErn1</body:TraderExciseNumber>
       |                <body:TraderName>GuarantorTraderName1</body:TraderName>
       |                <body:StreetName>GuarantorTraderStreetName1</body:StreetName>
       |                <body:StreetNumber>GuarantorTraderStreetNumber1</body:StreetNumber>
       |                <body:Postcode>GuarantorTraderPostcode1</body:Postcode>
       |                <body:City>GuarantorTraderCity1</body:City>
       |                <body:VatNumber>GuarantorTraderVatNumber1</body:VatNumber>
       |              </body:GuarantorTrader>
       |              <body:GuarantorTrader>
       |                <body:TraderExciseNumber>GuarantorTraderErn2</body:TraderExciseNumber>
       |                <body:TraderName>GuarantorTraderName2</body:TraderName>
       |                <body:StreetName>GuarantorTraderStreetName2</body:StreetName>
       |                <body:StreetNumber>GuarantorTraderStreetNumber2</body:StreetNumber>
       |                <body:Postcode>GuarantorTraderPostcode2</body:Postcode>
       |                <body:City>GuarantorTraderCity2</body:City>
       |                <body:VatNumber>GuarantorTraderVatNumber2</body:VatNumber>
       |              </body:GuarantorTrader>
       |            </body:MovementGuarantee>
       |            <body:BodyEadEsad>
       |              <body:BodyRecordUniqueReference>1</body:BodyRecordUniqueReference>
       |              <body:ExciseProductCode>BodyEadEsadExciseProductCode1</body:ExciseProductCode>
       |              <body:CnCode>BodyEadEsadCnCode1</body:CnCode>
       |              <body:Quantity>2</body:Quantity>
       |              <body:GrossMass>3</body:GrossMass>
       |              <body:NetMass>4</body:NetMass>
       |              <body:AlcoholicStrengthByVolumeInPercentage>5</body:AlcoholicStrengthByVolumeInPercentage>
       |              <body:DegreePlato>6</body:DegreePlato>
       |              <body:FiscalMark>BodyEadEsadFiscalMark1</body:FiscalMark>
       |              <body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
       |              <body:DesignationOfOrigin>BodyEadEsadDesignationOfOrigin1</body:DesignationOfOrigin>
       |              <body:SizeOfProducer>BodyEadEsadSizeOfProducer1</body:SizeOfProducer>
       |              <body:Density>7</body:Density>
       |              <body:CommercialDescription>BodyEadEsadCommercialDescription1</body:CommercialDescription>
       |              <body:BrandNameOfProducts>BodyEadEsadBrandNameOfProducts1</body:BrandNameOfProducts>
       |              <body:MaturationPeriodOrAgeOfProducts>BodyEadEsadMaturationPeriodOrAgeOfProducts1</body:MaturationPeriodOrAgeOfProducts>
       |              <body:IndependentSmallProducersDeclaration>BodyEadEsadIndependentSmallProducersDeclaration1</body:IndependentSmallProducersDeclaration>
       |              <body:Package>
       |                <body:KindOfPackages>PackageKindOfPackages11</body:KindOfPackages>
       |                <body:NumberOfPackages>1</body:NumberOfPackages>
       |                <body:ShippingMarks>PackageShippingMarks11</body:ShippingMarks>
       |                <body:CommercialSealIdentification>PackageCommercialSealIdentification11</body:CommercialSealIdentification>
       |                <body:SealInformation>PackageSealInformation11</body:SealInformation>
       |              </body:Package>
       |              <body:Package>
       |                <body:KindOfPackages>PackageKindOfPackages12</body:KindOfPackages>
       |                <body:NumberOfPackages>2</body:NumberOfPackages>
       |                <body:ShippingMarks>PackageShippingMarks12</body:ShippingMarks>
       |                <body:CommercialSealIdentification>PackageCommercialSealIdentification12</body:CommercialSealIdentification>
       |                <body:SealInformation>PackageSealInformation12</body:SealInformation>
       |              </body:Package>
       |              <body:WineProduct>
       |                <body:WineProductCategory>1</body:WineProductCategory>
       |                <body:WineGrowingZoneCode>WineProductWineGrowingZoneCode1</body:WineGrowingZoneCode>
       |                <body:ThirdCountryOfOrigin>WineProductThirdCountryOfOrigin1</body:ThirdCountryOfOrigin>
       |                <body:OtherInformation>WineProductOtherInformation1</body:OtherInformation>
       |                <body:WineOperation>
       |                  <body:WineOperationCode>WineOperationCode11</body:WineOperationCode>
       |                </body:WineOperation>
       |                <body:WineOperation>
       |                  <body:WineOperationCode>WineOperationCode12</body:WineOperationCode>
       |                </body:WineOperation>
       |              </body:WineProduct>
       |            </body:BodyEadEsad>
       |            <body:BodyEadEsad>
       |              <body:BodyRecordUniqueReference>2</body:BodyRecordUniqueReference>
       |              <body:ExciseProductCode>BodyEadEsadExciseProductCode2</body:ExciseProductCode>
       |              <body:CnCode>BodyEadEsadCnCode2</body:CnCode>
       |              <body:Quantity>3</body:Quantity>
       |              <body:GrossMass>4</body:GrossMass>
       |              <body:NetMass>5</body:NetMass>
       |              <body:AlcoholicStrengthByVolumeInPercentage>6</body:AlcoholicStrengthByVolumeInPercentage>
       |              <body:DegreePlato>7</body:DegreePlato>
       |              <body:FiscalMark>BodyEadEsadFiscalMark2</body:FiscalMark>
       |              <body:FiscalMarkUsedFlag>0</body:FiscalMarkUsedFlag>
       |              <body:DesignationOfOrigin>BodyEadEsadDesignationOfOrigin2</body:DesignationOfOrigin>
       |              <body:SizeOfProducer>BodyEadEsadSizeOfProducer2</body:SizeOfProducer>
       |              <body:Density>8</body:Density>
       |              <body:CommercialDescription>BodyEadEsadCommercialDescription2</body:CommercialDescription>
       |              <body:BrandNameOfProducts>BodyEadEsadBrandNameOfProducts2</body:BrandNameOfProducts>
       |              <body:MaturationPeriodOrAgeOfProducts>BodyEadEsadMaturationPeriodOrAgeOfProducts2</body:MaturationPeriodOrAgeOfProducts>
       |              <body:IndependentSmallProducersDeclaration>BodyEadEsadIndependentSmallProducersDeclaration2</body:IndependentSmallProducersDeclaration>
       |              <body:Package>
       |                <body:KindOfPackages>PackageKindOfPackages21</body:KindOfPackages>
       |                <body:NumberOfPackages>3</body:NumberOfPackages>
       |                <body:ShippingMarks>PackageShippingMarks21</body:ShippingMarks>
       |                <body:CommercialSealIdentification>PackageCommercialSealIdentification21</body:CommercialSealIdentification>
       |                <body:SealInformation>PackageSealInformation21</body:SealInformation>
       |              </body:Package>
       |              <body:Package>
       |                <body:KindOfPackages>PackageKindOfPackages22</body:KindOfPackages>
       |                <body:NumberOfPackages>4</body:NumberOfPackages>
       |                <body:ShippingMarks>PackageShippingMarks22</body:ShippingMarks>
       |                <body:CommercialSealIdentification>PackageCommercialSealIdentification22</body:CommercialSealIdentification>
       |                <body:SealInformation>PackageSealInformation22</body:SealInformation>
       |              </body:Package>
       |              <body:WineProduct>
       |                <body:WineProductCategory>2</body:WineProductCategory>
       |                <body:WineGrowingZoneCode>WineProductWineGrowingZoneCode2</body:WineGrowingZoneCode>
       |                <body:ThirdCountryOfOrigin>WineProductThirdCountryOfOrigin2</body:ThirdCountryOfOrigin>
       |                <body:OtherInformation>WineProductOtherInformation2</body:OtherInformation>
       |                <body:WineOperation>
       |                  <body:WineOperationCode>WineOperationCode21</body:WineOperationCode>
       |                </body:WineOperation>
       |                <body:WineOperation>
       |                  <body:WineOperationCode>WineOperationCode22</body:WineOperationCode>
       |                </body:WineOperation>
       |              </body:WineProduct>
       |            </body:BodyEadEsad>
       |            <body:TransportDetails>
       |              <body:TransportUnitCode>TransportDetailsTransportUnitCode1</body:TransportUnitCode>
       |              <body:IdentityOfTransportUnits>TransportDetailsIdentityOfTransportUnits1</body:IdentityOfTransportUnits>
       |              <body:CommercialSealIdentification>TransportDetailsCommercialSealIdentification1</body:CommercialSealIdentification>
       |              <body:ComplementaryInformation>TransportDetailsComplementaryInformation1</body:ComplementaryInformation>
       |              <body:SealInformation>TransportDetailsSealInformation1</body:SealInformation>
       |            </body:TransportDetails>
       |            <body:TransportDetails>
       |              <body:TransportUnitCode>TransportDetailsTransportUnitCode2</body:TransportUnitCode>
       |              <body:IdentityOfTransportUnits>TransportDetailsIdentityOfTransportUnits2</body:IdentityOfTransportUnits>
       |              <body:CommercialSealIdentification>TransportDetailsCommercialSealIdentification2</body:CommercialSealIdentification>
       |              <body:ComplementaryInformation>TransportDetailsComplementaryInformation2</body:ComplementaryInformation>
       |              <body:SealInformation>TransportDetailsSealInformation2</body:SealInformation>
       |            </body:TransportDetails>
       |          </body:EADESADContainer>
       |        </body:Body>
       |      </body:IE801>
       |    </mov:currentMovement>
       |    <mov:eventHistory>
       |          <urn:IE818 xmlns:urn="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE818:V3.01">
       |      <urn:Header>
       |        <urn1:MessageSender xmlns:urn1="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:TMS:V3.01">NDEA.XI</urn1:MessageSender>
       |        <urn1:MessageRecipient xmlns:urn1="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:TMS:V3.01">NDEA.GB</urn1:MessageRecipient>
       |        <urn1:DateOfPreparation xmlns:urn1="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:TMS:V3.01">2021-09-10</urn1:DateOfPreparation>
       |        <urn1:TimeOfPreparation xmlns:urn1="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:TMS:V3.01">11:11:09</urn1:TimeOfPreparation>
       |        <urn1:MessageIdentifier xmlns:urn1="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:TMS:V3.01">XI100000000291919</urn1:MessageIdentifier>
       |        <urn1:CorrelationIdentifier xmlns:urn1="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:TMS:V3.01">PORTAL5a1b930650c54fbca85cf509add5182e</urn1:CorrelationIdentifier>
       |      </urn:Header>
       |      <urn:Body>
       |        <urn:AcceptedOrRejectedReportOfReceiptExport>
       |          <urn:Attributes>
       |            <urn:DateAndTimeOfValidationOfReportOfReceiptExport>2021-09-10T11:11:12</urn:DateAndTimeOfValidationOfReportOfReceiptExport>
       |          </urn:Attributes>
       |          <urn:ConsigneeTrader language="en">
       |            <urn:Traderid>XIWK000000206</urn:Traderid>
       |            <urn:TraderName>SEED TRADER NI</urn:TraderName>
       |            <urn:StreetNumber>1</urn:StreetNumber>
       |            <urn:StreetName>Catherdral</urn:StreetName>
       |            <urn:Postcode>BT3 7BF</urn:Postcode>
       |            <urn:City>Salford</urn:City>
       |          </urn:ConsigneeTrader>
       |          <ie:ExciseMovement xmlns:ie="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE818:V3.01">
       |            <urn:AdministrativeReferenceCode>21GB00000000000351266</urn:AdministrativeReferenceCode>
       |            <urn:SequenceNumber>2</urn:SequenceNumber>
       |          </ie:ExciseMovement>
       |          <urn:DeliveryPlaceTrader language="en">
       |            <urn:Traderid>XI00000000207</urn:Traderid>
       |            <urn:TraderName>SEED TRADER NI 2</urn:TraderName>
       |            <urn:StreetNumber>2</urn:StreetNumber>
       |            <urn:StreetName>Catherdral</urn:StreetName>
       |            <urn:Postcode>BT3 7BF</urn:Postcode>
       |            <urn:City>Salford</urn:City>
       |          </urn:DeliveryPlaceTrader>
       |          <urn:DestinationOffice>
       |            <urn:ReferenceNumber>XI004098</urn:ReferenceNumber>
       |          </urn:DestinationOffice>
       |          <urn:ReportOfReceiptExport>
       |            <urn:DateOfArrivalOfExciseProducts>2021-09-08</urn:DateOfArrivalOfExciseProducts>
       |            <urn:GlobalConclusionOfReceipt>1</urn:GlobalConclusionOfReceipt>
       |            <urn:ComplementaryInformation>some great reason</urn:ComplementaryInformation>
       |          </urn:ReportOfReceiptExport>
       |          <urn:BodyReportOfReceiptExport>
       |            <urn:BodyRecordUniqueReference>1</urn:BodyRecordUniqueReference>
       |            <urn:IndicatorOfShortageOrExcess>E</urn:IndicatorOfShortageOrExcess>
       |            <urn:ObservedShortageOrExcess>21</urn:ObservedShortageOrExcess>
       |            <urn:ExciseProductCode>W300</urn:ExciseProductCode>
       |            <urn:RefusedQuantity>1</urn:RefusedQuantity>
       |            <urn:UnsatisfactoryReason>
       |              <urn:UnsatisfactoryReasonCode>1</urn:UnsatisfactoryReasonCode>
       |              <urn:ComplementaryInformation>some info</urn:ComplementaryInformation>
       |            </urn:UnsatisfactoryReason>
       |            <urn:UnsatisfactoryReason>
       |              <urn:UnsatisfactoryReasonCode>2</urn:UnsatisfactoryReasonCode>
       |            </urn:UnsatisfactoryReason>
       |          </urn:BodyReportOfReceiptExport>
       |        </urn:AcceptedOrRejectedReportOfReceiptExport>
       |      </urn:Body>
       |    </urn:IE818>
       |    <urn:IE803 xmlns:ie803="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE803:V3.13">
       |      <urn:Header>
       |        <urn:MessageSender>NDEA.GB</urn:MessageSender>
       |        <urn:MessageRecipient>NDEA.GB</urn:MessageRecipient>
       |        <urn:DateOfPreparation>2020-12-03</urn:DateOfPreparation>
       |        <urn:TimeOfPreparation>13:36:43.326</urn:TimeOfPreparation>
       |        <urn:MessageIdentifier>GB100000000289576</urn:MessageIdentifier>
       |      </urn:Header>
       |      <urn:Body>
       |        <urn:NotificationOfDivertedEADESAD>
       |          <urn:ExciseNotification>
       |            <urn:NotificationType>2</urn:NotificationType>
       |            <urn:NotificationDateAndTime>2024-06-05T00:00:01</urn:NotificationDateAndTime>
       |            <urn:AdministrativeReferenceCode>20GB00000000000341760</urn:AdministrativeReferenceCode>
       |            <urn:SequenceNumber>1</urn:SequenceNumber>
       |          </urn:ExciseNotification>
       |          <urn:DownstreamArc>
       |            <urn:AdministrativeReferenceCode>$testArc</urn:AdministrativeReferenceCode>
       |          </urn:DownstreamArc>
       |          <urn:DownstreamArc>
       |            <urn:AdministrativeReferenceCode>${testArc.dropRight(1)}1</urn:AdministrativeReferenceCode>
       |          </urn:DownstreamArc>
       |        </urn:NotificationOfDivertedEADESAD>
       |      </urn:Body>
       |    </urn:IE803>
       |
       |    <!-- Alert Event -->
       |      <ie819:IE819 xmlns:ie819="ie819:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE819:V3.13">
       |        <ie819:Header>
       |          <urn:MessageSender>NDEA.GB</urn:MessageSender>
       |          <urn:MessageRecipient>NDEA.GB</urn:MessageRecipient>
       |          <urn:DateOfPreparation>2023-12-18</urn:DateOfPreparation>
       |          <urn:TimeOfPreparation>08:59:59.441503</urn:TimeOfPreparation>
       |          <urn:MessageIdentifier>9de3f13e-7559-4f4d-8851-b954b01210c0</urn:MessageIdentifier>
       |          <urn:CorrelationIdentifier>e8803427-c7e5-4539-83b7-d174f511e70c</urn:CorrelationIdentifier>
       |        </ie819:Header>
       |        <ie819:Body>
       |          <ie819:AlertOrRejectionOfEADESAD>
       |            <ie819:Attributes>
       |              <ie819:DateAndTimeOfValidationOfAlertRejection>2023-12-18T09:00:00</ie819:DateAndTimeOfValidationOfAlertRejection>
       |            </ie819:Attributes>
       |            <ie819:ConsigneeTrader language="en">
       |              <ie819:Traderid>GBWK123456789</ie819:Traderid>
       |              <ie819:TraderName>Bizz</ie819:TraderName>
       |              <ie819:StreetName>GRANGE CENTRAL</ie819:StreetName>
       |              <ie819:Postcode>tf3 4er</ie819:Postcode>
       |              <ie819:City>Shropshire</ie819:City>
       |            </ie819:ConsigneeTrader>
       |            <ie819:ExciseMovement>
       |              <ie819:AdministrativeReferenceCode>18GB00000000000232361</ie819:AdministrativeReferenceCode>
       |              <ie819:SequenceNumber>1</ie819:SequenceNumber>
       |            </ie819:ExciseMovement>
       |            <ie819:DestinationOffice>
       |              <ie819:ReferenceNumber>GB004098</ie819:ReferenceNumber>
       |            </ie819:DestinationOffice>
       |            <ie819:AlertOrRejection>
       |              <ie819:DateOfAlertOrRejection>2023-12-18</ie819:DateOfAlertOrRejection>
       |              <ie819:EadEsadRejectedFlag>0</ie819:EadEsadRejectedFlag>
       |            </ie819:AlertOrRejection>
       |            <ie819:AlertOrRejectionOfEadEsadReason>
       |              <ie819:AlertOrRejectionOfMovementReasonCode>2</ie819:AlertOrRejectionOfMovementReasonCode>
       |              <ie819:ComplementaryInformation>Info</ie819:ComplementaryInformation>
       |            </ie819:AlertOrRejectionOfEadEsadReason>
       |            <ie819:AlertOrRejectionOfEadEsadReason>
       |              <ie819:AlertOrRejectionOfMovementReasonCode>1</ie819:AlertOrRejectionOfMovementReasonCode>
       |              <ie819:ComplementaryInformation>Info</ie819:ComplementaryInformation>
       |            </ie819:AlertOrRejectionOfEadEsadReason>
       |            <ie819:AlertOrRejectionOfEadEsadReason>
       |              <ie819:AlertOrRejectionOfMovementReasonCode>0</ie819:AlertOrRejectionOfMovementReasonCode>
       |              <ie819:ComplementaryInformation>Info</ie819:ComplementaryInformation>
       |            </ie819:AlertOrRejectionOfEadEsadReason>
       |            <ie819:AlertOrRejectionOfEadEsadReason>
       |              <ie819:AlertOrRejectionOfMovementReasonCode>3</ie819:AlertOrRejectionOfMovementReasonCode>
       |              <ie819:ComplementaryInformation>Info</ie819:ComplementaryInformation>
       |            </ie819:AlertOrRejectionOfEadEsadReason>
       |          </ie819:AlertOrRejectionOfEADESAD>
       |        </ie819:Body>
       |      </ie819:IE819>
       |
       |      <!-- 2nd Alert Event -->
       |      <ie819:IE819 xmlns:ie819="ie819:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE819:V3.13">
       |        <ie819:Header>
       |          <urn:MessageSender>NDEA.GB</urn:MessageSender>
       |          <urn:MessageRecipient>NDEA.GB</urn:MessageRecipient>
       |          <urn:DateOfPreparation>2023-12-18</urn:DateOfPreparation>
       |          <urn:TimeOfPreparation>09:59:59.441503</urn:TimeOfPreparation>
       |          <urn:MessageIdentifier>9de3f13e-7559-4f4d-8851-b954b01210c0</urn:MessageIdentifier>
       |          <urn:CorrelationIdentifier>e8803427-c7e5-4539-83b7-d174f511e70c</urn:CorrelationIdentifier>
       |        </ie819:Header>
       |        <ie819:Body>
       |          <ie819:AlertOrRejectionOfEADESAD>
       |            <ie819:Attributes>
       |              <ie819:DateAndTimeOfValidationOfAlertRejection>2023-12-18T10:00:00</ie819:DateAndTimeOfValidationOfAlertRejection>
       |            </ie819:Attributes>
       |            <ie819:ConsigneeTrader language="en">
       |              <ie819:Traderid>GBWK123456789</ie819:Traderid>
       |              <ie819:TraderName>Bizz</ie819:TraderName>
       |              <ie819:StreetName>GRANGE CENTRAL</ie819:StreetName>
       |              <ie819:Postcode>tf3 4er</ie819:Postcode>
       |              <ie819:City>Shropshire</ie819:City>
       |            </ie819:ConsigneeTrader>
       |            <ie819:ExciseMovement>
       |              <ie819:AdministrativeReferenceCode>18GB00000000000232361</ie819:AdministrativeReferenceCode>
       |              <ie819:SequenceNumber>1</ie819:SequenceNumber>
       |            </ie819:ExciseMovement>
       |            <ie819:DestinationOffice>
       |              <ie819:ReferenceNumber>GB004098</ie819:ReferenceNumber>
       |            </ie819:DestinationOffice>
       |            <ie819:AlertOrRejection>
       |              <ie819:DateOfAlertOrRejection>2023-12-18</ie819:DateOfAlertOrRejection>
       |              <ie819:EadEsadRejectedFlag>0</ie819:EadEsadRejectedFlag>
       |            </ie819:AlertOrRejection>
       |            <ie819:AlertOrRejectionOfEadEsadReason>
       |              <ie819:AlertOrRejectionOfMovementReasonCode>1</ie819:AlertOrRejectionOfMovementReasonCode>
       |            </ie819:AlertOrRejectionOfEadEsadReason>
       |          </ie819:AlertOrRejectionOfEADESAD>
       |        </ie819:Body>
       |      </ie819:IE819>
       |
       |      <!-- Rejection Event -->
       |      <ie819:IE819 xmlns:ie819="ie819:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE819:V3.13">
       |        <ie819:Header>
       |          <urn:MessageSender>NDEA.GB</urn:MessageSender>
       |          <urn:MessageRecipient>NDEA.GB</urn:MessageRecipient>
       |          <urn:DateOfPreparation>2023-12-19</urn:DateOfPreparation>
       |          <urn:TimeOfPreparation>08:59:59.441503</urn:TimeOfPreparation>
       |          <urn:MessageIdentifier>9de3f13e-7559-4f4d-8851-b954b01210c0</urn:MessageIdentifier>
       |          <urn:CorrelationIdentifier>e8803427-c7e5-4539-83b7-d174f511e70c</urn:CorrelationIdentifier>
       |        </ie819:Header>
       |        <ie819:Body>
       |          <ie819:AlertOrRejectionOfEADESAD>
       |            <ie819:Attributes>
       |              <ie819:DateAndTimeOfValidationOfAlertRejection>2023-12-19T09:00:00</ie819:DateAndTimeOfValidationOfAlertRejection>
       |            </ie819:Attributes>
       |            <ie819:ConsigneeTrader language="en">
       |              <ie819:Traderid>GBWK123456789</ie819:Traderid>
       |              <ie819:TraderName>Bizz</ie819:TraderName>
       |              <ie819:StreetName>GRANGE CENTRAL</ie819:StreetName>
       |              <ie819:Postcode>tf3 4er</ie819:Postcode>
       |              <ie819:City>Shropshire</ie819:City>
       |            </ie819:ConsigneeTrader>
       |            <ie819:ExciseMovement>
       |              <ie819:AdministrativeReferenceCode>18GB00000000000232361</ie819:AdministrativeReferenceCode>
       |              <ie819:SequenceNumber>1</ie819:SequenceNumber>
       |            </ie819:ExciseMovement>
       |            <ie819:DestinationOffice>
       |              <ie819:ReferenceNumber>GB004098</ie819:ReferenceNumber>
       |            </ie819:DestinationOffice>
       |            <ie819:AlertOrRejection>
       |              <ie819:DateOfAlertOrRejection>2023-12-18</ie819:DateOfAlertOrRejection>
       |              <ie819:EadEsadRejectedFlag>1</ie819:EadEsadRejectedFlag>
       |            </ie819:AlertOrRejection>
       |            <ie819:AlertOrRejectionOfEadEsadReason>
       |              <ie819:AlertOrRejectionOfMovementReasonCode>3</ie819:AlertOrRejectionOfMovementReasonCode>
       |            </ie819:AlertOrRejectionOfEadEsadReason>
       |          </ie819:AlertOrRejectionOfEADESAD>
       |        </ie819:Body>
       |      </ie819:IE819>
       |
       |      <!-- Movement accepted by customs -->
       |      <ie829:IE829 xmlns:ie829="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE829:V3.13">
       |        <ie829:Header>
       |          <urn:MessageSender>NDEA.GB</urn:MessageSender>
       |          <urn:MessageRecipient>NDEA.GB</urn:MessageRecipient>
       |          <urn:DateOfPreparation>2023-12-19</urn:DateOfPreparation>
       |          <urn:TimeOfPreparation>08:59:59.441503</urn:TimeOfPreparation>
       |          <urn:MessageIdentifier>9de3f13e-7559-4f4d-8851-b954b01210c0</urn:MessageIdentifier>
       |          <urn:CorrelationIdentifier>e8803427-c7e5-4539-83b7-d174f511e70c</urn:CorrelationIdentifier>
       |        </ie829:Header>
       |        <ie829:Body>
       |          <ie829:NotificationOfAcceptedExport>
       |            <ie829:Attributes>
       |              <ie829:DateAndTimeOfIssuance>2023-12-19T09:00:00</ie829:DateAndTimeOfIssuance>
       |            </ie829:Attributes>
       |            <ie829:ConsigneeTrader>
       |              <ie829:Traderid>BE345345345</ie829:Traderid>
       |              <ie829:TraderName>PEAR Supermarket</ie829:TraderName>
       |              <ie829:StreetName>Angels Business Park</ie829:StreetName>
       |              <ie829:Postcode>BD1 3NN</ie829:Postcode>
       |              <ie829:City>Bradford</ie829:City>
       |              <ie829:EoriNumber>GB00000578901</ie829:EoriNumber>
       |            </ie829:ConsigneeTrader>
       |            <ie829:ExciseMovementEad>
       |              <ie829:AdministrativeReferenceCode>18GB00000000000232361</ie829:AdministrativeReferenceCode>
       |              <ie829:SequenceNumber>1</ie829:SequenceNumber>
       |              <ie829:ExportDeclarationAcceptanceOrGoodsReleasedForExport>1</ie829:ExportDeclarationAcceptanceOrGoodsReleasedForExport>
       |            </ie829:ExciseMovementEad>
       |            <ie829:ExportPlaceCustomsOffice>
       |              <ie829:ReferenceNumber>GB000383</ie829:ReferenceNumber>
       |            </ie829:ExportPlaceCustomsOffice>
       |            <ie829:ExportDeclarationAcceptanceRelease>
       |              <ie829:ReferenceNumberOfSenderCustomsOffice>GB000101</ie829:ReferenceNumberOfSenderCustomsOffice>
       |              <ie829:IdentificationOfSenderCustomsOfficer>John Doe</ie829:IdentificationOfSenderCustomsOfficer>
       |              <ie829:DateOfAcceptance>2024-02-05</ie829:DateOfAcceptance>
       |              <ie829:DateOfRelease>2024-02-06</ie829:DateOfRelease>
       |              <ie829:DocumentReferenceNumber>645564546</ie829:DocumentReferenceNumber>
       |            </ie829:ExportDeclarationAcceptanceRelease>
       |          </ie829:NotificationOfAcceptedExport>
       |        </ie829:Body>
       |      </ie829:IE829>
       |
       |
       |      <!-- Explanation of Delay to Report a Receipt (IE837) -->
       |      <ie837:IE837 xmlns:ie837="ie837:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE837:V3.13">
       |         <ie837:Header>
       |            <urn:MessageSender>NDEA.GB</urn:MessageSender>
       |            <urn:MessageRecipient>NDEA.GB</urn:MessageRecipient>
       |            <urn:DateOfPreparation>2024-06-18</urn:DateOfPreparation>
       |            <urn:TimeOfPreparation>07:11:31.898476</urn:TimeOfPreparation>
       |            <urn:MessageIdentifier>GB100000000305526</urn:MessageIdentifier>
       |            <urn:CorrelationIdentifier>PORTALb32df82fde8741b4beb3fb832a9cdb76</urn:CorrelationIdentifier>
       |         </ie837:Header>
       |         <ie837:Body>
       |            <ie837:ExplanationOnDelayForDelivery>
       |               <ie837:Attributes>
       |                  <ie837:SubmitterIdentification>GBWK001234569</ie837:SubmitterIdentification>
       |                  <ie837:SubmitterType>1</ie837:SubmitterType>
       |                  <ie837:ExplanationCode>6</ie837:ExplanationCode>
       |                  <ie837:ComplementaryInformation language="en">Lorry crashed off cliff</ie837:ComplementaryInformation>
       |                  <ie837:MessageRole>1</ie837:MessageRole>
       |                  <ie837:DateAndTimeOfValidationOfExplanationOnDelay>2024-06-18T08:11:33</ie837:DateAndTimeOfValidationOfExplanationOnDelay>
       |               </ie837:Attributes>
       |               <ie837:ExciseMovement>
       |                  <ie837:AdministrativeReferenceCode>18GB00000000000232361</ie837:AdministrativeReferenceCode>
       |                  <ie837:SequenceNumber>1</ie837:SequenceNumber>
       |               </ie837:ExciseMovement>
       |            </ie837:ExplanationOnDelayForDelivery>
       |         </ie837:Body>
       |      </ie837:IE837>
       |
       |      <!-- Explanation of Delay to Change of Destination (IE837) -->
       |      <ie837:IE837 xmlns:ie837="ie837:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE837:V3.13">
       |         <ie837:Header>
       |            <urn:MessageSender>NDEA.GB</urn:MessageSender>
       |            <urn:MessageRecipient>NDEA.GB</urn:MessageRecipient>
       |            <urn:DateOfPreparation>2024-06-18</urn:DateOfPreparation>
       |            <urn:TimeOfPreparation>07:18:54.852159</urn:TimeOfPreparation>
       |            <urn:MessageIdentifier>GB100000000305527</urn:MessageIdentifier>
       |            <urn:CorrelationIdentifier>PORTAL07498cf951004becbc3c73c14c103b13</urn:CorrelationIdentifier>
       |         </ie837:Header>
       |         <ie837:Body>
       |            <ie837:ExplanationOnDelayForDelivery>
       |               <ie837:Attributes>
       |                  <ie837:SubmitterIdentification>GBWK001234569</ie837:SubmitterIdentification>
       |                  <ie837:SubmitterType>1</ie837:SubmitterType>
       |                  <ie837:ExplanationCode>5</ie837:ExplanationCode>
       |                  <ie837:MessageRole>2</ie837:MessageRole>
       |                  <ie837:DateAndTimeOfValidationOfExplanationOnDelay>2024-06-18T08:18:56</ie837:DateAndTimeOfValidationOfExplanationOnDelay>
       |               </ie837:Attributes>
       |               <ie837:ExciseMovement>
       |                  <ie837:AdministrativeReferenceCode>18GB00000000000232361</ie837:AdministrativeReferenceCode>
       |                  <ie837:SequenceNumber>1</ie837:SequenceNumber>
       |               </ie837:ExciseMovement>
       |            </ie837:ExplanationOnDelayForDelivery>
       |         </ie837:Body>
       |      </ie837:IE837>
       |    <urn:IE810 xmlns:urn="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE810:V3.01">
       |      <urn:Header>
       |        <urn1:MessageSender xmlns:urn1="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:TMS:V3.01">NDEA.XI</urn1:MessageSender>
       |        <urn1:MessageRecipient xmlns:urn1="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:TMS:V3.01">NDEA.GB</urn1:MessageRecipient>
       |        <urn1:DateOfPreparation xmlns:urn1="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:TMS:V3.01">2021-09-10</urn1:DateOfPreparation>
       |        <urn1:TimeOfPreparation xmlns:urn1="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:TMS:V3.01">11:11:09</urn1:TimeOfPreparation>
       |        <urn1:MessageIdentifier xmlns:urn1="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:TMS:V3.01">XI100000000291919</urn1:MessageIdentifier>
       |        <urn1:CorrelationIdentifier xmlns:urn1="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:TMS:V3.01">PORTAL5a1b930650c54fbca85cf509add5182e</urn1:CorrelationIdentifier>
       |      </urn:Header>
       |      <urn:Body>
       |        <urn:CancellationOfEAD>
       |          <urn:Attributes>
       |            <urn:DateAndTimeOfValidationOfCancellation>2021-09-10T11:11:12</urn:DateAndTimeOfValidationOfCancellation>
       |          </urn:Attributes>
       |          <urn:ExciseMovementEadType language="en">
       |            <urn:AdministrativeReferenceCode>21GB00000000000351266</urn:AdministrativeReferenceCode>
       |          </urn:ExciseMovementEadType>
       |          <urn:Cancellation>
       |            <urn:CancellationReasonCode>0</urn:CancellationReasonCode>
       |            <urn:ComplementaryInformation>some info</urn:ComplementaryInformation>
       |          </urn:Cancellation>
       |        </urn:CancellationOfEAD>
       |      </urn:Body>
       |    </urn:IE810>
       |    </mov:eventHistory>
       |  </mov:movementView>""".stripMargin


  def maxGetMovementResponse(sequenceNumber: Int = 1): GetMovementResponse = GetMovementResponse(
    arc = "ExciseMovementArc",
    sequenceNumber,
    destinationType = DestinationType.TemporaryCertifiedConsignee,
    memberStateCode = Some("CCTMemberStateCode"),
    serialNumberOfCertificateOfExemption = Some("CCTSerialNumber"),
    consignorTrader = TraderModel(
      traderExciseNumber = Some("ConsignorTraderExciseNumber"),
      traderName = Some("ConsignorTraderName"),
      address = Some(
        AddressModel(
          streetNumber = Some("ConsignorTraderStreetNumber"),
          street = Some("ConsignorTraderStreetName"),
          postcode = Some("ConsignorTraderPostcode"),
          city = Some("ConsignorTraderCity")
        )),
      vatNumber = None,
      eoriNumber = None
    ),
    consigneeTrader = Some(
      TraderModel(
        traderExciseNumber = Some("ConsigneeTraderId"),
        traderName = Some("ConsigneeTraderName"),
        address = Some(AddressModel(
          streetNumber = Some("ConsigneeTraderStreetNumber"),
          street = Some("ConsigneeTraderStreetName"),
          postcode = Some("ConsigneeTraderPostcode"),
          city = Some("ConsigneeTraderCity")
        )),
        vatNumber = None,
        eoriNumber = Some("ConsigneeTraderEori")
      )),
    deliveryPlaceTrader = Some(
      TraderModel(
        traderExciseNumber = Some("DeliveryPlaceTraderId"),
        traderName = Some("DeliveryPlaceTraderName"),
        address = Some(AddressModel(
          streetNumber = Some("DeliveryPlaceTraderStreetNumber"),
          street = Some("DeliveryPlaceTraderStreetName"),
          postcode = Some("DeliveryPlaceTraderPostcode"),
          city = Some("DeliveryPlaceTraderCity")
        )),
        vatNumber = None,
        eoriNumber = None
      )),
    placeOfDispatchTrader = Some(
      TraderModel(
        traderExciseNumber = Some("PlaceOfDispatchTraderReferenceOfTaxWarehouse"),
        traderName = Some("PlaceOfDispatchTraderName"),
        address = Some(AddressModel(
          streetNumber = Some("PlaceOfDispatchTraderStreetNumber"),
          street = Some("PlaceOfDispatchTraderStreetName"),
          postcode = Some("PlaceOfDispatchTraderPostcode"),
          city = Some("PlaceOfDispatchTraderCity")
        )),
        vatNumber = None,
        eoriNumber = None
      )),
    transportArrangerTrader = Some(
      TraderModel(
        traderExciseNumber = None,
        traderName = Some("TransportArrangerTraderName"),
        address = Some(AddressModel(
          streetNumber = Some("TransportArrangerTraderStreetNumber"),
          street = Some("TransportArrangerTraderStreetName"),
          postcode = Some("TransportArrangerTraderPostcode"),
          city = Some("TransportArrangerTraderCity")
        )),
        vatNumber = Some("TransportArrangerTraderVatNumber"),
        eoriNumber = None
      )),
    firstTransporterTrader = Some(
      TraderModel(
        traderExciseNumber = None,
        traderName = Some("FirstTransporterTraderName"),
        address = Some(AddressModel(
          streetNumber = Some("FirstTransporterTraderStreetNumber"),
          street = Some("FirstTransporterTraderStreetName"),
          postcode = Some("FirstTransporterTraderPostcode"),
          city = Some("FirstTransporterTraderCity")
        )),
        vatNumber = Some("FirstTransporterTraderVatNumber"),
        eoriNumber = None
      )),
    dispatchImportOfficeReferenceNumber = Some("DispatchImportOfficeErn"),
    deliveryPlaceCustomsOfficeReferenceNumber = Some("DeliveryPlaceCustomsOfficeErn"),
    competentAuthorityDispatchOfficeReferenceNumber = Some("CompetentAuthorityDispatchOfficeErn"),
    localReferenceNumber = "EadEsadLocalReferenceNumber",
    eadStatus = "Beans",
    dateAndTimeOfValidationOfEadEsad = "ExciseMovementDateTime",
    dateOfDispatch = "EadEsadDateOfDispatch",
    journeyTime = "10 hours",
    documentCertificate = Some(
      Seq(
        DocumentCertificateModel(
          documentType = Some("DocumentCertificateDocumentType1"),
          documentReference = Some("DocumentCertificateDocumentReference1"),
          documentDescription = Some("DocumentCertificateDocumentDescription1"),
          referenceOfDocument = Some("DocumentCertificateReferenceOfDocument1")
        ),
        DocumentCertificateModel(
          documentType = Some("DocumentCertificateDocumentType2"),
          documentReference = Some("DocumentCertificateDocumentReference2"),
          documentDescription = Some("DocumentCertificateDocumentDescription2"),
          referenceOfDocument = Some("DocumentCertificateReferenceOfDocument2")
        )
      )
    ),
    eadEsad = EadEsadModel(
      localReferenceNumber = "EadEsadLocalReferenceNumber",
      invoiceNumber = "EadEsadInvoiceNumber",
      invoiceDate = Some("EadEsadInvoiceDate"),
      originTypeCode = OriginType.DutyPaid,
      dateOfDispatch = "EadEsadDateOfDispatch",
      timeOfDispatch = Some("EadEsadTimeOfDispatch"),
      upstreamArc = Some("EadEsadUpstreamArc"),
      importSadNumber = Some(Seq("ImportSadNumber1", "ImportSadNumber2"))
    ),
    headerEadEsad = HeaderEadEsadModel(
      sequenceNumber = 1,
      dateAndTimeOfUpdateValidation = "HeaderEadEsadDateTime",
      destinationType = DestinationType.TemporaryCertifiedConsignee,
      journeyTime = "10 hours",
      transportArrangement = TransportArrangement.Consignee
    ),
    transportMode = TransportModeModel(
      transportModeCode = "TransportModeTransportModeCode",
      complementaryInformation = Some("TransportModeComplementaryInformation")
    ),
    movementGuarantee = MovementGuaranteeModel(
      guarantorTypeCode = GuarantorType.ConsignorTransporterOwner,
      guarantorTrader = Some(
        Seq(
          TraderModel(
            traderExciseNumber = Some("GuarantorTraderErn1"),
            traderName = Some("GuarantorTraderName1"),
            address = Some(
              AddressModel(
                streetNumber = Some("GuarantorTraderStreetNumber1"),
                street = Some("GuarantorTraderStreetName1"),
                postcode = Some("GuarantorTraderPostcode1"),
                city = Some("GuarantorTraderCity1")
              )),
            vatNumber = Some("GuarantorTraderVatNumber1"),
            eoriNumber = None
          ),
          TraderModel(
            traderExciseNumber = Some("GuarantorTraderErn2"),
            traderName = Some("GuarantorTraderName2"),
            address = Some(
              AddressModel(
                streetNumber = Some("GuarantorTraderStreetNumber2"),
                street = Some("GuarantorTraderStreetName2"),
                postcode = Some("GuarantorTraderPostcode2"),
                city = Some("GuarantorTraderCity2")
              )),
            vatNumber = Some("GuarantorTraderVatNumber2"),
            eoriNumber = None
          )
        )
      )
    ),
    items = Seq(
      MovementItem(
        itemUniqueReference = 1,
        productCode = "BodyEadEsadExciseProductCode1",
        cnCode = "BodyEadEsadCnCode1",
        quantity = 2,
        grossMass = 3,
        netMass = 4,
        alcoholicStrength = Some(5),
        degreePlato = Some(6),
        fiscalMark = Some("BodyEadEsadFiscalMark1"),
        fiscalMarkUsedFlag = Some(true),
        designationOfOrigin = Some("BodyEadEsadDesignationOfOrigin1"),
        sizeOfProducer = Some("BodyEadEsadSizeOfProducer1"),
        density = Some(7),
        commercialDescription = Some("BodyEadEsadCommercialDescription1"),
        brandNameOfProduct = Some("BodyEadEsadBrandNameOfProducts1"),
        maturationAge = Some("BodyEadEsadMaturationPeriodOrAgeOfProducts1"),
        independentSmallProducersDeclaration = Some("BodyEadEsadIndependentSmallProducersDeclaration1"),
        packaging = Seq(
          Packaging(
            typeOfPackage = "PackageKindOfPackages11",
            quantity = Some(1),
            shippingMarks = Some("PackageShippingMarks11"),
            identityOfCommercialSeal = Some("PackageCommercialSealIdentification11"),
            sealInformation = Some("PackageSealInformation11")
          ),
          Packaging(
            typeOfPackage = "PackageKindOfPackages12",
            quantity = Some(2),
            shippingMarks = Some("PackageShippingMarks12"),
            identityOfCommercialSeal = Some("PackageCommercialSealIdentification12"),
            sealInformation = Some("PackageSealInformation12")
          )
        ),
        wineProduct = Some(
          WineProduct(
            wineProductCategory = "1",
            wineGrowingZoneCode = Some("WineProductWineGrowingZoneCode1"),
            thirdCountryOfOrigin = Some("WineProductThirdCountryOfOrigin1"),
            otherInformation = Some("WineProductOtherInformation1"),
            wineOperations = Some(Seq("WineOperationCode11", "WineOperationCode12"))
          ))
      ),
      MovementItem(
        itemUniqueReference = 2,
        productCode = "BodyEadEsadExciseProductCode2",
        cnCode = "BodyEadEsadCnCode2",
        quantity = 3,
        grossMass = 4,
        netMass = 5,
        alcoholicStrength = Some(6),
        degreePlato = Some(7),
        fiscalMark = Some("BodyEadEsadFiscalMark2"),
        fiscalMarkUsedFlag = Some(false),
        designationOfOrigin = Some("BodyEadEsadDesignationOfOrigin2"),
        sizeOfProducer = Some("BodyEadEsadSizeOfProducer2"),
        density = Some(8),
        commercialDescription = Some("BodyEadEsadCommercialDescription2"),
        brandNameOfProduct = Some("BodyEadEsadBrandNameOfProducts2"),
        maturationAge = Some("BodyEadEsadMaturationPeriodOrAgeOfProducts2"),
        independentSmallProducersDeclaration = Some("BodyEadEsadIndependentSmallProducersDeclaration2"),
        packaging = Seq(
          Packaging(
            typeOfPackage = "PackageKindOfPackages21",
            quantity = Some(3),
            shippingMarks = Some("PackageShippingMarks21"),
            identityOfCommercialSeal = Some("PackageCommercialSealIdentification21"),
            sealInformation = Some("PackageSealInformation21")
          ),
          Packaging(
            typeOfPackage = "PackageKindOfPackages22",
            quantity = Some(4),
            shippingMarks = Some("PackageShippingMarks22"),
            identityOfCommercialSeal = Some("PackageCommercialSealIdentification22"),
            sealInformation = Some("PackageSealInformation22")
          )
        ),
        wineProduct = Some(
          WineProduct(
            wineProductCategory = "2",
            wineGrowingZoneCode = Some("WineProductWineGrowingZoneCode2"),
            thirdCountryOfOrigin = Some("WineProductThirdCountryOfOrigin2"),
            otherInformation = Some("WineProductOtherInformation2"),
            wineOperations = Some(Seq("WineOperationCode21", "WineOperationCode22"))
          ))
      )
    ),
    numberOfItems = 2,
    transportDetails = Seq(
      TransportDetailsModel(
        transportUnitCode = "TransportDetailsTransportUnitCode1",
        identityOfTransportUnits = Some("TransportDetailsIdentityOfTransportUnits1"),
        commercialSealIdentification = Some("TransportDetailsCommercialSealIdentification1"),
        complementaryInformation = Some("TransportDetailsComplementaryInformation1"),
        sealInformation = Some("TransportDetailsSealInformation1")
      ),
      TransportDetailsModel(
        transportUnitCode = "TransportDetailsTransportUnitCode2",
        identityOfTransportUnits = Some("TransportDetailsIdentityOfTransportUnits2"),
        commercialSealIdentification = Some("TransportDetailsCommercialSealIdentification2"),
        complementaryInformation = Some("TransportDetailsComplementaryInformation2"),
        sealInformation = Some("TransportDetailsSealInformation2")
      )
    ),
    movementViewHistoryAndExtraData = MovementViewHistoryAndExtraDataModel(
      arc = "ExciseMovementArc",
      serialNumberOfCertificateOfExemption = Some("CCTSerialNumber"),
      dispatchImportOfficeReferenceNumber = Some("DispatchImportOfficeErn"),
      deliveryPlaceCustomsOfficeReferenceNumber = Some("DeliveryPlaceCustomsOfficeErn"),
      competentAuthorityDispatchOfficeReferenceNumber = Some("CompetentAuthorityDispatchOfficeErn"),
      eadStatus = "Beans",
      dateAndTimeOfValidationOfEadEsad = "ExciseMovementDateTime",
      numberOfItems = 2,
      reportOfReceipt = Some(SubmitReportOfReceiptModel(
        arc = "21GB00000000000351266",
        sequenceNumber = 2,
        dateAndTimeOfValidationOfReportOfReceiptExport = Some("2021-09-10T11:11:12"),
        consigneeTrader = Some(
          TraderModel(
            traderExciseNumber = Some("XIWK000000206"),
            traderName = Some("SEED TRADER NI"),
            address = Some(
              AddressModel(
                streetNumber = Some("1"),
                street = Some("Catherdral"),
                postcode = Some("BT3 7BF"),
                city = Some("Salford")
              )),
            vatNumber = None,
            eoriNumber = None
          )
        ),
        deliveryPlaceTrader = Some(
          TraderModel(
            traderExciseNumber = Some("XI00000000207"),
            traderName = Some("SEED TRADER NI 2"),
            address = Some(
              AddressModel(
                streetNumber = Some("2"),
                street = Some("Catherdral"),
                postcode = Some("BT3 7BF"),
                city = Some("Salford")
              )),
            vatNumber = None,
            eoriNumber = None
          )
        ),
        destinationOffice = "XI004098",
        dateOfArrival = LocalDate.parse("2021-09-08"),
        acceptMovement = Satisfactory,
        otherInformation = Some("some great reason"),
        destinationType = None,
        individualItems = Seq(ReceiptedItemsModel(
          eadBodyUniqueReference = 1,
          excessAmount = Some(21),
          shortageAmount = None,
          productCode = "W300",
          refusedAmount = Some(1),
          unsatisfactoryReasons = Seq(
            UnsatisfactoryModel(Excess, Some("some info")),
            UnsatisfactoryModel(Shortage, None),
          )
        ))
      )),
      notificationOfDivertedMovement = Some(NotificationOfDivertedMovementModel(
        notificationType = SplitMovement,
        notificationDateAndTime = LocalDateTime.of(2024, 6, 5, 0, 0, 1),
        downstreamArcs = Seq(testArc, s"${testArc.dropRight(1)}1")
      )),
      notificationOfAlertOrRejection = Seq(
        NotificationOfAlertOrRejectionModel(
          notificationType = Alert,
          notificationDateAndTime = LocalDateTime.of(2023, 12, 18, 9, 0, 0),
          alertRejectReason = Seq(
            AlertOrRejectionReasonModel(
              reason = ProductDoesNotMatchOrder,
              additionalInformation = Some("Info")
            ),
            AlertOrRejectionReasonModel(
              reason = EADNotConcernRecipient,
              additionalInformation = Some("Info")
            ),
            AlertOrRejectionReasonModel(
              reason = Other,
              additionalInformation = Some("Info")
            ),
            AlertOrRejectionReasonModel(
              reason = QuantityDoesNotMatchOrder,
              additionalInformation = Some("Info")
            )
          )
        ),
        NotificationOfAlertOrRejectionModel(
          notificationType = Alert,
          notificationDateAndTime = LocalDateTime.of(2023, 12, 18, 10, 0, 0),
          alertRejectReason = Seq(AlertOrRejectionReasonModel(
            reason = EADNotConcernRecipient,
            additionalInformation = None
          ))
        ),
        NotificationOfAlertOrRejectionModel(
          notificationType = Rejection,
          notificationDateAndTime = LocalDateTime.of(2023, 12, 19, 9, 0, 0),
          alertRejectReason = Seq(AlertOrRejectionReasonModel(
            reason = QuantityDoesNotMatchOrder,
            additionalInformation = None
          ))
        )
      ),
      notificationOfAcceptedExport = Some(
        NotificationOfAcceptedExportModel(
          customsOfficeNumber = "GB000383",
          dateOfAcceptance = LocalDate.of(2024, 2, 5),
          referenceNumberOfSenderCustomsOffice = "GB000101",
          identificationOfSenderCustomsOfficer = "John Doe",
          documentReferenceNumber = "645564546",
          consigneeTrader = TraderModel(
            traderExciseNumber = Some("BE345345345"),
            traderName = Some("PEAR Supermarket"),
            address = Some(
              AddressModel(
                streetNumber = None,
                street = Some("Angels Business Park"),
                postcode = Some("BD1 3NN"),
                city = Some("Bradford")
              )),
            vatNumber = None,
            eoriNumber = Some("GB00000578901")
          )
        )
      ),
      notificationOfDelay = Seq(
        NotificationOfDelayModel(
          submitterIdentification = "GBWK001234569",
          submitterType = SubmitterType.Consignor,
          explanationCode = DelayReasonType.Accident,
          complementaryInformation = Some("Lorry crashed off cliff"),
          dateTime = LocalDateTime.parse("2024-06-18T08:11:33")
        ),
        NotificationOfDelayModel(
          submitterIdentification = "GBWK001234569",
          submitterType = SubmitterType.Consignor,
          explanationCode = DelayReasonType.Strikes,
          complementaryInformation = None,
          dateTime = LocalDateTime.parse("2024-06-18T08:18:56")
        )
      ),
      cancelMovement = Some(CancellationReasonModel(CancellationReasonType.Other, Some("some info")))
    )
  )

  def maxGetMovementJson(sequenceNumber: Int = 1): JsObject = Json.obj(fields =
    "arc"                                  -> "ExciseMovementArc",
    "sequenceNumber"                       -> sequenceNumber,
    "destinationType"                      -> "10",
    "memberStateCode"                      -> "CCTMemberStateCode",
    "serialNumberOfCertificateOfExemption" -> "CCTSerialNumber",
    "consignorTrader" -> Json.obj(
      "traderExciseNumber" -> "ConsignorTraderExciseNumber",
      "traderName"         -> "ConsignorTraderName",
      "address" -> Json.obj(
        "streetNumber" -> "ConsignorTraderStreetNumber",
        "street"       -> "ConsignorTraderStreetName",
        "postcode"     -> "ConsignorTraderPostcode",
        "city"         -> "ConsignorTraderCity"
      )
    ),
    "consigneeTrader" -> Json.obj(
      "traderExciseNumber" -> "ConsigneeTraderId",
      "traderName"         -> "ConsigneeTraderName",
      "address" -> Json.obj(
        "streetNumber" -> "ConsigneeTraderStreetNumber",
        "street"       -> "ConsigneeTraderStreetName",
        "postcode"     -> "ConsigneeTraderPostcode",
        "city"         -> "ConsigneeTraderCity"
      ),
      "eoriNumber" -> "ConsigneeTraderEori"
    ),
    "deliveryPlaceTrader" -> Json.obj(
      "traderExciseNumber" -> "DeliveryPlaceTraderId",
      "traderName"         -> "DeliveryPlaceTraderName",
      "address" -> Json.obj(
        "streetNumber" -> "DeliveryPlaceTraderStreetNumber",
        "street"       -> "DeliveryPlaceTraderStreetName",
        "postcode"     -> "DeliveryPlaceTraderPostcode",
        "city"         -> "DeliveryPlaceTraderCity"
      )
    ),
    "placeOfDispatchTrader" -> Json.obj(
      "traderExciseNumber" -> "PlaceOfDispatchTraderReferenceOfTaxWarehouse",
      "traderName"         -> "PlaceOfDispatchTraderName",
      "address" -> Json.obj(
        "streetNumber" -> "PlaceOfDispatchTraderStreetNumber",
        "street"       -> "PlaceOfDispatchTraderStreetName",
        "postcode"     -> "PlaceOfDispatchTraderPostcode",
        "city"         -> "PlaceOfDispatchTraderCity"
      )
    ),
    "transportArrangerTrader" -> Json.obj(
      "traderName" -> "TransportArrangerTraderName",
      "address" -> Json.obj(
        "streetNumber" -> "TransportArrangerTraderStreetNumber",
        "street"       -> "TransportArrangerTraderStreetName",
        "postcode"     -> "TransportArrangerTraderPostcode",
        "city"         -> "TransportArrangerTraderCity"
      ),
      "vatNumber" -> "TransportArrangerTraderVatNumber"
    ),
    "firstTransporterTrader" -> Json.obj(
      "traderName" -> "FirstTransporterTraderName",
      "address" -> Json.obj(
        "streetNumber" -> "FirstTransporterTraderStreetNumber",
        "street"       -> "FirstTransporterTraderStreetName",
        "postcode"     -> "FirstTransporterTraderPostcode",
        "city"         -> "FirstTransporterTraderCity"
      ),
      "vatNumber" -> "FirstTransporterTraderVatNumber"
    ),
    "dispatchImportOfficeReferenceNumber"             -> "DispatchImportOfficeErn",
    "deliveryPlaceCustomsOfficeReferenceNumber"       -> "DeliveryPlaceCustomsOfficeErn",
    "competentAuthorityDispatchOfficeReferenceNumber" -> "CompetentAuthorityDispatchOfficeErn",
    "localReferenceNumber"                            -> "EadEsadLocalReferenceNumber",
    "eadStatus"                                       -> "Beans",
    "dateAndTimeOfValidationOfEadEsad"                -> "ExciseMovementDateTime",
    "dateOfDispatch"                                  -> "EadEsadDateOfDispatch",
    "journeyTime"                                     -> "10 hours",
    "documentCertificate" -> Json.arr(
      Json.obj(
        "documentType"        -> "DocumentCertificateDocumentType1",
        "documentReference"   -> "DocumentCertificateDocumentReference1",
        "documentDescription" -> "DocumentCertificateDocumentDescription1",
        "referenceOfDocument" -> "DocumentCertificateReferenceOfDocument1"
      ),
      Json.obj(
        "documentType"        -> "DocumentCertificateDocumentType2",
        "documentReference"   -> "DocumentCertificateDocumentReference2",
        "documentDescription" -> "DocumentCertificateDocumentDescription2",
        "referenceOfDocument" -> "DocumentCertificateReferenceOfDocument2"
      )
    ),
    "eadEsad" -> Json.obj(
      "localReferenceNumber" -> "EadEsadLocalReferenceNumber",
      "invoiceNumber"        -> "EadEsadInvoiceNumber",
      "invoiceDate"          -> "EadEsadInvoiceDate",
      "originTypeCode"       -> "3",
      "dateOfDispatch"       -> "EadEsadDateOfDispatch",
      "timeOfDispatch"       -> "EadEsadTimeOfDispatch",
      "upstreamArc"          -> "EadEsadUpstreamArc",
      "importSadNumber"      -> Json.arr("ImportSadNumber1", "ImportSadNumber2")
    ),
    "headerEadEsad" -> Json.obj(
      "sequenceNumber"                -> 1,
      "dateAndTimeOfUpdateValidation" -> "HeaderEadEsadDateTime",
      "destinationType"               -> "10",
      "journeyTime"                   -> "10 hours",
      "transportArrangement"          -> "2"
    ),
    "transportMode" -> Json.obj(
      "transportModeCode"        -> "TransportModeTransportModeCode",
      "complementaryInformation" -> "TransportModeComplementaryInformation"
    ),
    "movementGuarantee" -> Json.obj(
      "guarantorTypeCode" -> "123",
      "guarantorTrader" -> Json.arr(
        Json.obj(
          "traderExciseNumber" -> "GuarantorTraderErn1",
          "traderName"         -> "GuarantorTraderName1",
          "address" -> Json.obj(
            "streetNumber" -> "GuarantorTraderStreetNumber1",
            "street"       -> "GuarantorTraderStreetName1",
            "postcode"     -> "GuarantorTraderPostcode1",
            "city"         -> "GuarantorTraderCity1"
          ),
          "vatNumber" -> "GuarantorTraderVatNumber1"
        ),
        Json.obj(
          "traderExciseNumber" -> "GuarantorTraderErn2",
          "traderName"         -> "GuarantorTraderName2",
          "address" -> Json.obj(
            "streetNumber" -> "GuarantorTraderStreetNumber2",
            "street"       -> "GuarantorTraderStreetName2",
            "postcode"     -> "GuarantorTraderPostcode2",
            "city"         -> "GuarantorTraderCity2"
          ),
          "vatNumber" -> "GuarantorTraderVatNumber2"
        )
      )
    ),
    "items" -> Json.arr(
      Json.obj(
        "itemUniqueReference"   -> 1,
        "productCode"           -> "BodyEadEsadExciseProductCode1",
        "cnCode"                -> "BodyEadEsadCnCode1",
        "quantity"              -> 2,
        "grossMass"             -> 3,
        "netMass"               -> 4,
        "alcoholicStrength"     -> 5,
        "degreePlato"           -> 6,
        "fiscalMark"            -> "BodyEadEsadFiscalMark1",
        "fiscalMarkUsedFlag"    -> true,
        "designationOfOrigin"   -> "BodyEadEsadDesignationOfOrigin1",
        "sizeOfProducer"        -> "BodyEadEsadSizeOfProducer1",
        "density"               -> 7,
        "commercialDescription" -> "BodyEadEsadCommercialDescription1",
        "brandNameOfProduct"    -> "BodyEadEsadBrandNameOfProducts1",
        "maturationAge"         -> "BodyEadEsadMaturationPeriodOrAgeOfProducts1",
        "independentSmallProducersDeclaration" -> "BodyEadEsadIndependentSmallProducersDeclaration1",
        "packaging" -> Json.arr(
          Json.obj(
            "typeOfPackage"            -> "PackageKindOfPackages11",
            "quantity"                 -> 1,
            "shippingMarks"            -> "PackageShippingMarks11",
            "identityOfCommercialSeal" -> "PackageCommercialSealIdentification11",
            "sealInformation"          -> "PackageSealInformation11"
          ),
          Json.obj(
            "typeOfPackage"            -> "PackageKindOfPackages12",
            "quantity"                 -> 2,
            "shippingMarks"            -> "PackageShippingMarks12",
            "identityOfCommercialSeal" -> "PackageCommercialSealIdentification12",
            "sealInformation"          -> "PackageSealInformation12"
          )
        ),
        "wineProduct" -> Json.obj(
          "wineProductCategory"  -> "1",
          "wineGrowingZoneCode"  -> "WineProductWineGrowingZoneCode1",
          "thirdCountryOfOrigin" -> "WineProductThirdCountryOfOrigin1",
          "otherInformation"     -> "WineProductOtherInformation1",
          "wineOperations"       -> Json.arr("WineOperationCode11", "WineOperationCode12")
        )
      ),
      Json.obj(
        "itemUniqueReference"   -> 2,
        "productCode"           -> "BodyEadEsadExciseProductCode2",
        "cnCode"                -> "BodyEadEsadCnCode2",
        "quantity"              -> 3,
        "grossMass"             -> 4,
        "netMass"               -> 5,
        "alcoholicStrength"     -> 6,
        "degreePlato"           -> 7,
        "fiscalMark"            -> "BodyEadEsadFiscalMark2",
        "fiscalMarkUsedFlag"    -> false,
        "designationOfOrigin"   -> "BodyEadEsadDesignationOfOrigin2",
        "sizeOfProducer"        -> "BodyEadEsadSizeOfProducer2",
        "density"               -> 8,
        "commercialDescription" -> "BodyEadEsadCommercialDescription2",
        "brandNameOfProduct"    -> "BodyEadEsadBrandNameOfProducts2",
        "maturationAge"         -> "BodyEadEsadMaturationPeriodOrAgeOfProducts2",
        "independentSmallProducersDeclaration" -> "BodyEadEsadIndependentSmallProducersDeclaration2",
        "packaging" -> Json.arr(
          Json.obj(
            "typeOfPackage"            -> "PackageKindOfPackages21",
            "quantity"                 -> 3,
            "shippingMarks"            -> "PackageShippingMarks21",
            "identityOfCommercialSeal" -> "PackageCommercialSealIdentification21",
            "sealInformation"          -> "PackageSealInformation21"
          ),
          Json.obj(
            "typeOfPackage"            -> "PackageKindOfPackages22",
            "quantity"                 -> 4,
            "shippingMarks"            -> "PackageShippingMarks22",
            "identityOfCommercialSeal" -> "PackageCommercialSealIdentification22",
            "sealInformation"          -> "PackageSealInformation22"
          )
        ),
        "wineProduct" -> Json.obj(
          "wineProductCategory"  -> "2",
          "wineGrowingZoneCode"  -> "WineProductWineGrowingZoneCode2",
          "thirdCountryOfOrigin" -> "WineProductThirdCountryOfOrigin2",
          "otherInformation"     -> "WineProductOtherInformation2",
          "wineOperations"       -> Json.arr("WineOperationCode21", "WineOperationCode22")
        )
      )
    ),
    "numberOfItems" -> 2,
    "transportDetails" -> Json.arr(
      Json.obj(
        "transportUnitCode"            -> "TransportDetailsTransportUnitCode1",
        "identityOfTransportUnits"     -> "TransportDetailsIdentityOfTransportUnits1",
        "commercialSealIdentification" -> "TransportDetailsCommercialSealIdentification1",
        "complementaryInformation"     -> "TransportDetailsComplementaryInformation1",
        "sealInformation"              -> "TransportDetailsSealInformation1"
      ),
      Json.obj(
        "transportUnitCode"            -> "TransportDetailsTransportUnitCode2",
        "identityOfTransportUnits"     -> "TransportDetailsIdentityOfTransportUnits2",
        "commercialSealIdentification" -> "TransportDetailsCommercialSealIdentification2",
        "complementaryInformation"     -> "TransportDetailsComplementaryInformation2",
        "sealInformation"              -> "TransportDetailsSealInformation2"
      )
    ),
    "reportOfReceipt" -> Json.obj(
      "arc" -> "21GB00000000000351266",
      "sequenceNumber" -> 2,
      "dateAndTimeOfValidationOfReportOfReceiptExport" -> "2021-09-10T11:11:12",
      "consigneeTrader" -> Json.obj(
        "traderExciseNumber" -> "XIWK000000206",
        "traderName"         -> "SEED TRADER NI",
        "address" -> Json.obj(
          "streetNumber" -> "1",
          "street"       -> "Catherdral",
          "postcode"     -> "BT3 7BF",
          "city"         -> "Salford"
        )
      ),
      "deliveryPlaceTrader" -> Json.obj(
        "traderExciseNumber" -> "XI00000000207",
        "traderName"         -> "SEED TRADER NI 2",
        "address" -> Json.obj(
          "streetNumber" -> "2",
          "street"       -> "Catherdral",
          "postcode"     -> "BT3 7BF",
          "city"         -> "Salford"
        )
      ),
      "destinationOffice" -> "XI004098",
      "dateOfArrival" -> "2021-09-08",
      "acceptMovement" -> "satisfactory",
      "individualItems" -> Json.arr(
        Json.obj(
          "eadBodyUniqueReference" -> 1,
          "productCode" -> "W300",
          "excessAmount" -> 21,
          "refusedAmount" -> 1,
          "unsatisfactoryReasons" -> Json.arr(
            Json.obj(
              "reason" -> "excess",
              "additionalInformation" -> "some info"
            ),
            Json.obj(
              "reason" -> "shortage"
            )
          )
        )
      ),
      "otherInformation" -> "some great reason"
    ),
    "notificationOfDivertedMovement" -> Json.obj(
      "notificationType" -> "2",
      "notificationDateAndTime" -> "2024-06-05T00:00:01",
      "downstreamArcs" -> Json.arr(
        testArc, s"${testArc.dropRight(1)}1"
      )
    ),
    "notificationOfAlertOrRejection" -> Json.arr(
      Json.obj(
        "notificationType" -> "0",
        "notificationDateAndTime" -> "2023-12-18T09:00:00",
        "alertRejectReason" -> Json.arr(
          Json.obj(
            "reason" -> "2",
            "additionalInformation" -> "Info"
          ),
          Json.obj(
            "reason" -> "1",
            "additionalInformation" -> "Info"
          ),
          Json.obj(
            "reason" -> "0",
            "additionalInformation" -> "Info"
          ),
          Json.obj(
            "reason" -> "3",
            "additionalInformation" -> "Info"
          )
        )
      ),
      Json.obj(
        "notificationType" -> "0",
        "notificationDateAndTime" -> "2023-12-18T10:00:00",
        "alertRejectReason" -> Json.arr(
          Json.obj(
            "reason" -> "1"
          )
        )
      ),
      Json.obj(
        "notificationType" -> "1",
        "notificationDateAndTime" -> "2023-12-19T09:00:00",
        "alertRejectReason" -> Json.arr(
          Json.obj(
            "reason" -> "3"
          )
        )
      )
    ),
    "notificationOfAcceptedExport" -> Json.obj(
      "customsOfficeNumber" -> "GB000383",
      "dateOfAcceptance" -> "2024-02-05",
      "referenceNumberOfSenderCustomsOffice" -> "GB000101",
      "identificationOfSenderCustomsOfficer" -> "John Doe",
      "documentReferenceNumber" -> "645564546",
      "consigneeTrader" -> Json.obj(
        "traderExciseNumber" -> "BE345345345",
        "traderName"         -> "PEAR Supermarket",
        "address" -> Json.obj(
          "street"   -> "Angels Business Park",
          "postcode" -> "BD1 3NN",
          "city"     -> "Bradford"
        ),
        "eoriNumber" -> "GB00000578901"
      )
    ),
    "notificationOfDelay" -> Json.arr(
      Json.obj(fields =
        "submitterIdentification" -> "GBWK001234569",
        "submitterType" -> "1",
        "explanationCode" -> "6",
        "complementaryInformation" -> "Lorry crashed off cliff",
        "dateTime" -> "2024-06-18T08:11:33"
      ),
      Json.obj(fields =
        "submitterIdentification" -> "GBWK001234569",
        "submitterType" -> "1",
        "explanationCode" -> "5",
        "dateTime" -> "2024-06-18T08:18:56"
      )
    ),
    "cancelMovement" -> Json.obj(
      "reason" -> "0",
      "complementaryInformation" -> "some info",
    )
  )

  def getMovementMongoResponse(sequenceNumber: Int = 1): GetMovementMongoResponse = GetMovementMongoResponse(testArc, sequenceNumber, data = JsString(getMovementResponseBody(sequenceNumber)))

}
