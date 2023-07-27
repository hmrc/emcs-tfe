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

package uk.gov.hmrc.emcstfe.models.cancellationOfMovement

import uk.gov.hmrc.emcstfe.models.common.{Enumerable, WithName}

sealed trait CancellationReasonType

object CancellationReasonType extends Enumerable.Implicits {

  case object Other extends WithName("0") with CancellationReasonType
  case object TypingError extends WithName("1") with CancellationReasonType
  case object SaleInterrupted extends WithName("2") with CancellationReasonType
  case object DuplicateMovement extends WithName("3") with CancellationReasonType
  case object LateMovement extends WithName("4") with CancellationReasonType

  val values: Seq[CancellationReasonType] = Seq(
    Other,
    TypingError,
    SaleInterrupted,
    DuplicateMovement,
    LateMovement
  )

  implicit val enumerable: Enumerable[CancellationReasonType] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
