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
import play.api.libs.json.JsString
import uk.gov.hmrc.emcstfe.fixtures.GetMovementFixture
import uk.gov.hmrc.emcstfe.models.mongo.GetMovementMongoResponse
import uk.gov.hmrc.emcstfe.utils.TimeMachine

import java.time.Instant
import java.time.temporal.ChronoUnit

class GetMovementRepositorySpec
  extends RepositoryBaseSpec[GetMovementMongoResponse]
    with GetMovementFixture {

  private val instantNow = Instant.now.truncatedTo(ChronoUnit.MILLIS)
  private val timeMachine: TimeMachine = () => instantNow

  private val userAnswers = GetMovementMongoResponse(testArc, JsString(getMovementResponseBody), Instant.ofEpochSecond(1))

  protected override val repository = new GetMovementRepository(
    mongoComponent = mongoComponent,
    appConfig = mockAppConfig,
    time = timeMachine
  )

  ".set" must {

    "set the last updated time on the supplied user answers to `now`, and save them" in {

      val expectedResult = userAnswers copy (lastUpdated = instantNow)

      val setResult = repository.set(userAnswers).futureValue
      val updatedRecord = find(Filters.equal("arc", userAnswers.arc)).futureValue.headOption.value

      setResult shouldBe expectedResult
      updatedRecord shouldBe expectedResult
    }
  }

  ".get" when {

    "there is a record for this id" must {

      "update the lastUpdated time and get the record" in {

        insert(userAnswers).futureValue

        val result = repository.get(userAnswers.arc).futureValue
        val expectedResult = userAnswers copy (lastUpdated = instantNow)

        result.value shouldBe expectedResult
      }
    }

    "there is no record for this id" must {

      "return None" in {

        repository.get("wrongArc").futureValue shouldBe None
      }
    }
  }
}
