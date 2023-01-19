/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.connector

import play.api.http.HeaderNames
import play.api.http.Status.OK
import play.api.libs.json.{JsError, JsSuccess, JsValue, Reads}
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{UnexpectedDownstreamResponseError, XmlValidationError}
import uk.gov.hmrc.emcstfe.utils.Logging
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}
import scala.xml.{NodeSeq, XML}

trait BaseConnector extends Logging {

  def appConfig: AppConfig

  implicit val httpResponseReads: HttpReads[HttpResponse] = (_: String, _: String, response: HttpResponse) => response

  implicit val chrisReads: HttpReads[Either[ErrorResponse, NodeSeq]] = (_: String, _: String, response: HttpResponse) => {
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

  implicit class KnownJsonResponse(response: HttpResponse) {

    def validateJson[T](implicit reads: Reads[T]): Option[T] = {
      Try(response.json) match {
        case Success(json: JsValue) => parseResult(json)
        case _ =>
          logger.warn("[KnownJsonResponse][validateJson] No JSON was returned")
          None
      }
    }

    private def parseResult[T](json: JsValue)(implicit reads: Reads[T]): Option[T] = json.validate[T] match {

      case JsSuccess(value, _) => Some(value)
      case JsError(error) =>
        logger.warn(s"[KnownJsonResponse][validateJson] Unable to parse JSON: $error")
        None
    }
  }

  private def chrisHeaders(action: String): Seq[(String, String)] = Seq(
    HeaderNames.ACCEPT -> "application/soap+xml",
    HeaderNames.CONTENT_TYPE -> s"""application/soap+xml; charset=UTF-8; action="$action"""
  )

  def postString(http: HttpClient, uri: String, body: String, action: String)(implicit ec: ExecutionContext,
                                                                              hc: HeaderCarrier): Future[Either[ErrorResponse, NodeSeq]] = {

    val headerCarrier = hc.copy(extraHeaders = hc.extraHeaders ++ hc.headers(appConfig.chrisHeaders))

    doPostString(chrisHeaderCarrier(Seq()))
  }

}
