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

sealed trait WrongWithMovement

object WrongWithMovement extends Enumerable.Implicits {

  case object Shortage extends WithName("shortage") with WrongWithMovement
  case object Excess extends WithName("excess") with WrongWithMovement
  case object Damaged extends WithName("damaged") with WrongWithMovement
  case object BrokenSeals extends WithName("brokenSeals") with WrongWithMovement
  case object Other extends WithName("other") with WrongWithMovement

  val values: Seq[WrongWithMovement] = Seq(
    Shortage,
    Excess,
    Damaged,
    BrokenSeals,
    Other
  )

  implicit val enumerable: Enumerable[WrongWithMovement] =
    Enumerable(values.distinct.map(v => v.toString -> v): _*)
}
