/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.connectors.httpParsers

import play.api.http.Status.OK
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{UnexpectedDownstreamResponseError, XmlValidationError}
import uk.gov.hmrc.emcstfe.utils.Logging
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

import scala.util.{Failure, Success, Try}
import scala.xml.{NodeSeq, XML}

class RawXMLHttpParser extends Logging {

  val rawXMLHttpReads: HttpReads[Either[ErrorResponse, NodeSeq]] = (_: String, _: String, response: HttpResponse) => {
    logger.debug(s"[RawXMLHttpParser][rawXMLHttpReads] ChRIS Response:\n\n  - Status: '${response.status}'\n\n - Body: '${response.body}'")
    response.status match {
      case OK => Try(XML.loadString(response.body)) match {
        case Failure(exception) =>
          logger.warn("Unable to read response body as XML", exception)
          Left(XmlValidationError)
        case Success(value) => Right(value)
      }
      case status =>
        logger.warn(s"Unexpected status from chris: $status")
        Left(UnexpectedDownstreamResponseError)
    }
  }

}
