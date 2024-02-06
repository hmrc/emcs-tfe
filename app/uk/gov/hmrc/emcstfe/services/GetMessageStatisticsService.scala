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

package uk.gov.hmrc.emcstfe.services

import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.connectors.{ChrisConnector, EisConnector}
import uk.gov.hmrc.emcstfe.featureswitch.core.config.{FeatureSwitching, SendToEIS}
import uk.gov.hmrc.emcstfe.models.request.GetMessageStatisticsRequest
import uk.gov.hmrc.emcstfe.models.response.{ErrorResponse, GetMessageStatisticsResponse}
import uk.gov.hmrc.emcstfe.utils.Logging
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetMessageStatisticsService @Inject()(eisConnector: EisConnector,
                                            chrisConnector: ChrisConnector,
                                            override val config: AppConfig) extends Logging with FeatureSwitching {
  def getMessageStatistics(getMessageStatisticsRequest: GetMessageStatisticsRequest)
                          (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, GetMessageStatisticsResponse]] =
    if (isEnabled(SendToEIS)) {
      eisConnector.getMessageStatistics(getMessageStatisticsRequest)
    } else {
      chrisConnector.postChrisSOAPRequestAndExtractToModel[GetMessageStatisticsResponse](getMessageStatisticsRequest)
    }

}
