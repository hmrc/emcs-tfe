/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.models.response

import com.lucidchart.open.xtract.{EmptyError, ParseFailure, ParseSuccess}
import play.api.libs.json.Json
import uk.gov.hmrc.emcstfe.fixtures.GetMovementFixture
import uk.gov.hmrc.emcstfe.models.common.JourneyTime.{Days, Hours, JourneyTimeParseFailure}
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

      "can handle duplicate CnCodes" in {

        GetMovementResponse.xmlReader.read(XML.loadString(
          // There are three CnCode values here but only two unique ones
          """<MovementDataResponse xsi:schemaLocation="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementData/3 MovementData.xsd"
            |	xmlns="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementData/3"
            |	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
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
            |						</body:BodyEadEsad>
            |						<body:BodyEadEsad>
            |							<body:BodyRecordUniqueReference>2</body:BodyRecordUniqueReference>
            |							<body:ExciseProductCode>W300</body:ExciseProductCode>
            |							<body:CnCode>22041019</body:CnCode>
            |							<body:Quantity>501</body:Quantity>
            |							<body:GrossMass>901</body:GrossMass>
            |							<body:NetMass>475</body:NetMass>
            |							<body:FiscalMark language="en">FM564790 Fiscal Mark</body:FiscalMark>
            |							<body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
            |							<body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
            |							<body:SizeOfProducer>20000</body:SizeOfProducer>
            |							<body:CommercialDescription language="en">Retsina</body:CommercialDescription>
            |							<body:BrandNameOfProducts language="en">BrandName</body:BrandNameOfProducts>
            |						</body:BodyEadEsad>
            |						<body:BodyEadEsad>
            |							<body:BodyRecordUniqueReference>2</body:BodyRecordUniqueReference>
            |							<body:ExciseProductCode>W300</body:ExciseProductCode>
            |							<body:CnCode>22041019</body:CnCode>
            |							<body:Quantity>501</body:Quantity>
            |							<body:GrossMass>901</body:GrossMass>
            |							<body:NetMass>475</body:NetMass>
            |							<body:FiscalMark language="en">FM564790 Fiscal Mark</body:FiscalMark>
            |							<body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
            |							<body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
            |							<body:SizeOfProducer>20000</body:SizeOfProducer>
            |							<body:CommercialDescription language="en">Retsina</body:CommercialDescription>
            |							<body:BrandNameOfProducts language="en">BrandName</body:BrandNameOfProducts>
            |						</body:BodyEadEsad>
            |					</body:EADESADContainer>
            |				</body:Body>
            |			</body:IE801>
            |		</mov:currentMovement>
            |	</mov:movementView>
            |</MovementDataResponse>""".stripMargin)) shouldBe ParseSuccess(getMovementResponse)
      }

      "handle hours and days" in {
        val modelWithHours = GetMovementResponse.xmlReader.read(XML.loadString(
          """<MovementDataResponse xsi:schemaLocation="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementData/3 MovementData.xsd"
            |	xmlns="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementData/3"
            |	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
            |	<mov:movementView xsi:schemaLocation="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3 movementView.xsd"
            |		xmlns:mov="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3">
            |		<mov:currentMovement>
            |			<mov:status>Accepted</mov:status>
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
            |						</body:BodyEadEsad>
            |						<body:BodyEadEsad>
            |							<body:BodyRecordUniqueReference>2</body:BodyRecordUniqueReference>
            |							<body:ExciseProductCode>W300</body:ExciseProductCode>
            |							<body:CnCode>22041019</body:CnCode>
            |							<body:Quantity>501</body:Quantity>
            |							<body:GrossMass>901</body:GrossMass>
            |							<body:NetMass>475</body:NetMass>
            |							<body:FiscalMark language="en">FM564790 Fiscal Mark</body:FiscalMark>
            |							<body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
            |							<body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
            |							<body:SizeOfProducer>20000</body:SizeOfProducer>
            |							<body:CommercialDescription language="en">Retsina</body:CommercialDescription>
            |							<body:BrandNameOfProducts language="en">BrandName</body:BrandNameOfProducts>
            |						</body:BodyEadEsad>
            |						<body:BodyEadEsad>
            |							<body:BodyRecordUniqueReference>2</body:BodyRecordUniqueReference>
            |							<body:ExciseProductCode>W300</body:ExciseProductCode>
            |							<body:CnCode>22041019</body:CnCode>
            |							<body:Quantity>501</body:Quantity>
            |							<body:GrossMass>901</body:GrossMass>
            |							<body:NetMass>475</body:NetMass>
            |							<body:FiscalMark language="en">FM564790 Fiscal Mark</body:FiscalMark>
            |							<body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
            |							<body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
            |							<body:SizeOfProducer>20000</body:SizeOfProducer>
            |							<body:CommercialDescription language="en">Retsina</body:CommercialDescription>
            |							<body:BrandNameOfProducts language="en">BrandName</body:BrandNameOfProducts>
            |						</body:BodyEadEsad>
            |					</body:EADESADContainer>
            |				</body:Body>
            |			</body:IE801>
            |		</mov:currentMovement>
            |	</mov:movementView>
            |</MovementDataResponse>""".stripMargin))

        modelWithHours shouldBe ParseSuccess(getMovementResponse.copy(journeyTime = Hours("20")))

        val modelWithDays = GetMovementResponse.xmlReader.read(XML.loadString(
          """<MovementDataResponse xsi:schemaLocation="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementData/3 MovementData.xsd"
            |	xmlns="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementData/3"
            |	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
            |	<mov:movementView xsi:schemaLocation="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3 movementView.xsd"
            |		xmlns:mov="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3">
            |		<mov:currentMovement>
            |			<mov:status>Accepted</mov:status>
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
            |						</body:BodyEadEsad>
            |						<body:BodyEadEsad>
            |							<body:BodyRecordUniqueReference>2</body:BodyRecordUniqueReference>
            |							<body:ExciseProductCode>W300</body:ExciseProductCode>
            |							<body:CnCode>22041019</body:CnCode>
            |							<body:Quantity>501</body:Quantity>
            |							<body:GrossMass>901</body:GrossMass>
            |							<body:NetMass>475</body:NetMass>
            |							<body:FiscalMark language="en">FM564790 Fiscal Mark</body:FiscalMark>
            |							<body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
            |							<body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
            |							<body:SizeOfProducer>20000</body:SizeOfProducer>
            |							<body:CommercialDescription language="en">Retsina</body:CommercialDescription>
            |							<body:BrandNameOfProducts language="en">BrandName</body:BrandNameOfProducts>
            |						</body:BodyEadEsad>
            |						<body:BodyEadEsad>
            |							<body:BodyRecordUniqueReference>2</body:BodyRecordUniqueReference>
            |							<body:ExciseProductCode>W300</body:ExciseProductCode>
            |							<body:CnCode>22041019</body:CnCode>
            |							<body:Quantity>501</body:Quantity>
            |							<body:GrossMass>901</body:GrossMass>
            |							<body:NetMass>475</body:NetMass>
            |							<body:FiscalMark language="en">FM564790 Fiscal Mark</body:FiscalMark>
            |							<body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
            |							<body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
            |							<body:SizeOfProducer>20000</body:SizeOfProducer>
            |							<body:CommercialDescription language="en">Retsina</body:CommercialDescription>
            |							<body:BrandNameOfProducts language="en">BrandName</body:BrandNameOfProducts>
            |						</body:BodyEadEsad>
            |					</body:EADESADContainer>
            |				</body:Body>
            |			</body:IE801>
            |		</mov:currentMovement>
            |	</mov:movementView>
            |</MovementDataResponse>""".stripMargin))

        modelWithDays shouldBe ParseSuccess(getMovementResponse.copy(journeyTime = Days("20")))
      }
    }

    "fail to read a movement" when {

      "missing status" in {

        val noStatusXML = XML.loadString(
          """<MovementDataResponse xsi:schemaLocation="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementData/3 MovementData.xsd"
            |	xmlns="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementData/3"
            |	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
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
            |						</body:BodyEadEsad>
            |						<body:BodyEadEsad>
            |							<body:BodyRecordUniqueReference>2</body:BodyRecordUniqueReference>
            |							<body:ExciseProductCode>W300</body:ExciseProductCode>
            |							<body:CnCode>22041019</body:CnCode>
            |							<body:Quantity>501</body:Quantity>
            |							<body:GrossMass>901</body:GrossMass>
            |							<body:NetMass>475</body:NetMass>
            |							<body:FiscalMark language="en">FM564790 Fiscal Mark</body:FiscalMark>
            |							<body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
            |							<body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
            |							<body:SizeOfProducer>20000</body:SizeOfProducer>
            |							<body:CommercialDescription language="en">Retsina</body:CommercialDescription>
            |							<body:BrandNameOfProducts language="en">BrandName</body:BrandNameOfProducts>
            |						</body:BodyEadEsad>
            |						<body:BodyEadEsad>
            |							<body:BodyRecordUniqueReference>2</body:BodyRecordUniqueReference>
            |							<body:ExciseProductCode>W300</body:ExciseProductCode>
            |							<body:CnCode>22041019</body:CnCode>
            |							<body:Quantity>501</body:Quantity>
            |							<body:GrossMass>901</body:GrossMass>
            |							<body:NetMass>475</body:NetMass>
            |							<body:FiscalMark language="en">FM564790 Fiscal Mark</body:FiscalMark>
            |							<body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
            |							<body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
            |							<body:SizeOfProducer>20000</body:SizeOfProducer>
            |							<body:CommercialDescription language="en">Retsina</body:CommercialDescription>
            |							<body:BrandNameOfProducts language="en">BrandName</body:BrandNameOfProducts>
            |						</body:BodyEadEsad>
            |					</body:EADESADContainer>
            |				</body:Body>
            |			</body:IE801>
            |		</mov:currentMovement>
            |	</mov:movementView>
            |</MovementDataResponse>""".stripMargin)

        GetMovementResponse.xmlReader.read(noStatusXML) shouldBe ParseFailure(EmptyError(GetMovementResponse.eadStatus))
      }

      "missing LRN" in {

        val noLrnXML = XML.loadString(
          """<MovementDataResponse xsi:schemaLocation="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementData/3 MovementData.xsd"
            |	xmlns="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementData/3"
            |	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
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
            |						</body:BodyEadEsad>
            |						<body:BodyEadEsad>
            |							<body:BodyRecordUniqueReference>2</body:BodyRecordUniqueReference>
            |							<body:ExciseProductCode>W300</body:ExciseProductCode>
            |							<body:CnCode>22041019</body:CnCode>
            |							<body:Quantity>501</body:Quantity>
            |							<body:GrossMass>901</body:GrossMass>
            |							<body:NetMass>475</body:NetMass>
            |							<body:FiscalMark language="en">FM564790 Fiscal Mark</body:FiscalMark>
            |							<body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
            |							<body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
            |							<body:SizeOfProducer>20000</body:SizeOfProducer>
            |							<body:CommercialDescription language="en">Retsina</body:CommercialDescription>
            |							<body:BrandNameOfProducts language="en">BrandName</body:BrandNameOfProducts>
            |						</body:BodyEadEsad>
            |						<body:BodyEadEsad>
            |							<body:BodyRecordUniqueReference>2</body:BodyRecordUniqueReference>
            |							<body:ExciseProductCode>W300</body:ExciseProductCode>
            |							<body:CnCode>22041019</body:CnCode>
            |							<body:Quantity>501</body:Quantity>
            |							<body:GrossMass>901</body:GrossMass>
            |							<body:NetMass>475</body:NetMass>
            |							<body:FiscalMark language="en">FM564790 Fiscal Mark</body:FiscalMark>
            |							<body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
            |							<body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
            |							<body:SizeOfProducer>20000</body:SizeOfProducer>
            |							<body:CommercialDescription language="en">Retsina</body:CommercialDescription>
            |							<body:BrandNameOfProducts language="en">BrandName</body:BrandNameOfProducts>
            |						</body:BodyEadEsad>
            |					</body:EADESADContainer>
            |				</body:Body>
            |			</body:IE801>
            |		</mov:currentMovement>
            |	</mov:movementView>
            |</MovementDataResponse>""".stripMargin)

        GetMovementResponse.xmlReader.read(noLrnXML) shouldBe ParseFailure(EmptyError(GetMovementResponse.localReferenceNumber))
      }

      "missing consignorName" in {

        val noConsignorXML = XML.loadString(
          """<MovementDataResponse xsi:schemaLocation="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementData/3 MovementData.xsd"
            |	xmlns="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementData/3"
            |	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
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
            |						</body:BodyEadEsad>
            |						<body:BodyEadEsad>
            |							<body:BodyRecordUniqueReference>2</body:BodyRecordUniqueReference>
            |							<body:ExciseProductCode>W300</body:ExciseProductCode>
            |							<body:CnCode>22041019</body:CnCode>
            |							<body:Quantity>501</body:Quantity>
            |							<body:GrossMass>901</body:GrossMass>
            |							<body:NetMass>475</body:NetMass>
            |							<body:FiscalMark language="en">FM564790 Fiscal Mark</body:FiscalMark>
            |							<body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
            |							<body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
            |							<body:SizeOfProducer>20000</body:SizeOfProducer>
            |							<body:CommercialDescription language="en">Retsina</body:CommercialDescription>
            |							<body:BrandNameOfProducts language="en">BrandName</body:BrandNameOfProducts>
            |						</body:BodyEadEsad>
            |						<body:BodyEadEsad>
            |							<body:BodyRecordUniqueReference>2</body:BodyRecordUniqueReference>
            |							<body:ExciseProductCode>W300</body:ExciseProductCode>
            |							<body:CnCode>22041019</body:CnCode>
            |							<body:Quantity>501</body:Quantity>
            |							<body:GrossMass>901</body:GrossMass>
            |							<body:NetMass>475</body:NetMass>
            |							<body:FiscalMark language="en">FM564790 Fiscal Mark</body:FiscalMark>
            |							<body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
            |							<body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
            |							<body:SizeOfProducer>20000</body:SizeOfProducer>
            |							<body:CommercialDescription language="en">Retsina</body:CommercialDescription>
            |							<body:BrandNameOfProducts language="en">BrandName</body:BrandNameOfProducts>
            |						</body:BodyEadEsad>
            |					</body:EADESADContainer>
            |				</body:Body>
            |			</body:IE801>
            |		</mov:currentMovement>
            |	</mov:movementView>
            |</MovementDataResponse>""".stripMargin)

        GetMovementResponse.xmlReader.read(noConsignorXML) shouldBe ParseFailure(EmptyError(GetMovementResponse.consignorName))
      }

      "missing dateOfDispatch" in {

        val noDateOfDispatchXML = XML.loadString(
          """<MovementDataResponse xsi:schemaLocation="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementData/3 MovementData.xsd"
            |	xmlns="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementData/3"
            |	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
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
            |						</body:BodyEadEsad>
            |						<body:BodyEadEsad>
            |							<body:BodyRecordUniqueReference>2</body:BodyRecordUniqueReference>
            |							<body:ExciseProductCode>W300</body:ExciseProductCode>
            |							<body:CnCode>22041019</body:CnCode>
            |							<body:Quantity>501</body:Quantity>
            |							<body:GrossMass>901</body:GrossMass>
            |							<body:NetMass>475</body:NetMass>
            |							<body:FiscalMark language="en">FM564790 Fiscal Mark</body:FiscalMark>
            |							<body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
            |							<body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
            |							<body:SizeOfProducer>20000</body:SizeOfProducer>
            |							<body:CommercialDescription language="en">Retsina</body:CommercialDescription>
            |							<body:BrandNameOfProducts language="en">BrandName</body:BrandNameOfProducts>
            |						</body:BodyEadEsad>
            |						<body:BodyEadEsad>
            |							<body:BodyRecordUniqueReference>2</body:BodyRecordUniqueReference>
            |							<body:ExciseProductCode>W300</body:ExciseProductCode>
            |							<body:CnCode>22041019</body:CnCode>
            |							<body:Quantity>501</body:Quantity>
            |							<body:GrossMass>901</body:GrossMass>
            |							<body:NetMass>475</body:NetMass>
            |							<body:FiscalMark language="en">FM564790 Fiscal Mark</body:FiscalMark>
            |							<body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
            |							<body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
            |							<body:SizeOfProducer>20000</body:SizeOfProducer>
            |							<body:CommercialDescription language="en">Retsina</body:CommercialDescription>
            |							<body:BrandNameOfProducts language="en">BrandName</body:BrandNameOfProducts>
            |						</body:BodyEadEsad>
            |					</body:EADESADContainer>
            |				</body:Body>
            |			</body:IE801>
            |		</mov:currentMovement>
            |	</mov:movementView>
            |</MovementDataResponse>""".stripMargin)

        GetMovementResponse.xmlReader.read(noDateOfDispatchXML) shouldBe ParseFailure(EmptyError(GetMovementResponse.dateOfDispatch))
      }

      "missing journeyTime" in {

        val noJourneyTimeXML = XML.loadString(
          """<MovementDataResponse xsi:schemaLocation="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementData/3 MovementData.xsd"
            |	xmlns="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementData/3"
            |	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
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
            |						</body:BodyEadEsad>
            |						<body:BodyEadEsad>
            |							<body:BodyRecordUniqueReference>2</body:BodyRecordUniqueReference>
            |							<body:ExciseProductCode>W300</body:ExciseProductCode>
            |							<body:CnCode>22041019</body:CnCode>
            |							<body:Quantity>501</body:Quantity>
            |							<body:GrossMass>901</body:GrossMass>
            |							<body:NetMass>475</body:NetMass>
            |							<body:FiscalMark language="en">FM564790 Fiscal Mark</body:FiscalMark>
            |							<body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
            |							<body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
            |							<body:SizeOfProducer>20000</body:SizeOfProducer>
            |							<body:CommercialDescription language="en">Retsina</body:CommercialDescription>
            |							<body:BrandNameOfProducts language="en">BrandName</body:BrandNameOfProducts>
            |						</body:BodyEadEsad>
            |						<body:BodyEadEsad>
            |							<body:BodyRecordUniqueReference>2</body:BodyRecordUniqueReference>
            |							<body:ExciseProductCode>W300</body:ExciseProductCode>
            |							<body:CnCode>22041019</body:CnCode>
            |							<body:Quantity>501</body:Quantity>
            |							<body:GrossMass>901</body:GrossMass>
            |							<body:NetMass>475</body:NetMass>
            |							<body:FiscalMark language="en">FM564790 Fiscal Mark</body:FiscalMark>
            |							<body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
            |							<body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
            |							<body:SizeOfProducer>20000</body:SizeOfProducer>
            |							<body:CommercialDescription language="en">Retsina</body:CommercialDescription>
            |							<body:BrandNameOfProducts language="en">BrandName</body:BrandNameOfProducts>
            |						</body:BodyEadEsad>
            |					</body:EADESADContainer>
            |				</body:Body>
            |			</body:IE801>
            |		</mov:currentMovement>
            |	</mov:movementView>
            |</MovementDataResponse>""".stripMargin)

        GetMovementResponse.xmlReader.read(noJourneyTimeXML) shouldBe ParseFailure(JourneyTimeParseFailure("Could not parse JourneyTime, received: ''"))
      }
    }
  }
}
