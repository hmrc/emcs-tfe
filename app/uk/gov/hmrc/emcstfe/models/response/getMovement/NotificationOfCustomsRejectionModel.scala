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

import cats.implicits.catsSyntaxTuple7Semigroupal
import com.lucidchart.open.xtract.XmlReader.strictReadSeq
import com.lucidchart.open.xtract.{XmlReader, __}
import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.emcstfe.models.common.{ConsigneeTrader, TraderModel}
import uk.gov.hmrc.emcstfe.utils.LocalDateTimeXMLReader.xmlLocalDateTimeReads

import java.time.LocalDateTime

case class NotificationOfCustomsRejectionModel(
    customsOfficeReferenceNumber: Option[String],
    rejectionDateAndTime: LocalDateTime,
    rejectionReasonCode: CustomsRejectionReasonCodeType,
    localReferenceNumber: Option[String],
    documentReferenceNumber: Option[String],
    diagnoses: Seq[CustomsRejectionDiagnosis],
    consignee: Option[TraderModel]
)

object NotificationOfCustomsRejectionModel {

  implicit val format: Format[NotificationOfCustomsRejectionModel] = Json.format[NotificationOfCustomsRejectionModel]

  private lazy val rejectionDateAndTime = __ \\ "RejectionDateAndTime"

  private lazy val rejectionReasonCode = __ \\ "RejectionReasonCode"

  private lazy val localReferenceNumber = __ \\ "ExportDeclarationInformation"  \ "LocalReferenceNumber"

  private lazy val documentReferenceNumber = __ \\ "ExportDeclarationInformation" \ "DocumentReferenceNumber"

  private lazy val diagnoses = __ \\ "UbrCrosscheckResult"

  private lazy val consignee = __ \\ "ConsigneeTrader"

  private lazy val customsOfficeReferenceNumber = __ \\ "ExportPlaceCustomsOffice" \ "ReferenceNumber"

  implicit val xmlReads: XmlReader[NotificationOfCustomsRejectionModel] = (
    customsOfficeReferenceNumber.read[Option[String]],
    rejectionDateAndTime.read[LocalDateTime],
    rejectionReasonCode.read[CustomsRejectionReasonCodeType](CustomsRejectionReasonCodeType.xmlReads("RejectionReasonCode")(CustomsRejectionReasonCodeType.enumerable)),
    localReferenceNumber.read[Option[String]],
    documentReferenceNumber.read[Option[String]],
    diagnoses.read[Seq[CustomsRejectionDiagnosis]](strictReadSeq),
    consignee.read[Option[TraderModel]](TraderModel.xmlReads(ConsigneeTrader).optional).map(model => if (model.exists(_.isEmpty)) None else model),
  ).mapN(NotificationOfCustomsRejectionModel.apply)
}
