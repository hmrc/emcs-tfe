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

import play.api.libs.json.{Format, Json}

import scala.xml.NodeSeq

case class GuarantorAddressModel(streetNumber: Option[String],
                        street: Option[String],
                        postcode: Option[String],
                        city: Option[String]) {
  def toXml: NodeSeq = NodeSeq.fromSeq(Seq(
    {street.map(x => <urn:StreetName>{x}</urn:StreetName>)},
    {streetNumber.map(x => <urn:StreetNumber>{x}</urn:StreetNumber>)},
    {city.map(x => <urn:City>{x}</urn:City>)},
    {postcode.map(x => <urn:Postcode>{x}</urn:Postcode>)}
  ).flatten)
}

object GuarantorAddressModel {
  implicit val fmt: Format[GuarantorAddressModel] = Json.format
}