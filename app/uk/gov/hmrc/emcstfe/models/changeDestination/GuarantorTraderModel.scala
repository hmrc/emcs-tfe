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

package uk.gov.hmrc.emcstfe.models.changeDestination

import play.api.libs.json.{Json, OFormat}

import scala.xml.{Elem, NodeSeq}

case class GuarantorTraderModel(
                                 traderExciseNumber: Option[String],
                                 traderName: Option[String],
                                 streetName: Option[String],
                                 streetNumber: Option[String],
                                 city: Option[String],
                                 postcode: Option[String],
                                 vatNumber: Option[String]
                               ) extends ChangeDestinationModel {
  def toXml: Elem = <urn:GuarantorTrader language="en">
    {traderExciseNumber.map(value => <urn:TraderExciseNumber>{value}</urn:TraderExciseNumber>).getOrElse(NodeSeq.Empty)}
    {traderName.map(value => <urn:TraderName>{value}</urn:TraderName>).getOrElse(NodeSeq.Empty)}
    {streetName.map(value => <urn:StreetName>{value}</urn:StreetName>).getOrElse(NodeSeq.Empty)}
    {streetNumber.map(value => <urn:StreetNumber>{value}</urn:StreetNumber>).getOrElse(NodeSeq.Empty)}
    {city.map(value => <urn:City>{value}</urn:City>).getOrElse(NodeSeq.Empty)}
    {postcode.map(value => <urn:Postcode>{value}</urn:Postcode>).getOrElse(NodeSeq.Empty)}
    {vatNumber.map(value => <urn:VatNumber>{value}</urn:VatNumber>).getOrElse(NodeSeq.Empty)}
  </urn:GuarantorTrader>
}

object GuarantorTraderModel {
  implicit val fmt: OFormat[GuarantorTraderModel] = Json.format
}
