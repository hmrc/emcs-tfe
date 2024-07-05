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

sealed trait GlobalConclusionofReceiptReasonCodeType

object GlobalConclusionofReceiptReasonCodeType extends Enumerable.Implicits {

  case object ReceiptAcceptedAndSatisfactory extends WithName("1") with GlobalConclusionofReceiptReasonCodeType

  case object ReceiptAcceptedAlthoughUnsatisfactory extends WithName("2") with GlobalConclusionofReceiptReasonCodeType

  case object ReceiptRefused extends WithName("3") with GlobalConclusionofReceiptReasonCodeType

  case object ReceiptPartiallyRefused extends WithName("4") with GlobalConclusionofReceiptReasonCodeType

  case object ExitAcceptedAndSatisfactory extends WithName("21") with GlobalConclusionofReceiptReasonCodeType

  case object ExitAcceptedAlthoughUnsatisfactory extends WithName("22") with GlobalConclusionofReceiptReasonCodeType

  case object ExitRefused extends WithName("23") with GlobalConclusionofReceiptReasonCodeType

  val values: Seq[GlobalConclusionofReceiptReasonCodeType] = Seq(ReceiptAcceptedAndSatisfactory, ReceiptAcceptedAlthoughUnsatisfactory, ReceiptRefused, ReceiptPartiallyRefused, ExitAcceptedAndSatisfactory, ExitAcceptedAlthoughUnsatisfactory, ExitRefused)

  implicit val enumerable: Enumerable[GlobalConclusionofReceiptReasonCodeType] = Enumerable(values.map(v => v.toString -> v): _*)
}
