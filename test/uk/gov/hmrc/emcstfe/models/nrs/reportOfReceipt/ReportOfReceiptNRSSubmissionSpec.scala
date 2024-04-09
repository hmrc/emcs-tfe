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

package uk.gov.hmrc.emcstfe.models.nrs.reportOfReceipt

import uk.gov.hmrc.emcstfe.fixtures.{SubmitExplainShortageExcessFixtures, SubmitReportOfReceiptFixtures}
import uk.gov.hmrc.emcstfe.models.explainShortageExcess.SubmitExplainShortageExcessModel
import uk.gov.hmrc.emcstfe.models.reportOfReceipt.SubmitReportOfReceiptModel
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

class ReportOfReceiptNRSSubmissionSpec extends TestBaseSpec with SubmitExplainShortageExcessFixtures with SubmitReportOfReceiptFixtures {

  ".apply" should {

    s"generate the correct model from the $SubmitReportOfReceiptModel" in {

      ReportOfReceiptNRSSubmission
        .apply(maxSubmitReportOfReceiptModel, testErn) shouldBe reportOfReceiptNRSSubmission
    }
  }
}
