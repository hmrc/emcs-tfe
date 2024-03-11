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
import uk.gov.hmrc.emcstfe.connectors.EisConnector
import uk.gov.hmrc.emcstfe.models.preValidate.PreValidateTraderModel
import uk.gov.hmrc.emcstfe.models.request.eis.preValidate._
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.models.response.prevalidate.PreValidateTraderApiResponse
import uk.gov.hmrc.emcstfe.utils.Logging
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PreValidateTraderService @Inject()(eisConnector: EisConnector, val config: AppConfig) extends Logging {

  def preValidateTrader(preValidateTraderRequest: PreValidateTraderModel)
                       (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, PreValidateTraderApiResponse]] = {

    val request = PreValidateRequest(
      exciseTraderValidationRequest = ExciseTraderValidationRequest(
        exciseTraderRequest = ExciseTraderRequest(
          exciseRegistrationNumber = preValidateTraderRequest.ern,
          entityGroup = "UK Trader", // Legacy EMCS only has this one option due to Brexit ?
          validateProductAuthorisationRequest = preValidateTraderRequest.productCodes.map(code => ValidateProductAuthorisationRequest(Product(code)))
        )
      )
    )

    eisConnector.preValidateTrader(request)
  }

}
