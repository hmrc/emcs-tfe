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

package uk.gov.hmrc.emcstfe.services.userAnswers

import play.api.libs.json.Json
import uk.gov.hmrc.emcstfe.fixtures.GetMovementListFixture
import uk.gov.hmrc.emcstfe.mocks.repository.MockUserAnswersRepository
import uk.gov.hmrc.emcstfe.models.mongo.UserAnswers
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.MongoError
import uk.gov.hmrc.emcstfe.support.UnitSpec

import java.time.Instant
import scala.concurrent.Future

trait BaseUserAnswersServiceSpec extends UnitSpec with GetMovementListFixture with MockUserAnswersRepository {

  val service: BaseUserAnswersService

  val userAnswers = UserAnswers(testErn, testArc, Json.obj(), Instant.now())

  ".get" should {
    "return a Right(Some(answers))" when {
      "UserAnswers are successfully returned from Mongo" in {

        MockUserAnswers.get(testErn, testArc).thenReturn(Future.successful(Some(userAnswers)))
        await(service.get(testErn, testArc)) shouldBe Right(Some(userAnswers))
      }
    }

    "return a Right(None)" when {
      "UserAnswers are not found in Mongo" in {

        MockUserAnswers.get(testErn, testArc).thenReturn(Future.successful(None))
        await(service.get(testErn, testArc)) shouldBe Right(None)
      }
    }
    "return a Left" when {
      "mongo error is returned" in {

        MockUserAnswers.get(testErn, testArc).thenReturn(Future.failed(new Exception("bang")))
        await(service.get(testErn, testArc)) shouldBe Left(MongoError("bang"))
      }
    }
  }

  ".set" should {
    "return a Right(boolean)" when {
      "UserAnswers are successfully saved/updated in Mongo" in {

        MockUserAnswers.set(userAnswers).thenReturn(Future.successful(true))
        await(service.set(userAnswers)) shouldBe Right(userAnswers)
      }
    }
    "return a Left" when {
      "mongo error is returned" in {

        MockUserAnswers.set(userAnswers).thenReturn(Future.failed(new Exception("bang")))
        await(service.set(userAnswers)) shouldBe Left(MongoError("bang"))
      }
    }
  }

  ".clear" should {
    "return a Right(boolean)" when {
      "UserAnswers are successfully saved/updated in Mongo" in {

        MockUserAnswers.clear(testErn, testArc).thenReturn(Future.successful(true))
        await(service.clear(testErn, testArc)) shouldBe Right(true)
      }
    }
    "return a Left" when {
      "mongo error is returned" in {

        MockUserAnswers.clear(testErn, testArc).thenReturn(Future.failed(new Exception("bang")))
        await(service.clear(testErn, testArc)) shouldBe Left(MongoError("bang"))
      }
    }
  }
}
