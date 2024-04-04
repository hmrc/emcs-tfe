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

import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.connectors.httpParsers.NRSBrokerHttpParser
import uk.gov.hmrc.emcstfe.models.nrs.NRSPayload
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.UnexpectedDownstreamResponseError
import uk.gov.hmrc.emcstfe.models.response.nrsBroker.NRSBrokerInsertPayloadResponse
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class NRSBrokerConnector @Inject()(http: HttpClient, config: AppConfig) extends NRSBrokerHttpParser {

  def submitPayload(nrsPayload: NRSPayload, ern: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, NRSBrokerInsertPayloadResponse]] = {
    http.PUT[NRSPayload, Either[ErrorResponse, NRSBrokerInsertPayloadResponse]](
      url = config.nrsBrokerBaseUrl() + s"/trader/$ern/nrs/submission",
      body = nrsPayload
    )
  }.recover {
    error =>
      logger.warn(s"[check] Unexpected error from NRS broker: ${error.getClass} ${error.getMessage.take(10000)}")
      Left(UnexpectedDownstreamResponseError)
  }
}
