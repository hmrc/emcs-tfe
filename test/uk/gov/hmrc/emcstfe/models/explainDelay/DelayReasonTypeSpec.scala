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

package uk.gov.hmrc.emcstfe.models.explainDelay

import uk.gov.hmrc.emcstfe.fixtures.SubmitExplainDelayFixtures
import uk.gov.hmrc.emcstfe.models.explainDelay.DelayReasonType._
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

class DelayReasonTypeSpec extends TestBaseSpec with SubmitExplainDelayFixtures {

  "DelayReasonType" must {

    "have the correct underlying enum values" in {
      Other.toString shouldBe "0"
      CancelledCommercialTransaction.toString shouldBe "1"
      PendingCommercialTransaction.toString shouldBe "2"
      OngoingInvestigation.toString shouldBe "3"
      BadWeather.toString shouldBe "4"
      Strikes.toString shouldBe "5"
      Accident.toString shouldBe "6"
    }
  }
}
