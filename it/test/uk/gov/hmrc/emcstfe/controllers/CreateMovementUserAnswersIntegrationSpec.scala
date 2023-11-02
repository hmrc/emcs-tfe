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
import play.api.http.Status.{NO_CONTENT, OK}
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import uk.gov.hmrc.emcstfe.fixtures.GetMovementFixture
import uk.gov.hmrc.emcstfe.models.mongo.CreateMovementUserAnswers
import uk.gov.hmrc.emcstfe.repositories.CreateMovementUserAnswersRepositoryImpl
import uk.gov.hmrc.emcstfe.stubs.AuthStub
import uk.gov.hmrc.emcstfe.support.IntegrationBaseSpec

import java.time.Instant
import java.time.temporal.ChronoUnit

class CreateMovementUserAnswersIntegrationSpec extends IntegrationBaseSpec with GetMovementFixture {

  val userAnswers: CreateMovementUserAnswers = CreateMovementUserAnswers(testErn, testDraftId, Json.obj(), Instant.now().truncatedTo(ChronoUnit.MILLIS))

  def uri: String = s"/user-answers/create-movement/$testErn/$testDraftId"

  private trait Test {

    lazy val mongoRepo: CreateMovementUserAnswersRepositoryImpl = app.injector.instanceOf[CreateMovementUserAnswersRepositoryImpl]

    def setupStubs(): StubMapping

    def request(): WSRequest = {
      await(mongoRepo.collection.deleteMany(Document()).toFuture())
      setupStubs()
      buildRequest(uri)
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
}
