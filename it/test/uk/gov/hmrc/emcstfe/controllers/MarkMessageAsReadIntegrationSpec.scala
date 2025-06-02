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
import play.api.libs.json.Json
import play.api.libs.ws.{WSRequest, WSResponse}
import uk.gov.hmrc.emcstfe.stubs.{AuthStub, DownstreamStub}
import uk.gov.hmrc.emcstfe.support.IntegrationBaseSpec
import uk.gov.hmrc.emcstfe.fixtures.MarkMessageAsReadFixtures
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse._

class MarkMessageAsReadIntegrationSpec extends IntegrationBaseSpec with MarkMessageAsReadFixtures {

  private trait Test {
    def setupStubs(): StubMapping

    def uri: String = s"/message/$testErn/$testMessageId"

    def eisUri: String = "/emcs/messages/v1/message"

    def downstreamQueryParams: Map[String, String] = Map(
      "exciseregistrationnumber" -> testErn,
      "uniquemessageid" -> testMessageId
    )

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri)
    }
  }

  "Calling the get messages endpoint" when {
    "user is unauthorised" must {
      s"return FORBIDDEN ($FORBIDDEN)" in new Test() {
        override def setupStubs(): StubMapping = {
          AuthStub.unauthorised()
        }

        val response: WSResponse = await(request().put(""))
        response.status shouldBe FORBIDDEN
      }
    }

    "user is authorised" when {

      "return forbidden" when {
        "the ERN requested does not match the ERN of the credential" in new Test() {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised("WrongERN")
          }

          val response: WSResponse = await(request().put(""))
          response.status shouldBe Status.FORBIDDEN
        }
      }

      "return a success" when {
        "all downstream calls are successful" in new Test() {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccess(DownstreamStub.PUT, eisUri, downstreamQueryParams, Status.OK, markMessageAsReadEisJson)
          }

          val response: WSResponse = await(request().put(""))
          response.status shouldBe Status.OK
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe markMessageAsReadJson
        }
      }
      "return an error" when {
        "downstream call returns an unexpected HTTP response" in new Test() {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccess(DownstreamStub.PUT, eisUri, downstreamQueryParams, Status.NO_CONTENT, Json.obj())
          }

          val response: WSResponse = await(request().put(""))
          response.status shouldBe Status.INTERNAL_SERVER_ERROR
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe Json.toJson(EISUnknownError(""))
        }
      }
    }
  }
}
