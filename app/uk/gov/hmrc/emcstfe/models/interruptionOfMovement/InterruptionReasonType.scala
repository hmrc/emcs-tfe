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

package uk.gov.hmrc.emcstfe.models.interruptionOfMovement

import uk.gov.hmrc.emcstfe.models.common.{Enumerable, WithName}

sealed trait InterruptionReasonType

object InterruptionReasonType extends Enumerable.Implicits {

  case object Other extends WithName("0") with InterruptionReasonType
  case object FraudSuspected extends WithName("1") with InterruptionReasonType
  case object GoodsDestroyed extends WithName("2") with InterruptionReasonType
  case object GoodsLostOrStolen extends WithName("3") with InterruptionReasonType
  case object InterruptionRequestAtControl extends WithName("4") with InterruptionReasonType

  val values: Seq[InterruptionReasonType] = Seq(
    Other,
    FraudSuspected,
    GoodsDestroyed,
    GoodsLostOrStolen,
    InterruptionRequestAtControl
  )

  implicit val enumerable: Enumerable[InterruptionReasonType] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
