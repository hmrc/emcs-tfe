/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.fixtures

import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.emcstfe.models.common.JourneyTime.Days
import uk.gov.hmrc.emcstfe.models.response.GetMessageResponse

trait GetMessageFixture {
  lazy val getMessageResponseBody: String = """<MovementDataResponse xsi:schemaLocation="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementData/3 MovementData.xsd"
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
                                              |					<body:EADContainer>
                                              |						<body:ConsigneeTrader language="en">
                                              |							<body:Traderid>GB11100000002</body:Traderid>
                                              |							<body:TraderName>Current 801 Consignee</body:TraderName>
                                              |							<body:StreetName>Aardvark Avenue 253</body:StreetName>
                                              |							<body:Postcode>SY1 3BQ</body:Postcode>
                                              |							<body:City>Shrewsbury</body:City>
                                              |						</body:ConsigneeTrader>
                                              |						<body:ExciseMovementEad>
                                              |							<body:AdministrativeReferenceCode>13AB7778889991ABCDEF9</body:AdministrativeReferenceCode>
                                              |							<body:DateAndTimeOfValidationOfEad>2008-09-04T10:22:50</body:DateAndTimeOfValidationOfEad>
                                              |						</body:ExciseMovementEad>
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
                                              |						<body:Ead>
                                              |							<body:LocalReferenceNumber>EN</body:LocalReferenceNumber>
                                              |							<body:InvoiceNumber>IN777888999</body:InvoiceNumber>
                                              |							<body:InvoiceDate>2008-09-04</body:InvoiceDate>
                                              |							<body:OriginTypeCode>1</body:OriginTypeCode>
                                              |							<body:DateOfDispatch>2008-11-20</body:DateOfDispatch>
                                              |							<body:TimeOfDispatch>10:00:00</body:TimeOfDispatch>
                                              |						</body:Ead>
                                              |						<body:HeaderEad>
                                              |							<body:SequenceNumber>1</body:SequenceNumber>
                                              |							<body:DateAndTimeOfUpdateValidation>2008-09-04T10:22:50</body:DateAndTimeOfUpdateValidation>
                                              |							<body:DestinationTypeCode>6</body:DestinationTypeCode>
                                              |							<body:JourneyTime>D20</body:JourneyTime>
                                              |							<body:TransportArrangement>1</body:TransportArrangement>
                                              |						</body:HeaderEad>
                                              |						<body:TransportMode>
                                              |							<body:TransportModeCode>1</body:TransportModeCode>
                                              |						</body:TransportMode>
                                              |						<body:MovementGuarantee>
                                              |							<body:GuarantorTypeCode>2</body:GuarantorTypeCode>
                                              |						</body:MovementGuarantee>
                                              |						<body:BodyEad>
                                              |							<body:BodyRecordUniqueReference>1</body:BodyRecordUniqueReference>
                                              |							<body:ExciseProductCode>W200</body:ExciseProductCode>
                                              |							<body:CnCode>22041011</body:CnCode>
                                              |							<body:Quantity>500</body:Quantity>
                                              |							<body:GrossWeight>900</body:GrossWeight>
                                              |							<body:NetWeight>375</body:NetWeight>
                                              |							<body:FiscalMark language="en">FM564789 Fiscal Mark</body:FiscalMark>
                                              |							<body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
                                              |							<body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
                                              |							<body:SizeOfProducer>20000</body:SizeOfProducer>
                                              |							<body:CommercialDescription language="en">Retsina</body:CommercialDescription>
                                              |							<body:BrandNameOfProducts language="en">MALAMATINA</body:BrandNameOfProducts>
                                              |							<body:Package>
                                              |								<body:KindOfPackages>BO</body:KindOfPackages>
                                              |								<body:NumberOfPackages>125</body:NumberOfPackages>
                                              |								<body:CommercialSealIdentification>SEAL456789321</body:CommercialSealIdentification>
                                              |								<body:SealInformation language="en">Red Strip</body:SealInformation>
                                              |							</body:Package>
                                              |							<body:WineProduct>
                                              |								<body:WineProductCategory>4</body:WineProductCategory>
                                              |								<body:ThirdCountryOfOrigin>FJ</body:ThirdCountryOfOrigin>
                                              |								<body:OtherInformation language="en">Not available</body:OtherInformation>
                                              |								<body:WineOperation>
                                              |									<body:WineOperationCode>4</body:WineOperationCode>
                                              |								</body:WineOperation>
                                              |								<body:WineOperation>
                                              |									<body:WineOperationCode>5</body:WineOperationCode>
                                              |								</body:WineOperation>
                                              |							</body:WineProduct>
                                              |						</body:BodyEad>
                                              |						<body:BodyEad>
                                              |							<body:BodyRecordUniqueReference>2</body:BodyRecordUniqueReference>
                                              |							<body:ExciseProductCode>W300</body:ExciseProductCode>
                                              |							<body:CnCode>22041019</body:CnCode>
                                              |							<body:Quantity>501</body:Quantity>
                                              |							<body:GrossWeight>901</body:GrossWeight>
                                              |							<body:NetWeight>475</body:NetWeight>
                                              |							<body:FiscalMark language="en">FM564790 Fiscal Mark</body:FiscalMark>
                                              |							<body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
                                              |							<body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
                                              |							<body:SizeOfProducer>20000</body:SizeOfProducer>
                                              |							<body:CommercialDescription language="en">Retsina</body:CommercialDescription>
                                              |							<body:BrandNameOfProducts language="en">BrandName</body:BrandNameOfProducts>
                                              |							<body:Package>
                                              |								<body:KindOfPackages>BO</body:KindOfPackages>
                                              |								<body:NumberOfPackages>125</body:NumberOfPackages>
                                              |								<body:CommercialSealIdentification>SEAL456789321</body:CommercialSealIdentification>
                                              |								<body:SealInformation language="en">Red Strip</body:SealInformation>
                                              |							</body:Package>
                                              |							<body:Package>
                                              |								<body:KindOfPackages>HG</body:KindOfPackages>
                                              |								<body:NumberOfPackages>7</body:NumberOfPackages>
                                              |								<body:CommercialSealIdentification>SEAL77</body:CommercialSealIdentification>
                                              |								<body:SealInformation language="en">Cork</body:SealInformation>
                                              |							</body:Package>
                                              |							<body:WineProduct>
                                              |								<body:WineProductCategory>3</body:WineProductCategory>
                                              |								<body:ThirdCountryOfOrigin>FJ</body:ThirdCountryOfOrigin>
                                              |								<body:OtherInformation language="en">Not available</body:OtherInformation>
                                              |								<body:WineOperation>
                                              |									<body:WineOperationCode>0</body:WineOperationCode>
                                              |								</body:WineOperation>
                                              |								<body:WineOperation>
                                              |									<body:WineOperationCode>1</body:WineOperationCode>
                                              |								</body:WineOperation>
                                              |							</body:WineProduct>
                                              |						</body:BodyEad>
                                              |						<body:TransportDetails>
                                              |							<body:TransportUnitCode>1</body:TransportUnitCode>
                                              |							<body:IdentityOfTransportUnits>Bottles</body:IdentityOfTransportUnits>
                                              |							<body:CommercialSealIdentification>SID13245678</body:CommercialSealIdentification>
                                              |							<body:ComplementaryInformation language="en">Bottles of Restina</body:ComplementaryInformation>
                                              |							<body:SealInformation language="en">Sealed with red strip</body:SealInformation>
                                              |						</body:TransportDetails>
                                              |						<body:TransportDetails>
                                              |							<body:TransportUnitCode>2</body:TransportUnitCode>
                                              |							<body:IdentityOfTransportUnits>Cans</body:IdentityOfTransportUnits>
                                              |							<body:CommercialSealIdentification>SID132987</body:CommercialSealIdentification>
                                              |							<body:ComplementaryInformation language="en">Cans</body:ComplementaryInformation>
                                              |							<body:SealInformation language="en">Seal info</body:SealInformation>
                                              |						</body:TransportDetails>
                                              |					</body:EADContainer>
                                              |				</body:Body>
                                              |			</body:IE801>
                                              |		</mov:currentMovement>
                                              |		<mov:eventHistory>
                                              |			<body:IE810
                                              |				xmlns:body="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:IE810:V2.02">
                                              |				<body:Header
                                              |					xmlns:head="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:TMS:V2.02">
                                              |					<head:MessageSender>token</head:MessageSender>
                                              |					<head:MessageRecipient>token</head:MessageRecipient>
                                              |					<head:DateOfPreparation>1967-08-13</head:DateOfPreparation>
                                              |					<head:TimeOfPreparation>14:20:0Z</head:TimeOfPreparation>
                                              |					<head:MessageIdentifier>token</head:MessageIdentifier>
                                              |					<head:CorrelationIdentifier>token</head:CorrelationIdentifier>
                                              |				</body:Header>
                                              |				<body:Body>
                                              |					<body:CancellationOfEAD>
                                              |						<body:Attributes>
                                              |							<body:DateAndTimeOfValidationOfCancellation>2008-09-04T10:22:53</body:DateAndTimeOfValidationOfCancellation>
                                              |						</body:Attributes>
                                              |						<body:ExciseMovementEad>
                                              |							<body:AdministrativeReferenceCode>13AB7778889991ABCDEF9</body:AdministrativeReferenceCode>
                                              |						</body:ExciseMovementEad>
                                              |						<body:Cancellation>
                                              |							<body:CancellationReasonCode>1</body:CancellationReasonCode>
                                              |						</body:Cancellation>
                                              |					</body:CancellationOfEAD>
                                              |				</body:Body>
                                              |			</body:IE810>
                                              |			<body:IE802
                                              |				xmlns:body="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:IE802:V2.02">
                                              |				<body:Header
                                              |					xmlns:head="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:TMS:V2.02">
                                              |					<head:MessageSender>token</head:MessageSender>
                                              |					<head:MessageRecipient>token</head:MessageRecipient>
                                              |					<head:DateOfPreparation>1967-08-13</head:DateOfPreparation>
                                              |					<head:TimeOfPreparation>14:20:0Z</head:TimeOfPreparation>
                                              |					<head:MessageIdentifier>token</head:MessageIdentifier>
                                              |					<head:CorrelationIdentifier>token</head:CorrelationIdentifier>
                                              |				</body:Header>
                                              |				<body:Body>
                                              |					<body:ReminderMessageForExciseMovement>
                                              |						<body:Attributes>
                                              |							<body:DateAndTimeOfIssuanceOfReminder>2008-09-04T10:22:53</body:DateAndTimeOfIssuanceOfReminder>
                                              |							<body:ReminderInformation language="en">To be completed by this date</body:ReminderInformation>
                                              |							<body:LimitDateAndTime>2008-09-04T10:22:53</body:LimitDateAndTime>
                                              |							<body:ReminderMessageType>1</body:ReminderMessageType>
                                              |						</body:Attributes>
                                              |						<body:ExciseMovementEad>
                                              |							<body:AdministrativeReferenceCode>13AB7778889991ABCDEF9</body:AdministrativeReferenceCode>
                                              |							<body:SequenceNumber>1</body:SequenceNumber>
                                              |						</body:ExciseMovementEad>
                                              |					</body:ReminderMessageForExciseMovement>
                                              |				</body:Body>
                                              |			</body:IE802>
                                              |			<body:IE803
                                              |				xmlns:body="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:IE803:V2.02">
                                              |				<body:Header
                                              |					xmlns:head="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:TMS:V2.02">
                                              |					<head:MessageSender>token</head:MessageSender>
                                              |					<head:MessageRecipient>token</head:MessageRecipient>
                                              |					<head:DateOfPreparation>1967-08-13</head:DateOfPreparation>
                                              |					<head:TimeOfPreparation>14:20:0Z</head:TimeOfPreparation>
                                              |					<head:MessageIdentifier>token</head:MessageIdentifier>
                                              |					<head:CorrelationIdentifier>token</head:CorrelationIdentifier>
                                              |				</body:Header>
                                              |				<body:Body>
                                              |					<body:NotificationOfDivertedEAD>
                                              |						<body:ExciseNotification>
                                              |							<body:NotificationType>1</body:NotificationType>
                                              |							<body:NotificationDateAndTime>2001-12-17T09:30:47.0Z</body:NotificationDateAndTime>
                                              |							<body:AdministrativeReferenceCode>13AB1234567891ABCDEF9</body:AdministrativeReferenceCode>
                                              |							<body:SequenceNumber>1</body:SequenceNumber>
                                              |						</body:ExciseNotification>
                                              |					</body:NotificationOfDivertedEAD>
                                              |				</body:Body>
                                              |			</body:IE803>
                                              |			<body:IE837
                                              |				xmlns:body="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:IE837:V2.02">
                                              |				<body:Header
                                              |					xmlns:head="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:TMS:V2.02">
                                              |					<head:MessageSender>token</head:MessageSender>
                                              |					<head:MessageRecipient>token</head:MessageRecipient>
                                              |					<head:DateOfPreparation>1967-08-13</head:DateOfPreparation>
                                              |					<head:TimeOfPreparation>14:20:0Z</head:TimeOfPreparation>
                                              |					<head:MessageIdentifier>token</head:MessageIdentifier>
                                              |					<head:CorrelationIdentifier>token</head:CorrelationIdentifier>
                                              |				</body:Header>
                                              |				<body:Body>
                                              |					<body:ExplanationOnDelayForDelivery>
                                              |						<body:Attributes>
                                              |							<body:SubmitterIdentification>837Submitter</body:SubmitterIdentification>
                                              |							<body:SubmitterType>1</body:SubmitterType>
                                              |							<body:ExplanationCode>1</body:ExplanationCode>
                                              |							<body:ComplementaryInformation language="to">837 complementory info</body:ComplementaryInformation>
                                              |							<body:MessageRole>1</body:MessageRole>
                                              |							<body:DateAndTimeOfValidationOfExplanationOnDelay>2001-12-17T09:30:47.0Z</body:DateAndTimeOfValidationOfExplanationOnDelay>
                                              |						</body:Attributes>
                                              |						<body:ExciseMovementEad>
                                              |							<body:AdministrativeReferenceCode>13AB1234567891ABCDEF9</body:AdministrativeReferenceCode>
                                              |							<body:SequenceNumber/>
                                              |						</body:ExciseMovementEad>
                                              |					</body:ExplanationOnDelayForDelivery>
                                              |				</body:Body>
                                              |			</body:IE837>
                                              |		</mov:eventHistory>
                                              |	</mov:movementView>
                                              |</MovementDataResponse>""".stripMargin

  lazy val getMessageSoapWrapper: String = s"""<tns:Envelope
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
                                              |						<![CDATA[$getMessageResponseBody]]>
                                              |					</con:Result>
                                              |				</con:Results>
                                              |			</con:OperationResponse>
                                              |		</con:Control>
                                              |	</tns:Body>
                                              |</tns:Envelope>""".stripMargin
  
  lazy val model: GetMessageResponse = GetMessageResponse(
    localReferenceNumber = "EN", eadStatus = "Accepted", consignorName = "Current 801 Consignor", dateOfDispatch = "2008-11-20", journeyTime = Days("20"), numberOfItems = 2
  )

  lazy val jsonResponse: JsValue = Json.parse(
    """{
      |  "localReferenceNumber": "EN",
      |  "eadStatus": "Accepted",
      |  "consignorName": "Current 801 Consignor",
      |  "dateOfDispatch": "2008-11-20",
      |  "journeyTime": "20 days",
      |  "numberOfItems": 2
      |}""".stripMargin)

}
