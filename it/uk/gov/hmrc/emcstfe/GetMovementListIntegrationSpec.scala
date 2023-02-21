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

package uk.gov.hmrc.emcstfe

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.Status
import play.api.http.Status.FORBIDDEN
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import uk.gov.hmrc.emcstfe.fixtures.GetMovementListFixture
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse._
import uk.gov.hmrc.emcstfe.stubs.{AuthStub, DownstreamStub}
import uk.gov.hmrc.emcstfe.support.IntegrationBaseSpec

import scala.xml.XML

class GetMovementListIntegrationSpec extends IntegrationBaseSpec with GetMovementListFixture {

  private trait Test {
    def setupStubs(): StubMapping

    def uri: String = s"/movements/$testErn"
    def downstreamUri: String = s"/ChRISOSB/EMCS/EMCSApplicationService/2"

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri)
    }
  }

  "Calling the get movement list endpoint" when {

    "user is unauthorised" must {
      s"return FORBIDDEN ($FORBIDDEN)" in new Test {
        override def setupStubs(): StubMapping = {
          AuthStub.unauthorised()
          DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Status.OK, XML.loadString(getMovementListSoapWrapper))
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
            DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Status.OK, XML.loadString(getMovementListSoapWrapper))
          }

          val response: WSResponse = await(request().get())
          response.status shouldBe Status.OK
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe getMovementListJson
        }
      }
      "return an error" when {
        "downstream call returns unexpected XML" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccess(
              DownstreamStub.POST,
              downstreamUri,
              Status.OK,
              <Errors>
                <Error>Something went wrong</Error>
              </Errors>
            )
          }

          val response: WSResponse = await(request().get())
          response.status shouldBe Status.INTERNAL_SERVER_ERROR
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe Json.toJson(SoapExtractionError)
        }
        "downstream call returns something other than XML" in new Test {
          val referenceDataResponseBody: JsValue = Json.obj("message" -> "Success!")

          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Status.OK, referenceDataResponseBody)
          }

          val response: WSResponse = await(request().get())
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

          val response: WSResponse = await(request().get())
          response.status shouldBe Status.INTERNAL_SERVER_ERROR
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe Json.toJson(UnexpectedDownstreamResponseError)
        }
      }
    }
  }
}
