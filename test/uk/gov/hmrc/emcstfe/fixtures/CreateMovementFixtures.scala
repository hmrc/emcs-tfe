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
import uk.gov.hmrc.emcstfe.models.common.DestinationType.TaxWarehouse
import uk.gov.hmrc.emcstfe.models.common.MovementType.UKtoUK
import uk.gov.hmrc.emcstfe.models.common._
import uk.gov.hmrc.emcstfe.models.createMovement._

import scala.xml.Elem

trait CreateMovementFixtures extends BaseFixtures
  with TraderModelFixtures
  with MovementGuaranteeFixtures
  with TransportDetailsFixtures
  with EISResponsesFixture {

  object CaMAttributesFixtures {
    lazy val attributesModelMax: AttributesModel = AttributesModel(
      submissionMessageType = SubmissionMessageType.DutyPaidB2B,
      deferredSubmissionFlag = Some(true)
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
      "deferredSubmissionFlag" -> true
    )
    lazy val attributesJsonMin: JsObject = Json.obj(
      "submissionMessageType" -> "1"
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
      destinationType = TaxWarehouse,
      journeyTime = JourneyTime.Hours("3"),
      transportArrangement = TransportArrangement.OwnerOfGoods
    )
    lazy val headerEadEsadXml: Elem = <urn:HeaderEadEsad>
      <urn:DestinationTypeCode>{TaxWarehouse.toString}</urn:DestinationTypeCode>
      <urn:JourneyTime>H03</urn:JourneyTime>
      <urn:TransportArrangement>{TransportArrangement.OwnerOfGoods.toString}</urn:TransportArrangement>
    </urn:HeaderEadEsad>
    lazy val headerEadEsadJson: JsObject = Json.obj(
      "destinationType" -> TaxWarehouse.toString,
      "journeyTime" -> "3 hours",
      "transportArrangement" -> TransportArrangement.OwnerOfGoods.toString
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
      <urn:WineOperation>
        <urn:WineOperationCode>op 1</urn:WineOperationCode>
      </urn:WineOperation>
      <urn:WineOperation>
        <urn:WineOperationCode>op 2</urn:WineOperationCode>
      </urn:WineOperation>
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
      bodyRecordUniqueReference = 1,
      exciseProductCode = "epc",
      cnCode = "cn",
      quantity = 1.1,
      grossMass = 1.2,
      netMass = 1.3,
      alcoholicStrengthByVolumeInPercentage = Some(1.4),
      degreePlato = Some(1.5),
      fiscalMark = Some("mark"),
      fiscalMarkUsedFlag = Some(false),
      designationOfOrigin = Some("destination"),
      sizeOfProducer = Some(1),
      density = Some(1.6),
      commercialDescription = Some("description"),
      brandNameOfProducts = Some("name"),
      maturationPeriodOrAgeOfProducts = Some("age"),
      independentSmallProducersDeclaration = Some("independent small producers declaration"),
      packages = Seq(packageModelMax, packageModelMin),
      wineProduct = Some(wineProductModelMax)
    )
    lazy val bodyEadEsadModelMin: BodyEadEsadModel = BodyEadEsadModel(
      bodyRecordUniqueReference = 1,
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
      independentSmallProducersDeclaration = None,
      packages = Seq(packageModelMin),
      wineProduct = None
    )
    lazy val bodyEadEsadXmlMax: Elem = <urn:BodyEadEsad>
      <urn:BodyRecordUniqueReference>1</urn:BodyRecordUniqueReference>
      <urn:ExciseProductCode>epc</urn:ExciseProductCode>
      <urn:CnCode>cn</urn:CnCode>
      <urn:Quantity>1.1</urn:Quantity>
      <urn:GrossMass>1.2</urn:GrossMass>
      <urn:NetMass>1.3</urn:NetMass>
      <urn:AlcoholicStrengthByVolumeInPercentage>1.4</urn:AlcoholicStrengthByVolumeInPercentage>
      <urn:DegreePlato>1.5</urn:DegreePlato>
      <urn:FiscalMark language="en">mark</urn:FiscalMark>
      <urn:FiscalMarkUsedFlag>0</urn:FiscalMarkUsedFlag>
      <urn:DesignationOfOrigin language="en">destination</urn:DesignationOfOrigin>
      <urn:SizeOfProducer>1</urn:SizeOfProducer>
      <urn:Density>1.6</urn:Density>
      <urn:CommercialDescription language="en">description</urn:CommercialDescription>
      <urn:BrandNameOfProducts language="en">name</urn:BrandNameOfProducts>
      <urn:MaturationPeriodOrAgeOfProducts language="en">age</urn:MaturationPeriodOrAgeOfProducts>
      <urn:IndependentSmallProducersDeclaration language="en">independent small producers declaration</urn:IndependentSmallProducersDeclaration>
      {packageXmlMax}
      {packageXmlMin}
      {wineProductXmlMax}
    </urn:BodyEadEsad>
    lazy val bodyEadEsadXmlMin: Elem = <urn:BodyEadEsad>
      <urn:BodyRecordUniqueReference>1</urn:BodyRecordUniqueReference>
      <urn:ExciseProductCode>epc</urn:ExciseProductCode>
      <urn:CnCode>cn</urn:CnCode>
      <urn:Quantity>1.1</urn:Quantity>
      <urn:GrossMass>1.2</urn:GrossMass>
      <urn:NetMass>1.3</urn:NetMass>
      {packageXmlMin}
    </urn:BodyEadEsad>
    lazy val bodyEadEsadJsonMax: JsObject = Json.obj(
      "bodyRecordUniqueReference" -> 1,
      "exciseProductCode" -> "epc",
      "cnCode" -> "cn",
      "quantity" -> 1.1,
      "grossMass" -> 1.2,
      "netMass" -> 1.3,
      "alcoholicStrengthByVolumeInPercentage" -> 1.4,
      "degreePlato" -> 1.5,
      "fiscalMark" -> "mark",
      "fiscalMarkUsedFlag" -> false,
      "designationOfOrigin" -> "destination",
      "sizeOfProducer" -> 1,
      "density" -> 1.6,
      "commercialDescription" -> "description",
      "brandNameOfProducts" -> "name",
      "maturationPeriodOrAgeOfProducts" -> "age",
      "independentSmallProducersDeclaration" -> "independent small producers declaration",
      "packages" -> Json.arr(packageJsonMax, packageJsonMin),
      "wineProduct" -> wineProductJsonMax
    )
    lazy val bodyEadEsadJsonMin: JsObject = Json.obj(
      "bodyRecordUniqueReference" -> 1,
      "exciseProductCode" -> "epc",
      "cnCode" -> "cn",
      "quantity" -> 1.1,
      "grossMass" -> 1.2,
      "netMass" -> 1.3,
      "packages" -> Json.arr(packageJsonMin)
    )
  }

  object ImportCustomsDeclarationFixtures {
    lazy val importCustomsDeclarationModel: ImportCustomsDeclarationModel = ImportCustomsDeclarationModel(
      importCustomsDeclarationNumber = "number"
    )
    lazy val importCustomsDeclarationXml: Elem = <urn:ImportCustomsDeclaration>
      <urn:ImportCustomsDeclarationNumber>number</urn:ImportCustomsDeclarationNumber>
    </urn:ImportCustomsDeclaration>
    lazy val importCustomsDeclarationJson: JsObject = Json.obj(
      "importCustomsDeclarationNumber" -> "number"
    )
  }

  object EadEsadDraftFixtures {
    import ImportCustomsDeclarationFixtures._
    lazy val eadEsadDraftModelMax: EadEsadDraftModel = EadEsadDraftModel(
      localReferenceNumber = "lrn",
      invoiceNumber = "number",
      invoiceDate = Some("inv date"),
      originTypeCode = OriginType.TaxWarehouse,
      dateOfDispatch = "date",
      timeOfDispatch = Some("time"),
      importCustomsDeclaration = Some(Seq(importCustomsDeclarationModel, importCustomsDeclarationModel))
    )
    lazy val eadEsadDraftModelMin: EadEsadDraftModel = EadEsadDraftModel(
      localReferenceNumber = "lrn",
      invoiceNumber = "number",
      invoiceDate = None,
      originTypeCode = OriginType.DutyPaid,
      dateOfDispatch = "date",
      timeOfDispatch = None,
      importCustomsDeclaration = None
    )
    lazy val eadEsadDraftXmlMax: Elem = <urn:EadEsadDraft>
      <urn:LocalReferenceNumber>lrn</urn:LocalReferenceNumber>
      <urn:InvoiceNumber>number</urn:InvoiceNumber>
      <urn:InvoiceDate>inv date</urn:InvoiceDate>
      <urn:OriginTypeCode>{OriginType.TaxWarehouse.toString}</urn:OriginTypeCode>
      <urn:DateOfDispatch>date</urn:DateOfDispatch>
      <urn:TimeOfDispatch>time</urn:TimeOfDispatch>
      {importCustomsDeclarationXml}
      {importCustomsDeclarationXml}
    </urn:EadEsadDraft>
    lazy val eadEsadDraftXmlMin: Elem = <urn:EadEsadDraft>
      <urn:LocalReferenceNumber>lrn</urn:LocalReferenceNumber>
      <urn:InvoiceNumber>number</urn:InvoiceNumber>
      <urn:OriginTypeCode>{OriginType.DutyPaid.toString}</urn:OriginTypeCode>
      <urn:DateOfDispatch>date</urn:DateOfDispatch>
    </urn:EadEsadDraft>
    lazy val eadEsadDraftJsonMax: JsObject = Json.obj(
      "localReferenceNumber" -> "lrn",
      "invoiceNumber" -> "number",
      "invoiceDate" -> "inv date",
      "originTypeCode" -> OriginType.TaxWarehouse.toString,
      "dateOfDispatch" -> "date",
      "timeOfDispatch" -> "time",
      "importCustomsDeclaration" -> Json.arr(importCustomsDeclarationJson, importCustomsDeclarationJson)
    )
    lazy val eadEsadDraftJsonMin: JsObject = Json.obj(
      "localReferenceNumber" -> "lrn",
      "invoiceNumber" -> "number",
      "originTypeCode" -> OriginType.DutyPaid.toString,
      "dateOfDispatch" -> "date"
    )
  }

  object CreateMovementFixtures {
    import BodyEadEsadFixtures._
    import CaMAttributesFixtures._
    import ComplementConsigneeTraderFixtures._
    import DocumentCertificateFixtures._
    import EadEsadDraftFixtures._
    import HeaderEadEsadFixtures._
    import OfficeFixtures._
    import TransportModeFixtures._
    lazy val createMovementModelMax: SubmitCreateMovementModel = SubmitCreateMovementModel(
      movementType = UKtoUK,
      attributes = attributesModelMax,
      consigneeTrader = Some(maxTraderModel(ConsigneeTrader)),
      consignorTrader = maxTraderModel(ConsignorTrader),
      placeOfDispatchTrader = Some(maxTraderModel(PlaceOfDispatchTrader)),
      dispatchImportOffice = Some(officeModel),
      complementConsigneeTrader = Some(complementConsigneeTraderModelMax),
      deliveryPlaceTrader = Some(maxTraderModel(DeliveryPlaceTrader)),
      deliveryPlaceCustomsOffice = Some(officeModel),
      competentAuthorityDispatchOffice = officeModel,
      transportArrangerTrader = Some(maxTraderModel(TransportTrader)),
      firstTransporterTrader = Some(maxTraderModel(TransportTrader)),
      documentCertificate = Some(Seq(documentCertificateModelMax, documentCertificateModelMin)),
      headerEadEsad = headerEadEsadModel,
      transportMode = transportModeModelMax,
      movementGuarantee = maxMovementGuaranteeModel,
      bodyEadEsad = Seq(bodyEadEsadModelMax, bodyEadEsadModelMin),
      eadEsadDraft = eadEsadDraftModelMax,
      transportDetails = Seq(maxTransportDetailsModel)
    )
    lazy val createMovementModelMin: SubmitCreateMovementModel = SubmitCreateMovementModel(
      movementType = UKtoUK,
      attributes = attributesModelMin,
      consigneeTrader = None,
      consignorTrader = maxTraderModel(ConsignorTrader),
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
      movementGuarantee = maxMovementGuaranteeModel,
      bodyEadEsad = Seq(bodyEadEsadModelMin),
      eadEsadDraft = eadEsadDraftModelMin,
      transportDetails = Seq(maxTransportDetailsModel)
    )

    lazy val createMovementModelMultipleCountryCodes: SubmitCreateMovementModel = SubmitCreateMovementModel(
      movementType = UKtoUK,
      attributes = attributesModelMax,
      consigneeTrader = Some(maxTraderModel(ConsignorTrader).copy(traderExciseNumber = Some("CT000001"))),
      consignorTrader = maxTraderModel(ConsignorTrader),
      placeOfDispatchTrader = Some(maxTraderModel(PlaceOfDispatchTrader).copy(traderExciseNumber = Some("PD000001"))),
      dispatchImportOffice = Some(officeModel),
      complementConsigneeTrader = Some(complementConsigneeTraderModelMax.copy(memberStateCode = "CC000001")),
      deliveryPlaceTrader = Some(maxTraderModel(DeliveryPlaceTrader).copy(traderExciseNumber = Some("DP000001"))),
      deliveryPlaceCustomsOffice = Some(officeModel),
      competentAuthorityDispatchOffice = officeModel,
      transportArrangerTrader = Some(maxTraderModel(TransportTrader)),
      firstTransporterTrader = Some(maxTraderModel(TransportTrader)),
      documentCertificate = Some(Seq(documentCertificateModelMax, documentCertificateModelMin)),
      headerEadEsad = headerEadEsadModel,
      transportMode = transportModeModelMax,
      movementGuarantee = maxMovementGuaranteeModel,
      bodyEadEsad = Seq(bodyEadEsadModelMax, bodyEadEsadModelMin),
      eadEsadDraft = eadEsadDraftModelMax,
      transportDetails = Seq(maxTransportDetailsModel)
    )

    lazy val createMovementXmlMax: Elem = <urn:SubmittedDraftOfEADESAD>
      {attributesXmlMax}
      <urn:ConsigneeTrader language="en">
        {maxTraderModelXML(ConsigneeTrader)}
      </urn:ConsigneeTrader>
      <urn:ConsignorTrader language="en">
        {maxTraderModelXML(ConsignorTrader)}
      </urn:ConsignorTrader>
      <urn:PlaceOfDispatchTrader language="en">
        {maxTraderModelXML(PlaceOfDispatchTrader)}
      </urn:PlaceOfDispatchTrader>
      <urn:DispatchImportOffice>{officeXml}</urn:DispatchImportOffice>
      {complementConsigneeTraderXmlMax}
      <urn:DeliveryPlaceTrader language="en">
        {maxTraderModelXML(DeliveryPlaceTrader)}
      </urn:DeliveryPlaceTrader>
      <urn:DeliveryPlaceCustomsOffice>{officeXml}</urn:DeliveryPlaceCustomsOffice>
      <urn:CompetentAuthorityDispatchOffice>{officeXml}</urn:CompetentAuthorityDispatchOffice>
      <urn:TransportArrangerTrader language="en">
        {maxTraderModelXML(TransportTrader)}
      </urn:TransportArrangerTrader>
      <urn:FirstTransporterTrader language="en">
        {maxTraderModelXML(TransportTrader)}
      </urn:FirstTransporterTrader>
      {documentCertificateXmlMax}
      {documentCertificateXmlMin}
      {headerEadEsadXml}
      {transportModeXmlMax}
      {maxMovementGuaranteeXml}
      {bodyEadEsadXmlMax}
      {bodyEadEsadXmlMin}
      {eadEsadDraftXmlMax}
      {maxTransportDetailsXml}
    </urn:SubmittedDraftOfEADESAD>
    lazy val createMovementXmlMin: Elem = <urn:SubmittedDraftOfEADESAD>
      {attributesXmlMin}
      <urn:ConsignorTrader language="en">
        {maxTraderModelXML(ConsignorTrader)}
      </urn:ConsignorTrader>
      <urn:CompetentAuthorityDispatchOffice>{officeXml}</urn:CompetentAuthorityDispatchOffice>
      {headerEadEsadXml}
      {transportModeXmlMin}
      {maxMovementGuaranteeXml}
      {bodyEadEsadXmlMin}
      {eadEsadDraftXmlMin}
      {maxTransportDetailsXml}
    </urn:SubmittedDraftOfEADESAD>
    lazy val createMovementJsonMax: JsObject = Json.obj(
      "movementType" -> UKtoUK.toString,
      "attributes" -> attributesJsonMax,
      "consigneeTrader" -> maxTraderModelJson(ConsigneeTrader),
      "consignorTrader" -> maxTraderModelJson(ConsignorTrader),
      "placeOfDispatchTrader" -> maxTraderModelJson(PlaceOfDispatchTrader),
      "dispatchImportOffice" -> officeJson,
      "complementConsigneeTrader" -> complementConsigneeTraderJsonMax,
      "deliveryPlaceTrader" -> maxTraderModelJson(DeliveryPlaceTrader),
      "deliveryPlaceCustomsOffice" -> officeJson,
      "competentAuthorityDispatchOffice" -> officeJson,
      "transportArrangerTrader" -> maxTraderModelJson(TransportTrader),
      "firstTransporterTrader" -> maxTraderModelJson(TransportTrader),
      "documentCertificate" -> Json.arr(documentCertificateJsonMax, documentCertificateJsonMin),
      "headerEadEsad" -> headerEadEsadJson,
      "transportMode" -> transportModeJsonMax,
      "movementGuarantee" -> maxMovementGuaranteeJson,
      "bodyEadEsad" -> Json.arr(bodyEadEsadJsonMax, bodyEadEsadJsonMin),
      "eadEsadDraft" -> eadEsadDraftJsonMax,
      "transportDetails" -> Json.arr(maxTransportDetailsJson)
    )
    lazy val createMovementJsonMin: JsObject = Json.obj(
      "movementType" -> UKtoUK.toString,
      "attributes" -> attributesJsonMin,
      "consignorTrader" -> maxTraderModelJson(ConsignorTrader),
      "competentAuthorityDispatchOffice" -> officeJson,
      "headerEadEsad" -> headerEadEsadJson,
      "transportMode" -> transportModeJsonMin,
      "movementGuarantee" -> maxMovementGuaranteeJson,
      "bodyEadEsad" -> Json.arr(bodyEadEsadJsonMin),
      "eadEsadDraft" -> eadEsadDraftJsonMin,
      "transportDetails" -> Json.arr(maxTransportDetailsJson)
    )
  }

}
