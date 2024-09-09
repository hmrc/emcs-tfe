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

package uk.gov.hmrc.emcstfe.controllers

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import org.mockito.Mockito.when
import org.mongodb.scala.bson.collection.immutable.Document
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.http.Status
import play.api.http.Status.{CREATED, INTERNAL_SERVER_ERROR, NO_CONTENT, OK}
import play.api.inject._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.{Application, Environment, Mode}
import uk.gov.hmrc.emcstfe.fixtures.{GetMovementFixture, MovementSubmissionFailureFixtures}
import uk.gov.hmrc.emcstfe.models.mongo.{CreateMovementUserAnswers, MovementTemplate}
import uk.gov.hmrc.emcstfe.repositories.{CreateMovementUserAnswersRepositoryImpl, MovementTemplatesRepositoryImpl}
import uk.gov.hmrc.emcstfe.stubs.AuthStub
import uk.gov.hmrc.emcstfe.support.IntegrationBaseSpec
import uk.gov.hmrc.emcstfe.utils.{TimeMachine, UUIDGenerator}

import java.time.Instant
import java.time.temporal.ChronoUnit

class MovementTemplatesIntegrationSpec extends IntegrationBaseSpec with GetMovementFixture with MovementSubmissionFailureFixtures {

  val mockUUIDGenerator = mock[UUIDGenerator]
  val instantNow = Instant.now().truncatedTo(ChronoUnit.MILLIS)
  implicit val timeMachine: TimeMachine = () => instantNow

  val template: MovementTemplate = MovementTemplate(
    ern = testErn,
    templateId = testTemplateId,
    templateName = testTemplateName,
    data = Json.obj(),
    lastUpdated = instantNow
  )

  private trait Test {

    lazy val mongoRepo: MovementTemplatesRepositoryImpl = app.injector.instanceOf[MovementTemplatesRepositoryImpl]
    lazy val draftsRepo: CreateMovementUserAnswersRepositoryImpl = app.injector.instanceOf[CreateMovementUserAnswersRepositoryImpl]

    implicit val uuidGenerator = app.injector.instanceOf[UUIDGenerator]

    def setupStubs(): StubMapping

    def request(url: String): WSRequest = {
      await(mongoRepo.collection.deleteMany(Document()).toFuture())
      await(draftsRepo.collection.deleteMany(Document()).toFuture())
      setupStubs()
      buildRequest(url)
    }
  }

  override implicit lazy val app: Application = new GuiceApplicationBuilder()
    .in(Environment.simple(mode = Mode.Dev))
    .configure(servicesConfig)
    .overrides(bind[UUIDGenerator].to(mockUUIDGenerator))
    .overrides(bind[TimeMachine].to(timeMachine))
    .build()

  //API returns a list of available templates for a supplied ERN
  s"GET /templates/:ern" when {

    "user is unauthenticated" must {
      "return Forbidden" in new Test {

        override def setupStubs(): StubMapping = {
          AuthStub.unauthorised()
        }

        val response: WSResponse = await(request(s"/templates/$testErn").get())
        response.status shouldBe Status.FORBIDDEN
      }
    }

    "user is authenticated but the ERN requested does not match the ERN of the credential" in new Test {
      override def setupStubs(): StubMapping = {
        AuthStub.authorised("WrongERN")
      }

      val response: WSResponse = await(request(s"/templates/$testErn").get())
      response.status shouldBe Status.FORBIDDEN
    }

    "user is authorised" must {

      s"return $OK (OK)" when {
        "data is retrieved from Mongo" in new Test {

          override def setupStubs(): StubMapping = {
            await(mongoRepo.set(template.copy(templateId = "foo1", templateName = "foo 1")))
            await(mongoRepo.set(template.copy(templateId = "foo2", templateName = "foo 2")))
            AuthStub.authorised()
          }

          val response: WSResponse = await(request(s"/templates/$testErn").get())

          response.status shouldBe OK
          response.header("Content-Type") shouldBe Some("application/json")

          response.json shouldBe Json.toJson(Seq(
            template.copy(templateId = "foo1", templateName = "foo 1"),
            template.copy(templateId = "foo2", templateName = "foo 2")
          ))
        }
      }

      s"return $NO_CONTENT (NO_CONTENT)" when {
        "no data is retrieved from Mongo" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
          }

          val response: WSResponse = await(request(s"/templates/$testErn").get())

          response.status shouldBe NO_CONTENT
        }
      }
    }
  }

  //API returns a specific template for a supplied ERN and TemplateID
  s"GET /template/:ern/:templateId" when {

    "user is unauthenticated" must {
      "return Forbidden" in new Test {

        override def setupStubs(): StubMapping = {
          AuthStub.unauthorised()
        }

        val response: WSResponse = await(request(s"/template/$testErn/$testTemplateId").get())
        response.status shouldBe Status.FORBIDDEN
      }
    }

    "user is authenticated but the ERN requested does not match the ERN of the credential" in new Test {
      override def setupStubs(): StubMapping = {
        AuthStub.authorised("WrongERN")
      }

      val response: WSResponse = await(request(s"/template/$testErn/$testTemplateId").get())
      response.status shouldBe Status.FORBIDDEN
    }

    "user is authorised" must {

      s"return $OK (OK)" when {
        "data is retrieved from Mongo" in new Test {

          override def setupStubs(): StubMapping = {
            await(mongoRepo.set(template))
            AuthStub.authorised()
          }

          val response: WSResponse = await(request(s"/template/$testErn/$testTemplateId").get())

          response.status shouldBe OK
          response.header("Content-Type") shouldBe Some("application/json")

          response.json shouldBe Json.toJson(template)
        }
      }

      s"return $NO_CONTENT (NO_CONTENT)" when {
        "no data is retrieved from Mongo" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
          }

          val response: WSResponse = await(request(s"/template/$testErn/$testTemplateId").get())

          response.status shouldBe NO_CONTENT
        }
      }
    }
  }

  //API stores a template for a specific ern and templateId
  s"PUT /template/:ern/:templateId" when {

    "user is unauthenticated" must {
      "return Forbidden" in new Test {

        override def setupStubs(): StubMapping = {
          AuthStub.unauthorised()
        }

        val response: WSResponse = await(request(s"/template/$testErn/$testTemplateId").put(
          Json.toJson(template)
        ))
        response.status shouldBe Status.FORBIDDEN
      }
    }

    "user is authenticated but the ERN requested does not match the ERN of the credential" in new Test {
      override def setupStubs(): StubMapping = {
        AuthStub.authorised("WrongERN")
      }

      val response: WSResponse = await(request(s"/template/$testErn/$testTemplateId").put(
        Json.toJson(template)
      ))
      response.status shouldBe Status.FORBIDDEN
    }

    "user is authorised" must {

      s"return $OK (OK)" when {
        "data is successfully stored to Mongo (no existing record)" in new Test {

          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
          }

          val response: WSResponse = await(request(s"/template/$testErn/$testTemplateId").put(
            Json.toJson(template)
          ))

          response.status shouldBe OK
          response.header("Content-Type") shouldBe Some("application/json")

          response.json shouldBe Json.toJson(template)
        }

        "data is successfully stored to Mongo (existing record updated)" in new Test {

          override def setupStubs(): StubMapping = {
            await(mongoRepo.set(template))
            AuthStub.authorised()
          }

          await(mongoRepo.get(testErn, testTemplateId)) shouldBe Some(template)

          val updatedTemplate = template.copy(data = Json.obj("foo" -> "new"))

          val response: WSResponse = await(request(s"/template/$testErn/$testTemplateId").put(
            Json.toJson(updatedTemplate)
          ))

          response.status shouldBe OK
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe Json.toJson(updatedTemplate)

          await(mongoRepo.get(testErn, testTemplateId)) shouldBe Some(updatedTemplate)
        }

      }

      s"return $INTERNAL_SERVER_ERROR (INTERNAL_SERVER_ERROR)" when {

        "another template with a different ID exists with the same templateName" in new Test {

          override def setupStubs(): StubMapping = {
            await(mongoRepo.set(template))
            await(mongoRepo.set(template.copy(templateId = "dupe", templateName = "Duplicate")))
            AuthStub.authorised()
          }

          val updatedTemplate = template.copy(data = Json.obj("foo" -> "new"), templateName = "Duplicate")

          val response: WSResponse = await(request(s"/template/$testErn/$testTemplateId").put(
            Json.toJson(updatedTemplate)
          ))

          response.status shouldBe INTERNAL_SERVER_ERROR
          response.header("Content-Type") shouldBe Some("application/json")
          response.json.toString() should include("E11000 duplicate key error")
        }
      }
    }
  }

  //API deletes a template for a specific ern and templateId
  s"DELETE /template/:ern/:templateId" when {

    "user is unauthenticated" must {
      "return Forbidden" in new Test {

        override def setupStubs(): StubMapping = {
          AuthStub.unauthorised()
        }

        val response: WSResponse = await(request(s"/template/$testErn/$testTemplateId").delete())
        response.status shouldBe Status.FORBIDDEN
      }
    }

    "user is authenticated but the ERN requested does not match the ERN of the credential" in new Test {
      override def setupStubs(): StubMapping = {
        AuthStub.authorised("WrongERN")
      }

      val response: WSResponse = await(request(s"/template/$testErn/$testTemplateId").delete())
      response.status shouldBe Status.FORBIDDEN
    }

    "user is authorised" must {

      s"return $NO_CONTENT (NO_CONTENT)" when {
        "no data is deleted (no existing record)" in new Test {

          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
          }

          val response: WSResponse = await(request(s"/template/$testErn/$testTemplateId").delete())
          response.status shouldBe NO_CONTENT
        }

        "data is successfully deleted from Mongo (existing record)" in new Test {

          override def setupStubs(): StubMapping = {
            await(mongoRepo.set(template))
            await(mongoRepo.get(testErn, testTemplateId)) shouldBe Some(template)
            AuthStub.authorised()
          }

          val response: WSResponse = await(request(s"/template/$testErn/$testTemplateId").delete())
          response.status shouldBe NO_CONTENT

          await(mongoRepo.get(testErn, testTemplateId)) shouldBe None
        }
      }
    }
  }

  //API checks if a template exists with supplied query string ERN and TemplateName
  s"GET /template/name-already-exists" when {

    "user is unauthenticated" must {
      "return Forbidden" in new Test {

        override def setupStubs(): StubMapping = {
          AuthStub.unauthorised()
        }

        val response: WSResponse = await(request(s"/template/name-already-exists?ern=$testErn&templateName=$testTemplateName").get())
        response.status shouldBe Status.FORBIDDEN
      }
    }

    "user is authenticated but the ERN requested does not match the ERN of the credential" in new Test {
      override def setupStubs(): StubMapping = {
        AuthStub.authorised("WrongERN")
      }

      val response: WSResponse = await(request(s"/template/name-already-exists?ern=$testErn&templateName=$testTemplateName").get())
      response.status shouldBe Status.FORBIDDEN
    }

    "user is authorised" must {

      s"return $OK (OK) true" when {
        "when name already exists in Mongo" in new Test {

          override def setupStubs(): StubMapping = {
            await(mongoRepo.set(template))
            AuthStub.authorised()
          }

          val response: WSResponse = await(request(s"/template/name-already-exists?ern=$testErn&templateName=$testTemplateName").get())

          response.status shouldBe OK
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe Json.toJson(true)
        }
      }

      s"return $OK (OK) false" when {
        "when name DOES NOT already exist in Mongo" in new Test {

          override def setupStubs(): StubMapping = {
            await(mongoRepo.set(template))
            AuthStub.authorised()
          }

          val response: WSResponse = await(request(s"/template/name-already-exists?ern=$testErn&templateName=foo").get())

          response.status shouldBe OK
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe Json.toJson(false)
        }
      }
    }
  }

  //API creates a draft movement based on the template
  s"GET /template/:ern/:templateId/create-draft-from-template" when {

    "user is unauthenticated" must {
      "return Forbidden" in new Test {

        override def setupStubs(): StubMapping = {
          AuthStub.unauthorised()
        }

        val response: WSResponse = await(request(s"/template/$testErn/$testTemplateId/create-draft-from-template").get())
        response.status shouldBe Status.FORBIDDEN
      }
    }

    "user is authenticated but the ERN requested does not match the ERN of the credential" in new Test {
      override def setupStubs(): StubMapping = {
        AuthStub.authorised("WrongERN")
      }

      val response: WSResponse = await(request(s"/template/$testErn/$testTemplateId/create-draft-from-template").get())
      response.status shouldBe Status.FORBIDDEN
    }

    "user is authorised" must {

      s"return $CREATED (CREATED)" when {
        "template exists and draft is successfully created" in new Test {

          override def setupStubs(): StubMapping = {
            await(mongoRepo.set(template))
            when(mockUUIDGenerator.randomUUID).thenReturn(testNewDraftId)
            AuthStub.authorised()
          }

          val response: WSResponse = await(request(s"/template/$testErn/$testTemplateId/create-draft-from-template").get())

          response.status shouldBe CREATED
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe Json.obj(
            "createdDraftId" -> testNewDraftId
          )

          await(draftsRepo.get(testErn, testNewDraftId)) shouldBe Some(CreateMovementUserAnswers.applyFromTemplate(template))
        }
      }

      s"return $INTERNAL_SERVER_ERROR (INTERNAL_SERVER_ERROR)" when {
        "template DOES NOT exist" in new Test {

          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
          }

          val response: WSResponse = await(request(s"/template/$testErn/$testTemplateId/create-draft-from-template").get())

          response.status shouldBe INTERNAL_SERVER_ERROR
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe Json.obj(
            "message" -> "No template exists with the templateId: template1234"
          )
        }
      }
    }
  }
}
