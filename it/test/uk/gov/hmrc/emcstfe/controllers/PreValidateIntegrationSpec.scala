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

package test.uk.gov.hmrc.emcstfe.controllers

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.{HeaderNames, MimeTypes, Status}
import play.api.libs.json.Json
import play.api.libs.ws.{WSRequest, WSResponse}
import test.uk.gov.hmrc.emcstfe.stubs.{AuthStub, DownstreamStub}
import test.uk.gov.hmrc.emcstfe.support.IntegrationBaseSpec
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.featureswitch.core.config.{EnablePreValidateViaETDS12, FeatureSwitching}
import uk.gov.hmrc.emcstfe.fixtures.PreValidateFixtures

class PreValidateIntegrationSpec
  extends IntegrationBaseSpec
    with PreValidateFixtures
    with FeatureSwitching {

  val config: AppConfig = app.injector.instanceOf[AppConfig]

  private abstract class Test(useEtds12API: Boolean = false) {
    def setupStubs(): StubMapping

    def uri: String = s"/pre-validate-trader/$testErn"

    def downstreamUri: String =
      if (useEtds12API) "/etds/traderprevalidation/v1" else "/emcs/pre-validate-trader/v1"

    def request(): WSRequest = {
      if (useEtds12API) enable(EnablePreValidateViaETDS12) else disable(EnablePreValidateViaETDS12)
      setupStubs()
      buildRequest(uri, "Content-Type" -> "application/json")
    }
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
  }

  "Calling the pre validate trader endpoint" when {
    "using the EMC15B API" should {

      "return a success" when {
        "all downstream calls are successful" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Status.OK, preValidateEmc15bApiResponseAsJson)
          }

          val response: WSResponse = await(request().post(Json.toJson(preValidateTraderModelRequest)))
          response.status shouldBe Status.OK
          response.header(HeaderNames.CONTENT_TYPE) shouldBe Some(MimeTypes.JSON)
          response.json shouldBe preValidateEtds12ApiResponseAsJson
        }
      }

      "return an error" when {
        "downstream call returns unexpected JSON" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onError(DownstreamStub.POST, downstreamUri, Status.OK, "{}")
          }

          val response: WSResponse = await(request().post(Json.toJson(preValidateTraderModelRequest)))
          response.status shouldBe Status.INTERNAL_SERVER_ERROR
          response.header(HeaderNames.CONTENT_TYPE) shouldBe Some(MimeTypes.JSON)
          response.json.toString.contains("Errors parsing JSON") shouldBe true
        }

        "downstream call returns a non-200 HTTP response" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onError(DownstreamStub.POST, downstreamUri, Status.INTERNAL_SERVER_ERROR, "bad things")
          }

          val response: WSResponse = await(request().post(Json.toJson(preValidateTraderModelRequest)))
          response.status shouldBe Status.INTERNAL_SERVER_ERROR
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe Json.obj(
            "message" -> "Request not processed returned by EIS, error response: bad things"
          )
        }

      }

    }

    "using the ETDS12 API" when {

      "passing an entity group" should {
        "return a success" when {
          "all downstream calls are successful" in new Test(useEtds12API = true) {
            override def setupStubs(): StubMapping = {
              AuthStub.authorised()
              DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Status.OK, preValidateEtds12ApiResponseAsJson)
            }

            val response: WSResponse = await(request().post(Json.toJson(preValidateTraderModelRequest)))
            response.status shouldBe Status.OK
            response.header(HeaderNames.CONTENT_TYPE) shouldBe Some(MimeTypes.JSON)
            response.json shouldBe preValidateEtds12ApiResponseAsJson
          }
        }
      }

      "not passing an entity group" should {
        "return a success" when {
          "all downstream calls are successful" in new Test(useEtds12API = true) {
            override def setupStubs(): StubMapping = {
              AuthStub.authorised()
              DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Status.OK, preValidateEtds12ApiResponseAsJson)
            }

            val response: WSResponse = await(request().post(Json.toJson(preValidateTraderModelRequest.copy(entityGroup = None))))
            response.status shouldBe Status.OK
            response.header(HeaderNames.CONTENT_TYPE) shouldBe Some(MimeTypes.JSON)
            response.json shouldBe preValidateEtds12ApiResponseAsJson
          }
        }
      }


      "return an error" when {
        "downstream call returns unexpected JSON" in new Test(useEtds12API = true) {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onError(DownstreamStub.POST, downstreamUri, Status.OK, "{}")
          }

          val response: WSResponse = await(request().post(Json.toJson(preValidateTraderModelRequest)))
          response.status shouldBe Status.INTERNAL_SERVER_ERROR
          response.header(HeaderNames.CONTENT_TYPE) shouldBe Some(MimeTypes.JSON)
          response.json.toString.contains("Errors parsing JSON") shouldBe true
        }

        "downstream call returns a non-200 HTTP response" in new Test(useEtds12API = true) {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onError(DownstreamStub.POST, downstreamUri, Status.INTERNAL_SERVER_ERROR, "bad things")
          }

          val response: WSResponse = await(request().post(Json.toJson(preValidateTraderModelRequest)))
          response.status shouldBe Status.INTERNAL_SERVER_ERROR
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe Json.obj(
            "message" -> "Request not processed returned by EIS, error response: bad things"
          )
        }

      }

    }
  }

}
