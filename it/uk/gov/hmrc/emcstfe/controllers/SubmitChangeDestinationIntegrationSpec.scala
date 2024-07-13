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
import play.api.http.Status.OK
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.featureswitch.core.config.{FeatureSwitching, SendToEIS}
import uk.gov.hmrc.emcstfe.fixtures.{GetMovementFixture, SubmitChangeDestinationFixtures}
import uk.gov.hmrc.emcstfe.models.response.ChRISSuccessResponse
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse._
import uk.gov.hmrc.emcstfe.stubs.{AuthStub, DownstreamStub}
import uk.gov.hmrc.emcstfe.support.IntegrationBaseSpec

import scala.xml.XML

class SubmitChangeDestinationIntegrationSpec extends IntegrationBaseSpec with SubmitChangeDestinationFixtures with GetMovementFixture with FeatureSwitching {

  override val config: AppConfig = app.injector.instanceOf[AppConfig]

  import SubmitChangeDestinationFixtures._

  private trait Test {
    def setupStubs(): StubMapping

    def uri: String = s"/change-destination/$testErn/$testArc"

    def downstreamUri: String = s"/ChRIS/EMCS/SubmitChangeOfDestinationPortal/3"

    def downstreamEisUri: String = s"/emcs/digital-submit-new-message/v1"

    def downstreamGetMovementUri: String = "/ChRISOSB/EMCS/EMCSApplicationService/2"

    def downstreamEisGetMovementUri: String = "/emcs/movements/v1/movement"

    def downstreamEisGetMovementQueryParam: Map[String, String] =
      Seq(
        Some("exciseregistrationnumber" -> testErn),
        Some("arc" -> testArc)
      ).flatten.toMap

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri, "Content-Type" -> "application/json")
    }
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    disable(SendToEIS)
  }

  "Calling the submit change destination endpoint" must {

    "when calling ChRIS" must {
      "return a success" when {
        "all downstream calls are successful" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccess(DownstreamStub.POST, downstreamGetMovementUri, Status.OK, XML.loadString(getMovementSoapWrapper()))
            DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Status.OK, XML.loadString(chrisSuccessSOAPResponseBody))
          }

          val response: WSResponse = await(request().post(submitChangeDestinationJsonMax))
          response.status shouldBe Status.OK
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe chrisSuccessJsonNoLRN()
        }
      }
      "return an error" when {
        "downstream call returns unexpected XML" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccess(DownstreamStub.POST, downstreamGetMovementUri, Status.OK, XML.loadString(getMovementSoapWrapper()))
            DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Status.OK, <Message>Success!</Message>)
          }

          val response: WSResponse = await(request().post(submitChangeDestinationJsonMax))
          response.status shouldBe Status.INTERNAL_SERVER_ERROR
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe Json.toJson(XmlParseError(Seq(EmptyError(ChRISSuccessResponse.digestValue), EmptyError(ChRISSuccessResponse.receiptDateTime), EmptyError(ChRISSuccessResponse.digestValue))))
        }
        "downstream call returns something other than XML" in new Test {
          val responseBody: JsValue = Json.obj("message" -> "Success!")

          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccess(DownstreamStub.POST, downstreamGetMovementUri, Status.OK, XML.loadString(getMovementSoapWrapper()))
            DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Status.OK, responseBody)
          }

          val response: WSResponse = await(request().post(submitChangeDestinationJsonMax))
          response.status shouldBe Status.INTERNAL_SERVER_ERROR
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe Json.toJson(XmlValidationError)
        }
        "downstream call returns RIM validation errors" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccess(DownstreamStub.POST, downstreamGetMovementUri, Status.OK, XML.loadString(getMovementSoapWrapper()))
            DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Status.OK, XML.loadString(chrisRimValidationResponseBody))
          }

          val response: WSResponse = await(request().post(submitChangeDestinationJsonMax))
          response.status shouldBe Status.UNPROCESSABLE_ENTITY
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe Json.obj(
            "message" -> "Request not processed returned by ChRIS"
          )
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
            DownstreamStub.onSuccess(DownstreamStub.POST, downstreamGetMovementUri, Status.OK, XML.loadString(getMovementSoapWrapper()))
            DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Status.INTERNAL_SERVER_ERROR, referenceDataResponseBody)
          }

          val response: WSResponse = await(request().post(submitChangeDestinationJsonMax))
          response.status shouldBe Status.INTERNAL_SERVER_ERROR
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe Json.toJson(UnexpectedDownstreamResponseError)
        }
      }
    }

    "when calling EIS" must {

      "return a success" when {

        "all downstream calls are successful" in new Test {

          override def setupStubs(): StubMapping = {
            enable(SendToEIS)
            AuthStub.authorised()
            DownstreamStub.onSuccess(DownstreamStub.GET, downstreamEisGetMovementUri, downstreamEisGetMovementQueryParam, OK, getRawMovementJson())
            DownstreamStub.onSuccess(DownstreamStub.POST, downstreamEisUri, Status.OK, eisSuccessJson())
          }

          val response: WSResponse = await(request().post(submitChangeDestinationJsonMax))
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

          val response: WSResponse = await(request().post(submitChangeDestinationJsonMax))
          response.status shouldBe Status.INTERNAL_SERVER_ERROR
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe Json.obj(
            "message" -> "Errors parsing JSON, errors: List(JsonValidationError(List(error.path.missing),List()))"
          )
        }

        "downstream call returns a 422 (not RIM validation errors)" in new Test {
          override def setupStubs(): StubMapping = {
            enable(SendToEIS)
            AuthStub.authorised()
            DownstreamStub.onError(DownstreamStub.POST, downstreamEisUri, Status.UNPROCESSABLE_ENTITY, Json.obj("foo" -> "bar").toString())
          }

          val response: WSResponse = await(request().post(submitChangeDestinationJsonMax))
          response.status shouldBe Status.INTERNAL_SERVER_ERROR
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe Json.obj(
            "message" -> "Request not processed returned by EIS, error response: {\"foo\":\"bar\"}"
          )
        }

        "downstream call returns RIM validation errors" in new Test {
          override def setupStubs(): StubMapping = {
            enable(SendToEIS)
            AuthStub.authorised()
            DownstreamStub.onError(DownstreamStub.POST, downstreamEisUri, Status.UNPROCESSABLE_ENTITY, eisRimValidationJsonResponse.toString())
          }

          val response: WSResponse = await(request().post(submitChangeDestinationJsonMax))
          response.status shouldBe Status.UNPROCESSABLE_ENTITY
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe Json.obj(
            "message" -> "Request not processed returned by EIS, correlation ID: 7be1db16-e8fb-4e81-97e5-3d3e2d21f6c4"
          )
        }

        "downstream call returns a non-200 HTTP response" in new Test {

          override def setupStubs(): StubMapping = {
            enable(SendToEIS)
            AuthStub.authorised()
            DownstreamStub.onError(DownstreamStub.POST, downstreamEisUri, Status.INTERNAL_SERVER_ERROR, "bad things")
          }

          val response: WSResponse = await(request().post(submitChangeDestinationJsonMax))
          response.status shouldBe Status.INTERNAL_SERVER_ERROR
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe Json.toJson(EISInternalServerError("bad things"))
        }
      }
    }
  }
}
