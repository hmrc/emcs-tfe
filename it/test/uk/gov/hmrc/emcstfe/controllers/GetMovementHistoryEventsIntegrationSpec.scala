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

package test.uk.gov.hmrc.emcstfe.controllers

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.Status
import play.api.http.Status.FORBIDDEN
import play.api.libs.json.{Json, JsonValidationError}
import play.api.libs.ws.{WSRequest, WSResponse}
import test.uk.gov.hmrc.emcstfe.stubs.{AuthStub, DownstreamStub}
import test.uk.gov.hmrc.emcstfe.support.IntegrationBaseSpec
import uk.gov.hmrc.emcstfe.fixtures.GetMovementHistoryEventsFixture
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.EISJsonParsingError

class GetMovementHistoryEventsIntegrationSpec extends IntegrationBaseSpec with GetMovementHistoryEventsFixture {

  private trait Test {
    def setupStubs(): StubMapping

    def uri: String = s"/movement-history/$testErn/$testArc"

    def downstreamEisUri: String = s"/emcs/movements/v1/movement-history"

    def downstreamQueryParams: Map[String, String] = Map(
      "exciseregistrationnumber" -> testErn,
      "arc" -> testArc
    )

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri)
    }
  }

  "getMovementHistory" when {

    "user is unauthorised" must {
      s"return FORBIDDEN ($FORBIDDEN)" in new Test {
        override def setupStubs(): StubMapping = {
          AuthStub.unauthorised()
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe FORBIDDEN
      }
    }
    "user is authorised" must {

      s"return FORBIDDEN ($FORBIDDEN)" when {
        "the ern requested doesnt match the ERN of the credential" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised("WrongERN")
          }

          val response: WSResponse = await(request().get())
          response.status shouldBe FORBIDDEN
        }
      }
      "return a success" when {
        "all downstream calls are successful" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccess(DownstreamStub.GET, downstreamEisUri, downstreamQueryParams, Status.OK, getMovementHistoryEventsEISResponseJson)
          }

          val response: WSResponse = await(request().get())
          response.status shouldBe Status.OK
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe getMovementHistoryEventsControllerResponseJson
        }

        "all downstream calls are successful with no events" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccess(DownstreamStub.GET, downstreamEisUri, downstreamQueryParams, Status.OK, emptyGetMovementHistoryEventsEISResponseJson)
          }

          val response: WSResponse = await(request().get())
          response.status shouldBe Status.OK
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe Json.arr()
        }
      }

      "return an error" when {

        "downstream call returns unencoded xml" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccess(DownstreamStub.GET, downstreamEisUri, downstreamQueryParams, Status.OK, notEncodedGetMovementHistoryEISEventsJson)
          }

          val response: WSResponse = await(request().get())
          response.status shouldBe Status.INTERNAL_SERVER_ERROR
          response.json shouldBe Json.toJson(EISJsonParsingError(Seq(JsonValidationError("{\"obj\":[{\"msg\":[\"Illegal base64 character 3c\"],\"args\":[]}]}"))))

        }
        "downstream call returns an invalid xml" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccess(DownstreamStub.GET, downstreamEisUri, downstreamQueryParams, Status.OK, invalidGetMovementHistoryEventsEISResponseJson)
          }

          val response: WSResponse = await(request().get())
          response.status shouldBe Status.INTERNAL_SERVER_ERROR
          response.json shouldBe Json.toJson(EISJsonParsingError(Seq(JsonValidationError("{\"obj\":[{\"msg\":[\"{\\\"obj\\\":[{\\\"msg\\\":[\\\"XML failed to parse, with the following errors:\\\\n - EmptyError(//MovementHistory//Events//EventType)\\\"],\\\"args\\\":[]}]}\"],\"args\":[]}]}"))))
        }
        "downstream call returns an unexpected http response" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccess(DownstreamStub.GET, downstreamEisUri, downstreamQueryParams, Status.NO_CONTENT, getMovementHistoryEventsEISResponseJson)
          }

          val response: WSResponse = await(request().get())
          response.status shouldBe Status.INTERNAL_SERVER_ERROR
          response.json shouldBe Json.toJson(ErrorResponse.EISUnknownError(""))
        }
      }
    }
  }
}
