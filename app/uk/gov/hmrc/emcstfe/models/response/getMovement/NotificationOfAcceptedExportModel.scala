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

import cats.implicits.catsSyntaxTuple6Semigroupal
import com.lucidchart.open.xtract.{XPath, XmlReader, __}
import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.emcstfe.models.common.{ConsigneeTrader, TraderModel}
import uk.gov.hmrc.emcstfe.utils.LocalDateXMLReader.xmlLocalDateReads

import java.time.LocalDate

case class NotificationOfAcceptedExportModel(
                                   customsOfficeNumber: String,
                                   dateOfAcceptance: LocalDate,
                                   referenceNumberOfSenderCustomsOffice: String,
                                   identificationOfSenderCustomsOfficer: String,
                                   documentReferenceNumber: String,
                                   consigneeTrader: TraderModel
                                 )

object NotificationOfAcceptedExportModel {
  implicit val format: Format[NotificationOfAcceptedExportModel] = Json.format[NotificationOfAcceptedExportModel]

  private lazy val customsOfficeNumber: XPath = __ \\ "ExportPlaceCustomsOffice" \ "ReferenceNumber"

  private lazy val dateOfAcceptance: XPath = __ \\ "ExportDeclarationAcceptanceRelease" \ "DateOfAcceptance"

  private lazy val referenceNumberOfSenderCustomsOffice: XPath = __ \\ "ExportDeclarationAcceptanceRelease" \ "ReferenceNumberOfSenderCustomsOffice"

  private lazy val identificationOfSenderCustomsOfficer: XPath = __ \\ "ExportDeclarationAcceptanceRelease" \ "IdentificationOfSenderCustomsOfficer"

  private lazy val documentReferenceNumber: XPath = __ \\ "ExportDeclarationAcceptanceRelease" \ "DocumentReferenceNumber"

  private lazy val consigneeTrader: XPath = __ \\ "ConsigneeTrader"

  implicit lazy val xmlReads: XmlReader[NotificationOfAcceptedExportModel] = (
    customsOfficeNumber.read[String],
    dateOfAcceptance.read[LocalDate],
    referenceNumberOfSenderCustomsOffice.read[String],
    identificationOfSenderCustomsOfficer.read[String],
    documentReferenceNumber.read[String],
    consigneeTrader.read[TraderModel](TraderModel.xmlReads(ConsigneeTrader))
  ).mapN(NotificationOfAcceptedExportModel.apply)

}
