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

package uk.gov.hmrc.emcstfe.models.nrs

import uk.gov.hmrc.emcstfe.models.nrs.NotableEvent._
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

class NotableEventSpec extends TestBaseSpec {

  "NotableEvent" must {

    "have the correct underlying enum values" in {
      CreateMovementNotableEvent.toString shouldBe "emcs-create-a-movement-ui"
      ChangeDestinationNotableEvent.toString shouldBe "emcs-change-a-destination-ui"
      ReportAReceiptNotableEvent.toString shouldBe "emcs-report-a-receipt-ui"
      ExplainDelayNotableEvent.toString shouldBe "emcs-explain-a-delay-ui"
      ExplainShortageOrExcessNotableEvent.toString shouldBe "emcs-explain-a-shortage-ui"
      CancelMovementNotableEvent.toString shouldBe "emcs-cancel-a-movement-ui"
      AlertRejectNotableEvent.toString shouldBe "emcs-submit-alert-or-rejection-ui"
    }
  }
}
