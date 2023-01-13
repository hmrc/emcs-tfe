/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.connector

import play.api.libs.json.{JsError, JsSuccess, JsValue, Reads}
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.utils.Logging
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Try}

trait BaseConnector extends Logging {

  def appConfig: AppConfig

  implicit val httpReads: HttpReads[HttpResponse] = (method: String, url: String, response: HttpResponse) => response

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
    ("Accept", "application/soap+xml"),
    ("Content-Type", s"""application/soap+xml; charset=UTF-8; action="$action"""")
  )

  private def chrisHeaderCarrier(extraHeaders: Seq[(String, String)])(implicit hc: HeaderCarrier): HeaderCarrier = {
    hc.copy(extraHeaders = hc.extraHeaders ++ hc.headers(appConfig.chrisHeaders) ++ extraHeaders)
  }

  def postString[A, B](http: HttpClient, uri: String, body: String, action: String)(resFn: HttpResponse => Either[A, B])(implicit ec: ExecutionContext,
                                                                                                                         hc: HeaderCarrier): Future[Either[A, B]] = {

    def doPostString(implicit hc: HeaderCarrier): Future[Either[A, B]] =
      http.POSTString[HttpResponse](uri, body, chrisHeaders(action)).map(resFn)

    doPostString(chrisHeaderCarrier(chrisHeaders(action)))
  }

}
