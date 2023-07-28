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

package uk.gov.hmrc.emcstfe.models.response

import com.lucidchart.open.xtract.{EmptyError, ParseFailure, ParseSuccess}
import play.api.libs.json.Json
import uk.gov.hmrc.emcstfe.fixtures.GetMovementFixture
import uk.gov.hmrc.emcstfe.models.common.{ConsigneeTrader, ConsignorTrader, PlaceOfDispatchTrader, TransportTrader}
import uk.gov.hmrc.emcstfe.models.common.JourneyTime.JourneyTimeParseFailure
import uk.gov.hmrc.emcstfe.support.UnitSpec

import scala.xml.XML

class GetMovementResponseSpec extends UnitSpec with GetMovementFixture {

  "writes" should {
    "write a model to JSON" in {
      Json.toJson(getMovementResponse) shouldBe getMovementJson
    }
  }

  ".xmlReads" should {

    "successfully read a movement" when {

      "all fields are valid" in {
        GetMovementResponse.xmlReader.read(XML.loadString(getMovementResponseBody)) shouldBe ParseSuccess(getMovementResponse)
      }

      "duplicate CnCodes" in {

        GetMovementResponse.xmlReader.read(XML.loadString(
          // There are three CnCode values here but only two unique ones
          s"""
            |	<mov:movementView xsi:schemaLocation="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3 movementView.xsd"
            |		xmlns:mov="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3">
            |		<mov:currentMovement>
            |			<mov:status>Accepted</mov:status>
            |			<mov:version_transaction_ref>008</mov:version_transaction_ref>
            |			<urn:IE801
            |				xmlns:body="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE801:V3.01">
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
            |						<urn:ExciseMovement>
            |							<urn:AdministrativeReferenceCode>13AB7778889991ABCDEF9</urn:AdministrativeReferenceCode>
            |							<urn:DateAndTimeOfValidationOfEad>2008-09-04T10:22:50</urn:DateAndTimeOfValidationOfEad>
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
            |							<urn:JourneyTime>D20</urn:JourneyTime>
            |							<urn:TransportArrangement>1</urn:TransportArrangement>
            |						</urn:HeaderEadEsad>
            |						<urn:TransportMode>
            |							<urn:TransportModeCode>1</urn:TransportModeCode>
            |						</urn:TransportMode>
            |						<urn:MovementGuarantee>
            |							<urn:GuarantorTypeCode>2</urn:GuarantorTypeCode>
            |						</urn:MovementGuarantee>
            |						<urn:BodyEadEsad>
            |              <urn:BodyRecordUniqueReference>1</urn:BodyRecordUniqueReference>
            |              <urn:ExciseProductCode>W200</urn:ExciseProductCode>
            |              <urn:CnCode>22041011</urn:CnCode>
            |              <urn:Quantity>500</urn:Quantity>
            |              <urn:GrossMass>900</urn:GrossMass>
            |              <urn:NetMass>375</urn:NetMass>
            |              <urn:FiscalMark language="en">FM564789 Fiscal Mark</urn:FiscalMark>
            |              <urn:FiscalMarkUsedFlag>1</urn:FiscalMarkUsedFlag>
            |              <urn:MaturationPeriodOrAgeOfProducts language="EN">Maturation Period</urn:MaturationPeriodOrAgeOfProducts>
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
            |						</urn:BodyEadEsad>
            |						<urn:BodyEadEsad>
            |							<urn:BodyRecordUniqueReference>3</urn:BodyRecordUniqueReference>
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
            |                <urn:NumberOfPackages>150</urn:NumberOfPackages>
            |                <urn:CommercialSealIdentification>SEAL456789321</urn:CommercialSealIdentification>
            |                <urn:SealInformation language="en">Red Strip</urn:SealInformation>
            |              </urn:Package>
            |              <urn:Package>
            |                <urn:KindOfPackages>CR</urn:KindOfPackages>
            |                <urn:NumberOfPackages>10</urn:NumberOfPackages>
            |                <urn:CommercialSealIdentification>SEAL77</urn:CommercialSealIdentification>
            |                <urn:SealInformation language="en">Cork</urn:SealInformation>
            |              </urn:Package>
            |						</urn:BodyEadEsad>
            |					</urn:EADESADContainer>
            |				</urn:Body>
            |			</urn:IE801>
            |		</mov:currentMovement>
            |	</mov:movementView>""".stripMargin)) shouldBe
          ParseSuccess(getMovementResponse.copy(
            items = getMovementResponse.items :+ MovementItem(
              itemUniqueReference = 3,
              productCode = "W300",
              cnCode = "27111901",
              quantity = BigDecimal(501),
              grossMass = BigDecimal(901),
              netMass = BigDecimal(475),
              alcoholicStrength = Some(12.7),
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
            numberOfItems = 3
          ))
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
            |              <urn:MaturationPeriodOrAgeOfProducts language="EN">Maturation Period</urn:MaturationPeriodOrAgeOfProducts>
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
            |              <urn:SealInformation language="en">Seal info</urn:SealInformation>
            |            </urn:TransportDetails>
            |          </urn:EADESADContainer>
            |        </urn:Body>
            |      </urn:IE801>
            |    </mov:currentMovement>
            |  </mov:movementView>""".stripMargin))

        modelWithHours shouldBe ParseSuccess(getMovementResponse.copy(journeyTime = "20 hours"))

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
            |              <urn:CnCode>22041011</urn:CnCode>
            |              <urn:Quantity>500</urn:Quantity>
            |              <urn:GrossMass>900</urn:GrossMass>
            |              <urn:NetMass>375</urn:NetMass>
            |              <urn:FiscalMark language="en">FM564789 Fiscal Mark</urn:FiscalMark>
            |              <urn:FiscalMarkUsedFlag>1</urn:FiscalMarkUsedFlag>
            |              <urn:MaturationPeriodOrAgeOfProducts language="EN">Maturation Period</urn:MaturationPeriodOrAgeOfProducts>
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
            |              <urn:SealInformation language="en">Seal info</urn:SealInformation>
            |            </urn:TransportDetails>
            |          </urn:EADESADContainer>
            |        </urn:Body>
            |      </urn:IE801>
            |    </mov:currentMovement>
            |  </mov:movementView>""".stripMargin))

        modelWithDays shouldBe ParseSuccess(getMovementResponse.copy(journeyTime = "20 days"))
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
            |						<urn:ExciseMovement>
            |							<urn:AdministrativeReferenceCode>13AB7778889991ABCDEF9</urn:AdministrativeReferenceCode>
            |							<urn:DateAndTimeOfValidationOfEad>2008-09-04T10:22:50</urn:DateAndTimeOfValidationOfEad>
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
            |						<urn:ExciseMovement>
            |							<urn:AdministrativeReferenceCode>13AB7778889991ABCDEF9</urn:AdministrativeReferenceCode>
            |							<urn:DateAndTimeOfValidationOfEad>2008-09-04T10:22:50</urn:DateAndTimeOfValidationOfEad>
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

        GetMovementResponse.xmlReader.read(noLrnXML) shouldBe ParseFailure(EmptyError(GetMovementResponse.localReferenceNumber))
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
            |						<urn:ExciseMovement>
            |							<urn:AdministrativeReferenceCode>13AB7778889991ABCDEF9</urn:AdministrativeReferenceCode>
            |							<urn:DateAndTimeOfValidationOfEad>2008-09-04T10:22:50</urn:DateAndTimeOfValidationOfEad>
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

        GetMovementResponse.xmlReader.read(noDateOfDispatchXML) shouldBe ParseFailure(EmptyError(GetMovementResponse.dateOfDispatch))
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
            |						<urn:ExciseMovement>
            |							<urn:AdministrativeReferenceCode>13AB7778889991ABCDEF9</urn:AdministrativeReferenceCode>
            |							<urn:DateAndTimeOfValidationOfEad>2008-09-04T10:22:50</urn:DateAndTimeOfValidationOfEad>
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
