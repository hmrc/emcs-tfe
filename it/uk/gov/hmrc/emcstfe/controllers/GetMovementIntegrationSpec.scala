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

package uk.gov.hmrc.emcstfe.controllers

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.{HeaderNames, Status}
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Injecting
import uk.gov.hmrc.emcstfe.fixtures.GetMovementFixture
import uk.gov.hmrc.emcstfe.models.mongo.GetMovementMongoResponse
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse._
import uk.gov.hmrc.emcstfe.repositories.GetMovementRepository
import uk.gov.hmrc.emcstfe.stubs.{AuthStub, DownstreamStub}
import uk.gov.hmrc.emcstfe.support.IntegrationBaseSpec

import scala.xml.XML

class GetMovementIntegrationSpec extends IntegrationBaseSpec with GetMovementFixture with Injecting {

  val repository: GetMovementRepository = inject[GetMovementRepository]

  override def beforeEach(): Unit = {
    await(repository.removeAll())
  }

  private trait Test {
    def setupStubs(): StubMapping

    def uri: String = s"/movement/$testErn/$testArc"

    def downstreamUri: String = s"/ChRISOSB/EMCS/EMCSApplicationService/2"

    def generateHeaders(action: String) = Map(HeaderNames.CONTENT_TYPE -> s"""application/soap+xml; charset=UTF-8; action="$action"""")

    def getMovementIfChangedHeaders: Map[String, String] = generateHeaders("http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/GetMovementIfChanged")
    def getMovementHeaders: Map[String, String] = generateHeaders("http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/GetMovement")

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri)
    }
  }

  "Calling the get movement endpoint" when {

    "user is unauthorised" must {
      "return Forbidden" in new Test {

        override def setupStubs(): StubMapping = {
          AuthStub.unauthorised()
          DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Status.OK, XML.loadString(getMovementSoapWrapper))
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.FORBIDDEN
      }
    }

    "user is unauthorised" must {

      "return forbidden" when {
        "the ERN requested does not match the ERN of the credential" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised("WrongERN")
          }

          val response: WSResponse = await(request().get())
          response.status shouldBe Status.FORBIDDEN
        }
      }
    }

    "user is authorized" must {

      "return a success" when {
        "no movement exists in mongo so GetMovement is called" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccessWithHeaders(DownstreamStub.POST, downstreamUri, getMovementHeaders, Status.OK, XML.loadString(getMovementSoapWrapper))
          }

          val response: WSResponse = await(request().get())
          response.status shouldBe Status.OK
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe getMovementJson
        }
        "a movement exists in mongo so GetMovementIfChanged is called, but the GetMovementIfChanged call returns no differences" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccessWithHeaders(DownstreamStub.POST, downstreamUri, getMovementIfChangedHeaders, Status.OK, XML.loadString(
              s"""<tns:Envelope
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
                 |				<con:Results/>
                 |			</con:OperationResponse>
                 |		</con:Control>
                 |	</tns:Body>
                 |</tns:Envelope>""".stripMargin))
          }

          await(repository.set(GetMovementMongoResponse(testInternalId, testErn, testArc, getMovementResponse)))

          val response: WSResponse = await(request().get())
          response.status shouldBe Status.OK
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe getMovementJson
        }
        "a movement exists in mongo so GetMovementIfChanged is called, and the GetMovementIfChanged call returns differences" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()

            // same as the one in the fixture except the LRN is different - enough to trigger a new "save to mongo"
            lazy val getMovementResponseBody: String =
              """<mov:movementView xsi:schemaLocation="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3 movementView.xsd" xmlns:mov="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3">
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
                |              <body:LocalReferenceNumber>localReferenceNumber</body:LocalReferenceNumber>
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
                |              <body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
                |              <body:SizeOfProducer>20000</body:SizeOfProducer>
                |              <body:CommercialDescription language="en">Retsina</body:CommercialDescription>
                |              <body:BrandNameOfProducts language="en">MALAMATINA</body:BrandNameOfProducts>
                |              <body:Package>
                |                <body:KindOfPackages>BO</body:KindOfPackages>
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
                |              <body:Traderid>GB11100000002</body:Traderid>
                |              <body:TraderName>Original 801 Consignee</body:TraderName>
                |              <body:StreetName>Evangelismus Avenue 253</body:StreetName>
                |              <body:Postcode>45690</body:Postcode>
                |              <body:City>Athens</body:City>
                |            </body:ConsigneeTrader>
                |            <body:ExciseMovement>
                |              <body:AdministrativeReferenceCode>13AB7778889991ABCDEF9</body:AdministrativeReferenceCode>
                |              <body:DateAndTimeOfValidationOfEadEsad>2008-09-04T10:22:50</body:DateAndTimeOfValidationOfEadEsad>
                |            </body:ExciseMovement>
                |            <body:ConsignorTrader language="en">
                |              <body:TraderExciseNumber>GB12345GTR144</body:TraderExciseNumber>
                |              <body:TraderName>Original 801 Consignor</body:TraderName>
                |              <body:StreetName>Montoyerstreet 101</body:StreetName>
                |              <body:Postcode>1000</body:Postcode>
                |              <body:City>Brussels</body:City>
                |            </body:ConsignorTrader>
                |            <body:PlaceOfDispatchTrader language="en">
                |              <body:ReferenceOfTaxWarehouse>GB12345GTR143</body:ReferenceOfTaxWarehouse>
                |              <body:TraderName>Original 801 DispatchTrader</body:TraderName>
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
                |              <body:TraderName>Original 801 FirstTransporter</body:TraderName>
                |              <body:StreetName>Kerkstraat 55</body:StreetName>
                |              <body:Postcode>9000</body:Postcode>
                |              <body:City>Gent</body:City>
                |            </body:FirstTransporterTrader>
                |            <body:DocumentCertificate>
                |              <body:DocumentDescription language="en">Test</body:DocumentDescription>
                |              <body:ReferenceOfDocument language="en">AB123</body:ReferenceOfDocument>
                |            </body:DocumentCertificate>
                |            <body:EadEsad>
                |              <body:LocalReferenceNumber>localReferenceNumber</body:LocalReferenceNumber>
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
                |          <head:TimeOfPreparation>14:20:0Z</head:TimeOfPreparation>
                |          <head:MessageIdentifier>token</head:MessageIdentifier>
                |          <head:CorrelationIdentifier>token</head:CorrelationIdentifier>
                |        </body:Header>
                |        <body:Body>
                |          <body:CancellationOfEAD>
                |            <body:Attributes>
                |              <body:DateAndTimeOfValidationOfCancellation>2008-09-04T10:22:53</body:DateAndTimeOfValidationOfCancellation>
                |            </body:Attributes>
                |            <body:ExciseMovement>
                |              <body:AdministrativeReferenceCode>13AB7778889991ABCDEF9</body:AdministrativeReferenceCode>
                |            </body:ExciseMovement>
                |            <body:Cancellation>
                |              <body:CancellationReasonCode>1</body:CancellationReasonCode>
                |            </body:Cancellation>
                |          </body:CancellationOfEAD>
                |        </body:Body>
                |      </body:IE810>
                |      <body:IE802 xmlns:body="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:IE802:V2.02">
                |        <body:Header xmlns:head="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:TMS:V2.02">
                |          <head:MessageSender>token</head:MessageSender>
                |          <head:MessageRecipient>token</head:MessageRecipient>
                |          <head:DateOfPreparation>1967-08-13</head:DateOfPreparation>
                |          <head:TimeOfPreparation>14:20:0Z</head:TimeOfPreparation>
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
                |              <body:SequenceNumber>1</body:SequenceNumber>
                |            </body:ExciseMovement>
                |          </body:ReminderMessageForExciseMovement>
                |        </body:Body>
                |      </body:IE802>
                |      <body:IE803 xmlns:body="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:IE803:V2.02">
                |        <body:Header xmlns:head="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:TMS:V2.02">
                |          <head:MessageSender>token</head:MessageSender>
                |          <head:MessageRecipient>token</head:MessageRecipient>
                |          <head:DateOfPreparation>1967-08-13</head:DateOfPreparation>
                |          <head:TimeOfPreparation>14:20:0Z</head:TimeOfPreparation>
                |          <head:MessageIdentifier>token</head:MessageIdentifier>
                |          <head:CorrelationIdentifier>token</head:CorrelationIdentifier>
                |        </body:Header>
                |        <body:Body>
                |          <body:NotificationOfDivertedEADESAD>
                |            <body:ExciseNotification>
                |              <body:NotificationType>1</body:NotificationType>
                |              <body:NotificationDateAndTime>2001-12-17T09:30:47.0Z</body:NotificationDateAndTime>
                |              <body:AdministrativeReferenceCode>13AB1234567891ABCDEF9</body:AdministrativeReferenceCode>
                |              <body:SequenceNumber>1</body:SequenceNumber>
                |            </body:ExciseNotification>
                |          </body:NotificationOfDivertedEADESAD>
                |        </body:Body>
                |      </body:IE803>
                |      <body:IE837 xmlns:body="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:IE837:V2.02">
                |        <body:Header xmlns:head="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:TMS:V2.02">
                |          <head:MessageSender>token</head:MessageSender>
                |          <head:MessageRecipient>token</head:MessageRecipient>
                |          <head:DateOfPreparation>1967-08-13</head:DateOfPreparation>
                |          <head:TimeOfPreparation>14:20:0Z</head:TimeOfPreparation>
                |          <head:MessageIdentifier>token</head:MessageIdentifier>
                |          <head:CorrelationIdentifier>token</head:CorrelationIdentifier>
                |        </body:Header>
                |        <body:Body>
                |          <body:ExplanationOnDelayForDelivery>
                |            <body:Attributes>
                |              <body:SubmitterIdentification>837Submitter</body:SubmitterIdentification>
                |              <body:SubmitterType>1</body:SubmitterType>
                |              <body:ExplanationCode>1</body:ExplanationCode>
                |              <body:ComplementaryInformation language="to">837 complementory info</body:ComplementaryInformation>
                |              <body:MessageRole>1</body:MessageRole>
                |              <body:DateAndTimeOfValidationOfExplanationOnDelay>2001-12-17T09:30:47.0Z</body:DateAndTimeOfValidationOfExplanationOnDelay>
                |            </body:Attributes>
                |            <body:ExciseMovement>
                |              <body:AdministrativeReferenceCode>13AB1234567891ABCDEF9</body:AdministrativeReferenceCode>
                |              <body:SequenceNumber/>
                |            </body:ExciseMovement>
                |          </body:ExplanationOnDelayForDelivery>
                |        </body:Body>
                |      </body:IE837>
                |    </mov:eventHistory>
                |  </mov:movementView>""".stripMargin

            lazy val getMovementSoapWrapper: String =
              s"""<tns:Envelope
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
                 |						<![CDATA[$getMovementResponseBody]]>
                 |					</con:Result>
                 |				</con:Results>
                 |			</con:OperationResponse>
                 |		</con:Control>
                 |	</tns:Body>
                 |</tns:Envelope>""".stripMargin

            DownstreamStub.onSuccessWithHeaders(DownstreamStub.POST, downstreamUri, getMovementIfChangedHeaders, Status.OK, XML.loadString(getMovementSoapWrapper))
          }

          await(repository.set(GetMovementMongoResponse(testInternalId, testErn, testArc, getMovementResponse.copy(localReferenceNumber = "localReferenceNumber"))))

          val response: WSResponse = await(request().get())
          response.status shouldBe Status.OK
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe  Json.obj(fields =
            "localReferenceNumber" -> "localReferenceNumber",
            "eadStatus" -> "Accepted",
            "consignorName" -> "Current 801 Consignor",
            "dateOfDispatch" -> "2008-11-20",
            "journeyTime" -> "20 days",
            "items" -> Json.arr(
              Json.obj(fields =
                "itemUniqueReference" -> 1,
                "productCode" -> "W200",
                "cnCode" -> "22041011",
                "quantity" -> 500,
                "grossMass" -> 900,
                "netMass" -> 375,
                "packaging" -> Json.arr(
                  Json.obj(fields =
                    "typeOfPackage" -> "BO",
                    "quantity" -> 125
                  )
                )
              ),
              Json.obj(fields =
                "itemUniqueReference" -> 2,
                "productCode" -> "W300",
                "cnCode" -> "27111901",
                "quantity" -> 501,
                "grossMass" -> 901,
                "netMass" -> 475,
                "alcoholicStrength" -> 12.7,
                "packaging" -> Json.arr(
                  Json.obj(fields =
                    "typeOfPackage" -> "BO",
                    "quantity" -> 125
                  ),
                  Json.obj(fields =
                    "typeOfPackage" -> "HG",
                    "quantity" -> 7
                  )
                )
              )
            ),
            "numberOfItems" -> 2
          )
        }
      }
      "return an error" when {
        "downstream call returns unexpected XML" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccessWithHeaders(DownstreamStub.POST, downstreamUri, getMovementHeaders, Status.OK, <Message>Success!</Message>)
          }

          val response: WSResponse = await(request().get())
          response.status shouldBe Status.INTERNAL_SERVER_ERROR
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe Json.toJson(SoapExtractionError)
        }
        "downstream call returns something other than XML" in new Test {
          val referenceDataResponseBody: JsValue = Json.obj("message" -> "Success!")

          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccessWithHeaders(DownstreamStub.POST, downstreamUri, getMovementHeaders, Status.OK, referenceDataResponseBody)
          }

          val response: WSResponse = await(request().get())
          response.status shouldBe Status.INTERNAL_SERVER_ERROR
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe Json.toJson(XmlValidationError)
        }
        "downstream call returns a non-200 HTTP response" in new Test {
          val referenceDataResponseBody: JsValue = Json.parse(
            s"""
               |{
               |   "message": "test message"
               |}
               |""".stripMargin
          )

          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccessWithHeaders(DownstreamStub.POST, downstreamUri, getMovementHeaders, Status.INTERNAL_SERVER_ERROR, referenceDataResponseBody)
          }

          val response: WSResponse = await(request().get())
          response.status shouldBe Status.INTERNAL_SERVER_ERROR
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe Json.toJson(UnexpectedDownstreamResponseError)
        }
      }
    }
  }
}
