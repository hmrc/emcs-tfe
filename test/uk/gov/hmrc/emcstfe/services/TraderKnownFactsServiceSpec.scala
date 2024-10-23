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
import uk.gov.hmrc.emcstfe.mocks.repository.MockKnownFactsRepository
import uk.gov.hmrc.emcstfe.models.mongo.KnownFacts
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.UnexpectedDownstreamResponseError
import uk.gov.hmrc.emcstfe.support.TestBaseSpec
import uk.gov.hmrc.play.bootstrap.tools.LogCapturing

import scala.concurrent.Future

class TraderKnownFactsServiceSpec extends TestBaseSpec
  with TraderKnownFactsFixtures
  with MockTraderKnownFactsConnector
  with MockEisConnector
  with FeatureSwitching
  with MockKnownFactsRepository
  with LogCapturing {

    lazy val config: AppConfig           = app.injector.instanceOf[AppConfig]
    lazy val service: TraderKnownFactsService = new TraderKnownFactsService(
      mockTraderKnownFactsConnector,
      mockEisConnector,
      mockKnownFactsRepository,
      config
    )

  "getTraderKnownFacts" when {
    "calling ETDS" when {
      "a known facts response exists in the cache" when {
        "the call to retrieve from Mongo is successful" should {
          "return from the cache without calling ETDS18" in {

            enable(EnableKnownFactsViaETDS18)

            MockKnownFactsRepository.get(testErn).returns(Future.successful(Some(KnownFacts(testErn, testTraderKnownFactsModel))))
            MockEisConnector.getTraderKnownFactsViaETDS18(testErn).never()

            await(service.getTraderKnownFacts(testErn)) shouldBe Right(Some(testTraderKnownFactsModel))
          }
        }
        "the call to retrieve from Mongo fails" should {
          "attempt to retrieve from ETDS18 (logging the mongo future failure as an error)" in {

            enable(EnableKnownFactsViaETDS18)

            MockKnownFactsRepository.get(testErn).returns(Future.failed(new Exception("bang")))
            MockKnownFactsRepository.set(testErn, testTraderKnownFactsModel).returns(Future.successful(true))

            MockEisConnector
              .getTraderKnownFactsViaETDS18(testErn)
              .returns(Future.successful(Right(Some(testTraderKnownFactsModel))))

            withCaptureOfLoggingFrom(service.logger) { logs =>
              await(service.getTraderKnownFacts(testErn)) shouldBe Right(Some(testTraderKnownFactsModel))
              logs.exists(_.getMessage.contains("Unexpected response from Mongo, attempting to retrieve latest Known Facts from ETDS18")) shouldBe true
            }
          }
        }
      }
      "a known facts response does NOT exist in the cache" should {
        "return a Right" when {
          "connector call is successful and JSON is in the correct format (storing the KF response successfully)" in {
            enable(EnableKnownFactsViaETDS18)

            MockKnownFactsRepository.get(testErn).returns(Future.successful(None))
            MockKnownFactsRepository.set(testErn, testTraderKnownFactsModel).returns(Future.successful(true))

            MockEisConnector
              .getTraderKnownFactsViaETDS18(testErn)
              .returns(Future.successful(Right(Some(testTraderKnownFactsModel))))

            await(service.getTraderKnownFacts(testErn)) shouldBe Right(Some(testTraderKnownFactsModel))
          }
        }

        "return a Left" when {
          "connector call is unsuccessful" in {
            enable(EnableKnownFactsViaETDS18)

            MockKnownFactsRepository.get(testErn).returns(Future.successful(None))

            MockEisConnector
              .getTraderKnownFactsViaETDS18(testErn)
              .returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

            await(service.getTraderKnownFacts(testErn)) shouldBe Left(UnexpectedDownstreamResponseError)
          }

          "connector fails future (logging an warning message)" in {
            enable(EnableKnownFactsViaETDS18)

            MockKnownFactsRepository.get(testErn).returns(Future.successful(None))

            MockEisConnector
              .getTraderKnownFactsViaETDS18(testErn)
              .returns(Future.failed(new Exception("bang")))

            withCaptureOfLoggingFrom(service.logger) { logs =>
              await(service.getTraderKnownFacts(testErn)) shouldBe Left(UnexpectedDownstreamResponseError)
              logs.exists(_.getMessage.contains("Unexpected error when retrieving known facts from ETDS18")) shouldBe true
            }
          }
        }
      }
    }
    "calling emcs-tfe-reference-data" should {

      "return a Right" when {
        "connector call is successful and JSON is in the correct format" in {
          disable(EnableKnownFactsViaETDS18)

          MockTraderKnownFactsConnector
            .getTraderKnownFactsViaReferenceData(testErn)
            .returns(Future.successful(Right(Some(testTraderKnownFactsModel))))

          await(service.getTraderKnownFacts(testErn)) shouldBe Right(Some(testTraderKnownFactsModel))
        }
      }

      "return a Left" when {
        "connector call is unsuccessful" in {
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
