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

import uk.gov.hmrc.emcstfe.models.common.{Enumerable, WithName}

sealed trait ManualClosureRequestReasonCodeType

object ManualClosureRequestReasonCodeType extends Enumerable.Implicits {

  case object Other extends WithName("0") with ManualClosureRequestReasonCodeType

  case object ExportClosedButNoIE518Available extends WithName("1") with ManualClosureRequestReasonCodeType

  case object ConsigneeNoLongerConnectedToEMCS extends WithName("2") with ManualClosureRequestReasonCodeType

  case object ExemptedConsignee extends WithName("3") with ManualClosureRequestReasonCodeType

  case object ExitConfirmedButNoIE829Submitted extends WithName("4") with ManualClosureRequestReasonCodeType

  case object NoMovementButCancellationNoLongerPossible extends WithName("5") with ManualClosureRequestReasonCodeType

  case object MultipleIssuancesOfeADsOreSADsForASingleMovement extends WithName("6") with ManualClosureRequestReasonCodeType

  case object eADOreSADDoesNotCoverActualMovement extends WithName("7") with ManualClosureRequestReasonCodeType

  case object ErroneousReportOfReceipt extends WithName("8") with ManualClosureRequestReasonCodeType

  case object ErroneousRejectionOfAneADOreSAD extends WithName("9") with ManualClosureRequestReasonCodeType

  val values: Seq[ManualClosureRequestReasonCodeType] = Seq(Other, ExportClosedButNoIE518Available, ConsigneeNoLongerConnectedToEMCS, ExemptedConsignee, ExitConfirmedButNoIE829Submitted, NoMovementButCancellationNoLongerPossible, MultipleIssuancesOfeADsOreSADsForASingleMovement, eADOreSADDoesNotCoverActualMovement, ErroneousReportOfReceipt, ErroneousRejectionOfAneADOreSAD)

      implicit val enumerable: Enumerable[ManualClosureRequestReasonCodeType] = Enumerable(values.map(v => v.toString -> v): _*)
}
