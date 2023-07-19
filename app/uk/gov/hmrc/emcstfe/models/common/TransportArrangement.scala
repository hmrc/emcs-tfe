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

sealed trait TransportArrangement

object TransportArrangement extends Enumerable.Implicits {

  case object Consignor extends WithName("1") with TransportArrangement
  case object Consignee extends WithName("2") with TransportArrangement
  case object OwnerOfGoods extends WithName("3") with TransportArrangement
  case object Other extends WithName("4") with TransportArrangement

  val values: Seq[TransportArrangement] = Seq(
    Consignor, Consignee, OwnerOfGoods, Other
  )

  implicit val enumerable: Enumerable[TransportArrangement] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
