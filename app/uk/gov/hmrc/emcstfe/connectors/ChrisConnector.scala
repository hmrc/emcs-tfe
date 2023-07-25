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
import uk.gov.hmrc.emcstfe.services.MetricsService
import uk.gov.hmrc.emcstfe.utils.XmlUtils
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.xml.{NodeSeq, XML}

@Singleton
class ChrisConnector @Inject()(val http: HttpClient,
                               override val appConfig: AppConfig,
                               override val metricsService: MetricsService,
                               chrisHttpParser: ChrisXMLHttpParser,
                               soapUtils: XmlUtils
                              ) extends BaseChrisConnector {

  def postChrisSOAPRequestAndExtractToModel[A](request: ChrisRequest)
                                              (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, xmlRds: XmlReader[A]): Future[Either[ErrorResponse, A]] =
    postString(http, appConfig.urlEMCSApplicationService(), request.requestBody, request)(ec, headerCarrier, chrisHttpParser.modelFromXmlHttpReads(request.shouldExtractFromSoap))

  def postChrisSOAPRequest(request: ChrisRequest)
                          (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, NodeSeq]] =
      postString(http, appConfig.urlEMCSApplicationService(), request.requestBody, request)(ec, headerCarrier, chrisHttpParser.rawXMLHttpReads(request.shouldExtractFromSoap))

  private def prepareXMLAndSubmit[A](url: String, request: ChrisRequest, callingMethod: String)
                                  (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, xmlRds: XmlReader[A]): Future[Either[ErrorResponse, A]] =
    soapUtils.prepareXmlForSubmission(XML.loadString(request.requestBody)) match {
      case Left(errorResponse) => Future.successful(Left(errorResponse))
      case Right(preparedXml) =>

        logger.debug(s"[$callingMethod] Sending to URL: $url")
        logger.debug(s"[$callingMethod] Sending body: $preparedXml")

        postString(http, url, preparedXml, request)(ec, headerCarrier, chrisHttpParser.modelFromXmlHttpReads(request.shouldExtractFromSoap))
    }

  def submitDraftMovementChrisSOAPRequest[A](request: ChrisRequest)
                                            (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, xmlRds: XmlReader[A]): Future[Either[ErrorResponse, A]] =
    prepareXMLAndSubmit(appConfig.urlSubmitDraftMovementPortal(), request, "submitDraftMovementChrisSOAPRequest")

  def submitReportOfReceiptChrisSOAPRequest[A](request: ChrisRequest)
                                              (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, xmlRds: XmlReader[A]): Future[Either[ErrorResponse, A]] =
    prepareXMLAndSubmit(appConfig.urlSubmitReportofReceiptPortal(), request, "submitReportOfReceiptChrisSOAPRequest")

  def submitExplainDelayChrisSOAPRequest[A](request: ChrisRequest)
                                           (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, xmlRds: XmlReader[A]): Future[Either[ErrorResponse, A]] =
    prepareXMLAndSubmit(appConfig.urlSubmitExplainDelay(), request, "submitExplainDelayChrisSOAPRequest")

  def submitChangeDestinationChrisSOAPRequest[A](request: ChrisRequest)
                                                (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, xmlRds: XmlReader[A]): Future[Either[ErrorResponse, A]] =
    prepareXMLAndSubmit(appConfig.urlSubmitChangeDestination(), request, "submitChangeDestinationChrisSOAPRequest")

  def submitExplainShortageExcessChrisSOAPRequest[A](request: ChrisRequest)
                                                (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, xmlRds: XmlReader[A]): Future[Either[ErrorResponse, A]] =
    prepareXMLAndSubmit(appConfig.urlSubmitExplainShortageExcess(), request, "submitExplainShortageExcessChrisSOAPRequest")

  def submitAlertOrRejectionChrisSOAPRequest[A](request: ChrisRequest)
                                           (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, xmlRds: XmlReader[A]): Future[Either[ErrorResponse, A]] =
    prepareXMLAndSubmit(appConfig.urlSubmitAlertOrRejection(), request, "submitAlertOrRejectionChrisSOAPRequest")
}
