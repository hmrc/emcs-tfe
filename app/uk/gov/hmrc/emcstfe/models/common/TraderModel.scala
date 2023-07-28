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

package uk.gov.hmrc.emcstfe.models.common

import cats.implicits.catsSyntaxTuple5Semigroupal
import com.lucidchart.open.xtract.{XmlReader, __}
import play.api.libs.json.{Format, Json}

import scala.xml.NodeSeq

case class TraderModel(traderExciseNumber: Option[String],
                       traderName: Option[String],
                       address: Option[AddressModel],
                       vatNumber: Option[String],
                       eoriNumber: Option[String]) {

  val isEmpty: Boolean =
    traderExciseNumber.isEmpty &&
      vatNumber.isEmpty &&
      traderName.isEmpty &&
      (address.isEmpty || address.exists(_.isEmpty)) &&
      eoriNumber.isEmpty

  lazy val countryCode: Option[String] = traderExciseNumber match {
    case Some(str) if str.length >= 2 => Some(str.substring(0, 2).toUpperCase)
    case _ => None
  }

  def toXml(traderType: TraderModelType): NodeSeq = {
    val ernNode = traderType match {
      case PlaceOfDispatchTrader =>
        traderExciseNumber.map(x => <urn:ReferenceOfTaxWarehouse>{x}</urn:ReferenceOfTaxWarehouse>)
      case ConsignorTrader | GuarantorTrader =>
        traderExciseNumber.map(x => <urn:TraderExciseNumber>{x}</urn:TraderExciseNumber>)
      case _ =>
        traderExciseNumber.map(x => <urn:Traderid>{x}</urn:Traderid>)
    }

    NodeSeq.fromSeq(traderType match {
      case GuarantorTrader =>
        Seq(
          ernNode.map(Seq(_)),
          traderName.map(x => Seq(<urn:TraderName>{x}</urn:TraderName>)),
          address.map(_.toXml(traderType).theSeq),
          vatNumber.map(x => Seq(<urn:VatNumber>{x}</urn:VatNumber>)),
        ).flatten.flatten
      case ConsigneeTrader =>
        Seq(
          ernNode.map(Seq(_)),
          traderName.map(x => Seq(<urn:TraderName>{x}</urn:TraderName>)),
          address.map(_.toXml(traderType).theSeq),
          eoriNumber.map(x => Seq(<urn:EoriNumber>{x}</urn:EoriNumber>))
        ).flatten.flatten
      case TransportTrader =>
        Seq(
          vatNumber.map(x => Seq(<urn:VatNumber>{x}</urn:VatNumber>)),
          traderName.map(x => Seq(<urn:TraderName>{x}</urn:TraderName>)),
          address.map(_.toXml(traderType).theSeq)
        ).flatten.flatten
      case _ =>
        Seq(
          ernNode.map(Seq(_)),
          traderName.map(x => Seq(<urn:TraderName>{x}</urn:TraderName>)),
          address.map(_.toXml(traderType).theSeq)
        ).flatten.flatten
    })
  }
}

object TraderModel {

  val xmlReadErn: TraderModelType => XmlReader[Option[String]] = {
    case TransportTrader => XmlReader.pure(None)
    case PlaceOfDispatchTrader =>
      (__ \\ "ReferenceOfTaxWarehouse").read[Option[String]]
    case ConsignorTrader | GuarantorTrader =>
      (__ \\ "TraderExciseNumber").read[Option[String]]
    case _ =>
      (__ \\ "Traderid").read[Option[String]]
  }

  def xmlReads(traderType: TraderModelType): XmlReader[TraderModel] = (
    xmlReadErn(traderType),
    (__ \\ "TraderName").read[Option[String]],
    __.read[Option[AddressModel]],
    (__ \\ "VatNumber").read[Option[String]],
    (__ \\ "EoriNumber").read[Option[String]]
  ).mapN(TraderModel.apply)

  implicit val fmt: Format[TraderModel] = Json.format
}
