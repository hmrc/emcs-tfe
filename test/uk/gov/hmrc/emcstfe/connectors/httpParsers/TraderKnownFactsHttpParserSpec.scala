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
import play.api.libs.json.{Json, JsonValidationError}
import uk.gov.hmrc.emcstfe.fixtures.TraderKnownFactsFixtures
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{TraderKnownFactsParsingError, UnexpectedDownstreamResponseError}
import uk.gov.hmrc.emcstfe.support.TestBaseSpec
import uk.gov.hmrc.emcstfe.utils.Logging
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.play.bootstrap.tools.LogCapturing

class TraderKnownFactsHttpParserSpec extends TestBaseSpec with TraderKnownFactsFixtures with LogCapturing with Logging {

  val _logger: LoggerLike = logger

  object TestParser extends TraderKnownFactsHttpParser {
    override val logger: LoggerLike = _logger
  }

  ".modelFromJsonHttpReads" must {

    "return a Right" when {
      s"an OK ($OK) response is received" when {

        "it contains valid Json" in {

          val response = HttpResponse(OK, json = Json.parse(traderKnownFactsETDSJson), headers = Map.empty)

          val result = TestParser.modelFromJsonHttpReads.read("GET", "/foo/bar", response)

          result shouldBe Right(Some(testTraderKnownFactsModel))
        }
      }
      s"a NO_CONTENT ($NO_CONTENT) response is received" when {

        "it contains valid Json" in {

          val response = HttpResponse(NO_CONTENT, json = Json.obj(), headers = Map.empty)

          val result = TestParser.modelFromJsonHttpReads.read("GET", "/foo/bar", response)

          result shouldBe Right(None)
        }
      }
    }

      "return a Left" when {

        "an OK response is returned but the body is invalid JSON" in {

          val response = HttpResponse(OK, json = Json.obj(), headers = Map.empty)

          val validationErrors = Seq(JsonValidationError(Seq("error.path.missing")), JsonValidationError(Seq("error.path.missing")))

          val result = TestParser.modelFromJsonHttpReads.read("GET", "/foo/bar", response)

          result shouldBe Left(TraderKnownFactsParsingError(validationErrors))

        }

        "an unknown response is returned" in {

          val response = HttpResponse(CONFLICT, body = "an error", headers = Map.empty)

          withCaptureOfLoggingFrom(logger) { logs =>
            val result = TestParser.modelFromJsonHttpReads.read("GET", "/foo/bar", response)

            result shouldBe Left(UnexpectedDownstreamResponseError)

            logs.exists(_.getMessage == "[TraderKnownFactsHttpParserSpec][modelFromJsonHttpReads] Received unexpected status: 409") shouldBe true

          }

        }
    }
  }

}
