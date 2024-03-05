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
import org.mongodb.scala.bson.collection.immutable.Document
import play.api.http.Status
import play.api.http.Status.{NOT_FOUND, NO_CONTENT, OK}
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import uk.gov.hmrc.emcstfe.fixtures.{GetMovementFixture, MovementSubmissionFailureFixtures}
import uk.gov.hmrc.emcstfe.models.mongo.CreateMovementUserAnswers
import uk.gov.hmrc.emcstfe.repositories.CreateMovementUserAnswersRepositoryImpl
import uk.gov.hmrc.emcstfe.stubs.AuthStub
import uk.gov.hmrc.emcstfe.support.IntegrationBaseSpec

import java.time.Instant
import java.time.temporal.ChronoUnit

class CreateMovementUserAnswersIntegrationSpec extends IntegrationBaseSpec with GetMovementFixture with MovementSubmissionFailureFixtures {

  val testSubmittedDraftId = "12345-12346-12347"

  val userAnswers: CreateMovementUserAnswers = CreateMovementUserAnswers(testErn, testDraftId, Json.obj("info" -> Json.obj("localReferenceNumber" -> testLrn)), submissionFailures = Seq.empty, Instant.now().truncatedTo(ChronoUnit.MILLIS), hasBeenSubmitted = true, submittedDraftId = Some(testSubmittedDraftId))

  def uri: String = s"/user-answers/create-movement/$testErn/$testDraftId"

  private trait Test {

    lazy val mongoRepo: CreateMovementUserAnswersRepositoryImpl = app.injector.instanceOf[CreateMovementUserAnswersRepositoryImpl]

    def setupStubs(): StubMapping

    def request(url: String = uri): WSRequest = {
      await(mongoRepo.collection.deleteMany(Document()).toFuture())
      setupStubs()
      buildRequest(url)
    }
  }

  private def removeLastUpdated: JsValue => JsObject = _.as[JsObject] - "lastUpdated"

  s"GET $uri" when {

    "user is unauthenticated" must {
      "return Forbidden" in new Test {

        override def setupStubs(): StubMapping = {
          AuthStub.unauthorised()
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.FORBIDDEN
      }
    }

    "user is authenticated but the ERN requested does not match the ERN of the credential" in new Test {
      override def setupStubs(): StubMapping = {
        AuthStub.authorised("WrongERN")
      }

      val response: WSResponse = await(request().get())
      response.status shouldBe Status.FORBIDDEN
    }

    "user is authorised" must {

      s"return $OK (OK)" when {
        "data is retrieved from Mongo" in new Test {

          override def setupStubs(): StubMapping = {
            await(mongoRepo.set(userAnswers))
            AuthStub.authorised()
          }

          val response: WSResponse = await(request().get())

          response.status shouldBe OK
          response.header("Content-Type") shouldBe Some("application/json")
          removeLastUpdated(response.json) shouldBe removeLastUpdated(Json.toJson(userAnswers))
        }
      }

      s"return $NO_CONTENT (NO_CONTENT)" when {
        "no data is retrieved from Mongo" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
          }

          val response: WSResponse = await(request().get())

          response.status shouldBe NO_CONTENT
        }
      }
    }
  }

  s"PUT $uri" when {

    "user is unauthenticated" must {
      "return Forbidden" in new Test {

        override def setupStubs(): StubMapping = {
          AuthStub.unauthorised()
        }

        val response: WSResponse = await(request().put(Json.toJson(userAnswers)))

        response.status shouldBe Status.FORBIDDEN
      }
    }

    "user is authenticated but the ERN requested does not match the ERN of the credential" in new Test {
      override def setupStubs(): StubMapping = {
        AuthStub.authorised("WrongERN")
      }

      val response: WSResponse = await(request().put(Json.toJson(userAnswers)))

      response.status shouldBe Status.FORBIDDEN
    }

    "user is authorised" must {

      s"return $OK (OK)" when {
        "no existing data exists so the mongo entry is created" in new Test {

          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
          }

          val response: WSResponse = await(request().put(Json.toJson(userAnswers)))

          response.status shouldBe OK
          response.header("Content-Type") shouldBe Some("application/json")
          removeLastUpdated(response.json) shouldBe removeLastUpdated(Json.toJson(userAnswers))

          await(mongoRepo.get(testErn, testDraftId)).map(_.data) shouldBe Some(userAnswers.data)
        }

        "existing data exists so the mongo entry is updated" in new Test {

          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
          }

          await(mongoRepo.set(userAnswers))

          val updatedAnswers = userAnswers.copy(data = Json.obj("foo" -> "bar"))

          val response: WSResponse = await(request().put(Json.toJson(updatedAnswers)))

          response.status shouldBe OK
          response.header("Content-Type") shouldBe Some("application/json")
          removeLastUpdated(response.json) shouldBe removeLastUpdated(Json.toJson(updatedAnswers))

          await(mongoRepo.get(testErn, testDraftId)).map(_.data) shouldBe Some(updatedAnswers.data)
        }
      }
    }
  }

  s"DELETE $uri" when {

    "user is unauthenticated" must {
      "return Forbidden" in new Test {

        override def setupStubs(): StubMapping = {
          AuthStub.unauthorised()
        }

        val response: WSResponse = await(request().delete())

        response.status shouldBe Status.FORBIDDEN
      }
    }

    "user is authenticated but the ERN requested does not match the ERN of the credential" in new Test {
      override def setupStubs(): StubMapping = {
        AuthStub.authorised("WrongERN")
      }

      val response: WSResponse = await(request().delete())

      response.status shouldBe Status.FORBIDDEN
    }

    "user is authorised" must {

      s"return $NO_CONTENT (NO_CONTENT)" when {
        "no existing data exists" in new Test {

          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
          }

          val response: WSResponse = await(request().delete())

          response.status shouldBe NO_CONTENT

          await(mongoRepo.get(testErn, testDraftId)) shouldBe None
        }

        "existing data exists so the mongo entry is removed" in new Test {

          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
          }

          await(mongoRepo.set(userAnswers))
          await(mongoRepo.get(testErn, testDraftId)).map(_.data) shouldBe Some(userAnswers.data)

          val response: WSResponse = await(request().delete())

          response.status shouldBe NO_CONTENT

          await(mongoRepo.get(testErn, testDraftId)) shouldBe None
        }
      }
    }
  }

  s"GET /user-answers/create-movement/draft/$testErn/$testDraftId" when {
    val draftUri: String = s"/user-answers/create-movement/draft/$testErn/$testDraftId"

    "user is unauthenticated" must {
      "return Forbidden" in new Test {

        override def setupStubs(): StubMapping = {
          AuthStub.unauthorised()
        }

        val response: WSResponse = await(request(draftUri).get())

        response.status shouldBe Status.FORBIDDEN
      }
    }

    "user is authenticated but the ERN requested does not match the ERN of the credential" in new Test {
      override def setupStubs(): StubMapping = {
        AuthStub.authorised("WrongERN")
      }

      val response: WSResponse = await(request(draftUri).get())

      response.status shouldBe Status.FORBIDDEN
    }

    "user is authorised" must {

      s"return $OK (OK) and exists to false" when {
        "no data exists in mongo" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
          }

          val response: WSResponse = await(request(draftUri).get())

          response.status shouldBe OK
          response.json shouldBe Json.obj("draftExists" -> false)
        }
      }

      s"return $OK (OK) and exists to true" when {
        "data exists in mongo" in new Test {
          override def setupStubs(): StubMapping = {
            await(mongoRepo.set(userAnswers)) shouldBe true
            await(mongoRepo.get(testErn, testDraftId)).map(_.data) shouldBe Some(userAnswers.data)
            AuthStub.authorised()
          }

          val response: WSResponse = await(request(draftUri).get())

          response.status shouldBe OK
          response.json shouldBe Json.obj("draftExists" -> true)

        }
      }
    }
  }

  s"PUT /user-answers/create-movement/$testErn/$testSubmittedDraftId/error-messages" when {

    def putErrorMessagesUri(submittedDraftId: String = testSubmittedDraftId): String = s"/user-answers/create-movement/$testErn/$submittedDraftId/error-messages"

    "user is unauthenticated" must {
      "return Forbidden" in new Test {

        override def setupStubs(): StubMapping = {
          AuthStub.unauthorised()
        }

        val response: WSResponse = await(request(putErrorMessagesUri()).put(Json.toJson(Seq(movementSubmissionFailureModel))))

        response.status shouldBe Status.FORBIDDEN
      }
    }

    "user is authenticated but the ERN requested does not match the ERN of the credential" in new Test {
      override def setupStubs(): StubMapping = {
        AuthStub.authorised("WrongERN")
      }

      val response: WSResponse = await(request(putErrorMessagesUri()).put(Json.toJson(Seq(movementSubmissionFailureModel))))

      response.status shouldBe Status.FORBIDDEN
    }

    "user is authorised" must {

      s"return $NOT_FOUND (NOT_FOUND)" when {
        "no data exists in mongo" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
          }

          val response: WSResponse = await(request(putErrorMessagesUri()).put(Json.toJson(Seq(movementSubmissionFailureModel))))

          response.status shouldBe NOT_FOUND
          response.body shouldBe "The draft movement could not be found"
        }

        "data exists in mongo but not for the ERN / LRN combo" in new Test {
          override def setupStubs(): StubMapping = {
            await(mongoRepo.set(userAnswers)) shouldBe true
            AuthStub.authorised()
          }

          val response: WSResponse = await(request(putErrorMessagesUri(submittedDraftId = "ABCD")).put(Json.toJson(Seq(movementSubmissionFailureModel))))

          response.status shouldBe NOT_FOUND
          response.body shouldBe "The draft movement could not be found"
        }
      }

      s"return $OK (OK)" when {
        "data exists in mongo for the LRN / ERN combo" in new Test {
          override def setupStubs(): StubMapping = {
            await(mongoRepo.set(userAnswers.copy(submittedDraftId = Some(testSubmittedDraftId)))) shouldBe true
            AuthStub.authorised()
          }

          val response: WSResponse = await(request(putErrorMessagesUri()).put(Json.toJson(Seq(movementSubmissionFailureModel))))

          response.status shouldBe OK
          response.json shouldBe Json.obj("draftId" -> testDraftId)

        }
      }
    }
  }

  s"PUT /user-answers/create-movement/draft/$testErn/$testDraftId/mark-as-draft" when {

    def markAsDraftUri(draftId: String = testDraftId): String = s"/user-answers/create-movement/$testErn/$draftId/mark-as-draft"

    "user is unauthenticated" must {
      "return Forbidden" in new Test {

        override def setupStubs(): StubMapping = {
          AuthStub.unauthorised()
        }

        val response: WSResponse = await(request(markAsDraftUri()).put(""))

        response.status shouldBe Status.FORBIDDEN
      }
    }

    "user is authenticated but the ERN requested does not match the ERN of the credential" in new Test {
      override def setupStubs(): StubMapping = {
        AuthStub.authorised("WrongERN")
      }

      val response: WSResponse = await(request(markAsDraftUri()).put(""))

      response.status shouldBe Status.FORBIDDEN
    }

    "user is authorised" must {

      s"return $OK (OK) when the movement draft can be found" in new Test {
        override def setupStubs(): StubMapping = {
          await(mongoRepo.set(userAnswers.copy(hasBeenSubmitted = true))) shouldBe true
          await(mongoRepo.get(testErn, testDraftId)).map(_.data) shouldBe Some(userAnswers.data)
          AuthStub.authorised()
        }


        val response: WSResponse = await(request(markAsDraftUri()).put(""))

        response.status shouldBe OK
        response.json shouldBe Json.obj("draftId" -> testDraftId)
        await(mongoRepo.get(testErn, testDraftId)).get.hasBeenSubmitted shouldBe false
      }

      s"return $NOT_FOUND (NOT_FOUND) when the movement draft cannot be found" in new Test {
        override def setupStubs(): StubMapping = {
          await(mongoRepo.set(userAnswers.copy(hasBeenSubmitted = true))) shouldBe true
          await(mongoRepo.get(testErn, testDraftId)).map(_.data) shouldBe Some(userAnswers.data)
          AuthStub.authorised()
        }


        val response: WSResponse = await(request(markAsDraftUri("blah")).put(""))

        response.status shouldBe NOT_FOUND
        response.body shouldBe "The draft movement could not be found"
      }
    }
  }
}
