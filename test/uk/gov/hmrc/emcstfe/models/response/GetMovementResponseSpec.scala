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
          """
            |	<mov:movementView xsi:schemaLocation="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3 movementView.xsd"
            |		xmlns:mov="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3">
            |		<mov:currentMovement>
            |			<mov:status>Accepted</mov:status>
            |			<mov:version_transaction_ref>008</mov:version_transaction_ref>
            |			<body:IE801
            |				xmlns:body="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE801:V3.01">
            |				<body:Header
            |					xmlns:head="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:TMS:V2.02">
            |					<head:MessageSender>NDEA.FR</head:MessageSender>
            |					<head:MessageRecipient>NDEA.GB</head:MessageRecipient>
            |					<head:DateOfPreparation>2008-09-04</head:DateOfPreparation>
            |					<head:TimeOfPreparation>10:22:50</head:TimeOfPreparation>
            |					<head:MessageIdentifier>Message identifier</head:MessageIdentifier>
            |				</body:Header>
            |				<body:Body>
            |					<body:EADESADContainer>
            |						<body:ConsigneeTrader language="en">
            |							<body:Traderid>GB11100000002</body:Traderid>
            |							<body:TraderName>Current 801 Consignee</body:TraderName>
            |							<body:StreetName>Aardvark Avenue 253</body:StreetName>
            |							<body:Postcode>SY1 3BQ</body:Postcode>
            |							<body:City>Shrewsbury</body:City>
            |						</body:ConsigneeTrader>
            |						<body:ExciseMovement>
            |							<body:AdministrativeReferenceCode>13AB7778889991ABCDEF9</body:AdministrativeReferenceCode>
            |							<body:DateAndTimeOfValidationOfEad>2008-09-04T10:22:50</body:DateAndTimeOfValidationOfEad>
            |						</body:ExciseMovement>
            |						<body:ConsignorTrader language="en">
            |							<body:TraderExciseNumber>GB12345GTR144</body:TraderExciseNumber>
            |							<body:TraderName>Current 801 Consignor</body:TraderName>
            |							<body:StreetName>Main101</body:StreetName>
            |							<body:Postcode>ZZ78</body:Postcode>
            |							<body:City>Zeebrugge</body:City>
            |						</body:ConsignorTrader>
            |						<body:PlaceOfDispatchTrader language="en">
            |							<body:ReferenceOfTaxWarehouse>GB12345GTR143</body:ReferenceOfTaxWarehouse>
            |							<body:TraderName>Current 801 Dispatcher</body:TraderName>
            |							<body:StreetName>Psiristreet 59</body:StreetName>
            |							<body:Postcode>45690</body:Postcode>
            |							<body:City>Athens</body:City>
            |						</body:PlaceOfDispatchTrader>
            |						<body:DeliveryPlaceCustomsOffice>
            |							<body:ReferenceNumber>FR000003</body:ReferenceNumber>
            |						</body:DeliveryPlaceCustomsOffice>
            |						<body:CompetentAuthorityDispatchOffice>
            |							<body:ReferenceNumber>GB000002</body:ReferenceNumber>
            |						</body:CompetentAuthorityDispatchOffice>
            |						<body:FirstTransporterTrader language="en">
            |							<body:VatNumber>GB32445345</body:VatNumber>
            |							<body:TraderName>Current 801 FirstTransporter</body:TraderName>
            |							<body:StreetName>Kerkstraat 55</body:StreetName>
            |							<body:Postcode>9000</body:Postcode>
            |							<body:City>Gent</body:City>
            |						</body:FirstTransporterTrader>
            |						<body:DocumentCertificate>
            |							<body:DocumentDescription language="en">Test</body:DocumentDescription>
            |							<body:ReferenceOfDocument language="en">AB123</body:ReferenceOfDocument>
            |						</body:DocumentCertificate>
            |						<body:EadEsad>
            |							<body:LocalReferenceNumber>EN</body:LocalReferenceNumber>
            |							<body:InvoiceNumber>IN777888999</body:InvoiceNumber>
            |							<body:InvoiceDate>2008-09-04</body:InvoiceDate>
            |							<body:OriginTypeCode>1</body:OriginTypeCode>
            |							<body:DateOfDispatch>2008-11-20</body:DateOfDispatch>
            |							<body:TimeOfDispatch>10:00:00</body:TimeOfDispatch>
            |						</body:EadEsad>
            |						<body:HeaderEadEsad>
            |							<body:SequenceNumber>1</body:SequenceNumber>
            |							<body:DateAndTimeOfUpdateValidation>2008-09-04T10:22:50</body:DateAndTimeOfUpdateValidation>
            |							<body:DestinationTypeCode>6</body:DestinationTypeCode>
            |							<body:JourneyTime>D20</body:JourneyTime>
            |							<body:TransportArrangement>1</body:TransportArrangement>
            |						</body:HeaderEadEsad>
            |						<body:TransportMode>
            |							<body:TransportModeCode>1</body:TransportModeCode>
            |						</body:TransportMode>
            |						<body:MovementGuarantee>
            |							<body:GuarantorTypeCode>2</body:GuarantorTypeCode>
            |						</body:MovementGuarantee>
            |						<body:BodyEadEsad>
            |              <body:BodyRecordUniqueReference>1</body:BodyRecordUniqueReference>
            |              <body:ExciseProductCode>W200</body:ExciseProductCode>
            |              <body:CnCode>22041011</body:CnCode>
            |              <body:Quantity>500</body:Quantity>
            |              <body:GrossMass>900</body:GrossMass>
            |              <body:NetMass>375</body:NetMass>
            |              <body:FiscalMark language="en">FM564789 Fiscal Mark</body:FiscalMark>
            |              <body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
            |              <body:MaturationPeriodOrAgeOfProducts language="EN">Maturation Period</body:MaturationPeriodOrAgeOfProducts>
            |              <body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
            |              <body:DegreePlato>1.2</body:DegreePlato>
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
            |						</body:BodyEadEsad>
            |						<body:BodyEadEsad>
            |							<body:BodyRecordUniqueReference>2</body:BodyRecordUniqueReference>
            |							<body:ExciseProductCode>W300</body:ExciseProductCode>
            |							<body:CnCode>27111901</body:CnCode>
            |							<body:Quantity>501</body:Quantity>
            |							<body:GrossMass>901</body:GrossMass>
            |							<body:NetMass>475</body:NetMass>
            |             <body:AlcoholicStrengthByVolumeInPercentage>12.7</body:AlcoholicStrengthByVolumeInPercentage>
            |							<body:FiscalMark language="en">FM564790 Fiscal Mark</body:FiscalMark>
            |							<body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
            |							<body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
            |							<body:SizeOfProducer>20000</body:SizeOfProducer>
            |							<body:CommercialDescription language="en">Retsina</body:CommercialDescription>
            |							<body:BrandNameOfProducts language="en">BrandName</body:BrandNameOfProducts>
            |             <body:Package>
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
            |						</body:BodyEadEsad>
            |						<body:BodyEadEsad>
            |							<body:BodyRecordUniqueReference>3</body:BodyRecordUniqueReference>
            |							<body:ExciseProductCode>W300</body:ExciseProductCode>
            |							<body:CnCode>27111901</body:CnCode>
            |							<body:Quantity>501</body:Quantity>
            |							<body:GrossMass>901</body:GrossMass>
            |							<body:NetMass>475</body:NetMass>
            |             <body:AlcoholicStrengthByVolumeInPercentage>12.7</body:AlcoholicStrengthByVolumeInPercentage>
            |							<body:FiscalMark language="en">FM564790 Fiscal Mark</body:FiscalMark>
            |							<body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
            |							<body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
            |							<body:SizeOfProducer>20000</body:SizeOfProducer>
            |							<body:CommercialDescription language="en">Retsina</body:CommercialDescription>
            |							<body:BrandNameOfProducts language="en">BrandName</body:BrandNameOfProducts>
            |             <body:Package>
            |                <body:KindOfPackages>BO</body:KindOfPackages>
            |                <body:NumberOfPackages>150</body:NumberOfPackages>
            |                <body:CommercialSealIdentification>SEAL456789321</body:CommercialSealIdentification>
            |                <body:SealInformation language="en">Red Strip</body:SealInformation>
            |              </body:Package>
            |              <body:Package>
            |                <body:KindOfPackages>CR</body:KindOfPackages>
            |                <body:NumberOfPackages>10</body:NumberOfPackages>
            |                <body:CommercialSealIdentification>SEAL77</body:CommercialSealIdentification>
            |                <body:SealInformation language="en">Cork</body:SealInformation>
            |              </body:Package>
            |						</body:BodyEadEsad>
            |					</body:EADESADContainer>
            |				</body:Body>
            |			</body:IE801>
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
          """
            |	<mov:movementView xsi:schemaLocation="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3 movementView.xsd" xmlns:mov="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3">
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
            |              <body:Traderid>GB11100000002</body:Traderid>
            |              <body:TraderName>Current 801 Consignee</body:TraderName>
            |              <body:StreetName>Aardvark Avenue 253</body:StreetName>
            |              <body:Postcode>SY1 3BQ</body:Postcode>
            |              <body:City>Shrewsbury</body:City>
            |            </body:ConsigneeTrader>
            |            <body:ExciseMovement>
            |              <body:AdministrativeReferenceCode>13AB7778889991ABCDEF9</body:AdministrativeReferenceCode>
            |              <body:DateAndTimeOfValidationOfEadEsad>2008-09-04T10:22:50</body:DateAndTimeOfValidationOfEadEsad>
            |            </body:ExciseMovement>
            |            <body:ConsignorTrader language="en">
            |              <body:TraderExciseNumber>GB12345GTR144</body:TraderExciseNumber>
            |              <body:TraderName>Current 801 Consignor</body:TraderName>
            |              <body:StreetName>Main101</body:StreetName>
            |              <body:Postcode>ZZ78</body:Postcode>
            |              <body:City>Zeebrugge</body:City>
            |            </body:ConsignorTrader>
            |            <body:PlaceOfDispatchTrader language="en">
            |              <body:ReferenceOfTaxWarehouse>GB12345GTR143</body:ReferenceOfTaxWarehouse>
            |              <body:TraderName>Current 801 Dispatcher</body:TraderName>
            |              <body:StreetName>Psiristreet 59</body:StreetName>
            |              <body:Postcode>45690</body:Postcode>
            |              <body:City>Athens</body:City>
            |            </body:PlaceOfDispatchTrader>
            |            <body:DeliveryPlaceCustomsOffice>
            |              <body:ReferenceNumber>FR000003</body:ReferenceNumber>
            |            </body:DeliveryPlaceCustomsOffice>
            |            <body:CompetentAuthorityDispatchOffice>
            |              <body:ReferenceNumber>GB000002</body:ReferenceNumber>
            |            </body:CompetentAuthorityDispatchOffice>
            |            <body:FirstTransporterTrader language="en">
            |              <body:VatNumber>GB32445345</body:VatNumber>
            |              <body:TraderName>Current 801 FirstTransporter</body:TraderName>
            |              <body:StreetName>Kerkstraat 55</body:StreetName>
            |              <body:Postcode>9000</body:Postcode>
            |              <body:City>Gent</body:City>
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
            |              <body:JourneyTime>H20</body:JourneyTime>
            |              <body:TransportArrangement>1</body:TransportArrangement>
            |            </body:HeaderEadEsad>
            |            <body:TransportMode>
            |              <body:TransportModeCode>1</body:TransportModeCode>
            |            </body:TransportMode>
            |            <body:MovementGuarantee>
            |              <body:GuarantorTypeCode>2</body:GuarantorTypeCode>
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
            |              <body:MaturationPeriodOrAgeOfProducts language="EN">Maturation Period</body:MaturationPeriodOrAgeOfProducts>
            |              <body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
            |              <body:DegreePlato>1.2</body:DegreePlato>
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
            |              <body:SealInformation language="en">Seal info</body:SealInformation>
            |            </body:TransportDetails>
            |          </body:EADESADContainer>
            |        </body:Body>
            |      </body:IE801>
            |    </mov:currentMovement>
            |  </mov:movementView>""".stripMargin))

        modelWithHours shouldBe ParseSuccess(getMovementResponse.copy(journeyTime = "20 hours"))

        val modelWithDays = GetMovementResponse.xmlReader.read(XML.loadString(
          """
            |<mov:movementView xsi:schemaLocation="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3 movementView.xsd" xmlns:mov="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3">
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
            |              <body:Traderid>GB11100000002</body:Traderid>
            |              <body:TraderName>Current 801 Consignee</body:TraderName>
            |              <body:StreetName>Aardvark Avenue 253</body:StreetName>
            |              <body:Postcode>SY1 3BQ</body:Postcode>
            |              <body:City>Shrewsbury</body:City>
            |            </body:ConsigneeTrader>
            |            <body:ExciseMovement>
            |              <body:AdministrativeReferenceCode>13AB7778889991ABCDEF9</body:AdministrativeReferenceCode>
            |              <body:DateAndTimeOfValidationOfEadEsad>2008-09-04T10:22:50</body:DateAndTimeOfValidationOfEadEsad>
            |            </body:ExciseMovement>
            |            <body:ConsignorTrader language="en">
            |              <body:TraderExciseNumber>GB12345GTR144</body:TraderExciseNumber>
            |              <body:TraderName>Current 801 Consignor</body:TraderName>
            |              <body:StreetName>Main101</body:StreetName>
            |              <body:Postcode>ZZ78</body:Postcode>
            |              <body:City>Zeebrugge</body:City>
            |            </body:ConsignorTrader>
            |            <body:PlaceOfDispatchTrader language="en">
            |              <body:ReferenceOfTaxWarehouse>GB12345GTR143</body:ReferenceOfTaxWarehouse>
            |              <body:TraderName>Current 801 Dispatcher</body:TraderName>
            |              <body:StreetName>Psiristreet 59</body:StreetName>
            |              <body:Postcode>45690</body:Postcode>
            |              <body:City>Athens</body:City>
            |            </body:PlaceOfDispatchTrader>
            |            <body:DeliveryPlaceCustomsOffice>
            |              <body:ReferenceNumber>FR000003</body:ReferenceNumber>
            |            </body:DeliveryPlaceCustomsOffice>
            |            <body:CompetentAuthorityDispatchOffice>
            |              <body:ReferenceNumber>GB000002</body:ReferenceNumber>
            |            </body:CompetentAuthorityDispatchOffice>
            |            <body:FirstTransporterTrader language="en">
            |              <body:VatNumber>GB32445345</body:VatNumber>
            |              <body:TraderName>Current 801 FirstTransporter</body:TraderName>
            |              <body:StreetName>Kerkstraat 55</body:StreetName>
            |              <body:Postcode>9000</body:Postcode>
            |              <body:City>Gent</body:City>
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
            |              <body:GuarantorTypeCode>2</body:GuarantorTypeCode>
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
            |              <body:MaturationPeriodOrAgeOfProducts language="EN">Maturation Period</body:MaturationPeriodOrAgeOfProducts>
            |              <body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
            |              <body:DegreePlato>1.2</body:DegreePlato>
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
            |              <body:SealInformation language="en">Seal info</body:SealInformation>
            |            </body:TransportDetails>
            |          </body:EADESADContainer>
            |        </body:Body>
            |      </body:IE801>
            |    </mov:currentMovement>
            |  </mov:movementView>""".stripMargin))

        modelWithDays shouldBe ParseSuccess(getMovementResponse.copy(journeyTime = "20 days"))
      }
    }

    "fail to read a movement" when {

      "missing status" in {

        val noStatusXML = XML.loadString(
          """
            |	<mov:movementView xsi:schemaLocation="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3 movementView.xsd"
            |		xmlns:mov="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3">
            |		<mov:currentMovement>
            |			<mov:version_transaction_ref>008</mov:version_transaction_ref>
            |			<body:IE801
            |				xmlns:body="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:IE801:V2.02">
            |				<body:Header
            |					xmlns:head="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:TMS:V2.02">
            |					<head:MessageSender>NDEA.FR</head:MessageSender>
            |					<head:MessageRecipient>NDEA.GB</head:MessageRecipient>
            |					<head:DateOfPreparation>2008-09-04</head:DateOfPreparation>
            |					<head:TimeOfPreparation>10:22:50</head:TimeOfPreparation>
            |					<head:MessageIdentifier>Message identifier</head:MessageIdentifier>
            |				</body:Header>
            |				<body:Body>
            |					<body:EADESADContainer>
            |						<body:ConsigneeTrader language="en">
            |							<body:Traderid>GB11100000002</body:Traderid>
            |							<body:TraderName>Current 801 Consignee</body:TraderName>
            |							<body:StreetName>Aardvark Avenue 253</body:StreetName>
            |							<body:Postcode>SY1 3BQ</body:Postcode>
            |							<body:City>Shrewsbury</body:City>
            |						</body:ConsigneeTrader>
            |						<body:ExciseMovement>
            |							<body:AdministrativeReferenceCode>13AB7778889991ABCDEF9</body:AdministrativeReferenceCode>
            |							<body:DateAndTimeOfValidationOfEad>2008-09-04T10:22:50</body:DateAndTimeOfValidationOfEad>
            |						</body:ExciseMovement>
            |						<body:ConsignorTrader language="en">
            |							<body:TraderExciseNumber>GB12345GTR144</body:TraderExciseNumber>
            |							<body:TraderName>Current 801 Consignor</body:TraderName>
            |							<body:StreetName>Main101</body:StreetName>
            |							<body:Postcode>ZZ78</body:Postcode>
            |							<body:City>Zeebrugge</body:City>
            |						</body:ConsignorTrader>
            |						<body:PlaceOfDispatchTrader language="en">
            |							<body:ReferenceOfTaxWarehouse>GB12345GTR143</body:ReferenceOfTaxWarehouse>
            |							<body:TraderName>Current 801 Dispatcher</body:TraderName>
            |							<body:StreetName>Psiristreet 59</body:StreetName>
            |							<body:Postcode>45690</body:Postcode>
            |							<body:City>Athens</body:City>
            |						</body:PlaceOfDispatchTrader>
            |						<body:DeliveryPlaceCustomsOffice>
            |							<body:ReferenceNumber>FR000003</body:ReferenceNumber>
            |						</body:DeliveryPlaceCustomsOffice>
            |						<body:CompetentAuthorityDispatchOffice>
            |							<body:ReferenceNumber>GB000002</body:ReferenceNumber>
            |						</body:CompetentAuthorityDispatchOffice>
            |						<body:FirstTransporterTrader language="en">
            |							<body:VatNumber>GB32445345</body:VatNumber>
            |							<body:TraderName>Current 801 FirstTransporter</body:TraderName>
            |							<body:StreetName>Kerkstraat 55</body:StreetName>
            |							<body:Postcode>9000</body:Postcode>
            |							<body:City>Gent</body:City>
            |						</body:FirstTransporterTrader>
            |						<body:DocumentCertificate>
            |							<body:DocumentDescription language="en">Test</body:DocumentDescription>
            |							<body:ReferenceOfDocument language="en">AB123</body:ReferenceOfDocument>
            |						</body:DocumentCertificate>
            |						<body:EadEsad>
            |							<body:LocalReferenceNumber>EN</body:LocalReferenceNumber>
            |							<body:InvoiceNumber>IN777888999</body:InvoiceNumber>
            |							<body:InvoiceDate>2008-09-04</body:InvoiceDate>
            |							<body:OriginTypeCode>1</body:OriginTypeCode>
            |							<body:DateOfDispatch>2008-11-20</body:DateOfDispatch>
            |							<body:TimeOfDispatch>10:00:00</body:TimeOfDispatch>
            |						</body:EadEsad>
            |						<body:HeaderEadEsad>
            |							<body:SequenceNumber>1</body:SequenceNumber>
            |							<body:DateAndTimeOfUpdateValidation>2008-09-04T10:22:50</body:DateAndTimeOfUpdateValidation>
            |							<body:DestinationTypeCode>6</body:DestinationTypeCode>
            |							<body:JourneyTime>H20</body:JourneyTime>
            |							<body:TransportArrangement>1</body:TransportArrangement>
            |						</body:HeaderEadEsad>
            |						<body:TransportMode>
            |							<body:TransportModeCode>1</body:TransportModeCode>
            |						</body:TransportMode>
            |						<body:MovementGuarantee>
            |							<body:GuarantorTypeCode>2</body:GuarantorTypeCode>
            |						</body:MovementGuarantee>
            |						<body:BodyEadEsad>
            |							<body:BodyRecordUniqueReference>1</body:BodyRecordUniqueReference>
            |							<body:ExciseProductCode>W200</body:ExciseProductCode>
            |							<body:CnCode>22041011</body:CnCode>
            |							<body:Quantity>500</body:Quantity>
            |							<body:GrossMass>900</body:GrossMass>
            |							<body:NetMass>375</body:NetMass>
            |							<body:FiscalMark language="en">FM564789 Fiscal Mark</body:FiscalMark>
            |							<body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
            |							<body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
            |							<body:SizeOfProducer>20000</body:SizeOfProducer>
            |							<body:CommercialDescription language="en">Retsina</body:CommercialDescription>
            |							<body:BrandNameOfProducts language="en">MALAMATINA</body:BrandNameOfProducts>
            |             <body:Package>
            |                <body:KindOfPackages>BO</body:KindOfPackages>
            |                <body:NumberOfPackages>125</body:NumberOfPackages>
            |                <body:CommercialSealIdentification>SEAL456789321</body:CommercialSealIdentification>
            |                <body:SealInformation language="en">Red Strip</body:SealInformation>
            |              </body:Package>
            |						</body:BodyEadEsad>
            |						<body:BodyEadEsad>
            |							<body:BodyRecordUniqueReference>2</body:BodyRecordUniqueReference>
            |							<body:ExciseProductCode>W300</body:ExciseProductCode>
            |							<body:CnCode>27111901</body:CnCode>
            |							<body:Quantity>501</body:Quantity>
            |							<body:GrossMass>901</body:GrossMass>
            |							<body:NetMass>475</body:NetMass>
            |             <body:AlcoholicStrengthByVolumeInPercentage>12.7</body:AlcoholicStrengthByVolumeInPercentage>
            |							<body:FiscalMark language="en">FM564790 Fiscal Mark</body:FiscalMark>
            |							<body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
            |							<body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
            |							<body:SizeOfProducer>20000</body:SizeOfProducer>
            |							<body:CommercialDescription language="en">Retsina</body:CommercialDescription>
            |							<body:BrandNameOfProducts language="en">BrandName</body:BrandNameOfProducts>
            |             <body:Package>
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
            |						</body:BodyEadEsad>
            |					</body:EADESADContainer>
            |				</body:Body>
            |			</body:IE801>
            |		</mov:currentMovement>
            |	</mov:movementView>""".stripMargin)

        GetMovementResponse.xmlReader.read(noStatusXML) shouldBe ParseFailure(EmptyError(GetMovementResponse.eadStatus))
      }

      "missing LRN" in {

        val noLrnXML = XML.loadString(
          """
            |	<mov:movementView xsi:schemaLocation="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3 movementView.xsd"
            |		xmlns:mov="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3">
            |		<mov:currentMovement>
            |     <mov:status>Accepted</mov:status>
            |			<mov:version_transaction_ref>008</mov:version_transaction_ref>
            |			<body:IE801
            |				xmlns:body="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:IE801:V2.02">
            |				<body:Header
            |					xmlns:head="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:TMS:V2.02">
            |					<head:MessageSender>NDEA.FR</head:MessageSender>
            |					<head:MessageRecipient>NDEA.GB</head:MessageRecipient>
            |					<head:DateOfPreparation>2008-09-04</head:DateOfPreparation>
            |					<head:TimeOfPreparation>10:22:50</head:TimeOfPreparation>
            |					<head:MessageIdentifier>Message identifier</head:MessageIdentifier>
            |				</body:Header>
            |				<body:Body>
            |					<body:EADESADContainer>
            |						<body:ConsigneeTrader language="en">
            |							<body:Traderid>GB11100000002</body:Traderid>
            |							<body:TraderName>Current 801 Consignee</body:TraderName>
            |							<body:StreetName>Aardvark Avenue 253</body:StreetName>
            |							<body:Postcode>SY1 3BQ</body:Postcode>
            |							<body:City>Shrewsbury</body:City>
            |						</body:ConsigneeTrader>
            |						<body:ExciseMovement>
            |							<body:AdministrativeReferenceCode>13AB7778889991ABCDEF9</body:AdministrativeReferenceCode>
            |							<body:DateAndTimeOfValidationOfEad>2008-09-04T10:22:50</body:DateAndTimeOfValidationOfEad>
            |						</body:ExciseMovement>
            |						<body:ConsignorTrader language="en">
            |							<body:TraderExciseNumber>GB12345GTR144</body:TraderExciseNumber>
            |							<body:TraderName>Current 801 Consignor</body:TraderName>
            |							<body:StreetName>Main101</body:StreetName>
            |							<body:Postcode>ZZ78</body:Postcode>
            |							<body:City>Zeebrugge</body:City>
            |						</body:ConsignorTrader>
            |						<body:PlaceOfDispatchTrader language="en">
            |							<body:ReferenceOfTaxWarehouse>GB12345GTR143</body:ReferenceOfTaxWarehouse>
            |							<body:TraderName>Current 801 Dispatcher</body:TraderName>
            |							<body:StreetName>Psiristreet 59</body:StreetName>
            |							<body:Postcode>45690</body:Postcode>
            |							<body:City>Athens</body:City>
            |						</body:PlaceOfDispatchTrader>
            |						<body:DeliveryPlaceCustomsOffice>
            |							<body:ReferenceNumber>FR000003</body:ReferenceNumber>
            |						</body:DeliveryPlaceCustomsOffice>
            |						<body:CompetentAuthorityDispatchOffice>
            |							<body:ReferenceNumber>GB000002</body:ReferenceNumber>
            |						</body:CompetentAuthorityDispatchOffice>
            |						<body:FirstTransporterTrader language="en">
            |							<body:VatNumber>GB32445345</body:VatNumber>
            |							<body:TraderName>Current 801 FirstTransporter</body:TraderName>
            |							<body:StreetName>Kerkstraat 55</body:StreetName>
            |							<body:Postcode>9000</body:Postcode>
            |							<body:City>Gent</body:City>
            |						</body:FirstTransporterTrader>
            |						<body:DocumentCertificate>
            |							<body:DocumentDescription language="en">Test</body:DocumentDescription>
            |							<body:ReferenceOfDocument language="en">AB123</body:ReferenceOfDocument>
            |						</body:DocumentCertificate>
            |						<body:EadEsad>
            |							<body:InvoiceNumber>IN777888999</body:InvoiceNumber>
            |							<body:InvoiceDate>2008-09-04</body:InvoiceDate>
            |							<body:OriginTypeCode>1</body:OriginTypeCode>
            |							<body:DateOfDispatch>2008-11-20</body:DateOfDispatch>
            |							<body:TimeOfDispatch>10:00:00</body:TimeOfDispatch>
            |						</body:EadEsad>
            |						<body:HeaderEadEsad>
            |							<body:SequenceNumber>1</body:SequenceNumber>
            |							<body:DateAndTimeOfUpdateValidation>2008-09-04T10:22:50</body:DateAndTimeOfUpdateValidation>
            |							<body:DestinationTypeCode>6</body:DestinationTypeCode>
            |							<body:JourneyTime>H20</body:JourneyTime>
            |							<body:TransportArrangement>1</body:TransportArrangement>
            |						</body:HeaderEadEsad>
            |						<body:TransportMode>
            |							<body:TransportModeCode>1</body:TransportModeCode>
            |						</body:TransportMode>
            |						<body:MovementGuarantee>
            |							<body:GuarantorTypeCode>2</body:GuarantorTypeCode>
            |						</body:MovementGuarantee>
            |						<body:BodyEadEsad>
            |							<body:BodyRecordUniqueReference>1</body:BodyRecordUniqueReference>
            |							<body:ExciseProductCode>W200</body:ExciseProductCode>
            |							<body:CnCode>22041011</body:CnCode>
            |							<body:Quantity>500</body:Quantity>
            |							<body:GrossMass>900</body:GrossMass>
            |							<body:NetMass>375</body:NetMass>
            |							<body:FiscalMark language="en">FM564789 Fiscal Mark</body:FiscalMark>
            |							<body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
            |							<body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
            |							<body:SizeOfProducer>20000</body:SizeOfProducer>
            |							<body:CommercialDescription language="en">Retsina</body:CommercialDescription>
            |							<body:BrandNameOfProducts language="en">MALAMATINA</body:BrandNameOfProducts>
            |             <body:Package>
            |                <body:KindOfPackages>BO</body:KindOfPackages>
            |                <body:NumberOfPackages>125</body:NumberOfPackages>
            |                <body:CommercialSealIdentification>SEAL456789321</body:CommercialSealIdentification>
            |                <body:SealInformation language="en">Red Strip</body:SealInformation>
            |              </body:Package>
            |						</body:BodyEadEsad>
            |						<body:BodyEadEsad>
            |							<body:BodyRecordUniqueReference>2</body:BodyRecordUniqueReference>
            |							<body:ExciseProductCode>W300</body:ExciseProductCode>
            |							<body:CnCode>27111901</body:CnCode>
            |							<body:Quantity>501</body:Quantity>
            |							<body:GrossMass>901</body:GrossMass>
            |							<body:NetMass>475</body:NetMass>
            |             <body:AlcoholicStrengthByVolumeInPercentage>12.7</body:AlcoholicStrengthByVolumeInPercentage>
            |							<body:FiscalMark language="en">FM564790 Fiscal Mark</body:FiscalMark>
            |							<body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
            |							<body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
            |							<body:SizeOfProducer>20000</body:SizeOfProducer>
            |							<body:CommercialDescription language="en">Retsina</body:CommercialDescription>
            |							<body:BrandNameOfProducts language="en">BrandName</body:BrandNameOfProducts>
            |             <body:Package>
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
            |						</body:BodyEadEsad>
            |					</body:EADESADContainer>
            |				</body:Body>
            |			</body:IE801>
            |		</mov:currentMovement>
            |	</mov:movementView>""".stripMargin)

        GetMovementResponse.xmlReader.read(noLrnXML) shouldBe ParseFailure(EmptyError(GetMovementResponse.localReferenceNumber))
      }

      "missing consignorName" in {

        val noConsignorXML = XML.loadString(
          """
            |	<mov:movementView xsi:schemaLocation="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3 movementView.xsd"
            |		xmlns:mov="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3">
            |		<mov:currentMovement>
            |     <mov:status>Accepted</mov:status>
            |			<mov:version_transaction_ref>008</mov:version_transaction_ref>
            |			<body:IE801
            |				xmlns:body="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:IE801:V2.02">
            |				<body:Header
            |					xmlns:head="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:TMS:V2.02">
            |					<head:MessageSender>NDEA.FR</head:MessageSender>
            |					<head:MessageRecipient>NDEA.GB</head:MessageRecipient>
            |					<head:DateOfPreparation>2008-09-04</head:DateOfPreparation>
            |					<head:TimeOfPreparation>10:22:50</head:TimeOfPreparation>
            |					<head:MessageIdentifier>Message identifier</head:MessageIdentifier>
            |				</body:Header>
            |				<body:Body>
            |					<body:EADESADContainer>
            |						<body:ConsigneeTrader language="en">
            |							<body:Traderid>GB11100000002</body:Traderid>
            |							<body:StreetName>Aardvark Avenue 253</body:StreetName>
            |							<body:Postcode>SY1 3BQ</body:Postcode>
            |							<body:City>Shrewsbury</body:City>
            |						</body:ConsigneeTrader>
            |						<body:ExciseMovement>
            |							<body:AdministrativeReferenceCode>13AB7778889991ABCDEF9</body:AdministrativeReferenceCode>
            |							<body:DateAndTimeOfValidationOfEad>2008-09-04T10:22:50</body:DateAndTimeOfValidationOfEad>
            |						</body:ExciseMovement>
            |						<body:ConsignorTrader language="en">
            |							<body:TraderExciseNumber>GB12345GTR144</body:TraderExciseNumber>
            |							<body:StreetName>Main101</body:StreetName>
            |							<body:Postcode>ZZ78</body:Postcode>
            |							<body:City>Zeebrugge</body:City>
            |						</body:ConsignorTrader>
            |						<body:PlaceOfDispatchTrader language="en">
            |							<body:ReferenceOfTaxWarehouse>GB12345GTR143</body:ReferenceOfTaxWarehouse>
            |							<body:TraderName>Current 801 Dispatcher</body:TraderName>
            |							<body:StreetName>Psiristreet 59</body:StreetName>
            |							<body:Postcode>45690</body:Postcode>
            |							<body:City>Athens</body:City>
            |						</body:PlaceOfDispatchTrader>
            |						<body:DeliveryPlaceCustomsOffice>
            |							<body:ReferenceNumber>FR000003</body:ReferenceNumber>
            |						</body:DeliveryPlaceCustomsOffice>
            |						<body:CompetentAuthorityDispatchOffice>
            |							<body:ReferenceNumber>GB000002</body:ReferenceNumber>
            |						</body:CompetentAuthorityDispatchOffice>
            |						<body:FirstTransporterTrader language="en">
            |							<body:VatNumber>GB32445345</body:VatNumber>
            |							<body:TraderName>Current 801 FirstTransporter</body:TraderName>
            |							<body:StreetName>Kerkstraat 55</body:StreetName>
            |							<body:Postcode>9000</body:Postcode>
            |							<body:City>Gent</body:City>
            |						</body:FirstTransporterTrader>
            |						<body:DocumentCertificate>
            |							<body:DocumentDescription language="en">Test</body:DocumentDescription>
            |							<body:ReferenceOfDocument language="en">AB123</body:ReferenceOfDocument>
            |						</body:DocumentCertificate>
            |						<body:EadEsad>
            |							<body:LocalReferenceNumber>EN</body:LocalReferenceNumber>
            |							<body:InvoiceNumber>IN777888999</body:InvoiceNumber>
            |							<body:InvoiceDate>2008-09-04</body:InvoiceDate>
            |							<body:OriginTypeCode>1</body:OriginTypeCode>
            |							<body:DateOfDispatch>2008-11-20</body:DateOfDispatch>
            |							<body:TimeOfDispatch>10:00:00</body:TimeOfDispatch>
            |						</body:EadEsad>
            |						<body:HeaderEadEsad>
            |							<body:SequenceNumber>1</body:SequenceNumber>
            |							<body:DateAndTimeOfUpdateValidation>2008-09-04T10:22:50</body:DateAndTimeOfUpdateValidation>
            |							<body:DestinationTypeCode>6</body:DestinationTypeCode>
            |							<body:JourneyTime>H20</body:JourneyTime>
            |							<body:TransportArrangement>1</body:TransportArrangement>
            |						</body:HeaderEadEsad>
            |						<body:TransportMode>
            |							<body:TransportModeCode>1</body:TransportModeCode>
            |						</body:TransportMode>
            |						<body:MovementGuarantee>
            |							<body:GuarantorTypeCode>2</body:GuarantorTypeCode>
            |						</body:MovementGuarantee>
            |						<body:BodyEadEsad>
            |							<body:BodyRecordUniqueReference>1</body:BodyRecordUniqueReference>
            |							<body:ExciseProductCode>W200</body:ExciseProductCode>
            |							<body:CnCode>22041011</body:CnCode>
            |							<body:Quantity>500</body:Quantity>
            |							<body:GrossMass>900</body:GrossMass>
            |							<body:NetMass>375</body:NetMass>
            |							<body:FiscalMark language="en">FM564789 Fiscal Mark</body:FiscalMark>
            |							<body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
            |							<body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
            |							<body:SizeOfProducer>20000</body:SizeOfProducer>
            |							<body:CommercialDescription language="en">Retsina</body:CommercialDescription>
            |							<body:BrandNameOfProducts language="en">MALAMATINA</body:BrandNameOfProducts>
            |             <body:Package>
            |                <body:KindOfPackages>BO</body:KindOfPackages>
            |                <body:NumberOfPackages>125</body:NumberOfPackages>
            |                <body:CommercialSealIdentification>SEAL456789321</body:CommercialSealIdentification>
            |                <body:SealInformation language="en">Red Strip</body:SealInformation>
            |              </body:Package>
            |						</body:BodyEadEsad>
            |						<body:BodyEadEsad>
            |							<body:BodyRecordUniqueReference>2</body:BodyRecordUniqueReference>
            |							<body:ExciseProductCode>W300</body:ExciseProductCode>
            |							<body:CnCode>27111901</body:CnCode>
            |							<body:Quantity>501</body:Quantity>
            |							<body:GrossMass>901</body:GrossMass>
            |							<body:NetMass>475</body:NetMass>
            |             <body:AlcoholicStrengthByVolumeInPercentage>12.7</body:AlcoholicStrengthByVolumeInPercentage>
            |							<body:FiscalMark language="en">FM564790 Fiscal Mark</body:FiscalMark>
            |							<body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
            |							<body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
            |							<body:SizeOfProducer>20000</body:SizeOfProducer>
            |							<body:CommercialDescription language="en">Retsina</body:CommercialDescription>
            |							<body:BrandNameOfProducts language="en">BrandName</body:BrandNameOfProducts>
            |             <body:Package>
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
            |						</body:BodyEadEsad>
            |					</body:EADESADContainer>
            |				</body:Body>
            |			</body:IE801>
            |		</mov:currentMovement>
            |	</mov:movementView>""".stripMargin)

        GetMovementResponse.xmlReader.read(noConsignorXML) shouldBe ParseFailure(EmptyError(GetMovementResponse.consignorTrader \\ "TraderName"))
      }

      "missing consignorERN" in {

        val noConsignorXML = XML.loadString(
          """
            |	<mov:movementView xsi:schemaLocation="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3 movementView.xsd"
            |		xmlns:mov="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3">
            |		<mov:currentMovement>
            |     <mov:status>Accepted</mov:status>
            |			<mov:version_transaction_ref>008</mov:version_transaction_ref>
            |			<body:IE801
            |				xmlns:body="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:IE801:V2.02">
            |				<body:Header
            |					xmlns:head="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:TMS:V2.02">
            |					<head:MessageSender>NDEA.FR</head:MessageSender>
            |					<head:MessageRecipient>NDEA.GB</head:MessageRecipient>
            |					<head:DateOfPreparation>2008-09-04</head:DateOfPreparation>
            |					<head:TimeOfPreparation>10:22:50</head:TimeOfPreparation>
            |					<head:MessageIdentifier>Message identifier</head:MessageIdentifier>
            |				</body:Header>
            |				<body:Body>
            |					<body:EADESADContainer>
            |						<body:ConsigneeTrader language="en">
            |							<body:Traderid>GB11100000002</body:Traderid>
            |							<body:StreetName>Aardvark Avenue 253</body:StreetName>
            |							<body:Postcode>SY1 3BQ</body:Postcode>
            |							<body:City>Shrewsbury</body:City>
            |						</body:ConsigneeTrader>
            |						<body:ExciseMovement>
            |							<body:AdministrativeReferenceCode>13AB7778889991ABCDEF9</body:AdministrativeReferenceCode>
            |							<body:DateAndTimeOfValidationOfEad>2008-09-04T10:22:50</body:DateAndTimeOfValidationOfEad>
            |						</body:ExciseMovement>
            |						<body:ConsignorTrader language="en">
            |							<body:TraderName>Current 801 Consignor</body:TraderName>
            |							<body:StreetName>Main101</body:StreetName>
            |							<body:Postcode>ZZ78</body:Postcode>
            |							<body:City>Zeebrugge</body:City>
            |						</body:ConsignorTrader>
            |						<body:PlaceOfDispatchTrader language="en">
            |							<body:ReferenceOfTaxWarehouse>GB12345GTR143</body:ReferenceOfTaxWarehouse>
            |							<body:TraderName>Current 801 Dispatcher</body:TraderName>
            |							<body:StreetName>Psiristreet 59</body:StreetName>
            |							<body:Postcode>45690</body:Postcode>
            |							<body:City>Athens</body:City>
            |						</body:PlaceOfDispatchTrader>
            |						<body:DeliveryPlaceCustomsOffice>
            |							<body:ReferenceNumber>FR000003</body:ReferenceNumber>
            |						</body:DeliveryPlaceCustomsOffice>
            |						<body:CompetentAuthorityDispatchOffice>
            |							<body:ReferenceNumber>GB000002</body:ReferenceNumber>
            |						</body:CompetentAuthorityDispatchOffice>
            |						<body:FirstTransporterTrader language="en">
            |							<body:VatNumber>GB32445345</body:VatNumber>
            |							<body:TraderName>Current 801 FirstTransporter</body:TraderName>
            |							<body:StreetName>Kerkstraat 55</body:StreetName>
            |							<body:Postcode>9000</body:Postcode>
            |							<body:City>Gent</body:City>
            |						</body:FirstTransporterTrader>
            |						<body:DocumentCertificate>
            |							<body:DocumentDescription language="en">Test</body:DocumentDescription>
            |							<body:ReferenceOfDocument language="en">AB123</body:ReferenceOfDocument>
            |						</body:DocumentCertificate>
            |						<body:EadEsad>
            |							<body:LocalReferenceNumber>EN</body:LocalReferenceNumber>
            |							<body:InvoiceNumber>IN777888999</body:InvoiceNumber>
            |							<body:InvoiceDate>2008-09-04</body:InvoiceDate>
            |							<body:OriginTypeCode>1</body:OriginTypeCode>
            |							<body:DateOfDispatch>2008-11-20</body:DateOfDispatch>
            |							<body:TimeOfDispatch>10:00:00</body:TimeOfDispatch>
            |						</body:EadEsad>
            |						<body:HeaderEadEsad>
            |							<body:SequenceNumber>1</body:SequenceNumber>
            |							<body:DateAndTimeOfUpdateValidation>2008-09-04T10:22:50</body:DateAndTimeOfUpdateValidation>
            |							<body:DestinationTypeCode>6</body:DestinationTypeCode>
            |							<body:JourneyTime>H20</body:JourneyTime>
            |							<body:TransportArrangement>1</body:TransportArrangement>
            |						</body:HeaderEadEsad>
            |						<body:TransportMode>
            |							<body:TransportModeCode>1</body:TransportModeCode>
            |						</body:TransportMode>
            |						<body:MovementGuarantee>
            |							<body:GuarantorTypeCode>2</body:GuarantorTypeCode>
            |						</body:MovementGuarantee>
            |						<body:BodyEadEsad>
            |							<body:BodyRecordUniqueReference>1</body:BodyRecordUniqueReference>
            |							<body:ExciseProductCode>W200</body:ExciseProductCode>
            |							<body:CnCode>22041011</body:CnCode>
            |							<body:Quantity>500</body:Quantity>
            |							<body:GrossMass>900</body:GrossMass>
            |							<body:NetMass>375</body:NetMass>
            |							<body:FiscalMark language="en">FM564789 Fiscal Mark</body:FiscalMark>
            |							<body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
            |							<body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
            |							<body:SizeOfProducer>20000</body:SizeOfProducer>
            |							<body:CommercialDescription language="en">Retsina</body:CommercialDescription>
            |							<body:BrandNameOfProducts language="en">MALAMATINA</body:BrandNameOfProducts>
            |             <body:Package>
            |                <body:KindOfPackages>BO</body:KindOfPackages>
            |                <body:NumberOfPackages>125</body:NumberOfPackages>
            |                <body:CommercialSealIdentification>SEAL456789321</body:CommercialSealIdentification>
            |                <body:SealInformation language="en">Red Strip</body:SealInformation>
            |              </body:Package>
            |						</body:BodyEadEsad>
            |						<body:BodyEadEsad>
            |							<body:BodyRecordUniqueReference>2</body:BodyRecordUniqueReference>
            |							<body:ExciseProductCode>W300</body:ExciseProductCode>
            |							<body:CnCode>27111901</body:CnCode>
            |							<body:Quantity>501</body:Quantity>
            |							<body:GrossMass>901</body:GrossMass>
            |							<body:NetMass>475</body:NetMass>
            |             <body:AlcoholicStrengthByVolumeInPercentage>12.7</body:AlcoholicStrengthByVolumeInPercentage>
            |							<body:FiscalMark language="en">FM564790 Fiscal Mark</body:FiscalMark>
            |							<body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
            |							<body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
            |							<body:SizeOfProducer>20000</body:SizeOfProducer>
            |							<body:CommercialDescription language="en">Retsina</body:CommercialDescription>
            |							<body:BrandNameOfProducts language="en">BrandName</body:BrandNameOfProducts>
            |             <body:Package>
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
            |						</body:BodyEadEsad>
            |					</body:EADESADContainer>
            |				</body:Body>
            |			</body:IE801>
            |		</mov:currentMovement>
            |	</mov:movementView>""".stripMargin)

        GetMovementResponse.xmlReader.read(noConsignorXML) shouldBe ParseFailure(EmptyError(GetMovementResponse.consignorTrader \\ "TraderExciseNumber"))
      }

      "missing dateOfDispatch" in {

        val noDateOfDispatchXML = XML.loadString(
          """
            |	<mov:movementView xsi:schemaLocation="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3 movementView.xsd"
            |		xmlns:mov="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3">
            |		<mov:currentMovement>
            |     <mov:status>Accepted</mov:status>
            |			<mov:version_transaction_ref>008</mov:version_transaction_ref>
            |			<body:IE801
            |				xmlns:body="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:IE801:V2.02">
            |				<body:Header
            |					xmlns:head="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:TMS:V2.02">
            |					<head:MessageSender>NDEA.FR</head:MessageSender>
            |					<head:MessageRecipient>NDEA.GB</head:MessageRecipient>
            |					<head:DateOfPreparation>2008-09-04</head:DateOfPreparation>
            |					<head:TimeOfPreparation>10:22:50</head:TimeOfPreparation>
            |					<head:MessageIdentifier>Message identifier</head:MessageIdentifier>
            |				</body:Header>
            |				<body:Body>
            |					<body:EADESADContainer>
            |						<body:ConsigneeTrader language="en">
            |							<body:Traderid>GB11100000002</body:Traderid>
            |							<body:StreetName>Aardvark Avenue 253</body:StreetName>
            |							<body:Postcode>SY1 3BQ</body:Postcode>
            |							<body:City>Shrewsbury</body:City>
            |						</body:ConsigneeTrader>
            |						<body:ExciseMovement>
            |							<body:AdministrativeReferenceCode>13AB7778889991ABCDEF9</body:AdministrativeReferenceCode>
            |							<body:DateAndTimeOfValidationOfEad>2008-09-04T10:22:50</body:DateAndTimeOfValidationOfEad>
            |						</body:ExciseMovement>
            |						<body:ConsignorTrader language="en">
            |							<body:TraderExciseNumber>GB12345GTR144</body:TraderExciseNumber>
            |							<body:TraderName>Current 801 Consignor</body:TraderName>
            |							<body:StreetName>Main101</body:StreetName>
            |							<body:Postcode>ZZ78</body:Postcode>
            |							<body:City>Zeebrugge</body:City>
            |						</body:ConsignorTrader>
            |						<body:PlaceOfDispatchTrader language="en">
            |							<body:ReferenceOfTaxWarehouse>GB12345GTR143</body:ReferenceOfTaxWarehouse>
            |							<body:TraderName>Current 801 Dispatcher</body:TraderName>
            |							<body:StreetName>Psiristreet 59</body:StreetName>
            |							<body:Postcode>45690</body:Postcode>
            |							<body:City>Athens</body:City>
            |						</body:PlaceOfDispatchTrader>
            |						<body:DeliveryPlaceCustomsOffice>
            |							<body:ReferenceNumber>FR000003</body:ReferenceNumber>
            |						</body:DeliveryPlaceCustomsOffice>
            |						<body:CompetentAuthorityDispatchOffice>
            |							<body:ReferenceNumber>GB000002</body:ReferenceNumber>
            |						</body:CompetentAuthorityDispatchOffice>
            |						<body:FirstTransporterTrader language="en">
            |							<body:VatNumber>GB32445345</body:VatNumber>
            |							<body:TraderName>Current 801 FirstTransporter</body:TraderName>
            |							<body:StreetName>Kerkstraat 55</body:StreetName>
            |							<body:Postcode>9000</body:Postcode>
            |							<body:City>Gent</body:City>
            |						</body:FirstTransporterTrader>
            |						<body:DocumentCertificate>
            |							<body:DocumentDescription language="en">Test</body:DocumentDescription>
            |							<body:ReferenceOfDocument language="en">AB123</body:ReferenceOfDocument>
            |						</body:DocumentCertificate>
            |						<body:EadEsad>
            |							<body:LocalReferenceNumber>EN</body:LocalReferenceNumber>
            |							<body:InvoiceNumber>IN777888999</body:InvoiceNumber>
            |							<body:InvoiceDate>2008-09-04</body:InvoiceDate>
            |							<body:OriginTypeCode>1</body:OriginTypeCode>
            |							<body:TimeOfDispatch>10:00:00</body:TimeOfDispatch>
            |						</body:EadEsad>
            |						<body:HeaderEadEsad>
            |							<body:SequenceNumber>1</body:SequenceNumber>
            |							<body:DateAndTimeOfUpdateValidation>2008-09-04T10:22:50</body:DateAndTimeOfUpdateValidation>
            |							<body:DestinationTypeCode>6</body:DestinationTypeCode>
            |							<body:JourneyTime>H20</body:JourneyTime>
            |							<body:TransportArrangement>1</body:TransportArrangement>
            |						</body:HeaderEadEsad>
            |						<body:TransportMode>
            |							<body:TransportModeCode>1</body:TransportModeCode>
            |						</body:TransportMode>
            |						<body:MovementGuarantee>
            |							<body:GuarantorTypeCode>2</body:GuarantorTypeCode>
            |						</body:MovementGuarantee>
            |						<body:BodyEadEsad>
            |							<body:BodyRecordUniqueReference>1</body:BodyRecordUniqueReference>
            |							<body:ExciseProductCode>W200</body:ExciseProductCode>
            |							<body:CnCode>22041011</body:CnCode>
            |							<body:Quantity>500</body:Quantity>
            |							<body:GrossMass>900</body:GrossMass>
            |							<body:NetMass>375</body:NetMass>
            |							<body:FiscalMark language="en">FM564789 Fiscal Mark</body:FiscalMark>
            |							<body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
            |							<body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
            |							<body:SizeOfProducer>20000</body:SizeOfProducer>
            |							<body:CommercialDescription language="en">Retsina</body:CommercialDescription>
            |							<body:BrandNameOfProducts language="en">MALAMATINA</body:BrandNameOfProducts>
            |             <body:Package>
            |                <body:KindOfPackages>BO</body:KindOfPackages>
            |                <body:NumberOfPackages>125</body:NumberOfPackages>
            |                <body:CommercialSealIdentification>SEAL456789321</body:CommercialSealIdentification>
            |                <body:SealInformation language="en">Red Strip</body:SealInformation>
            |              </body:Package>
            |						</body:BodyEadEsad>
            |						<body:BodyEadEsad>
            |							<body:BodyRecordUniqueReference>2</body:BodyRecordUniqueReference>
            |							<body:ExciseProductCode>W300</body:ExciseProductCode>
            |							<body:CnCode>27111901</body:CnCode>
            |							<body:Quantity>501</body:Quantity>
            |							<body:GrossMass>901</body:GrossMass>
            |							<body:NetMass>475</body:NetMass>
            |             <body:AlcoholicStrengthByVolumeInPercentage>12.7</body:AlcoholicStrengthByVolumeInPercentage>
            |							<body:FiscalMark language="en">FM564790 Fiscal Mark</body:FiscalMark>
            |							<body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
            |							<body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
            |							<body:SizeOfProducer>20000</body:SizeOfProducer>
            |							<body:CommercialDescription language="en">Retsina</body:CommercialDescription>
            |							<body:BrandNameOfProducts language="en">BrandName</body:BrandNameOfProducts>
            |             <body:Package>
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
            |						</body:BodyEadEsad>
            |					</body:EADESADContainer>
            |				</body:Body>
            |			</body:IE801>
            |		</mov:currentMovement>
            |	</mov:movementView>""".stripMargin)

        GetMovementResponse.xmlReader.read(noDateOfDispatchXML) shouldBe ParseFailure(EmptyError(GetMovementResponse.dateOfDispatch))
      }

      "missing journeyTime" in {

        val noJourneyTimeXML = XML.loadString(
          """
            |	<mov:movementView xsi:schemaLocation="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3 movementView.xsd"
            |		xmlns:mov="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3">
            |		<mov:currentMovement>
            |     <mov:status>Accepted</mov:status>
            |			<mov:version_transaction_ref>008</mov:version_transaction_ref>
            |			<body:IE801
            |				xmlns:body="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:IE801:V2.02">
            |				<body:Header
            |					xmlns:head="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:TMS:V2.02">
            |					<head:MessageSender>NDEA.FR</head:MessageSender>
            |					<head:MessageRecipient>NDEA.GB</head:MessageRecipient>
            |					<head:DateOfPreparation>2008-09-04</head:DateOfPreparation>
            |					<head:TimeOfPreparation>10:22:50</head:TimeOfPreparation>
            |					<head:MessageIdentifier>Message identifier</head:MessageIdentifier>
            |				</body:Header>
            |				<body:Body>
            |					<body:EADESADContainer>
            |						<body:ConsigneeTrader language="en">
            |							<body:Traderid>GB11100000002</body:Traderid>
            |							<body:StreetName>Aardvark Avenue 253</body:StreetName>
            |							<body:Postcode>SY1 3BQ</body:Postcode>
            |							<body:City>Shrewsbury</body:City>
            |						</body:ConsigneeTrader>
            |						<body:ExciseMovement>
            |							<body:AdministrativeReferenceCode>13AB7778889991ABCDEF9</body:AdministrativeReferenceCode>
            |							<body:DateAndTimeOfValidationOfEad>2008-09-04T10:22:50</body:DateAndTimeOfValidationOfEad>
            |						</body:ExciseMovement>
            |						<body:ConsignorTrader language="en">
            |							<body:TraderExciseNumber>GB12345GTR144</body:TraderExciseNumber>
            |							<body:TraderName>Current 801 Consignor</body:TraderName>
            |							<body:StreetName>Main101</body:StreetName>
            |							<body:Postcode>ZZ78</body:Postcode>
            |							<body:City>Zeebrugge</body:City>
            |						</body:ConsignorTrader>
            |						<body:PlaceOfDispatchTrader language="en">
            |							<body:ReferenceOfTaxWarehouse>GB12345GTR143</body:ReferenceOfTaxWarehouse>
            |							<body:TraderName>Current 801 Dispatcher</body:TraderName>
            |							<body:StreetName>Psiristreet 59</body:StreetName>
            |							<body:Postcode>45690</body:Postcode>
            |							<body:City>Athens</body:City>
            |						</body:PlaceOfDispatchTrader>
            |						<body:DeliveryPlaceCustomsOffice>
            |							<body:ReferenceNumber>FR000003</body:ReferenceNumber>
            |						</body:DeliveryPlaceCustomsOffice>
            |						<body:CompetentAuthorityDispatchOffice>
            |							<body:ReferenceNumber>GB000002</body:ReferenceNumber>
            |						</body:CompetentAuthorityDispatchOffice>
            |						<body:FirstTransporterTrader language="en">
            |							<body:VatNumber>GB32445345</body:VatNumber>
            |							<body:TraderName>Current 801 FirstTransporter</body:TraderName>
            |							<body:StreetName>Kerkstraat 55</body:StreetName>
            |							<body:Postcode>9000</body:Postcode>
            |							<body:City>Gent</body:City>
            |						</body:FirstTransporterTrader>
            |						<body:DocumentCertificate>
            |							<body:DocumentDescription language="en">Test</body:DocumentDescription>
            |							<body:ReferenceOfDocument language="en">AB123</body:ReferenceOfDocument>
            |						</body:DocumentCertificate>
            |						<body:EadEsad>
            |							<body:LocalReferenceNumber>EN</body:LocalReferenceNumber>
            |							<body:InvoiceNumber>IN777888999</body:InvoiceNumber>
            |							<body:InvoiceDate>2008-09-04</body:InvoiceDate>
            |							<body:OriginTypeCode>1</body:OriginTypeCode>
            |							<body:DateOfDispatch>2008-11-20</body:DateOfDispatch>
            |							<body:TimeOfDispatch>10:00:00</body:TimeOfDispatch>
            |						</body:EadEsad>
            |						<body:HeaderEadEsad>
            |							<body:SequenceNumber>1</body:SequenceNumber>
            |							<body:DateAndTimeOfUpdateValidation>2008-09-04T10:22:50</body:DateAndTimeOfUpdateValidation>
            |							<body:DestinationTypeCode>6</body:DestinationTypeCode>
            |							<body:TransportArrangement>1</body:TransportArrangement>
            |						</body:HeaderEadEsad>
            |						<body:TransportMode>
            |							<body:TransportModeCode>1</body:TransportModeCode>
            |						</body:TransportMode>
            |						<body:MovementGuarantee>
            |							<body:GuarantorTypeCode>2</body:GuarantorTypeCode>
            |						</body:MovementGuarantee>
            |						<body:BodyEadEsad>
            |							<body:BodyRecordUniqueReference>1</body:BodyRecordUniqueReference>
            |							<body:ExciseProductCode>W200</body:ExciseProductCode>
            |							<body:CnCode>22041011</body:CnCode>
            |							<body:Quantity>500</body:Quantity>
            |							<body:GrossMass>900</body:GrossMass>
            |							<body:NetMass>375</body:NetMass>
            |							<body:FiscalMark language="en">FM564789 Fiscal Mark</body:FiscalMark>
            |							<body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
            |							<body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
            |							<body:SizeOfProducer>20000</body:SizeOfProducer>
            |							<body:CommercialDescription language="en">Retsina</body:CommercialDescription>
            |							<body:BrandNameOfProducts language="en">MALAMATINA</body:BrandNameOfProducts>
            |             <body:Package>
            |                <body:KindOfPackages>BO</body:KindOfPackages>
            |                <body:NumberOfPackages>125</body:NumberOfPackages>
            |                <body:CommercialSealIdentification>SEAL456789321</body:CommercialSealIdentification>
            |                <body:SealInformation language="en">Red Strip</body:SealInformation>
            |              </body:Package>
            |						</body:BodyEadEsad>
            |						<body:BodyEadEsad>
            |							<body:BodyRecordUniqueReference>2</body:BodyRecordUniqueReference>
            |							<body:ExciseProductCode>W300</body:ExciseProductCode>
            |							<body:CnCode>27111901</body:CnCode>
            |							<body:Quantity>501</body:Quantity>
            |							<body:GrossMass>901</body:GrossMass>
            |							<body:NetMass>475</body:NetMass>
            |             <body:AlcoholicStrengthByVolumeInPercentage>12.7</body:AlcoholicStrengthByVolumeInPercentage>
            |							<body:FiscalMark language="en">FM564790 Fiscal Mark</body:FiscalMark>
            |							<body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
            |							<body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
            |							<body:SizeOfProducer>20000</body:SizeOfProducer>
            |							<body:CommercialDescription language="en">Retsina</body:CommercialDescription>
            |							<body:BrandNameOfProducts language="en">BrandName</body:BrandNameOfProducts>
            |             <body:Package>
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
            |						</body:BodyEadEsad>
            |					</body:EADESADContainer>
            |				</body:Body>
            |			</body:IE801>
            |		</mov:currentMovement>
            |	</mov:movementView>""".stripMargin)

        GetMovementResponse.xmlReader.read(noJourneyTimeXML) shouldBe ParseFailure(JourneyTimeParseFailure("Could not parse JourneyTime from XML, received: ''"))
      }
    }
  }
}
