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

package uk.gov.hmrc.emcstfe.connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.http.Fault
import org.scalatest.concurrent.ScalaFutures
import play.api.Application
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NOT_FOUND, OK}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers.AUTHORIZATION
import uk.gov.hmrc.emcstfe.connectors.userAllowList.UserAllowListConnector
import uk.gov.hmrc.emcstfe.models.request.CheckUserAllowListRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.UnexpectedDownstreamResponseError
import uk.gov.hmrc.emcstfe.support.IntegrationBaseSpec
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.SECONDS

class UserAllowListConnectorISpec
  extends IntegrationBaseSpec with ScalaFutures {

  override implicit lazy val hc: HeaderCarrier = HeaderCarrier()
  override implicit lazy val ec: ExecutionContext = global

  override lazy val app: Application =
    new GuiceApplicationBuilder()
      .configure(
        "microservice.services.user-allow-list.port" -> wireMockServer.port,
        "internal-auth.token" -> "token"
      )
      .build()

  private lazy val connector: UserAllowListConnector = app.injector.instanceOf[UserAllowListConnector]

  ".check" must {

    val service = "emcs-tfe"
    val feature = "reportOfReceipt"
    val url = s"/user-allow-list/$service/$feature/check"
    val request = CheckUserAllowListRequest("value")

    "return true when the server responds OK" in {

      wireMockServer.stubFor(
        post(urlEqualTo(url))
          .withHeader(AUTHORIZATION, equalTo("token"))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(request))))
          .willReturn(aResponse().withStatus(OK))
      )

      connector.check(request)(hc, ec).futureValue shouldBe Right(true)
    }

    "return false when the server responds NOT_FOUND" in {

      wireMockServer.stubFor(
        post(urlEqualTo(url))
          .withHeader(AUTHORIZATION, equalTo("token"))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(request))))
          .willReturn(aResponse().withStatus(NOT_FOUND))
      )

      connector.check(request)(hc, ec).futureValue shouldBe Right(false)
    }

    "fail when the server responds with any other status" in {

      wireMockServer.stubFor(
        post(urlEqualTo(url))
          .withHeader(AUTHORIZATION, equalTo("token"))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(request))))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.check(request)(hc, ec).futureValue shouldBe Left(UnexpectedDownstreamResponseError)
    }

    "fail when the connection fails" in {

      wireMockServer.stubFor(
        post(urlEqualTo(url))
          .withHeader(AUTHORIZATION, equalTo("token"))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(request))))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      await(connector.check(request)(hc, ec), 100, SECONDS) shouldBe Left(UnexpectedDownstreamResponseError)
    }
  }
}
