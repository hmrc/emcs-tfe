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

package uk.gov.hmrc.emcstfe.models.response

import com.lucidchart.open.xtract.ParseError
import play.api.libs.json._

sealed trait ErrorResponse {
  val message: String
}

object ErrorResponse {

  implicit val writes: OWrites[ErrorResponse] = (o: ErrorResponse) => Json.obj("message" -> o.message)

  implicit def genericWrites[T <: ErrorResponse]: OWrites[T] =
    writes.contramap[T](c => c: ErrorResponse)

  case object UnexpectedDownstreamResponseError extends ErrorResponse {
    val message = "Unexpected downstream response status"
  }

  case object XmlValidationError extends ErrorResponse {
    val message = "XML validation error"
  }

  case class GenericParseError(message: String) extends ParseError

  case class XmlParseError(errors: Seq[ParseError]) extends ErrorResponse {
    val message = s"XML failed to parse, with the following errors:\n - ${errors.mkString("\n - ")}"
  }

  case object SoapExtractionError extends ErrorResponse {
    val message = "Error extracting response body from SOAP wrapper"
  }

  case object MarkCreationError extends ErrorResponse {
    val message = "Error creating an HMRC Mark with the supplied XML"
  }

  case object MarkPlacementError extends ErrorResponse {
    val message = "Error placing an HMRC Mark into the supplied XML"
  }

  case class MongoError(msg: String) extends ErrorResponse {
    val message = s"Error from Mongo with message: $msg"
  }

  case class EISJsonParsingError(errors: Seq[JsonValidationError]) extends ErrorResponse {
    val message = s"Errors parsing JSON, errors: $errors"
  }

  case class EISJsonSchemaMismatchError(errorResponse: String) extends ErrorResponse {
    val message = s"Invalid JSON sent from EIS, error response: $errorResponse"
  }

  case class EISResourceNotFoundError(errorResponse: String) extends ErrorResponse {
    val message = s"Not found returned from EIS, error response: $errorResponse"
  }

  case class EISBusinessError(errorResponse: String) extends ErrorResponse {
    val message = s"Request not processed returned by EIS, error response: $errorResponse"
  }

  case class EISInternalServerError(errorResponse: String) extends ErrorResponse {
    val message = s"Request not processed returned by EIS, error response: $errorResponse"
  }

  case class EISServiceUnavailableError(errorResponse: String) extends ErrorResponse {
    val message = s"Service unavailable returned by EIS, error response: $errorResponse"
  }

  case class EISUnknownError(errorResponse: String) extends ErrorResponse {
    val message = s"An unknown response was returned by EIS, error response: $errorResponse"
  }

  case class QueryParameterError(queryParams: Seq[(String, String)]) extends ErrorResponse {
    val message = s"Invalid query parameters provided. Query parameters: $queryParams"
  }

  case class InvalidLegacyRequestProvided(message: String) extends ErrorResponse

  case class InvalidLegacyActionProvided(action: String) extends ErrorResponse {
    val message = s"Unknown action requested for legacy: $action"
  }

  case object NoLegacyActionProvided extends ErrorResponse {
    val message = s"no action found in the request"
  }

  case class NRSBrokerJsonParsingError(errors: Seq[JsonValidationError]) extends ErrorResponse {
    val message = s"Errors parsing JSON, errors: $errors"
  }

  case class IdentityDataException(message: String) extends ErrorResponse
}
