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
import uk.gov.hmrc.emcstfe.models.response.rimValidation.EISRIMValidationErrorResponse

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

  case class GenericParseError(message: String) extends ParseError

  case class XmlParseError(errors: Seq[ParseError]) extends ErrorResponse {
    val message = s"XML failed to parse, with the following errors:\n - ${errors.mkString("\n - ")}"
  }

  case class MongoError(msg: String) extends ErrorResponse {
    val message = s"Error from Mongo with message: $msg"
  }

  case class TraderKnownFactsParsingError(errors: Seq[JsonValidationError]) extends ErrorResponse {
    val message = s"Errors parsing JSON, errors: $errors"
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

  case class EISRIMValidationError(errorResponse: EISRIMValidationErrorResponse) extends ErrorResponse {
    val message = s"Request not processed returned by EIS, correlation ID: ${errorResponse.emcsCorrelationId}"
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

  case class TemplateDoesNotExist(templateId: String) extends ErrorResponse {
    val message = s"No template exists with the templateId: $templateId"
  }

  case object TooManyTemplates extends ErrorResponse {
    val message = "Too many templates exist for this ERN"
  }
}
