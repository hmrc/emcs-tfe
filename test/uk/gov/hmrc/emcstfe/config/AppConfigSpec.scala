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

package uk.gov.hmrc.emcstfe.config

import uk.gov.hmrc.emcstfe.featureswitch.core.config.{FeatureSwitching, UseDownstreamStub}
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.concurrent.duration.Duration

class AppConfigSpec extends TestBaseSpec with FeatureSwitching {

  lazy val config = app.injector.instanceOf[AppConfig]

  "AppConfig" must {

    "have a TTL for ReportAReceipt UserAnswers" in {
      //note: In app-config-base this will be set to the actual TTL for Environments including Production
      config.reportReceiptUserAnswersTTL() shouldBe Duration("15minutes")
    }

    ".chris url()" should {
      "when UseDownstreamStub is enabled" should  {

        "must return to the legacy URL" in {
          enable(UseDownstreamStub)
          config.urlEMCSApplicationService() shouldBe s"http://localhost:8308/ChRISOSB/EMCS/EMCSApplicationService/2"
          config.urlSubmitCreateMovement() shouldBe s"http://localhost:8308/ChRIS/EMCS/SubmitDraftMovementPortal/3"
          config.urlSubmitReportOfReceipt() shouldBe s"http://localhost:8308/ChRIS/EMCS/SubmitReportofReceiptPortal/4"
        }
      }

      "when UseDownstreamStub is disabled" should {

        "must return to the new URL" in {
          disable(UseDownstreamStub)
          config.urlEMCSApplicationService() shouldBe s"http://localhost:8308/ChRISOSB/EMCS/EMCSApplicationService/2"
          config.urlSubmitCreateMovement() shouldBe s"http://localhost:8308/ChRIS/EMCS/SubmitDraftMovementPortal/3"
          config.urlSubmitReportOfReceipt() shouldBe s"http://localhost:8308/ChRIS/EMCS/SubmitReportofReceiptPortal/4"
        }
      }
    }

    ".eis url()" should {
      "when UseDownstreamStub is enabled" should {

        "must return to the legacy URL" in {
          enable(UseDownstreamStub)
          config.urlSubmitReportOfReceiptEis() shouldBe s"http://localhost:8308/emcs/digital-submit-new-message/v1"
        }
      }

      "when UseDownstreamStub is disabled" should {

        "must return to the new URL" in {
          disable(UseDownstreamStub)
          config.urlSubmitReportOfReceiptEis() shouldBe s"http://localhost:8308/emcs/digital-submit-new-message/v1"
        }
      }
    }
  }

}
