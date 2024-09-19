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

import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.featureswitch.core.config.{EnableKnownFactsViaETDS18, FeatureSwitching}
import uk.gov.hmrc.emcstfe.fixtures.TraderKnownFactsFixtures
import uk.gov.hmrc.emcstfe.mocks.connectors.{MockEisConnector, MockTraderKnownFactsConnector}
import uk.gov.hmrc.emcstfe.models.request.eis.TraderKnownFactsETDS18Request
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.UnexpectedDownstreamResponseError
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.concurrent.Future

class TraderKnownFactsServiceSpec extends TestBaseSpec with TraderKnownFactsFixtures {

  trait Test extends MockTraderKnownFactsConnector with MockEisConnector with FeatureSwitching {
    lazy val config: AppConfig           = app.injector.instanceOf[AppConfig]
    val service: TraderKnownFactsService = new TraderKnownFactsService(mockTraderKnownFactsConnector, mockEisConnector, config)
  }

  "getTraderKnownFacts" when {
    "calling ETDS" should {

      "return a Right" when {
        "connector call is successful and JSON is in the correct format" in new Test {
          enable(EnableKnownFactsViaETDS18)

          MockEisConnector
            .getTraderKnownFactsViaETDS18(TraderKnownFactsETDS18Request(userRequest))
            .returns(Future.successful(Right(Some(testTraderKnownFactsModel))))

          await(service.getTraderKnownFacts(testErn)) shouldBe Right(Some(testTraderKnownFactsModel))
        }
      }

      "return a Left" when {
        "connector call is unsuccessful" in new Test {
          enable(EnableKnownFactsViaETDS18)

          MockEisConnector
            .getTraderKnownFactsViaETDS18(TraderKnownFactsETDS18Request(userRequest))
            .returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

          await(service.getTraderKnownFacts(testErn)) shouldBe Left(UnexpectedDownstreamResponseError)
        }
      }
    }
    "calling emcs-tfe-reference-data" should {

      "return a Right" when {
        "connector call is successful and JSON is in the correct format" in new Test {
          disable(EnableKnownFactsViaETDS18)

          MockTraderKnownFactsConnector
            .getTraderKnownFactsViaReferenceData(testErn)
            .returns(Future.successful(Right(Some(testTraderKnownFactsModel))))

          await(service.getTraderKnownFacts(testErn)) shouldBe Right(Some(testTraderKnownFactsModel))
        }
      }

      "return a Left" when {
        "connector call is unsuccessful" in new Test {
          disable(EnableKnownFactsViaETDS18)

          MockTraderKnownFactsConnector
            .getTraderKnownFactsViaReferenceData(testErn)
            .returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

          await(service.getTraderKnownFacts(testErn)) shouldBe Left(UnexpectedDownstreamResponseError)
        }
      }
    }
  }

}
