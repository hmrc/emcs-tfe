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
import uk.gov.hmrc.emcstfe.models.changeDestination._
import uk.gov.hmrc.emcstfe.models.common.{DestinationType, TransportArrangement}

import scala.xml.Elem

trait SubmitChangeDestinationFixtures extends BaseFixtures with ChRISResponsesFixture {
  object AttributesFixtures {
    lazy val attributesModelMax: AttributesModel = AttributesModel(
      dateAndTimeOfValidationOfChangeOfDestination = Some("date")
    )

    lazy val attributesModelMin: AttributesModel = AttributesModel(
      dateAndTimeOfValidationOfChangeOfDestination = None
    )

    lazy val attributesXmlMax: Elem = <urn:Attributes>
      <urn:DateAndTimeOfValidationOfChangeOfDestination>date</urn:DateAndTimeOfValidationOfChangeOfDestination>
    </urn:Attributes>

    lazy val attributesXmlMin: Elem = <urn:Attributes/>

    lazy val attributesJsonMax: JsObject = Json.obj(
      "dateAndTimeOfValidationOfChangeOfDestination" -> "date"
    )

    lazy val attributesJsonMin: JsObject = Json.obj()
  }

  object NewTransportArrangerTraderFixtures {
    lazy val newTransportArrangerTraderModelMax: NewTransportArrangerTraderModel = NewTransportArrangerTraderModel(
      vatNumber = Some("number"),
      traderName = "name",
      streetName = "street",
      streetNumber = Some("street number"),
      postcode = "a postcode",
      city = "a city"
    )

    lazy val newTransportArrangerTraderModelMin: NewTransportArrangerTraderModel = NewTransportArrangerTraderModel(
      vatNumber = None,
      traderName = "name",
      streetName = "street",
      streetNumber = None,
      postcode = "a postcode",
      city = "a city"
    )

    lazy val newTransportArrangerTraderXmlMax: Elem = <urn:NewTransportArrangerTrader language="en">
      <urn:VatNumber>number</urn:VatNumber>
      <urn:TraderName>name</urn:TraderName>
      <urn:StreetName>street</urn:StreetName>
      <urn:StreetNumber>street number</urn:StreetNumber>
      <urn:Postcode>a postcode</urn:Postcode>
      <urn:City>a city</urn:City>
    </urn:NewTransportArrangerTrader>

    lazy val newTransportArrangerTraderXmlMin: Elem = <urn:NewTransportArrangerTrader language="en">
      <urn:TraderName>name</urn:TraderName>
      <urn:StreetName>street</urn:StreetName>
      <urn:Postcode>a postcode</urn:Postcode>
      <urn:City>a city</urn:City>
    </urn:NewTransportArrangerTrader>

    lazy val newTransportArrangerTraderJsonMax: JsObject = Json.obj(
      "vatNumber" -> "number",
      "traderName" -> "name",
      "streetName" -> "street",
      "streetNumber" -> "street number",
      "postcode" -> "a postcode",
      "city" -> "a city"
    )

    lazy val newTransportArrangerTraderJsonMin: JsObject = Json.obj(
      "traderName" -> "name",
      "streetName" -> "street",
      "postcode" -> "a postcode",
      "city" -> "a city"
    )
  }

  object UpdateEadEsadFixtures {
    lazy val updateEadEsadModelMax: UpdateEadEsadModel = UpdateEadEsadModel(
      administrativeReferenceCode = testArc,
      journeyTime = Some("time"),
      changedTransportArrangement = Some(TransportArrangement.OwnerOfGoods),
      sequenceNumber = Some("1"),
      invoiceDate = Some("date"),
      invoiceNumber = Some("number"),
      transportModeCode = Some("code"),
      complementaryInformation = Some("info")
    )

    lazy val updateEadEsadModelMin: UpdateEadEsadModel = UpdateEadEsadModel(
      administrativeReferenceCode = testArc,
      journeyTime = None,
      changedTransportArrangement = None,
      sequenceNumber = None,
      invoiceDate = None,
      invoiceNumber = None,
      transportModeCode = None,
      complementaryInformation = None
    )

    lazy val updateEadEsadXmlMax: Elem = <urn:UpdateEadEsad>
      <urn:AdministrativeReferenceCode>{testArc}</urn:AdministrativeReferenceCode>
      <urn:JourneyTime>time</urn:JourneyTime>
      <urn:ChangedTransportArrangement>3</urn:ChangedTransportArrangement>
      <urn:SequenceNumber>1</urn:SequenceNumber>
      <urn:InvoiceDate>date</urn:InvoiceDate>
      <urn:InvoiceNumber>number</urn:InvoiceNumber>
      <urn:TransportModeCode>code</urn:TransportModeCode>
      <urn:ComplementaryInformation language="en">info</urn:ComplementaryInformation>
    </urn:UpdateEadEsad>

    lazy val updateEadEsadXmlMin: Elem = <urn:UpdateEadEsad>
      <urn:AdministrativeReferenceCode>{testArc}</urn:AdministrativeReferenceCode>
    </urn:UpdateEadEsad>

    lazy val updateEadEsadJsonMax: JsObject = Json.obj(
      "administrativeReferenceCode" -> testArc,
      "journeyTime" -> "time",
      "changedTransportArrangement" -> TransportArrangement.OwnerOfGoods.toString,
      "sequenceNumber" -> "1",
      "invoiceDate" -> "date",
      "invoiceNumber" -> "number",
      "transportModeCode" -> "code",
      "complementaryInformation" -> "info"
    )

    lazy val updateEadEsadJsonMin: JsObject = Json.obj(
      "administrativeReferenceCode" -> testArc
    )
  }

  object NewConsigneeTraderFixtures {
    lazy val newConsigneeTraderModelMax: NewConsigneeTraderModel = NewConsigneeTraderModel(
      traderId = Some("id"),
      traderName = "name",
      streetName = "street",
      streetNumber = Some("street number"),
      postcode = "a postcode",
      city = "a city",
      eoriNumber = Some("eori")
    )

    lazy val newConsigneeTraderModelMin: NewConsigneeTraderModel = NewConsigneeTraderModel(
      traderId = None,
      traderName = "name",
      streetName = "street",
      streetNumber = None,
      postcode = "a postcode",
      city = "a city",
      eoriNumber = None
    )

    lazy val newConsigneeTraderXmlMax: Elem = <urn:NewConsigneeTrader language="en">
      <urn:Traderid>id</urn:Traderid>
      <urn:TraderName>name</urn:TraderName>
      <urn:StreetName>street</urn:StreetName>
      <urn:StreetNumber>street number</urn:StreetNumber>
      <urn:Postcode>a postcode</urn:Postcode>
      <urn:City>a city</urn:City>
      <urn:EoriNumber>eori</urn:EoriNumber>
    </urn:NewConsigneeTrader>

    lazy val newConsigneeTraderXmlMin: Elem = <urn:NewConsigneeTrader language="en">
      <urn:TraderName>name</urn:TraderName>
      <urn:StreetName>street</urn:StreetName>
      <urn:Postcode>a postcode</urn:Postcode>
      <urn:City>a city</urn:City>
    </urn:NewConsigneeTrader>

    lazy val newConsigneeTraderJsonMax: JsObject = Json.obj(
      "traderId" -> "id",
      "traderName" -> "name",
      "streetName" -> "street",
      "streetNumber" -> "street number",
      "postcode" -> "a postcode",
      "city" -> "a city",
      "eoriNumber" -> "eori"
    )

    lazy val newConsigneeTraderJsonMin: JsObject = Json.obj(
      "traderName" -> "name",
      "streetName" -> "street",
      "postcode" -> "a postcode",
      "city" -> "a city"
    )
  }

  object DeliveryPlaceTraderFixtures {
    lazy val deliveryPlaceTraderModelMax: DeliveryPlaceTraderModel = DeliveryPlaceTraderModel(
      traderId = Some("id"),
      traderName = Some("name"),
      streetName = Some("street"),
      streetNumber = Some("street number"),
      postcode = Some("a postcode"),
      city = Some("a city")
    )

    lazy val deliveryPlaceTraderModelMin: DeliveryPlaceTraderModel = DeliveryPlaceTraderModel(
      traderId = None,
      traderName = None,
      streetName = None,
      streetNumber = None,
      postcode = None,
      city = None
    )

    lazy val deliveryPlaceTraderXmlMax: Elem = <urn:DeliveryPlaceTrader language="en">
      <urn:Traderid>id</urn:Traderid>
      <urn:TraderName>name</urn:TraderName>
      <urn:StreetName>street</urn:StreetName>
      <urn:StreetNumber>street number</urn:StreetNumber>
      <urn:Postcode>a postcode</urn:Postcode>
      <urn:City>a city</urn:City>
    </urn:DeliveryPlaceTrader>

    lazy val deliveryPlaceTraderXmlMin: Elem = <urn:DeliveryPlaceTrader language="en"/>

    lazy val deliveryPlaceTraderJsonMax: JsObject = Json.obj(
      "traderId" -> "id",
      "traderName" -> "name",
      "streetName" -> "street",
      "streetNumber" -> "street number",
      "postcode" -> "a postcode",
      "city" -> "a city"
    )

    lazy val deliveryPlaceTraderJsonMin: JsObject = Json.obj()
  }

  object DeliveryPlaceCustomsOfficeFixtures {
    lazy val deliveryPlaceCustomsOfficeModel: DeliveryPlaceCustomsOfficeModel = DeliveryPlaceCustomsOfficeModel(
      referenceNumber = "number"
    )

    lazy val deliveryPlaceCustomsOfficeXml: Elem = <urn:DeliveryPlaceCustomsOffice>
      <urn:ReferenceNumber>number</urn:ReferenceNumber>
    </urn:DeliveryPlaceCustomsOffice>

    lazy val deliveryPlaceCustomsOfficeJson: JsObject = Json.obj(
      "referenceNumber" -> "number"
    )
  }

  object GuarantorTraderFixtures {
    lazy val guarantorTraderModelMax: GuarantorTraderModel = GuarantorTraderModel(
      traderExciseNumber = Some("trader number"),
      traderName = Some("name"),
      streetName = Some("street"),
      streetNumber = Some("street number"),
      city = Some("a city"),
      postcode = Some("a postcode"),
      vatNumber = Some("vat number")
    )

    lazy val guarantorTraderModelMin: GuarantorTraderModel = GuarantorTraderModel(
      traderExciseNumber = None,
      traderName = None,
      streetName = None,
      streetNumber = None,
      city = None,
      postcode = None,
      vatNumber = None
    )

    lazy val guarantorTraderXmlMax: Elem = <urn:GuarantorTrader language="en">
      <urn:TraderExciseNumber>trader number</urn:TraderExciseNumber>
      <urn:TraderName>name</urn:TraderName>
      <urn:StreetName>street</urn:StreetName>
      <urn:StreetNumber>street number</urn:StreetNumber>
      <urn:City>a city</urn:City>
      <urn:Postcode>a postcode</urn:Postcode>
      <urn:VatNumber>vat number</urn:VatNumber>
    </urn:GuarantorTrader>

    lazy val guarantorTraderXmlMin: Elem = <urn:GuarantorTrader language="en"/>

    lazy val guarantorTraderJsonMax: JsObject = Json.obj(
      "traderExciseNumber" -> "trader number",
      "traderName" -> "name",
      "streetName" -> "street",
      "streetNumber" -> "street number",
      "city" -> "a city",
      "postcode" -> "a postcode",
      "vatNumber" -> "vat number"
    )

    lazy val guarantorTraderJsonMin: JsObject = Json.obj()
  }

  object MovementGuaranteeFixtures {
    import GuarantorTraderFixtures._

    lazy val movementGuaranteeModelMax: MovementGuaranteeModel = MovementGuaranteeModel(
      guarantorTypeCode = "124",
      guarantorTrader = Some(Seq(guarantorTraderModelMax, guarantorTraderModelMin))
    )

    lazy val movementGuaranteeModelMin: MovementGuaranteeModel = MovementGuaranteeModel(
      guarantorTypeCode = "5",
      guarantorTrader = None
    )

    lazy val movementGuaranteeXmlMax: Elem = <urn:MovementGuarantee>
      <urn:GuarantorTypeCode>124</urn:GuarantorTypeCode>
      {guarantorTraderXmlMax}
      {guarantorTraderXmlMin}
    </urn:MovementGuarantee>

    lazy val movementGuaranteeXmlMin: Elem = <urn:MovementGuarantee>
      <urn:GuarantorTypeCode>5</urn:GuarantorTypeCode>
    </urn:MovementGuarantee>

    lazy val movementGuaranteeJsonMax: JsObject = Json.obj(
      "guarantorTypeCode" -> "124",
      "guarantorTrader" -> Json.arr(guarantorTraderJsonMax, guarantorTraderJsonMin),
    )

    lazy val movementGuaranteeJsonMin: JsObject = Json.obj(
      "guarantorTypeCode" -> "5"
    )
  }

  object DestinationChangedFixtures {
    import NewConsigneeTraderFixtures._
    import DeliveryPlaceTraderFixtures._
    import DeliveryPlaceCustomsOfficeFixtures._
    import MovementGuaranteeFixtures._

    lazy val destinationChangedModelMax: DestinationChangedModel = DestinationChangedModel(
      destinationTypeCode = DestinationType.TemporaryCertifiedConsignee,
      newConsigneeTrader = Some(newConsigneeTraderModelMax),
      deliveryPlaceTrader = Some(deliveryPlaceTraderModelMax),
      deliveryPlaceCustomsOffice = Some(deliveryPlaceCustomsOfficeModel),
      movementGuarantee = Some(movementGuaranteeModelMax)
    )

    lazy val destinationChangedModelMin: DestinationChangedModel = DestinationChangedModel(
      destinationTypeCode = DestinationType.CertifiedConsignee,
      newConsigneeTrader = None,
      deliveryPlaceTrader = None,
      deliveryPlaceCustomsOffice = None,
      movementGuarantee = None
    )

    lazy val destinationChangedXmlMax: Elem = <urn:DestinationChanged>
      <urn:DestinationTypeCode>10</urn:DestinationTypeCode>
      {newConsigneeTraderXmlMax}
      {deliveryPlaceTraderXmlMax}
      {deliveryPlaceCustomsOfficeXml}
      {movementGuaranteeXmlMax}
    </urn:DestinationChanged>

    lazy val destinationChangedXmlMin: Elem = <urn:DestinationChanged>
      <urn:DestinationTypeCode>9</urn:DestinationTypeCode>
    </urn:DestinationChanged>

    lazy val destinationChangedJsonMax: JsObject = Json.obj(
      "destinationTypeCode" -> DestinationType.TemporaryCertifiedConsignee.toString,
      "newConsigneeTrader" -> newConsigneeTraderJsonMax,
      "deliveryPlaceTrader" -> deliveryPlaceTraderJsonMax,
      "deliveryPlaceCustomsOffice" -> deliveryPlaceCustomsOfficeJson,
      "movementGuarantee" -> movementGuaranteeJsonMax,
    )

    lazy val destinationChangedJsonMin: JsObject = Json.obj(
      "destinationTypeCode" -> DestinationType.CertifiedConsignee.toString
    )
  }

  object NewTransporterTraderFixtures {
    lazy val newTransporterTraderModelMax: NewTransporterTraderModel = NewTransporterTraderModel(
      vatNumber = Some("number"),
      traderName = "name",
      streetName = "street",
      streetNumber = Some("street number"),
      postcode = "a postcode",
      city = "a city"
    )

    lazy val newTransporterTraderModelMin: NewTransporterTraderModel = NewTransporterTraderModel(
      vatNumber = None,
      traderName = "name",
      streetName = "street",
      streetNumber = None,
      postcode = "a postcode",
      city = "a city"
    )

    lazy val newTransporterTraderXmlMax: Elem = <urn:NewTransporterTrader language="en">
      <urn:VatNumber>number</urn:VatNumber>
      <urn:TraderName>name</urn:TraderName>
      <urn:StreetName>street</urn:StreetName>
      <urn:StreetNumber>street number</urn:StreetNumber>
      <urn:Postcode>a postcode</urn:Postcode>
      <urn:City>a city</urn:City>
    </urn:NewTransporterTrader>

    lazy val newTransporterTraderXmlMin: Elem = <urn:NewTransporterTrader language="en">
      <urn:TraderName>name</urn:TraderName>
      <urn:StreetName>street</urn:StreetName>
      <urn:Postcode>a postcode</urn:Postcode>
      <urn:City>a city</urn:City>
    </urn:NewTransporterTrader>

    lazy val newTransporterTraderJsonMax: JsObject = Json.obj(
      "vatNumber" -> "number",
      "traderName" -> "name",
      "streetName" -> "street",
      "streetNumber" -> "street number",
      "postcode" -> "a postcode",
      "city" -> "a city"
    )

    lazy val newTransporterTraderJsonMin: JsObject = Json.obj(
      "traderName" -> "name",
      "streetName" -> "street",
      "postcode" -> "a postcode",
      "city" -> "a city"
    )
  }

  object TransportDetailsFixtures {
    lazy val transportDetailsModelMax: TransportDetailsModel = TransportDetailsModel(
      transportUnitCode = "code",
      identityOfTransportUnits = Some("units"),
      commercialSealIdentification = Some("commercial seal info"),
      complementaryInformation = Some("complementary info"),
      sealInformation = Some("seal info")
    )

    lazy val transportDetailsModelMin: TransportDetailsModel = TransportDetailsModel(
      transportUnitCode = "code",
      identityOfTransportUnits = None,
      commercialSealIdentification = None,
      complementaryInformation = None,
      sealInformation = None
    )

    lazy val transportDetailsXmlMax: Elem = <urn:TransportDetails>
      <urn:TransportUnitCode>code</urn:TransportUnitCode>
      <urn:IdentityOfTransportUnits>units</urn:IdentityOfTransportUnits>
      <urn:CommercialSealIdentification>commercial seal info</urn:CommercialSealIdentification>
      <urn:ComplementaryInformation language="en">complementary info</urn:ComplementaryInformation>
      <urn:SealInformation language="en">seal info</urn:SealInformation>
    </urn:TransportDetails>

    lazy val transportDetailsXmlMin: Elem = <urn:TransportDetails>
      <urn:TransportUnitCode>code</urn:TransportUnitCode>
    </urn:TransportDetails>

    lazy val transportDetailsJsonMax: JsObject = Json.obj(
      "transportUnitCode" -> "code",
      "identityOfTransportUnits" -> "units",
      "commercialSealIdentification" -> "commercial seal info",
      "complementaryInformation" -> "complementary info",
      "sealInformation" -> "seal info"
    )

    lazy val transportDetailsJsonMin: JsObject = Json.obj(
      "transportUnitCode" -> "code"
    )
  }

  object SubmitChangeDestinationFixtures {
    import AttributesFixtures._
    import NewTransportArrangerTraderFixtures._
    import UpdateEadEsadFixtures._
    import DestinationChangedFixtures._
    import NewTransporterTraderFixtures._
    import TransportDetailsFixtures._

    lazy val submitChangeDestinationModelMax: SubmitChangeDestinationModel = SubmitChangeDestinationModel(
      attributes = attributesModelMax,
      newTransportArrangerTrader = Some(newTransportArrangerTraderModelMax),
      updateEadEsad = updateEadEsadModelMax,
      destinationChanged = destinationChangedModelMax,
      newTransporterTrader = Some(newTransporterTraderModelMax),
      transportDetails = Some(Seq(transportDetailsModelMax, transportDetailsModelMin))
    )

    lazy val submitChangeDestinationModelMin: SubmitChangeDestinationModel = SubmitChangeDestinationModel(
      attributes = attributesModelMin,
      newTransportArrangerTrader = None,
      updateEadEsad = updateEadEsadModelMin,
      destinationChanged = destinationChangedModelMin,
      newTransporterTrader = None,
      transportDetails = None
    )

    lazy val submitChangeDestinationXmlMax: Elem = <urn:ChangeOfDestination>
      {attributesXmlMax}
      {newTransportArrangerTraderXmlMax}
      {updateEadEsadXmlMax}
      {destinationChangedXmlMax}
      {newTransporterTraderXmlMax}
      {transportDetailsXmlMax}
      {transportDetailsXmlMin}
    </urn:ChangeOfDestination>

    lazy val submitChangeDestinationXmlMin: Elem = <urn:ChangeOfDestination>
      {attributesXmlMin}
      {updateEadEsadXmlMin}
      {destinationChangedXmlMin}
    </urn:ChangeOfDestination>

    lazy val submitChangeDestinationJsonMax: JsObject = Json.obj(
      "attributes" -> attributesJsonMax,
      "newTransportArrangerTrader" -> newTransportArrangerTraderJsonMax,
      "updateEadEsad" -> updateEadEsadJsonMax,
      "destinationChanged" -> destinationChangedJsonMax,
      "newTransporterTrader" -> newTransporterTraderJsonMax,
      "transportDetails" -> Json.arr(transportDetailsJsonMax, transportDetailsJsonMin)
    )

    lazy val submitChangeDestinationJsonMin: JsObject = Json.obj(
      "attributes" -> attributesJsonMin,
      "updateEadEsad" -> updateEadEsadJsonMin,
      "destinationChanged" -> destinationChangedJsonMin
    )
  }
}
