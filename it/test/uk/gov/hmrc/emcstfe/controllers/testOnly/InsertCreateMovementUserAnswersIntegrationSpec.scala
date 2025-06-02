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

package uk.gov.hmrc.emcstfe.controllers.testOnly

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import org.mongodb.scala.bson.collection.immutable.Document
import play.api.http.Status.OK
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import uk.gov.hmrc.emcstfe.stubs.AuthStub
import uk.gov.hmrc.emcstfe.support.IntegrationBaseSpec
import uk.gov.hmrc.emcstfe.fixtures.GetMovementFixture
import uk.gov.hmrc.emcstfe.models.mongo.CreateMovementUserAnswers
import uk.gov.hmrc.emcstfe.repositories.CreateMovementUserAnswersRepositoryImpl

import java.time.Instant
import java.time.temporal.ChronoUnit

class InsertCreateMovementUserAnswersIntegrationSpec extends IntegrationBaseSpec with GetMovementFixture {

  val userAnswers: CreateMovementUserAnswers = CreateMovementUserAnswers(ern = testErn, draftId = testDraftId, data = Json.obj(), submissionFailures = Seq.empty, validationErrors = Seq.empty, lastUpdated = Instant.now().truncatedTo(ChronoUnit.MILLIS), hasBeenSubmitted = true, submittedDraftId = Some(testDraftId))

  def uri: String = s"/test-only/user-answers/create-movement/$testErn/$testDraftId"

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

  s"PUT $uri" when {

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
