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

package uk.gov.hmrc.emcstfe.models.response.getMovement

import com.lucidchart.open.xtract.{EmptyError, ParseFailure, ParseSuccess}
import play.api.libs.json.Json
import uk.gov.hmrc.emcstfe.fixtures.GetMovementFixture
import uk.gov.hmrc.emcstfe.models.common.AcceptMovement.Satisfactory
import uk.gov.hmrc.emcstfe.models.common.JourneyTime.JourneyTimeParseFailure
import uk.gov.hmrc.emcstfe.models.common._
import uk.gov.hmrc.emcstfe.models.reportOfReceipt.SubmitReportOfReceiptModel
import uk.gov.hmrc.emcstfe.models.response.Packaging
import uk.gov.hmrc.emcstfe.models.response.getMovement.GetMovementResponse.EADESADContainer
import uk.gov.hmrc.emcstfe.models.response.getMovement.NotificationOfDivertedMovementType.SplitMovement
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import java.time.{LocalDate, LocalDateTime}
import scala.xml.XML

class GetMovementResponseSpec extends TestBaseSpec with GetMovementFixture {

  "writes" should {
    "write a model to JSON" in {
      Json.toJson(getMovementResponse()) shouldBe getMovementJson()
    }
    "write a max model to JSON" in {
      Json.toJson(maxGetMovementResponse()) shouldBe maxGetMovementJson()
    }
  }

  "xmlReads" should {

    "successfully read a movement" when {

      "all fields are valid" in {
        GetMovementResponse.xmlReader.read(XML.loadString(getMovementResponseBody())) shouldBe ParseSuccess(getMovementResponse())
      }

      "maximum fields" in {
        GetMovementResponse.xmlReader.read(XML.loadString(maxGetMovementResponseBody())) shouldBe ParseSuccess(maxGetMovementResponse())
      }

      "duplicate CnCodes" in {

        GetMovementResponse.xmlReader.read(XML.loadString(
          // There are three CnCode values here but only two unique ones
          s"""
            |<mov:movementView xsi:schemaLocation="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3 movementView.xsd"
            |	xmlns:mov="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3">
            |	<mov:currentMovement>
            |		<mov:status>Accepted</mov:status>
            |		<mov:version_transaction_ref>008</mov:version_transaction_ref>
            |		<urn:IE801
            |			xmlns:body="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE801:V3.01">
            |			<urn:Header
            |				xmlns:head="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:TMS:V2.02">
            |				<head:MessageSender>NDEA.FR</head:MessageSender>
            |				<head:MessageRecipient>NDEA.GB</head:MessageRecipient>
            |				<head:DateOfPreparation>2008-09-04</head:DateOfPreparation>
            |				<head:TimeOfPreparation>10:22:50</head:TimeOfPreparation>
            |				<head:MessageIdentifier>Message identifier</head:MessageIdentifier>
            |			</urn:Header>
            |			<urn:Body>
            |				<urn:EADESADContainer>
            |					<urn:ConsigneeTrader language="en">
            |						${maxTraderModelXML(ConsigneeTrader)}
            |					</urn:ConsigneeTrader>
            |        <body:ComplementConsigneeTrader>
            |          <MemberStateCode>GB</MemberStateCode>
            |        </body:ComplementConsigneeTrader>
            |					<urn:ExciseMovement>
            |						<urn:AdministrativeReferenceCode>13AB7778889991ABCDEF9</urn:AdministrativeReferenceCode>
            |						<urn:DateAndTimeOfValidationOfEadEsad>2008-09-04T10:22:50</urn:DateAndTimeOfValidationOfEadEsad>
            |					</urn:ExciseMovement>
            |					<urn:ConsignorTrader language="en">
            |						${maxTraderModelXML(ConsignorTrader)}
            |					</urn:ConsignorTrader>
            |					<urn:PlaceOfDispatchTrader language="en">
            |						${maxTraderModelXML(PlaceOfDispatchTrader)}
            |					</urn:PlaceOfDispatchTrader>
            |					<urn:DeliveryPlaceCustomsOffice>
            |						<urn:ReferenceNumber>FR000003</urn:ReferenceNumber>
            |					</urn:DeliveryPlaceCustomsOffice>
            |					<urn:CompetentAuthorityDispatchOffice>
            |						<urn:ReferenceNumber>GB000002</urn:ReferenceNumber>
            |					</urn:CompetentAuthorityDispatchOffice>
            |					<urn:FirstTransporterTrader language="en">
            |						${maxTraderModelXML(TransportTrader)}
            |					</urn:FirstTransporterTrader>
            |					<urn:DocumentCertificate>
            |						<urn:DocumentDescription language="en">Test</urn:DocumentDescription>
            |						<urn:ReferenceOfDocument language="en">AB123</urn:ReferenceOfDocument>
            |					</urn:DocumentCertificate>
            |					<urn:EadEsad>
            |						<urn:LocalReferenceNumber>EN</urn:LocalReferenceNumber>
            |						<urn:InvoiceNumber>IN777888999</urn:InvoiceNumber>
            |						<urn:InvoiceDate>2008-09-04</urn:InvoiceDate>
            |						<urn:OriginTypeCode>1</urn:OriginTypeCode>
            |						<urn:DateOfDispatch>2008-11-20</urn:DateOfDispatch>
            |						<urn:TimeOfDispatch>10:00:00</urn:TimeOfDispatch>
            |					</urn:EadEsad>
            |					<urn:HeaderEadEsad>
            |						<urn:SequenceNumber>1</urn:SequenceNumber>
            |						<urn:DateAndTimeOfUpdateValidation>2008-09-04T10:22:50</urn:DateAndTimeOfUpdateValidation>
            |						<urn:DestinationTypeCode>6</urn:DestinationTypeCode>
            |						<urn:JourneyTime>D20</urn:JourneyTime>
            |						<urn:TransportArrangement>1</urn:TransportArrangement>
            |					</urn:HeaderEadEsad>
            |					<urn:TransportMode>
            |						<urn:TransportModeCode>1</urn:TransportModeCode>
            |					</urn:TransportMode>
            |					<urn:MovementGuarantee>
            |						<urn:GuarantorTypeCode>0</urn:GuarantorTypeCode>
            |					</urn:MovementGuarantee>
            |					<urn:BodyEadEsad>
            |           <urn:BodyRecordUniqueReference>1</urn:BodyRecordUniqueReference>
            |           <urn:ExciseProductCode>W200</urn:ExciseProductCode>
            |           <urn:CnCode>22041011</urn:CnCode>
            |           <urn:Quantity>500</urn:Quantity>
            |           <urn:GrossMass>900</urn:GrossMass>
            |           <urn:NetMass>375</urn:NetMass>
            |           <urn:FiscalMark language="en">FM564789 Fiscal Mark</urn:FiscalMark>
            |           <urn:FiscalMarkUsedFlag>1</urn:FiscalMarkUsedFlag>
            |           <urn:MaturationPeriodOrAgeOfProducts language="EN">Maturation Period</urn:MaturationPeriodOrAgeOfProducts>
            |           <urn:IndependentSmallProducersDeclaration language="EN">Independent Small Producers Declaration</urn:IndependentSmallProducersDeclaration>
            |           <urn:DesignationOfOrigin language="en">Designation of Origin</urn:DesignationOfOrigin>
            |           <urn:DegreePlato>1.2</urn:DegreePlato>
            |           <urn:SizeOfProducer>20000</urn:SizeOfProducer>
            |           <urn:Density>880</urn:Density>
            |           <urn:CommercialDescription language="en">Retsina</urn:CommercialDescription>
            |           <urn:BrandNameOfProducts language="en">MALAMATINA</urn:BrandNameOfProducts>
            |           <urn:Package>
            |             <urn:KindOfPackages>BO</urn:KindOfPackages>
            |             <urn:NumberOfPackages>125</urn:NumberOfPackages>
            |             <urn:ShippingMarks>MARKS</urn:ShippingMarks>
            |             <urn:CommercialSealIdentification>SEAL456789321</urn:CommercialSealIdentification>
            |             <urn:SealInformation language="en">Red Strip</urn:SealInformation>
            |           </urn:Package>
            |           <urn:WineProduct>
            |             <urn:WineProductCategory>4</urn:WineProductCategory>
            |             <urn:WineGrowingZoneCode>2</urn:WineGrowingZoneCode>
            |             <urn:ThirdCountryOfOrigin>FJ</urn:ThirdCountryOfOrigin>
            |             <urn:OtherInformation language="en">Not available</urn:OtherInformation>
            |             <urn:WineOperation>
            |               <urn:WineOperationCode>4</urn:WineOperationCode>
            |             </urn:WineOperation>
            |             <urn:WineOperation>
            |               <urn:WineOperationCode>5</urn:WineOperationCode>
            |             </urn:WineOperation>
            |           </urn:WineProduct>
            |					</urn:BodyEadEsad>
            |					<urn:BodyEadEsad>
            |						<urn:BodyRecordUniqueReference>2</urn:BodyRecordUniqueReference>
            |						<urn:ExciseProductCode>W300</urn:ExciseProductCode>
            |						<urn:CnCode>27111901</urn:CnCode>
            |						<urn:Quantity>501</urn:Quantity>
            |						<urn:GrossMass>901</urn:GrossMass>
            |						<urn:NetMass>475</urn:NetMass>
            |          <urn:AlcoholicStrengthByVolumeInPercentage>12.7</urn:AlcoholicStrengthByVolumeInPercentage>
            |						<urn:FiscalMark language="en">FM564790 Fiscal Mark</urn:FiscalMark>
            |						<urn:FiscalMarkUsedFlag>1</urn:FiscalMarkUsedFlag>
            |						<urn:DesignationOfOrigin language="en">Designation of Origin</urn:DesignationOfOrigin>
            |						<urn:SizeOfProducer>20000</urn:SizeOfProducer>
            |						<urn:CommercialDescription language="en">Retsina</urn:CommercialDescription>
            |						<urn:BrandNameOfProducts language="en">BrandName</urn:BrandNameOfProducts>
            |          <urn:Package>
            |             <urn:KindOfPackages>BO</urn:KindOfPackages>
            |             <urn:NumberOfPackages>125</urn:NumberOfPackages>
            |             <urn:CommercialSealIdentification>SEAL456789321</urn:CommercialSealIdentification>
            |             <urn:SealInformation language="en">Red Strip</urn:SealInformation>
            |           </urn:Package>
            |           <urn:Package>
            |             <urn:KindOfPackages>HG</urn:KindOfPackages>
            |             <urn:NumberOfPackages>7</urn:NumberOfPackages>
            |             <urn:CommercialSealIdentification>SEAL77</urn:CommercialSealIdentification>
            |             <urn:SealInformation language="en">Cork</urn:SealInformation>
            |           </urn:Package>
            |           <urn:WineProduct>
            |             <urn:WineProductCategory>3</urn:WineProductCategory>
            |             <urn:ThirdCountryOfOrigin>FJ</urn:ThirdCountryOfOrigin>
            |             <urn:OtherInformation language="en">Not available</urn:OtherInformation>
            |             <urn:WineOperation>
            |               <urn:WineOperationCode>0</urn:WineOperationCode>
            |             </urn:WineOperation>
            |             <urn:WineOperation>
            |               <urn:WineOperationCode>1</urn:WineOperationCode>
            |             </urn:WineOperation>
            |           </urn:WineProduct>
            |					</urn:BodyEadEsad>
            |					<urn:BodyEadEsad>
            |						<urn:BodyRecordUniqueReference>3</urn:BodyRecordUniqueReference>
            |						<urn:ExciseProductCode>W300</urn:ExciseProductCode>
            |						<urn:CnCode>27111901</urn:CnCode>
            |						<urn:Quantity>501</urn:Quantity>
            |						<urn:GrossMass>901</urn:GrossMass>
            |						<urn:NetMass>475</urn:NetMass>
            |          <urn:AlcoholicStrengthByVolumeInPercentage>12.7</urn:AlcoholicStrengthByVolumeInPercentage>
            |						<urn:FiscalMark language="en">FM564790 Fiscal Mark</urn:FiscalMark>
            |						<urn:FiscalMarkUsedFlag>1</urn:FiscalMarkUsedFlag>
            |						<urn:DesignationOfOrigin language="en">Designation of Origin</urn:DesignationOfOrigin>
            |						<urn:SizeOfProducer>20000</urn:SizeOfProducer>
            |						<urn:CommercialDescription language="en">Retsina</urn:CommercialDescription>
            |						<urn:BrandNameOfProducts language="en">BrandName</urn:BrandNameOfProducts>
            |          <urn:Package>
            |             <urn:KindOfPackages>BO</urn:KindOfPackages>
            |             <urn:NumberOfPackages>150</urn:NumberOfPackages>
            |             <urn:CommercialSealIdentification>SEAL456789321</urn:CommercialSealIdentification>
            |             <urn:SealInformation language="en">Red Strip</urn:SealInformation>
            |           </urn:Package>
            |           <urn:Package>
            |             <urn:KindOfPackages>CR</urn:KindOfPackages>
            |             <urn:NumberOfPackages>10</urn:NumberOfPackages>
            |             <urn:CommercialSealIdentification>SEAL77</urn:CommercialSealIdentification>
            |             <urn:SealInformation language="en">Cork</urn:SealInformation>
            |           </urn:Package>
            |					</urn:BodyEadEsad>
            |           <urn:TransportDetails>
            |                   <urn:TransportUnitCode>1</urn:TransportUnitCode>
            |                   <urn:IdentityOfTransportUnits>Bottles</urn:IdentityOfTransportUnits>
            |                   <urn:CommercialSealIdentification>SID13245678</urn:CommercialSealIdentification>
            |                   <urn:ComplementaryInformation language="en">Bottles of Restina</urn:ComplementaryInformation>
            |                   <urn:SealInformation language="en">Sealed with red strip</urn:SealInformation>
            |                 </urn:TransportDetails>
            |                 <urn:TransportDetails>
            |                   <urn:TransportUnitCode>2</urn:TransportUnitCode>
            |                   <urn:IdentityOfTransportUnits>Cans</urn:IdentityOfTransportUnits>
            |                   <urn:CommercialSealIdentification>SID132987</urn:CommercialSealIdentification>
            |                   <urn:ComplementaryInformation language="en">Cans</urn:ComplementaryInformation>
            |                   <urn:SealInformation language="en">Seal</urn:SealInformation>
            |                 </urn:TransportDetails>
            |				</urn:EADESADContainer>
            |			</urn:Body>
            |		</urn:IE801>
            |        <urn:IE818 xmlns:urn="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE818:V3.01">
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
            |	</mov:currentMovement>
            | <mov:eventHistory>
            |   <urn:IE818 xmlns:urn="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE818:V3.01">
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
            |    </mov:eventHistory>
            |</mov:movementView>""".stripMargin)) shouldBe
          ParseSuccess(getMovementResponse().copy(
            items = getMovementResponse().items :+ MovementItem(
              itemUniqueReference = 3,
              productCode = "W300",
              cnCode = "27111901",
              quantity = BigDecimal(501),
              grossMass = BigDecimal(901),
              netMass = BigDecimal(475),
              alcoholicStrength = Some(12.7),
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
                  quantity = Some(150),
                  shippingMarks = None,
                  identityOfCommercialSeal = Some("SEAL456789321"),
                  sealInformation = Some("Red Strip")
                ),
                Packaging(
                  typeOfPackage = "CR",
                  quantity = Some(10),
                  shippingMarks = None,
                  identityOfCommercialSeal = Some("SEAL77"),
                  sealInformation = Some("Cork")
                )
              ),
              wineProduct = None
            ),
            numberOfItems = 3,
            movementViewHistoryAndExtraData = MovementViewHistoryAndExtraDataModel(
              arc = "13AB7778889991ABCDEF9",
              serialNumberOfCertificateOfExemption = None,
              dispatchImportOfficeReferenceNumber = None,
              deliveryPlaceCustomsOfficeReferenceNumber = Some("FR000003"),
              competentAuthorityDispatchOfficeReferenceNumber = Some("GB000002"),
              eadStatus = "Accepted",
              dateAndTimeOfValidationOfEadEsad = "2008-09-04T10:22:50",
              numberOfItems = 3,
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
              notificationOfAlertOrRejection = Seq(),
              notificationOfAcceptedExport = None,
              notificationOfDelay = Seq(),
              cancelMovement = None
            )))
      }

      "handle hours and days" in {
        val modelWithHours = GetMovementResponse.xmlReader.read(XML.loadString(
          s"""
            |	<mov:movementView xsi:schemaLocation="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3 movementView.xsd" xmlns:mov="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3">
            |    <mov:currentMovement>
            |      <mov:status>Accepted</mov:status>
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
            |            <body:ComplementConsigneeTrader>
            |              <MemberStateCode>GB</MemberStateCode>
            |            </body:ComplementConsigneeTrader>
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
            |              <urn:JourneyTime>H20</urn:JourneyTime>
            |              <urn:TransportArrangement>1</urn:TransportArrangement>
            |            </urn:HeaderEadEsad>
            |            <urn:TransportMode>
            |              <urn:TransportModeCode>1</urn:TransportModeCode>
            |            </urn:TransportMode>
            |            <urn:MovementGuarantee>
            |              <urn:GuarantorTypeCode>0</urn:GuarantorTypeCode>
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
            |              <urn:MaturationPeriodOrAgeOfProducts language="EN">Maturation Period</urn:MaturationPeriodOrAgeOfProducts>
            |              <urn:IndependentSmallProducersDeclaration language="EN">Independent Small Producers Declaration</urn:IndependentSmallProducersDeclaration>
            |              <urn:DesignationOfOrigin language="en">Designation of Origin</urn:DesignationOfOrigin>
            |              <urn:DegreePlato>1.2</urn:DegreePlato>
            |              <urn:SizeOfProducer>20000</urn:SizeOfProducer>
            |              <urn:Density>880</urn:Density>
            |              <urn:CommercialDescription language="en">Retsina</urn:CommercialDescription>
            |              <urn:BrandNameOfProducts language="en">MALAMATINA</urn:BrandNameOfProducts>
            |              <urn:Package>
            |                <urn:KindOfPackages>BO</urn:KindOfPackages>
            |                <urn:NumberOfPackages>125</urn:NumberOfPackages>
            |                <urn:ShippingMarks>MARKS</urn:ShippingMarks>
            |                <urn:CommercialSealIdentification>SEAL456789321</urn:CommercialSealIdentification>
            |                <urn:SealInformation language="en">Red Strip</urn:SealInformation>
            |              </urn:Package>
            |              <urn:WineProduct>
            |                <urn:WineProductCategory>4</urn:WineProductCategory>
            |                <urn:WineGrowingZoneCode>2</urn:WineGrowingZoneCode>
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
            |              <urn:SealInformation language="en">Seal</urn:SealInformation>
            |            </urn:TransportDetails>
            |          </urn:EADESADContainer>
            |        </urn:Body>
            |      </urn:IE801>
            |    </mov:currentMovement>
            |     <mov:eventHistory>
            |   <urn:IE818 xmlns:urn="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE818:V3.01">
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
            |          <urn:MessageRecipient>NDEA.FR</urn:MessageRecipient>
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
            |    <body:IE810 xmlns:body="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:IE810:V2.02">
            |        <body:Header xmlns:head="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:TMS:V2.02">
            |          <head:MessageSender>token</head:MessageSender>
            |          <head:MessageRecipient>token</head:MessageRecipient>
            |          <head:DateOfPreparation>1967-08-13</head:DateOfPreparation>
            |          <head:TimeOfPreparation>14:20:0Z</head:TimeOfPreparation>
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
            |    </mov:eventHistory>
            |  </mov:movementView>""".stripMargin))

        modelWithHours shouldBe ParseSuccess(getMovementResponse(journeyTimeValue = "20 hours"))

        val modelWithDays = GetMovementResponse.xmlReader.read(XML.loadString(
          s"""
            |<mov:movementView xsi:schemaLocation="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3 movementView.xsd" xmlns:mov="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3">
            |    <mov:currentMovement>
            |      <mov:status>Accepted</mov:status>
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
            |            <body:ComplementConsigneeTrader>
            |              <MemberStateCode>GB</MemberStateCode>
            |            </body:ComplementConsigneeTrader>
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
            |              <urn:GuarantorTypeCode>0</urn:GuarantorTypeCode>
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
            |              <urn:MaturationPeriodOrAgeOfProducts language="EN">Maturation Period</urn:MaturationPeriodOrAgeOfProducts>
            |              <urn:IndependentSmallProducersDeclaration language="EN">Independent Small Producers Declaration</urn:IndependentSmallProducersDeclaration>
            |              <urn:DesignationOfOrigin language="en">Designation of Origin</urn:DesignationOfOrigin>
            |              <urn:DegreePlato>1.2</urn:DegreePlato>
            |              <urn:SizeOfProducer>20000</urn:SizeOfProducer>
            |              <urn:Density>880</urn:Density>
            |              <urn:CommercialDescription language="en">Retsina</urn:CommercialDescription>
            |              <urn:BrandNameOfProducts language="en">MALAMATINA</urn:BrandNameOfProducts>
            |              <urn:Package>
            |                <urn:KindOfPackages>BO</urn:KindOfPackages>
            |                <urn:NumberOfPackages>125</urn:NumberOfPackages>
            |                <urn:ShippingMarks>MARKS</urn:ShippingMarks>
            |                <urn:CommercialSealIdentification>SEAL456789321</urn:CommercialSealIdentification>
            |                <urn:SealInformation language="en">Red Strip</urn:SealInformation>
            |              </urn:Package>
            |              <urn:WineProduct>
            |                <urn:WineProductCategory>4</urn:WineProductCategory>
            |                <urn:WineGrowingZoneCode>2</urn:WineGrowingZoneCode>
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
            |              <urn:SealInformation language="en">Seal</urn:SealInformation>
            |            </urn:TransportDetails>
            |          </urn:EADESADContainer>
            |        </urn:Body>
            |      </urn:IE801>
            |    </mov:currentMovement>
            |     <mov:eventHistory>
            |   <urn:IE818 xmlns:urn="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE818:V3.01">
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
            |    <body:IE810 xmlns:body="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:IE810:V2.02">
            |        <body:Header xmlns:head="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:TMS:V2.02">
            |          <head:MessageSender>token</head:MessageSender>
            |          <head:MessageRecipient>token</head:MessageRecipient>
            |          <head:DateOfPreparation>1967-08-13</head:DateOfPreparation>
            |          <head:TimeOfPreparation>14:20:0Z</head:TimeOfPreparation>
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
            |    </mov:eventHistory>
            |  </mov:movementView>""".stripMargin))

        modelWithDays shouldBe ParseSuccess(getMovementResponse(journeyTimeValue = "20 days"))
      }
    }

    "fail to read a movement" when {

      "missing status" in {

        val noStatusXML = XML.loadString(
          s"""
            |	<mov:movementView xsi:schemaLocation="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3 movementView.xsd"
            |		xmlns:mov="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3">
            |		<mov:currentMovement>
            |			<mov:version_transaction_ref>008</mov:version_transaction_ref>
            |			<urn:IE801
            |				xmlns:body="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:IE801:V2.02">
            |				<urn:Header
            |					xmlns:head="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:TMS:V2.02">
            |					<head:MessageSender>NDEA.FR</head:MessageSender>
            |					<head:MessageRecipient>NDEA.GB</head:MessageRecipient>
            |					<head:DateOfPreparation>2008-09-04</head:DateOfPreparation>
            |					<head:TimeOfPreparation>10:22:50</head:TimeOfPreparation>
            |					<head:MessageIdentifier>Message identifier</head:MessageIdentifier>
            |				</urn:Header>
            |				<urn:Body>
            |					<urn:EADESADContainer>
            |						<urn:ConsigneeTrader language="en">
            |							${maxTraderModelXML(ConsigneeTrader)}
            |						</urn:ConsigneeTrader>
            |           <body:ComplementConsigneeTrader>
            |             <MemberStateCode>GB</MemberStateCode>
            |           </body:ComplementConsigneeTrader>
            |						<urn:ExciseMovement>
            |							<urn:AdministrativeReferenceCode>13AB7778889991ABCDEF9</urn:AdministrativeReferenceCode>
            |							<urn:DateAndTimeOfValidationOfEadEsad>2008-09-04T10:22:50</urn:DateAndTimeOfValidationOfEadEsad>
            |						</urn:ExciseMovement>
            |						<urn:ConsignorTrader language="en">
            |							${maxTraderModelXML(ConsignorTrader)}
            |						</urn:ConsignorTrader>
            |						<urn:PlaceOfDispatchTrader language="en">
            |							${maxTraderModelXML(PlaceOfDispatchTrader)}
            |						</urn:PlaceOfDispatchTrader>
            |						<urn:DeliveryPlaceCustomsOffice>
            |							<urn:ReferenceNumber>FR000003</urn:ReferenceNumber>
            |						</urn:DeliveryPlaceCustomsOffice>
            |						<urn:CompetentAuthorityDispatchOffice>
            |							<urn:ReferenceNumber>GB000002</urn:ReferenceNumber>
            |						</urn:CompetentAuthorityDispatchOffice>
            |						<urn:FirstTransporterTrader language="en">
            |							${maxTraderModelXML(TransportTrader)}
            |						</urn:FirstTransporterTrader>
            |						<urn:DocumentCertificate>
            |							<urn:DocumentDescription language="en">Test</urn:DocumentDescription>
            |							<urn:ReferenceOfDocument language="en">AB123</urn:ReferenceOfDocument>
            |						</urn:DocumentCertificate>
            |						<urn:EadEsad>
            |							<urn:LocalReferenceNumber>EN</urn:LocalReferenceNumber>
            |							<urn:InvoiceNumber>IN777888999</urn:InvoiceNumber>
            |							<urn:InvoiceDate>2008-09-04</urn:InvoiceDate>
            |							<urn:OriginTypeCode>1</urn:OriginTypeCode>
            |							<urn:DateOfDispatch>2008-11-20</urn:DateOfDispatch>
            |							<urn:TimeOfDispatch>10:00:00</urn:TimeOfDispatch>
            |						</urn:EadEsad>
            |						<urn:HeaderEadEsad>
            |							<urn:SequenceNumber>1</urn:SequenceNumber>
            |							<urn:DateAndTimeOfUpdateValidation>2008-09-04T10:22:50</urn:DateAndTimeOfUpdateValidation>
            |							<urn:DestinationTypeCode>6</urn:DestinationTypeCode>
            |							<urn:JourneyTime>H20</urn:JourneyTime>
            |							<urn:TransportArrangement>1</urn:TransportArrangement>
            |						</urn:HeaderEadEsad>
            |						<urn:TransportMode>
            |							<urn:TransportModeCode>1</urn:TransportModeCode>
            |						</urn:TransportMode>
            |						<urn:MovementGuarantee>
            |							<urn:GuarantorTypeCode>2</urn:GuarantorTypeCode>
            |						</urn:MovementGuarantee>
            |						<urn:BodyEadEsad>
            |							<urn:BodyRecordUniqueReference>1</urn:BodyRecordUniqueReference>
            |							<urn:ExciseProductCode>W200</urn:ExciseProductCode>
            |							<urn:CnCode>22041011</urn:CnCode>
            |							<urn:Quantity>500</urn:Quantity>
            |							<urn:GrossMass>900</urn:GrossMass>
            |							<urn:NetMass>375</urn:NetMass>
            |							<urn:FiscalMark language="en">FM564789 Fiscal Mark</urn:FiscalMark>
            |							<urn:FiscalMarkUsedFlag>1</urn:FiscalMarkUsedFlag>
            |							<urn:DesignationOfOrigin language="en">Designation of Origin</urn:DesignationOfOrigin>
            |							<urn:SizeOfProducer>20000</urn:SizeOfProducer>
            |							<urn:CommercialDescription language="en">Retsina</urn:CommercialDescription>
            |							<urn:BrandNameOfProducts language="en">MALAMATINA</urn:BrandNameOfProducts>
            |             <urn:Package>
            |                <urn:KindOfPackages>BO</urn:KindOfPackages>
            |                <urn:NumberOfPackages>125</urn:NumberOfPackages>
            |                <urn:CommercialSealIdentification>SEAL456789321</urn:CommercialSealIdentification>
            |                <urn:SealInformation language="en">Red Strip</urn:SealInformation>
            |              </urn:Package>
            |						</urn:BodyEadEsad>
            |						<urn:BodyEadEsad>
            |							<urn:BodyRecordUniqueReference>2</urn:BodyRecordUniqueReference>
            |							<urn:ExciseProductCode>W300</urn:ExciseProductCode>
            |							<urn:CnCode>27111901</urn:CnCode>
            |							<urn:Quantity>501</urn:Quantity>
            |							<urn:GrossMass>901</urn:GrossMass>
            |							<urn:NetMass>475</urn:NetMass>
            |             <urn:AlcoholicStrengthByVolumeInPercentage>12.7</urn:AlcoholicStrengthByVolumeInPercentage>
            |							<urn:FiscalMark language="en">FM564790 Fiscal Mark</urn:FiscalMark>
            |							<urn:FiscalMarkUsedFlag>1</urn:FiscalMarkUsedFlag>
            |							<urn:DesignationOfOrigin language="en">Designation of Origin</urn:DesignationOfOrigin>
            |							<urn:SizeOfProducer>20000</urn:SizeOfProducer>
            |							<urn:CommercialDescription language="en">Retsina</urn:CommercialDescription>
            |							<urn:BrandNameOfProducts language="en">BrandName</urn:BrandNameOfProducts>
            |             <urn:Package>
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
            |						</urn:BodyEadEsad>
            |					</urn:EADESADContainer>
            |				</urn:Body>
            |			</urn:IE801>
            |		</mov:currentMovement>
            |	</mov:movementView>""".stripMargin)

        GetMovementResponse.xmlReader.read(noStatusXML) shouldBe ParseFailure(EmptyError(GetMovementResponse.eadStatus))
      }

      "missing LRN" in {

        val noLrnXML = XML.loadString(
          s"""
            |	<mov:movementView xsi:schemaLocation="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3 movementView.xsd"
            |		xmlns:mov="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3">
            |		<mov:currentMovement>
            |     <mov:status>Accepted</mov:status>
            |			<mov:version_transaction_ref>008</mov:version_transaction_ref>
            |			<urn:IE801
            |				xmlns:body="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:IE801:V2.02">
            |				<urn:Header
            |					xmlns:head="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:TMS:V2.02">
            |					<head:MessageSender>NDEA.FR</head:MessageSender>
            |					<head:MessageRecipient>NDEA.GB</head:MessageRecipient>
            |					<head:DateOfPreparation>2008-09-04</head:DateOfPreparation>
            |					<head:TimeOfPreparation>10:22:50</head:TimeOfPreparation>
            |					<head:MessageIdentifier>Message identifier</head:MessageIdentifier>
            |				</urn:Header>
            |				<urn:Body>
            |					<urn:EADESADContainer>
            |						<urn:ConsigneeTrader language="en">
            |							${maxTraderModelXML(ConsigneeTrader)}
            |						</urn:ConsigneeTrader>
            |           <body:ComplementConsigneeTrader>
            |             <MemberStateCode>GB</MemberStateCode>
            |           </body:ComplementConsigneeTrader>
            |						<urn:ExciseMovement>
            |							<urn:AdministrativeReferenceCode>13AB7778889991ABCDEF9</urn:AdministrativeReferenceCode>
            |							<urn:DateAndTimeOfValidationOfEadEsad>2008-09-04T10:22:50</urn:DateAndTimeOfValidationOfEadEsad>
            |						</urn:ExciseMovement>
            |						<urn:ConsignorTrader language="en">
            |							${maxTraderModelXML(ConsignorTrader)}
            |						</urn:ConsignorTrader>
            |						<urn:PlaceOfDispatchTrader language="en">
            |							${maxTraderModelXML(PlaceOfDispatchTrader)}
            |						</urn:PlaceOfDispatchTrader>
            |						<urn:DeliveryPlaceCustomsOffice>
            |							<urn:ReferenceNumber>FR000003</urn:ReferenceNumber>
            |						</urn:DeliveryPlaceCustomsOffice>
            |						<urn:CompetentAuthorityDispatchOffice>
            |							<urn:ReferenceNumber>GB000002</urn:ReferenceNumber>
            |						</urn:CompetentAuthorityDispatchOffice>
            |						<urn:FirstTransporterTrader language="en">
            |							${maxTraderModelXML(TransportTrader)}
            |						</urn:FirstTransporterTrader>
            |						<urn:DocumentCertificate>
            |							<urn:DocumentDescription language="en">Test</urn:DocumentDescription>
            |							<urn:ReferenceOfDocument language="en">AB123</urn:ReferenceOfDocument>
            |						</urn:DocumentCertificate>
            |						<urn:EadEsad>
            |							<urn:InvoiceNumber>IN777888999</urn:InvoiceNumber>
            |							<urn:InvoiceDate>2008-09-04</urn:InvoiceDate>
            |							<urn:OriginTypeCode>1</urn:OriginTypeCode>
            |							<urn:DateOfDispatch>2008-11-20</urn:DateOfDispatch>
            |							<urn:TimeOfDispatch>10:00:00</urn:TimeOfDispatch>
            |						</urn:EadEsad>
            |						<urn:HeaderEadEsad>
            |							<urn:SequenceNumber>1</urn:SequenceNumber>
            |							<urn:DateAndTimeOfUpdateValidation>2008-09-04T10:22:50</urn:DateAndTimeOfUpdateValidation>
            |							<urn:DestinationTypeCode>6</urn:DestinationTypeCode>
            |							<urn:JourneyTime>H20</urn:JourneyTime>
            |							<urn:TransportArrangement>1</urn:TransportArrangement>
            |						</urn:HeaderEadEsad>
            |						<urn:TransportMode>
            |							<urn:TransportModeCode>1</urn:TransportModeCode>
            |						</urn:TransportMode>
            |						<urn:MovementGuarantee>
            |							<urn:GuarantorTypeCode>2</urn:GuarantorTypeCode>
            |						</urn:MovementGuarantee>
            |						<urn:BodyEadEsad>
            |							<urn:BodyRecordUniqueReference>1</urn:BodyRecordUniqueReference>
            |							<urn:ExciseProductCode>W200</urn:ExciseProductCode>
            |							<urn:CnCode>22041011</urn:CnCode>
            |							<urn:Quantity>500</urn:Quantity>
            |							<urn:GrossMass>900</urn:GrossMass>
            |							<urn:NetMass>375</urn:NetMass>
            |							<urn:FiscalMark language="en">FM564789 Fiscal Mark</urn:FiscalMark>
            |							<urn:FiscalMarkUsedFlag>1</urn:FiscalMarkUsedFlag>
            |							<urn:DesignationOfOrigin language="en">Designation of Origin</urn:DesignationOfOrigin>
            |							<urn:SizeOfProducer>20000</urn:SizeOfProducer>
            |							<urn:CommercialDescription language="en">Retsina</urn:CommercialDescription>
            |							<urn:BrandNameOfProducts language="en">MALAMATINA</urn:BrandNameOfProducts>
            |             <urn:Package>
            |                <urn:KindOfPackages>BO</urn:KindOfPackages>
            |                <urn:NumberOfPackages>125</urn:NumberOfPackages>
            |                <urn:CommercialSealIdentification>SEAL456789321</urn:CommercialSealIdentification>
            |                <urn:SealInformation language="en">Red Strip</urn:SealInformation>
            |              </urn:Package>
            |						</urn:BodyEadEsad>
            |						<urn:BodyEadEsad>
            |							<urn:BodyRecordUniqueReference>2</urn:BodyRecordUniqueReference>
            |							<urn:ExciseProductCode>W300</urn:ExciseProductCode>
            |							<urn:CnCode>27111901</urn:CnCode>
            |							<urn:Quantity>501</urn:Quantity>
            |							<urn:GrossMass>901</urn:GrossMass>
            |							<urn:NetMass>475</urn:NetMass>
            |             <urn:AlcoholicStrengthByVolumeInPercentage>12.7</urn:AlcoholicStrengthByVolumeInPercentage>
            |							<urn:FiscalMark language="en">FM564790 Fiscal Mark</urn:FiscalMark>
            |							<urn:FiscalMarkUsedFlag>1</urn:FiscalMarkUsedFlag>
            |							<urn:DesignationOfOrigin language="en">Designation of Origin</urn:DesignationOfOrigin>
            |							<urn:SizeOfProducer>20000</urn:SizeOfProducer>
            |							<urn:CommercialDescription language="en">Retsina</urn:CommercialDescription>
            |							<urn:BrandNameOfProducts language="en">BrandName</urn:BrandNameOfProducts>
            |             <urn:Package>
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
            |						</urn:BodyEadEsad>
            |					</urn:EADESADContainer>
            |				</urn:Body>
            |			</urn:IE801>
            |		</mov:currentMovement>
            |	</mov:movementView>""".stripMargin)

        GetMovementResponse.xmlReader.read(noLrnXML) shouldBe ParseFailure(EmptyError(EADESADContainer \ "EadEsad" \\ "LocalReferenceNumber"))
      }

      "missing dateOfDispatch" in {

        val noDateOfDispatchXML = XML.loadString(
          s"""
            |	<mov:movementView xsi:schemaLocation="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3 movementView.xsd"
            |		xmlns:mov="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3">
            |		<mov:currentMovement>
            |     <mov:status>Accepted</mov:status>
            |			<mov:version_transaction_ref>008</mov:version_transaction_ref>
            |			<urn:IE801
            |				xmlns:body="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:IE801:V2.02">
            |				<urn:Header
            |					xmlns:head="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:TMS:V2.02">
            |					<head:MessageSender>NDEA.FR</head:MessageSender>
            |					<head:MessageRecipient>NDEA.GB</head:MessageRecipient>
            |					<head:DateOfPreparation>2008-09-04</head:DateOfPreparation>
            |					<head:TimeOfPreparation>10:22:50</head:TimeOfPreparation>
            |					<head:MessageIdentifier>Message identifier</head:MessageIdentifier>
            |				</urn:Header>
            |				<urn:Body>
            |					<urn:EADESADContainer>
            |						<urn:ConsigneeTrader language="en">
            |							${maxTraderModelXML(ConsigneeTrader)}
            |						</urn:ConsigneeTrader>
            |           <body:ComplementConsigneeTrader>
            |             <MemberStateCode>GB</MemberStateCode>
            |           </body:ComplementConsigneeTrader>
            |						<urn:ExciseMovement>
            |							<urn:AdministrativeReferenceCode>13AB7778889991ABCDEF9</urn:AdministrativeReferenceCode>
            |							<urn:DateAndTimeOfValidationOfEadEsad>2008-09-04T10:22:50</urn:DateAndTimeOfValidationOfEadEsad>
            |						</urn:ExciseMovement>
            |						<urn:ConsignorTrader language="en">
            |							${maxTraderModelXML(ConsignorTrader)}
            |						</urn:ConsignorTrader>
            |						<urn:PlaceOfDispatchTrader language="en">
            |							${maxTraderModelXML(PlaceOfDispatchTrader)}
            |						</urn:PlaceOfDispatchTrader>
            |						<urn:DeliveryPlaceCustomsOffice>
            |							<urn:ReferenceNumber>FR000003</urn:ReferenceNumber>
            |						</urn:DeliveryPlaceCustomsOffice>
            |						<urn:CompetentAuthorityDispatchOffice>
            |							<urn:ReferenceNumber>GB000002</urn:ReferenceNumber>
            |						</urn:CompetentAuthorityDispatchOffice>
            |						<urn:FirstTransporterTrader language="en">
            |							${maxTraderModelXML(TransportTrader)}
            |						</urn:FirstTransporterTrader>
            |						<urn:DocumentCertificate>
            |							<urn:DocumentDescription language="en">Test</urn:DocumentDescription>
            |							<urn:ReferenceOfDocument language="en">AB123</urn:ReferenceOfDocument>
            |						</urn:DocumentCertificate>
            |						<urn:EadEsad>
            |							<urn:LocalReferenceNumber>EN</urn:LocalReferenceNumber>
            |							<urn:InvoiceNumber>IN777888999</urn:InvoiceNumber>
            |							<urn:InvoiceDate>2008-09-04</urn:InvoiceDate>
            |							<urn:OriginTypeCode>1</urn:OriginTypeCode>
            |							<urn:TimeOfDispatch>10:00:00</urn:TimeOfDispatch>
            |						</urn:EadEsad>
            |						<urn:HeaderEadEsad>
            |							<urn:SequenceNumber>1</urn:SequenceNumber>
            |							<urn:DateAndTimeOfUpdateValidation>2008-09-04T10:22:50</urn:DateAndTimeOfUpdateValidation>
            |							<urn:DestinationTypeCode>6</urn:DestinationTypeCode>
            |							<urn:JourneyTime>H20</urn:JourneyTime>
            |							<urn:TransportArrangement>1</urn:TransportArrangement>
            |						</urn:HeaderEadEsad>
            |						<urn:TransportMode>
            |							<urn:TransportModeCode>1</urn:TransportModeCode>
            |						</urn:TransportMode>
            |						<urn:MovementGuarantee>
            |							<urn:GuarantorTypeCode>2</urn:GuarantorTypeCode>
            |						</urn:MovementGuarantee>
            |						<urn:BodyEadEsad>
            |							<urn:BodyRecordUniqueReference>1</urn:BodyRecordUniqueReference>
            |							<urn:ExciseProductCode>W200</urn:ExciseProductCode>
            |							<urn:CnCode>22041011</urn:CnCode>
            |							<urn:Quantity>500</urn:Quantity>
            |							<urn:GrossMass>900</urn:GrossMass>
            |							<urn:NetMass>375</urn:NetMass>
            |							<urn:FiscalMark language="en">FM564789 Fiscal Mark</urn:FiscalMark>
            |							<urn:FiscalMarkUsedFlag>1</urn:FiscalMarkUsedFlag>
            |							<urn:DesignationOfOrigin language="en">Designation of Origin</urn:DesignationOfOrigin>
            |							<urn:SizeOfProducer>20000</urn:SizeOfProducer>
            |							<urn:CommercialDescription language="en">Retsina</urn:CommercialDescription>
            |							<urn:BrandNameOfProducts language="en">MALAMATINA</urn:BrandNameOfProducts>
            |             <urn:Package>
            |                <urn:KindOfPackages>BO</urn:KindOfPackages>
            |                <urn:NumberOfPackages>125</urn:NumberOfPackages>
            |                <urn:CommercialSealIdentification>SEAL456789321</urn:CommercialSealIdentification>
            |                <urn:SealInformation language="en">Red Strip</urn:SealInformation>
            |              </urn:Package>
            |						</urn:BodyEadEsad>
            |						<urn:BodyEadEsad>
            |							<urn:BodyRecordUniqueReference>2</urn:BodyRecordUniqueReference>
            |							<urn:ExciseProductCode>W300</urn:ExciseProductCode>
            |							<urn:CnCode>27111901</urn:CnCode>
            |							<urn:Quantity>501</urn:Quantity>
            |							<urn:GrossMass>901</urn:GrossMass>
            |							<urn:NetMass>475</urn:NetMass>
            |             <urn:AlcoholicStrengthByVolumeInPercentage>12.7</urn:AlcoholicStrengthByVolumeInPercentage>
            |							<urn:FiscalMark language="en">FM564790 Fiscal Mark</urn:FiscalMark>
            |							<urn:FiscalMarkUsedFlag>1</urn:FiscalMarkUsedFlag>
            |							<urn:DesignationOfOrigin language="en">Designation of Origin</urn:DesignationOfOrigin>
            |							<urn:SizeOfProducer>20000</urn:SizeOfProducer>
            |							<urn:CommercialDescription language="en">Retsina</urn:CommercialDescription>
            |							<urn:BrandNameOfProducts language="en">BrandName</urn:BrandNameOfProducts>
            |             <urn:Package>
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
            |						</urn:BodyEadEsad>
            |					</urn:EADESADContainer>
            |				</urn:Body>
            |			</urn:IE801>
            |		</mov:currentMovement>
            |	</mov:movementView>""".stripMargin)

        GetMovementResponse.xmlReader.read(noDateOfDispatchXML) shouldBe ParseFailure(EmptyError(EADESADContainer \ "EadEsad" \\ "DateOfDispatch"))
      }

      "missing journeyTime" in {

        val noJourneyTimeXML = XML.loadString(
          s"""
            |	<mov:movementView xsi:schemaLocation="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3 movementView.xsd"
            |		xmlns:mov="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3">
            |		<mov:currentMovement>
            |     <mov:status>Accepted</mov:status>
            |			<mov:version_transaction_ref>008</mov:version_transaction_ref>
            |			<urn:IE801
            |				xmlns:body="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:IE801:V2.02">
            |				<urn:Header
            |					xmlns:head="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:TMS:V2.02">
            |					<head:MessageSender>NDEA.FR</head:MessageSender>
            |					<head:MessageRecipient>NDEA.GB</head:MessageRecipient>
            |					<head:DateOfPreparation>2008-09-04</head:DateOfPreparation>
            |					<head:TimeOfPreparation>10:22:50</head:TimeOfPreparation>
            |					<head:MessageIdentifier>Message identifier</head:MessageIdentifier>
            |				</urn:Header>
            |				<urn:Body>
            |					<urn:EADESADContainer>
            |						<urn:ConsigneeTrader language="en">
            |							${maxTraderModelXML(ConsigneeTrader)}
            |						</urn:ConsigneeTrader>
            |           <body:ComplementConsigneeTrader>
            |             <MemberStateCode>GB</MemberStateCode>
            |           </body:ComplementConsigneeTrader>
            |						<urn:ExciseMovement>
            |							<urn:AdministrativeReferenceCode>13AB7778889991ABCDEF9</urn:AdministrativeReferenceCode>
            |							<urn:DateAndTimeOfValidationOfEadEsad>2008-09-04T10:22:50</urn:DateAndTimeOfValidationOfEadEsad>
            |						</urn:ExciseMovement>
            |						<urn:ConsignorTrader language="en">
            |							${maxTraderModelXML(ConsignorTrader)}
            |						</urn:ConsignorTrader>
            |						<urn:PlaceOfDispatchTrader language="en">
            |							${maxTraderModelXML(PlaceOfDispatchTrader)}
            |						</urn:PlaceOfDispatchTrader>
            |						<urn:DeliveryPlaceCustomsOffice>
            |							<urn:ReferenceNumber>FR000003</urn:ReferenceNumber>
            |						</urn:DeliveryPlaceCustomsOffice>
            |						<urn:CompetentAuthorityDispatchOffice>
            |							<urn:ReferenceNumber>GB000002</urn:ReferenceNumber>
            |						</urn:CompetentAuthorityDispatchOffice>
            |						<urn:FirstTransporterTrader language="en">
            |							${maxTraderModelXML(TransportTrader)}
            |						</urn:FirstTransporterTrader>
            |						<urn:DocumentCertificate>
            |							<urn:DocumentDescription language="en">Test</urn:DocumentDescription>
            |							<urn:ReferenceOfDocument language="en">AB123</urn:ReferenceOfDocument>
            |						</urn:DocumentCertificate>
            |						<urn:EadEsad>
            |							<urn:LocalReferenceNumber>EN</urn:LocalReferenceNumber>
            |							<urn:InvoiceNumber>IN777888999</urn:InvoiceNumber>
            |							<urn:InvoiceDate>2008-09-04</urn:InvoiceDate>
            |							<urn:OriginTypeCode>1</urn:OriginTypeCode>
            |							<urn:DateOfDispatch>2008-11-20</urn:DateOfDispatch>
            |							<urn:TimeOfDispatch>10:00:00</urn:TimeOfDispatch>
            |						</urn:EadEsad>
            |						<urn:HeaderEadEsad>
            |							<urn:SequenceNumber>1</urn:SequenceNumber>
            |							<urn:DateAndTimeOfUpdateValidation>2008-09-04T10:22:50</urn:DateAndTimeOfUpdateValidation>
            |							<urn:DestinationTypeCode>6</urn:DestinationTypeCode>
            |							<urn:TransportArrangement>1</urn:TransportArrangement>
            |						</urn:HeaderEadEsad>
            |						<urn:TransportMode>
            |							<urn:TransportModeCode>1</urn:TransportModeCode>
            |						</urn:TransportMode>
            |						<urn:MovementGuarantee>
            |							<urn:GuarantorTypeCode>2</urn:GuarantorTypeCode>
            |						</urn:MovementGuarantee>
            |						<urn:BodyEadEsad>
            |							<urn:BodyRecordUniqueReference>1</urn:BodyRecordUniqueReference>
            |							<urn:ExciseProductCode>W200</urn:ExciseProductCode>
            |							<urn:CnCode>22041011</urn:CnCode>
            |							<urn:Quantity>500</urn:Quantity>
            |							<urn:GrossMass>900</urn:GrossMass>
            |							<urn:NetMass>375</urn:NetMass>
            |							<urn:FiscalMark language="en">FM564789 Fiscal Mark</urn:FiscalMark>
            |							<urn:FiscalMarkUsedFlag>1</urn:FiscalMarkUsedFlag>
            |							<urn:DesignationOfOrigin language="en">Designation of Origin</urn:DesignationOfOrigin>
            |							<urn:SizeOfProducer>20000</urn:SizeOfProducer>
            |							<urn:CommercialDescription language="en">Retsina</urn:CommercialDescription>
            |							<urn:BrandNameOfProducts language="en">MALAMATINA</urn:BrandNameOfProducts>
            |             <urn:Package>
            |                <urn:KindOfPackages>BO</urn:KindOfPackages>
            |                <urn:NumberOfPackages>125</urn:NumberOfPackages>
            |                <urn:CommercialSealIdentification>SEAL456789321</urn:CommercialSealIdentification>
            |                <urn:SealInformation language="en">Red Strip</urn:SealInformation>
            |              </urn:Package>
            |						</urn:BodyEadEsad>
            |						<urn:BodyEadEsad>
            |							<urn:BodyRecordUniqueReference>2</urn:BodyRecordUniqueReference>
            |							<urn:ExciseProductCode>W300</urn:ExciseProductCode>
            |							<urn:CnCode>27111901</urn:CnCode>
            |							<urn:Quantity>501</urn:Quantity>
            |							<urn:GrossMass>901</urn:GrossMass>
            |							<urn:NetMass>475</urn:NetMass>
            |             <urn:AlcoholicStrengthByVolumeInPercentage>12.7</urn:AlcoholicStrengthByVolumeInPercentage>
            |							<urn:FiscalMark language="en">FM564790 Fiscal Mark</urn:FiscalMark>
            |							<urn:FiscalMarkUsedFlag>1</urn:FiscalMarkUsedFlag>
            |							<urn:DesignationOfOrigin language="en">Designation of Origin</urn:DesignationOfOrigin>
            |							<urn:SizeOfProducer>20000</urn:SizeOfProducer>
            |							<urn:CommercialDescription language="en">Retsina</urn:CommercialDescription>
            |							<urn:BrandNameOfProducts language="en">BrandName</urn:BrandNameOfProducts>
            |             <urn:Package>
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
            |						</urn:BodyEadEsad>
            |					</urn:EADESADContainer>
            |				</urn:Body>
            |			</urn:IE801>
            |		</mov:currentMovement>
            |	</mov:movementView>""".stripMargin)

        GetMovementResponse.xmlReader.read(noJourneyTimeXML) shouldBe ParseFailure(JourneyTimeParseFailure("Could not parse JourneyTime from XML, received: ''"))
      }
    }
  }
}
