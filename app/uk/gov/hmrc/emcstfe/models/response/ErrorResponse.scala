/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.models.response

import play.api.libs.json.{Json, OWrites}

trait ErrorResponse {
  val message: String
}

object ErrorResponse {

  implicit val writes: OWrites[ErrorResponse] = (o: ErrorResponse) => Json.obj("message" -> o.message)

  implicit def genericWrites[T <: ErrorResponse]: OWrites[T] =
    writes.contramap[T](c => c: ErrorResponse)

  object UnexpectedDownstreamResponseError extends ErrorResponse {
    val message = "Unexpected downstream response status"
  }

  object JsonValidationError extends ErrorResponse {
    val message = "JSON validation error"
  }

  object XmlValidationError extends ErrorResponse {
    val message = "XML validation error"
  }

  object SoapExtractionError extends ErrorResponse {
    val message = "Error extracting response body from SOAP wrapper"
  }

}
