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

package uk.gov.hmrc.emcstfe.controllers.userAllowList

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.Status
import play.api.libs.json.Json
import play.api.libs.ws.{WSRequest, WSResponse}
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.featureswitch.core.config.{EnablePrivateBeta, EnablePublicBetaThrottling, FeatureSwitching}
import uk.gov.hmrc.emcstfe.stubs.{AuthStub, DownstreamStub}
import uk.gov.hmrc.emcstfe.support.IntegrationBaseSpec

class UserAllowListControllerIntegrationSpec extends IntegrationBaseSpec with FeatureSwitching {

  override val config: AppConfig = app.injector.instanceOf[AppConfig]

  abstract class Test(serviceName: String, ern: String, isPrivateBetaEnabled: Boolean, isPublicBetaEnabled: Boolean) {

    def setupStubs(): StubMapping

    def uri: String = s"/beta/eligibility/$ern/$serviceName"

    def downstreamUri: String = s"/user-allow-list/emcs-tfe/$serviceName/check"

    def request(): WSRequest = {
      if(isPrivateBetaEnabled) enable(EnablePrivateBeta) else disable(EnablePrivateBeta)
      if(isPublicBetaEnabled) enable(EnablePublicBetaThrottling) else disable(EnablePublicBetaThrottling)
      setupStubs()
      buildRequest(uri, "Content-Type" -> "application/json")
    }
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    sys.props -= EnablePrivateBeta.configName
    sys.props -= EnablePublicBetaThrottling.configName
  }

  "Calling the eligibility endpoint (private beta enabled)" should {

    "return OK" when {

      "the ERN is in the private beta list (only private beta feature enabled)" in new Test("createMovement", testErn, isPrivateBetaEnabled = true, isPublicBetaEnabled = false) {
        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Status.OK, Json.obj())
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.OK
      }

    }

    "return NO_CONTENT" when {

      "the ERN is not in the private beta list (only private beta feature enabled)" in new Test("tfeNavHub", ern = testErn, isPrivateBetaEnabled = true, isPublicBetaEnabled = false) {
        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Status.NOT_FOUND, Json.obj())
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.NO_CONTENT
      }

    }

    "return ISE" when {

      "there is a fault calling the user allow list service" in new Test("createMovement", ern = "GBRC123458889", isPrivateBetaEnabled = true, isPublicBetaEnabled = false) {
        override def setupStubs(): StubMapping = {
          AuthStub.authorised("GBRC123458889")
          DownstreamStub.onError(DownstreamStub.POST, downstreamUri, Status.BAD_GATEWAY)
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }
  }

  "Calling the eligibility endpoint (public beta enabled)" should {

    "return OK" when {

      "the ERN is in the traffic percentage for public beta" in new Test("changeDestination", ern = "GBRC123454989", isPrivateBetaEnabled = false, isPublicBetaEnabled = true) {
        override def setupStubs(): StubMapping = {
          AuthStub.authorised("GBRC123454989")
          DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Status.NOT_FOUND, Json.obj())
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.OK
      }

      "the ERN is not in the traffic percentage for public beta but was in private beta" in new Test("tfeNavHub", ern = "GBRC123458889", isPrivateBetaEnabled = false, isPublicBetaEnabled = true) {
        override def setupStubs(): StubMapping = {
          AuthStub.authorised("GBRC123458889")
          DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Status.OK, Json.obj())
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.OK
      }

    }

    "return NO_CONTENT" when {

      "the ERN is not in the traffic percentage nor was in private beta" in new Test("createMovement", ern = "GBWK812541450", isPrivateBetaEnabled = false, isPublicBetaEnabled = true) {
        override def setupStubs(): StubMapping = {
          AuthStub.authorised("GBWK812541450") //79%
          DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Status.NOT_FOUND, Json.obj())
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.NO_CONTENT
      }
    }

    "return ISE" when {

      "there is a fault calling the user allow list service" in new Test("createMovement", ern = "GBRC123458889", isPrivateBetaEnabled = false, isPublicBetaEnabled = true) {
        override def setupStubs(): StubMapping = {
          AuthStub.authorised("GBRC123458889")
          DownstreamStub.onError(DownstreamStub.POST, downstreamUri, Status.BAD_GATEWAY)
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }
  }


}
