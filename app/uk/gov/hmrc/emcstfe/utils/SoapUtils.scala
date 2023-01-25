/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.utils

import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.SoapExtractionError

import scala.util.{Failure, Right, Success, Try}
import scala.xml.{Elem, NodeSeq, XML}

object SoapUtils extends Logging {
  def extractFromSoap(xml: NodeSeq): Either[ErrorResponse, NodeSeq] = Try {
    val cdata = (xml \\ "OperationResponse" \\ "Results" \\ "Result").text
    XML.loadString(cdata)
  } match {
    case Failure(exception) =>
      logger.warn("Error extracting response body from SOAP wrapper", exception)
      (xml \\ "Errors" \\ "Error").foreach(error => logger.warn(error.text))
      Left(SoapExtractionError)
    case Success(value) => Right(value)
  }
}
