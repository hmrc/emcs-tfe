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

package uk.gov.hmrc.emcstfe.models.reportOfReceipt

import cats.implicits.catsSyntaxTuple6Semigroupal
import com.lucidchart.open.xtract.XmlReader.strictReadSeq
import com.lucidchart.open.xtract.{XmlReader, __}
import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.emcstfe.utils.{XmlReaderUtils, XmlWriterUtils}

case class ReceiptedItemsModel(eadBodyUniqueReference: Int,
                               productCode: String,
                               excessAmount: Option[BigDecimal],
                               shortageAmount: Option[BigDecimal],
                               refusedAmount: Option[BigDecimal],
                               unsatisfactoryReasons: Seq[UnsatisfactoryModel]) extends XmlWriterUtils {

  private val shortageExcessIndicator = (excessAmount, shortageAmount) match {
    case (Some(amt), _) if amt > 0 => Some("E")
    case (_, Some(amt)) if amt > 0 => Some("S")
    case _ => None
  }

  private val shortageExcessAmount = (excessAmount, shortageAmount) match {
    case (Some(amt), _) if amt > 0 => Some(amt)
    case (_, Some(amt)) if amt > 0 => Some(amt)
    case _ => None
  }

  def toXml =
    <urn:BodyReportOfReceiptExport>
      <urn:BodyRecordUniqueReference>
        {eadBodyUniqueReference}
      </urn:BodyRecordUniqueReference>
      {shortageExcessIndicator.mapNodeSeq(x => <urn:IndicatorOfShortageOrExcess>{x}</urn:IndicatorOfShortageOrExcess>)}
      {shortageExcessAmount.mapNodeSeq(x => <urn:ObservedShortageOrExcess>{x}</urn:ObservedShortageOrExcess>)}
      <urn:ExciseProductCode>
        {productCode}
      </urn:ExciseProductCode>
      {refusedAmount.mapNodeSeq(x => <urn:RefusedQuantity>{x}</urn:RefusedQuantity>)}
      {unsatisfactoryReasons.map(_.toXml)}
    </urn:BodyReportOfReceiptExport>
}

object ReceiptedItemsModel extends XmlReaderUtils {

  val xmlReads: XmlReader[ReceiptedItemsModel] = (
    (__ \ "BodyRecordUniqueReference").read[Int],
    (__ \ "ExciseProductCode").read[String],
    (__ \ "IndicatorOfShortageOrExcess").read[Option[String]],
    (__ \ "ObservedShortageOrExcess").read[Option[BigDecimal]],
    (__ \ "RefusedQuantity").read[Option[BigDecimal]],
    (__ \ "UnsatisfactoryReason").read[Seq[UnsatisfactoryModel]](strictReadSeq(UnsatisfactoryModel.xmlReads))
  ).mapN {
    case (uniqueReference, epc, shortageOrExcessIndicator, shortageOrExcessValue, refusedAmount, unsatisfactoryReasons) => {
      ReceiptedItemsModel(
        eadBodyUniqueReference = uniqueReference,
        productCode = epc,
        excessAmount = if (shortageOrExcessIndicator.contains("E")) shortageOrExcessValue else None,
        shortageAmount = if (shortageOrExcessIndicator.contains("S")) shortageOrExcessValue else None,
        refusedAmount = refusedAmount,
        unsatisfactoryReasons = unsatisfactoryReasons
      )
    }
  }
  implicit val fmt: Format[ReceiptedItemsModel] = Json.format
}
