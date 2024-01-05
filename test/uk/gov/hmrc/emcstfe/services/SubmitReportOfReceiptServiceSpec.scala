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

import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.emcstfe.fixtures.SubmitReportOfReceiptFixtures
import uk.gov.hmrc.emcstfe.mocks.config.MockAppConfig
import uk.gov.hmrc.emcstfe.mocks.connectors.{MockChrisConnector, MockEisConnector}
import uk.gov.hmrc.emcstfe.mocks.services.MockMetricsService
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.common.AcceptMovement.{PartiallyRefused, Refused, Satisfactory, Unsatisfactory}
import uk.gov.hmrc.emcstfe.models.request.SubmitReportOfReceiptRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{EISUnknownError, XmlValidationError}
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.concurrent.Future

class SubmitReportOfReceiptServiceSpec extends TestBaseSpec with SubmitReportOfReceiptFixtures {

  trait Test extends MockChrisConnector with MockEisConnector with MockAppConfig with MockMetricsService {
    implicit val request: UserRequest[AnyContentAsEmpty.type] = UserRequest(FakeRequest(), testErn, testInternalId, testCredId)
    val submitReportOfReceiptRequest: SubmitReportOfReceiptRequest = SubmitReportOfReceiptRequest(maxSubmitReportOfReceiptModel)

    val service: SubmitReportOfReceiptService = new SubmitReportOfReceiptService(mockChrisConnector, mockEisConnector, mockAppConfig, mockMetricsService)
  }

  "submit" when {

    "calling EIS" should {

      "return a Right" when {
        "connector call is successful and Json is the correct format" when {

          Seq(
            PartiallyRefused -> "partially-refused",
            Refused -> "refused",
            Satisfactory -> "satisfactory",
            Unsatisfactory -> "unsatisfactory"
          ).foreach { statusAndMetricName =>

            s"status is ${statusAndMetricName._2}" in new Test {

              val model = maxSubmitReportOfReceiptModel.copy(acceptMovement = statusAndMetricName._1)

              override val submitReportOfReceiptRequest: SubmitReportOfReceiptRequest =
                SubmitReportOfReceiptRequest(model)

              MockEisConnector
                .submit(submitReportOfReceiptRequest)
                .returns(Future.successful(Right(eisSuccessResponse)))

              MockMetricsService.rorStatusCounter(statusAndMetricName._2)

              await(service.submitViaEIS(model)) shouldBe Right(eisSuccessResponse)


            }
          }
        }

        "return a Left" when {
          "connector call is unsuccessful" in new Test {

            MockEisConnector
              .submit(submitReportOfReceiptRequest)
              .returns(Future.successful(Left(EISUnknownError("Downstream failed to respond"))))

            MockMetricsService.rorStatusCounter("failed-submission")

            await(service.submitViaEIS(maxSubmitReportOfReceiptModel)) shouldBe Left(EISUnknownError("Downstream failed to respond"))
          }
        }
      }
    }

    "calling ChRIS" should {

      "return a Right" when {
        "connector call is successful and XML is the correct format" when {

          Seq(
            PartiallyRefused -> "partially-refused",
            Refused -> "refused",
            Satisfactory -> "satisfactory",
            Unsatisfactory -> "unsatisfactory"
          ).foreach { statusAndMetricName =>

            s"status is ${statusAndMetricName._2}" in new Test {

              val model = maxSubmitReportOfReceiptModel.copy(acceptMovement = statusAndMetricName._1)

              override val submitReportOfReceiptRequest: SubmitReportOfReceiptRequest =
                SubmitReportOfReceiptRequest(model)

              MockChrisConnector
                .submitReportOfReceiptChrisSOAPRequest(submitReportOfReceiptRequest)
                .returns(Future.successful(Right(chrisSuccessResponse)))

              MockMetricsService.rorStatusCounter(statusAndMetricName._2)

              await(service.submit(model)) shouldBe Right(chrisSuccessResponse)
            }
          }
        }

        "return a Left" when {
          "connector call is unsuccessful" in new Test {

            MockChrisConnector
              .submitReportOfReceiptChrisSOAPRequest(submitReportOfReceiptRequest)
              .returns(Future.successful(Left(XmlValidationError)))

            MockMetricsService.rorStatusCounter("failed-submission")

            await(service.submit(maxSubmitReportOfReceiptModel)) shouldBe Left(XmlValidationError)
          }
        }
      }
    }


  }
}
