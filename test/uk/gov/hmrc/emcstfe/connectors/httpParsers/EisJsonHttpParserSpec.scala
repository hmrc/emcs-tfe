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

import play.api.http.Status
import play.api.http.Status.OK
import play.api.libs.json.JsonValidationError
import uk.gov.hmrc.emcstfe.fixtures.EISResponsesFixture
import uk.gov.hmrc.emcstfe.models.response.EISSubmissionSuccessResponse
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{EISBusinessError, EISInternalServerError, EISJsonParsingError, EISJsonSchemaMismatchError, EISResourceNotFoundError, EISServiceUnavailableError, EISUnknownError}
import uk.gov.hmrc.emcstfe.support.TestBaseSpec
import uk.gov.hmrc.emcstfe.utils.Logging
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.play.bootstrap.tools.LogCapturing

class EisJsonHttpParserSpec extends TestBaseSpec with EISResponsesFixture with LogCapturing with Logging {

  object TestParser extends EisJsonHttpParser

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

        Seq(Status.BAD_REQUEST -> EISJsonSchemaMismatchError("an error"),
          Status.NOT_FOUND -> EISResourceNotFoundError("an error"),
          Status.UNPROCESSABLE_ENTITY -> EISBusinessError("an error"),
          Status.INTERNAL_SERVER_ERROR -> EISInternalServerError("an error"),
          Status.SERVICE_UNAVAILABLE -> EISServiceUnavailableError("an error")).foreach { statusAndErrorModel =>

          s"a ${statusAndErrorModel._1} response is returned" in {

            val response = HttpResponse(statusAndErrorModel._1, body = "an error", headers = Map.empty)

            val result = TestParser.modelFromJsonHttpReads[EISSubmissionSuccessResponse].read("POST", "/eis/foo/bar", response)

            result shouldBe Left(statusAndErrorModel._2)

          }
        }

        "an unknown response is returned" in {

          val response = HttpResponse(Status.CONFLICT, body = "an error", headers = Map.empty)

          withCaptureOfLoggingFrom(logger) {
            logs =>

              val result = TestParser.modelFromJsonHttpReads[EISSubmissionSuccessResponse].read("POST", "/eis/foo/bar", response)

              result shouldBe Left(EISUnknownError("an error"))

              logs.exists(_.getMessage == "[TestParser][modelFromJsonHttpReads] Received unexpected status: 409") shouldBe true

          }

        }
      }
    }
  }
}
