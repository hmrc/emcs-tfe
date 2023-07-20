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

package uk.gov.hmrc.emcstfe.models.createMovement
import play.api.libs.json.{Json, OFormat}

import scala.xml.{Elem, NodeSeq}

case class PackageModel(
                         kindOfPackages: String,
                         numberOfPackages: Option[Int],
                         shippingMarks: Option[String],
                         commercialSealIdentification: Option[String],
                         sealInformation: Option[String]
                       ) extends CreateMovement {
  def toXml: Elem = <urn:Package>
    <urn:KindOfPackages>{kindOfPackages}</urn:KindOfPackages>
    {numberOfPackages.map(value => <urn:NumberOfPackages>{value}</urn:NumberOfPackages>).getOrElse(NodeSeq.Empty)}
    {shippingMarks.map(value => <urn:ShippingMarks>{value}</urn:ShippingMarks>).getOrElse(NodeSeq.Empty)}
    {commercialSealIdentification.map(value => <urn:CommercialSealIdentification>{value}</urn:CommercialSealIdentification>).getOrElse(NodeSeq.Empty)}
    {sealInformation.map(value => <urn:SealInformation language="en">{value}</urn:SealInformation>).getOrElse(NodeSeq.Empty)}
  </urn:Package>
}

object PackageModel {
  implicit val fmt: OFormat[PackageModel] = Json.format
}
