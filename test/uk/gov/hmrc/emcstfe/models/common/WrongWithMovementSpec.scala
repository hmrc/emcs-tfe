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

import uk.gov.hmrc.emcstfe.models.common.WrongWithMovement._
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

class WrongWithMovementSpec extends TestBaseSpec {

  ".apply" should {
    Seq(
      0 -> Other,
      1 -> Excess,
      2 -> Shortage,
      3 -> Damaged,
      4 -> BrokenSeals
    ).foreach { codeToStatus =>
      s"return ${codeToStatus._2}" when {

        s"the code is ${codeToStatus._1}" in {
          WrongWithMovement.apply(codeToStatus._1) shouldBe codeToStatus._2
        }

      }
    }
  }

}
