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
import play.api.libs.json.{JsonValidationError, Reads}
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse._
import uk.gov.hmrc.emcstfe.models.response.rimValidation.EISRIMValidationErrorResponse
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

import scala.util.{Failure, Success, Try}

trait EisJsonHttpParser {

  val logger: LoggerLike

  def modelFromJsonHttpReads[A](implicit jsonReads: Reads[A]): HttpReads[Either[ErrorResponse, A]] = (_: String, _: String, response: HttpResponse) => {
    response.status match {
      case OK => Try {
        jsonReads.reads(response.json).fold(
          errors => Left(EISJsonParsingError(errors.flatMap(_._2).toSeq)),
          Right(_)
        )
      } match {
        case Failure(exception) =>
          logger.error(exception.getMessage, exception)
          Left(EISJsonParsingError(Seq(JsonValidationError(exception.getMessage))))
        case Success(value) => value
      }
      case BAD_REQUEST => Left(EISJsonSchemaMismatchError(response.body))
      case NOT_FOUND => Left(EISResourceNotFoundError(response.body))
      case UNPROCESSABLE_ENTITY =>
        logger.debug(s"[modelFromJsonHttpReads] Business/RIM validation error (422) from EIS: ${response.body}")
        Try {
          EISRIMValidationErrorResponse.format.reads(response.json).fold(_ => None, Some(_))
        } match {
          case Failure(exception) =>
            logger.error(exception.getMessage, exception)
            Left(EISJsonParsingError(Seq(JsonValidationError(exception.getMessage))))
          case Success(Some(rimErrorResponse)) => Left(EISRIMValidationError(rimErrorResponse))
          case Success(_) => Left(EISBusinessError(response.body))
        }
      case INTERNAL_SERVER_ERROR => Left(EISInternalServerError(response.body))
      case SERVICE_UNAVAILABLE => Left(EISServiceUnavailableError(response.body))
      case _ =>
        logger.warn(s"[modelFromJsonHttpReads] Received unexpected status: ${response.status}")
        logger.debug(s"[modelFromJsonHttpReads] Error response body: ${response.body}")
        Left(EISUnknownError(response.body))
    }
  }

}
