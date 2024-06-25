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

sealed trait CustomsRejectionReasonCodeType

object CustomsRejectionReasonCodeType extends Enumerable.Implicits {

  case object ImportDataNotFound extends WithName("1") with CustomsRejectionReasonCodeType

  case object ImportDataMismatch extends WithName("2") with CustomsRejectionReasonCodeType

  //PCP says this is export data not found but the schema says (reserved)
  case object ExportDataNotFound extends WithName("3") with CustomsRejectionReasonCodeType

  case object ExportDataMismatch extends WithName("4") with CustomsRejectionReasonCodeType

  case object RejectedAtExportProcedure extends WithName("5") with CustomsRejectionReasonCodeType

  val values: Seq[CustomsRejectionReasonCodeType] = Seq(ImportDataNotFound, ImportDataMismatch, ExportDataNotFound, ExportDataMismatch, RejectedAtExportProcedure)

  implicit val enumerable: Enumerable[CustomsRejectionReasonCodeType] = Enumerable(values.map(v => v.toString -> v): _*)
}
