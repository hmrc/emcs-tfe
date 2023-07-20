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
import uk.gov.hmrc.emcstfe.models.common.TraderModel

import scala.xml.NodeSeq

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

}
