/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.models.response

import play.api.libs.json.{Json, OWrites}

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

  case object JsonValidationError extends ErrorResponse {
    val message = "JSON validation error"
  }

  case object XmlValidationError extends ErrorResponse {
    val message = "XML validation error"
  }

  case object SoapExtractionError extends ErrorResponse {
    val message = "Error extracting response body from SOAP wrapper"
  }

}