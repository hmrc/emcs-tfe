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
import com.lucidchart.open.xtract.EmptyError
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.featureswitch.core.config.{FeatureSwitching, SendToEIS}
import uk.gov.hmrc.emcstfe.fixtures.SubmitCancellationOfMovementFixtures
import uk.gov.hmrc.emcstfe.models.response.ChRISSuccessResponse
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse._
import uk.gov.hmrc.emcstfe.stubs.{AuthStub, DownstreamStub}
import uk.gov.hmrc.emcstfe.support.IntegrationBaseSpec

import scala.xml.XML

class SubmitCancellationOfMovementControllerIntegrationSpec extends IntegrationBaseSpec
  with SubmitCancellationOfMovementFixtures
  with FeatureSwitching
  {

  override val config: AppConfig = app.injector.instanceOf[AppConfig]

  private trait Test {
    def setupStubs(): StubMapping

    def uri: String = s"/cancel-movement/$testErn/$testArc"

    def downstreamUri: String = "/ChRIS/EMCS/SubmitCancellationPortal/3"

    def downstreamEisUri: String = s"/emcs/digital-submit-new-message/v1"

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri, "Content-Type" -> "application/json")
    }
  }

  override def beforeEach(): Unit = {
    disable(SendToEIS)
    super.beforeEach()
  }

  "Calling the submit draft movement endpoint" when {
    "calling Chris" must {
      "return a success" when {
        "all downstream calls are successful" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Status.OK, XML.loadString(chrisSuccessSOAPResponseBody))
          }

          val response: WSResponse = await(request().post(maxSubmitCancellationOfMovementModelJson))
          response.status shouldBe Status.OK
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe chrisSuccessJsonNoLRN()
        }
      }
      "return an error" when {
        "downstream call returns unexpected XML" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Status.OK, <Message>Success!</Message>)
          }

          val response: WSResponse = await(request().post(Json.toJson(maxSubmitCancellationOfMovementModel)))
          response.status shouldBe Status.INTERNAL_SERVER_ERROR
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe Json.toJson(XmlParseError(Seq(EmptyError(ChRISSuccessResponse.digestValue), EmptyError(ChRISSuccessResponse.receiptDateTime), EmptyError(ChRISSuccessResponse.digestValue))))
        }
        "downstream call returns something other than XML" in new Test {
          val responseBody: JsValue = Json.obj("message" -> "Success!")

          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Status.OK, responseBody)
          }

          val response: WSResponse = await(request().post(maxSubmitCancellationOfMovementModelJson))
          response.status shouldBe Status.INTERNAL_SERVER_ERROR
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe Json.toJson(XmlValidationError)
        }
        "downstream call returns a non-200 HTTP response" in new Test {
          val referenceDataResponseBody: JsValue = Json.parse(
            s"""
               |{
               |   "message": "test message"
               |}
               |""".stripMargin
          )

          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Status.INTERNAL_SERVER_ERROR, referenceDataResponseBody)
          }

          val response: WSResponse = await(request().post(maxSubmitCancellationOfMovementModelJson))
          response.status shouldBe Status.INTERNAL_SERVER_ERROR
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe Json.toJson(UnexpectedDownstreamResponseError)
        }
      }
    }
    "calling EIS" must {
      "return a success" when {
        "all downstream calls are successful" in new Test {
          override def setupStubs(): StubMapping = {
            enable(SendToEIS)
            AuthStub.authorised()
            DownstreamStub.onSuccess(DownstreamStub.POST, downstreamEisUri, Status.OK, eisSuccessJson())
          }

          val response: WSResponse = await(request().post(maxSubmitCancellationOfMovementModelJson))
          response.status shouldBe Status.OK
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe eisSuccessJson()
        }

      }

      "return an error" when {

        "downstream call returns unexpected JSON" in new Test {
          override def setupStubs(): StubMapping = {
            enable(SendToEIS)
            AuthStub.authorised()
            DownstreamStub.onError(DownstreamStub.POST, downstreamEisUri, Status.OK, incompleteEisSuccessJson.toString())
          }

          val response: WSResponse = await(request().post(maxSubmitCancellationOfMovementModelJson))
          response.status shouldBe Status.INTERNAL_SERVER_ERROR
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe Json.obj(
            "message" -> "Errors parsing JSON, errors: List(JsonValidationError(List(error.path.missing),List()))"
          )
        }

        "downstream call returns a non-200 HTTP response" in new Test {
          override def setupStubs(): StubMapping = {
            enable(SendToEIS)
            AuthStub.authorised()
            DownstreamStub.onError(DownstreamStub.POST, downstreamEisUri, Status.INTERNAL_SERVER_ERROR, "bad things")
          }

          val response: WSResponse = await(request().post(maxSubmitCancellationOfMovementModelJson))
          response.status shouldBe Status.INTERNAL_SERVER_ERROR
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe Json.toJson(EISInternalServerError("bad things"))
        }
      }
    }

  }
}
