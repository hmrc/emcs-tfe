/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.connectors

import com.lucidchart.open.xtract.XmlReader
import play.api.http.Status._
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.connectors.httpParsers.ChrisXMLHttpParser
import uk.gov.hmrc.emcstfe.models.request.ChrisRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse._
import uk.gov.hmrc.emcstfe.models.response.{ErrorResponse, HelloWorldResponse}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ChrisConnector @Inject()(val http: HttpClient,
                               val config: AppConfig,
                               chrisHttpParser: ChrisXMLHttpParser
                              ) extends BaseConnector {

  override def appConfig: AppConfig = config

  def hello()(implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, HelloWorldResponse]] = {
    val url: String = s"${config.chrisUrl}/hello-world"

    //TODO: Temporary, this entire method will be removed but this is needed for now to prevent thrown exceptions from HttpClient library
    implicit val rdsHttpResponse: HttpReads[HttpResponse] = (_: String, _: String, response: HttpResponse) => response

    http.GET[HttpResponse](url).map {
      response =>
        response.status match {
          case OK => response.validateJson[HelloWorldResponse] match {
            case Some(valid) => Right(valid)
            case None =>
              logger.warn(s"Bad JSON response from emcs-tfe-chris-stub")
              Left(JsonValidationError)
          }
          case status =>
            logger.warn(s"Unexpected status from emcs-tfe-chris-stub: $status")
            Left(UnexpectedDownstreamResponseError)
        }
    }
  }

  def postChrisSOAPRequest[A](request: ChrisRequest)
                             (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, xmlRds: XmlReader[A]): Future[Either[ErrorResponse, A]] = {
    val url: String = s"${config.chrisUrl}/ChRISOSB/EMCS/EMCSApplicationService/2"
    postString(http, url, request.requestBody, request.action)(ec, headerCarrier, chrisHttpParser.rawXMLHttpReads)
  }
}
