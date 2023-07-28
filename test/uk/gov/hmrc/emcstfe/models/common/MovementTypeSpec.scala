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

import uk.gov.hmrc.emcstfe.models.common.MovementType._
import uk.gov.hmrc.emcstfe.support.UnitSpec

class MovementTypeSpec extends UnitSpec {

  "MovementType" should {

    "have the correct codes" in {
      UKtoUK.toString shouldBe "1"
      UKtoEU.toString shouldBe "2"
      DirectExport.toString shouldBe "3"
      ImportEU.toString shouldBe "4"
      ImportUK.toString shouldBe "5"
      IndirectExport.toString shouldBe "6"
      ImportDirectExport.toString shouldBe "7"
      ImportIndirectExport.toString shouldBe "8"
      ImportUnknownDestination.toString shouldBe "9"
    }
  }
}
