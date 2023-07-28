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
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.common.{JourneyTime, TransportArrangement, XmlBaseModel}
import uk.gov.hmrc.emcstfe.utils.XmlWriterUtils

import scala.xml.Elem

case class UpdateEadEsadModel(
                               administrativeReferenceCode: String,
                               journeyTime: Option[JourneyTime],
                               changedTransportArrangement: Option[TransportArrangement],
                               sequenceNumber: Option[String],
                               invoiceDate: Option[String],
                               invoiceNumber: Option[String],
                               transportModeCode: Option[String],
                               complementaryInformation: Option[String]
                             ) extends XmlBaseModel with XmlWriterUtils {

  def toXml(implicit request: UserRequest[_]): Elem = <urn:UpdateEadEsad>
    <urn:AdministrativeReferenceCode>{administrativeReferenceCode}</urn:AdministrativeReferenceCode>
    {journeyTime.mapNodeSeq(value => <urn:JourneyTime>{value.toDownstream}</urn:JourneyTime>)}
    {changedTransportArrangement.mapNodeSeq(value => <urn:ChangedTransportArrangement>{value.toString}</urn:ChangedTransportArrangement>)}
    {sequenceNumber.mapNodeSeq(value => <urn:SequenceNumber>{value}</urn:SequenceNumber>)}
    {invoiceDate.mapNodeSeq(value => <urn:InvoiceDate>{value}</urn:InvoiceDate>)}
    {invoiceNumber.mapNodeSeq(value => <urn:InvoiceNumber>{value}</urn:InvoiceNumber>)}
    {transportModeCode.mapNodeSeq(value => <urn:TransportModeCode>{value}</urn:TransportModeCode>)}
    {complementaryInformation.mapNodeSeq(value => <urn:ComplementaryInformation language="en">{value}</urn:ComplementaryInformation>)}
  </urn:UpdateEadEsad>
}

object UpdateEadEsadModel {
  implicit val fmt: OFormat[UpdateEadEsadModel] = Json.format
}
