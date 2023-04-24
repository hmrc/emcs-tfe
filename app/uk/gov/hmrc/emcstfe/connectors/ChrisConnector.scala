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

package uk.gov.hmrc.emcstfe.connectors

import com.lucidchart.open.xtract.XmlReader
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.connectors.httpParsers.ChrisXMLHttpParser
import uk.gov.hmrc.emcstfe.models.request.ChrisRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.utils.XmlUtils
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.xml.{NodeSeq, XML}

@Singleton
class ChrisConnector @Inject()(val http: HttpClient,
                               override val appConfig: AppConfig,
                               chrisHttpParser: ChrisXMLHttpParser,
                               soapUtils: XmlUtils
                              ) extends BaseConnector {

  def postChrisSOAPRequestAndExtractToModel[A](request: ChrisRequest)
                             (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, xmlRds: XmlReader[A]): Future[Either[ErrorResponse, A]] = {
    val url: String = s"${appConfig.chrisUrl}/ChRISOSB/EMCS/EMCSApplicationService/2"
    postString(http, url, request.requestBody, request.action)(ec, headerCarrier, chrisHttpParser.modelFromXmlHttpReads(shouldExtractFromSoap = request.shouldExtractFromSoap))
  }

  def postChrisSOAPRequest(request: ChrisRequest)
                             (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, NodeSeq]] = {
    val url: String = s"${appConfig.chrisUrl}/ChRISOSB/EMCS/EMCSApplicationService/2"
    postString(http, url, request.requestBody, request.action)(ec, headerCarrier, chrisHttpParser.rawXMLHttpReads(shouldExtractFromSoap = request.shouldExtractFromSoap))
  }

  def submitDraftMovementChrisSOAPRequest[A](request: ChrisRequest)
                               (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, xmlRds: XmlReader[A]): Future[Either[ErrorResponse, A]] = {
    val url: String = s"${appConfig.chrisUrl}/ChRIS/EMCS/SubmitDraftMovementPortal/3"
    soapUtils.prepareXmlForSubmission(XML.loadString(request.requestBody)) match {
      case Left(errorResponse) => Future.successful(Left(errorResponse))
      case Right(preparedXml) =>

        logger.debug(s"[submitDraftMovementChrisSOAPRequest] Sending to URL: $url")
        logger.debug(s"[submitDraftMovementChrisSOAPRequest] Sending body: $preparedXml")

        postString(http, url, preparedXml, request.action)(ec, headerCarrier, chrisHttpParser.modelFromXmlHttpReads(shouldExtractFromSoap = false))
    }
  }

  def submitReportOfReceiptChrisSOAPRequest[A](request: ChrisRequest)
                                              (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, xmlRds: XmlReader[A]): Future[Either[ErrorResponse, A]] = {
    val url: String = s"${appConfig.chrisUrl}/ChRIS/EMCS/SubmitReportofReceiptPortal/4"
    soapUtils.prepareXmlForSubmission(XML.loadString(request.requestBody)) match {
      case Left(errorResponse) => Future.successful(Left(errorResponse))
      case Right(preparedXml) =>

        logger.debug(s"[submitReportOfReceiptChrisSOAPRequest] Sending to URL: $url")
        logger.debug(s"[submitReportOfReceiptChrisSOAPRequest] Sending body: $preparedXml")

        postString(http, url, preparedXml, request.action)(ec, headerCarrier, chrisHttpParser.modelFromXmlHttpReads(request.shouldExtractFromSoap))
    }
  }
}
