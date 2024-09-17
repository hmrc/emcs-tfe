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

package uk.gov.hmrc.emcstfe.connectors

import org.scalatest.BeforeAndAfterEach
import play.api.http.{HeaderNames, MimeTypes, Status}
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.featureswitch.core.config.FeatureSwitching
import uk.gov.hmrc.emcstfe.fixtures.TraderKnownFactsFixtures
import uk.gov.hmrc.emcstfe.mocks.connectors.MockHttpClient
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.UnexpectedDownstreamResponseError
import uk.gov.hmrc.emcstfe.support.UnitSpec

import scala.concurrent.Future

class TraderKnownFactsConnectorSpec extends UnitSpec
  with Status
  with MimeTypes
  with HeaderNames
  with MockHttpClient
  with FeatureSwitching
  with BeforeAndAfterEach
  with TraderKnownFactsFixtures {

  lazy val config: AppConfig = app.injector.instanceOf[AppConfig]

  override def afterEach(): Unit = {
    super.afterEach()
  }

  val connector = new TraderKnownFactsConnector(mockHttpClient, config)

  "getTraderKnownFacts" should {
    "return Right[TraderKnownFacts]" when {
      "the http client response returns Right[TraderKnownFacts]" in {
        val ern = "1234567890"

        MockHttpClient.get(config.knownFactsCandEUrl(ern)).returns(Future.successful(Right(testTraderKnownFactsModel)))

        val result = await(connector.getTraderKnownFacts(ern))

        result shouldBe Right(testTraderKnownFactsModel)
      }
    }

    "return Left[ErrorResponse]" when {
      "the http client response returns Left[ErrorResponse]" in {
        val ern = "1234567890"

        MockHttpClient.get(config.knownFactsCandEUrl(ern)).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        val result = await(connector.getTraderKnownFacts(ern))

        result shouldBe Left(UnexpectedDownstreamResponseError)
      }
    }
  }

}
