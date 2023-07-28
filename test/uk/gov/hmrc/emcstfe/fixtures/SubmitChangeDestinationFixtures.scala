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
import uk.gov.hmrc.emcstfe.models.common.JourneyTime.Hours
import uk.gov.hmrc.emcstfe.models.common._

import scala.xml.Elem

trait SubmitChangeDestinationFixtures extends BaseFixtures
  with TraderModelFixtures
  with MovementGuaranteeFixtures
  with TransportDetailsFixtures
  with ChRISResponsesFixture {

    object UpdateEadEsadFixtures {
    lazy val updateEadEsadModelMax: UpdateEadEsadModel = UpdateEadEsadModel(
      administrativeReferenceCode = testArc,
      journeyTime = Some(Hours("20")),
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
      <urn:JourneyTime>H20</urn:JourneyTime>
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
      "journeyTime" -> "20 hours",
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

  object DestinationChangedFixtures {
    import DeliveryPlaceCustomsOfficeFixtures._

    lazy val destinationChangedModelMax: DestinationChangedModel = DestinationChangedModel(
      destinationTypeCode = DestinationType.TemporaryCertifiedConsignee,
      newConsigneeTrader = Some(maxTraderModel(ConsigneeTrader)),
      deliveryPlaceTrader = Some(maxTraderModel(DeliveryPlaceTrader)),
      deliveryPlaceCustomsOffice = Some(deliveryPlaceCustomsOfficeModel),
      movementGuarantee = Some(maxMovementGuaranteeModel)
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
      <urn:NewConsigneeTrader language="en">
        {maxTraderModelXML(ConsigneeTrader)}
      </urn:NewConsigneeTrader>
      <urn:DeliveryPlaceTrader language="en">
        {maxTraderModelXML(DeliveryPlaceTrader)}
      </urn:DeliveryPlaceTrader>
      {deliveryPlaceCustomsOfficeXml}
      {maxMovementGuaranteeXml}
    </urn:DestinationChanged>

    lazy val destinationChangedXmlMin: Elem = <urn:DestinationChanged>
      <urn:DestinationTypeCode>9</urn:DestinationTypeCode>
    </urn:DestinationChanged>

    lazy val destinationChangedJsonMax: JsObject = Json.obj(
      "destinationTypeCode" -> DestinationType.TemporaryCertifiedConsignee.toString,
      "newConsigneeTrader" -> maxTraderModelJson(ConsigneeTrader),
      "deliveryPlaceTrader" -> maxTraderModelJson(DeliveryPlaceTrader),
      "deliveryPlaceCustomsOffice" -> deliveryPlaceCustomsOfficeJson,
      "movementGuarantee" -> maxMovementGuaranteeJson
    )

    lazy val destinationChangedJsonMin: JsObject = Json.obj(
      "destinationTypeCode" -> DestinationType.CertifiedConsignee.toString
    )
  }

  object SubmitChangeDestinationFixtures {
    import DestinationChangedFixtures._
    import UpdateEadEsadFixtures._

    lazy val submitChangeDestinationModelMax: SubmitChangeDestinationModel = SubmitChangeDestinationModel(
      newTransportArrangerTrader = Some(maxTraderModel(TransportTrader)),
      updateEadEsad = updateEadEsadModelMax,
      destinationChanged = destinationChangedModelMax,
      newTransporterTrader = Some(maxTraderModel(TransportTrader)),
      transportDetails = Some(Seq(maxTransportDetailsModel, maxTransportDetailsModel))
    )

    lazy val submitChangeDestinationModelMin: SubmitChangeDestinationModel = SubmitChangeDestinationModel(
      newTransportArrangerTrader = None,
      updateEadEsad = updateEadEsadModelMin,
      destinationChanged = destinationChangedModelMin,
      newTransporterTrader = None,
      transportDetails = None
    )

    lazy val submitChangeDestinationXmlMax: Elem = <urn:ChangeOfDestination>
      <urn:Attributes />
      <urn:NewTransportArrangerTrader language="en">
        {maxTraderModelXML(TransportTrader)}
      </urn:NewTransportArrangerTrader>
      {updateEadEsadXmlMax}
      {destinationChangedXmlMax}
      <urn:NewTransporterTrader language="en">
        {maxTraderModelXML(TransportTrader)}
      </urn:NewTransporterTrader>
      {maxTransportDetailsXml}
      {maxTransportDetailsXml}
    </urn:ChangeOfDestination>

    lazy val submitChangeDestinationXmlMin: Elem = <urn:ChangeOfDestination>
      <urn:Attributes/>
      {updateEadEsadXmlMin}
      {destinationChangedXmlMin}
    </urn:ChangeOfDestination>

    lazy val submitChangeDestinationJsonMax: JsObject = Json.obj(
      "newTransportArrangerTrader" -> maxTraderModelJson(TransportTrader),
      "updateEadEsad" -> updateEadEsadJsonMax,
      "destinationChanged" -> destinationChangedJsonMax,
      "newTransporterTrader" -> maxTraderModelJson(TransportTrader),
      "transportDetails" -> Json.arr(maxTransportDetailsJson, maxTransportDetailsJson)
    )

    lazy val submitChangeDestinationJsonMin: JsObject = Json.obj(
      "updateEadEsad" -> updateEadEsadJsonMin,
      "destinationChanged" -> destinationChangedJsonMin
    )
  }
}
