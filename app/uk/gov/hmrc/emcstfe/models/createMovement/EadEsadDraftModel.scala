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

case class EadEsadDraftModel(
                              localReferenceNumber: String,
                              invoiceNumber: String,
                              invoiceDate: Option[String],
                              originTypeCode: String,
                              dateOfDispatch: String,
                              timeOfDispatch: Option[String],
                              importSad: Option[Seq[ImportSadModel]]
                            ) extends CreateMovement {
  def toXml: Elem = <urn:EadEsadDraft>
    <urn:LocalReferenceNumber>{localReferenceNumber}</urn:LocalReferenceNumber>
    <urn:InvoiceNumber>{invoiceNumber}</urn:InvoiceNumber>
    {invoiceDate.map(value => <urn:InvoiceDate>{value}</urn:InvoiceDate>).getOrElse(NodeSeq.Empty)}
    <urn:OriginTypeCode>{originTypeCode}</urn:OriginTypeCode>
    <urn:DateOfDispatch>{dateOfDispatch}</urn:DateOfDispatch>
    {timeOfDispatch.map(value => <urn:TimeOfDispatch>{value}</urn:TimeOfDispatch>).getOrElse(NodeSeq.Empty)}
    {importSad.map(_.map(_.toXml)).getOrElse(NodeSeq.Empty)}
  </urn:EadEsadDraft>
}

object EadEsadDraftModel {
  implicit val fmt: OFormat[EadEsadDraftModel] = Json.format
}
