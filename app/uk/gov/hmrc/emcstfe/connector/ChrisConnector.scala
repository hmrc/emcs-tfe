/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.connector

import play.api.http.Status._
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.models.request.GetMessageRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse._
import uk.gov.hmrc.emcstfe.models.response.{ErrorResponse, HelloWorldResponse}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}
import scala.xml.{NodeSeq, XML}

@Singleton
class ChrisConnector @Inject()(val http: HttpClient,
                               val config: AppConfig
                                  ) extends BaseConnector {

  override def appConfig: AppConfig = config

  def hello()(implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, HelloWorldResponse]] = {
    val url: String = s"${config.chrisUrl}/hello-world"

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

  def getMessage(request: GetMessageRequest)(implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, NodeSeq]] = {
    val url: String = s"${config.chrisUrl}/ChRISOSB/EMCS/EMCSApplicationService/2"

    postString(http, url, request.requestBody, request.action) {
        response =>
          response.status match {
            case OK => Try(XML.loadString(response.body)) match {
              case Failure(exception) =>
                logger.warn("Unable to read response body as XML", exception)
                Left(XmlValidationError)
              case Success(value) => Right(value)
            }
            case status =>
              logger.warn(s"Unexpected status from emcs-tfe-chris-stub: $status")
              Left(UnexpectedDownstreamResponseError)
          }
      }
  }

}
