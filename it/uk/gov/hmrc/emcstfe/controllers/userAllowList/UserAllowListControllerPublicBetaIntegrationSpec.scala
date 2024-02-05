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
import uk.gov.hmrc.emcstfe.stubs.{AuthStub, DownstreamStub}
import uk.gov.hmrc.emcstfe.support.{IntegrationBaseSpec, WireMockHelper}

class UserAllowListControllerPublicBetaIntegrationSpec extends IntegrationBaseSpec {

  override def servicesConfig: Map[String, _] = Map(
    "microservice.services.auth.port" -> WireMockHelper.wireMockPort,
    "microservice.services.chris.port" -> WireMockHelper.wireMockPort,
    "microservice.services.eis.port" -> WireMockHelper.wireMockPort,
    "microservice.services.user-allow-list.port" -> WireMockHelper.wireMockPort,
    "auditing.consumer.baseUri.port" -> WireMockHelper.wireMockPort,
    "play.http.router" -> "testOnlyDoNotUseInAppConf.Routes",
    "createMovementUserAnswers.TTL" -> testTtl,
    "createMovementUserAnswers.replaceIndexes" -> testReplaceIndexes,
    "getMovement.TTL" -> testTtl,
    "getMovement.replaceIndexes" -> testReplaceIndexes.toString,
    "beta.private.enabled" -> "false",
    "beta.public.enabled" -> "true",
    "beta.public.createMovement.trafficPercentage" -> "50",
    "beta.public.changeDestination.trafficPercentage" -> "50",
    "beta.public.navHub.trafficPercentage" -> "50"
  )

  abstract class Test(serviceName: String, ern: String) {

    def setupStubs(): StubMapping

    def uri: String = s"/beta/eligibility/$ern/$serviceName"

    def downstreamUri: String = s"/user-allow-list/emcs-tfe/$serviceName/check"

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri, "Content-Type" -> "application/json")
    }
  }

  "Calling the eligibility endpoint (public beta enabled)" should {

    "return OK" when {

      "the ERN is in the traffic percentage for public beta" in new Test("changeDestination", ern = "GBRC123454989") {
        override def setupStubs(): StubMapping = {
          AuthStub.authorised("GBRC123454989")
          DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Status.NOT_FOUND, Json.obj())
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.OK
      }

      "the ERN is not in the traffic percentage for public beta but was in private beta" in new Test("navHub", ern = "GBRC123458889") {
        override def setupStubs(): StubMapping = {
          AuthStub.authorised("GBRC123458889")
          DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Status.OK, Json.obj())
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.OK
      }

    }

    "return NOT_FOUND" when {

      "the ERN is not in the traffic percentage nor was in private beta" in new Test("createMovement", ern = "GBRC123458889") {
        override def setupStubs(): StubMapping = {
          AuthStub.authorised("GBRC123458889")
          DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Status.NOT_FOUND, Json.obj())
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.NOT_FOUND
      }
    }

    "return ISE" when {

      "there is a fault calling the user allow list service" in new Test("createMovement", ern = "GBRC123458889") {
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
