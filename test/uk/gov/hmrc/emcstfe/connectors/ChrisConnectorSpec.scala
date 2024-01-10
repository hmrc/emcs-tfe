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

import org.scalatest.BeforeAndAfterEach
import play.api.http.{HeaderNames, MimeTypes, Status}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.connectors.httpParsers.ChrisXMLHttpParser
import uk.gov.hmrc.emcstfe.featureswitch.core.config.{FeatureSwitching, UseDownstreamStub}
import uk.gov.hmrc.emcstfe.fixtures._
import uk.gov.hmrc.emcstfe.mocks.config.MockAppConfig
import uk.gov.hmrc.emcstfe.mocks.connectors.MockHttpClient
import uk.gov.hmrc.emcstfe.mocks.services.MockMetricsService
import uk.gov.hmrc.emcstfe.mocks.utils.MockXmlUtils
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.common.SubmitterType.Consignor
import uk.gov.hmrc.emcstfe.models.request._
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{MarkPlacementError, UnexpectedDownstreamResponseError, XmlValidationError}
import uk.gov.hmrc.emcstfe.models.response.getMovement.GetMovementResponse
import uk.gov.hmrc.emcstfe.models.response.{ChRISSuccessResponse, ErrorResponse}
import uk.gov.hmrc.emcstfe.support.TestBaseSpec
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class ChrisConnectorSpec extends TestBaseSpec with Status with MimeTypes with HeaderNames with MockAppConfig with MockHttpClient with MockXmlUtils with FeatureSwitching with BeforeAndAfterEach
  with GetMovementFixture
  with SubmitDraftMovementFixture
  with SubmitReportOfReceiptFixtures
  with SubmitExplainDelayFixtures
  with SubmitChangeDestinationFixtures
  with SubmitExplainShortageExcessFixtures
  with SubmitAlertOrRejectionFixtures
  with SubmitCancellationOfMovementFixtures
  with CreateMovementFixtures
  with MockMetricsService {

  override def afterEach(): Unit = {
    disable(UseDownstreamStub)
    super.afterEach()
  }

  lazy val config: AppConfig = app.injector.instanceOf[AppConfig]

  trait Test {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

    val connector = new ChrisConnector(mockHttpClient, config, mockMetricsService, new ChrisXMLHttpParser(mockXmlUtils), mockXmlUtils)

    val baseUrl: String = "http://localhost:8308"
  }

  "postChrisSOAPRequestAndExtractToModel" should {
    val getMovementRequest = GetMovementRequest("", "")
    "return a Right" when {
      "downstream call is successful" in new Test {


        MockMetricsService.requestTimer(getMovementRequest.metricName)
        MockMetricsService.processWithTimer()

        MockHttpClient.postString(
          url = s"$baseUrl/ChRISOSB/EMCS/EMCSApplicationService/2",
          body = getMovementRequest.requestBody,
          headers = Seq(
            HeaderNames.ACCEPT -> "application/soap+xml",
            HeaderNames.CONTENT_TYPE -> s"""application/soap+xml; charset=UTF-8; action="${getMovementRequest.action}""""
          )
        )
          .returns(Future.successful(Right(getMovementResponse())))

        await(connector.postChrisSOAPRequestAndExtractToModel[GetMovementResponse](getMovementRequest)) shouldBe Right(getMovementResponse())
      }
    }
    "return a Left" when {
      "downstream call is successful but can't convert the response to XML" in new Test {

        val response: Either[ErrorResponse, String] = Left(XmlValidationError)

        MockMetricsService.requestTimer(getMovementRequest.metricName)
        MockMetricsService.processWithTimer()

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
        val response: Either[ErrorResponse, String] = Left(UnexpectedDownstreamResponseError)

        MockMetricsService.requestTimer(getMovementRequest.metricName)
        MockMetricsService.processWithTimer()

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

        MockMetricsService.requestTimer(getMovementRequest.metricName)
        MockMetricsService.processWithTimer()

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

        val response: Either[ErrorResponse, String] = Left(XmlValidationError)

        MockMetricsService.requestTimer(getMovementRequest.metricName)
        MockMetricsService.processWithTimer()

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
        val response: Either[ErrorResponse, String] = Left(UnexpectedDownstreamResponseError)

        MockMetricsService.requestTimer(getMovementRequest.metricName)
        MockMetricsService.processWithTimer()

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

  "submitCreateMovementChrisSOAPRequest" should {

    implicit val request = UserRequest(FakeRequest(), testErn, testInternalId, testCredId)
    val submitCreateMovementRequest = SubmitCreateMovementRequest(CreateMovementFixtures.createMovementModelMax, testDraftId, useFS41SchemaVersion = false)

    "return a Right" when {
      "downstream call is successful" in new Test {

        MockMetricsService.requestTimer(submitCreateMovementRequest.metricName)
        MockMetricsService.processWithTimer()

        MockHttpClient.postString(
          url = s"$baseUrl/ChRIS/EMCS/SubmitDraftMovementPortal/3",
          body = submitCreateMovementRequest.requestBody,
          headers = Seq(
            HeaderNames.ACCEPT -> "application/soap+xml",
            HeaderNames.CONTENT_TYPE -> s"""application/soap+xml; charset=UTF-8; action="${submitCreateMovementRequest.action}""""
          )
        )
          .returns(Future.successful(Right(chrisSuccessResponse)))

        MockXmlUtils.prepareXmlForSubmission()
          .returns(Right(""))

        await(connector.submitCreateMovementChrisSOAPRequest[ChRISSuccessResponse](submitCreateMovementRequest)) shouldBe Right(chrisSuccessResponse)
      }
    }
    "return a Left" when {
      "downstream call is successful but can't convert the response to XML" in new Test {

        val response: Either[ErrorResponse, String] = Left(XmlValidationError)

        MockMetricsService.requestTimer(submitCreateMovementRequest.metricName)
        MockMetricsService.processWithTimer()

        MockHttpClient.postString(
          url = s"$baseUrl/ChRIS/EMCS/SubmitDraftMovementPortal/3",
          body = submitCreateMovementRequest.requestBody,
          headers = Seq(
            HeaderNames.ACCEPT -> "application/soap+xml",
            HeaderNames.CONTENT_TYPE -> s"""application/soap+xml; charset=UTF-8; action="${submitCreateMovementRequest.action}""""
          )
        ).returns(Future.successful(response))

        MockXmlUtils.prepareXmlForSubmission()
          .returns(Right(""))

        await(connector.submitCreateMovementChrisSOAPRequest[ChRISSuccessResponse](submitCreateMovementRequest)) shouldBe response
      }
      "downstream call is unsuccessful" in new Test {
        val response: Either[ErrorResponse, String] = Left(UnexpectedDownstreamResponseError)

        MockMetricsService.requestTimer(submitCreateMovementRequest.metricName)
        MockMetricsService.processWithTimer()

        MockHttpClient.postString(
          url = s"$baseUrl/ChRIS/EMCS/SubmitDraftMovementPortal/3",
          body = submitCreateMovementRequest.requestBody,
          headers = Seq(
            HeaderNames.ACCEPT -> "application/soap+xml",
            HeaderNames.CONTENT_TYPE -> s"""application/soap+xml; charset=UTF-8; action="${submitCreateMovementRequest.action}""""
          )
        ).returns(Future.successful(response))

        MockXmlUtils.prepareXmlForSubmission()
          .returns(Right(""))

        await(connector.submitCreateMovementChrisSOAPRequest[ChRISSuccessResponse](submitCreateMovementRequest)) shouldBe response
      }
      "cannot prepare XML for submission" in new Test {
        val response: Either[ErrorResponse, String] = Left(MarkPlacementError)


        MockXmlUtils.prepareXmlForSubmission()
          .returns(response)

        await(connector.submitCreateMovementChrisSOAPRequest[ChRISSuccessResponse](submitCreateMovementRequest)) shouldBe response
      }
    }
  }

  "submitReportOfReceiptChrisSOAPRequest" should {

    implicit val request = UserRequest(FakeRequest(), testErn, testInternalId, testCredId)
    val submitReportOfReceiptRequest = SubmitReportOfReceiptRequest(maxSubmitReportOfReceiptModel, useFS41SchemaVersion = false)

    "return a Right" when {
      "downstream call is successful" in new Test {

        MockMetricsService.requestTimer(submitReportOfReceiptRequest.metricName)
        MockMetricsService.processWithTimer()

        MockHttpClient.postString(
          url = s"$baseUrl/ChRIS/EMCS/SubmitReportofReceiptPortal/4",
          body = submitReportOfReceiptRequest.requestBody,
          headers = Seq(
            HeaderNames.ACCEPT -> "application/soap+xml",
            HeaderNames.CONTENT_TYPE -> s"""application/soap+xml; charset=UTF-8; action="${submitReportOfReceiptRequest.action}""""
          )
        )
          .returns(Future.successful(Right(chrisSuccessResponse)))

        MockXmlUtils.prepareXmlForSubmission()
          .returns(Right(""))

        await(connector.submitReportOfReceiptChrisSOAPRequest[ChRISSuccessResponse](submitReportOfReceiptRequest)) shouldBe Right(chrisSuccessResponse)
      }
    }
    "return a Left" when {
      "downstream call is successful but can't convert the response to XML" in new Test {

        val response: Either[ErrorResponse, String] = Left(XmlValidationError)

        MockMetricsService.requestTimer(submitReportOfReceiptRequest.metricName)
        MockMetricsService.processWithTimer()

        MockHttpClient.postString(
          url = s"$baseUrl/ChRIS/EMCS/SubmitReportofReceiptPortal/4",
          body = submitReportOfReceiptRequest.requestBody,
          headers = Seq(
            HeaderNames.ACCEPT -> "application/soap+xml",
            HeaderNames.CONTENT_TYPE -> s"""application/soap+xml; charset=UTF-8; action="${submitReportOfReceiptRequest.action}""""
          )
        ).returns(Future.successful(response))

        MockXmlUtils.prepareXmlForSubmission()
          .returns(Right(""))

        await(connector.submitReportOfReceiptChrisSOAPRequest[ChRISSuccessResponse](submitReportOfReceiptRequest)) shouldBe response
      }
      "downstream call is unsuccessful" in new Test {
        val response: Either[ErrorResponse, String] = Left(UnexpectedDownstreamResponseError)

        MockMetricsService.requestTimer(submitReportOfReceiptRequest.metricName)
        MockMetricsService.processWithTimer()

        MockHttpClient.postString(
          url = s"$baseUrl/ChRIS/EMCS/SubmitReportofReceiptPortal/4",
          body = submitReportOfReceiptRequest.requestBody,
          headers = Seq(
            HeaderNames.ACCEPT -> "application/soap+xml",
            HeaderNames.CONTENT_TYPE -> s"""application/soap+xml; charset=UTF-8; action="${submitReportOfReceiptRequest.action}""""
          )
        ).returns(Future.successful(response))

        MockXmlUtils.prepareXmlForSubmission()
          .returns(Right(""))

        await(connector.submitReportOfReceiptChrisSOAPRequest[ChRISSuccessResponse](submitReportOfReceiptRequest)) shouldBe response
      }
      "cannot prepare XML for submission" in new Test {

        val response: Either[ErrorResponse, String] = Left(MarkPlacementError)

        MockXmlUtils.prepareXmlForSubmission()
          .returns(response)

        await(connector.submitReportOfReceiptChrisSOAPRequest[ChRISSuccessResponse](submitReportOfReceiptRequest)) shouldBe response
      }
    }
  }

  "submitExplainDelayChrisSOAPRequest" should {

    implicit val request = UserRequest(FakeRequest(), testErn, testInternalId, testCredId)
    val submitExplainDelayRequest = SubmitExplainDelayRequest(maxSubmitExplainDelayModel, useFS41SchemaVersion = false)

    "return a Right" when {
      "downstream call is successful" in new Test {

        MockMetricsService.requestTimer(submitExplainDelayRequest.metricName)
        MockMetricsService.processWithTimer()

        MockHttpClient.postString(
          url = s"$baseUrl/ChRIS/EMCS/SubmitExplainDelayToDeliveryPortal/4",
          body = submitExplainDelayRequest.requestBody,
          headers = Seq(
            HeaderNames.ACCEPT -> "application/soap+xml",
            HeaderNames.CONTENT_TYPE -> s"""application/soap+xml; charset=UTF-8; action="${submitExplainDelayRequest.action}""""
          )
        )
          .returns(Future.successful(Right(chrisSuccessResponse)))

        MockXmlUtils.prepareXmlForSubmission()
          .returns(Right(""))

        await(connector.submitExplainDelayChrisSOAPRequest[ChRISSuccessResponse](submitExplainDelayRequest)) shouldBe Right(chrisSuccessResponse)
      }
    }
    "return a Left" when {
      "downstream call is successful but can't convert the response to XML" in new Test {

        val response: Either[ErrorResponse, String] = Left(XmlValidationError)

        MockMetricsService.requestTimer(submitExplainDelayRequest.metricName)
        MockMetricsService.processWithTimer()

        MockHttpClient.postString(
          url = s"$baseUrl/ChRIS/EMCS/SubmitExplainDelayToDeliveryPortal/4",
          body = submitExplainDelayRequest.requestBody,
          headers = Seq(
            HeaderNames.ACCEPT -> "application/soap+xml",
            HeaderNames.CONTENT_TYPE -> s"""application/soap+xml; charset=UTF-8; action="${submitExplainDelayRequest.action}""""
          )
        ).returns(Future.successful(response))

        MockXmlUtils.prepareXmlForSubmission()
          .returns(Right(""))

        await(connector.submitExplainDelayChrisSOAPRequest[ChRISSuccessResponse](submitExplainDelayRequest)) shouldBe response
      }
      "downstream call is unsuccessful" in new Test {
        val response: Either[ErrorResponse, String] = Left(UnexpectedDownstreamResponseError)

        MockMetricsService.requestTimer(submitExplainDelayRequest.metricName)
        MockMetricsService.processWithTimer()

        MockHttpClient.postString(
          url = s"$baseUrl/ChRIS/EMCS/SubmitExplainDelayToDeliveryPortal/4",
          body = submitExplainDelayRequest.requestBody,
          headers = Seq(
            HeaderNames.ACCEPT -> "application/soap+xml",
            HeaderNames.CONTENT_TYPE -> s"""application/soap+xml; charset=UTF-8; action="${submitExplainDelayRequest.action}""""
          )
        ).returns(Future.successful(response))

        MockXmlUtils.prepareXmlForSubmission()
          .returns(Right(""))

        await(connector.submitExplainDelayChrisSOAPRequest[ChRISSuccessResponse](submitExplainDelayRequest)) shouldBe response
      }
      "cannot prepare XML for submission" in new Test {

        val response: Either[ErrorResponse, String] = Left(MarkPlacementError)

        MockXmlUtils.prepareXmlForSubmission()
          .returns(response)

        await(connector.submitExplainDelayChrisSOAPRequest[ChRISSuccessResponse](submitExplainDelayRequest)) shouldBe response
      }
    }
  }

  "submitChangeDestinationChrisSOAPRequest" should {

    import SubmitChangeDestinationFixtures.submitChangeDestinationModelMax

    implicit val request = UserRequest(FakeRequest(), testErn, testInternalId, testCredId)
    val submitChangeDestinationRequest = SubmitChangeDestinationRequest(submitChangeDestinationModelMax, useFS41SchemaVersion = false)

    "return a Right" when {
      "downstream call is successful" in new Test {

        MockMetricsService.requestTimer(submitChangeDestinationRequest.metricName)
        MockMetricsService.processWithTimer()

        MockHttpClient.postString(
          url = s"$baseUrl/ChRIS/EMCS/SubmitChangeOfDestinationPortal/3",
          body = submitChangeDestinationRequest.requestBody,
          headers = Seq(
            HeaderNames.ACCEPT -> "application/soap+xml",
            HeaderNames.CONTENT_TYPE -> s"""application/soap+xml; charset=UTF-8; action="${submitChangeDestinationRequest.action}""""
          )
        )
          .returns(Future.successful(Right(chrisSuccessResponse)))

        MockXmlUtils.prepareXmlForSubmission()
          .returns(Right(""))

        await(connector.submitChangeDestinationChrisSOAPRequest[ChRISSuccessResponse](submitChangeDestinationRequest)) shouldBe Right(chrisSuccessResponse)
      }
    }
    "return a Left" when {
      "downstream call is successful but can't convert the response to XML" in new Test {

        val response: Either[ErrorResponse, String] = Left(XmlValidationError)

        MockMetricsService.requestTimer(submitChangeDestinationRequest.metricName)
        MockMetricsService.processWithTimer()

        MockHttpClient.postString(
          url = s"$baseUrl/ChRIS/EMCS/SubmitChangeOfDestinationPortal/3",
          body = submitChangeDestinationRequest.requestBody,
          headers = Seq(
            HeaderNames.ACCEPT -> "application/soap+xml",
            HeaderNames.CONTENT_TYPE -> s"""application/soap+xml; charset=UTF-8; action="${submitChangeDestinationRequest.action}""""
          )
        ).returns(Future.successful(response))

        MockXmlUtils.prepareXmlForSubmission()
          .returns(Right(""))

        await(connector.submitChangeDestinationChrisSOAPRequest[ChRISSuccessResponse](submitChangeDestinationRequest)) shouldBe response
      }
      "downstream call is unsuccessful" in new Test {
        val response: Either[ErrorResponse, String] = Left(UnexpectedDownstreamResponseError)

        MockMetricsService.requestTimer(submitChangeDestinationRequest.metricName)
        MockMetricsService.processWithTimer()

        MockHttpClient.postString(
          url = s"$baseUrl/ChRIS/EMCS/SubmitChangeOfDestinationPortal/3",
          body = submitChangeDestinationRequest.requestBody,
          headers = Seq(
            HeaderNames.ACCEPT -> "application/soap+xml",
            HeaderNames.CONTENT_TYPE -> s"""application/soap+xml; charset=UTF-8; action="${submitChangeDestinationRequest.action}""""
          )
        ).returns(Future.successful(response))

        MockXmlUtils.prepareXmlForSubmission()
          .returns(Right(""))

        await(connector.submitChangeDestinationChrisSOAPRequest[ChRISSuccessResponse](submitChangeDestinationRequest)) shouldBe response
      }
      "cannot prepare XML for submission" in new Test {

        val response: Either[ErrorResponse, String] = Left(MarkPlacementError)

        MockXmlUtils.prepareXmlForSubmission()
          .returns(response)

        await(connector.submitChangeDestinationChrisSOAPRequest[ChRISSuccessResponse](submitChangeDestinationRequest)) shouldBe response
      }
    }
  }

  "submitExplainShortageExcessChrisSOAPRequest" should {

    import SubmitExplainShortageExcessFixtures.submitExplainShortageExcessModelMax

    implicit val request = UserRequest(FakeRequest(), testErn, testInternalId, testCredId)
    val submitExplainShortageExcessRequest = SubmitExplainShortageExcessRequest(submitExplainShortageExcessModelMax(Consignor), useFS41SchemaVersion = false)

    "return a Right" when {
      "downstream call is successful" in new Test {

        MockMetricsService.requestTimer(submitExplainShortageExcessRequest.metricName)
        MockMetricsService.processWithTimer()

        MockHttpClient.postString(
          url = s"$baseUrl/ChRIS/EMCS/SubmitReasonForShortagePortal/2",
          body = submitExplainShortageExcessRequest.requestBody,
          headers = Seq(
            HeaderNames.ACCEPT -> "application/soap+xml",
            HeaderNames.CONTENT_TYPE -> s"""application/soap+xml; charset=UTF-8; action="${submitExplainShortageExcessRequest.action}""""
          )
        )
          .returns(Future.successful(Right(chrisSuccessResponse)))

        MockXmlUtils.prepareXmlForSubmission()
          .returns(Right(""))

        await(connector.submitExplainShortageExcessChrisSOAPRequest[ChRISSuccessResponse](submitExplainShortageExcessRequest)) shouldBe Right(chrisSuccessResponse)
      }
    }
    "return a Left" when {
      "downstream call is successful but can't convert the response to XML" in new Test {

        val response: Either[ErrorResponse, String] = Left(XmlValidationError)

        MockMetricsService.requestTimer(submitExplainShortageExcessRequest.metricName)
        MockMetricsService.processWithTimer()

        MockHttpClient.postString(
          url = s"$baseUrl/ChRIS/EMCS/SubmitReasonForShortagePortal/2",
          body = submitExplainShortageExcessRequest.requestBody,
          headers = Seq(
            HeaderNames.ACCEPT -> "application/soap+xml",
            HeaderNames.CONTENT_TYPE -> s"""application/soap+xml; charset=UTF-8; action="${submitExplainShortageExcessRequest.action}""""
          )
        ).returns(Future.successful(response))

        MockXmlUtils.prepareXmlForSubmission()
          .returns(Right(""))

        await(connector.submitExplainShortageExcessChrisSOAPRequest[ChRISSuccessResponse](submitExplainShortageExcessRequest)) shouldBe response
      }
      "downstream call is unsuccessful" in new Test {
        val response: Either[ErrorResponse, String] = Left(UnexpectedDownstreamResponseError)

        MockMetricsService.requestTimer(submitExplainShortageExcessRequest.metricName)
        MockMetricsService.processWithTimer()

        MockHttpClient.postString(
          url = s"$baseUrl/ChRIS/EMCS/SubmitReasonForShortagePortal/2",
          body = submitExplainShortageExcessRequest.requestBody,
          headers = Seq(
            HeaderNames.ACCEPT -> "application/soap+xml",
            HeaderNames.CONTENT_TYPE -> s"""application/soap+xml; charset=UTF-8; action="${submitExplainShortageExcessRequest.action}""""
          )
        ).returns(Future.successful(response))

        MockXmlUtils.prepareXmlForSubmission()
          .returns(Right(""))

        await(connector.submitExplainShortageExcessChrisSOAPRequest[ChRISSuccessResponse](submitExplainShortageExcessRequest)) shouldBe response
      }
      "cannot prepare XML for submission" in new Test {

        val response: Either[ErrorResponse, String] = Left(MarkPlacementError)

        MockXmlUtils.prepareXmlForSubmission()
          .returns(response)

        await(connector.submitExplainShortageExcessChrisSOAPRequest[ChRISSuccessResponse](submitExplainShortageExcessRequest)) shouldBe response
      }
    }
  }

  "submitAlertOrRejectionChrisSOAPRequest" should {

    implicit val request = UserRequest(FakeRequest(), testErn, testInternalId, testCredId)
    val submitAlertOrRejectionRequest = SubmitAlertOrRejectionRequest(maxSubmitAlertOrRejectionModel, useFS41SchemaVersion = false)

    "return a Right" when {
      "downstream call is successful" in new Test {

        MockMetricsService.requestTimer(submitAlertOrRejectionRequest.metricName)
        MockMetricsService.processWithTimer()

        MockHttpClient.postString(
          url = s"$baseUrl/ChRIS/EMCS/SubmitAlertOrRejectionMovementPortal/2",
          body = submitAlertOrRejectionRequest.requestBody,
          headers = Seq(
            HeaderNames.ACCEPT -> "application/soap+xml",
            HeaderNames.CONTENT_TYPE -> s"""application/soap+xml; charset=UTF-8; action="${submitAlertOrRejectionRequest.action}""""
          )
        )
          .returns(Future.successful(Right(chrisSuccessResponse)))

        MockXmlUtils.prepareXmlForSubmission()
          .returns(Right(""))

        await(connector.submitAlertOrRejectionChrisSOAPRequest[ChRISSuccessResponse](submitAlertOrRejectionRequest)) shouldBe Right(chrisSuccessResponse)
      }
    }
    "return a Left" when {
      "downstream call is successful but can't convert the response to XML" in new Test {

        val response: Either[ErrorResponse, String] = Left(XmlValidationError)

        MockMetricsService.requestTimer(submitAlertOrRejectionRequest.metricName)
        MockMetricsService.processWithTimer()

        MockHttpClient.postString(
          url = s"$baseUrl/ChRIS/EMCS/SubmitAlertOrRejectionMovementPortal/2",
          body = submitAlertOrRejectionRequest.requestBody,
          headers = Seq(
            HeaderNames.ACCEPT -> "application/soap+xml",
            HeaderNames.CONTENT_TYPE -> s"""application/soap+xml; charset=UTF-8; action="${submitAlertOrRejectionRequest.action}""""
          )
        ).returns(Future.successful(response))

        MockXmlUtils.prepareXmlForSubmission()
          .returns(Right(""))

        await(connector.submitAlertOrRejectionChrisSOAPRequest[ChRISSuccessResponse](submitAlertOrRejectionRequest)) shouldBe response
      }
      "downstream call is unsuccessful" in new Test {
        val response: Either[ErrorResponse, String] = Left(UnexpectedDownstreamResponseError)

        MockMetricsService.requestTimer(submitAlertOrRejectionRequest.metricName)
        MockMetricsService.processWithTimer()

        MockHttpClient.postString(
          url = s"$baseUrl/ChRIS/EMCS/SubmitAlertOrRejectionMovementPortal/2",
          body = submitAlertOrRejectionRequest.requestBody,
          headers = Seq(
            HeaderNames.ACCEPT -> "application/soap+xml",
            HeaderNames.CONTENT_TYPE -> s"""application/soap+xml; charset=UTF-8; action="${submitAlertOrRejectionRequest.action}""""
          )
        ).returns(Future.successful(response))

        MockXmlUtils.prepareXmlForSubmission()
          .returns(Right(""))

        await(connector.submitAlertOrRejectionChrisSOAPRequest[ChRISSuccessResponse](submitAlertOrRejectionRequest)) shouldBe response
      }
      "cannot prepare XML for submission" in new Test {

        val response: Either[ErrorResponse, String] = Left(MarkPlacementError)

        MockXmlUtils.prepareXmlForSubmission()
          .returns(response)

        await(connector.submitAlertOrRejectionChrisSOAPRequest[ChRISSuccessResponse](submitAlertOrRejectionRequest)) shouldBe response
      }
    }
  }

  "submitCancellationOfMovementChrisSOAPRequest" should {

    implicit val request: UserRequest[AnyContentAsEmpty.type] = UserRequest(FakeRequest(), testErn, testInternalId, testCredId)
    val submitCancellationOfMovementRequest: SubmitCancellationOfMovementRequest = SubmitCancellationOfMovementRequest(maxSubmitCancellationOfMovementModel, useFS41SchemaVersion = false)

    "return a Right" when {
      "downstream call is successful" in new Test {

        MockMetricsService.requestTimer(submitCancellationOfMovementRequest.metricName)
        MockMetricsService.processWithTimer()

        MockHttpClient.postString(
          url = s"$baseUrl/ChRIS/EMCS/SubmitCancellationPortal/3",
          body = submitCancellationOfMovementRequest.requestBody,
          headers = Seq(
            HeaderNames.ACCEPT -> "application/soap+xml",
            HeaderNames.CONTENT_TYPE -> s"""application/soap+xml; charset=UTF-8; action="${submitCancellationOfMovementRequest.action}""""
          )
        )
          .returns(Future.successful(Right(chrisSuccessResponse)))

        MockXmlUtils.prepareXmlForSubmission()
          .returns(Right(""))

        await(connector.submitCancellationOfMovementChrisSOAPRequest[ChRISSuccessResponse](submitCancellationOfMovementRequest)) shouldBe Right(chrisSuccessResponse)
      }
    }
    "return a Left" when {
      "downstream call is successful but can't convert the response to XML" in new Test {

        val response: Either[ErrorResponse, String] = Left(XmlValidationError)

        MockMetricsService.requestTimer(submitCancellationOfMovementRequest.metricName)
        MockMetricsService.processWithTimer()

        MockHttpClient.postString(
          url = s"$baseUrl/ChRIS/EMCS/SubmitCancellationPortal/3",
          body = submitCancellationOfMovementRequest.requestBody,
          headers = Seq(
            HeaderNames.ACCEPT -> "application/soap+xml",
            HeaderNames.CONTENT_TYPE -> s"""application/soap+xml; charset=UTF-8; action="${submitCancellationOfMovementRequest.action}""""
          )
        ).returns(Future.successful(response))

        MockXmlUtils.prepareXmlForSubmission()
          .returns(Right(""))

        await(connector.submitCancellationOfMovementChrisSOAPRequest[ChRISSuccessResponse](submitCancellationOfMovementRequest)) shouldBe response
      }
      "downstream call is unsuccessful" in new Test {
        val response: Either[ErrorResponse, String] = Left(UnexpectedDownstreamResponseError)

        MockMetricsService.requestTimer(submitCancellationOfMovementRequest.metricName)
        MockMetricsService.processWithTimer()

        MockHttpClient.postString(
          url = s"$baseUrl/ChRIS/EMCS/SubmitCancellationPortal/3",
          body = submitCancellationOfMovementRequest.requestBody,
          headers = Seq(
            HeaderNames.ACCEPT -> "application/soap+xml",
            HeaderNames.CONTENT_TYPE -> s"""application/soap+xml; charset=UTF-8; action="${submitCancellationOfMovementRequest.action}""""
          )
        ).returns(Future.successful(response))

        MockXmlUtils.prepareXmlForSubmission()
          .returns(Right(""))

        await(connector.submitCancellationOfMovementChrisSOAPRequest[ChRISSuccessResponse](submitCancellationOfMovementRequest)) shouldBe response
      }
      "cannot prepare XML for submission" in new Test {

        val response: Either[ErrorResponse, String] = Left(MarkPlacementError)

        MockXmlUtils.prepareXmlForSubmission()
          .returns(response)

        await(connector.submitCancellationOfMovementChrisSOAPRequest[ChRISSuccessResponse](submitCancellationOfMovementRequest)) shouldBe response
      }
    }
  }
}