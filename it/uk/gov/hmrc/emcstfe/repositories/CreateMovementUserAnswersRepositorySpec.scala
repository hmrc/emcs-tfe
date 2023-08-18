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

package uk.gov.hmrc.emcstfe.repositories

import org.mongodb.scala.model.Filters
import org.scalamock.scalatest.MockFactory
import org.scalatest.OptionValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import play.api.libs.json.Json
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.fixtures.BaseFixtures
import uk.gov.hmrc.emcstfe.models.mongo.CreateMovementUserAnswers
import uk.gov.hmrc.emcstfe.support.IntegrationBaseSpec
import uk.gov.hmrc.emcstfe.utils.TimeMachine
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport

import java.time.Instant
import java.time.temporal.ChronoUnit
import scala.concurrent.duration.Duration

class CreateMovementUserAnswersRepositorySpec extends IntegrationBaseSpec
    with DefaultPlayMongoRepositorySupport[CreateMovementUserAnswers]
    with MockFactory
    with OptionValues
    with IntegrationPatience
    with ScalaFutures
    with BaseFixtures {

  private val instantNow = Instant.now.truncatedTo(ChronoUnit.MILLIS)
  private val timeMachine: TimeMachine = () => instantNow

  private val userAnswers = CreateMovementUserAnswers(testErn, testArc, Json.obj("foo" -> "bar"), Instant.ofEpochSecond(1))

  private val mockAppConfig = mock[AppConfig]
  (() => mockAppConfig.createMovementUserAnswersTTL(): Duration)
    .expects()
    .returns(Duration("1seconds"))
    .anyNumberOfTimes()
  (() => mockAppConfig.createMovementUserAnswersReplaceIndexes(): Boolean)
    .expects()
    .returns(true)
    .anyNumberOfTimes()

  protected override val repository = new CreateMovementUserAnswersRepository(
    mongoComponent = mongoComponent,
    appConfig      = mockAppConfig,
    time           = timeMachine
  )

  ".set" must {

    "set the last updated time on the supplied user answers to `now`, and save them" in {

      val expectedResult = userAnswers copy (lastUpdated = instantNow)

      val setResult     = repository.set(userAnswers).futureValue
      val updatedRecord = find(
        Filters.and(
          Filters.equal("ern", userAnswers.ern),
          Filters.equal("lrn", userAnswers.lrn)
        )
      ).futureValue.headOption.value

      setResult shouldBe true
      updatedRecord shouldBe expectedResult
    }
  }

  ".get" when {

    "there is a record for this id" must {

      "update the lastUpdated time and get the record" in {

        insert(userAnswers).futureValue

        val result         = repository.get(userAnswers.ern, userAnswers.lrn).futureValue
        val expectedResult = userAnswers copy (lastUpdated = instantNow)

        result.value shouldBe expectedResult
      }
    }

    "there is no record for this id" must {

      "return None" in {

        repository.get(userAnswers.ern, "wrongLrn").futureValue shouldBe None
      }
    }
  }

  ".clear" must {

    "remove a record" in {

      insert(userAnswers).futureValue

      val result = repository.clear(userAnswers.ern, userAnswers.lrn).futureValue

      result shouldBe true
      repository.get(userAnswers.ern, userAnswers.lrn).futureValue shouldBe None
    }

    "return true when there is no record to remove" in {
      val result = repository.clear(userAnswers.ern, userAnswers.lrn).futureValue

      result shouldBe true
    }
  }

  ".keepAlive" when {

    "there is a record for this id" must {

      "update its lastUpdated to `now` and return true" in {

        insert(userAnswers).futureValue

        val result = repository.keepAlive(userAnswers.ern, userAnswers.lrn).futureValue

        val expectedUpdatedAnswers = userAnswers copy (lastUpdated = instantNow)

        result shouldBe true
        val updatedAnswers = find(
          Filters.and(
            Filters.equal("ern", userAnswers.ern),
            Filters.equal("lrn", userAnswers.lrn)
          )
        ).futureValue.headOption.value
        updatedAnswers shouldBe expectedUpdatedAnswers
      }
    }

    "there is no record for this id" must {

      "return true" in {

        repository.keepAlive(userAnswers.ern, "wrongLrn").futureValue shouldBe true
      }
    }
  }
}
