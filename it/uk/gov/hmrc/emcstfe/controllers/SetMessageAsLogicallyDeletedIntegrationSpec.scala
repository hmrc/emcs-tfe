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

package uk.gov.hmrc.emcstfe.controllers

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.Status
import play.api.http.Status.FORBIDDEN
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.featureswitch.core.config.{FeatureSwitching, SendToEIS}
import uk.gov.hmrc.emcstfe.fixtures.SetMessageAsLogicallyDeletedFixtures
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse._
import uk.gov.hmrc.emcstfe.stubs.{AuthStub, DownstreamStub}
import uk.gov.hmrc.emcstfe.support.IntegrationBaseSpec

import scala.xml.XML

class SetMessageAsLogicallyDeletedIntegrationSpec extends IntegrationBaseSpec with SetMessageAsLogicallyDeletedFixtures with FeatureSwitching {

  override val config = app.injector.instanceOf[AppConfig]

  private abstract class Test(sendToEIS: Boolean = true) {
    def setupStubs(): StubMapping

    def uri: String = s"/message/$testErn/$testMessageId"

    def eisUri: String = s"/emcs/messages/v1/message"

    def chrisUri: String = "/ChRISOSB/EMCS/EMCSApplicationService/2"

    def downstreamQueryParams: Map[String, String] = Map(
      "exciseregistrationnumber" -> testErn,
      "uniquemessageid" -> testMessageId
    )

    def request(): WSRequest = {
      if (sendToEIS) enable(SendToEIS) else disable(SendToEIS)
      setupStubs()
      buildRequest(uri)
    }
  }

  "Calling the set message as logically deleted endpoint" when {

    "user is unauthorised" must {
      s"return FORBIDDEN ($FORBIDDEN)" in new Test {
        override def setupStubs(): StubMapping = {
          AuthStub.unauthorised()
        }

        val response: WSResponse = await(request().delete())
        response.status shouldBe FORBIDDEN
      }
    }

    "user is authorised" when {

      "return forbidden" when {
        "the ERN requested does not match the ERN of the credential" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised("WrongERN")
          }

          val response: WSResponse = await(request().delete())
          response.status shouldBe Status.FORBIDDEN
        }
      }

      "return a success" when {
        "calls are sent to EIS" when {
          "all downstream calls are successful" in new Test {
            override def setupStubs(): StubMapping = {
              AuthStub.authorised()
              DownstreamStub.onSuccess(DownstreamStub.DELETE, eisUri, downstreamQueryParams, Status.OK, setMessageAsLogicallyDeletedDownstreamJson)
            }

            val response: WSResponse = await(request().delete())
            response.status shouldBe Status.OK
            response.header("Content-Type") shouldBe Some("application/json")
            response.json shouldBe setMessageAsLogicallyDeletedJson
          }

          "return an error" when {
            "downstream call returns an unexpected HTTP response" in new Test {
              override def setupStubs(): StubMapping = {
                AuthStub.authorised()
                DownstreamStub.onSuccess(DownstreamStub.DELETE, eisUri, downstreamQueryParams, Status.NO_CONTENT, Json.obj())
              }

              val response: WSResponse = await(request().delete())
              response.status shouldBe Status.INTERNAL_SERVER_ERROR
              response.header("Content-Type") shouldBe Some("application/json")
              response.json shouldBe Json.toJson(EISUnknownError(""))
            }
          }
        }

        "calls are sent to ChRIS" when {
          "all downstream calls are successful" in new Test(sendToEIS = false) {
            override def setupStubs(): StubMapping = {
              AuthStub.authorised()
              DownstreamStub.onSuccess(DownstreamStub.POST, chrisUri, Status.OK, XML.loadString(setMessageAsLogicallyDeletedXMLResponse))
            }

            val response: WSResponse = await(request().delete())
            response.status shouldBe Status.OK
            response.header("Content-Type") shouldBe Some("application/json")
            response.json shouldBe setMessageAsLogicallyDeletedJson
          }
          "return an error" when {
            "downstream call returns unexpected XML" in new Test(sendToEIS = false) {
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

              val response: WSResponse = await(request().delete())
              response.status shouldBe Status.INTERNAL_SERVER_ERROR
              response.header("Content-Type") shouldBe Some("application/json")
              response.json shouldBe Json.toJson(SoapExtractionError)
            }
            "downstream call returns something other than XML" in new Test(sendToEIS = false) {
              val referenceDataResponseBody: JsValue = Json.obj("message" -> "Success!")

              override def setupStubs(): StubMapping = {
                AuthStub.authorised()
                DownstreamStub.onSuccess(DownstreamStub.POST, chrisUri, Status.OK, referenceDataResponseBody)
              }

              val response: WSResponse = await(request().delete())
              response.status shouldBe Status.INTERNAL_SERVER_ERROR
              response.header("Content-Type") shouldBe Some("application/json")
              response.json shouldBe Json.toJson(XmlValidationError)
            }
            "downstream call returns a non-200 HTTP response" in new Test(sendToEIS = false) {

              override def setupStubs(): StubMapping = {
                AuthStub.authorised()
                DownstreamStub.onSuccess(DownstreamStub.POST, chrisUri, Status.INTERNAL_SERVER_ERROR, Json.obj())
              }

              val response: WSResponse = await(request().delete())
              response.status shouldBe Status.INTERNAL_SERVER_ERROR
              response.header("Content-Type") shouldBe Some("application/json")
              response.json shouldBe Json.toJson(UnexpectedDownstreamResponseError)
            }
          }
        }
      }
    }
  }
}
