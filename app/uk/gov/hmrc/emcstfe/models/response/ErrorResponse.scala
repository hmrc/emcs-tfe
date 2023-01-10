/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.models.response

trait ErrorResponse {
  val message: String
}

object ErrorResponse {

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
