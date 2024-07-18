/*
 * Copyright 2024 HM Revenue & Customs
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

import cats.implicits.catsSyntaxTuple11Semigroupal
import com.lucidchart.open.xtract.XmlReader.strictReadSeq
import com.lucidchart.open.xtract.{XPath, XmlReader, __}
import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.emcstfe.models.common.SupportingDocumentModel
import uk.gov.hmrc.emcstfe.models.response.getMovement.MovementItem.StringExtensions
import uk.gov.hmrc.emcstfe.utils.LocalDateTimeXMLReader.xmlLocalDateTimeReads
import uk.gov.hmrc.emcstfe.utils.XmlReaderUtils

import java.time.LocalDateTime

case class ManualClosureResponseModel(
    sequenceNumber: Int,
    dateOfArrivalOfExciseProducts: Option[LocalDateTime],
    globalConclusionOfReceipt: GlobalConclusionofReceiptReasonCodeType,
    complementaryInformation: Option[String],
    manualClosureRequestReason: ManualClosureRequestReasonCodeType,
    manualClosureRequestReasonComplement: Option[String],
    manualClosureRequestAccepted: Boolean,
    manualClosureRejectionReason: Option[ManualClosureRejectionReasonCodeType],
    manualClosureRejectionComplement: Option[String],
    supportingDocuments: Option[Seq[SupportingDocumentModel]],
    bodyManualClosure: Option[Seq[ManualClosureItem]]
)

object ManualClosureResponseModel extends XmlReaderUtils {

  private lazy val sequenceNumber: XPath = __ \\ "Attributes" \\ "SequenceNumber"

  private lazy val dateOfArrivalOfExciseProducts: XPath = __ \\ "Attributes" \\ "DateOfArrivalOfExciseProducts"

  private lazy val globalConclusionOfReceipt: XPath = __ \\ "Attributes" \\ "GlobalConclusionOfReceipt"

  private lazy val complementaryInformation: XPath = __ \\ "Attributes" \\ "ComplementaryInformation"

  private lazy val manualClosureRequestReasonCode: XPath = __ \\ "Attributes" \\ "ManualClosureRequestReasonCode"

  private lazy val manualClosureRequestReasonCodeComplement: XPath = __ \\ "Attributes" \\ "ManualClosureRequestReasonCodeComplement"

  private lazy val manualClosureRequestAccepted: XPath = __ \\ "Attributes" \\ "ManualClosureRequestAccepted"

  private lazy val manualClosureRejectionReasonCode: XPath = __ \\ "Attributes" \\ "ManualClosureRejectionReasonCode"

  private lazy val manualClosureRejectionComplement: XPath = __ \\ "Attributes" \\ "ManualClosureRejectionComplement"

  private lazy val supportingDocuments: XPath = __ \\ "SupportingDocuments"

  private lazy val bodyManualClosure: XPath = __ \\ "BodyManualClosure"


  implicit val xmlReads: XmlReader[ManualClosureResponseModel] = (
    sequenceNumber.read[Int],
    dateOfArrivalOfExciseProducts.read[Option[LocalDateTime]],
    globalConclusionOfReceipt.read[GlobalConclusionofReceiptReasonCodeType](GlobalConclusionofReceiptReasonCodeType.xmlReads("GlobalConclusionOfReceipt")(GlobalConclusionofReceiptReasonCodeType.enumerable)),
    complementaryInformation.read[String].optional,
    manualClosureRequestReasonCode.read[ManualClosureRequestReasonCodeType](ManualClosureRequestReasonCodeType.xmlReads("ManualClosureRequestReasonCode")(ManualClosureRequestReasonCodeType.enumerable)),
    manualClosureRequestReasonCodeComplement.read[String].optional,
    manualClosureRequestAccepted.read[String].map(_.fromFlag),
    manualClosureRejectionReasonCode.read[ManualClosureRejectionReasonCodeType](ManualClosureRejectionReasonCodeType.xmlReads("ManualClosureRejectionReasonCode")(ManualClosureRejectionReasonCodeType.enumerable)).optional,
    manualClosureRejectionComplement.read[String].optional,
    supportingDocuments.read[Seq[SupportingDocumentModel]](strictReadSeq(SupportingDocumentModel.xmlReads)).seqToOptionSeq,
    bodyManualClosure.read[Seq[ManualClosureItem]](strictReadSeq(ManualClosureItem.xmlReads)).seqToOptionSeq,
  ).mapN(ManualClosureResponseModel.apply)

  implicit val format: Format[ManualClosureResponseModel] = Json.format[ManualClosureResponseModel]
}
