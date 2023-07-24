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

package uk.gov.hmrc.emcstfe.models.alertOrRejection

import uk.gov.hmrc.emcstfe.models.common.{Enumerable, WithName}

sealed trait AlertOrRejectionReasonType

object AlertOrRejectionReasonType extends Enumerable.Implicits {

  case object Other extends WithName("0") with AlertOrRejectionReasonType
  case object EADNotConcernRecipient extends WithName("1") with AlertOrRejectionReasonType
  case object ProductDoesNotMatchOrder extends WithName("2") with AlertOrRejectionReasonType
  case object QuantityDoesNotMatchOrder extends WithName("3") with AlertOrRejectionReasonType

  val values: Seq[AlertOrRejectionReasonType] = Seq(
    Other,
    EADNotConcernRecipient,
    ProductDoesNotMatchOrder,
    QuantityDoesNotMatchOrder
  )

  implicit val enumerable: Enumerable[AlertOrRejectionReasonType] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
