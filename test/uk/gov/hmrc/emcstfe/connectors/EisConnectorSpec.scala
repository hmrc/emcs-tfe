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
import play.api.libs.json.JsonValidationError
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.featureswitch.core.config.{FeatureSwitching, UseDownstreamStub}
import uk.gov.hmrc.emcstfe.fixtures._
import uk.gov.hmrc.emcstfe.mocks.connectors.MockHttpClient
import uk.gov.hmrc.emcstfe.mocks.services.MockMetricsService
import uk.gov.hmrc.emcstfe.models.request._
import uk.gov.hmrc.emcstfe.models.request.eis.preValidate.PreValidateETDS12Request
import uk.gov.hmrc.emcstfe.models.request.eis.{EisHeaders, TraderKnownFactsETDS18Request}
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse._
import uk.gov.hmrc.emcstfe.models.response.{EISSubmissionSuccessResponse, ErrorResponse}
import uk.gov.hmrc.emcstfe.support.TestBaseSpec
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}
import uk.gov.hmrc.emcstfe.utils.RequestHelper

import java.time.Instant
import java.time.temporal.ChronoUnit
import scala.concurrent.{ExecutionContext, Future}

class EisConnectorSpec
    extends TestBaseSpec
    with MockHttpClient
    with FeatureSwitching
    with BeforeAndAfterEach
    with GetMovementFixture
    with GetMovementListFixture
    with SubmitReportOfReceiptFixtures
    with SubmitExplainShortageExcessFixtures
    with SubmitExplainDelayFixtures
    with SubmitCancellationOfMovementFixtures
    with CreateMovementFixtures
    with GetMessagesFixtures
    with GetSubmissionFailureMessageFixtures
    with MarkMessageAsReadFixtures
    with SetMessageAsLogicallyDeletedFixtures
    with GetMessageStatisticsFixtures
    with GetMovementHistoryEventsFixture
    with TraderKnownFactsFixtures
    with PreValidateFixtures
    with MockMetricsService
    with RequestHelper {

  override def afterEach(): Unit = {
    disable(UseDownstreamStub)
    super.afterEach()
  }

  lazy val config: AppConfig = app.injector.instanceOf[AppConfig]

  trait Test {
    implicit val hc: HeaderCarrier    = HeaderCarrier()
    implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

    val connector = new EisConnector(mockHttpClient, config, mockMetricsService)

    val baseUrl: String = config.eisBaseUrl
  }

  "EISConnector" when {

    "submit is called" should {

      val submitReportOfReceiptRequest = SubmitReportOfReceiptRequest(maxSubmitReportOfReceiptModel)

      "return a Right" when {
        "downstream call is successful" in new Test {

          MockMetricsService.requestTimer(submitReportOfReceiptRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .postJson(
              url = s"$baseUrl/emcs/digital-submit-new-message/v1",
              body = submitReportOfReceiptRequest.toJson
            )
            .returns(Future.successful(Right(eisSuccessResponse)))

          await(connector.submit[EISSubmissionSuccessResponse](submitReportOfReceiptRequest, "submitReportOfReceiptEISRequest")) shouldBe Right(eisSuccessResponse)
        }
      }

      "return a Left" when {

        "downstream call succeeds but the JSON response body can't be parsed" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISJsonParsingError(Seq(JsonValidationError("'sample' field is wrong"))))

          MockMetricsService.requestTimer(submitReportOfReceiptRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .postJson(
              url = s"$baseUrl/emcs/digital-submit-new-message/v1",
              body = submitReportOfReceiptRequest.toJson
            )
            .returns(Future.successful(response))

          await(connector.submit[EISSubmissionSuccessResponse](submitReportOfReceiptRequest, "submitReportOfReceiptEISRequest")) shouldBe response
        }

        "downstream call fails due to a 400 (Bad Request) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISJsonSchemaMismatchError("JSON is wrong"))

          MockMetricsService.requestTimer(submitReportOfReceiptRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .postJson(
              url = s"$baseUrl/emcs/digital-submit-new-message/v1",
              body = submitReportOfReceiptRequest.toJson
            )
            .returns(Future.successful(response))

          await(connector.submit[EISSubmissionSuccessResponse](submitReportOfReceiptRequest, "submitReportOfReceiptEISRequest")) shouldBe response
        }

        "downstream call fails due to a 404 (Not Found) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISResourceNotFoundError("Url?"))

          MockMetricsService.requestTimer(submitReportOfReceiptRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .postJson(
              url = s"$baseUrl/emcs/digital-submit-new-message/v1",
              body = submitReportOfReceiptRequest.toJson
            )
            .returns(Future.successful(response))

          await(connector.submit[EISSubmissionSuccessResponse](submitReportOfReceiptRequest, "submitReportOfReceiptEISRequest")) shouldBe response
        }

        "downstream call fails due to a 422 (Unprocessable Entity) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISBusinessError("The request body was invalid"))

          MockMetricsService.requestTimer(submitReportOfReceiptRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .postJson(
              url = s"$baseUrl/emcs/digital-submit-new-message/v1",
              body = submitReportOfReceiptRequest.toJson
            )
            .returns(Future.successful(response))

          await(connector.submit[EISSubmissionSuccessResponse](submitReportOfReceiptRequest, "submitReportOfReceiptEISRequest")) shouldBe response
        }

        "downstream call fails due to a 422 (Unprocessable Entity) response (RIM Validation errors)" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISRIMValidationError(eisRimValidationResponse))

          MockMetricsService.requestTimer(submitReportOfReceiptRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .postJson(
              url = s"$baseUrl/emcs/digital-submit-new-message/v1",
              body = submitReportOfReceiptRequest.toJson
            )
            .returns(Future.successful(response))

          await(connector.submit[EISSubmissionSuccessResponse](submitReportOfReceiptRequest, "submitReportOfReceiptEISRequest")) shouldBe response
        }

        "downstream call fails due to a 500 (ISE) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISInternalServerError("Malformed JSON receieved"))

          MockMetricsService.requestTimer(submitReportOfReceiptRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .postJson(
              url = s"$baseUrl/emcs/digital-submit-new-message/v1",
              body = submitReportOfReceiptRequest.toJson
            )
            .returns(Future.successful(response))

          await(connector.submit[EISSubmissionSuccessResponse](submitReportOfReceiptRequest, "submitReportOfReceiptEISRequest")) shouldBe response
        }

        "downstream call fails due to a 503 (Service Unavailable) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISServiceUnavailableError("No servers running"))

          MockMetricsService.requestTimer(submitReportOfReceiptRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .postJson(
              url = s"$baseUrl/emcs/digital-submit-new-message/v1",
              body = submitReportOfReceiptRequest.toJson
            )
            .returns(Future.successful(response))

          await(connector.submit[EISSubmissionSuccessResponse](submitReportOfReceiptRequest, "submitReportOfReceiptEISRequest")) shouldBe response
        }

        "downstream call is unsuccessful" in new Test {
          val response: Either[ErrorResponse, String] = Left(EISUnknownError("429 returned"))

          MockMetricsService.requestTimer(submitReportOfReceiptRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .postJson(
              url = s"$baseUrl/emcs/digital-submit-new-message/v1",
              body = submitReportOfReceiptRequest.toJson
            )
            .returns(Future.successful(response))

          await(connector.submit[EISSubmissionSuccessResponse](submitReportOfReceiptRequest, "submitReportOfReceiptEISRequest")) shouldBe response
        }
      }
    }

    "getMessages is called" should {

      val getMessagesRequest = GetMessagesRequest(testErn, "messagetype", "A", 3)
      val parameters: Seq[(String, String)] = Seq(
        "exciseregistrationnumber" -> testErn,
        "sortfield"                -> "messagetype",
        "sortorder"                -> "A",
        "startposition"            -> "20",
        "maxnotoreturn"            -> "10"
      )

      import GetMessagesResponseFixtures._

      "return a Right" when {
        "downstream call is successful" in new Test {

          MockMetricsService.requestTimer(getMessagesRequest.metricName)
          MockMetricsService.processWithTimer()

          val uri = s"$baseUrl/emcs/messages/v1/messages"
          val urlWithQuery: String = uri + makeQueryString(parameters)

          MockHttpClient
            .get(url"$urlWithQuery")
            .returns(Future.successful(Right(getMessagesResponseModel)))

          await(connector.getMessages(getMessagesRequest)) shouldBe Right(getMessagesResponseModel)
        }
      }

      "return a Left" when {

        "downstream call succeeds but the JSON response body can't be parsed" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISJsonParsingError(Seq(JsonValidationError("'sample' field is wrong"))))

          MockMetricsService.requestTimer(getMessagesRequest.metricName)
          MockMetricsService.processWithTimer()

          val uri = s"$baseUrl/emcs/messages/v1/messages"
          val urlWithQuery: String = uri + makeQueryString(parameters)

          MockHttpClient
            .get(url"$urlWithQuery")
            .returns(Future.successful(response))

          await(connector.getMessages(getMessagesRequest)) shouldBe response
        }

        "downstream call fails due to a 400 (Bad Request) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISJsonSchemaMismatchError("JSON is wrong"))

          MockMetricsService.requestTimer(getMessagesRequest.metricName)
          MockMetricsService.processWithTimer()

          val uri = s"$baseUrl/emcs/messages/v1/messages"
          val urlWithQuery: String = uri + makeQueryString(parameters)

          MockHttpClient
            .get(url"$urlWithQuery")
            .returns(Future.successful(response))

          await(connector.getMessages(getMessagesRequest)) shouldBe response
        }

        "downstream call fails due to a 404 (Not Found) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISResourceNotFoundError("Url?"))

          MockMetricsService.requestTimer(getMessagesRequest.metricName)
          MockMetricsService.processWithTimer()

          val uri = s"$baseUrl/emcs/messages/v1/messages"
          val urlWithQuery: String = uri + makeQueryString(parameters)

          MockHttpClient
            .get(url"$urlWithQuery")
            .returns(Future.successful(response))

          await(connector.getMessages(getMessagesRequest)) shouldBe response
        }

        "downstream call fails due to a 422 (Unprocessable Entity) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISBusinessError("The request body was invalid"))

          MockMetricsService.requestTimer(getMessagesRequest.metricName)
          MockMetricsService.processWithTimer()

          val uri = s"$baseUrl/emcs/messages/v1/messages"
          val urlWithQuery: String = uri + makeQueryString(parameters)

          MockHttpClient
            .get(url"$urlWithQuery")
            .returns(Future.successful(response))

          await(connector.getMessages(getMessagesRequest)) shouldBe response
        }

        "downstream call fails due to a 500 (ISE) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISInternalServerError("Malformed JSON receieved"))

          MockMetricsService.requestTimer(getMessagesRequest.metricName)
          MockMetricsService.processWithTimer()

          val uri = s"$baseUrl/emcs/messages/v1/messages"
          val urlWithQuery: String = uri + makeQueryString(parameters)

          MockHttpClient
            .get(url"$urlWithQuery")
            .returns(Future.successful(response))

          await(connector.getMessages(getMessagesRequest)) shouldBe response
        }

        "downstream call fails due to a 503 (Service Unavailable) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISServiceUnavailableError("No servers running"))

          MockMetricsService.requestTimer(getMessagesRequest.metricName)
          MockMetricsService.processWithTimer()

          val uri = s"$baseUrl/emcs/messages/v1/messages"
          val urlWithQuery: String = uri + makeQueryString(parameters)

          MockHttpClient
            .get(url"$urlWithQuery")
            .returns(Future.successful(response))

          await(connector.getMessages(getMessagesRequest)) shouldBe response
        }

        "downstream call is unsuccessful" in new Test {
          val response: Either[ErrorResponse, String] = Left(EISUnknownError("429 returned"))

          MockMetricsService.requestTimer(getMessagesRequest.metricName)
          MockMetricsService.processWithTimer()

          val uri = s"$baseUrl/emcs/messages/v1/messages"
          val urlWithQuery: String = uri + makeQueryString(parameters)

          MockHttpClient
            .get(url"$urlWithQuery")
            .returns(Future.successful(response))

          await(connector.getMessages(getMessagesRequest)) shouldBe response
        }
      }
    }

    "getSubmissionFailureMessage is called" should {

      val getSubmissionFailureMessageRequest = GetSubmissionFailureMessageRequest(testErn, testMessageId)
      val parameters = Seq(
        "exciseregistrationnumber" -> testErn,
        "uniquemessageid"          -> testMessageId
      )

      import GetSubmissionFailureMessageResponseFixtures._

      "return a Right" when {
        "downstream call is successful" in new Test {

          MockMetricsService.requestTimer(getSubmissionFailureMessageRequest.metricName)
          MockMetricsService.processWithTimer()

          val uri = s"$baseUrl/emcs/messages/v1/submission-failure-message"
          val urlWithQuery: String = uri + makeQueryString(parameters)

          MockHttpClient
            .get(url"$urlWithQuery")
            .returns(Future.successful(Right(getSubmissionFailureMessageResponseModel)))

          await(connector.getSubmissionFailureMessage(getSubmissionFailureMessageRequest)) shouldBe Right(getSubmissionFailureMessageResponseModel)
        }
      }

      "return a Left" when {

        "downstream call succeeds but the JSON response body can't be parsed" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISJsonParsingError(Seq(JsonValidationError("'sample' field is wrong"))))

          MockMetricsService.requestTimer(getSubmissionFailureMessageRequest.metricName)
          MockMetricsService.processWithTimer()

          val uri = s"$baseUrl/emcs/messages/v1/submission-failure-message"
          val urlWithQuery: String = uri + makeQueryString(parameters)

          MockHttpClient
            .get(url"$urlWithQuery")
            .returns(Future.successful(response))

          await(connector.getSubmissionFailureMessage(getSubmissionFailureMessageRequest)) shouldBe response
        }

        "downstream call fails due to a 400 (Bad Request) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISJsonSchemaMismatchError("JSON is wrong"))

          MockMetricsService.requestTimer(getSubmissionFailureMessageRequest.metricName)
          MockMetricsService.processWithTimer()

          val uri = s"$baseUrl/emcs/messages/v1/submission-failure-message"
          val urlWithQuery: String = uri + makeQueryString(parameters)

          MockHttpClient
            .get(url"$urlWithQuery")
            .returns(Future.successful(response))

          await(connector.getSubmissionFailureMessage(getSubmissionFailureMessageRequest)) shouldBe response
        }

        "downstream call fails due to a 404 (Not Found) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISResourceNotFoundError("Url?"))

          MockMetricsService.requestTimer(getSubmissionFailureMessageRequest.metricName)
          MockMetricsService.processWithTimer()

          val uri = s"$baseUrl/emcs/messages/v1/submission-failure-message"
          val urlWithQuery: String = uri + makeQueryString(parameters)

          MockHttpClient
            .get(url"$urlWithQuery")
            .returns(Future.successful(response))

          await(connector.getSubmissionFailureMessage(getSubmissionFailureMessageRequest)) shouldBe response
        }

        "downstream call fails due to a 422 (Unprocessable Entity) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISBusinessError("The request body was invalid"))

          MockMetricsService.requestTimer(getSubmissionFailureMessageRequest.metricName)
          MockMetricsService.processWithTimer()

          val uri = s"$baseUrl/emcs/messages/v1/submission-failure-message"
          val urlWithQuery: String = uri + makeQueryString(parameters)

          MockHttpClient
            .get(url"$urlWithQuery")
            .returns(Future.successful(response))

          await(connector.getSubmissionFailureMessage(getSubmissionFailureMessageRequest)) shouldBe response
        }

        "downstream call fails due to a 500 (ISE) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISInternalServerError("Malformed JSON receieved"))

          MockMetricsService.requestTimer(getSubmissionFailureMessageRequest.metricName)
          MockMetricsService.processWithTimer()

          val uri = s"$baseUrl/emcs/messages/v1/submission-failure-message"
          val urlWithQuery: String = uri + makeQueryString(parameters)

          MockHttpClient
            .get(url"$urlWithQuery")
            .returns(Future.successful(response))

          await(connector.getSubmissionFailureMessage(getSubmissionFailureMessageRequest)) shouldBe response
        }

        "downstream call fails due to a 503 (Service Unavailable) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISServiceUnavailableError("No servers running"))

          MockMetricsService.requestTimer(getSubmissionFailureMessageRequest.metricName)
          MockMetricsService.processWithTimer()

          val uri = s"$baseUrl/emcs/messages/v1/submission-failure-message"
          val urlWithQuery: String = uri + makeQueryString(parameters)

          MockHttpClient
            .get(url"$urlWithQuery")
            .returns(Future.successful(response))

          await(connector.getSubmissionFailureMessage(getSubmissionFailureMessageRequest)) shouldBe response
        }

        "downstream call is unsuccessful" in new Test {
          val response: Either[ErrorResponse, String] = Left(EISUnknownError("429 returned"))

          MockMetricsService.requestTimer(getSubmissionFailureMessageRequest.metricName)
          MockMetricsService.processWithTimer()

          val uri = s"$baseUrl/emcs/messages/v1/submission-failure-message"
          val urlWithQuery: String = uri + makeQueryString(parameters)

          MockHttpClient
            .get(url"$urlWithQuery")
            .returns(Future.successful(response))

          await(connector.getSubmissionFailureMessage(getSubmissionFailureMessageRequest)) shouldBe response
        }
      }
    }

    "markMessageAsRead is called" should {

      val markMessageAsReadRequest = MarkMessageAsReadRequest(testErn, testMessageId)

      "return a Right" when {
        "downstream call is successful" in new Test {

          MockMetricsService.requestTimer(markMessageAsReadRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .putEmpty(
              url = s"$baseUrl/emcs/messages/v1/message?exciseregistrationnumber=$testErn&uniquemessageid=$testMessageId",
              headers = Seq(
                EisHeaders.dateTime      -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
                EisHeaders.correlationId -> markMessageAsReadRequest.correlationUUID,
                EisHeaders.forwardedHost -> "MDTP",
                EisHeaders.source        -> "TFE",
                EisHeaders.authorization -> "Bearer value-messages"
              ),
              bearerToken = "Bearer value-messages"
            )
            .returns(Future.successful(Right(markMessageAsReadResponseModel)))

          await(connector.markMessageAsRead(markMessageAsReadRequest)) shouldBe Right(markMessageAsReadResponseModel)
        }
      }

      "return a Left" when {

        "downstream call succeeds but the JSON response body can't be parsed" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISJsonParsingError(Seq(JsonValidationError("'sample' field is wrong"))))

          MockMetricsService.requestTimer(markMessageAsReadRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .putEmpty(
              url = s"$baseUrl/emcs/messages/v1/message?exciseregistrationnumber=$testErn&uniquemessageid=$testMessageId",
              headers = Seq(
                EisHeaders.dateTime      -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
                EisHeaders.correlationId -> markMessageAsReadRequest.correlationUUID,
                EisHeaders.forwardedHost -> "MDTP",
                EisHeaders.source        -> "TFE",
                EisHeaders.authorization -> "Bearer value-messages"
              ),
              bearerToken = "Bearer value-messages"
            )
            .returns(Future.successful(response))

          await(connector.markMessageAsRead(markMessageAsReadRequest)) shouldBe response
        }

        "downstream call fails due to a 400 (Bad Request) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISJsonSchemaMismatchError("JSON is wrong"))

          MockMetricsService.requestTimer(markMessageAsReadRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .putEmpty(
              url = s"$baseUrl/emcs/messages/v1/message?exciseregistrationnumber=$testErn&uniquemessageid=$testMessageId",
              headers = Seq(
                EisHeaders.dateTime      -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
                EisHeaders.correlationId -> markMessageAsReadRequest.correlationUUID,
                EisHeaders.forwardedHost -> "MDTP",
                EisHeaders.source        -> "TFE",
                EisHeaders.authorization -> "Bearer value-messages"
              ),
              bearerToken = "Bearer value-messages"
            )
            .returns(Future.successful(response))

          await(connector.markMessageAsRead(markMessageAsReadRequest)) shouldBe response
        }

        "downstream call fails due to a 404 (Not Found) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISResourceNotFoundError("Url?"))

          MockMetricsService.requestTimer(markMessageAsReadRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .putEmpty(
              url = s"$baseUrl/emcs/messages/v1/message?exciseregistrationnumber=$testErn&uniquemessageid=$testMessageId",
              headers = Seq(
                EisHeaders.dateTime      -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
                EisHeaders.correlationId -> markMessageAsReadRequest.correlationUUID,
                EisHeaders.forwardedHost -> "MDTP",
                EisHeaders.source        -> "TFE",
                EisHeaders.authorization -> "Bearer value-messages"
              ),
              bearerToken = "Bearer value-messages"
            )
            .returns(Future.successful(response))

          await(connector.markMessageAsRead(markMessageAsReadRequest)) shouldBe response
        }

        "downstream call fails due to a 422 (Unprocessable Entity) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISBusinessError("The request body was invalid"))

          MockMetricsService.requestTimer(markMessageAsReadRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .putEmpty(
              url = s"$baseUrl/emcs/messages/v1/message?exciseregistrationnumber=$testErn&uniquemessageid=$testMessageId",
              headers = Seq(
                EisHeaders.dateTime      -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
                EisHeaders.correlationId -> markMessageAsReadRequest.correlationUUID,
                EisHeaders.forwardedHost -> "MDTP",
                EisHeaders.source        -> "TFE",
                EisHeaders.authorization -> "Bearer value-messages"
              ),
              bearerToken = "Bearer value-messages"
            )
            .returns(Future.successful(response))

          await(connector.markMessageAsRead(markMessageAsReadRequest)) shouldBe response
        }

        "downstream call fails due to a 500 (ISE) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISInternalServerError("Malformed JSON receieved"))

          MockMetricsService.requestTimer(markMessageAsReadRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .putEmpty(
              url = s"$baseUrl/emcs/messages/v1/message?exciseregistrationnumber=$testErn&uniquemessageid=$testMessageId",
              headers = Seq(
                EisHeaders.dateTime      -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
                EisHeaders.correlationId -> markMessageAsReadRequest.correlationUUID,
                EisHeaders.forwardedHost -> "MDTP",
                EisHeaders.source        -> "TFE",
                EisHeaders.authorization -> "Bearer value-messages"
              ),
              bearerToken = "Bearer value-messages"
            )
            .returns(Future.successful(response))

          await(connector.markMessageAsRead(markMessageAsReadRequest)) shouldBe response
        }

        "downstream call fails due to a 503 (Service Unavailable) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISServiceUnavailableError("No servers running"))

          MockMetricsService.requestTimer(markMessageAsReadRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .putEmpty(
              url = s"$baseUrl/emcs/messages/v1/message?exciseregistrationnumber=$testErn&uniquemessageid=$testMessageId",
              headers = Seq(
                EisHeaders.dateTime      -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
                EisHeaders.correlationId -> markMessageAsReadRequest.correlationUUID,
                EisHeaders.forwardedHost -> "MDTP",
                EisHeaders.source        -> "TFE",
                EisHeaders.authorization -> "Bearer value-messages"
              ),
              bearerToken = "Bearer value-messages"
            )
            .returns(Future.successful(response))

          await(connector.markMessageAsRead(markMessageAsReadRequest)) shouldBe response
        }

        "downstream call is unsuccessful" in new Test {
          val response: Either[ErrorResponse, String] = Left(EISUnknownError("429 returned"))

          MockMetricsService.requestTimer(markMessageAsReadRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .putEmpty(
              url = s"$baseUrl/emcs/messages/v1/message?exciseregistrationnumber=$testErn&uniquemessageid=$testMessageId",
              headers = Seq(
                EisHeaders.dateTime      -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
                EisHeaders.correlationId -> markMessageAsReadRequest.correlationUUID,
                EisHeaders.forwardedHost -> "MDTP",
                EisHeaders.source        -> "TFE",
                EisHeaders.authorization -> "Bearer value-messages"
              ),
              bearerToken = "Bearer value-messages"
            )
            .returns(Future.successful(response))

          await(connector.markMessageAsRead(markMessageAsReadRequest)) shouldBe response
        }
      }
    }

    "setMessageAsLogicallyDeleted is called" should {

      val setMessageAsLogicallyDeletedRequest = SetMessageAsLogicallyDeletedRequest(testErn, testMessageId)

      "return a Right" when {
        "downstream call is successful" in new Test {

          MockMetricsService.requestTimer(setMessageAsLogicallyDeletedRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .delete(s"$baseUrl/emcs/messages/v1/message?exciseregistrationnumber=$testErn&uniquemessageid=$testMessageId")
            .returns(Future.successful(Right(setMessageAsLogicallyDeletedResponseModel)))

          await(connector.setMessageAsLogicallyDeleted(setMessageAsLogicallyDeletedRequest)) shouldBe Right(setMessageAsLogicallyDeletedResponseModel)
        }
      }

      "return a Left" when {

        "downstream call succeeds but the JSON response body can't be parsed" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISJsonParsingError(Seq(JsonValidationError("'sample' field is wrong"))))

          MockMetricsService.requestTimer(setMessageAsLogicallyDeletedRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .delete(s"$baseUrl/emcs/messages/v1/message?exciseregistrationnumber=$testErn&uniquemessageid=$testMessageId")
            .returns(Future.successful(response))

          await(connector.setMessageAsLogicallyDeleted(setMessageAsLogicallyDeletedRequest)) shouldBe response
        }

        "downstream call fails due to a 400 (Bad Request) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISJsonSchemaMismatchError("JSON is wrong"))

          MockMetricsService.requestTimer(setMessageAsLogicallyDeletedRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .delete(
              url = s"$baseUrl/emcs/messages/v1/message?exciseregistrationnumber=$testErn&uniquemessageid=$testMessageId"
            )
            .returns(Future.successful(response))

          await(connector.setMessageAsLogicallyDeleted(setMessageAsLogicallyDeletedRequest)) shouldBe response
        }

        "downstream call fails due to a 404 (Not Found) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISResourceNotFoundError("Url?"))

          MockMetricsService.requestTimer(setMessageAsLogicallyDeletedRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .delete(
              url = s"$baseUrl/emcs/messages/v1/message?exciseregistrationnumber=$testErn&uniquemessageid=$testMessageId"
            )
            .returns(Future.successful(response))

          await(connector.setMessageAsLogicallyDeleted(setMessageAsLogicallyDeletedRequest)) shouldBe response
        }

        "downstream call fails due to a 422 (Unprocessable Entity) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISBusinessError("The request body was invalid"))

          MockMetricsService.requestTimer(setMessageAsLogicallyDeletedRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .delete(
              url = s"$baseUrl/emcs/messages/v1/message?exciseregistrationnumber=$testErn&uniquemessageid=$testMessageId"
            )
            .returns(Future.successful(response))

          await(connector.setMessageAsLogicallyDeleted(setMessageAsLogicallyDeletedRequest)) shouldBe response
        }

        "downstream call fails due to a 500 (ISE) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISInternalServerError("Malformed JSON receieved"))

          MockMetricsService.requestTimer(setMessageAsLogicallyDeletedRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .delete(
              url = s"$baseUrl/emcs/messages/v1/message?exciseregistrationnumber=$testErn&uniquemessageid=$testMessageId"
            )
            .returns(Future.successful(response))

          await(connector.setMessageAsLogicallyDeleted(setMessageAsLogicallyDeletedRequest)) shouldBe response
        }

        "downstream call fails due to a 503 (Service Unavailable) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISServiceUnavailableError("No servers running"))

          MockMetricsService.requestTimer(setMessageAsLogicallyDeletedRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .delete(
              url = s"$baseUrl/emcs/messages/v1/message?exciseregistrationnumber=$testErn&uniquemessageid=$testMessageId"
            )
            .returns(Future.successful(response))

          await(connector.setMessageAsLogicallyDeleted(setMessageAsLogicallyDeletedRequest)) shouldBe response
        }

        "downstream call is unsuccessful" in new Test {
          val response: Either[ErrorResponse, String] = Left(EISUnknownError("429 returned"))

          MockMetricsService.requestTimer(setMessageAsLogicallyDeletedRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .delete(
              url = s"$baseUrl/emcs/messages/v1/message?exciseregistrationnumber=$testErn&uniquemessageid=$testMessageId"
            )
            .returns(Future.successful(response))

          await(connector.setMessageAsLogicallyDeleted(setMessageAsLogicallyDeletedRequest)) shouldBe response
        }
      }
    }

    "getMessageStatistics is called" should {

      val getMessageStatisticsRequest = GetMessageStatisticsRequest(testErn)

      val parameters = Seq(
        "exciseregistrationnumber" -> testErn
      )

      "return a Right" when {
        "downstream call is successful" in new Test {

          MockMetricsService.requestTimer(getMessageStatisticsRequest.metricName)
          MockMetricsService.processWithTimer()

          val uri = s"$baseUrl/emcs/messages/v1/message-statistics"
          val urlWithQuery: String = uri + makeQueryString(parameters)

          MockHttpClient
            .get(url"$urlWithQuery")
            .returns(Future.successful(Right(getMessageStatisticsResponseModel)))

          await(connector.getMessageStatistics(getMessageStatisticsRequest)) shouldBe Right(getMessageStatisticsResponseModel)
        }
      }

      "return a Left" when {

        "downstream call succeeds but the JSON response body can't be parsed" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISJsonParsingError(Seq(JsonValidationError("'sample' field is wrong"))))

          MockMetricsService.requestTimer(getMessageStatisticsRequest.metricName)
          MockMetricsService.processWithTimer()

          val uri = s"$baseUrl/emcs/messages/v1/message-statistics"
          val urlWithQuery: String = uri + makeQueryString(parameters)

          MockHttpClient
            .get(url"$urlWithQuery")
            .returns(Future.successful(response))

          await(connector.getMessageStatistics(getMessageStatisticsRequest)) shouldBe response
        }

        "downstream call fails due to a 400 (Bad Request) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISJsonSchemaMismatchError("JSON is wrong"))

          MockMetricsService.requestTimer(getMessageStatisticsRequest.metricName)
          MockMetricsService.processWithTimer()

          val uri = s"$baseUrl/emcs/messages/v1/message-statistics"
          val urlWithQuery: String = uri + makeQueryString(parameters)

          MockHttpClient
            .get(url"$urlWithQuery")
            .returns(Future.successful(response))

          await(connector.getMessageStatistics(getMessageStatisticsRequest)) shouldBe response
        }

        "downstream call fails due to a 404 (Not Found) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISResourceNotFoundError("Url?"))

          MockMetricsService.requestTimer(getMessageStatisticsRequest.metricName)
          MockMetricsService.processWithTimer()

          val uri = s"$baseUrl/emcs/messages/v1/message-statistics"
          val urlWithQuery: String = uri + makeQueryString(parameters)

          MockHttpClient
            .get(url"$urlWithQuery")
            .returns(Future.successful(response))

          await(connector.getMessageStatistics(getMessageStatisticsRequest)) shouldBe response
        }

        "downstream call fails due to a 422 (Unprocessable Entity) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISBusinessError("The request body was invalid"))

          MockMetricsService.requestTimer(getMessageStatisticsRequest.metricName)
          MockMetricsService.processWithTimer()

          val uri = s"$baseUrl/emcs/messages/v1/message-statistics"
          val urlWithQuery: String = uri + makeQueryString(parameters)

          MockHttpClient
            .get(url"$urlWithQuery")
            .returns(Future.successful(response))

          await(connector.getMessageStatistics(getMessageStatisticsRequest)) shouldBe response
        }

        "downstream call fails due to a 500 (ISE) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISInternalServerError("Malformed JSON receieved"))

          MockMetricsService.requestTimer(getMessageStatisticsRequest.metricName)
          MockMetricsService.processWithTimer()

          val uri = s"$baseUrl/emcs/messages/v1/message-statistics"
          val urlWithQuery: String = uri + makeQueryString(parameters)

          MockHttpClient
            .get(url"$urlWithQuery")
            .returns(Future.successful(response))

          await(connector.getMessageStatistics(getMessageStatisticsRequest)) shouldBe response
        }

        "downstream call fails due to a 503 (Service Unavailable) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISServiceUnavailableError("No servers running"))

          MockMetricsService.requestTimer(getMessageStatisticsRequest.metricName)
          MockMetricsService.processWithTimer()

          val uri = s"$baseUrl/emcs/messages/v1/message-statistics"
          val urlWithQuery: String = uri + makeQueryString(parameters)

          MockHttpClient
            .get(url"$urlWithQuery")
            .returns(Future.successful(response))

          await(connector.getMessageStatistics(getMessageStatisticsRequest)) shouldBe response
        }

        "downstream call is unsuccessful" in new Test {
          val response: Either[ErrorResponse, String] = Left(EISUnknownError("429 returned"))

          MockMetricsService.requestTimer(getMessageStatisticsRequest.metricName)
          MockMetricsService.processWithTimer()

          val uri = s"$baseUrl/emcs/messages/v1/message-statistics"
          val urlWithQuery: String = uri + makeQueryString(parameters)

          MockHttpClient
            .get(url"$urlWithQuery")
            .returns(Future.successful(response))

          await(connector.getMessageStatistics(getMessageStatisticsRequest)) shouldBe response
        }
      }
    }

    "getRawMovement is called" should {
      val getMovementRequest = GetMovementRequest(testErn, testArc, Some(1))

      val parameters = Seq(
        "exciseregistrationnumber" -> testErn,
        "arc"                      -> testArc,
        "sequencenumber"           -> "1"
      )

      "return a right" when {
        "when downstream call is successful" in new Test {
          MockMetricsService.requestTimer(getMovementRequest.metricName)
          MockMetricsService.processWithTimer()

          val uri = s"$baseUrl/emcs/movements/v1/movement"
          val urlWithQuery: String = uri + makeQueryString(parameters)

          MockHttpClient
            .get(url"$urlWithQuery")
            .returns(Future.successful(Right(getRawMovementResponse())))

          await(connector.getRawMovement(getMovementRequest)) shouldBe Right(getRawMovementResponse())
        }
      }
      "return a left" when {
        "downstream call returns a left" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISJsonParsingError(Seq(JsonValidationError("'sample' field is wrong"))))

          MockMetricsService.requestTimer(getMovementRequest.metricName)
          MockMetricsService.processWithTimer()

          val uri = s"$baseUrl/emcs/movements/v1/movement"
          val urlWithQuery: String = uri + makeQueryString(parameters)

          MockHttpClient
            .get(url"$urlWithQuery")
            .returns(Future.successful(response))

          await(connector.getRawMovement(getMovementRequest)) shouldBe response
        }
      }
    }

    "getMovementList is called" should {
      val searchOptions = GetMovementListSearchOptions(
        traderRole = Some("foo"),
        sortField = Some("bar"),
        sortOrder = "wizz",
        startPosition = Some(10),
        maxRows = 99,
        arc = Some(testArc),
        otherTraderId = Some("GB123456789"),
        lrn = Some(testLrn),
        dateOfDispatchFrom = Some("06/07/2020"),
        dateOfDispatchTo = Some("07/07/2020"),
        dateOfReceiptFrom = Some("08/07/2020"),
        dateOfReceiptTo = Some("09/07/2020"),
        countryOfOrigin = Some("GB"),
        movementStatus = Some("e-AD Manually Closed"),
        transporterTraderName = Some("Trader 1"),
        undischargedMovements = Some("Accepted"),
        exciseProductCode = Some("6000")
      )
      val getMovementListRequest = GetMovementListRequest(testErn, searchOptions)
      val url                    = "/emcs/movements/v1/movements"
      val queryParameters = Seq(
        "exciseregistrationnumber" -> testErn,
        "traderrole"               -> "foo",
        "sortfield"                -> "bar",
        "sortorder"                -> "wizz",
        "startposition"            -> "10",
        "maxnotoreturn"            -> "99",
        "arc"                      -> testArc,
        "othertraderid"            -> "GB123456789",
        "localreferencenumber"     -> testLrn,
        "dateofdispatchfrom"       -> "06/07/2020",
        "dateofdispatchto"         -> "07/07/2020",
        "dateofreceiptfrom"        -> "08/07/2020",
        "dateofreceiptto"          -> "09/07/2020",
        "countryoforigin"          -> "GB",
        "movementstatus"           -> "e-AD Manually Closed",
        "transportertradername"    -> "Trader 1",
        "undischargedmovements"    -> "Accepted",
        "exciseproductcode"        -> "6000"


      )

      "return a Right" when {
        "downstream call is successful" in new Test {
          MockMetricsService.requestTimer(getMovementListRequest.metricName)
          MockMetricsService.processWithTimer()

          val uri = s"$baseUrl$url"
          val urlWithQuery: String = uri + makeQueryString(queryParameters)

          MockHttpClient
            .get(url"$urlWithQuery")
            .returns(Future.successful(Right(getMovementListResponse)))

          await(connector.getMovementList(getMovementListRequest)) shouldBe Right(getMovementListResponse)
        }
      }
      "return a Left" when {
        "downstream call returns a Left" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISJsonParsingError(Seq(JsonValidationError("'sample' field is wrong"))))

          MockMetricsService.requestTimer(getMovementListRequest.metricName)
          MockMetricsService.processWithTimer()

          val uri = s"$baseUrl$url"
          val urlWithQuery: String = uri + makeQueryString(queryParameters)

          MockHttpClient
            .get(url"$urlWithQuery")
            .returns(Future.successful(response))

          await(connector.getMovementList(getMovementListRequest)) shouldBe response
        }
      }
    }

    "getMovementHistoryEvent is called" should {

      val getMovementHistoryEventsRequest = GetMovementHistoryEventsRequest(testErn, testArc)

      val parameters = Seq(
        "exciseregistrationnumber" -> testErn,
        "arc"                      -> testArc
      )

      "return a Right" when {
        "downstream call is successful" in new Test {

          MockMetricsService.requestTimer(getMovementHistoryEventsRequest.metricName)
          MockMetricsService.processWithTimer()

          val uri = s"$baseUrl/emcs/movements/v1/movement-history"
          val urlWithQuery: String = uri + makeQueryString(parameters)

          MockHttpClient
            .get(url"$urlWithQuery")
            .returns(Future.successful(Right(getMovementHistoryEventsResponseModel)))

          await(connector.getMovementHistoryEvents(getMovementHistoryEventsRequest)) shouldBe Right(getMovementHistoryEventsResponseModel)
        }
      }

      "return a Left" when {

        "downstream call succeeds but the JSON response body can't be parsed" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISJsonParsingError(Seq(JsonValidationError("'sample' field is wrong"))))

          MockMetricsService.requestTimer(getMovementHistoryEventsRequest.metricName)
          MockMetricsService.processWithTimer()

          val uri = s"$baseUrl/emcs/movements/v1/movement-history"
          val urlWithQuery: String = uri + makeQueryString(parameters)

          MockHttpClient
            .get(url"$urlWithQuery")
            .returns(Future.successful(response))

          await(connector.getMovementHistoryEvents(getMovementHistoryEventsRequest)) shouldBe response
        }

        "downstream call fails due to a 400 (Bad Request) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISJsonSchemaMismatchError("JSON is wrong"))

          MockMetricsService.requestTimer(getMovementHistoryEventsRequest.metricName)
          MockMetricsService.processWithTimer()

          val uri = s"$baseUrl/emcs/movements/v1/movement-history"
          val urlWithQuery: String = uri + makeQueryString(parameters)

          MockHttpClient
            .get(url"$urlWithQuery")
            .returns(Future.successful(response))

          await(connector.getMovementHistoryEvents(getMovementHistoryEventsRequest)) shouldBe response
        }

        "downstream call fails due to a 404 (Not Found) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISResourceNotFoundError("Url?"))

          MockMetricsService.requestTimer(getMovementHistoryEventsRequest.metricName)
          MockMetricsService.processWithTimer()

          val uri = s"$baseUrl/emcs/movements/v1/movement-history"
          val urlWithQuery: String = uri + makeQueryString(parameters)

          MockHttpClient
            .get(url"$urlWithQuery")
            .returns(Future.successful(response))

          await(connector.getMovementHistoryEvents(getMovementHistoryEventsRequest)) shouldBe response
        }

        "downstream call fails due to a 422 (Unprocessable Entity) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISBusinessError("The request body was invalid"))

          MockMetricsService.requestTimer(getMovementHistoryEventsRequest.metricName)
          MockMetricsService.processWithTimer()

          val uri = s"$baseUrl/emcs/movements/v1/movement-history"
          val urlWithQuery: String = uri + makeQueryString(parameters)

          MockHttpClient
            .get(url"$urlWithQuery")
            .returns(Future.successful(response))

          await(connector.getMovementHistoryEvents(getMovementHistoryEventsRequest)) shouldBe response
        }

        "downstream call fails due to a 500 (ISE) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISInternalServerError("Malformed JSON receieved"))

          MockMetricsService.requestTimer(getMovementHistoryEventsRequest.metricName)
          MockMetricsService.processWithTimer()

          val uri = s"$baseUrl/emcs/movements/v1/movement-history"
          val urlWithQuery: String = uri + makeQueryString(parameters)

          MockHttpClient
            .get(url"$urlWithQuery")
            .returns(Future.successful(response))

          await(connector.getMovementHistoryEvents(getMovementHistoryEventsRequest)) shouldBe response
        }

        "downstream call fails due to a 503 (Service Unavailable) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISServiceUnavailableError("No servers running"))

          MockMetricsService.requestTimer(getMovementHistoryEventsRequest.metricName)
          MockMetricsService.processWithTimer()

          val uri = s"$baseUrl/emcs/movements/v1/movement-history"
          val urlWithQuery: String = uri + makeQueryString(parameters)

          MockHttpClient
            .get(url"$urlWithQuery")
            .returns(Future.successful(response))

          await(connector.getMovementHistoryEvents(getMovementHistoryEventsRequest)) shouldBe response
        }

        "downstream call is unsuccessful" in new Test {
          val response: Either[ErrorResponse, String] = Left(EISUnknownError("429 returned"))

          MockMetricsService.requestTimer(getMovementHistoryEventsRequest.metricName)
          MockMetricsService.processWithTimer()

          val uri = s"$baseUrl/emcs/movements/v1/movement-history"
          val urlWithQuery: String = uri + makeQueryString(parameters)

          MockHttpClient
            .get(url"$urlWithQuery")
            .returns(Future.successful(response))

          await(connector.getMovementHistoryEvents(getMovementHistoryEventsRequest)) shouldBe response
        }
      }
    }

    "getTraderKnownFactsViaETDS18 is called" should {

      val getTraderKnownFactsViaETDS18Request = TraderKnownFactsETDS18Request(userRequest)

      "return a Right" when {
        "downstream call is successful" in new Test {

          MockMetricsService.requestTimer(getTraderKnownFactsViaETDS18Request.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .get(url"$baseUrl/etds/trader/knownfacts/$testErn")
            .returns(Future.successful(Right(testTraderKnownFactsModel)))

          await(connector.getTraderKnownFactsViaETDS18(testErn)) shouldBe Right(Some(testTraderKnownFactsModel))
        }
      }

      "return a Left" when {

        "downstream call succeeds but the JSON response body can't be parsed" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISJsonParsingError(Seq(JsonValidationError("'sample' field is wrong"))))

          MockMetricsService.requestTimer(getTraderKnownFactsViaETDS18Request.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .get(url"$baseUrl/etds/trader/knownfacts/$testErn")
            .returns(Future.successful(response))

          await(connector.getTraderKnownFactsViaETDS18(testErn)) shouldBe response
        }

        "downstream call fails due to a 400 (Bad Request) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISJsonSchemaMismatchError("JSON is wrong"))

          MockMetricsService.requestTimer(getTraderKnownFactsViaETDS18Request.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .get(url"$baseUrl/etds/trader/knownfacts/$testErn")
            .returns(Future.successful(response))

          await(connector.getTraderKnownFactsViaETDS18(testErn)) shouldBe response
        }

        "downstream call fails due to a 404 (Not Found) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISResourceNotFoundError("Url?"))

          MockMetricsService.requestTimer(getTraderKnownFactsViaETDS18Request.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .get(url"$baseUrl/etds/trader/knownfacts/$testErn")
            .returns(Future.successful(response))

          await(connector.getTraderKnownFactsViaETDS18(testErn)) shouldBe response
        }

        "downstream call fails due to a 422 (Unprocessable Entity) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISBusinessError("The request body was invalid"))

          MockMetricsService.requestTimer(getTraderKnownFactsViaETDS18Request.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .get(url"$baseUrl/etds/trader/knownfacts/$testErn")
            .returns(Future.successful(response))

          await(connector.getTraderKnownFactsViaETDS18(testErn)) shouldBe response
        }

        "downstream call fails due to a 500 (ISE) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISInternalServerError("Malformed JSON receieved"))

          MockMetricsService.requestTimer(getTraderKnownFactsViaETDS18Request.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .get(url"$baseUrl/etds/trader/knownfacts/$testErn")
            .returns(Future.successful(response))

          await(connector.getTraderKnownFactsViaETDS18(testErn)) shouldBe response
        }

        "downstream call fails due to a 503 (Service Unavailable) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISServiceUnavailableError("No servers running"))

          MockMetricsService.requestTimer(getTraderKnownFactsViaETDS18Request.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .get(url"$baseUrl/etds/trader/knownfacts/$testErn")
            .returns(Future.successful(response))

          await(connector.getTraderKnownFactsViaETDS18(testErn)) shouldBe response
        }

        "downstream call is unsuccessful" in new Test {
          val response: Either[ErrorResponse, String] = Left(EISUnknownError("429 returned"))

          MockMetricsService.requestTimer(getTraderKnownFactsViaETDS18Request.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .get(url"$baseUrl/etds/trader/knownfacts/$testErn")
            .returns(Future.successful(response))

          await(connector.getTraderKnownFactsViaETDS18(testErn)) shouldBe response
        }
      }
    }

    "preValidateTraderViaETDS12 is called" should {

      val preValidateTraderViaETDS12Request = PreValidateETDS12Request(userRequest.ern, None, None)

      "return a Right" when {
        "downstream call is successful" in new Test {

          MockMetricsService.requestTimer(preValidateTraderViaETDS12Request.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .postJson(
              url = s"$baseUrl/etds/traderprevalidation/v1",
              body = preValidateTraderViaETDS12Request.toJson
            )
            .returns(Future.successful(Right(preValidateEtds12ApiResponseModel)))

          await(connector.preValidateTraderViaETDS12(preValidateTraderViaETDS12Request)) shouldBe Right(preValidateEtds12ApiResponseModel)
        }
      }

      "return a Left" when {

        "downstream call succeeds but the JSON response body can't be parsed" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISJsonParsingError(Seq(JsonValidationError("'sample' field is wrong"))))

          MockMetricsService.requestTimer(preValidateTraderViaETDS12Request.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .postJson(
              url = s"$baseUrl/etds/traderprevalidation/v1",
              body = preValidateTraderViaETDS12Request.toJson
            )
            .returns(Future.successful(response))

          await(connector.preValidateTraderViaETDS12(preValidateTraderViaETDS12Request)) shouldBe response
        }

        "downstream call fails due to a 400 (Bad Request) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISJsonSchemaMismatchError("JSON is wrong"))

          MockMetricsService.requestTimer(preValidateTraderViaETDS12Request.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .postJson(
              url = s"$baseUrl/etds/traderprevalidation/v1",
              body = preValidateTraderViaETDS12Request.toJson
            )
            .returns(Future.successful(response))

          await(connector.preValidateTraderViaETDS12(preValidateTraderViaETDS12Request)) shouldBe response
        }

        "downstream call fails due to a 404 (Not Found) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISResourceNotFoundError("Url?"))

          MockMetricsService.requestTimer(preValidateTraderViaETDS12Request.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .postJson(
              url = s"$baseUrl/etds/traderprevalidation/v1",
              body = preValidateTraderViaETDS12Request.toJson
            )
            .returns(Future.successful(response))

          await(connector.preValidateTraderViaETDS12(preValidateTraderViaETDS12Request)) shouldBe response
        }

        "downstream call fails due to a 422 (Unprocessable Entity) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISBusinessError("The request body was invalid"))

          MockMetricsService.requestTimer(preValidateTraderViaETDS12Request.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .postJson(
              url = s"$baseUrl/etds/traderprevalidation/v1",
              body = preValidateTraderViaETDS12Request.toJson
            )
            .returns(Future.successful(response))

          await(connector.preValidateTraderViaETDS12(preValidateTraderViaETDS12Request)) shouldBe response
        }

        "downstream call fails due to a 500 (ISE) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISInternalServerError("Malformed JSON receieved"))

          MockMetricsService.requestTimer(preValidateTraderViaETDS12Request.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .postJson(
              url = s"$baseUrl/etds/traderprevalidation/v1",
              body = preValidateTraderViaETDS12Request.toJson
            )
            .returns(Future.successful(response))

          await(connector.preValidateTraderViaETDS12(preValidateTraderViaETDS12Request)) shouldBe response
        }

        "downstream call fails due to a 503 (Service Unavailable) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISServiceUnavailableError("No servers running"))

          MockMetricsService.requestTimer(preValidateTraderViaETDS12Request.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .postJson(
              url = s"$baseUrl/etds/traderprevalidation/v1",
              body = preValidateTraderViaETDS12Request.toJson
            )
            .returns(Future.successful(response))

          await(connector.preValidateTraderViaETDS12(preValidateTraderViaETDS12Request)) shouldBe response
        }

        "downstream call is unsuccessful" in new Test {
          val response: Either[ErrorResponse, String] = Left(EISUnknownError("429 returned"))

          MockMetricsService.requestTimer(preValidateTraderViaETDS12Request.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .postJson(
              url = s"$baseUrl/etds/traderprevalidation/v1",
              body = preValidateTraderViaETDS12Request.toJson
            )
            .returns(Future.successful(response))

          await(connector.preValidateTraderViaETDS12(preValidateTraderViaETDS12Request)) shouldBe response
        }
      }
    }

    "preValidateTrader is called" should {

      val preValidateTraderRequest = preValidateEmc15bApiRequestModel

      "return a Right" when {
        "downstream call is successful" in new Test {

          MockMetricsService.requestTimer(preValidateTraderRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .postJson(
              url = s"$baseUrl/emcs/pre-validate-trader/v1",
              body = preValidateTraderRequest.toJson
            )
            .returns(Future.successful(Right(preValidateEtds12ApiResponseModel)))

          await(connector.preValidateTrader(preValidateTraderRequest)) shouldBe Right(preValidateEtds12ApiResponseModel)
        }
      }

      "return a Left" when {

        "downstream call succeeds but the JSON response body can't be parsed" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISJsonParsingError(Seq(JsonValidationError("'sample' field is wrong"))))

          MockMetricsService.requestTimer(preValidateTraderRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .postJson(
              url = s"$baseUrl/emcs/pre-validate-trader/v1",
              body = preValidateTraderRequest.toJson
            )
            .returns(Future.successful(response))

          await(connector.preValidateTrader(preValidateTraderRequest)) shouldBe response
        }

        "downstream call fails due to a 400 (Bad Request) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISJsonSchemaMismatchError("JSON is wrong"))

          MockMetricsService.requestTimer(preValidateTraderRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .postJson(
              url = s"$baseUrl/emcs/pre-validate-trader/v1",
              body = preValidateTraderRequest.toJson
            )
            .returns(Future.successful(response))

          await(connector.preValidateTrader(preValidateTraderRequest)) shouldBe response
        }

        "downstream call fails due to a 404 (Not Found) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISResourceNotFoundError("Url?"))

          MockMetricsService.requestTimer(preValidateTraderRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .postJson(
              url = s"$baseUrl/emcs/pre-validate-trader/v1",
              body = preValidateTraderRequest.toJson
            )
            .returns(Future.successful(response))

          await(connector.preValidateTrader(preValidateTraderRequest)) shouldBe response
        }

        "downstream call fails due to a 422 (Unprocessable Entity) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISBusinessError("The request body was invalid"))

          MockMetricsService.requestTimer(preValidateTraderRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .postJson(
              url = s"$baseUrl/emcs/pre-validate-trader/v1",
              body = preValidateTraderRequest.toJson
            )
            .returns(Future.successful(response))

          await(connector.preValidateTrader(preValidateTraderRequest)) shouldBe response
        }

        "downstream call fails due to a 500 (ISE) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISInternalServerError("Malformed JSON receieved"))

          MockMetricsService.requestTimer(preValidateTraderRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .postJson(
              url = s"$baseUrl/emcs/pre-validate-trader/v1",
              body = preValidateTraderRequest.toJson
            )
            .returns(Future.successful(response))

          await(connector.preValidateTrader(preValidateTraderRequest)) shouldBe response
        }

        "downstream call fails due to a 503 (Service Unavailable) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISServiceUnavailableError("No servers running"))

          MockMetricsService.requestTimer(preValidateTraderRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .postJson(
              url = s"$baseUrl/emcs/pre-validate-trader/v1",
              body = preValidateTraderRequest.toJson
            )
            .returns(Future.successful(response))

          await(connector.preValidateTrader(preValidateTraderRequest)) shouldBe response
        }

        "downstream call is unsuccessful" in new Test {
          val response: Either[ErrorResponse, String] = Left(EISUnknownError("429 returned"))

          MockMetricsService.requestTimer(preValidateTraderRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient
            .postJson(
              url = s"$baseUrl/emcs/pre-validate-trader/v1",
              body = preValidateTraderRequest.toJson
            )
            .returns(Future.successful(response))

          await(connector.preValidateTrader(preValidateTraderRequest)) shouldBe response
        }
      }
    }

  }

}
