/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.connectors

import play.api.http.HeaderNames
import play.api.libs.json.{JsError, JsSuccess, JsValue, Reads}
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.utils.Logging
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Try}

trait BaseConnector extends Logging {

  def appConfig: AppConfig

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
    HeaderNames.CONTENT_TYPE -> s"""application/soap+xml; charset=UTF-8; action="$action""""
  )

  def postString[A, B](http: HttpClient, uri: String, body: String, action: String)
                      (implicit ec: ExecutionContext, hc: HeaderCarrier, rds: HttpReads[Either[A,B]]): Future[Either[A, B]] = {

    val headerCarrier = hc.copy(extraHeaders = hc.extraHeaders ++ hc.headers(appConfig.chrisHeaders))

    http.POSTString[Either[A,B]](uri, body, chrisHeaders(action))(rds, headerCarrier, ec)
  }
}
