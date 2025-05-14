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

package test.uk.gov.hmrc.emcstfe.repositories

import org.mongodb.scala.Document
import play.api.libs.json.Json
import uk.gov.hmrc.emcstfe.fixtures.{EISResponsesFixture, MovementSubmissionFailureFixtures}
import uk.gov.hmrc.emcstfe.models.mongo.UserAnswers
import uk.gov.hmrc.emcstfe.repositories.ChangeDestinationUserAnswersRepositoryImpl
import uk.gov.hmrc.emcstfe.utils.TimeMachine

import java.time.Instant
import java.time.temporal.ChronoUnit

class ChangeDestinationUserAnswersRepositorySpec extends RepositoryBaseSpec[UserAnswers] with MovementSubmissionFailureFixtures with EISResponsesFixture {

  private val instantNow = Instant.now.truncatedTo(ChronoUnit.MILLIS)
  private val timeMachine: TimeMachine = () => instantNow

  val userAnswers: UserAnswers = UserAnswers(testErn, testArc, Json.obj("foo" -> "bar"), validationErrors = Seq.empty, timeMachine.instant())

  protected override val repository = new ChangeDestinationUserAnswersRepositoryImpl()(
    mongoComponent = mongoComponent,
    appConfig = mockAppConfig,
    time = timeMachine,
    ec = ec
  )

  override protected def beforeEach(): Unit = {
    super.afterEach()
    repository.collection.deleteMany(Document()).toFuture().futureValue
  }

  ".setValidationErrorMessagesForDraftMovement" must {

    "update the record when the there is one" in {
      insert(userAnswers).futureValue
      repository.setValidationErrorMessagesForDraftMovement(testErn, testArc, eisRimValidationResults).futureValue shouldBe true
      repository.get(testErn, testArc).futureValue.get.validationErrors shouldBe eisRimValidationResults
    }


    "not update any records when the search criteria doesn't match any" in {

      repository.setValidationErrorMessagesForDraftMovement(testErn, "ABC1234", eisRimValidationResults).futureValue shouldBe true
      repository.get(testErn, "ABC1234").futureValue shouldBe None
    }
  }

}
