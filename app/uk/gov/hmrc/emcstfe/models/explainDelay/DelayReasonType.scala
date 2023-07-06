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

package uk.gov.hmrc.emcstfe.models.explainDelay

import uk.gov.hmrc.emcstfe.models.common.{Enumerable, WithName}

sealed trait DelayReasonType

object DelayReasonType extends Enumerable.Implicits {

  case object Other extends WithName("0") with DelayReasonType
  case object CancelledCommercialTransaction extends WithName("1") with DelayReasonType
  case object PendingCommercialTransaction extends WithName("2") with DelayReasonType
  case object OngoingInvestigation extends WithName("3") with DelayReasonType
  case object BadWeather extends WithName("4") with DelayReasonType
  case object Strikes extends WithName("5") with DelayReasonType
  case object Accident extends WithName("6") with DelayReasonType

  val values: Seq[DelayReasonType] = Seq(
    Other,
    CancelledCommercialTransaction,
    PendingCommercialTransaction,
    OngoingInvestigation,
    BadWeather,
    Strikes,
    Accident
  )

  implicit val enumerable: Enumerable[DelayReasonType] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
