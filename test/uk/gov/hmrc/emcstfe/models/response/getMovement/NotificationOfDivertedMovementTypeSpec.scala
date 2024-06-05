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

import uk.gov.hmrc.emcstfe.models.response.getMovement.NotificationOfDivertedMovementType.{ChangeOfDestination, SplitMovement}
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

class NotificationOfDivertedMovementTypeSpec extends TestBaseSpec {

  "NotificationOfDivertedMovementType" should {

    "return the correct types" in {

      ChangeOfDestination.toString shouldBe "1"
      SplitMovement.toString shouldBe "2"
    }
  }

}
