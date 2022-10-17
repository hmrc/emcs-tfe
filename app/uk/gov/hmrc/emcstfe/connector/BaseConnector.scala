/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.connector

import play.api.Logger
import play.api.libs.json.{JsError, JsSuccess, JsValue, Reads}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

import scala.util.{Success, Try}

trait BaseConnector {

  lazy val logger: Logger = Logger(this.getClass)

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

}
