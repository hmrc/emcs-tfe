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

sealed trait OriginType

object OriginType extends Enumerable.Implicits {

  case object TaxWarehouse extends WithName("1") with OriginType
  case object Import extends WithName("2") with OriginType
  case object DutyPaid extends WithName("3") with OriginType

  val values: Seq[OriginType] = Seq(
    TaxWarehouse,
    Import,
    DutyPaid
  )

  implicit val enumerable: Enumerable[OriginType] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
