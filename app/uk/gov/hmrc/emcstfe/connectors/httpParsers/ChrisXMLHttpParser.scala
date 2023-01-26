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

import com.lucidchart.open.xtract.{ParseFailure, ParseResult, ParseSuccess, PartialParseSuccess, XmlReader}
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
              handleParseResult(XmlReader.of[A].read(xmlBody))
            }
        }
      case status =>
        logger.warn(s"Unexpected status from chris: $status")
        Left(UnexpectedDownstreamResponseError)
    }
  }

  private[httpParsers] def handleParseResult[A]: ParseResult[A] => Either[ErrorResponse, A] = {
    case ParseSuccess(model) => Right(model)
    case ParseFailure(errors) =>
      logger.warn(s"[ChrisXMLHttpParser][rawXMLHttpReads] XML Response from ChRIS could not be parsed to model. Errors: \n\n - ${errors.mkString("\n - ")}")
      Left(XmlParseError(errors))
    case PartialParseSuccess(_, errors) =>
      logger.warn(s"[ChrisXMLHttpParser][rawXMLHttpReads] PartialParseSuccess - XML Response from ChRIS could not be fully parsed to model. Errors: \n\n - ${errors.mkString("\n - ")}")
      Left(XmlParseError(errors))
  }

}
