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

import play.api.http.{HeaderNames, MimeTypes, Status}
import uk.gov.hmrc.emcstfe.connectors.httpParsers.ChrisXMLHttpParser
import uk.gov.hmrc.emcstfe.fixtures.{GetMovementFixture, SubmitDraftMovementFixture}
import uk.gov.hmrc.emcstfe.mocks.config.MockAppConfig
import uk.gov.hmrc.emcstfe.mocks.connectors.MockHttpClient
import uk.gov.hmrc.emcstfe.mocks.utils.MockXmlUtils
import uk.gov.hmrc.emcstfe.models.request.{GetMovementRequest, SubmitDraftMovementRequest}
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{MarkPlacementError, UnexpectedDownstreamResponseError, XmlValidationError}
import uk.gov.hmrc.emcstfe.models.response.{GetMovementResponse, SubmitDraftMovementResponse}
import uk.gov.hmrc.emcstfe.support.UnitSpec
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class ChrisConnectorSpec extends UnitSpec with Status with MimeTypes with HeaderNames with MockAppConfig with MockHttpClient with MockXmlUtils with GetMovementFixture with SubmitDraftMovementFixture {

  trait Test {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

    val connector = new ChrisConnector(mockHttpClient, mockAppConfig, new ChrisXMLHttpParser(mockXmlUtils), mockXmlUtils)

    val baseUrl: String = "http://test-BaseUrl"
    MockedAppConfig.chrisUrl.returns(baseUrl)
    MockedAppConfig.chrisHeaders.returns(Seq()).anyNumberOfTimes()
  }

  "postChrisSOAPRequestAndExtractToModel" should {
    val getMovementRequest = GetMovementRequest("", "")
    "return a Right" when {
      "downstream call is successful" in new Test {

        MockHttpClient.postString(
          url = s"$baseUrl/ChRISOSB/EMCS/EMCSApplicationService/2",
          body = getMovementRequest.requestBody,
          headers = Seq(
            HeaderNames.ACCEPT -> "application/soap+xml",
            HeaderNames.CONTENT_TYPE -> s"""application/soap+xml; charset=UTF-8; action="${getMovementRequest.action}""""
          )
        )
          .returns(Future.successful(Right(getMovementResponse)))

        await(connector.postChrisSOAPRequestAndExtractToModel[GetMovementResponse](getMovementRequest)) shouldBe Right(getMovementResponse)
      }
    }
    "return a Left" when {
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

        await(connector.postChrisSOAPRequestAndExtractToModel[GetMovementResponse](getMovementRequest)) shouldBe response
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

        await(connector.postChrisSOAPRequestAndExtractToModel[GetMovementResponse](getMovementRequest)) shouldBe response
      }
    }
  }

  "postChrisSOAPRequest" should {
    val getMovementRequest = GetMovementRequest("", "")
    "return a Right" when {
      "downstream call is successful" in new Test {

        MockHttpClient.postString(
          url = s"$baseUrl/ChRISOSB/EMCS/EMCSApplicationService/2",
          body = getMovementRequest.requestBody,
          headers = Seq(
            HeaderNames.ACCEPT -> "application/soap+xml",
            HeaderNames.CONTENT_TYPE -> s"""application/soap+xml; charset=UTF-8; action="${getMovementRequest.action}""""
          )
        )
          .returns(Future.successful(Right(getMovementSoapWrapper)))

        await(connector.postChrisSOAPRequest(getMovementRequest)) shouldBe Right(getMovementSoapWrapper)
      }
    }
    "return a Left" when {
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

  "submitDraftMovementChrisSOAPRequest" should {
    val submitDraftMovementRequest = SubmitDraftMovementRequest("", "", submitDraftMovementRequestBody)
    "return a Right" when {
      "downstream call is successful" in new Test {

        MockHttpClient.postString(
          url = s"$baseUrl/ChRIS/EMCS/SubmitDraftMovementPortal/3",
          body = submitDraftMovementRequest.requestBody,
          headers = Seq(
            HeaderNames.ACCEPT -> "application/soap+xml",
            HeaderNames.CONTENT_TYPE -> s"""application/soap+xml; charset=UTF-8; action="${submitDraftMovementRequest.action}""""
          )
        )
          .returns(Future.successful(Right(submitDraftMovementResponse)))

        MockXmlUtils.prepareXmlForSubmission()
          .returns(Right(""))

        await(connector.submitDraftMovementChrisSOAPRequest[SubmitDraftMovementResponse](submitDraftMovementRequest)) shouldBe Right(submitDraftMovementResponse)
      }
    }
    "return a Left" when {
      "downstream call is successful but can't convert the response to XML" in new Test {

        val response = Left(XmlValidationError)

        MockHttpClient.postString(
          url = s"$baseUrl/ChRIS/EMCS/SubmitDraftMovementPortal/3",
          body = submitDraftMovementRequest.requestBody,
          headers = Seq(
            HeaderNames.ACCEPT -> "application/soap+xml",
            HeaderNames.CONTENT_TYPE -> s"""application/soap+xml; charset=UTF-8; action="${submitDraftMovementRequest.action}""""
          )
        ).returns(Future.successful(response))

        MockXmlUtils.prepareXmlForSubmission()
          .returns(Right(""))

        await(connector.submitDraftMovementChrisSOAPRequest[SubmitDraftMovementResponse](submitDraftMovementRequest)) shouldBe response
      }
      "downstream call is unsuccessful" in new Test {
        val response = Left(UnexpectedDownstreamResponseError)

        MockHttpClient.postString(
          url = s"$baseUrl/ChRIS/EMCS/SubmitDraftMovementPortal/3",
          body = submitDraftMovementRequest.requestBody,
          headers = Seq(
            HeaderNames.ACCEPT -> "application/soap+xml",
            HeaderNames.CONTENT_TYPE -> s"""application/soap+xml; charset=UTF-8; action="${submitDraftMovementRequest.action}""""
          )
        ).returns(Future.successful(response))

        MockXmlUtils.prepareXmlForSubmission()
          .returns(Right(""))

        await(connector.submitDraftMovementChrisSOAPRequest[SubmitDraftMovementResponse](submitDraftMovementRequest)) shouldBe response
      }
      "cannot prepare XML for submission" in new Test {
        val response = Left(MarkPlacementError)


        MockXmlUtils.prepareXmlForSubmission()
          .returns(response)

        await(connector.submitDraftMovementChrisSOAPRequest[SubmitDraftMovementResponse](submitDraftMovementRequest)) shouldBe response
      }
    }
  }

  "submitReportOfReceiptChrisSOAPRequest" should {
    val submitDraftMovementRequest = SubmitDraftMovementRequest("", "", submitDraftMovementRequestBody)
    "return a Right" when {
      "downstream call is successful" in new Test {

        MockHttpClient.postString(
          url = s"$baseUrl/ChRIS/EMCS/SubmitDraftMovementPortal/3",
          body = submitDraftMovementRequest.requestBody,
          headers = Seq(
            HeaderNames.ACCEPT -> "application/soap+xml",
            HeaderNames.CONTENT_TYPE -> s"""application/soap+xml; charset=UTF-8; action="${submitDraftMovementRequest.action}""""
          )
        )
          .returns(Future.successful(Right(submitDraftMovementResponse)))

        MockXmlUtils.prepareXmlForSubmission()
          .returns(Right(""))

        await(connector.submitDraftMovementChrisSOAPRequest[SubmitDraftMovementResponse](submitDraftMovementRequest)) shouldBe Right(submitDraftMovementResponse)
      }
    }
    "return a Left" when {
      "downstream call is successful but can't convert the response to XML" in new Test {

        val response = Left(XmlValidationError)

        MockHttpClient.postString(
          url = s"$baseUrl/ChRIS/EMCS/SubmitDraftMovementPortal/3",
          body = submitDraftMovementRequest.requestBody,
          headers = Seq(
            HeaderNames.ACCEPT -> "application/soap+xml",
            HeaderNames.CONTENT_TYPE -> s"""application/soap+xml; charset=UTF-8; action="${submitDraftMovementRequest.action}""""
          )
        ).returns(Future.successful(response))

        MockXmlUtils.prepareXmlForSubmission()
          .returns(Right(""))

        await(connector.submitDraftMovementChrisSOAPRequest[SubmitDraftMovementResponse](submitDraftMovementRequest)) shouldBe response
      }
      "downstream call is unsuccessful" in new Test {
        val response = Left(UnexpectedDownstreamResponseError)

        MockHttpClient.postString(
          url = s"$baseUrl/ChRIS/EMCS/SubmitDraftMovementPortal/3",
          body = submitDraftMovementRequest.requestBody,
          headers = Seq(
            HeaderNames.ACCEPT -> "application/soap+xml",
            HeaderNames.CONTENT_TYPE -> s"""application/soap+xml; charset=UTF-8; action="${submitDraftMovementRequest.action}""""
          )
        ).returns(Future.successful(response))

        MockXmlUtils.prepareXmlForSubmission()
          .returns(Right(""))

        await(connector.submitDraftMovementChrisSOAPRequest[SubmitDraftMovementResponse](submitDraftMovementRequest)) shouldBe response
      }
      "cannot prepare XML for submission" in new Test {
        val response = Left(MarkPlacementError)


        MockXmlUtils.prepareXmlForSubmission()
          .returns(response)

        await(connector.submitDraftMovementChrisSOAPRequest[SubmitDraftMovementResponse](submitDraftMovementRequest)) shouldBe response
      }
    }
  }
}