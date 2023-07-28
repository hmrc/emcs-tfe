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

package uk.gov.hmrc.emcstfe.models.common;


sealed trait MovementType

object MovementType extends Enumerable.Implicits {

  case object UKtoUK extends WithName("1") with MovementType
  case object UKtoEU extends WithName("2") with MovementType
  case object DirectExport extends WithName("3") with MovementType
  case object ImportEU extends WithName("4") with MovementType
  case object ImportUK extends WithName("5") with MovementType
  case object IndirectExport extends WithName("6") with MovementType
  case object ImportDirectExport extends WithName("7") with MovementType
  case object ImportIndirectExport extends WithName("8") with MovementType
  case object ImportUnknownDestination extends WithName("9") with MovementType

  val values: Seq[MovementType] = Seq(
    UKtoUK,
    UKtoEU,
    DirectExport,
    ImportEU,
    ImportUK,
    IndirectExport,
    ImportDirectExport,
    ImportIndirectExport,
    ImportUnknownDestination
  )

  implicit val enumerable: Enumerable[MovementType] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
