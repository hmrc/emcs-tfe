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
import uk.gov.hmrc.emcstfe.connectors.httpParsers.UserAllowListHttpParser
import uk.gov.hmrc.emcstfe.models.request.userAllowList.CheckUserAllowListRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.UnexpectedDownstreamResponseError
import uk.gov.hmrc.http.{Authorization, HeaderCarrier, HttpClient}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UserAllowListConnector @Inject()(http: HttpClient, config: AppConfig) extends UserAllowListHttpParser {

  def check(service: String, checkRequest: CheckUserAllowListRequest)
           (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, Boolean]] = {
    val headerCarrierWithInternalAuthToken = hc.copy(authorization = Some(Authorization(config.internalAuthToken())))

    http.POST[CheckUserAllowListRequest, Either[ErrorResponse, Boolean]](
      url = config.userAllowListBaseUrl() + s"/emcs-tfe/$service/check",
      body = checkRequest
    )(CheckUserAllowListRequest.writes, UserAllowListReads, headerCarrierWithInternalAuthToken, ec)
  }.recover {
    error =>
      logger.warn(s"[check] Unexpected error from user-allow-list: ${error.getClass} ${error.getMessage}")
      Left(UnexpectedDownstreamResponseError)
  }

}