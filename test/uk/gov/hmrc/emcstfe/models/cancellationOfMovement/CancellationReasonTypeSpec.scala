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

package uk.gov.hmrc.emcstfe.models.cancellationOfMovement

import uk.gov.hmrc.emcstfe.fixtures.SubmitExplainDelayFixtures
import uk.gov.hmrc.emcstfe.models.cancellationOfMovement.CancellationReasonType._
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

class CancellationReasonTypeSpec extends TestBaseSpec with SubmitExplainDelayFixtures {

  "CancellationReasonType" must {

    "have the correct underlying enum values" in {
      Other.toString shouldBe "0"
      TypingError.toString shouldBe "1"
      SaleInterrupted.toString shouldBe "2"
      DuplicateMovement.toString shouldBe "3"
      LateMovement.toString shouldBe "4"
    }
  }
}
