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

import uk.gov.hmrc.emcstfe.fixtures.GetMessageStatisticsFixtures
import uk.gov.hmrc.emcstfe.mocks.connectors.MockEisConnector
import uk.gov.hmrc.emcstfe.models.request.GetMessageStatisticsRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.EISUnknownError
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.concurrent.Future

class GetMessageStatisticsServiceSpec extends TestBaseSpec with GetMessageStatisticsFixtures with MockEisConnector {

  lazy val getMessageStatisticsRequest: GetMessageStatisticsRequest = GetMessageStatisticsRequest(testErn)
  lazy val service: GetMessageStatisticsService = new GetMessageStatisticsService(mockEisConnector)

  "getMessageStatistics" when {
    "return a Right" when {
      "connector call to EIS is successful and XML is the correct format" in {

        MockEisConnector.getMessageStatistics(getMessageStatisticsRequest).returns(
          Future.successful(Right(getMessageStatisticsResponseModel))
        )

        await(service.getMessageStatistics(getMessageStatisticsRequest)) shouldBe Right(getMessageStatisticsResponseModel)
      }
    }
    "return a Left" when {
      "connector call to EIS is unsuccessful" in {

        MockEisConnector.getMessageStatistics(getMessageStatisticsRequest).returns(
          Future.successful(Left(EISUnknownError("Downstream failed to respond")))
        )

        await(service.getMessageStatistics(getMessageStatisticsRequest)) shouldBe Left(EISUnknownError("Downstream failed to respond"))
      }
    }
  }
}
