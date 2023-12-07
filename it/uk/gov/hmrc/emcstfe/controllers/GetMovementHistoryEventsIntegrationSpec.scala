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
import play.api.libs.ws.WSRequest
import uk.gov.hmrc.emcstfe.fixtures.GetMovementHistoryEventsFixture
import uk.gov.hmrc.emcstfe.support.IntegrationBaseSpec
import play.api.libs.ws.WSResponse
import play.api.http.Status.FORBIDDEN
import uk.gov.hmrc.emcstfe.stubs.{AuthStub, DownstreamStub}
import play.api.http.Status
import play.api.libs.json.Json
import play.api.libs.json.JsonValidationError
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.EISJsonParsingError
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse



class GetMovementHistoryEventsIntegrationSpec extends IntegrationBaseSpec with GetMovementHistoryEventsFixture {

  private trait Test {
    def setupStubs(): StubMapping

    def uri: String = s"/movement-history/$testErn/$testArc"

    def downstreamUri: String = s"/emcs/movements/v1/movement-history"

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
        "all downstream calls are succesful" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUri, downstreamQueryParams, Status.OK, getMovementHistoryEventsResponseJson)
          }

          val response: WSResponse = await(request().get())
          response.status shouldBe Status.OK
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe Json.toJson(getMovementHistoryEventsResponseModel)
        }
        "all downstreeam calls are successful with no events" in new Test {
          override def setupStubs():StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUri, downstreamQueryParams, Status.OK, emptyGetMovementHistoryEventsResponseJson)
          }

          val response: WSResponse = await(request().get())
          response.status shouldBe Status.OK
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe Json.toJson(emptyGetMovementHistoryEventsResponseModel)
        }
      }
      "return an error" when {
        "downstream call returns xml with wrong encoding" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()

          }
        }
        "downstream call returns unencoded xml" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUri, downstreamQueryParams, Status.OK, notEncodedGetMovementHistoryEventsJson)
          }

          val response: WSResponse = await(request().get())
          response.status shouldBe Status.INTERNAL_SERVER_ERROR
          response.json shouldBe Json.toJson(EISJsonParsingError(Seq(JsonValidationError("{\"obj\":[{\"msg\":[\"Illegal base64 character 3c\"],\"args\":[]}]}")))) 

        }
        "downstream call returns an invalid xml" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUri, downstreamQueryParams, Status.OK, invalidGetMovementHistoryEventsResponseJson)
          }

          val response: WSResponse = await(request().get())
          response.status shouldBe Status.INTERNAL_SERVER_ERROR
          response.json shouldBe Json.toJson(EISJsonParsingError(Seq(JsonValidationError("{\"obj\":[{\"msg\":[\"{\\\"obj\\\":[{\\\"msg\\\":[\\\"XML failed to parse, with the following errors:\\\\n - EmptyError(//MovementHistory//Events//EventType)\\\"],\\\"args\\\":[]}]}\"],\"args\":[]}]}"))))
        }
        "downstream call returns an unexpected http response" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUri, downstreamQueryParams, Status.NO_CONTENT, getMovementHistoryEventsResponseJson)
          }

          val response: WSResponse = await(request().get())
          response.status shouldBe Status.INTERNAL_SERVER_ERROR
          response.json shouldBe Json.toJson(ErrorResponse.EISUnknownError(""))
        }
      }
    }
  }
}
