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

import cats.implicits.catsSyntaxTuple3Semigroupal
import com.lucidchart.open.xtract.{XmlReader, __}
import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.emcstfe.models.auth.UserRequest

import scala.xml.Elem

case class ConsignorTraderModel(traderExciseNumber: String,
                                traderName: String,
                                address: AddressModel) extends XmlBaseModel {

  lazy val countryCode: String = traderExciseNumber.substring(0, 2).toUpperCase

  def toXml(implicit request: UserRequest[_]): Elem =
    <urn:ConsignorTrader language="en">
      <urn:TraderExciseNumber>{traderExciseNumber}</urn:TraderExciseNumber>
      <urn:TraderName>{traderName}</urn:TraderName>
      {address.toXml}
    </urn:ConsignorTrader>
}

object ConsignorTraderModel {

  implicit val xmlReads: XmlReader[ConsignorTraderModel] = (
    (__ \\ "TraderExciseNumber").read[String],
    (__ \\ "TraderName").read[String],
    __.read[AddressModel]
  ).mapN(ConsignorTraderModel.apply)

  implicit val fmt: Format[ConsignorTraderModel] = Json.format
}
