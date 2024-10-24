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

package uk.gov.hmrc.emcstfe.repositories

import org.mongodb.scala.Document
import uk.gov.hmrc.emcstfe.fixtures.{BaseFixtures, TraderKnownFactsFixtures}
import uk.gov.hmrc.emcstfe.models.mongo.KnownFacts
import uk.gov.hmrc.emcstfe.utils.TimeMachine

import java.time.Instant
import java.time.temporal.ChronoUnit

class KnownFactsRepositorySpec extends RepositoryBaseSpec[KnownFacts] with BaseFixtures with TraderKnownFactsFixtures {

  override def checkTtlIndex: Boolean = false

  private val instantNow = Instant.now.truncatedTo(ChronoUnit.MILLIS)
  private val timeMachine: TimeMachine = () => instantNow

  val knownFacts: KnownFacts = KnownFacts(
    ern = testErn,
    knownFacts = testTraderKnownFactsModel,
    lastUpdated = Instant.ofEpochSecond(1)
  )

  protected override val repository = new KnownFactsRepositoryImpl(
    mongoComponent = mongoComponent,
    appConfig = mockAppConfig,
    time = timeMachine
  )

  override protected def beforeEach(): Unit = {
    super.afterEach()
    repository.collection.deleteMany(Document()).toFuture().futureValue
  }

  ".set" must {

    "insert when no template exists and set the last updated time" in {

      repository.set(testErn, testTraderKnownFactsModel).futureValue

      val expectedResult = knownFacts copy (lastUpdated = instantNow)
      val updatedRecord = repository.get(testErn).futureValue.get

      updatedRecord shouldBe expectedResult
    }

    "upsert when template already exists and update the last updated time" in {

      insert(knownFacts).futureValue
      repository.set(testErn, testTraderKnownFactsModel).futureValue

      val expectedResult = knownFacts copy (lastUpdated = instantNow)
      val updatedRecord = repository.get(testErn).futureValue.value

      updatedRecord shouldBe expectedResult
    }
  }

  ".get" when {

    "there is a known facts response for this ern" must {

      "return Some(known facts)" in {

        insert(knownFacts).futureValue

        val result = repository.get(testErn).futureValue

        result shouldBe Some(knownFacts)
      }
    }

    "there is no known facts for this ern" must {

      "return None" in {
        repository.get(testErn).futureValue shouldBe None
      }
    }
  }
}
