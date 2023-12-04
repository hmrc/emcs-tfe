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

package uk.gov.hmrc.emcstfe.models.response.getMovement

import cats.implicits.catsSyntaxTuple8Semigroupal
import com.lucidchart.open.xtract.XmlReader.{strictReadSeq, stringReader}
import com.lucidchart.open.xtract.{XmlReader, __}
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.emcstfe.models.common.OriginType
import uk.gov.hmrc.emcstfe.utils.XmlReaderUtils

case class EadEsadModel(
                         localReferenceNumber: String,
                         invoiceNumber: String,
                         invoiceDate: Option[String],
                         originTypeCode: OriginType,
                         dateOfDispatch: String,
                         timeOfDispatch: Option[String],
                         upstreamArc: Option[String],
                         importSadNumber: Option[Seq[String]]
                       )

object EadEsadModel extends XmlReaderUtils {
  implicit val xmlReads: XmlReader[EadEsadModel] = (
    (__ \\ "LocalReferenceNumber").read[String],
    (__ \\ "InvoiceNumber").read[String],
    (__ \\ "InvoiceDate").read[Option[String]],
    (__ \\ "OriginTypeCode").read[OriginType](OriginType.xmlReads("EadEsad/OriginTypeCode")(OriginType.enumerable)),
    (__ \\ "DateOfDispatch").read[String],
    (__ \\ "TimeOfDispatch").read[Option[String]],
    (__ \\ "UpstreamArc").read[Option[String]],
    (__ \\ "ImportSad" \\ "ImportSadNumber").read[Seq[String]](strictReadSeq).seqToOptionSeq
  ).mapN(EadEsadModel.apply)

  implicit val fmt: OFormat[EadEsadModel] = Json.format
}
