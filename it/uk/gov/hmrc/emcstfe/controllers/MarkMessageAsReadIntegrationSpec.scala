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
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.featureswitch.core.config.{FeatureSwitching, SendToEIS}
import uk.gov.hmrc.emcstfe.fixtures.MarkMessageAsReadFixtures
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse._
import uk.gov.hmrc.emcstfe.stubs.{AuthStub, DownstreamStub}
import uk.gov.hmrc.emcstfe.support.IntegrationBaseSpec

import scala.xml.XML

class MarkMessageAsReadIntegrationSpec extends IntegrationBaseSpec with MarkMessageAsReadFixtures with FeatureSwitching {

  override lazy val config = app.injector.instanceOf[AppConfig]
  
  private abstract class Test(sendToEis: Boolean = true) {
    def setupStubs(): StubMapping

    def uri: String = s"/message/$testErn/$testMessageId"

    def eisUri: String = "/emcs/messages/v1/message"
    def chrisUri: String = "/ChRISOSB/EMCS/EMCSApplicationService/2"

    def downstreamQueryParams: Map[String, String] = Map(
      "exciseregistrationnumber" -> testErn,
      "uniquemessageid" -> testMessageId
    )

    def request(): WSRequest = {
      if(sendToEis) enable(SendToEIS) else disable(SendToEIS)
      setupStubs()
      buildRequest(uri)
    }
  }

  "Calling the get messages endpoint" when {

    "when sending to EIS" when {
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


    "when sending to ChRIS" when {
      "user is unauthorised" must {
        s"return FORBIDDEN ($FORBIDDEN)" in new Test(sendToEis = false) {
          override def setupStubs(): StubMapping = {
            AuthStub.unauthorised()
          }

          val response: WSResponse = await(request().put(""))
          response.status shouldBe FORBIDDEN
        }
      }

      "user is authorised" when {

        "return forbidden" when {
          "the ERN requested does not match the ERN of the credential" in new Test(sendToEis = false) {
            override def setupStubs(): StubMapping = {
              AuthStub.authorised("WrongERN")
            }

            val response: WSResponse = await(request().put(""))
            response.status shouldBe Status.FORBIDDEN
          }
        }

        "return a success" when {
          "all downstream calls are successful" in new Test(sendToEis = false) {
            override def setupStubs(): StubMapping = {
              AuthStub.authorised()
              DownstreamStub.onSuccess(DownstreamStub.POST, chrisUri, Status.OK, XML.loadString(markMessageAsReadChrisXml))
            }

            val response: WSResponse = await(request().put(""))
            response.status shouldBe Status.OK
            response.header("Content-Type") shouldBe Some("application/json")
            response.json shouldBe markMessageAsReadJson
          }
        }
        "return an error" when {
          "downstream call returns unexpected XML" in new Test(sendToEis = false) {
            override def setupStubs(): StubMapping = {
              AuthStub.authorised()
              DownstreamStub.onSuccess(
                DownstreamStub.POST,
                chrisUri,
                Status.OK,
                <Errors>
                  <Error>Something went wrong</Error>
                </Errors>
              )
            }

            val response: WSResponse = await(request().put(""))
            response.status shouldBe Status.INTERNAL_SERVER_ERROR
            response.header("Content-Type") shouldBe Some("application/json")
            response.json shouldBe Json.toJson(SoapExtractionError)
          }
          "downstream call returns something other than XML" in new Test(sendToEis = false) {

            override def setupStubs(): StubMapping = {
              AuthStub.authorised()
              DownstreamStub.onSuccess(DownstreamStub.POST, chrisUri, Status.OK, Json.obj())
            }

            val response: WSResponse = await(request().put(""))
            response.status shouldBe Status.INTERNAL_SERVER_ERROR
            response.header("Content-Type") shouldBe Some("application/json")
            response.json shouldBe Json.toJson(XmlValidationError)
          }
          "downstream call returns a non-200 HTTP response" in new Test(sendToEis = false) {

            override def setupStubs(): StubMapping = {
              AuthStub.authorised()
              DownstreamStub.onSuccess(DownstreamStub.POST, chrisUri, Status.INTERNAL_SERVER_ERROR, Json.obj())
            }

            val response: WSResponse = await(request().put(""))
            response.status shouldBe Status.INTERNAL_SERVER_ERROR
            response.header("Content-Type") shouldBe Some("application/json")
            response.json shouldBe Json.toJson(UnexpectedDownstreamResponseError)
          }
        }
      }
    }
  }
}
