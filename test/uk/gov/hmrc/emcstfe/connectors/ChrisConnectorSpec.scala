/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.connectors

import play.api.http.{HeaderNames, MimeTypes, Status}
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.emcstfe.connector.ChrisConnector
import uk.gov.hmrc.emcstfe.mocks.config.MockAppConfig
import uk.gov.hmrc.emcstfe.mocks.connectors.MockHttpClient
import uk.gov.hmrc.emcstfe.models.request.GetMessageRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{JsonValidationError, UnexpectedDownstreamResponseError, XmlValidationError}
import uk.gov.hmrc.emcstfe.models.response.HelloWorldResponse
import uk.gov.hmrc.emcstfe.support.UnitSpec
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}
import scala.xml.Elem

class ChrisConnectorSpec extends UnitSpec with Status with MimeTypes with HeaderNames with MockAppConfig with MockHttpClient {

  trait Test {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

    val connector = new ChrisConnector(mockHttpClient, mockAppConfig)

    val baseUrl: String = "http://test-BaseUrl"
    MockedAppConfig.chrisUrl.returns(baseUrl)
  }

  "hello" should {
    "return a Right" when {
      "downstream call is successful" in new Test {
        val response: HttpResponse = HttpResponse(status = Status.OK, json = Json.toJson(HelloWorldResponse("test message")), headers = Map.empty)

        MockHttpClient.get(s"$baseUrl/hello-world").returns(Future.successful(response))

        await(connector.hello()) shouldBe Right(HelloWorldResponse("test message"))
      }
    }
    "return a Left" when {
      "downstream call is successful but doesn't match expected JSON" in new Test {

        case class TestModel(field: String)

        object TestModel {
          implicit val format: OFormat[TestModel] = Json.format
        }

        val response: HttpResponse = HttpResponse(status = Status.OK, json = Json.toJson(TestModel("test message")), headers = Map.empty)

        MockHttpClient.get(s"$baseUrl/hello-world").returns(Future.successful(response))

        await(connector.hello()) shouldBe Left(JsonValidationError)
      }
      "downstream call is unsuccessful" in new Test {
        val response: HttpResponse = HttpResponse(status = Status.INTERNAL_SERVER_ERROR, json = Json.toJson(HelloWorldResponse("test message")), headers = Map.empty)

        MockHttpClient.get(s"$baseUrl/hello-world").returns(Future.successful(response))

        await(connector.hello()) shouldBe Left(UnexpectedDownstreamResponseError)
      }
    }
  }

  "getMessage" should {
    val getMessageRequest = GetMessageRequest("", "")
    "return a Right" when {
      "downstream call is successful" in new Test {
        val successXml: Elem = <Message>Success!</Message>

        val response: HttpResponse = HttpResponse(status = Status.OK, body = successXml.toString(), headers = Map.empty)

        MockHttpClient.postString(s"$baseUrl/ChRISOSB/EMCS/EMCSApplicationService/2", getMessageRequest.requestBody).returns(Future.successful(response))

        await(connector.getMessage(getMessageRequest)) shouldBe Right(successXml)
      }
    }
    "return a Left" when {
      "downstream call is successful but can't convert the response to XML" in new Test {

        val response: HttpResponse = HttpResponse(status = Status.OK, body = Json.obj().toString(), headers = Map.empty)

        MockHttpClient.postString(s"$baseUrl/ChRISOSB/EMCS/EMCSApplicationService/2", getMessageRequest.requestBody).returns(Future.successful(response))

        await(connector.getMessage(getMessageRequest)) shouldBe Left(XmlValidationError)
      }
      "downstream call is unsuccessful" in new Test {
        val response: HttpResponse = HttpResponse(status = Status.INTERNAL_SERVER_ERROR, body = "", headers = Map.empty)

        MockHttpClient.postString(s"$baseUrl/ChRISOSB/EMCS/EMCSApplicationService/2", getMessageRequest.requestBody).returns(Future.successful(response))

        await(connector.getMessage(getMessageRequest)) shouldBe Left(UnexpectedDownstreamResponseError)
      }
    }
  }
}