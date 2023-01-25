/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.connectors.httpParsers

import com.lucidchart.open.xtract.{ParseFailure, ParseSuccess, XmlReader}
import play.api.http.Status.OK
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{UnexpectedDownstreamResponseError, XmlParseError, XmlValidationError}
import uk.gov.hmrc.emcstfe.utils.{Logging, SoapUtils}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

import scala.util.{Failure, Success, Try}
import scala.xml.XML

class ChrisXMLHttpParser extends Logging {

  def rawXMLHttpReads[A](implicit xmlReads: XmlReader[A]): HttpReads[Either[ErrorResponse, A]] = (_: String, _: String, response: HttpResponse) => {
    logger.debug(s"[RawXMLHttpParser][rawXMLHttpReads] ChRIS Response:\n\n  - Status: '${response.status}'\n\n - Body: '${response.body}'")
    response.status match {
      case OK =>
        Try(XML.loadString(response.body)) match {
          case Failure(exception) =>
            logger.warn("Unable to read response body as XML", exception)
            Left(XmlValidationError)
          case Success(xml) =>
            SoapUtils.extractFromSoap(xml) flatMap { xmlBody =>
              XmlReader.of[A].read(xmlBody) match {
                case ParseSuccess(model) => Right(model)
                case ParseFailure(errors) =>
                  logger.warn(s"[ChrisXMLHttpParser][rawXMLHttpReads] XML Response from ChRIS could not be parsed to model. Errors: \n\n - ${errors.mkString("\n - ")}")
                  Left(XmlParseError(errors))
              }
            }
        }
      case status =>
        logger.warn(s"Unexpected status from chris: $status")
        Left(UnexpectedDownstreamResponseError)
    }
  }

}
