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
import uk.gov.hmrc.emcstfe.models.common.{AddressModel, TraderModel}

import scala.xml.{Elem, NodeSeq}

trait TraderModelFixtures extends BaseFixtures with AddressModelFixtures {

  val traderId = "GB0000000012346"

  val maxTraderModel: TraderModel = TraderModel(
    referenceOfTaxWarehouse = Some("reference"),
    vatNumber = Some("number"),
    traderExciseNumber = Some("excise number"),
    traderId = Some(traderId),
    traderName = Some("name"),
    address = Some(maxAddressModel),
    eoriNumber = Some("eori")
  )

  val maxTraderModelXML: NodeSeq =
    NodeSeq.fromSeq(Seq(
      Seq(<urn:ReferenceOfTaxWarehouse>reference</urn:ReferenceOfTaxWarehouse>),
      Seq(<urn:VatNumber>number</urn:VatNumber>),
      Seq(<urn:TraderExciseNumber>excise number</urn:TraderExciseNumber>),
      Seq(<urn:Traderid>{traderId}</urn:Traderid>),
      Seq(<urn:TraderName>name</urn:TraderName>),
      maxAddressModelXML,
      Seq(<urn:EoriNumber>eori</urn:EoriNumber>)
    ).flatten)

  val maxTraderModelJson: JsObject = Json.obj(
    "referenceOfTaxWarehouse" -> "reference",
    "vatNumber" -> "number",
    "traderExciseNumber" -> "excise number",
    "traderId" -> traderId,
    "traderName" -> "name",
    "address" -> maxAddressModelJson,
    "eoriNumber" -> "eori"
  )

  val minTraderModel: TraderModel = TraderModel(
    referenceOfTaxWarehouse = None,
    vatNumber = None,
    traderExciseNumber = None,
    traderId = None,
    traderName = None,
    address = None,
    eoriNumber = None
  )

  val minTraderModelXML: NodeSeq = NodeSeq.Empty

  val minTraderModelJson: JsObject = Json.obj()

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

  object NewTransportArrangerTraderFixtures {
    lazy val newTransportArrangerTraderModelMax: TraderModel = TraderModel(
      referenceOfTaxWarehouse = None,
      vatNumber = Some("number"),
      traderExciseNumber = None,
      traderName = Some("name"),
      address = Some(AddressModel(
        streetNumber = Some("street number"),
        street = Some("street"),
        postcode = Some("a postcode"),
        city = Some("a city")
      )),
      traderId = None,
      eoriNumber = None
    )

    lazy val newTransportArrangerTraderXmlMax: Elem = <urn:NewTransportArrangerTrader language="en">
      <urn:VatNumber>number</urn:VatNumber>
      <urn:TraderName>name</urn:TraderName>
      <urn:StreetName>street</urn:StreetName>
      <urn:StreetNumber>street number</urn:StreetNumber>
      <urn:Postcode>a postcode</urn:Postcode>
      <urn:City>a city</urn:City>
    </urn:NewTransportArrangerTrader>

    lazy val newTransportArrangerTraderJsonMax: JsObject = Json.obj(
      "vatNumber" -> "number",
      "traderName" -> "name",
      "address" -> Json.obj(
        "streetNumber" -> "street number",
        "street" -> "street",
        "postcode" -> "a postcode",
        "city" -> "a city"
      )
    )
  }

  object NewConsigneeTraderFixtures {
    lazy val newConsigneeTraderModelMax: TraderModel = TraderModel(
      referenceOfTaxWarehouse = None,
      vatNumber = None,
      traderExciseNumber = None,
      traderId = Some("id"),
      traderName = Some("name"),
      address = Some(AddressModel(
        streetNumber = Some("street number"),
        street = Some("street"),
        postcode = Some("a postcode"),
        city = Some("a city")
      )),
      eoriNumber = Some("eori")
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

    lazy val newConsigneeTraderJsonMax: JsObject = Json.obj(
      "traderId" -> "id",
      "traderName" -> "name",
      "address" -> Json.obj(
        "street" -> "street",
        "streetNumber" -> "street number",
        "postcode" -> "a postcode",
        "city" -> "a city"
      ),
      "eoriNumber" -> "eori"
    )
  }

}
