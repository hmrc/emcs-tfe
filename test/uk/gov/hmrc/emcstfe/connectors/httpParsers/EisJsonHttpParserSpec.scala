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

package uk.gov.hmrc.emcstfe.connectors.httpParsers

import play.api.LoggerLike
import play.api.http.Status._
import play.api.libs.json.JsonValidationError
import uk.gov.hmrc.emcstfe.fixtures.EISResponsesFixture
import uk.gov.hmrc.emcstfe.models.response.EISSubmissionSuccessResponse
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{EISBusinessError, EISInternalServerError, EISJsonParsingError, EISJsonSchemaMismatchError, EISRIMValidationError, EISResourceNotFoundError, EISServiceUnavailableError, EISUnknownError}
import uk.gov.hmrc.emcstfe.support.TestBaseSpec
import uk.gov.hmrc.emcstfe.utils.Logging
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.play.bootstrap.tools.LogCapturing

class EisJsonHttpParserSpec extends TestBaseSpec with EISResponsesFixture with LogCapturing with Logging {

  val _logger: LoggerLike = logger

  object TestParser extends EisJsonHttpParser {
    override val logger: LoggerLike = _logger
  }

  ".modelFromJsonHttpReads" when {

    s"an OK ($OK) response is received" must {

      "return a Right" when {

        "it contains valid Json" in {

          val response = HttpResponse(OK, json = eisSuccessJson(), headers = Map.empty)

          val result = TestParser.modelFromJsonHttpReads[EISSubmissionSuccessResponse].read("POST", "/eis/foo/bar", response)

          result shouldBe Right(eisSuccessResponse)
        }
      }

      "return a Left" when {

        "an OK response is returned but the body is invalid JSON" in {

          val response = HttpResponse(OK, json = incompleteEisSuccessJson, headers = Map.empty)

          val validationErrors = Seq(JsonValidationError(Seq("error.path.missing")))

          val result = TestParser.modelFromJsonHttpReads[EISSubmissionSuccessResponse].read("POST", "/eis/foo/bar", response)

          result shouldBe Left(EISJsonParsingError(validationErrors))

        }

        Seq(BAD_REQUEST -> EISJsonSchemaMismatchError("an error"),
          NOT_FOUND -> EISResourceNotFoundError("an error"),
          INTERNAL_SERVER_ERROR -> EISInternalServerError("an error"),
          SERVICE_UNAVAILABLE -> EISServiceUnavailableError("an error")).foreach { statusAndErrorModel =>

          s"a ${statusAndErrorModel._1} response is returned" in {

            val response = HttpResponse(statusAndErrorModel._1, body = "an error", headers = Map.empty)

            val result = TestParser.modelFromJsonHttpReads[EISSubmissionSuccessResponse].read("POST", "/eis/foo/bar", response)

            result shouldBe Left(statusAndErrorModel._2)

          }
        }

        s"a status code of UNPROCESSABLE_ENTITY $UNPROCESSABLE_ENTITY is returned" in {

          val response = HttpResponse(UNPROCESSABLE_ENTITY, body = eisRimValidationJsonResponse.toString(), headers = Map.empty)

          val result = TestParser.modelFromJsonHttpReads[EISSubmissionSuccessResponse].read("POST", "/eis/foo/bar", response)

          result shouldBe Left(EISRIMValidationError(eisRimValidationResponse))

        }


        s"a status code of UNPROCESSABLE_ENTITY $UNPROCESSABLE_ENTITY is returned but the body is not valid for a RIM validation error response - return response body" in {

          val responseBody = "{\"message\": \"No data found\"}"

          val response = HttpResponse(UNPROCESSABLE_ENTITY, body = responseBody, headers = Map.empty)

          val result = TestParser.modelFromJsonHttpReads[EISSubmissionSuccessResponse].read("POST", "/eis/foo/bar", response)

          result shouldBe Left(EISBusinessError(responseBody))

        }

        s"a status code of UNPROCESSABLE_ENTITY $UNPROCESSABLE_ENTITY is returned but the body is not valid JSON" in {

          val response = HttpResponse(UNPROCESSABLE_ENTITY, body = "foo bar", headers = Map.empty)

          val result = TestParser.modelFromJsonHttpReads[EISSubmissionSuccessResponse].read("POST", "/eis/foo/bar", response)

          result shouldBe Left(EISJsonParsingError(List(JsonValidationError(List("Unrecognized token 'foo': was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')\n at [Source: (String)\"foo bar\"; line: 1, column: 4]")))))

        }

        "an unknown response is returned" in {

          val response = HttpResponse(CONFLICT, body = "an error", headers = Map.empty)

          withCaptureOfLoggingFrom(logger) {
            logs =>

              val result = TestParser.modelFromJsonHttpReads[EISSubmissionSuccessResponse].read("POST", "/eis/foo/bar", response)

              result shouldBe Left(EISUnknownError("an error"))

              logs.exists(_.getMessage == "[EisJsonHttpParserSpec][modelFromJsonHttpReads] Received unexpected status: 409") shouldBe true

          }

        }
      }
    }
  }
}
