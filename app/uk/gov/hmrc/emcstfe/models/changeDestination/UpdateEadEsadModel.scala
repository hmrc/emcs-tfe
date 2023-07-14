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
import uk.gov.hmrc.emcstfe.models.common.TransportArrangement

import scala.xml.{Elem, NodeSeq}

case class UpdateEadEsadModel(
                               administrativeReferenceCode: String,
                               journeyTime: Option[String],
                               changedTransportArrangement: Option[TransportArrangement],
                               sequenceNumber: Option[String],
                               invoiceDate: Option[String],
                               invoiceNumber: Option[String],
                               transportModeCode: Option[String],
                               complementaryInformation: Option[String]
                             ) extends ChangeDestinationModel {

  def toXml: Elem = <urn:UpdateEadEsad>
    <urn:AdministrativeReferenceCode>{administrativeReferenceCode}</urn:AdministrativeReferenceCode>
    {journeyTime.map(value => <urn:JourneyTime>{value}</urn:JourneyTime>).getOrElse(NodeSeq.Empty)}
    {changedTransportArrangement.map(value => <urn:ChangedTransportArrangement>{value.toString}</urn:ChangedTransportArrangement>).getOrElse(NodeSeq.Empty)}
    {sequenceNumber.map(value => <urn:SequenceNumber>{value}</urn:SequenceNumber>).getOrElse(NodeSeq.Empty)}
    {invoiceDate.map(value => <urn:InvoiceDate>{value}</urn:InvoiceDate>).getOrElse(NodeSeq.Empty)}
    {invoiceNumber.map(value => <urn:InvoiceNumber>{value}</urn:InvoiceNumber>).getOrElse(NodeSeq.Empty)}
    {transportModeCode.map(value => <urn:TransportModeCode>{value}</urn:TransportModeCode>).getOrElse(NodeSeq.Empty)}
    {complementaryInformation.map(value => <urn:ComplementaryInformation language="en">{value}</urn:ComplementaryInformation>).getOrElse(NodeSeq.Empty)}
  </urn:UpdateEadEsad>
}

object UpdateEadEsadModel {
  implicit val fmt: OFormat[UpdateEadEsadModel] = Json.format
}
