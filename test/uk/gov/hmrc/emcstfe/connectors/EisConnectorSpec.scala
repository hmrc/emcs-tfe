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
import uk.gov.hmrc.emcstfe.models.common.SubmitterType.Consignee
import uk.gov.hmrc.emcstfe.models.request._
import uk.gov.hmrc.emcstfe.models.request.eis.EisHeaders
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse._
import uk.gov.hmrc.emcstfe.models.response.{EISSuccessResponse, ErrorResponse}
import uk.gov.hmrc.emcstfe.support.TestBaseSpec
import uk.gov.hmrc.http.HeaderCarrier

import java.time.Instant
import java.time.temporal.ChronoUnit
import scala.concurrent.{ExecutionContext, Future}

class EisConnectorSpec extends TestBaseSpec with Status with MimeTypes with HeaderNames with MockAppConfig with MockHttpClient with FeatureSwitching with BeforeAndAfterEach
  with GetMovementFixture
  with SubmitReportOfReceiptFixtures
  with SubmitExplainShortageExcessFixtures
  with CreateMovementFixtures
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
    "submitReportOfReceiptEISRequest is called" should {

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

          await(connector.submitReportOfReceiptEISRequest[EISSuccessResponse](submitReportOfReceiptRequest)) shouldBe Right(eisSuccessResponse)
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

          await(connector.submitReportOfReceiptEISRequest[EISSuccessResponse](submitReportOfReceiptRequest)) shouldBe response
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

          await(connector.submitReportOfReceiptEISRequest[EISSuccessResponse](submitReportOfReceiptRequest)) shouldBe response
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

          await(connector.submitReportOfReceiptEISRequest[EISSuccessResponse](submitReportOfReceiptRequest)) shouldBe response
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

          await(connector.submitReportOfReceiptEISRequest[EISSuccessResponse](submitReportOfReceiptRequest)) shouldBe response
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

          await(connector.submitReportOfReceiptEISRequest[EISSuccessResponse](submitReportOfReceiptRequest)) shouldBe response
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

          await(connector.submitReportOfReceiptEISRequest[EISSuccessResponse](submitReportOfReceiptRequest)) shouldBe response
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


          await(connector.submitReportOfReceiptEISRequest[EISSuccessResponse](submitReportOfReceiptRequest)) shouldBe response
        }
      }
    }

    "submitExplainShortageExcessEISRequest is called" should {

      implicit val request: UserRequest[AnyContentAsEmpty.type] = UserRequest(FakeRequest(), testErn, testInternalId, testCredId)

      val submitExplainShortageExcessRequest = SubmitExplainShortageExcessRequest(SubmitExplainShortageExcessFixtures.submitExplainShortageExcessModelMax(Consignee))

      "return a Right" when {
        "downstream call is successful" in new Test {

          MockMetricsService.requestTimer(submitExplainShortageExcessRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.postJson(
            url = s"$baseUrl/emcs/digital-submit-new-message/v1",
            body = submitExplainShortageExcessRequest.toJson,
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> submitExplainShortageExcessRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE",
              EisHeaders.contentType -> "application/json",
              EisHeaders.accept -> "application/json"
            )
          ).returns(Future.successful(Right(eisSuccessResponse)))

          await(connector.submitExplainShortageExcessEISRequest[EISSuccessResponse](submitExplainShortageExcessRequest)) shouldBe Right(eisSuccessResponse)
        }
      }

      "return a Left" when {

        "downstream call succeeds but the JSON response body can't be parsed" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISJsonParsingError(Seq(JsonValidationError("'sample' field is wrong"))))

          MockMetricsService.requestTimer(submitExplainShortageExcessRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.postJson(
            url = s"$baseUrl/emcs/digital-submit-new-message/v1",
            body = submitExplainShortageExcessRequest.toJson,
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> submitExplainShortageExcessRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE",
              EisHeaders.contentType -> "application/json",
              EisHeaders.accept -> "application/json"
            )
          ).returns(Future.successful(response))

          await(connector.submitExplainShortageExcessEISRequest[EISSuccessResponse](submitExplainShortageExcessRequest)) shouldBe response
        }

        "downstream call fails due to a 400 (Bad Request) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISJsonSchemaMismatchError("JSON is wrong"))

          MockMetricsService.requestTimer(submitExplainShortageExcessRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.postJson(
            url = s"$baseUrl/emcs/digital-submit-new-message/v1",
            body = submitExplainShortageExcessRequest.toJson,
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> submitExplainShortageExcessRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE",
              EisHeaders.contentType -> "application/json",
              EisHeaders.accept -> "application/json"
            )
          ).returns(Future.successful(response))

          await(connector.submitExplainShortageExcessEISRequest[EISSuccessResponse](submitExplainShortageExcessRequest)) shouldBe response
        }

        "downstream call fails due to a 404 (Not Found) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISResourceNotFoundError("Url?"))

          MockMetricsService.requestTimer(submitExplainShortageExcessRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.postJson(
            url = s"$baseUrl/emcs/digital-submit-new-message/v1",
            body = submitExplainShortageExcessRequest.toJson,
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> submitExplainShortageExcessRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE",
              EisHeaders.contentType -> "application/json",
              EisHeaders.accept -> "application/json"
            )
          ).returns(Future.successful(response))

          await(connector.submitExplainShortageExcessEISRequest[EISSuccessResponse](submitExplainShortageExcessRequest)) shouldBe response
        }

        "downstream call fails due to a 422 (Unprocessable Entity) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISBusinessError("The request body was invalid"))

          MockMetricsService.requestTimer(submitExplainShortageExcessRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.postJson(
            url = s"$baseUrl/emcs/digital-submit-new-message/v1",
            body = submitExplainShortageExcessRequest.toJson,
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> submitExplainShortageExcessRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE",
              EisHeaders.contentType -> "application/json",
              EisHeaders.accept -> "application/json"
            )
          ).returns(Future.successful(response))

          await(connector.submitExplainShortageExcessEISRequest[EISSuccessResponse](submitExplainShortageExcessRequest)) shouldBe response
        }

        "downstream call fails due to a 500 (ISE) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISInternalServerError("Malformed JSON receieved"))

          MockMetricsService.requestTimer(submitExplainShortageExcessRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.postJson(
            url = s"$baseUrl/emcs/digital-submit-new-message/v1",
            body = submitExplainShortageExcessRequest.toJson,
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> submitExplainShortageExcessRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE",
              EisHeaders.contentType -> "application/json",
              EisHeaders.accept -> "application/json"
            )
          ).returns(Future.successful(response))

          await(connector.submitExplainShortageExcessEISRequest[EISSuccessResponse](submitExplainShortageExcessRequest)) shouldBe response
        }

        "downstream call fails due to a 503 (Service Unavailable) response" in new Test {

          val response: Either[ErrorResponse, String] = Left(EISServiceUnavailableError("No servers running"))

          MockMetricsService.requestTimer(submitExplainShortageExcessRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.postJson(
            url = s"$baseUrl/emcs/digital-submit-new-message/v1",
            body = submitExplainShortageExcessRequest.toJson,
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> submitExplainShortageExcessRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE",
              EisHeaders.contentType -> "application/json",
              EisHeaders.accept -> "application/json"
            )
          ).returns(Future.successful(response))

          await(connector.submitExplainShortageExcessEISRequest[EISSuccessResponse](submitExplainShortageExcessRequest)) shouldBe response
        }

        "downstream call is unsuccessful" in new Test {
          val response: Either[ErrorResponse, String] = Left(EISUnknownError("429 returned"))

          MockMetricsService.requestTimer(submitExplainShortageExcessRequest.metricName)
          MockMetricsService.processWithTimer()

          MockHttpClient.postJson(
            url = s"$baseUrl/emcs/digital-submit-new-message/v1",
            body = submitExplainShortageExcessRequest.toJson,
            headers = Seq(
              EisHeaders.dateTime -> s"${Instant.now.truncatedTo(ChronoUnit.MILLIS)}",
              EisHeaders.correlationId -> submitExplainShortageExcessRequest.correlationUUID.toString,
              EisHeaders.forwardedHost -> "MDTP",
              EisHeaders.source -> "TFE",
              EisHeaders.contentType -> "application/json",
              EisHeaders.accept -> "application/json"
            )
          ).returns(Future.successful(response))


          await(connector.submitExplainShortageExcessEISRequest[EISSuccessResponse](submitExplainShortageExcessRequest)) shouldBe response
        }
      }
    }
  }

}