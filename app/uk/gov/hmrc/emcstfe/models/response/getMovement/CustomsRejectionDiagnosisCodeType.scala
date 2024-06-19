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

sealed trait CustomsRejectionDiagnosisCodeType

object CustomsRejectionDiagnosisCodeType extends Enumerable.Implicits {

  //PCP says this is Unknown ARC not found but the schema says (reserved)
  case object UnknownArc extends WithName("1") with CustomsRejectionDiagnosisCodeType

  case object BodyRecordUniqueReferenceDoesNotExist extends WithName("2") with CustomsRejectionDiagnosisCodeType

  case object NoGoodsItemInDeclaration extends WithName("3") with CustomsRejectionDiagnosisCodeType

  case object WeightMismatch extends WithName("4") with CustomsRejectionDiagnosisCodeType

  case object DestinationTypeIsNotExport extends WithName("5") with CustomsRejectionDiagnosisCodeType

  case object CommodityCodesDoNotMatch extends WithName("6") with CustomsRejectionDiagnosisCodeType

  val values: Seq[CustomsRejectionDiagnosisCodeType] = Seq(UnknownArc, BodyRecordUniqueReferenceDoesNotExist, NoGoodsItemInDeclaration, WeightMismatch, DestinationTypeIsNotExport, CommodityCodesDoNotMatch)

  implicit val enumerable: Enumerable[CustomsRejectionDiagnosisCodeType] = Enumerable(values.map(v => v.toString -> v): _*)
}
