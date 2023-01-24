/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import uk.gov.hmrc.emcstfe.fixtures.GetMovementFixture
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse._
import uk.gov.hmrc.emcstfe.stubs.DownstreamStub
import uk.gov.hmrc.emcstfe.support.IntegrationBaseSpec

import scala.xml.XML

class GetMovementIntegrationSpec extends IntegrationBaseSpec with GetMovementFixture {

  private trait Test {
    def setupStubs(): StubMapping

    val exciseRegistrationNumber: String = "MyERN"
    val arc: String = "MyARC"

    def uri: String = s"/movement/$exciseRegistrationNumber/$arc"
    def downstreamUri: String = s"/ChRISOSB/EMCS/EMCSApplicationService/2"

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri)
    }
  }

  "Calling the get movement endpoint" should {
    "return a success" when {
      "all downstream calls are successful" in new Test {
        override def setupStubs(): StubMapping = {
          DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Status.OK, XML.loadString(getMovementSoapWrapper))
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe getMovementJson
      }
    }
    "return an error" when {
      "downstream call returns unexpected XML" in new Test {
        override def setupStubs(): StubMapping = {
          DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Status.OK, <Message>Success!</Message>)
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.INTERNAL_SERVER_ERROR
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe Json.toJson(SoapExtractionError)
      }
      "downstream call returns something other than XML" in new Test {
        val referenceDataResponseBody: JsValue = Json.obj("message" -> "Success!")

        override def setupStubs(): StubMapping = {
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
          DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Status.INTERNAL_SERVER_ERROR, referenceDataResponseBody)
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.INTERNAL_SERVER_ERROR
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe Json.toJson(UnexpectedDownstreamResponseError)
      }
    }
  }
}
