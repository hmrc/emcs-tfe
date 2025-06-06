/*
 * Copyright 2024 HM Revenue & Customs
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

import com.google.inject.Inject
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.connectors.httpParsers.TraderKnownFactsHttpParser
import uk.gov.hmrc.emcstfe.models.response.{ErrorResponse, TraderKnownFacts}
import uk.gov.hmrc.emcstfe.utils.Logging
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import scala.concurrent.{ExecutionContext, Future}

class TraderKnownFactsConnector @Inject() (val http: HttpClientV2, appConfig: AppConfig) extends TraderKnownFactsHttpParser with Logging {

  def getTraderKnownFactsViaReferenceData(ern: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, Option[TraderKnownFacts]]] = {
    http
      .get(url"${appConfig.knownFactsCandEUrl(ern)}")
      .execute[Either[ErrorResponse, Option[TraderKnownFacts]]](modelFromJsonHttpReads, ec)
  }

}
