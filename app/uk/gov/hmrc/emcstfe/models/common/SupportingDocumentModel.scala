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
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.utils.XmlWriterUtils

import scala.xml.Elem

case class SupportingDocumentModel(
    supportingDocumentDescription: Option[String],
    referenceOfSupportingDocument: Option[String],
    supportingDocumentType: Option[String]
) extends XmlBaseModel with XmlWriterUtils {

  def toXml(implicit request: UserRequest[_]): Elem = <urn:SupportingDocuments>
    {supportingDocumentDescription.mapNodeSeq(x => <urn:SupportingDocumentDescription>{x}</urn:SupportingDocumentDescription>)}
    {referenceOfSupportingDocument.mapNodeSeq(x => <urn:ReferenceOfSupportingDocument>{x}</urn:ReferenceOfSupportingDocument>)}
    {supportingDocumentType.mapNodeSeq(x => <urn:SupportingDocumentType>{x}</urn:SupportingDocumentType>)}
  </urn:SupportingDocuments>

}

object SupportingDocumentModel {

  implicit val xmlReads: XmlReader[SupportingDocumentModel] = (
    (__ \\ "SupportingDocumentDescription").read[Option[String]],
    (__ \\ "ReferenceOfSupportingDocument").read[Option[String]],
    (__ \\ "SupportingDocumentType").read[Option[String]]
  ).mapN(SupportingDocumentModel.apply)

  implicit val fmt: OFormat[SupportingDocumentModel] = Json.format
}
