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

package uk.gov.hmrc.emcstfe.models.common

sealed trait AcceptMovement

object AcceptMovement extends Enumerable.Implicits {

  case object Satisfactory extends WithName("satisfactory") with AcceptMovement
  case object Unsatisfactory extends WithName("unsatisfactory") with AcceptMovement
  case object Refused extends WithName("refused") with AcceptMovement
  case object PartiallyRefused extends WithName("partiallyRefused") with AcceptMovement

  val values: Seq[AcceptMovement] = Seq(
    Satisfactory, Unsatisfactory, Refused, PartiallyRefused
  )

  def apply(globalConclusion: Int): AcceptMovement = globalConclusion match {
    case 1 | 21 => Satisfactory
    case 2 | 22 => Unsatisfactory
    case 3 | 23 => Refused
    case 4 => PartiallyRefused
  }

  implicit val enumerable: Enumerable[AcceptMovement] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
