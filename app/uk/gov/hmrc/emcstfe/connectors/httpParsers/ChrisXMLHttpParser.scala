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

import com.lucidchart.open.xtract._
import play.api.http.Status.OK
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{UnexpectedDownstreamResponseError, XmlValidationError}
import uk.gov.hmrc.emcstfe.utils.XmlResultParser.handleParseResult
import uk.gov.hmrc.emcstfe.utils.{Logging, SoapUtils}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

import javax.inject.{Inject, Singleton}
import scala.util.{Failure, Success, Try}
import scala.xml.XML

@Singleton
class ChrisXMLHttpParser @Inject()(soapUtils: SoapUtils) extends Logging {

  def rawXMLHttpReads[A](shouldExtractFromSoap: Boolean)(implicit xmlReads: XmlReader[A]): HttpReads[Either[ErrorResponse, A]] = (_: String, _: String, response: HttpResponse) => {
    logger.debug(s"[rawXMLHttpReads] ChRIS Response:\n\n  - Status: '${response.status}'\n\n - Body: '${response.body}'")
    response.status match {
      case OK =>
        Try(XML.loadString(response.body)) match {
          case Failure(exception) =>
            logger.warn("[rawXMLHttpReads] Unable to read response body as XML", exception)
            Left(XmlValidationError)
          case Success(xml) =>
            if(shouldExtractFromSoap) {
              soapUtils.extractFromSoap(xml) flatMap { xmlBody =>
                handleParseResult(XmlReader.of[A].read(xmlBody))
              }
            } else handleParseResult(XmlReader.of[A].read(xml))
        }
      case status =>
        logger.warn(s"[rawXMLHttpReads] Unexpected status from chris: $status")
        Left(UnexpectedDownstreamResponseError)
    }
  }

}
