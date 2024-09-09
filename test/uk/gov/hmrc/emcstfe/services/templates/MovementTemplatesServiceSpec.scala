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

package uk.gov.hmrc.emcstfe.services.templates

import org.specs2.mock.Mockito.theStubbed
import play.api.libs.json.Json
import uk.gov.hmrc.emcstfe.fixtures.{GetMovementListFixture, MovementSubmissionFailureFixtures}
import uk.gov.hmrc.emcstfe.mocks.repository.{MockCreateMovementUserAnswersRepository, MockMovementTemplatesRepository}
import uk.gov.hmrc.emcstfe.mocks.utils.MockUUIDGenerator
import uk.gov.hmrc.emcstfe.models.mongo.{CreateMovementUserAnswers, MovementTemplate}
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{MongoError, TemplateDoesNotExist}
import uk.gov.hmrc.emcstfe.support.TestBaseSpec
import uk.gov.hmrc.emcstfe.utils.TimeMachine

import java.time.Instant
import java.time.temporal.ChronoUnit
import scala.concurrent.Future

class MovementTemplatesServiceSpec extends TestBaseSpec
  with GetMovementListFixture
  with MovementSubmissionFailureFixtures
  with MockUUIDGenerator
  with MockCreateMovementUserAnswersRepository
  with MockMovementTemplatesRepository {

  val instantNow = Instant.now().truncatedTo(ChronoUnit.MILLIS)
  implicit val timeMachine: TimeMachine = () => instantNow

  trait Test {
    val service: MovementTemplatesService = new MovementTemplatesService(
      mockMovementTemplatesRepository,
      mockCreateMovementUserAnswersRepository,
      )(mockUUIDGenerator, timeMachine)
  }

  val template: MovementTemplate =
    MovementTemplate(
      ern = testErn,
      templateId = testTemplateId,
      templateName = testTemplateName,
      data = Json.obj(),
      lastUpdated = instantNow
    )

  ".getList" should {
    "return a Right(Seq(templates))" when {
      "templates are successfully returned from Mongo" in new Test {

        MockMovementTemplatesRepository.getList(testErn).returns(Future.successful(Seq(template)))
        await(service.getList(testErn)) shouldBe Right(Seq(template))
      }
    }

    "return a Right(Seq())" when {
      "templates are not found in Mongo" in new Test {

        MockMovementTemplatesRepository.getList(testErn).returns(Future.successful(Seq()))
        await(service.getList(testErn)) shouldBe Right(Seq())
      }
    }
    "return a Left" when {
      "mongo error is returned" in new Test {

        MockMovementTemplatesRepository.getList(testErn).returns(Future.failed(new Exception("bang")))
        await(service.getList(testErn)) shouldBe Left(MongoError("bang"))
      }
    }
  }

  ".get" should {
    "return a Right(Some(template))" when {
      "a template is successfully returned from Mongo" in new Test {

        MockMovementTemplatesRepository.get(testErn, testTemplateId).returns(Future.successful(Some(template)))
        await(service.get(testErn, testTemplateId)) shouldBe Right(Some(template))
      }
    }

    "return a Right(None)" when {
      "template is not found in Mongo" in new Test {

        MockMovementTemplatesRepository.get(testErn, testTemplateId).returns(Future.successful(None))
        await(service.get(testErn, testTemplateId)) shouldBe Right(None)
      }
    }
    "return a Left" when {
      "mongo error is returned" in new Test {

        MockMovementTemplatesRepository.get(testErn, testTemplateId).returns(Future.failed(new Exception("bang")))
        await(service.get(testErn, testTemplateId)) shouldBe Left(MongoError("bang"))
      }
    }
  }

  ".set" should {
    "return a Right(boolean)" when {
      "template is successfully saved/updated in Mongo" in new Test {

        MockMovementTemplatesRepository.set(template).returns(Future.successful(true))
        await(service.set(template)) shouldBe Right(template)
      }
    }
    "return a Left" when {
      "mongo error is returned" in new Test {

        MockMovementTemplatesRepository.set(template).returns(Future.failed(new Exception("bang")))
        await(service.set(template)) shouldBe Left(MongoError("bang"))
      }
    }
  }

  ".delete" should {
    "return a Right(boolean)" when {
      "template is successfully saved/updated in Mongo" in new Test {

        MockMovementTemplatesRepository.delete(testErn, testTemplateId).returns(Future.successful(true))
        await(service.delete(testErn, testTemplateId)) shouldBe Right(true)
      }
    }
    "return a Left" when {
      "mongo error is returned" in new Test {

        MockMovementTemplatesRepository.delete(testErn, testTemplateId).returns(Future.failed(new Exception("bang")))
        await(service.delete(testErn, testTemplateId)) shouldBe Left(MongoError("bang"))
      }
    }
  }

  ".checkIfTemplateNameAlreadyExists()" should {
    "return a Right(true)" when {
      "there is already a template with the ERN and TemplateName" in new Test {
        MockMovementTemplatesRepository.checkIfTemplateNameAlreadyExists(testErn, testTemplateName).returns(Future.successful(true))
        await(service.checkIfTemplateNameAlreadyExists(testErn, testTemplateName)) shouldBe Right(true)
      }
    }

    "return a Right(false)" when {
      "there isn't a template with the ERN and TemplateName" in new Test {
        MockMovementTemplatesRepository.checkIfTemplateNameAlreadyExists(testErn, testTemplateName).returns(Future.successful(false))
        await(service.checkIfTemplateNameAlreadyExists(testErn, testTemplateName)) shouldBe Right(false)
      }
    }

    "return a Left" when {
      "mongo error is returned" in new Test {

        MockMovementTemplatesRepository.checkIfTemplateNameAlreadyExists(testErn, testTemplateName).returns(Future.failed(new Exception("bang")))
        await(service.checkIfTemplateNameAlreadyExists(testErn, testTemplateName)) shouldBe Left(MongoError("bang"))
      }
    }
  }

  ".createDraftMovementFromTemplate()" should {
    "return a Right(true)" when {
      "there is template with the ERN and TemplateName and the creation of the draft is successful" in new Test {

        MockMovementTemplatesRepository.get(testErn, testTemplateId).returns(Future.successful(Some(template)))
        MockUUIDGenerator.randomUUID.returns(testNewDraftId)
        MockCreateMovementUserAnswersRepository.set(CreateMovementUserAnswers.applyFromTemplate(template)).returns(Future.successful(true))

        await(service.createDraftMovementFromTemplate(testErn, testTemplateId)) shouldBe Right(testNewDraftId)
      }
    }

    "return a Left(TemplateDoesNotExist) error" when {
      "no template exists" in new Test {
        MockMovementTemplatesRepository.get(testErn, testTemplateId).returns(Future.successful(None))
        await(service.createDraftMovementFromTemplate(testErn, testTemplateId)) shouldBe Left(TemplateDoesNotExist(testTemplateId))
      }
    }

    "return a Left(mongoError)" when {
      "mongo error is returned from templates repository" in new Test {

        MockMovementTemplatesRepository.get(testErn, testTemplateId).returns(Future.failed(new Exception("bang")))
        await(service.createDraftMovementFromTemplate(testErn, testTemplateId)) shouldBe Left(MongoError("bang"))
      }

      "mongo error is returned from create movement userAnswers repository" in new Test {


        MockMovementTemplatesRepository.get(testErn, testTemplateId).returns(Future.successful(Some(template)))
        MockUUIDGenerator.randomUUID.returns(testNewDraftId)
        MockCreateMovementUserAnswersRepository.set(CreateMovementUserAnswers.applyFromTemplate(template)).returns(Future.failed(new Exception("bang")))

        await(service.createDraftMovementFromTemplate(testErn, testTemplateId)) shouldBe Left(MongoError("bang"))
      }
    }
  }
}
