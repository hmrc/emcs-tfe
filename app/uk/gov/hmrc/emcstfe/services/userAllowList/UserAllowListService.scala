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

package uk.gov.hmrc.emcstfe.services.userAllowList

import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.connectors.UserAllowListConnector
import uk.gov.hmrc.emcstfe.featureswitch.core.config.{EnablePrivateBeta, EnablePublicBeta, FeatureSwitching}
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.request.userAllowList.CheckUserAllowListRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UserAllowListService @Inject()(connector: UserAllowListConnector,
                                     val config: AppConfig)(implicit ec: ExecutionContext) extends FeatureSwitching {

  def isEligible(ern: String, service: String)(implicit hc: HeaderCarrier, request: UserRequest[_]): Future[Either[ErrorResponse, Boolean]] = {
    lazy val isPrivateBetaEnabled = isEnabled(EnablePrivateBeta)
    lazy val isPublicBetaEnabled = isEnabled(EnablePublicBeta)
    val userAllowListRequestModel = CheckUserAllowListRequest(ern)
    if(isPrivateBetaEnabled) {
      connector.check(service, userAllowListRequestModel)
    } else if(isPublicBetaEnabled) {
      for {
        privateBetaEligibilityResponse <- connector.check(service, userAllowListRequestModel)
        publicBetaEligibility = checkPublicBetaEligibility(service)
      } yield {
        privateBetaEligibilityResponse.fold(
          Left(_),
          privateBetaEligibility => Right(privateBetaEligibility || publicBetaEligibility)
        )
      }
    } else {
      Future(Right(false))
    }
  }

  private[services] def checkPublicBetaEligibility(service: String)(implicit request: UserRequest[_]): Boolean = {
    // Drop the last 2 chars from the ERN and take 2 from the result (which saves performing a modulus calculation)
    // It adds 1 to prevent returning 0 as a final result so the 'ernAsPercentile' is between 1-100
    config.publicBetaTrafficPercentageForService(service).exists { trafficPercentage =>
      request.allUserERNs.map { userErn =>
        val ernAsPercentile = userErn.dropRight(2).takeRight(2)
        ernAsPercentile.toInt + 1 <= trafficPercentage
      }.exists(identity)
    }
  }

}
