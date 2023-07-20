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

import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.emcstfe.models.common._
import uk.gov.hmrc.emcstfe.models.createMovement._

import scala.xml.Elem

trait CreateMovementFixtures extends BaseFixtures with ChRISResponsesFixture {

  object AttributesFixtures {
    lazy val attributesModelMax: AttributesModel = AttributesModel(
      submissionMessageType = SubmissionMessageType.DutyPaidB2B,
      deferredSubmissionFlag = Some(Flag.True)
    )
    lazy val attributesModelMin: AttributesModel = AttributesModel(
      submissionMessageType = SubmissionMessageType.Standard,
      deferredSubmissionFlag = None
    )
    lazy val attributesXmlMax: Elem = <urn:Attributes>
      <urn:SubmissionMessageType>3</urn:SubmissionMessageType>
      <urn:DeferredSubmissionFlag>1</urn:DeferredSubmissionFlag>
    </urn:Attributes>
    lazy val attributesXmlMin: Elem = <urn:Attributes>
      <urn:SubmissionMessageType>1</urn:SubmissionMessageType>
    </urn:Attributes>
    lazy val attributesJsonMax: JsObject = Json.obj(
      "submissionMessageType" -> "3",
      "deferredSubmissionFlag" -> "1"
    )
    lazy val attributesJsonMin: JsObject = Json.obj(
      "submissionMessageType" -> "1"
    )
  }

  object ConsigneeTraderFixtures {
    lazy val consigneeTraderModel: TraderModel = TraderModel(
      referenceOfTaxWarehouse = None,
      vatNumber = None,
      traderExciseNumber = None,
      traderId = Some("GB000001"),
      traderName = Some("name"),
      address = Some(AddressModel(
        streetNumber = Some("number"),
        street = Some("street"),
        postcode = Some("postcode"),
        city = Some("city")
      )),
      eoriNumber = Some("eori")
    )
    lazy val consigneeTraderXml: Elem = <urn:ConsigneeTrader language="en">
      <urn:Traderid>GB000001</urn:Traderid>
      <urn:TraderName>name</urn:TraderName>
      <urn:StreetName>street</urn:StreetName>
      <urn:StreetNumber>number</urn:StreetNumber>
      <urn:Postcode>postcode</urn:Postcode>
      <urn:City>city</urn:City>
      <urn:EoriNumber>eori</urn:EoriNumber>
    </urn:ConsigneeTrader>
    lazy val consigneeTraderJson: JsObject = Json.obj(
      "traderId" -> "GB000001",
      "traderName" -> "name",
      "address" -> Json.obj(
        "streetNumber" -> "number",
        "street" -> "street",
        "postcode" -> "postcode",
        "city" -> "city"
      ),
      "eoriNumber" -> "eori"
    )
  }

  object ConsignorTraderFixtures {
    lazy val consignorTraderModel: TraderModel = TraderModel(
      referenceOfTaxWarehouse = None,
      vatNumber = None,
      traderExciseNumber = Some("GB000001"),
      traderId = None,
      traderName = Some("name"),
      address = Some(AddressModel(
        streetNumber = Some("number"),
        street = Some("street"),
        postcode = Some("postcode"),
        city = Some("city")
      )),
      eoriNumber = None
    )
    lazy val consignorTraderXml: Elem = <urn:ConsignorTrader language="en">
      <urn:TraderExciseNumber>GB000001</urn:TraderExciseNumber>
      <urn:TraderName>name</urn:TraderName>
      <urn:StreetName>street</urn:StreetName>
      <urn:StreetNumber>number</urn:StreetNumber>
      <urn:Postcode>postcode</urn:Postcode>
      <urn:City>city</urn:City>
    </urn:ConsignorTrader>
    lazy val consignorTraderJson: JsObject = Json.obj(
      "traderExciseNumber" -> "GB000001",
      "traderName" -> "name",
      "address" -> Json.obj(
        "streetNumber" -> "number",
        "street" -> "street",
        "postcode" -> "postcode",
        "city" -> "city"
      )
    )
  }

  object PlaceOfDispatchTraderFixtures {
    lazy val placeOfDispatchTraderModel: TraderModel = TraderModel(
      referenceOfTaxWarehouse = Some("GB000001"),
      vatNumber = None,
      traderExciseNumber = None,
      traderId = None,
      traderName = Some("name"),
      address = Some(AddressModel(
        streetNumber = Some("number"),
        street = Some("street"),
        postcode = Some("postcode"),
        city = Some("city")
      )),
      eoriNumber = None
    )
    lazy val placeOfDispatchTraderXml: Elem = <urn:PlaceOfDispatchTrader language="en">
      <urn:ReferenceOfTaxWarehouse>GB000001</urn:ReferenceOfTaxWarehouse>
      <urn:TraderName>name</urn:TraderName>
      <urn:StreetName>street</urn:StreetName>
      <urn:StreetNumber>number</urn:StreetNumber>
      <urn:Postcode>postcode</urn:Postcode>
      <urn:City>city</urn:City>
    </urn:PlaceOfDispatchTrader>
    lazy val placeOfDispatchTraderJson: JsObject = Json.obj(
      "referenceOfTaxWarehouse" -> "GB000001",
      "traderName" -> "name",
      "address" -> Json.obj(
        "streetNumber" -> "number",
        "street" -> "street",
        "postcode" -> "postcode",
        "city" -> "city"
      )
    )
  }

  object OfficeFixtures {
    lazy val officeModel: OfficeModel = OfficeModel(
      referenceNumber = "number"
    )
    lazy val officeXml: Elem = <urn:ReferenceNumber>number</urn:ReferenceNumber>
    lazy val officeJson: JsObject = Json.obj(
      "referenceNumber" -> "number"
    )
  }

  object ComplementConsigneeTraderFixtures {
    lazy val complementConsigneeTraderModelMax: ComplementConsigneeTraderModel = ComplementConsigneeTraderModel(
      memberStateCode = "code",
      serialNumberOfCertificateOfExemption = Some("number")
    )
    lazy val complementConsigneeTraderModelMin: ComplementConsigneeTraderModel = ComplementConsigneeTraderModel(
      memberStateCode = "code",
      serialNumberOfCertificateOfExemption = None
    )
    lazy val complementConsigneeTraderXmlMax: Elem = <urn:ComplementConsigneeTrader>
      <urn:MemberStateCode>code</urn:MemberStateCode>
      <urn:SerialNumberOfCertificateOfExemption>number</urn:SerialNumberOfCertificateOfExemption>
    </urn:ComplementConsigneeTrader>
    lazy val complementConsigneeTraderXmlMin: Elem = <urn:ComplementConsigneeTrader>
      <urn:MemberStateCode>code</urn:MemberStateCode>
    </urn:ComplementConsigneeTrader>
    lazy val complementConsigneeTraderJsonMax: JsObject = Json.obj(
      "memberStateCode" -> "code",
      "serialNumberOfCertificateOfExemption" -> "number"
    )
    lazy val complementConsigneeTraderJsonMin: JsObject = Json.obj(
      "memberStateCode" -> "code"
    )
  }

  object DeliveryPlaceTraderFixtures {
    lazy val deliveryPlaceTraderModel: TraderModel = TraderModel(
      referenceOfTaxWarehouse = None,
      vatNumber = None,
      traderExciseNumber = None,
      traderId = Some("GB000001"),
      traderName = Some("name"),
      address = Some(AddressModel(
        streetNumber = Some("number"),
        street = Some("street"),
        postcode = Some("postcode"),
        city = Some("city")
      )),
      eoriNumber = None
    )
    lazy val deliveryPlaceTraderXml: Elem = <urn:DeliveryPlaceTrader language="en">
      <urn:Traderid>GB000001</urn:Traderid>
      <urn:TraderName>name</urn:TraderName>
      <urn:StreetName>street</urn:StreetName>
      <urn:StreetNumber>number</urn:StreetNumber>
      <urn:Postcode>postcode</urn:Postcode>
      <urn:City>city</urn:City>
    </urn:DeliveryPlaceTrader>
    lazy val deliveryPlaceTraderJson: JsObject = Json.obj(
      "traderId" -> "GB000001",
      "traderName" -> "name",
      "address" -> Json.obj(
        "streetNumber" -> "number",
        "street" -> "street",
        "postcode" -> "postcode",
        "city" -> "city"
      )
    )
  }

  object TransportArrangerTraderFixtures {
    lazy val transportArrangerTraderModel: TraderModel = TraderModel(
      referenceOfTaxWarehouse = None,
      vatNumber = Some("vat"),
      traderExciseNumber = None,
      traderId = None,
      traderName = Some("name"),
      address = Some(AddressModel(
        streetNumber = Some("number"),
        street = Some("street"),
        postcode = Some("postcode"),
        city = Some("city")
      )),
      eoriNumber = None
    )
    lazy val transportArrangerTraderXml: Elem = <urn:TransportArrangerTrader language="en">
      <urn:VatNumber>vat</urn:VatNumber>
      <urn:TraderName>name</urn:TraderName>
      <urn:StreetName>street</urn:StreetName>
      <urn:StreetNumber>number</urn:StreetNumber>
      <urn:Postcode>postcode</urn:Postcode>
      <urn:City>city</urn:City>
    </urn:TransportArrangerTrader>
    lazy val transportArrangerTraderJson: JsObject = Json.obj(
      "vatNumber" -> "vat",
      "traderName" -> "name",
      "address" -> Json.obj(
        "streetNumber" -> "number",
        "street" -> "street",
        "postcode" -> "postcode",
        "city" -> "city"
      )
    )
  }

  object FirstTransporterTraderFixtures {
    lazy val firstTransporterTraderModel: TraderModel = TraderModel(
      referenceOfTaxWarehouse = None,
      vatNumber = Some("vat"),
      traderExciseNumber = None,
      traderId = None,
      traderName = Some("name"),
      address = Some(AddressModel(
        streetNumber = Some("number"),
        street = Some("street"),
        postcode = Some("postcode"),
        city = Some("city")
      )),
      eoriNumber = None
    )
    lazy val firstTransporterTraderXml: Elem = <urn:FirstTransporterTrader language="en">
      <urn:VatNumber>vat</urn:VatNumber>
      <urn:TraderName>name</urn:TraderName>
      <urn:StreetName>street</urn:StreetName>
      <urn:StreetNumber>number</urn:StreetNumber>
      <urn:Postcode>postcode</urn:Postcode>
      <urn:City>city</urn:City>
    </urn:FirstTransporterTrader>
    lazy val firstTransporterTraderJson: JsObject = Json.obj(
      "vatNumber" -> "vat",
      "traderName" -> "name",
      "address" -> Json.obj(
        "streetNumber" -> "number",
        "street" -> "street",
        "postcode" -> "postcode",
        "city" -> "city"
      )
    )
  }

  object DocumentCertificateFixtures {
    lazy val documentCertificateModelMax: DocumentCertificateModel = DocumentCertificateModel(
      documentType = Some("type"),
      documentReference = Some("document reference"),
      documentDescription = Some("description"),
      referenceOfDocument = Some("reference of document"),
    )
    lazy val documentCertificateModelMin: DocumentCertificateModel = DocumentCertificateModel(
      documentType = None,
      documentReference = None,
      documentDescription = None,
      referenceOfDocument = None
    )
    lazy val documentCertificateXmlMax: Elem = <urn:DocumentCertificate>
      <urn:DocumentType>type</urn:DocumentType>
      <urn:DocumentReference>document reference</urn:DocumentReference>
      <urn:DocumentDescription language="en">description</urn:DocumentDescription>
      <urn:ReferenceOfDocument language="en">reference of document</urn:ReferenceOfDocument>
    </urn:DocumentCertificate>
    lazy val documentCertificateXmlMin: Elem = <urn:DocumentCertificate></urn:DocumentCertificate>
    lazy val documentCertificateJsonMax: JsObject = Json.obj(
      "documentType" -> "type",
      "documentReference" -> "document reference",
      "documentDescription" -> "description",
      "referenceOfDocument" -> "reference of document"
    )
    lazy val documentCertificateJsonMin: JsObject = Json.obj()
  }

  object HeaderEadEsadFixtures {
    lazy val headerEadEsadModel: HeaderEadEsadModel = HeaderEadEsadModel(
      destinationTypeCode = "123",
      journeyTime = JourneyTime.Hours("3"),
      transportArrangement = TransportArrangement.OwnerOfGoods
    )
    lazy val headerEadEsadXml: Elem = <urn:HeaderEadEsad>
      <urn:DestinationTypeCode>123</urn:DestinationTypeCode>
      <urn:JourneyTime>H3</urn:JourneyTime>
      <urn:TransportArrangement>3</urn:TransportArrangement>
    </urn:HeaderEadEsad>
    lazy val headerEadEsadJson: JsObject = Json.obj(
      "destinationTypeCode" -> "123",
      "journeyTime" -> "3 hours",
      "transportArrangement" -> "3"
    )
  }

  object TransportModeFixtures {
    lazy val transportModeModelMax: TransportModeModel = TransportModeModel(
      transportModeCode = "code",
      complementaryInformation = Some("info")
    )
    lazy val transportModeModelMin: TransportModeModel = TransportModeModel(
      transportModeCode = "code",
      complementaryInformation = None
    )
    lazy val transportModeXmlMax: Elem = <urn:TransportMode>
      <urn:TransportModeCode>code</urn:TransportModeCode>
      <urn:ComplementaryInformation language="en">info</urn:ComplementaryInformation>
    </urn:TransportMode>
    lazy val transportModeXmlMin: Elem = <urn:TransportMode>
      <urn:TransportModeCode>code</urn:TransportModeCode>
    </urn:TransportMode>
    lazy val transportModeJsonMax: JsObject = Json.obj(
      "transportModeCode" -> "code",
      "complementaryInformation" -> "info"
    )
    lazy val transportModeJsonMin: JsObject = Json.obj(
      "transportModeCode" -> "code"
    )
  }

  object MovementGuaranteeFixtures {
    lazy val movementGuaranteeModel: MovementGuaranteeModel = MovementGuaranteeModel(
      guarantorTypeCode = "code",
      guarantorTrader = Some(Seq(
        GuarantorTraderModel(
          traderExciseNumber = Some("number"),
          traderName = Some("name"),
          address = Some(GuarantorAddressModel(
            streetNumber = Some("number"),
            street = Some("street"),
            postcode = Some("postcode"),
            city = Some("city"))
          ),
          vatNumber = Some("vat")
        ),
        GuarantorTraderModel(
          traderExciseNumber = Some("number 2"),
          traderName = Some("name 2"),
          address = Some(GuarantorAddressModel(
            streetNumber = Some("number 2"),
            street = Some("street 2"),
            postcode = Some("postcode 2"),
            city = Some("city 2"))
          ),
          vatNumber = Some("vat 2")
        )
      ))
    )
    lazy val movementGuaranteeXml: Elem = <urn:MovementGuarantee>
      <urn:GuarantorTypeCode>code</urn:GuarantorTypeCode>
      <urn:GuarantorTrader language="en">
        <urn:TraderExciseNumber>number</urn:TraderExciseNumber>
        <urn:TraderName>name</urn:TraderName>
        <urn:StreetName>street</urn:StreetName>
        <urn:StreetNumber>number</urn:StreetNumber>
        <urn:City>city</urn:City>
        <urn:Postcode>postcode</urn:Postcode>
        <urn:VatNumber>vat</urn:VatNumber>
      </urn:GuarantorTrader>
      <urn:GuarantorTrader language="en">
        <urn:TraderExciseNumber>number 2</urn:TraderExciseNumber>
        <urn:TraderName>name 2</urn:TraderName>
        <urn:StreetName>street 2</urn:StreetName>
        <urn:StreetNumber>number 2</urn:StreetNumber>
        <urn:City>city 2</urn:City>
        <urn:Postcode>postcode 2</urn:Postcode>
        <urn:VatNumber>vat 2</urn:VatNumber>
      </urn:GuarantorTrader>
    </urn:MovementGuarantee>
    lazy val movementGuaranteeJson: JsObject = Json.obj(
      "guarantorTypeCode" -> "code",
      "guarantorTrader" -> Json.arr(
        Json.obj(
          "traderExciseNumber" -> "number",
          "traderName" -> "name",
          "address" -> Json.obj(
            "streetNumber" -> "number",
            "street" -> "street",
            "postcode" -> "postcode",
            "city" -> "city"
          ),
          "vatNumber" -> "vat"
        ),
        Json.obj(
          "traderExciseNumber" -> "number 2",
          "traderName" -> "name 2",
          "address" -> Json.obj(
            "streetNumber" -> "number 2",
            "street" -> "street 2",
            "postcode" -> "postcode 2",
            "city" -> "city 2"
          ),
          "vatNumber" -> "vat 2"
        )
      )
    )
  }

  object PackageFixtures {
    lazy val packageModelMax: PackageModel = PackageModel(
      kindOfPackages = "kind",
      numberOfPackages = Some(1),
      shippingMarks = Some("marks"),
      commercialSealIdentification = Some("id"),
      sealInformation = Some("info")
    )
    lazy val packageModelMin: PackageModel = PackageModel(
      kindOfPackages = "kind",
      numberOfPackages = None,
      shippingMarks = None,
      commercialSealIdentification = None,
      sealInformation = None
    )
    lazy val packageXmlMax: Elem = <urn:Package>
      <urn:KindOfPackages>kind</urn:KindOfPackages>
      <urn:NumberOfPackages>1</urn:NumberOfPackages>
      <urn:ShippingMarks>marks</urn:ShippingMarks>
      <urn:CommercialSealIdentification>id</urn:CommercialSealIdentification>
      <urn:SealInformation language="en">info</urn:SealInformation>
    </urn:Package>
    lazy val packageXmlMin: Elem = <urn:Package>
      <urn:KindOfPackages>kind</urn:KindOfPackages>
    </urn:Package>
    lazy val packageJsonMax: JsObject = Json.obj(
      "kindOfPackages" -> "kind",
      "numberOfPackages" -> 1,
      "shippingMarks" -> "marks",
      "commercialSealIdentification" -> "id",
      "sealInformation" -> "info"
    )
    lazy val packageJsonMin: JsObject = Json.obj(
      "kindOfPackages" -> "kind"
    )
  }

  object WineProductFixtures {
    lazy val wineProductModelMax: WineProductModel = WineProductModel(
      wineProductCategory = "1",
      wineGrowingZoneCode = Some("zone"),
      thirdCountryOfOrigin = Some("country"),
      otherInformation = Some("info"),
      wineOperations = Some(Seq("op 1", "op 2"))
    )
    lazy val wineProductModelMin: WineProductModel = WineProductModel(
      wineProductCategory = "1",
      wineGrowingZoneCode = None,
      thirdCountryOfOrigin = None,
      otherInformation = None,
      wineOperations = None
    )
    lazy val wineProductXmlMax: Elem = <urn:WineProduct>
      <urn:WineProductCategory>1</urn:WineProductCategory>
      <urn:WineGrowingZoneCode>zone</urn:WineGrowingZoneCode>
      <urn:ThirdCountryOfOrigin>country</urn:ThirdCountryOfOrigin>
      <urn:OtherInformation language="en">info</urn:OtherInformation>
      <urn:WineOperation>op 1</urn:WineOperation>
      <urn:WineOperation>op 2</urn:WineOperation>
    </urn:WineProduct>
    lazy val wineProductXmlMin: Elem = <urn:WineProduct>
      <urn:WineProductCategory>1</urn:WineProductCategory>
    </urn:WineProduct>
    lazy val wineProductJsonMax: JsObject = Json.obj(
      "wineProductCategory" -> "1",
      "wineGrowingZoneCode" -> "zone",
      "thirdCountryOfOrigin" -> "country",
      "otherInformation" -> "info",
      "wineOperations" -> Json.arr("op 1", "op 2")
    )
    lazy val wineProductJsonMin: JsObject = Json.obj(
      "wineProductCategory" -> "1"
    )
  }

  object BodyEadEsadFixtures {
    import PackageFixtures._
    import WineProductFixtures._

    lazy val bodyEadEsadModelMax: BodyEadEsadModel = BodyEadEsadModel(
      bodyRecordUniqueReference = "unique ref",
      exciseProductCode = "epc",
      cnCode = "cn",
      quantity = 1.1,
      grossMass = 1.2,
      netMass = 1.3,
      alcoholicStrengthByVolumeInPercentage = Some(1.4),
      degreePlato = Some(1.5),
      fiscalMark = Some("mark"),
      fiscalMarkUsedFlag = Some(Flag.False),
      designationOfOrigin = Some("destination"),
      sizeOfProducer = Some(1),
      density = Some(1.6),
      commercialDescription = Some("description"),
      brandNameOfProducts = Some("name"),
      maturationPeriodOrAgeOfProducts = Some("age"),
      `package` = Seq(packageModelMax, packageModelMin),
      wineProduct = Some(wineProductModelMax)
    )
    lazy val bodyEadEsadModelMin: BodyEadEsadModel = BodyEadEsadModel(
      bodyRecordUniqueReference = "unique ref",
      exciseProductCode = "epc",
      cnCode = "cn",
      quantity = 1.1,
      grossMass = 1.2,
      netMass = 1.3,
      alcoholicStrengthByVolumeInPercentage = None,
      degreePlato = None,
      fiscalMark = None,
      fiscalMarkUsedFlag = None,
      designationOfOrigin = None,
      sizeOfProducer = None,
      density = None,
      commercialDescription = None,
      brandNameOfProducts = None,
      maturationPeriodOrAgeOfProducts = None,
      `package` = Seq(packageModelMin),
      wineProduct = None
    )
    lazy val bodyEadEsadXmlMax: Elem = <urn:BodyEadEsad>
      <BodyRecordUniqueReference>unique ref</BodyRecordUniqueReference>
      <ExciseProductCode>epc</ExciseProductCode>
      <CnCode>cn</CnCode>
      <Quantity>1.1</Quantity>
      <GrossMass>1.2</GrossMass>
      <NetMass>1.3</NetMass>
      <AlcoholicStrengthByVolumeInPercentage>1.4</AlcoholicStrengthByVolumeInPercentage>
      <DegreePlato>1.5</DegreePlato>
      <FiscalMark language="en">mark</FiscalMark>
      <FiscalMarkUsedFlag>0</FiscalMarkUsedFlag>
      <DesignationOfOrigin language="en">destination</DesignationOfOrigin>
      <SizeOfProducer>1</SizeOfProducer>
      <Density>1.6</Density>
      <CommercialDescription language="en">description</CommercialDescription>
      <BrandNameOfProducts language="en">name</BrandNameOfProducts>
      <MaturationPeriodOrAgeOfProducts language="en">age</MaturationPeriodOrAgeOfProducts>
      {packageXmlMax}
      {packageXmlMin}
      {wineProductXmlMax}
    </urn:BodyEadEsad>
    lazy val bodyEadEsadXmlMin: Elem = <urn:BodyEadEsad>
      <BodyRecordUniqueReference>unique ref</BodyRecordUniqueReference>
      <ExciseProductCode>epc</ExciseProductCode>
      <CnCode>cn</CnCode>
      <Quantity>1.1</Quantity>
      <GrossMass>1.2</GrossMass>
      <NetMass>1.3</NetMass>
      {packageXmlMin}
    </urn:BodyEadEsad>
    lazy val bodyEadEsadJsonMax: JsObject = Json.obj(
      "bodyRecordUniqueReference" -> "unique ref",
      "exciseProductCode" -> "epc",
      "cnCode" -> "cn",
      "quantity" -> 1.1,
      "grossMass" -> 1.2,
      "netMass" -> 1.3,
      "alcoholicStrengthByVolumeInPercentage" -> 1.4,
      "degreePlato" -> 1.5,
      "fiscalMark" -> "mark",
      "fiscalMarkUsedFlag" -> "0",
      "designationOfOrigin" -> "destination",
      "sizeOfProducer" -> 1,
      "density" -> 1.6,
      "commercialDescription" -> "description",
      "brandNameOfProducts" -> "name",
      "maturationPeriodOrAgeOfProducts" -> "age",
      "package" -> Json.arr(packageJsonMax, packageJsonMin),
      "wineProduct" -> wineProductJsonMax
    )
    lazy val bodyEadEsadJsonMin: JsObject = Json.obj(
      "bodyRecordUniqueReference" -> "unique ref",
      "exciseProductCode" -> "epc",
      "cnCode" -> "cn",
      "quantity" -> 1.1,
      "grossMass" -> 1.2,
      "netMass" -> 1.3,
      "package" -> Json.arr(packageJsonMin)
    )
  }

  object ImportSadFixtures {
    lazy val importSadModel: ImportSadModel = ImportSadModel(
      importSadNumber = "number"
    )
    lazy val importSadXml: Elem = <urn:ImportSad>
      <urn:ImportSadNumber>number</urn:ImportSadNumber>
    </urn:ImportSad>
    lazy val importSadJson: JsObject = Json.obj(
      "importSadNumber" -> "number"
    )
  }

  object EadEsadDraftFixtures {
    import ImportSadFixtures._
    lazy val eadEsadDraftModelMax: EadEsadDraftModel = EadEsadDraftModel(
      localReferenceNumber = "lrn",
      invoiceNumber = "number",
      invoiceDate = Some("inv date"),
      originTypeCode = "code",
      dateOfDispatch = "date",
      timeOfDispatch = Some("time"),
      importSad = Some(Seq(importSadModel, importSadModel))
    )
    lazy val eadEsadDraftModelMin: EadEsadDraftModel = EadEsadDraftModel(
      localReferenceNumber = "lrn",
      invoiceNumber = "number",
      invoiceDate = None,
      originTypeCode = "code",
      dateOfDispatch = "date",
      timeOfDispatch = None,
      importSad = None
    )
    lazy val eadEsadDraftXmlMax: Elem = <urn:EadEsadDraft>
      <urn:LocalReferenceNumber>lrn</urn:LocalReferenceNumber>
      <urn:InvoiceNumber>number</urn:InvoiceNumber>
      <urn:InvoiceDate>inv date</urn:InvoiceDate>
      <urn:OriginTypeCode>code</urn:OriginTypeCode>
      <urn:DateOfDispatch>date</urn:DateOfDispatch>
      <urn:TimeOfDispatch>time</urn:TimeOfDispatch>
      {importSadXml}
      {importSadXml}
    </urn:EadEsadDraft>
    lazy val eadEsadDraftXmlMin: Elem = <urn:EadEsadDraft>
      <urn:LocalReferenceNumber>lrn</urn:LocalReferenceNumber>
      <urn:InvoiceNumber>number</urn:InvoiceNumber>
      <urn:OriginTypeCode>code</urn:OriginTypeCode>
      <urn:DateOfDispatch>date</urn:DateOfDispatch>
    </urn:EadEsadDraft>
    lazy val eadEsadDraftJsonMax: JsObject = Json.obj(
      "localReferenceNumber" -> "lrn",
      "invoiceNumber" -> "number",
      "invoiceDate" -> "inv date",
      "originTypeCode" -> "code",
      "dateOfDispatch" -> "date",
      "timeOfDispatch" -> "time",
      "importSad" -> Json.arr(importSadJson, importSadJson)
    )
    lazy val eadEsadDraftJsonMin: JsObject = Json.obj(
      "localReferenceNumber" -> "lrn",
      "invoiceNumber" -> "number",
      "originTypeCode" -> "code",
      "dateOfDispatch" -> "date"
    )
  }

  object TransportDetailsFixtures {
    lazy val transportDetailsModel: TransportDetailsModel = TransportDetailsModel(
      transportUnitCode = "code",
      identityOfTransportUnits = Some("units"),
      commercialSealIdentification = Some("id"),
      complementaryInformation = Some("comp info"),
      sealInformation = Some("seal info")
    )
    lazy val transportDetailsXml: Elem = <urn:TransportDetails>
      <urn:TransportUnitCode>code</urn:TransportUnitCode>
      <urn:IdentityOfTransportUnits>units</urn:IdentityOfTransportUnits>
      <urn:CommercialSealIdentification>id</urn:CommercialSealIdentification>
      <urn:ComplementaryInformation language="en">comp info</urn:ComplementaryInformation>
      <urn:SealInformation language="en">seal info</urn:SealInformation>
    </urn:TransportDetails>
    lazy val transportDetailsJson: JsObject = Json.obj(
      "transportUnitCode" -> "code",
      "identityOfTransportUnits" -> "units",
      "commercialSealIdentification" -> "id",
      "complementaryInformation" -> "comp info",
      "sealInformation" -> "seal info"
    )
  }

  object CreateMovementFixtures {
    import AttributesFixtures._
    import BodyEadEsadFixtures._
    import ComplementConsigneeTraderFixtures._
    import ConsigneeTraderFixtures._
    import ConsignorTraderFixtures._
    import DeliveryPlaceTraderFixtures._
    import DocumentCertificateFixtures._
    import EadEsadDraftFixtures._
    import FirstTransporterTraderFixtures._
    import HeaderEadEsadFixtures._
    import MovementGuaranteeFixtures._
    import OfficeFixtures._
    import PlaceOfDispatchTraderFixtures._
    import TransportArrangerTraderFixtures._
    import TransportDetailsFixtures._
    import TransportModeFixtures._
    lazy val createMovementModelMax: CreateMovementModel = CreateMovementModel(
      attributes = attributesModelMax,
      consigneeTrader = Some(consigneeTraderModel),
      consignorTrader = consignorTraderModel,
      placeOfDispatchTrader = Some(placeOfDispatchTraderModel),
      dispatchImportOffice = Some(officeModel),
      complementConsigneeTrader = Some(complementConsigneeTraderModelMax),
      deliveryPlaceTrader = Some(deliveryPlaceTraderModel),
      deliveryPlaceCustomsOffice = Some(officeModel),
      competentAuthorityDispatchOffice = officeModel,
      transportArrangerTrader = Some(transportArrangerTraderModel),
      firstTransporterTrader = Some(firstTransporterTraderModel),
      documentCertificate = Some(Seq(documentCertificateModelMax, documentCertificateModelMin)),
      headerEadEsad = headerEadEsadModel,
      transportMode = transportModeModelMax,
      movementGuarantee = movementGuaranteeModel,
      bodyEadEsad = Seq(bodyEadEsadModelMax, bodyEadEsadModelMin),
      eadEsadDraft = eadEsadDraftModelMax,
      transportDetails = Seq(transportDetailsModel)
    )
    lazy val createMovementModelMin: CreateMovementModel = CreateMovementModel(
      attributes = attributesModelMin,
      consigneeTrader = None,
      consignorTrader = consignorTraderModel,
      placeOfDispatchTrader = None,
      dispatchImportOffice = None,
      complementConsigneeTrader = None,
      deliveryPlaceTrader = None,
      deliveryPlaceCustomsOffice = None,
      competentAuthorityDispatchOffice = officeModel,
      transportArrangerTrader = None,
      firstTransporterTrader = None,
      documentCertificate = None,
      headerEadEsad = headerEadEsadModel,
      transportMode = transportModeModelMin,
      movementGuarantee = movementGuaranteeModel,
      bodyEadEsad = Seq(bodyEadEsadModelMin),
      eadEsadDraft = eadEsadDraftModelMin,
      transportDetails = Seq(transportDetailsModel)
    )
    lazy val createMovementXmlMax: Elem = <urn:SubmittedDraftOfEADESAD>
      {attributesXmlMax}
      {consigneeTraderXml}
      {consignorTraderXml}
      {placeOfDispatchTraderXml}
      <urn:DispatchImportOffice>{officeXml}</urn:DispatchImportOffice>
      {complementConsigneeTraderXmlMax}
      {deliveryPlaceTraderXml}
      <urn:DeliveryPlaceCustomsOffice>{officeXml}</urn:DeliveryPlaceCustomsOffice>
      <urn:CompetentAuthorityDispatchOffice>{officeXml}</urn:CompetentAuthorityDispatchOffice>
      {transportArrangerTraderXml}
      {firstTransporterTraderXml}
      {documentCertificateXmlMax}
      {documentCertificateXmlMin}
      {headerEadEsadXml}
      {transportModeXmlMax}
      {movementGuaranteeXml}
      {bodyEadEsadXmlMax}
      {bodyEadEsadXmlMin}
      {eadEsadDraftXmlMax}
      {transportDetailsXml}
    </urn:SubmittedDraftOfEADESAD>
    lazy val createMovementXmlMin: Elem = <urn:SubmittedDraftOfEADESAD>
      {attributesXmlMin}
      {consignorTraderXml}
      <urn:CompetentAuthorityDispatchOffice>{officeXml}</urn:CompetentAuthorityDispatchOffice>
      {headerEadEsadXml}
      {transportModeXmlMin}
      {movementGuaranteeXml}
      {bodyEadEsadXmlMin}
      {eadEsadDraftXmlMin}
      {transportDetailsXml}
    </urn:SubmittedDraftOfEADESAD>
    lazy val createMovementJsonMax: JsObject = Json.obj(
      "attributes" -> attributesJsonMax,
      "consigneeTrader" -> consigneeTraderJson,
      "consignorTrader" -> consignorTraderJson,
      "placeOfDispatchTrader" -> placeOfDispatchTraderJson,
      "dispatchImportOffice" -> officeJson,
      "complementConsigneeTrader" -> complementConsigneeTraderJsonMax,
      "deliveryPlaceTrader" -> deliveryPlaceTraderJson,
      "deliveryPlaceCustomsOffice" -> officeJson,
      "competentAuthorityDispatchOffice" -> officeJson,
      "transportArrangerTrader" -> transportArrangerTraderJson,
      "firstTransporterTrader" -> firstTransporterTraderJson,
      "documentCertificate" -> Json.arr(documentCertificateJsonMax, documentCertificateJsonMin),
      "headerEadEsad" -> headerEadEsadJson,
      "transportMode" -> transportModeJsonMax,
      "movementGuarantee" -> movementGuaranteeJson,
      "bodyEadEsad" -> Json.arr(bodyEadEsadJsonMax, bodyEadEsadJsonMin),
      "eadEsadDraft" -> eadEsadDraftJsonMax,
      "transportDetails" -> Json.arr(transportDetailsJson)
    )
    lazy val createMovementJsonMin: JsObject = Json.obj(
      "attributes" -> attributesJsonMin,
      "consignorTrader" -> consignorTraderJson,
      "competentAuthorityDispatchOffice" -> officeJson,
      "headerEadEsad" -> headerEadEsadJson,
      "transportMode" -> transportModeJsonMin,
      "movementGuarantee" -> movementGuaranteeJson,
      "bodyEadEsad" -> Json.arr(bodyEadEsadJsonMin),
      "eadEsadDraft" -> eadEsadDraftJsonMin,
      "transportDetails" -> Json.arr(transportDetailsJson)
    )
  }

}
