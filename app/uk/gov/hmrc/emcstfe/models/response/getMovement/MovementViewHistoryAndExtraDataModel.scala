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

import cats.implicits.catsSyntaxTuple13Semigroupal
import com.lucidchart.open.xtract.XmlReader.strictReadSeq
import com.lucidchart.open.xtract.{XmlReader, __}
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.emcstfe.models.reportOfReceipt.SubmitReportOfReceiptModel
import uk.gov.hmrc.emcstfe.models.response.getMovement.GetMovementResponse._

// To circumvent the 22-parameter limit without having to maintain a big x-tuple extension class
case class MovementViewHistoryAndExtraDataModel(
                                     arc: String,
                                     serialNumberOfCertificateOfExemption: Option[String],
                                     dispatchImportOfficeReferenceNumber: Option[String],
                                     deliveryPlaceCustomsOfficeReferenceNumber: Option[String],
                                     competentAuthorityDispatchOfficeReferenceNumber: Option[String],
                                     eadStatus: String,
                                     dateAndTimeOfValidationOfEadEsad: String,
                                     numberOfItems: Int,
                                     reportOfReceipt: Option[SubmitReportOfReceiptModel],
                                     notificationOfDivertedMovement: Option[NotificationOfDivertedMovementModel],
                                     notificationOfAlertOrRejection: Seq[NotificationOfAlertOrRejectionModel],
                                     notificationOfAcceptedExport: Option[NotificationOfAcceptedExportModel],
                                     notificationOfDelay: Seq[NotificationOfDelayModel])

object MovementViewHistoryAndExtraDataModel {

  private[getMovement] lazy val reportOfReceipt = __ \\ "eventHistory" \ "IE818" \ "Body" \ "AcceptedOrRejectedReportOfReceiptExport"
  private[getMovement] lazy val notificationOfDivertedMovement = __ \\ "eventHistory" \ "IE803"
  private[getMovement] lazy val notificationOfAlertOrRejection = __ \\ "eventHistory" \ "IE819"
  private[getMovement] lazy val notificationOfDelay = __ \\ "eventHistory" \ "IE837"

  private[getMovement] lazy val notificationOfAcceptedExport = __ \\ "eventHistory" \ "IE829"

  implicit lazy val xmlReader: XmlReader[MovementViewHistoryAndExtraDataModel] = (
    arc.read[String],
    serialNumberOfCertificateOfExemption.read[Option[String]],
    dispatchImportOfficeReferenceNumber.read[Option[String]],
    deliveryPlaceCustomsOfficeReferenceNumber.read[Option[String]],
    competentAuthorityDispatchOfficeReferenceNumber.read[Option[String]],
    eadStatus.read[String],
    dateAndTimeOfValidationOfEadEsad.read[String],
    numberOfItems.read[Seq[String]](strictReadSeq).map(_.length),
    reportOfReceipt.read[SubmitReportOfReceiptModel](SubmitReportOfReceiptModel.xmlReads).optional,
    notificationOfDivertedMovement.read[NotificationOfDivertedMovementModel](NotificationOfDivertedMovementModel.xmlReads).optional,
    notificationOfAlertOrRejection.read[Seq[NotificationOfAlertOrRejectionModel]](strictReadSeq(NotificationOfAlertOrRejectionModel.xmlReads)),
    notificationOfAcceptedExport.read[NotificationOfAcceptedExportModel](NotificationOfAcceptedExportModel.xmlReads).optional,
    notificationOfDelay.read[Seq[NotificationOfDelayModel]](strictReadSeq(NotificationOfDelayModel.xmlReads))
  ).mapN(MovementViewHistoryAndExtraDataModel.apply)

  implicit val fmt: OFormat[MovementViewHistoryAndExtraDataModel] = Json.format

}