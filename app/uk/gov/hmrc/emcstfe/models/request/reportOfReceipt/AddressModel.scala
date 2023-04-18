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

package uk.gov.hmrc.emcstfe.models.request.reportOfReceipt

import play.api.libs.json.{Format, Json}

import scala.xml.NodeSeq

case class AddressModel(streetNumber: Option[String],
                        street: Option[String],
                        postcode: Option[String],
                        city: Option[String]) {

  def toXml: NodeSeq = NodeSeq.fromSeq(Seq(
    {street.map(x => <StreetName>{x}</StreetName>)},
    {streetNumber.map(x => <StreetNumber>{x}</StreetNumber>)},
    {postcode.map(x => <Postcode>{x}</Postcode>)},
    {city.map(x => <City>{x}</City>)}
  ).flatten)


}

object AddressModel {
  implicit val fmt: Format[AddressModel] = Json.format
}