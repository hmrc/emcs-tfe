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
import uk.gov.hmrc.emcstfe.models.common.GuarantorType.ConsignorOwner
import uk.gov.hmrc.emcstfe.models.common.{GuarantorAddressModel, GuarantorTraderModel, MovementGuaranteeModel}

import scala.xml.Elem

trait MovementGuaranteeFixtures extends BaseFixtures with AddressModelFixtures {

  lazy val maxMovementGuaranteeModel: MovementGuaranteeModel = MovementGuaranteeModel(
    guarantorTypeCode = ConsignorOwner,
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

  lazy val maxMovementGuaranteeXml: Elem = <urn:MovementGuarantee>
    <urn:GuarantorTypeCode>{ConsignorOwner.toString}</urn:GuarantorTypeCode>
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

  lazy val maxMovementGuaranteeJson: JsObject = Json.obj(
    "guarantorTypeCode" -> ConsignorOwner.toString,
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


  lazy val minMovementGuaranteeModel: MovementGuaranteeModel = MovementGuaranteeModel(
    guarantorTypeCode = ConsignorOwner,
    guarantorTrader = None
  )

  lazy val minMovementGuaranteeXml: Elem = <urn:MovementGuarantee>
    <urn:GuarantorTypeCode>{ConsignorOwner.toString}</urn:GuarantorTypeCode>
  </urn:MovementGuarantee>

  lazy val minMovementGuaranteeJson: JsObject = Json.obj(
    "guarantorTypeCode" -> ConsignorOwner.toString
  )

}
