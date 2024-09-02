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

package uk.gov.hmrc.emcstfe.services

import uk.gov.hmrc.emcstfe.fixtures.SubmitAlertOrRejectionFixtures
import uk.gov.hmrc.emcstfe.mocks.connectors.MockEisConnector
import uk.gov.hmrc.emcstfe.models.request.SubmitAlertOrRejectionRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.EISUnknownError
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.concurrent.Future

class SubmitAlertOrRejectionServiceSpec extends TestBaseSpec with SubmitAlertOrRejectionFixtures {

  trait Test extends MockEisConnector {
    val submitAlertOrRejectionRequest: SubmitAlertOrRejectionRequest = SubmitAlertOrRejectionRequest(maxSubmitAlertOrRejectionModel)
    val service: SubmitAlertOrRejectionService = new SubmitAlertOrRejectionService(mockEisConnector)
  }

  "SubmitAlertOrRejectionService" when {
    "when calling submitViaEIS" must {
      "return a Right" when {
        "connector call is successful and XML is the correct format" in new Test {

          MockEisConnector.submit(submitAlertOrRejectionRequest).returns(Future.successful(Right(eisSuccessResponse)))

          await(service.submitViaEIS(maxSubmitAlertOrRejectionModel)) shouldBe Right(eisSuccessResponse)
        }
      }
      "return a Left" when {
        "connector call is unsuccessful" in new Test {

          MockEisConnector.submit(submitAlertOrRejectionRequest).returns(Future.successful(Left(EISUnknownError("Downstream failed to respond"))))

          await(service.submitViaEIS(maxSubmitAlertOrRejectionModel)) shouldBe Left(EISUnknownError("Downstream failed to respond"))
        }
      }
    }
  }
}
