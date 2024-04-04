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

package uk.gov.hmrc.emcstfe.models.nrs

import uk.gov.hmrc.emcstfe.models.common.{Enumerable, WithName}

sealed trait NotableEvent

object NotableEvent extends Enumerable.Implicits {

  case object CreateMovementNotableEvent extends WithName("emcs-create-a-movement-ui") with NotableEvent

  case object ChangeDestinationNotableEvent extends WithName("emcs-change-a-destination-ui") with NotableEvent

  case object ReportAReceiptNotableEvent extends WithName("emcs-report-a-receipt-ui") with NotableEvent

  case object ExplainDelayNotableEvent extends WithName("emcs-explain-a-delay-ui") with NotableEvent

  case object ExplainShortageOrExcessNotableEvent extends WithName("emcs-explain-a-shortage-ui") with NotableEvent

  case object CancelMovementNotableEvent extends WithName("emcs-cancel-a-movement-ui") with NotableEvent

  case object AlertRejectNotableEvent extends WithName("emcs-submit-alert-or-rejection-ui") with NotableEvent

  val values: Seq[NotableEvent] = Seq(
    CreateMovementNotableEvent,
    ChangeDestinationNotableEvent,
    ReportAReceiptNotableEvent,
    ExplainDelayNotableEvent,
    ExplainShortageOrExcessNotableEvent,
    CancelMovementNotableEvent,
    AlertRejectNotableEvent
  )

  implicit val enumerable: Enumerable[NotableEvent] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
