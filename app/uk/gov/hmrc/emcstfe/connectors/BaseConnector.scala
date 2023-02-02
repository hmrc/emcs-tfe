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

import play.api.http.HeaderNames
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.utils.Logging
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads}

import scala.concurrent.{ExecutionContext, Future}

trait BaseConnector extends Logging {

  def appConfig: AppConfig

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
