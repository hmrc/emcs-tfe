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

import uk.gov.hmrc.emcstfe.models.common.GuarantorType._
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

class GuarantorTypeSpec extends TestBaseSpec {

  "GuarantorType" should {

    "have the correct codes" in {
      GuarantorNotRequired.toString shouldBe "0"
      Consignor.toString shouldBe "1"
      ConsignorTransporter.toString shouldBe "12"
      ConsignorTransporterOwner.toString shouldBe "123"
      ConsignorTransporterOwnerConsignee.toString shouldBe "1234"
      ConsignorTransporterConsignee.toString shouldBe "124"
      ConsignorOwner.toString shouldBe "13"
      ConsignorOwnerConsignee.toString shouldBe "134"
      JointConsignorConsignee.toString shouldBe "14"
      Transporter.toString shouldBe "2"
      TransporterOwner.toString shouldBe "23"
      TransporterOwnerConsignee.toString shouldBe "234"
      TransporterConsignee.toString shouldBe "24"
      Owner.toString shouldBe "3"
      OwnerConsignee.toString shouldBe "34"
      Consignee.toString shouldBe "4"
      NoGuarantor.toString shouldBe "5"
    }
  }
}
