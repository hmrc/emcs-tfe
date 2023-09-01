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

import play.api.test.FakeRequest
import uk.gov.hmrc.emcstfe.fixtures.SubmitReportOfReceiptFixtures
import uk.gov.hmrc.emcstfe.mocks.config.MockAppConfig
import uk.gov.hmrc.emcstfe.mocks.connectors.MockChrisConnector
import uk.gov.hmrc.emcstfe.mocks.services.MockMetricsService
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.common.AcceptMovement.{Refused, Satisfactory, Unsatisfactory}
import uk.gov.hmrc.emcstfe.models.request.SubmitReportOfReceiptRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.XmlValidationError
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.concurrent.Future

class SubmitReportOfReceiptServiceSpec extends TestBaseSpec with SubmitReportOfReceiptFixtures with MockMetricsService {
  trait Test extends MockChrisConnector with MockAppConfig {
    implicit val request = UserRequest(FakeRequest(), testErn, testInternalId, testCredId)
    val submitReportOfReceiptRequest: SubmitReportOfReceiptRequest = SubmitReportOfReceiptRequest(maxSubmitReportOfReceiptModel)
    val service: SubmitReportOfReceiptService = new SubmitReportOfReceiptService(mockConnector, mockAppConfig, mockMetricsService)
  }

  "submit" should {
    "return a Right" when {
      "connector call is successful and XML is the correct format" when {
        "status is partially-refused" in new Test {

          MockConnector
            .submitReportOfReceiptChrisSOAPRequest(submitReportOfReceiptRequest)
            .returns(Future.successful(Right(chrisSuccessResponse)))

          MockMetricsService.rorStatusCounter("partially-refused")

          await(service.submit(maxSubmitReportOfReceiptModel)) shouldBe Right(chrisSuccessResponse)
        }

        "status is refused" in new Test {

          val model = maxSubmitReportOfReceiptModel.copy(acceptMovement = Refused)

          override val submitReportOfReceiptRequest: SubmitReportOfReceiptRequest =
            SubmitReportOfReceiptRequest(model)

          MockConnector
            .submitReportOfReceiptChrisSOAPRequest(submitReportOfReceiptRequest)
            .returns(Future.successful(Right(chrisSuccessResponse)))

          MockMetricsService.rorStatusCounter("refused")

          await(service.submit(model)) shouldBe Right(chrisSuccessResponse)
        }
        "status is satisfactory" in new Test {

          val model = maxSubmitReportOfReceiptModel.copy(acceptMovement = Satisfactory)

          override val submitReportOfReceiptRequest: SubmitReportOfReceiptRequest = SubmitReportOfReceiptRequest(model)

          MockConnector
            .submitReportOfReceiptChrisSOAPRequest(submitReportOfReceiptRequest)
            .returns(Future.successful(Right(chrisSuccessResponse)))

          MockMetricsService.rorStatusCounter("satisfactory")

          await(service.submit(model)) shouldBe Right(chrisSuccessResponse)
        }
        "status is unsatisfactory" in new Test {

          val model = maxSubmitReportOfReceiptModel.copy(acceptMovement = Unsatisfactory)

          override val submitReportOfReceiptRequest: SubmitReportOfReceiptRequest =
            SubmitReportOfReceiptRequest(model)

          MockConnector
            .submitReportOfReceiptChrisSOAPRequest(submitReportOfReceiptRequest)
            .returns(Future.successful(Right(chrisSuccessResponse)))

          MockMetricsService.rorStatusCounter("unsatisfactory")

          await(service.submit(model)) shouldBe Right(chrisSuccessResponse)
        }
      }
    }
    "return a Left" when {
      "connector call is unsuccessful" in new Test {


        MockConnector
          .submitReportOfReceiptChrisSOAPRequest(submitReportOfReceiptRequest)
          .returns(Future.successful(Left(XmlValidationError)))

        MockMetricsService.rorStatusCounter("failed-submission")

        await(service.submit(maxSubmitReportOfReceiptModel)) shouldBe Left(XmlValidationError)
      }
    }
  }
}
