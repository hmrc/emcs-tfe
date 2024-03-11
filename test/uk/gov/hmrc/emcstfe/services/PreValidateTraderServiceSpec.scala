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

package uk.gov.hmrc.emcstfe.services

import uk.gov.hmrc.emcstfe.fixtures.PreValidateFixtures
import uk.gov.hmrc.emcstfe.mocks.config.MockAppConfig
import uk.gov.hmrc.emcstfe.mocks.connectors.MockEisConnector
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.EISUnknownError
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.concurrent.Future

class PreValidateTraderServiceSpec extends TestBaseSpec with PreValidateFixtures {

  trait Test extends MockEisConnector with MockAppConfig {
    val service: PreValidateTraderService = new PreValidateTraderService(mockEisConnector, mockAppConfig)
  }

  ".preValidateTrader" should {
    "return a Right" when {
      "connector call is successful and a valid JSON response" in new Test {

        MockEisConnector.preValidateTrader(preValidateApiRequestModel).returns(
            Future.successful(Right(preValidateApiResponseModel))
          )

        await(service.preValidateTrader(preValidateTraderModelRequest)) shouldBe Right(preValidateApiResponseModel)

      }
    }

    "return a Left" when {
      "connector call is unsuccessful" in new Test {

        MockEisConnector.preValidateTrader(preValidateApiRequestModel)
          .returns(
            Future.successful(Left(EISUnknownError("Downstream failed to respond")))
          )

        await(service.preValidateTrader(preValidateTraderModelRequest)) shouldBe Left(EISUnknownError("Downstream failed to respond"))
      }
    }
  }

}
