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
import play.api.libs.json.Json
import play.api.libs.ws.{WSRequest, WSResponse}
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.featureswitch.core.config.{EnableKnownFactsViaETDS18, FeatureSwitching}
import uk.gov.hmrc.emcstfe.fixtures.TraderKnownFactsFixtures
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse._
import uk.gov.hmrc.emcstfe.stubs.{AuthStub, DownstreamStub}
import uk.gov.hmrc.emcstfe.support.IntegrationBaseSpec

class TraderKnownFactsIntegrationSpec extends IntegrationBaseSpec with TraderKnownFactsFixtures with FeatureSwitching {

  val config: AppConfig = app.injector.instanceOf[AppConfig]

  private trait Test {
    def setupStubs(): StubMapping

    def uri: String = s"/trader-known-facts"

    def emcsTfeReferenceDataUrl: String = "/emcs-tfe-reference-data/oracle/trader-known-facts"
    def etdsKnownFactsUrl: String = s"/etds/trader/knownfacts/$testErn"

    def downstreamQueryParams: Map[String, String] = Map(
      "exciseRegistrationId" -> testErn
    )

    def request(useEtds18API: Boolean): WSRequest = {
      if (useEtds18API) enable(EnableKnownFactsViaETDS18) else disable(EnableKnownFactsViaETDS18)
      setupStubs()
      buildRequest(uri).withQueryStringParameters("exciseRegistrationId" -> testErn)
    }

  }

  "Calling the get trader known facts endpoint" when {

    "user is unauthorised" must {
      s"return FORBIDDEN ($FORBIDDEN)" in new Test {
        override def setupStubs(): StubMapping = {
          AuthStub.unauthorised()
        }

        val response: WSResponse = await(request(useEtds18API = true).get())
        response.status shouldBe FORBIDDEN
      }
    }

    "user is authorised" when {

      "return forbidden" when {
        "the ERN requested does not match the ERN of the credential" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised("WrongERN")
          }

          val response: WSResponse = await(request(useEtds18API = true).get())
          response.status shouldBe Status.FORBIDDEN
        }
      }

      "EnableKnownFactsViaETDS18 is disabled" should {

        "return a success" when {
          "all downstream calls are successful" in new Test {
            override def setupStubs(): StubMapping = {
              AuthStub.authorised()
              DownstreamStub.onSuccess(DownstreamStub.GET, emcsTfeReferenceDataUrl, downstreamQueryParams, Status.OK, Json.parse(traderKnownFactsCandEJson))
            }

            val response: WSResponse = await(request(useEtds18API = false).get())
            response.status shouldBe Status.OK
            response.header("Content-Type") shouldBe Some("application/json")
            response.json shouldBe Json.parse(testTraderKnownFactsJson)
          }
          "downstream returns a 204" in new Test {
            override def setupStubs(): StubMapping = {
              AuthStub.authorised()
              DownstreamStub.onSuccess(DownstreamStub.GET, emcsTfeReferenceDataUrl, downstreamQueryParams, Status.NO_CONTENT, Json.obj())
            }

            val response: WSResponse = await(request(useEtds18API = false).get())
            response.status shouldBe Status.NO_CONTENT
          }
        }
        "return an error" when {
          "downstream call returns an unexpected HTTP response" in new Test {
            override def setupStubs(): StubMapping = {
              AuthStub.authorised()
              DownstreamStub.onSuccess(DownstreamStub.GET, emcsTfeReferenceDataUrl, downstreamQueryParams, Status.INTERNAL_SERVER_ERROR, Json.obj())
            }

            val response: WSResponse = await(request(useEtds18API = false).get())
            response.status shouldBe Status.INTERNAL_SERVER_ERROR
            response.header("Content-Type") shouldBe Some("application/json")
            response.json shouldBe Json.toJson(UnexpectedDownstreamResponseError)
          }
        }
      }

      "EnableKnownFactsViaETDS18 is enabled" should {

        "return a success" when {
          "all downstream calls are successful" in new Test {
            override def setupStubs(): StubMapping = {
              AuthStub.authorised()
              DownstreamStub.onSuccess(DownstreamStub.GET, etdsKnownFactsUrl, Status.OK, Json.parse(traderKnownFactsCandEJson))
            }

            val response: WSResponse = await(request(useEtds18API = true).get())
            response.status shouldBe Status.OK
            response.header("Content-Type") shouldBe Some("application/json")
            response.json shouldBe Json.parse(testTraderKnownFactsJson)
          }
        }
        "return an error" when {
          "downstream call returns an unexpected HTTP response" in new Test {
            override def setupStubs(): StubMapping = {
              AuthStub.authorised()
              DownstreamStub.onSuccess(DownstreamStub.GET, etdsKnownFactsUrl, Status.INTERNAL_SERVER_ERROR, Json.obj())
            }

            val response: WSResponse = await(request(useEtds18API = true).get())
            response.status shouldBe Status.INTERNAL_SERVER_ERROR
            response.header("Content-Type") shouldBe Some("application/json")
            response.json shouldBe Json.toJson(EISInternalServerError("{}"))
          }
        }
      }
    }
  }

}
