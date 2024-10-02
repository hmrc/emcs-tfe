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

package uk.gov.hmrc.emcstfe.services

import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.connectors.{EisConnector, TraderKnownFactsConnector}
import uk.gov.hmrc.emcstfe.featureswitch.core.config.{EnableKnownFactsViaETDS18, FeatureSwitching}
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.response.{ErrorResponse, TraderKnownFacts}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TraderKnownFactsService @Inject() (
    traderKnownFactsConnector: TraderKnownFactsConnector,
    eisConnector: EisConnector,
    val config: AppConfig
) extends FeatureSwitching {

  def getTraderKnownFacts(ern: String)(implicit request: UserRequest[_], hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, Option[TraderKnownFacts]]] = {
    if (isEnabled(EnableKnownFactsViaETDS18)) {
      eisConnector.getTraderKnownFactsViaETDS18(ern)
    } else {
      traderKnownFactsConnector.getTraderKnownFactsViaReferenceData(ern)
    }
  }

}