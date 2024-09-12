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

import org.mongodb.scala.Document
import play.api.libs.json.Json
import uk.gov.hmrc.emcstfe.fixtures.BaseFixtures
import uk.gov.hmrc.emcstfe.models.mongo.{MovementTemplate, MovementTemplates}
import uk.gov.hmrc.emcstfe.utils.TimeMachine

import java.time.Instant
import java.time.temporal.ChronoUnit

class MovementTemplatesRepositorySpec extends RepositoryBaseSpec[MovementTemplate] with BaseFixtures {

  override def checkTtlIndex: Boolean = false

  private val instantNow = Instant.now.truncatedTo(ChronoUnit.MILLIS)
  private val timeMachine: TimeMachine = () => instantNow

  val template = MovementTemplate(
    ern = testErn,
    templateId = testTemplateId,
    templateName = testTemplateName,
    data = Json.obj("foo" -> "bar"),
    lastUpdated = Instant.ofEpochSecond(1)
  )

  protected override val repository = new MovementTemplatesRepositoryImpl(
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

      repository.set(template).futureValue

      val expectedResult = template copy (lastUpdated = instantNow)
      val updatedRecord = repository.get(testErn, testTemplateId).futureValue.get

      updatedRecord shouldBe expectedResult
    }

    "upsert when template already exists and update the last updated time" in {

      insert(template).futureValue
      repository.set(template).futureValue

      val expectedResult = template copy (lastUpdated = instantNow)
      val updatedRecord = repository.get(testErn, testTemplateId).futureValue.value

      updatedRecord shouldBe expectedResult
    }
  }

  ".get" when {

    "there is a template for this id (templateId)" must {

      "return Some(template)" in {

        insert(template).futureValue

        val result = repository.get(testErn, testTemplateId).futureValue

        result shouldBe Some(template)
      }
    }

    "there is no template for this template id" must {

      "return None" in {
        repository.get(testErn, "foo").futureValue shouldBe None
      }
    }
  }

  ".getList" when {

    "there are templates for the ern" must {

      "return Seq(templates)" when {

        "number of results is less than pageSize" in {

          insert(template.copy(templateId = "foo1", templateName = "foo 1")).futureValue
          insert(template.copy(templateId = "foo2", templateName = "foo 2")).futureValue
          insert(template.copy(templateId = "foo3", templateName = "foo 3")).futureValue

          val result = repository.getList(testErn, 1, 3).futureValue

          result shouldBe MovementTemplates(Seq(
            template.copy(templateId = "foo1", templateName = "foo 1"),
            template.copy(templateId = "foo2", templateName = "foo 2"),
            template.copy(templateId = "foo3", templateName = "foo 3")
          ), 3)
        }

        "number of results is greater than pageSize" in {

          insert(template.copy(templateId = "foo1", templateName = "foo 1")).futureValue
          insert(template.copy(templateId = "foo2", templateName = "foo 2")).futureValue
          insert(template.copy(templateId = "foo3", templateName = "foo 3")).futureValue

          val result = repository.getList(testErn, 1, 2).futureValue

          result shouldBe MovementTemplates(Seq(
            template.copy(templateId = "foo1", templateName = "foo 1"),
            template.copy(templateId = "foo2", templateName = "foo 2")
          ), 3)
        }

        "inserted data is unsorted" in {

          insert(template.copy(templateId = "foo1", templateName = "Foo 1")).futureValue
          insert(template.copy(templateId = "foo3", templateName = "foo 3")).futureValue
          insert(template.copy(templateId = "foo2", templateName = "boo 2")).futureValue

          val result = repository.getList(testErn, 1, 3).futureValue

          result shouldBe MovementTemplates(Seq(
            template.copy(templateId = "foo2", templateName = "boo 2"),
            template.copy(templateId = "foo1", templateName = "Foo 1"),
            template.copy(templateId = "foo3", templateName = "foo 3")
          ), 3)
        }
      }
    }

    "there are no templates for this ern" must {

      "return MovementTemplates(Seq(), 0)" in {
        repository.getList(testErn, 1, 3).futureValue shouldBe MovementTemplates(Seq(), 0)
      }
    }
  }

  ".delete" must {

    "remove a template when one is found" in {
      insert(template).futureValue
      repository.get(testErn, testTemplateId).futureValue shouldBe Some(template)
      repository.delete(testErn, testTemplateId).futureValue
      repository.get(testErn, testTemplateId).futureValue shouldBe None
    }

    "not fail when a template does not exist as this is already the desired outcome" in {
      val result = repository.delete(testErn, testTemplateId).futureValue
      result shouldBe true
    }
  }

  ".checkIfTemplateNameAlreadyExists" when {

    "there is a template with the same name" must {

      "return true" in {
        insert(template).futureValue
        repository.checkIfTemplateNameAlreadyExists(testErn, testTemplateName).futureValue shouldBe true
      }
    }

    "there is NO template with the same name" must {

      "return true" in {
        insert(template).futureValue
        repository.checkIfTemplateNameAlreadyExists(testErn, "foo").futureValue shouldBe false
      }
    }

    "there are no templates" must {

      "return None" in {
        repository.checkIfTemplateNameAlreadyExists(testErn, "foo").futureValue shouldBe false
      }
    }
  }
}
