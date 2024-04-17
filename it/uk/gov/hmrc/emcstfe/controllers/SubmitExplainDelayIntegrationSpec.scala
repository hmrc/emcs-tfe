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

import com.github.tomakehurst.wiremock.client.WireMock.{putRequestedFor, urlEqualTo, verify}
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import com.lucidchart.open.xtract.EmptyError
import play.api.http.Status
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.featureswitch.core.config.{EnableNRS, FeatureSwitching, SendToEIS}
import uk.gov.hmrc.emcstfe.fixtures.{NRSBrokerFixtures, SubmitExplainDelayFixtures}
import uk.gov.hmrc.emcstfe.models.nrs.explainDelay.ExplainDelayNRSSubmission
import uk.gov.hmrc.emcstfe.models.response.ChRISSuccessResponse
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse._
import uk.gov.hmrc.emcstfe.stubs.{AuthStub, DownstreamStub}
import uk.gov.hmrc.emcstfe.support.IntegrationBaseSpec

import scala.xml.XML

class SubmitExplainDelayIntegrationSpec
  extends IntegrationBaseSpec
    with SubmitExplainDelayFixtures
    with FeatureSwitching
    with NRSBrokerFixtures {

  override val config: AppConfig = app.injector.instanceOf[AppConfig]

  private trait Test {
    def setupStubs(): StubMapping

    def uri: String = s"/explain-delay/$testErn/$testArc"

    def downstreamUri: String = s"/ChRIS/EMCS/SubmitExplainDelayToDeliveryPortal/4"

    def downstreamEisUri: String = s"/emcs/digital-submit-new-message/v1"

    def downstreamNRSBrokerUri: String = s"/emcs-tfe-nrs-message-broker/trader/$testErn/nrs/submission"

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri, "Content-Type" -> "application/json")
    }
  }

  override def beforeEach(): Unit = {
    disable(SendToEIS)
    disable(EnableNRS)
    super.beforeEach()
  }


  "Calling the explain delay endpoint" must {
    "return a success from Chris" when {
      "all downstream calls are successful" in new Test {
        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Status.OK, XML.loadString(chrisSuccessSOAPResponseBody))
        }

        val response: WSResponse = await(request().post(Json.toJson(maxSubmitExplainDelayModel)))
        response.status shouldBe Status.OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe chrisSuccessJsonNoLRN()
      }
    }
    "return an error from Chris" when {
      "downstream call returns unexpected XML" in new Test {
        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          DownstreamStub.onError(DownstreamStub.POST, downstreamUri, Status.OK, <Message>Success!</Message>.toString())
        }

        val response: WSResponse = await(request().post(Json.toJson(maxSubmitExplainDelayModel)))
        response.status shouldBe Status.INTERNAL_SERVER_ERROR
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe Json.toJson(XmlParseError(Seq(EmptyError(ChRISSuccessResponse.digestValue), EmptyError(ChRISSuccessResponse.receiptDateTime), EmptyError(ChRISSuccessResponse.digestValue))))
      }
      "downstream call returns something other than XML" in new Test {
        val responseBody: JsValue = Json.obj("message" -> "Success!")

        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          DownstreamStub.onError(DownstreamStub.POST, downstreamUri, Status.OK, responseBody.toString())
        }

        val response: WSResponse = await(request().post(Json.toJson(maxSubmitExplainDelayModel)))
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
          DownstreamStub.onError(DownstreamStub.POST, downstreamUri, Status.INTERNAL_SERVER_ERROR, referenceDataResponseBody.toString())
        }

        val response: WSResponse = await(request().post(Json.toJson(maxSubmitExplainDelayModel)))
        response.status shouldBe Status.INTERNAL_SERVER_ERROR
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe Json.toJson(UnexpectedDownstreamResponseError)
      }
    }

    "return a success from EIS" when {
      "all downstream calls are successful" in new Test {
        override def setupStubs(): StubMapping = {
          enable(SendToEIS)
          AuthStub.authorised()
          DownstreamStub.onSuccess(DownstreamStub.POST, downstreamEisUri, Status.OK, eisSuccessJson())
        }

        val response: WSResponse = await(request().post(Json.toJson(maxSubmitExplainDelayModel)))
        response.status shouldBe Status.OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe eisSuccessJson()
      }
    }
    "return an error from EIS" when {
      "downstream call returns unexpected JSON" in new Test {
        override def setupStubs(): StubMapping = {
          enable(SendToEIS)
          AuthStub.authorised()
          DownstreamStub.onError(DownstreamStub.POST, downstreamEisUri, Status.OK, incompleteEisSuccessJson.toString())
        }

        val response: WSResponse = await(request().post(Json.toJson(maxSubmitExplainDelayModel)))
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

        val response: WSResponse = await(request().post(Json.toJson(maxSubmitExplainDelayModel)))
        response.status shouldBe Status.INTERNAL_SERVER_ERROR
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe Json.toJson(EISInternalServerError("bad things"))
      }
    }

    "when submitting payloads to NRS (downstream submission agnostic)" must {

      "return a success" in new Test {

        /*
          This uses JsonUnit (a Wiremock-provided library) to ignore some unmatchable body elements.
          In this case userSubmissionTimestamp is naturally impossible to match accurately.
          Header data is made up as part of the request processing, so the tests can't accurately replicate this.
          If userSubmissionTimestamp or headerData was missing in the actual payload then the test would fail.
         */
        val nrsRequestBody: JsObject = {
          Json.toJson(createNRSPayload(ExplainDelayNRSSubmission(maxSubmitExplainDelayModel)))
            .as[JsObject]
            .deepMerge(Json.obj("metadata" -> Json.obj("userSubmissionTimestamp" -> f"$${json-unit.any-string}", "headerData" -> f"$${json-unit.ignore}")))
        }

        override def setupStubs(): StubMapping = {
          enable(SendToEIS)
          enable(EnableNRS)
          AuthStub.authorised(withIdentityData = true)
          DownstreamStub.onSuccess(DownstreamStub.POST, downstreamEisUri, Status.OK, eisSuccessJson())
          DownstreamStub.onSuccessWithRequestBodyAndHeaders(DownstreamStub.PUT, downstreamNRSBrokerUri, status = Status.ACCEPTED, requestBody = Some(Json.stringify(nrsRequestBody)), responseBody = nrsBrokerResponseJson, headers = Map("Authorization" -> testAuthToken))
        }

        val response: WSResponse = await(request().post(Json.toJson(maxSubmitExplainDelayModel)))
        response.status shouldBe Status.OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe eisSuccessJson()
        verify(1, putRequestedFor(urlEqualTo(s"/emcs-tfe-nrs-message-broker/trader/$testErn/nrs/submission")))
        wireMockServer.findAllUnmatchedRequests.size() shouldBe 0
      }
    }
  }


}
