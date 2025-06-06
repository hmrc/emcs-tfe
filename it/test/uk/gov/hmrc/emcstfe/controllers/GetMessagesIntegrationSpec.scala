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
import play.api.http.Status
import play.api.http.Status.FORBIDDEN
import play.api.libs.json.{Json, JsonValidationError}
import play.api.libs.ws.{WSRequest, WSResponse}
import uk.gov.hmrc.emcstfe.stubs.{AuthStub, DownstreamStub}
import uk.gov.hmrc.emcstfe.support.IntegrationBaseSpec
import uk.gov.hmrc.emcstfe.fixtures.GetMessagesFixtures
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse._

class GetMessagesIntegrationSpec extends IntegrationBaseSpec with GetMessagesFixtures {

  import GetMessagesResponseFixtures._

  private trait Test {
    def setupStubs(): StubMapping

    def uri: String = s"/messages/$testErn"

    def eisUri: String = s"/emcs/messages/v1/messages"

    val sortField = "readindicator"
    val sortOrder = "D"
    val page = 2
    val maxNoToReturn = 10
    val startPosition = 10

    def queryParams: Seq[(String, String)] = Seq(
      "sortField" -> sortField,
      "sortOrder" -> sortOrder,
      "page" -> page.toString
    )

    def downstreamQueryParams: Map[String, String] = Map(
      "exciseregistrationnumber" -> testErn,
      "sortfield" -> sortField,
      "sortorder" -> sortOrder,
      "startposition" -> startPosition.toString,
      "maxnotoreturn" -> maxNoToReturn.toString,
    )

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri)
        .withQueryStringParameters(queryParams: _*)
    }
  }

  "Calling the get messages endpoint" when {

    "user is unauthorised" must {
      s"return FORBIDDEN ($FORBIDDEN)" in new Test {
        override def setupStubs(): StubMapping = {
          AuthStub.unauthorised()
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe FORBIDDEN
      }
    }

    "user is authorised" when {
      "return forbidden" when {
        "the ERN requested does not match the ERN of the credential" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised("WrongERN")
          }

          val response: WSResponse = await(request().get())
          response.status shouldBe Status.FORBIDDEN
        }
      }

      "return a success" when {
        "all downstream calls are successful" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccess(DownstreamStub.GET, eisUri, downstreamQueryParams, Status.OK, getMessagesResponseDownstreamJson)
          }

          val response: WSResponse = await(request().get())
          response.status shouldBe Status.OK
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe getMessagesResponseJson
        }
      }
      "return an error" when {
        "downstream call returns XML with the wrong encoding" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccess(DownstreamStub.GET, eisUri, downstreamQueryParams, Status.OK, getMessagesResponseDownstreamJsonWrongEncoding)
          }

          val response: WSResponse = await(request().get())
          response.status shouldBe Status.INTERNAL_SERVER_ERROR
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe Json.toJson(EISJsonParsingError(Seq(JsonValidationError("{\"obj\":[{\"msg\":[\"Content is not allowed in prolog.\"],\"args\":[]}]}"))))
        }
        "downstream call returns unencoded XML" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccess(DownstreamStub.GET, eisUri, downstreamQueryParams, Status.OK, getMessagesResponseDownstreamJsonNotEncoded)
          }

          val response: WSResponse = await(request().get())
          response.status shouldBe Status.INTERNAL_SERVER_ERROR
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe Json.toJson(EISJsonParsingError(Seq(JsonValidationError("{\"obj\":[{\"msg\":[\"Illegal base64 character 3c\"],\"args\":[]}]}"))))
        }
        "downstream call returns XML which can't be parsed to JSON" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccess(DownstreamStub.GET, eisUri, downstreamQueryParams, Status.OK, getMessagesResponseDownstreamJsonBadXml)
          }

          val response: WSResponse = await(request().get())
          response.status shouldBe Status.INTERNAL_SERVER_ERROR
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe Json.toJson(EISJsonParsingError(Seq(JsonValidationError("{\"obj\":[{\"msg\":[\"{\\\"obj\\\":[{\\\"msg\\\":[\\\"XML failed to parse, with the following errors:\\\\n - EmptyError(//MessagesDataResponse//TotalNumberOfMessagesAvailable)\\\"],\\\"args\\\":[]}]}\"],\"args\":[]}]}"))))
        }
        "downstream call returns an unexpected HTTP response" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccess(DownstreamStub.GET, eisUri, downstreamQueryParams, Status.NO_CONTENT, Json.obj())
          }

          val response: WSResponse = await(request().get())
          response.status shouldBe Status.INTERNAL_SERVER_ERROR
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe Json.toJson(EISUnknownError(""))
        }
        "bad query parameters" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
          }

          override val sortField: String = "beans"
          override val sortOrder: String = "beans"
          override val page: Int = 0

          val response: WSResponse = await(request().get())
          response.status shouldBe Status.INTERNAL_SERVER_ERROR
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe Json.toJson(QueryParameterError(queryParams))
        }
      }
    }
  }
}
