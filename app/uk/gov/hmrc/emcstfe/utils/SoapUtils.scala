/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.utils

import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.SoapExtractionError

import scala.util.{Failure, Success, Try}
import scala.xml.{NodeSeq, XML}

object SoapUtils extends Logging {
  def extractFromSoap(xml: NodeSeq): Try[NodeSeq] = Try {
    val cdata = (xml \\ "OperationResponse" \\ "Results" \\ "Result").text
    XML.loadString(cdata)
  }

  def parseResponseXMLAsEitherT[T](asT: NodeSeq => T): Either[ErrorResponse, NodeSeq] => Either[ErrorResponse, T] =
    _.flatMap { xml =>
      SoapUtils.extractFromSoap(xml) match {
        case Failure(exception) =>
          logger.warn("Error extracting response body from SOAP wrapper", exception)
          (xml \\ "Errors" \\ "Error").foreach(error => logger.warn(error.text))
          Left(SoapExtractionError)
        case Success(value) => Right(asT(value))
      }
    }

}
