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
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.common.{OriginType, XmlBaseModel}
import uk.gov.hmrc.emcstfe.utils.XmlWriterUtils

import scala.xml.Elem

case class EadEsadDraftModel(
                              localReferenceNumber: String,
                              invoiceNumber: String,
                              invoiceDate: Option[String],
                              originTypeCode: OriginType,
                              dateOfDispatch: String,
                              timeOfDispatch: Option[String],
                              importSad: Option[Seq[ImportSadModel]]
                            ) extends XmlBaseModel with XmlWriterUtils {
  def toXml(implicit request: UserRequest[_]): Elem = <urn:EadEsadDraft>
    <urn:LocalReferenceNumber>{localReferenceNumber}</urn:LocalReferenceNumber>
    <urn:InvoiceNumber>{invoiceNumber}</urn:InvoiceNumber>
    {invoiceDate.mapNodeSeq(value => <urn:InvoiceDate>{value}</urn:InvoiceDate>)}
    <urn:OriginTypeCode>{originTypeCode.toString}</urn:OriginTypeCode>
    <urn:DateOfDispatch>{dateOfDispatch}</urn:DateOfDispatch>
    {timeOfDispatch.mapNodeSeq(value => <urn:TimeOfDispatch>{value}</urn:TimeOfDispatch>)}
    {importSad.mapNodeSeq(_.map(_.toXml))}
  </urn:EadEsadDraft>
}

object EadEsadDraftModel {
  implicit val fmt: OFormat[EadEsadDraftModel] = Json.format
}
