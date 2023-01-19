/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.connectors

import play.api.http.{HeaderNames, MimeTypes, Status}
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.emcstfe.connectors.httpParsers.RawXMLHttpParser
import uk.gov.hmrc.emcstfe.mocks.config.MockAppConfig
import uk.gov.hmrc.emcstfe.mocks.connectors.MockHttpClient
import uk.gov.hmrc.emcstfe.models.request.GetMovementRequest
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

    val connector = new ChrisConnector(mockHttpClient, mockAppConfig, new RawXMLHttpParser)

    val baseUrl: String = "http://test-BaseUrl"
    MockedAppConfig.chrisUrl.returns(baseUrl)
    MockedAppConfig.chrisHeaders.returns(Seq()).anyNumberOfTimes()
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

  "getMovement" should {
    val getMovementRequest = GetMovementRequest("", "")
    "return a Right" when {
      "downstream call is successful" in new Test {
        val successXml: Elem = <Message>Success!</Message>

        MockHttpClient.postString(
          url = s"$baseUrl/ChRISOSB/EMCS/EMCSApplicationService/2",
          body = getMovementRequest.requestBody,
          headers = Seq(
            HeaderNames.ACCEPT -> "application/soap+xml",
            HeaderNames.CONTENT_TYPE -> s"""application/soap+xml; charset=UTF-8; action="${getMovementRequest.action}""""
          )
        )
          .returns(Future.successful(Right(successXml)))

        await(connector.postChrisSOAPRequest(getMovementRequest)) shouldBe Right(successXml)
      }
    }
    "return a Left" when {
      //TODO test BaseConnector's chrisReads function
      "downstream call is successful but can't convert the response to XML" in new Test {

        val response = Left(XmlValidationError)

        MockHttpClient.postString(
          url = s"$baseUrl/ChRISOSB/EMCS/EMCSApplicationService/2",
          body = getMovementRequest.requestBody,
          headers = Seq(
            HeaderNames.ACCEPT -> "application/soap+xml",
            HeaderNames.CONTENT_TYPE -> s"""application/soap+xml; charset=UTF-8; action="${getMovementRequest.action}""""
          )
        ).returns(Future.successful(response))

        await(connector.postChrisSOAPRequest(getMovementRequest)) shouldBe response
      }
      "downstream call is unsuccessful" in new Test {
        val response = Left(UnexpectedDownstreamResponseError)

        MockHttpClient.postString(
          url = s"$baseUrl/ChRISOSB/EMCS/EMCSApplicationService/2",
          body = getMovementRequest.requestBody,
          headers = Seq(
            HeaderNames.ACCEPT -> "application/soap+xml",
            HeaderNames.CONTENT_TYPE -> s"""application/soap+xml; charset=UTF-8; action="${getMovementRequest.action}""""
          )
        ).returns(Future.successful(response))

        await(connector.postChrisSOAPRequest(getMovementRequest)) shouldBe response
      }
    }
  }
}