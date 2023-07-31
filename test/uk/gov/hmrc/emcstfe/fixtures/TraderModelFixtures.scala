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

import scala.xml.NodeSeq

trait TraderModelFixtures extends BaseFixtures with AddressModelFixtures {

  val traderExciseNumber = "GB0000000012346"

  def maxTraderModel(traderType: TraderModelType): TraderModel = TraderModel(
    traderExciseNumber = if(traderType == TransportTrader) None else Some(traderExciseNumber),
    traderName = Some("name"),
    address = Some(maxAddressModel),
    vatNumber = if(traderType == TransportTrader || traderType == GuarantorTrader) Some("number") else None,
    eoriNumber = if(traderType == ConsigneeTrader) Some("eori") else None
  )

  private val ernNode: TraderModelType => NodeSeq = {
    case PlaceOfDispatchTrader =>
      Seq(<urn:ReferenceOfTaxWarehouse>{traderExciseNumber}</urn:ReferenceOfTaxWarehouse>)
    case ConsignorTrader | GuarantorTrader =>
      Seq(<urn:TraderExciseNumber>{traderExciseNumber}</urn:TraderExciseNumber>)
    case _ =>
      Seq(<urn:Traderid>{traderExciseNumber}</urn:Traderid>)
  }

  def maxTraderModelXML(traderType: TraderModelType): NodeSeq =
    NodeSeq.fromSeq(
      traderType match {
        case GuarantorTrader =>
          Seq(
            ernNode(traderType),
            Seq(<urn:TraderName>name</urn:TraderName>),
            maxAddressModelXML(traderType),
            Seq(<urn:VatNumber>number</urn:VatNumber>)
          ).flatten
        case ConsigneeTrader =>
          Seq(
            ernNode(traderType),
            Seq(<urn:TraderName>name</urn:TraderName>),
            maxAddressModelXML(traderType),
            Seq(<urn:EoriNumber>eori</urn:EoriNumber>)
          ).flatten
        case TransportTrader =>
          Seq(
            Seq(<urn:VatNumber>number</urn:VatNumber>),
            Seq(<urn:TraderName>name</urn:TraderName>),
            maxAddressModelXML(traderType),
          ).flatten
        case _ =>
          Seq(
            ernNode(traderType),
            Seq(<urn:TraderName>name</urn:TraderName>),
            maxAddressModelXML(traderType)
          ).flatten
      })

  def maxTraderModelJson(traderType: TraderModelType): JsObject = traderType match {
    case GuarantorTrader =>
      Json.obj(
        "traderExciseNumber" -> traderExciseNumber,
        "traderName" -> "name",
        "address" -> maxAddressModelJson,
        "vatNumber" -> "number"
      )
    case TransportTrader =>
      Json.obj(
        "traderName" -> "name",
        "address" -> maxAddressModelJson,
        "vatNumber" -> "number"
      )
    case ConsigneeTrader =>
      Json.obj(
        "traderExciseNumber" -> traderExciseNumber,
        "traderName" -> "name",
        "address" -> maxAddressModelJson,
        "eoriNumber" -> "eori"
      )
    case _ =>
      Json.obj(
        "traderExciseNumber" -> traderExciseNumber,
        "traderName" -> "name",
        "address" -> maxAddressModelJson
      )
  }

  val minTraderModel: TraderModel = TraderModel(
    traderExciseNumber = None,
    traderName = None,
    address = None,
    vatNumber = None,
    eoriNumber = None
  )

  val minTraderModelXML: NodeSeq = NodeSeq.Empty

  val minTraderModelJson: JsObject = Json.obj()

}
