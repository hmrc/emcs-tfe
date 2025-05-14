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

package test.uk.gov.hmrc.emcstfe.controllers.testOnly

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import org.mongodb.scala.bson.collection.immutable.Document
import play.api.http.Status.OK
import play.api.libs.json.{JsObject, JsValue, Json, OFormat}
import play.api.libs.ws.{WSRequest, WSResponse}
import test.uk.gov.hmrc.emcstfe.stubs.AuthStub
import test.uk.gov.hmrc.emcstfe.support.IntegrationBaseSpec
import uk.gov.hmrc.emcstfe.fixtures.GetMovementFixture
import uk.gov.hmrc.emcstfe.models.mongo.MovementTemplate
import uk.gov.hmrc.emcstfe.repositories.MovementTemplatesRepositoryImpl

import java.time.Instant
import java.time.temporal.ChronoUnit

class InsertMovementTemplateIntegrationSpec extends IntegrationBaseSpec with GetMovementFixture {

  implicit val movementTemplateFormat: OFormat[MovementTemplate] = MovementTemplate.mongoFormat

  val template: MovementTemplate = MovementTemplate(
    ern = testErn,
    templateId = testTemplateId,
    templateName = testTemplateName,
    data = Json.obj(),
    lastUpdated = Instant.now().truncatedTo(ChronoUnit.MILLIS)
  )

  def uri: String = s"/test-only/templates/create-template/$testErn/$testTemplateId"

  private trait Test {

    lazy val mongoRepo: MovementTemplatesRepositoryImpl = app.injector.instanceOf[MovementTemplatesRepositoryImpl]

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

        val response: WSResponse = await(request().put(Json.toJson(template)))

        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        removeLastUpdated(response.json) shouldBe removeLastUpdated(Json.toJson(template))

        await(mongoRepo.get(testErn, testTemplateId)).map(_.data) shouldBe Some(template.data)
      }

      "existing data exists so the mongo entry is updated" in new Test {

        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
        }

        await(mongoRepo.set(template))

        val updatedTemplate = template.copy(data = Json.obj("foo" -> "bar"))

        val response: WSResponse = await(request().put(Json.toJson(updatedTemplate)))

        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        removeLastUpdated(response.json) shouldBe removeLastUpdated(Json.toJson(updatedTemplate))

        await(mongoRepo.get(testErn, testTemplateId)).map(_.data) shouldBe Some(updatedTemplate.data)
      }
    }
  }
}
