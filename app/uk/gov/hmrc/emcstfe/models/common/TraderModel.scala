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

import cats.implicits.catsSyntaxTuple7Semigroupal
import com.lucidchart.open.xtract.{XmlReader, __}
import play.api.libs.json.{Format, Json}

import scala.xml.NodeSeq

case class TraderModel(referenceOfTaxWarehouse: Option[String],
                       vatNumber: Option[String],
                       traderExciseNumber: Option[String],
                       traderId: Option[String],
                       traderName: Option[String],
                       address: Option[AddressModel],
                       eoriNumber: Option[String]) {

  val isEmpty: Boolean =
    referenceOfTaxWarehouse.isEmpty &&
      vatNumber.isEmpty &&
      traderExciseNumber.isEmpty &&
      traderId.isEmpty &&
      traderName.isEmpty &&
      (address.isEmpty || address.exists(_.isEmpty)) &&
      eoriNumber.isEmpty

  lazy val countryCode: Option[String] = traderId match {
    case Some(str) if str.length >= 2 => Some(str.substring(0, 2).toUpperCase)
    case _ => None
  }

  def toXml: NodeSeq = NodeSeq.fromSeq(Seq(
    referenceOfTaxWarehouse.map(x => Seq(<urn:ReferenceOfTaxWarehouse>{x}</urn:ReferenceOfTaxWarehouse>)),
    vatNumber.map(x => Seq(<urn:VatNumber>{x}</urn:VatNumber>)),
    traderExciseNumber.map(x => Seq(<urn:TraderExciseNumber>{x}</urn:TraderExciseNumber>)),
    traderId.map(x => Seq(<urn:Traderid>{x}</urn:Traderid>)),
    traderName.map(x => Seq(<urn:TraderName>{x}</urn:TraderName>)),
    address.map(_.toXml.theSeq),
    eoriNumber.map(x => Seq(<urn:EoriNumber>{x}</urn:EoriNumber>))
  ).flatten.flatten)
}

object TraderModel {

  implicit val xmlReads: XmlReader[TraderModel] = (
    (__ \\ "ReferenceOfTaxWarehouse").read[Option[String]],
    (__ \\ "VatNumber").read[Option[String]],
    (__ \\ "TraderExciseNumber").read[Option[String]],
    (__ \\ "Traderid").read[Option[String]],
    (__ \\ "TraderName").read[Option[String]],
    __.read[Option[AddressModel]],
    (__ \\ "EoriNumber").read[Option[String]]
  ).mapN(TraderModel.apply)

  implicit val fmt: Format[TraderModel] = Json.format
}
