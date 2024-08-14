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
import play.api.libs.json.{JsValue, Json, JsonValidationError}
import play.api.libs.ws.{WSRequest, WSResponse}
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.featureswitch.core.config.{FeatureSwitching, SendToEIS}
import uk.gov.hmrc.emcstfe.fixtures.GetMovementListFixture
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse._
import uk.gov.hmrc.emcstfe.stubs.{AuthStub, DownstreamStub}
import uk.gov.hmrc.emcstfe.support.IntegrationBaseSpec

import scala.xml.XML

class GetMovementListIntegrationSpec extends IntegrationBaseSpec with GetMovementListFixture with FeatureSwitching {

  override val config: AppConfig = app.injector.instanceOf[AppConfig]

  private trait Test {
    def setupStubs(): StubMapping

    def uri: String = s"/movements/$testErn"

    def downstreamUri: String = "/ChRISOSB/EMCS/EMCSApplicationService/2"

    def downstreamEisUri: String = "/emcs/movements/v1/movements"

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

    "user is authorised" must {

      "return forbidden" when {
        "the ERN requested does not match the ERN of the credential" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised("WrongERN")
          }

          val response: WSResponse = await(request().get())
          response.status shouldBe Status.FORBIDDEN
        }
      }

      "when calling ChRIS" should {

        "return a success" when {
          "all downstream calls are successful" in new Test {
            override def setupStubs(): StubMapping = {
              disable(SendToEIS)
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
              disable(SendToEIS)
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
              disable(SendToEIS)
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
              disable(SendToEIS)
              DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Status.INTERNAL_SERVER_ERROR, referenceDataResponseBody)
            }

            val response: WSResponse = await(request().get())
            response.status shouldBe Status.INTERNAL_SERVER_ERROR
            response.header("Content-Type") shouldBe Some("application/json")
            response.json shouldBe Json.toJson(UnexpectedDownstreamResponseError)
          }
        }
      }

      "when calling EIS" should {

        val requestQueryParams: Map[String, String] = Map(
          "exciseregistrationnumber" -> "GBWK000001234",
          "traderrole" -> "both",
          "sortfield" -> "dateofdispatch",
          "sortorder" -> "D",
          "startposition" -> "0",
          "maxnotoreturn" -> "30"
        )

        "return a success" when {
          "all downstream calls are successful" in new Test {
            override def setupStubs(): StubMapping = {
              enable(SendToEIS)
              AuthStub.authorised()
              DownstreamStub.onSuccess(DownstreamStub.GET, downstreamEisUri, requestQueryParams, Status.OK, getMovementListJsonResponse)
            }

            val response: WSResponse = await(request().get())
            response.status shouldBe Status.OK
            response.header("Content-Type") shouldBe Some("application/json")
            response.json shouldBe getMovementListJson
          }
        }

        "return an error" when {
          "downstream call returns unexpected encoded XML" in new Test {
            val jsonResponse = Json.obj(
              "exciseRegistrationNumber" -> testErn,
              "dateTime" -> "2023-09-07T12:39:20.354Z",
              "message" -> "PHZhbHVlPm5ldmVyIGdvbm5hIGdpdmUgeW91IHVwPC92YWx1ZT4="
            )

            override def setupStubs(): StubMapping = {
              enable(SendToEIS)
              AuthStub.authorised()
              DownstreamStub.onSuccess(DownstreamStub.GET, downstreamEisUri, requestQueryParams, Status.OK, jsonResponse)
            }

            val response: WSResponse = await(request().get())
            response.status shouldBe Status.INTERNAL_SERVER_ERROR
            response.header("Content-Type") shouldBe Some("application/json")
            response.json shouldBe Json.toJson(EISJsonParsingError(Seq(JsonValidationError("{\"obj\":[{\"msg\":[\"{\\\"obj\\\":[{\\\"msg\\\":[\\\"XML failed to parse, with the following errors:\\\\n - EmptyError(/CountOfMovementsAvailable)\\\"],\\\"args\\\":[]}]}\"],\"args\":[]}]}"))
            ))
          }

          "downstream call returns something other than the expected response" in new Test {
            val responseBody: JsValue = Json.obj("message" -> "Success!")

            override def setupStubs(): StubMapping = {
              enable(SendToEIS)
              AuthStub.authorised()
              DownstreamStub.onSuccess(DownstreamStub.GET, downstreamEisUri, requestQueryParams, Status.OK, responseBody)
            }

            val response: WSResponse = await(request().get())
            response.status shouldBe Status.INTERNAL_SERVER_ERROR
            response.header("Content-Type") shouldBe Some("application/json")
            response.json shouldBe Json.toJson(EISJsonParsingError(Seq(JsonValidationError("{\"obj\":[{\"msg\":[\"Illegal base64 character 21\"],\"args\":[]}]}"))))
          }

          "downstream call returns a non-200 HTTP response" in new Test {
            val responseBody: JsValue = Json.parse(
              s"""
                 |{
                 |   "message": "test message"
                 |}
                 |""".stripMargin
            )

            override def setupStubs(): StubMapping = {
              enable(SendToEIS)
              AuthStub.authorised()
              DownstreamStub.onSuccess(DownstreamStub.GET, downstreamEisUri, requestQueryParams, Status.INTERNAL_SERVER_ERROR, responseBody)
            }

            val response: WSResponse = await(request().get())
            response.status shouldBe Status.INTERNAL_SERVER_ERROR
            response.header("Content-Type") shouldBe Some("application/json")
            response.json shouldBe Json.toJson(EISInternalServerError("{\"message\":\"test message\"}"))
          }
        }
      }
    }
  }
}
