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

package uk.gov.hmrc.emcstfe.models.nrs.explainShortageExcess

import uk.gov.hmrc.emcstfe.fixtures.SubmitExplainShortageExcessFixtures
import uk.gov.hmrc.emcstfe.models.common.SubmitterType.{Consignee, Consignor}
import uk.gov.hmrc.emcstfe.models.explainShortageExcess.SubmitExplainShortageExcessModel
import uk.gov.hmrc.emcstfe.models.nrs.NotableEvent.ExplainShortageOrExcessNotableEvent
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

class ExplainShortageExcessNRSSubmissionSpec extends TestBaseSpec with SubmitExplainShortageExcessFixtures {

  ".apply" should {

    s"generate the correct model from the $SubmitExplainShortageExcessModel" in {

      ExplainShortageExcessNRSSubmission(SubmitExplainShortageExcessFixtures.submitExplainShortageExcessModelMax(Consignee), testErn) shouldBe
        explainShortageExcessNRSSubmission(Consignee, testErn)

      ExplainShortageExcessNRSSubmission(SubmitExplainShortageExcessFixtures.submitExplainShortageExcessModelMax(Consignor), testErn) shouldBe
        explainShortageExcessNRSSubmission(Consignor, testErn)
    }

    "have the correct notableEvent" in {
      ExplainShortageExcessNRSSubmission(SubmitExplainShortageExcessFixtures.submitExplainShortageExcessModelMax(Consignor), testErn).notableEvent shouldBe
        ExplainShortageOrExcessNotableEvent
    }
  }
}
