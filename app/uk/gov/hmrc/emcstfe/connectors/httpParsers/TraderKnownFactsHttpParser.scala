/*
 * Copyright 2024 HM Revenue & Customs
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
import play.api.http.Status.{NO_CONTENT, OK}
import play.api.libs.json.{JsonValidationError, Reads}
import uk.gov.hmrc.emcstfe.models.response.{ErrorResponse, TraderKnownFacts}
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{TraderKnownFactsParsingError, UnexpectedDownstreamResponseError}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

import scala.util.{Failure, Success, Try}

trait TraderKnownFactsHttpParser {
  val logger: LoggerLike

  def modelFromJsonHttpReads(implicit jsonReads: Reads[TraderKnownFacts]): HttpReads[Either[ErrorResponse, Option[TraderKnownFacts]]] = (_: String, _: String, response: HttpResponse) => {
    response.status match {
      case OK => Try {
        jsonReads.reads(response.json).fold(
          errors => Left(TraderKnownFactsParsingError(errors.flatMap(_._2).toSeq)),
          Right(_)
        )
      } match {
        case Failure(exception) =>
          logger.error(exception.getMessage, exception)
          Left(TraderKnownFactsParsingError(Seq(JsonValidationError(exception.getMessage))))
        case Success(value) => value.map(Some(_))
      }
      case NO_CONTENT =>
        Right(None)
      case _ =>
        logger.warn(s"[modelFromJsonHttpReads] Received unexpected status: ${response.status}")
        logger.debug(s"[modelFromJsonHttpReads] Error response body: ${response.body}")
        Left(UnexpectedDownstreamResponseError)
    }
  }
}
