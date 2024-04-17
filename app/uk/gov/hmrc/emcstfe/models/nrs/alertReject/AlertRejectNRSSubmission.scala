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

package uk.gov.hmrc.emcstfe.models.nrs.alertReject

import play.api.libs.json.{Json, Writes}
import uk.gov.hmrc.emcstfe.models.alertOrRejection.{AlertOrRejectionReasonModel, SubmitAlertOrRejectionModel}
import uk.gov.hmrc.emcstfe.models.common.{ExciseMovementModel, TraderModel}
import uk.gov.hmrc.emcstfe.models.nrs.NotableEvent.AlertRejectNotableEvent
import uk.gov.hmrc.emcstfe.models.nrs.{NRSSubmission, NotableEvent}

import java.time.LocalDate

//Based from the FE audit model
case class AlertRejectNRSSubmission(
                                  arc: String,
                                  sequenceNumber: Int,
                                  consigneeTrader: Option[TraderModel],
                                  exciseMovement: ExciseMovementModel,
                                  destinationOffice: String,
                                  dateOfAlertOrRejection: LocalDate,
                                  isRejected: Boolean,
                                  alertOrRejectionReasons: Option[Seq[AlertOrRejectionReasonModel]]
                                ) extends NRSSubmission {
  override val notableEvent: NotableEvent = AlertRejectNotableEvent
}

object AlertRejectNRSSubmission {

  def apply(submission: SubmitAlertOrRejectionModel): AlertRejectNRSSubmission = AlertRejectNRSSubmission(
    arc = submission.exciseMovement.arc,
    sequenceNumber = submission.exciseMovement.sequenceNumber,
    consigneeTrader = submission.consigneeTrader,
    exciseMovement = submission.exciseMovement,
    destinationOffice = submission.destinationOffice,
    dateOfAlertOrRejection = submission.dateOfAlertOrRejection,
    isRejected = submission.isRejected,
    alertOrRejectionReasons = submission.alertOrRejectionReasons
  )

  implicit val writes: Writes[AlertRejectNRSSubmission] = Json.writes[AlertRejectNRSSubmission]

}
