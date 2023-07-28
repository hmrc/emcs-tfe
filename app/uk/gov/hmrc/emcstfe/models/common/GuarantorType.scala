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

sealed trait GuarantorType

object GuarantorType extends Enumerable.Implicits {

  case object Consignor extends WithName("1") with GuarantorType
  case object ConsignorTransporter extends WithName("12") with GuarantorType
  case object ConsignorTransporterOwner extends WithName("123") with GuarantorType
  case object ConsignorTransporterOwnerConsignee extends WithName("1234") with GuarantorType
  case object ConsignorTransporterConsignee extends WithName("124") with GuarantorType
  case object ConsignorOwner extends WithName("13") with GuarantorType
  case object ConsignorOwnerConsignee extends WithName("134") with GuarantorType
  case object JointConsignorConsignee extends WithName("14") with GuarantorType
  case object Transporter extends WithName("2") with GuarantorType
  case object TransporterOwner extends WithName("23") with GuarantorType
  case object TransporterOwnerConsignee extends WithName("234") with GuarantorType
  case object TransporterConsignee extends WithName("24") with GuarantorType
  case object Owner extends WithName("3") with GuarantorType
  case object OwnerConsignee extends WithName("34") with GuarantorType
  case object Consignee extends WithName("4") with GuarantorType
  case object NoGuarantor extends WithName("5") with GuarantorType

  val values: Seq[GuarantorType] = Seq(
    Consignor,
    ConsignorTransporter,
    ConsignorTransporterOwner,
    ConsignorTransporterOwnerConsignee,
    ConsignorTransporterConsignee,
    ConsignorOwner,
    ConsignorOwnerConsignee,
    JointConsignorConsignee,
    Transporter,
    TransporterOwner,
    TransporterOwnerConsignee,
    TransporterConsignee,
    Owner,
    OwnerConsignee,
    Consignee,
    NoGuarantor
  )

  implicit val enumerable: Enumerable[GuarantorType] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
