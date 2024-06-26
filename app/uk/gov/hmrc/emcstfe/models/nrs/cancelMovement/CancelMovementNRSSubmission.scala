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

package uk.gov.hmrc.emcstfe.models.nrs.cancelMovement

import play.api.libs.json.{Json, Writes}
import uk.gov.hmrc.emcstfe.models.cancellationOfMovement.{CancellationReasonType, SubmitCancellationOfMovementModel}
import uk.gov.hmrc.emcstfe.models.common.{DestinationType, TraderModel}
import uk.gov.hmrc.emcstfe.models.nrs.NotableEvent.CancelMovementNotableEvent
import uk.gov.hmrc.emcstfe.models.nrs.{NRSSubmission, NotableEvent}

//Based from the FE audit model
case class CancelMovementNRSSubmission(
                                        ern: String,
                                        arc: String,
                                        sequenceNumber: Int,
                                        consigneeTrader: Option[TraderModel],
                                        destinationType: DestinationType,
                                        memberStateCode: Option[String],
                                        cancelReason: CancellationReasonType,
                                        additionalInformation: Option[String]
                                      ) extends NRSSubmission {
  override val notableEvent: NotableEvent = CancelMovementNotableEvent
}

object CancelMovementNRSSubmission {

  def apply(submission: SubmitCancellationOfMovementModel, ern: String): CancelMovementNRSSubmission = CancelMovementNRSSubmission(
    ern = ern,
    arc = submission.exciseMovement.arc,
    sequenceNumber = submission.exciseMovement.sequenceNumber,
    consigneeTrader = submission.consigneeTrader,
    destinationType = submission.destinationType,
    memberStateCode = submission.memberStateCode,
    cancelReason = submission.cancellationReason.reason,
    additionalInformation = submission.cancellationReason.complementaryInformation
  )

  implicit val writes: Writes[CancelMovementNRSSubmission] = Json.writes[CancelMovementNRSSubmission]

}