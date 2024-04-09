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

package uk.gov.hmrc.emcstfe.models.nrs.reportOfReceipt

import play.api.libs.json.{Json, Writes}
import uk.gov.hmrc.emcstfe.models.common.{AcceptMovement, DestinationType, TraderModel}
import uk.gov.hmrc.emcstfe.models.reportOfReceipt.{ReceiptedItemsModel, SubmitReportOfReceiptModel}

import java.time.LocalDate

case class ReportOfReceiptNRSSubmission(
                                         ern: String,
                                         arc: String,
                                         sequenceNumber: Int,
                                         dateAndTimeOfValidationOfReportOfReceiptExport: Option[String],
                                         destinationType: Option[DestinationType],
                                         consigneeTrader: Option[TraderModel],
                                         deliveryPlaceTrader: Option[TraderModel],
                                         destinationOffice: String,
                                         dateOfArrival: LocalDate,
                                         acceptMovement: AcceptMovement,
                                         individualItems: Seq[ReceiptedItemsModel],
                                         otherInformation: Option[String]
                                       )

object ReportOfReceiptNRSSubmission {

  def apply(submission: SubmitReportOfReceiptModel, ern: String): ReportOfReceiptNRSSubmission = {
    ReportOfReceiptNRSSubmission(
      ern = ern,
      arc = submission.arc,
      sequenceNumber = submission.sequenceNumber,
      dateAndTimeOfValidationOfReportOfReceiptExport = submission.dateAndTimeOfValidationOfReportOfReceiptExport,
      destinationType = submission.destinationType,
      consigneeTrader = submission.consigneeTrader,
      deliveryPlaceTrader = submission.deliveryPlaceTrader,
      destinationOffice = submission.destinationOffice,
      dateOfArrival = submission.dateOfArrival,
      acceptMovement = submission.acceptMovement,
      individualItems = submission.individualItems,
      otherInformation = submission.otherInformation
    )
  }

  implicit val writes: Writes[ReportOfReceiptNRSSubmission] = Json.writes[ReportOfReceiptNRSSubmission]
}