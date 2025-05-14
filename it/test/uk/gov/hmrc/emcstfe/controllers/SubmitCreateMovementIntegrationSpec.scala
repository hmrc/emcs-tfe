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
import play.api.libs.json.Json
import play.api.libs.ws.{WSRequest, WSResponse}
import test.uk.gov.hmrc.emcstfe.stubs.{AuthStub, DownstreamStub}
import test.uk.gov.hmrc.emcstfe.support.IntegrationBaseSpec
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.featureswitch.core.config.{DefaultDraftMovementCorrelationId, FeatureSwitching}
import uk.gov.hmrc.emcstfe.fixtures.CreateMovementFixtures
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse._

class SubmitCreateMovementIntegrationSpec extends IntegrationBaseSpec with CreateMovementFixtures with FeatureSwitching {

  override val config: AppConfig = app.injector.instanceOf[AppConfig]

  private trait Test {
    def setupStubs(): StubMapping

    def uri: String = s"/create-movement/$testErn/$testArc"

    def downstreamEisUri: String = s"/emcs/digital-submit-new-message/v1"

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri, "Content-Type" -> "application/json")
    }

  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    enable(DefaultDraftMovementCorrelationId)
  }

  override def afterEach(): Unit = {
    super.beforeEach()
    sys.props -= DefaultDraftMovementCorrelationId.configName
  }

  "Calling the submit draft movement endpoint" must {

    "return a success" when {

      "all downstream calls are successful" in new Test {

        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          DownstreamStub.onSuccess(DownstreamStub.POST, downstreamEisUri, Status.OK, eisSuccessJson())
        }

        val response: WSResponse = await(request().post(CreateMovementFixtures.createMovementJsonMax))
        response.status shouldBe Status.OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe eisSuccessJson(withSubmittedDraftId = true, submittedDraftId = Some("PORTAL123"))
      }

    }

    "return an error" when {

      "downstream call returns unexpected JSON" in new Test {
        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          DownstreamStub.onError(DownstreamStub.POST, downstreamEisUri, Status.OK, incompleteEisSuccessJson.toString())
        }

        val response: WSResponse = await(request().post(CreateMovementFixtures.createMovementJsonMax))
        response.status shouldBe Status.INTERNAL_SERVER_ERROR
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe Json.obj(
          "message" -> "Errors parsing JSON, errors: List(JsonValidationError(List(error.path.missing),List()))"
        )
      }

      "downstream call returns a 422 (not RIM validation errors)" in new Test {
        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          DownstreamStub.onError(DownstreamStub.POST, downstreamEisUri, Status.UNPROCESSABLE_ENTITY, Json.obj("foo" -> "bar").toString())
        }

        val response: WSResponse = await(request().post(CreateMovementFixtures.createMovementJsonMax))
        response.status shouldBe Status.INTERNAL_SERVER_ERROR
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe Json.obj(
          "message" -> "Request not processed returned by EIS, error response: {\"foo\":\"bar\"}"
        )
      }

      "downstream call returns RIM validation errors" in new Test {
        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          DownstreamStub.onError(DownstreamStub.POST, downstreamEisUri, Status.UNPROCESSABLE_ENTITY, eisRimValidationJsonResponse.toString())
        }

        val response: WSResponse = await(request().post(CreateMovementFixtures.createMovementJsonMax))
        response.status shouldBe Status.UNPROCESSABLE_ENTITY
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe Json.obj(
          "message" -> "Request not processed returned by EIS, correlation ID: 7be1db16-e8fb-4e81-97e5-3d3e2d21f6c4"
        )
      }

      "downstream call returns a non-200 HTTP response" in new Test {

        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          DownstreamStub.onError(DownstreamStub.POST, downstreamEisUri, Status.INTERNAL_SERVER_ERROR, "bad things")
        }

        val response: WSResponse = await(request().post(CreateMovementFixtures.createMovementJsonMax))
        response.status shouldBe Status.INTERNAL_SERVER_ERROR
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe Json.toJson(EISInternalServerError("bad things"))
      }
    }
  }
}
