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

import cats.implicits.catsSyntaxTuple10Semigroupal
import com.lucidchart.open.xtract.XmlReader.strictReadSeq
import com.lucidchart.open.xtract.{XPath, XmlReader, __}
import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.common.AcceptMovement._
import uk.gov.hmrc.emcstfe.models.common._
import uk.gov.hmrc.emcstfe.utils.LocalDateXMLReader._
import uk.gov.hmrc.emcstfe.utils.XmlWriterUtils

import java.time.LocalDate
import scala.xml.Elem

case class SubmitReportOfReceiptModel(arc: String,
                                      sequenceNumber: Int,
                                      dateAndTimeOfValidationOfReportOfReceiptExport: Option[String],
                                      destinationType: Option[DestinationType],
                                      consigneeTrader: Option[TraderModel],
                                      deliveryPlaceTrader: Option[TraderModel],
                                      destinationOffice: String,
                                      dateOfArrival: LocalDate,
                                      acceptMovement: AcceptMovement,
                                      individualItems: Seq[ReceiptedItemsModel],
                                      otherInformation: Option[String]) extends XmlBaseModel with XmlWriterUtils {

  val globalConclusion = acceptMovement match {
    case Satisfactory => 1
    case Unsatisfactory => 2
    case Refused => 3
    case PartiallyRefused => 4
  }

  def toXml(implicit request: UserRequest[_]): Elem =
    <urn:AcceptedOrRejectedReportOfReceiptExport>
      <urn:Attributes/>
      {consigneeTrader.mapNodeSeq { ct =>
        <urn:ConsigneeTrader language="en">
          {ct.toXml(ConsigneeTrader)}
        </urn:ConsigneeTrader>
      }}
      <urn:ExciseMovement>
        <urn:AdministrativeReferenceCode>
          {arc}
        </urn:AdministrativeReferenceCode>
        <urn:SequenceNumber>
          {sequenceNumber}
        </urn:SequenceNumber>
      </urn:ExciseMovement>
      {deliveryPlaceTrader.mapNodeSeq { dt =>
        <urn:DeliveryPlaceTrader language="en">
          {dt.toXml(DeliveryPlaceTrader)}
        </urn:DeliveryPlaceTrader>
      }}
      <urn:DestinationOffice>
        <urn:ReferenceNumber>
          {destinationOffice}
        </urn:ReferenceNumber>
      </urn:DestinationOffice>
      <urn:ReportOfReceiptExport>
        <urn:DateOfArrivalOfExciseProducts>
          {dateOfArrival.toString}
        </urn:DateOfArrivalOfExciseProducts>
        <urn:GlobalConclusionOfReceipt>
          {globalConclusion}
        </urn:GlobalConclusionOfReceipt>
        {otherInformation.mapNodeSeq(x => <urn:ComplementaryInformation language="en">{x}</urn:ComplementaryInformation>)}
      </urn:ReportOfReceiptExport>
      {individualItems.map(_.toXml)}
    </urn:AcceptedOrRejectedReportOfReceiptExport>
}

object SubmitReportOfReceiptModel {
  private[reportOfReceipt] lazy val arc = __ \ "ExciseMovement" \ "AdministrativeReferenceCode"
  private[reportOfReceipt] lazy val sequenceNumber = __ \ "ExciseMovement" \ "SequenceNumber"
  private[reportOfReceipt] lazy val dateAndTimeOfValidationOfReportOfReceiptExport = __ \ "Attributes" \ "DateAndTimeOfValidationOfReportOfReceiptExport"
  private[reportOfReceipt] lazy val consigneeTrader = __ \ "ConsigneeTrader"
  private[reportOfReceipt] lazy val deliveryPlaceTrader = __ \ "DeliveryPlaceTrader"
  private[reportOfReceipt] lazy val destinationOffice = __ \ "DestinationOffice" \ "ReferenceNumber"
  private[reportOfReceipt] lazy val dateOfArrival: XPath = __ \ "ReportOfReceiptExport" \ "DateOfArrivalOfExciseProducts"
  private[reportOfReceipt] lazy val globalConclusionOfReceipt: XPath = __ \ "ReportOfReceiptExport" \ "GlobalConclusionOfReceipt"
  private[reportOfReceipt] lazy val complementaryInformation: XPath = __ \ "ReportOfReceiptExport" \ "ComplementaryInformation"
  private[reportOfReceipt] lazy val receiptedItems: XPath = __ \\ "BodyReportOfReceiptExport"


  val xmlReads: XmlReader[SubmitReportOfReceiptModel] = (
    arc.read[String],
    sequenceNumber.read[Int],
    dateAndTimeOfValidationOfReportOfReceiptExport.read[Option[String]],
    consigneeTrader.read[Option[TraderModel]](TraderModel.reportOfReceiptXMLReads.optional).map(model => if (model.exists(_.isEmpty)) None else model),
    deliveryPlaceTrader.read[Option[TraderModel]](TraderModel.reportOfReceiptXMLReads.optional).map(model => if (model.exists(_.isEmpty)) None else model),
    destinationOffice.read[String],
    dateOfArrival.read[LocalDate],
    globalConclusionOfReceipt.read[Int],
    receiptedItems.read[Seq[ReceiptedItemsModel]](strictReadSeq(ReceiptedItemsModel.xmlReads)),
    complementaryInformation.read[Option[String]]
  ).mapN {
    case (arc,
    sequenceNumber,
    dateAndTimeOfValidationOfReportOfReceiptExport,
    consigneeTrader,
    deliveryPlaceTrader,
    destinationOffice,
    dateOfArrival,
    receiptStatus,
    items,
    optOtherInformation) =>
      val movementStatus = AcceptMovement.apply(receiptStatus)
      SubmitReportOfReceiptModel(arc, sequenceNumber, dateAndTimeOfValidationOfReportOfReceiptExport, None, consigneeTrader, deliveryPlaceTrader, destinationOffice, dateOfArrival, movementStatus, items, optOtherInformation)
  }

  implicit val fmt: Format[SubmitReportOfReceiptModel] = Json.format
}
