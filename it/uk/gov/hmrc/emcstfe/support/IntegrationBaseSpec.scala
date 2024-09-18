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

package uk.gov.hmrc.emcstfe.support

import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, OptionValues}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.http.HeaderNames
import play.api.http.Status.OK
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import play.api.{Application, Environment, Mode}
import uk.gov.hmrc.emcstfe.fixtures.BaseFixtures
import uk.gov.hmrc.emcstfe.stubs.DownstreamStub

trait IntegrationBaseSpec
  extends UnitSpec
    with WireMockHelper
    with GuiceOneServerPerSuite
    with BeforeAndAfterEach
    with BeforeAndAfterAll
    with OptionValues
    with BaseFixtures {

  lazy val client: WSClient = app.injector.instanceOf[WSClient]

  def servicesConfig: Map[String, _] = Map(
    "microservice.services.auth.port" -> WireMockHelper.wireMockPort,
    "microservice.services.eis.port" -> WireMockHelper.wireMockPort,
    "microservice.services.emcs-tfe-reference-data.port" -> WireMockHelper.wireMockPort,
    "auditing.consumer.baseUri.port" -> WireMockHelper.wireMockPort,
    "play.http.router" -> "testOnlyDoNotUseInAppConf.Routes",
    "createMovementUserAnswers.TTL" -> testTtl,
    "createMovementUserAnswers.replaceIndexes" -> testReplaceIndexes,
    "getMovement.TTL" -> testTtl,
    "getMovement.replaceIndexes" -> testReplaceIndexes.toString
  )

  override implicit lazy val app: Application = new GuiceApplicationBuilder()
    .in(Environment.simple(mode = Mode.Dev))
    .configure(servicesConfig)
    .build()

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    startWireMock()
    DownstreamStub.onSuccess(DownstreamStub.POST, "/write/audit", OK, Json.obj())
    DownstreamStub.onSuccess(DownstreamStub.POST, "/write/audit/merged", OK, Json.obj())
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    stopWireMock()
  }

  def buildRequest(path: String, extraHeaders: (String, String)*): WSRequest = client
    .url(s"http://localhost:$port/emcs-tfe$path")
    .withHttpHeaders(Seq(HeaderNames.AUTHORIZATION -> testAuthToken) ++ extraHeaders: _*)
    .withFollowRedirects(false)

  def document(response: WSResponse): JsValue = Json.parse(response.body)
}
