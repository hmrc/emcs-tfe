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
import play.api.libs.json.JsonValidationError
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.connectors.httpParsers.EisJsonHttpParser
import uk.gov.hmrc.emcstfe.featureswitch.core.config.{FeatureSwitching, UseDownstreamStub}
import uk.gov.hmrc.emcstfe.fixtures._
import uk.gov.hmrc.emcstfe.mocks.config.MockAppConfig
import uk.gov.hmrc.emcstfe.mocks.connectors.MockHttpClient
import uk.gov.hmrc.emcstfe.mocks.services.MockMetricsService
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.request._
import uk.gov.hmrc.emcstfe.models.request.eis.EisHeaders
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse._
import uk.gov.hmrc.emcstfe.models.response.{EISSubmissionSuccessResponse, ErrorResponse}
import uk.gov.hmrc.emcstfe.support.TestBaseSpec
import uk.gov.hmrc.http.HeaderCarrier

import java.time.Instant
import java.time.temporal.ChronoUnit
import scala.concurrent.{ExecutionContext, Future}

class EisConnectorSpec extends TestBaseSpec
  with Status
  with MimeTypes
  with HeaderNames
  with MockAppConfig
  with MockHttpClient
  with FeatureSwitching
  with BeforeAndAfterEach
  with GetMovementFixture
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
  with MockMetricsService {

  override def afterEach(): Unit = {
    disable(UseDownstreamStub)
    super.afterEach()
  }

  val jsonParser: EisJsonHttpParser = new EisJsonHttpParser

  lazy val config: AppConfig = app.injector.instanceOf[AppConfig]

  trait Test {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

    val connector = new EisConnector(mockHttpClient, config, mockMetricsService, jsonParser)

    val baseUrl: String = "http://localhost:8308"
  }

  "EISConnector" when {

    "submit is called" should {

      implicit val request: UserRequest[AnyContentAsEmpty.type] = UserRequest(FakeRequest(), testErn, testInternalId, testCredId)
      val submitReportOfReceiptRequest = SubmitReportOfReceiptRequest(maxSubmitReportOfReceiptModel)

      "return a Right" when {
        "downstream call is successful" in new Test {

          MockMetricsService.requestTimer(submitReportOfReceiptRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.postJson(
            url = s"$baseUrl/emcs/digital-submit-new-message/v1",
            body = submitReportOfReceiptRequest.toJson,
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> submitReportOfReceiptRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE",
              EisHeaders.contentType -> "application/json",
              EisHeaders.accept -> "application/json"
            )
          ).returns(Future.successful(Right(eisSuccessResponse)))

          await(connector.submit[EISSubmissionSuccessResponse](submitReportOfReceiptRequest, "submitReportOfReceiptEISRequest")) shouldBe Right(eisSuccessResponse)
        }
      }

      "return a Left" when {

        "downstream call succeeds but the JSON response body can't be parsed" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISJsonParsingError(Seq(JsonValidationError("'sample' field is wrong"))))

          MockMetricsService.requestTimer(submitReportOfReceiptRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.postJson(
            url = s"$baseUrl/emcs/digital-submit-new-message/v1",
            body = submitReportOfReceiptRequest.toJson,
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> submitReportOfReceiptRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE",
              EisHeaders.contentType -> "application/json",
              EisHeaders.accept -> "application/json"
            )
          ).returns(Future.successful(response))

          await(connector.submit[EISSubmissionSuccessResponse](submitReportOfReceiptRequest, "submitReportOfReceiptEISRequest")) shouldBe response
        }

        "downstream call fails due to a 400 (Bad Request) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISJsonSchemaMismatchError("JSON is wrong"))

          MockMetricsService.requestTimer(submitReportOfReceiptRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.postJson(
            url = s"$baseUrl/emcs/digital-submit-new-message/v1",
            body = submitReportOfReceiptRequest.toJson,
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> submitReportOfReceiptRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE",
              EisHeaders.contentType -> "application/json",
              EisHeaders.accept -> "application/json"
            )
          ).returns(Future.successful(response))

          await(connector.submit[EISSubmissionSuccessResponse](submitReportOfReceiptRequest, "submitReportOfReceiptEISRequest")) shouldBe response
        }

        "downstream call fails due to a 404 (Not Found) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISResourceNotFoundError("Url?"))

          MockMetricsService.requestTimer(submitReportOfReceiptRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.postJson(
            url = s"$baseUrl/emcs/digital-submit-new-message/v1",
            body = submitReportOfReceiptRequest.toJson,
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> submitReportOfReceiptRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE",
              EisHeaders.contentType -> "application/json",
              EisHeaders.accept -> "application/json"
            )
          ).returns(Future.successful(response))

          await(connector.submit[EISSubmissionSuccessResponse](submitReportOfReceiptRequest, "submitReportOfReceiptEISRequest")) shouldBe response
        }

        "downstream call fails due to a 422 (Unprocessable Entity) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISBusinessError("The request body was invalid"))

          MockMetricsService.requestTimer(submitReportOfReceiptRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.postJson(
            url = s"$baseUrl/emcs/digital-submit-new-message/v1",
            body = submitReportOfReceiptRequest.toJson,
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> submitReportOfReceiptRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE",
              EisHeaders.contentType -> "application/json",
              EisHeaders.accept -> "application/json"
            )
          ).returns(Future.successful(response))

          await(connector.submit[EISSubmissionSuccessResponse](submitReportOfReceiptRequest, "submitReportOfReceiptEISRequest")) shouldBe response
        }

        "downstream call fails due to a 500 (ISE) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISInternalServerError("Malformed JSON receieved"))

          MockMetricsService.requestTimer(submitReportOfReceiptRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.postJson(
            url = s"$baseUrl/emcs/digital-submit-new-message/v1",
            body = submitReportOfReceiptRequest.toJson,
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> submitReportOfReceiptRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE",
              EisHeaders.contentType -> "application/json",
              EisHeaders.accept -> "application/json"
            )
          ).returns(Future.successful(response))

          await(connector.submit[EISSubmissionSuccessResponse](submitReportOfReceiptRequest, "submitReportOfReceiptEISRequest")) shouldBe response
        }

        "downstream call fails due to a 503 (Service Unavailable) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISServiceUnavailableError("No servers running"))

          MockMetricsService.requestTimer(submitReportOfReceiptRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.postJson(
            url = s"$baseUrl/emcs/digital-submit-new-message/v1",
            body = submitReportOfReceiptRequest.toJson,
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> submitReportOfReceiptRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE",
              EisHeaders.contentType -> "application/json",
              EisHeaders.accept -> "application/json"
            )
          ).returns(Future.successful(response))

          await(connector.submit[EISSubmissionSuccessResponse](submitReportOfReceiptRequest, "submitReportOfReceiptEISRequest")) shouldBe response
        }

        "downstream call is unsuccessful" in new Test {
          val response: Either[ErrorResponse, String] = Left(EISUnknownError("429 returned"))

          MockMetricsService.requestTimer(submitReportOfReceiptRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.postJson(
            url = s"$baseUrl/emcs/digital-submit-new-message/v1",
            body = submitReportOfReceiptRequest.toJson,
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> submitReportOfReceiptRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE",
              EisHeaders.contentType -> "application/json",
              EisHeaders.accept -> "application/json"
            )
          ).returns(Future.successful(response))


          await(connector.submit[EISSubmissionSuccessResponse](submitReportOfReceiptRequest, "submitReportOfReceiptEISRequest")) shouldBe response
        }
      }
    }

    "getMessages is called" should {

      val getMessagesRequest = GetMessagesRequest(testErn, "messagetype", "A", 3)

      import GetMessagesResponseFixtures._

      "return a Right" when {
        "downstream call is successful" in new Test {

          MockMetricsService.requestTimer(getMessagesRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.get(
            url = s"$baseUrl/emcs/messages/v1/messages",
            parameters = Seq(
              "exciseregistrationnumber" -> testErn,
              "sortfield" -> "messagetype",
              "sortorder" -> "A",
              "startposition" -> "20",
              "maxnotoreturn" -> "10"
            ),
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> getMessagesRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(Right(getMessagesResponseModel)))

          await(connector.getMessages(getMessagesRequest)) shouldBe Right(getMessagesResponseModel)
        }
      }

      "return a Left" when {

        "downstream call succeeds but the JSON response body can't be parsed" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISJsonParsingError(Seq(JsonValidationError("'sample' field is wrong"))))

          MockMetricsService.requestTimer(getMessagesRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.get(
            url = s"$baseUrl/emcs/messages/v1/messages",
            parameters = Seq(
              "exciseregistrationnumber" -> testErn,
              "sortfield" -> "messagetype",
              "sortorder" -> "A",
              "startposition" -> "20",
              "maxnotoreturn" -> "10"
            ),
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> getMessagesRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(response))

          await(connector.getMessages(getMessagesRequest)) shouldBe response
        }

        "downstream call fails due to a 400 (Bad Request) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISJsonSchemaMismatchError("JSON is wrong"))

          MockMetricsService.requestTimer(getMessagesRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.get(
            url = s"$baseUrl/emcs/messages/v1/messages",
            parameters = Seq(
              "exciseregistrationnumber" -> testErn,
              "sortfield" -> "messagetype",
              "sortorder" -> "A",
              "startposition" -> "20",
              "maxnotoreturn" -> "10"
            ),
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> getMessagesRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(response))

          await(connector.getMessages(getMessagesRequest)) shouldBe response
        }

        "downstream call fails due to a 404 (Not Found) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISResourceNotFoundError("Url?"))

          MockMetricsService.requestTimer(getMessagesRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.get(
            url = s"$baseUrl/emcs/messages/v1/messages",
            parameters = Seq(
              "exciseregistrationnumber" -> testErn,
              "sortfield" -> "messagetype",
              "sortorder" -> "A",
              "startposition" -> "20",
              "maxnotoreturn" -> "10"
            ),
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> getMessagesRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(response))

          await(connector.getMessages(getMessagesRequest)) shouldBe response
        }

        "downstream call fails due to a 422 (Unprocessable Entity) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISBusinessError("The request body was invalid"))

          MockMetricsService.requestTimer(getMessagesRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.get(
            url = s"$baseUrl/emcs/messages/v1/messages",
            parameters = Seq(
              "exciseregistrationnumber" -> testErn,
              "sortfield" -> "messagetype",
              "sortorder" -> "A",
              "startposition" -> "20",
              "maxnotoreturn" -> "10"
            ),
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> getMessagesRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(response))

          await(connector.getMessages(getMessagesRequest)) shouldBe response
        }

        "downstream call fails due to a 500 (ISE) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISInternalServerError("Malformed JSON receieved"))

          MockMetricsService.requestTimer(getMessagesRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.get(
            url = s"$baseUrl/emcs/messages/v1/messages",
            parameters = Seq(
              "exciseregistrationnumber" -> testErn,
              "sortfield" -> "messagetype",
              "sortorder" -> "A",
              "startposition" -> "20",
              "maxnotoreturn" -> "10"
            ),
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> getMessagesRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(response))

          await(connector.getMessages(getMessagesRequest)) shouldBe response
        }

        "downstream call fails due to a 503 (Service Unavailable) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISServiceUnavailableError("No servers running"))

          MockMetricsService.requestTimer(getMessagesRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.get(
            url = s"$baseUrl/emcs/messages/v1/messages",
            parameters = Seq(
              "exciseregistrationnumber" -> testErn,
              "sortfield" -> "messagetype",
              "sortorder" -> "A",
              "startposition" -> "20",
              "maxnotoreturn" -> "10"
            ),
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> getMessagesRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(response))

          await(connector.getMessages(getMessagesRequest)) shouldBe response
        }

        "downstream call is unsuccessful" in new Test {
          val response: Either[ErrorResponse, String] = Left(EISUnknownError("429 returned"))

          MockMetricsService.requestTimer(getMessagesRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.get(
            url = s"$baseUrl/emcs/messages/v1/messages",
            parameters = Seq(
              "exciseregistrationnumber" -> testErn,
              "sortfield" -> "messagetype",
              "sortorder" -> "A",
              "startposition" -> "20",
              "maxnotoreturn" -> "10"
            ),
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> getMessagesRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(response))

          await(connector.getMessages(getMessagesRequest)) shouldBe response
        }
      }
    }

    "getSubmissionFailureMessage is called" should {

      val getSubmissionFailureMessageRequest = GetSubmissionFailureMessageRequest(testErn, testMessageId)

      import GetSubmissionFailureMessageResponseFixtures._

      "return a Right" when {
        "downstream call is successful" in new Test {

          MockMetricsService.requestTimer(getSubmissionFailureMessageRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.get(
            url = s"$baseUrl/emcs/messages/v1/submission-failure-message",
            parameters = Seq(
              "exciseregistrationnumber" -> testErn,
              "uniquemessageid" -> testMessageId
            ),
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> getSubmissionFailureMessageRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(Right(getSubmissionFailureMessageResponseModel)))

          await(connector.getSubmissionFailureMessage(getSubmissionFailureMessageRequest)) shouldBe Right(getSubmissionFailureMessageResponseModel)
        }
      }

      "return a Left" when {

        "downstream call succeeds but the JSON response body can't be parsed" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISJsonParsingError(Seq(JsonValidationError("'sample' field is wrong"))))

          MockMetricsService.requestTimer(getSubmissionFailureMessageRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.get(
            url = s"$baseUrl/emcs/messages/v1/submission-failure-message",
            parameters = Seq(
              "exciseregistrationnumber" -> testErn,
              "uniquemessageid" -> testMessageId
            ),
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> getSubmissionFailureMessageRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(response))

          await(connector.getSubmissionFailureMessage(getSubmissionFailureMessageRequest)) shouldBe response
        }

        "downstream call fails due to a 400 (Bad Request) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISJsonSchemaMismatchError("JSON is wrong"))

          MockMetricsService.requestTimer(getSubmissionFailureMessageRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.get(
            url = s"$baseUrl/emcs/messages/v1/submission-failure-message",
            parameters = Seq(
              "exciseregistrationnumber" -> testErn,
              "uniquemessageid" -> testMessageId
            ),
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> getSubmissionFailureMessageRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(response))

          await(connector.getSubmissionFailureMessage(getSubmissionFailureMessageRequest)) shouldBe response
        }

        "downstream call fails due to a 404 (Not Found) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISResourceNotFoundError("Url?"))

          MockMetricsService.requestTimer(getSubmissionFailureMessageRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.get(
            url = s"$baseUrl/emcs/messages/v1/submission-failure-message",
            parameters = Seq(
              "exciseregistrationnumber" -> testErn,
              "uniquemessageid" -> testMessageId
            ),
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> getSubmissionFailureMessageRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(response))

          await(connector.getSubmissionFailureMessage(getSubmissionFailureMessageRequest)) shouldBe response
        }

        "downstream call fails due to a 422 (Unprocessable Entity) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISBusinessError("The request body was invalid"))

          MockMetricsService.requestTimer(getSubmissionFailureMessageRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.get(
            url = s"$baseUrl/emcs/messages/v1/submission-failure-message",
            parameters = Seq(
              "exciseregistrationnumber" -> testErn,
              "uniquemessageid" -> testMessageId
            ),
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> getSubmissionFailureMessageRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(response))

          await(connector.getSubmissionFailureMessage(getSubmissionFailureMessageRequest)) shouldBe response
        }

        "downstream call fails due to a 500 (ISE) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISInternalServerError("Malformed JSON receieved"))

          MockMetricsService.requestTimer(getSubmissionFailureMessageRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.get(
            url = s"$baseUrl/emcs/messages/v1/submission-failure-message",
            parameters = Seq(
              "exciseregistrationnumber" -> testErn,
              "uniquemessageid" -> testMessageId
            ),
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> getSubmissionFailureMessageRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(response))

          await(connector.getSubmissionFailureMessage(getSubmissionFailureMessageRequest)) shouldBe response
        }

        "downstream call fails due to a 503 (Service Unavailable) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISServiceUnavailableError("No servers running"))

          MockMetricsService.requestTimer(getSubmissionFailureMessageRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.get(
            url = s"$baseUrl/emcs/messages/v1/submission-failure-message",
            parameters = Seq(
              "exciseregistrationnumber" -> testErn,
              "uniquemessageid" -> testMessageId
            ),
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> getSubmissionFailureMessageRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(response))

          await(connector.getSubmissionFailureMessage(getSubmissionFailureMessageRequest)) shouldBe response
        }

        "downstream call is unsuccessful" in new Test {
          val response: Either[ErrorResponse, String] = Left(EISUnknownError("429 returned"))

          MockMetricsService.requestTimer(getSubmissionFailureMessageRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.get(
            url = s"$baseUrl/emcs/messages/v1/submission-failure-message",
            parameters = Seq(
              "exciseregistrationnumber" -> testErn,
              "uniquemessageid" -> testMessageId
            ),
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> getSubmissionFailureMessageRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(response))

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

          MockHttpClient.putEmpty(
            url = s"$baseUrl/emcs/messages/v1/message?exciseregistrationnumber=$testErn&uniquemessageid=$testMessageId",
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> markMessageAsReadRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(Right(markMessageAsReadResponseModel)))

          await(connector.markMessageAsRead(markMessageAsReadRequest)) shouldBe Right(markMessageAsReadResponseModel)
        }
      }

      "return a Left" when {

        "downstream call succeeds but the JSON response body can't be parsed" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISJsonParsingError(Seq(JsonValidationError("'sample' field is wrong"))))

          MockMetricsService.requestTimer(markMessageAsReadRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.putEmpty(
            url = s"$baseUrl/emcs/messages/v1/message?exciseregistrationnumber=$testErn&uniquemessageid=$testMessageId",
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> markMessageAsReadRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(response))

          await(connector.markMessageAsRead(markMessageAsReadRequest)) shouldBe response
        }

        "downstream call fails due to a 400 (Bad Request) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISJsonSchemaMismatchError("JSON is wrong"))

          MockMetricsService.requestTimer(markMessageAsReadRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.putEmpty(
            url = s"$baseUrl/emcs/messages/v1/message?exciseregistrationnumber=$testErn&uniquemessageid=$testMessageId",
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> markMessageAsReadRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(response))

          await(connector.markMessageAsRead(markMessageAsReadRequest)) shouldBe response
        }

        "downstream call fails due to a 404 (Not Found) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISResourceNotFoundError("Url?"))

          MockMetricsService.requestTimer(markMessageAsReadRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.putEmpty(
            url = s"$baseUrl/emcs/messages/v1/message?exciseregistrationnumber=$testErn&uniquemessageid=$testMessageId",
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> markMessageAsReadRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(response))

          await(connector.markMessageAsRead(markMessageAsReadRequest)) shouldBe response
        }

        "downstream call fails due to a 422 (Unprocessable Entity) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISBusinessError("The request body was invalid"))

          MockMetricsService.requestTimer(markMessageAsReadRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.putEmpty(
            url = s"$baseUrl/emcs/messages/v1/message?exciseregistrationnumber=$testErn&uniquemessageid=$testMessageId",
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> markMessageAsReadRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(response))

          await(connector.markMessageAsRead(markMessageAsReadRequest)) shouldBe response
        }

        "downstream call fails due to a 500 (ISE) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISInternalServerError("Malformed JSON receieved"))

          MockMetricsService.requestTimer(markMessageAsReadRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.putEmpty(
            url = s"$baseUrl/emcs/messages/v1/message?exciseregistrationnumber=$testErn&uniquemessageid=$testMessageId",
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> markMessageAsReadRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(response))

          await(connector.markMessageAsRead(markMessageAsReadRequest)) shouldBe response
        }

        "downstream call fails due to a 503 (Service Unavailable) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISServiceUnavailableError("No servers running"))

          MockMetricsService.requestTimer(markMessageAsReadRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.putEmpty(
            url = s"$baseUrl/emcs/messages/v1/message?exciseregistrationnumber=$testErn&uniquemessageid=$testMessageId",
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> markMessageAsReadRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(response))

          await(connector.markMessageAsRead(markMessageAsReadRequest)) shouldBe response
        }

        "downstream call is unsuccessful" in new Test {
          val response: Either[ErrorResponse, String] = Left(EISUnknownError("429 returned"))

          MockMetricsService.requestTimer(markMessageAsReadRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.putEmpty(
            url = s"$baseUrl/emcs/messages/v1/message?exciseregistrationnumber=$testErn&uniquemessageid=$testMessageId",
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> markMessageAsReadRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(response))

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

          MockHttpClient.delete(
            url = s"$baseUrl/emcs/messages/v1/message?exciseregistrationnumber=$testErn&uniquemessageid=$testMessageId",
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> setMessageAsLogicallyDeletedRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(Right(setMessageAsLogicallyDeletedResponseModel)))

          await(connector.setMessageAsLogicallyDeleted(setMessageAsLogicallyDeletedRequest)) shouldBe Right(setMessageAsLogicallyDeletedResponseModel)
        }
      }

      "return a Left" when {

        "downstream call succeeds but the JSON response body can't be parsed" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISJsonParsingError(Seq(JsonValidationError("'sample' field is wrong"))))

          MockMetricsService.requestTimer(setMessageAsLogicallyDeletedRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.delete(
            url = s"$baseUrl/emcs/messages/v1/message?exciseregistrationnumber=$testErn&uniquemessageid=$testMessageId",
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> setMessageAsLogicallyDeletedRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(response))

          await(connector.setMessageAsLogicallyDeleted(setMessageAsLogicallyDeletedRequest)) shouldBe response
        }

        "downstream call fails due to a 400 (Bad Request) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISJsonSchemaMismatchError("JSON is wrong"))

          MockMetricsService.requestTimer(setMessageAsLogicallyDeletedRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.delete(
            url = s"$baseUrl/emcs/messages/v1/message?exciseregistrationnumber=$testErn&uniquemessageid=$testMessageId",
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> setMessageAsLogicallyDeletedRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(response))

          await(connector.setMessageAsLogicallyDeleted(setMessageAsLogicallyDeletedRequest)) shouldBe response
        }

        "downstream call fails due to a 404 (Not Found) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISResourceNotFoundError("Url?"))

          MockMetricsService.requestTimer(setMessageAsLogicallyDeletedRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.delete(
            url = s"$baseUrl/emcs/messages/v1/message?exciseregistrationnumber=$testErn&uniquemessageid=$testMessageId",
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> setMessageAsLogicallyDeletedRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(response))

          await(connector.setMessageAsLogicallyDeleted(setMessageAsLogicallyDeletedRequest)) shouldBe response
        }

        "downstream call fails due to a 422 (Unprocessable Entity) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISBusinessError("The request body was invalid"))

          MockMetricsService.requestTimer(setMessageAsLogicallyDeletedRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.delete(
            url = s"$baseUrl/emcs/messages/v1/message?exciseregistrationnumber=$testErn&uniquemessageid=$testMessageId",
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> setMessageAsLogicallyDeletedRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(response))

          await(connector.setMessageAsLogicallyDeleted(setMessageAsLogicallyDeletedRequest)) shouldBe response
        }

        "downstream call fails due to a 500 (ISE) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISInternalServerError("Malformed JSON receieved"))

          MockMetricsService.requestTimer(setMessageAsLogicallyDeletedRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.delete(
            url = s"$baseUrl/emcs/messages/v1/message?exciseregistrationnumber=$testErn&uniquemessageid=$testMessageId",
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> setMessageAsLogicallyDeletedRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(response))

          await(connector.setMessageAsLogicallyDeleted(setMessageAsLogicallyDeletedRequest)) shouldBe response
        }

        "downstream call fails due to a 503 (Service Unavailable) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISServiceUnavailableError("No servers running"))

          MockMetricsService.requestTimer(setMessageAsLogicallyDeletedRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.delete(
            url = s"$baseUrl/emcs/messages/v1/message?exciseregistrationnumber=$testErn&uniquemessageid=$testMessageId",
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> setMessageAsLogicallyDeletedRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(response))

          await(connector.setMessageAsLogicallyDeleted(setMessageAsLogicallyDeletedRequest)) shouldBe response
        }

        "downstream call is unsuccessful" in new Test {
          val response: Either[ErrorResponse, String] = Left(EISUnknownError("429 returned"))

          MockMetricsService.requestTimer(setMessageAsLogicallyDeletedRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.delete(
            url = s"$baseUrl/emcs/messages/v1/message?exciseregistrationnumber=$testErn&uniquemessageid=$testMessageId",
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> setMessageAsLogicallyDeletedRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(response))

          await(connector.setMessageAsLogicallyDeleted(setMessageAsLogicallyDeletedRequest)) shouldBe response
        }
      }
    }

    "getMessageStatistics is called" should {

      val getMessageStatisticsRequest = GetMessageStatisticsRequest(testErn)

      "return a Right" when {
        "downstream call is successful" in new Test {

          MockMetricsService.requestTimer(getMessageStatisticsRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.get(
            url = s"$baseUrl/emcs/messages/v1/message-statistics",
            parameters = Seq(
              "exciseregistrationnumber" -> testErn
            ),
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> getMessageStatisticsRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(Right(getMessageStatisticsResponseModel)))

          await(connector.getMessageStatistics(getMessageStatisticsRequest)) shouldBe Right(getMessageStatisticsResponseModel)
        }
      }

      "return a Left" when {

        "downstream call succeeds but the JSON response body can't be parsed" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISJsonParsingError(Seq(JsonValidationError("'sample' field is wrong"))))

          MockMetricsService.requestTimer(getMessageStatisticsRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.get(
            url = s"$baseUrl/emcs/messages/v1/message-statistics",
            parameters = Seq(
              "exciseregistrationnumber" -> testErn
            ),
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> getMessageStatisticsRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(response))

          await(connector.getMessageStatistics(getMessageStatisticsRequest)) shouldBe response
        }

        "downstream call fails due to a 400 (Bad Request) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISJsonSchemaMismatchError("JSON is wrong"))

          MockMetricsService.requestTimer(getMessageStatisticsRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.get(
            url = s"$baseUrl/emcs/messages/v1/message-statistics",
            parameters = Seq(
              "exciseregistrationnumber" -> testErn
            ),
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> getMessageStatisticsRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(response))

          await(connector.getMessageStatistics(getMessageStatisticsRequest)) shouldBe response
        }

        "downstream call fails due to a 404 (Not Found) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISResourceNotFoundError("Url?"))

          MockMetricsService.requestTimer(getMessageStatisticsRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.get(
            url = s"$baseUrl/emcs/messages/v1/message-statistics",
            parameters = Seq(
              "exciseregistrationnumber" -> testErn
            ),
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> getMessageStatisticsRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(response))

          await(connector.getMessageStatistics(getMessageStatisticsRequest)) shouldBe response
        }

        "downstream call fails due to a 422 (Unprocessable Entity) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISBusinessError("The request body was invalid"))

          MockMetricsService.requestTimer(getMessageStatisticsRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.get(
            url = s"$baseUrl/emcs/messages/v1/message-statistics",
            parameters = Seq(
              "exciseregistrationnumber" -> testErn
            ),
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> getMessageStatisticsRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(response))

          await(connector.getMessageStatistics(getMessageStatisticsRequest)) shouldBe response
        }

        "downstream call fails due to a 500 (ISE) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISInternalServerError("Malformed JSON receieved"))

          MockMetricsService.requestTimer(getMessageStatisticsRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.get(
            url = s"$baseUrl/emcs/messages/v1/message-statistics",
            parameters = Seq(
              "exciseregistrationnumber" -> testErn
            ),
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> getMessageStatisticsRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(response))

          await(connector.getMessageStatistics(getMessageStatisticsRequest)) shouldBe response
        }

        "downstream call fails due to a 503 (Service Unavailable) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISServiceUnavailableError("No servers running"))

          MockMetricsService.requestTimer(getMessageStatisticsRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.get(
            url = s"$baseUrl/emcs/messages/v1/message-statistics",
            parameters = Seq(
              "exciseregistrationnumber" -> testErn
            ),
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> getMessageStatisticsRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(response))

          await(connector.getMessageStatistics(getMessageStatisticsRequest)) shouldBe response
        }

        "downstream call is unsuccessful" in new Test {
          val response: Either[ErrorResponse, String] = Left(EISUnknownError("429 returned"))

          MockMetricsService.requestTimer(getMessageStatisticsRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.get(
            url = s"$baseUrl/emcs/messages/v1/message-statistics",
            parameters = Seq(
              "exciseregistrationnumber" -> testErn
            ),
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> getMessageStatisticsRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(response))

          await(connector.getMessageStatistics(getMessageStatisticsRequest)) shouldBe response
        }
      }
    }

    "getRawMovement is called" should {
      val getMovementRequest = GetMovementRequest(testErn, testArc, Some(1))
      val  url = "/emcs/movements/v1/movement"
      "return a right" when {
        "when downstream call is successful" in new Test {
          MockMetricsService.requestTimer(getMovementRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.get(
            url = s"$baseUrl$url",
            parameters = Seq(
              "exciseregistrationnumber" -> testErn,
              "arc" -> testArc,
              "sequencenumber" -> "1"
            ),
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> getMovementRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(Right(getRawMovementResponse)))

          await(connector.getRawMovement(getMovementRequest)) shouldBe Right(getRawMovementResponse)
        }
      }
      "return a left" when {
        "downstream call returns a left" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISJsonParsingError(Seq(JsonValidationError("'sample' field is wrong"))))

          MockMetricsService.requestTimer(getMovementRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.get(
            url = s"$baseUrl$url",
            parameters = Seq(
              "exciseregistrationnumber" -> testErn,
              "arc" -> testArc,
              "sequencenumber" -> "1"
            ),
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> getMovementRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE"
            )
          ).returns(Future.successful(response))

          await(connector.getRawMovement(getMovementRequest)) shouldBe response
        }
      }
    }
  }

}
