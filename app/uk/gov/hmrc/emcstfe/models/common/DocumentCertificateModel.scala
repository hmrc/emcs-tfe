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

import cats.implicits.catsSyntaxTuple4Semigroupal
import com.lucidchart.open.xtract.{XmlReader, __}
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.utils.XmlWriterUtils

import scala.xml.Elem

case class DocumentCertificateModel(
    documentType: Option[String],
    documentReference: Option[String],
    documentDescription: Option[String],
    referenceOfDocument: Option[String]
) extends XmlBaseModel
    with XmlWriterUtils {

  def toXml(implicit request: UserRequest[_]): Elem = <urn:DocumentCertificate>
    {documentType.mapNodeSeq(x => <urn:DocumentType>{x}</urn:DocumentType>)}
    {documentReference.mapNodeSeq(x => <urn:DocumentReference>{x}</urn:DocumentReference>)}
    {documentDescription.mapNodeSeq(x => <urn:DocumentDescription language="en">{x}</urn:DocumentDescription>)}
    {referenceOfDocument.mapNodeSeq(x => <urn:ReferenceOfDocument language="en">{x}</urn:ReferenceOfDocument>)}
  </urn:DocumentCertificate>

}

object DocumentCertificateModel {

  implicit val xmlReads: XmlReader[DocumentCertificateModel] = (
    (__ \\ "DocumentType").read[Option[String]],
    (__ \\ "DocumentReference").read[Option[String]],
    (__ \\ "DocumentDescription").read[Option[String]],
    (__ \\ "ReferenceOfDocument").read[Option[String]]
  ).mapN(DocumentCertificateModel.apply)

  implicit val fmt: OFormat[DocumentCertificateModel] = Json.format
}
