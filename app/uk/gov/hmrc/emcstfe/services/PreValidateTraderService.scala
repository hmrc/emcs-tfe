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
import uk.gov.hmrc.emcstfe.featureswitch.core.config.{EnablePreValidateViaETDS12, FeatureSwitching}
import uk.gov.hmrc.emcstfe.models.preValidate.PreValidateTraderModel
import uk.gov.hmrc.emcstfe.models.request.eis.preValidate._
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.models.response.prevalidate._
import uk.gov.hmrc.emcstfe.utils.Logging
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PreValidateTraderService @Inject() (eisConnector: EisConnector, val config: AppConfig) extends Logging with FeatureSwitching {

  def preValidateTrader(preValidateTraderRequest: PreValidateTraderModel)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, PreValidateTraderETDSResponse]] = {

    if (isEnabled(EnablePreValidateViaETDS12)) {
      val request = PreValidateETDS12Request(
        preValidateTraderRequest.ern,
        preValidateTraderRequest.entityGroup,
        preValidateTraderRequest.productCodes.map(_.map(Product.apply))
      )

      eisConnector.preValidateTraderViaETDS12(request)
    } else {
      val request = PreValidateRequest(
        exciseTraderValidationRequest = ExciseTraderValidationRequest(
          ExciseTraderRequest(
            preValidateTraderRequest.ern,
            preValidateTraderRequest.entityGroup.getOrElse("UK Record"),
            preValidateTraderRequest.productCodes.map(_.map(productCode => ValidateProductAuthorisationRequest(Product(productCode)))).getOrElse(Seq.empty)
          )
        )
      )

      eisConnector.preValidateTrader(request).map {
        case Left(error)     => Left(error)
        case Right(response) => Right(convertTo(response))
      }
    }
  }

  def convertTo(response: PreValidateTraderApiResponse): PreValidateTraderETDSResponse = {
    val exciseTraderResponse = response.exciseTraderValidationResponse.exciseTraderResponse.head

    val containsProductErrors = exciseTraderResponse.validateProductAuthorisationResponse.exists(_.productError.exists(_.nonEmpty))

    PreValidateTraderETDSResponse(
      processingDateTime = response.exciseTraderValidationResponse.validationTimestamp,
      exciseId = exciseTraderResponse.exciseRegistrationNumber,
      validationResult = if (exciseTraderResponse.validTrader && !containsProductErrors) "Pass" else "Fail",
      failDetails = exciseTraderResponse.validateProductAuthorisationResponse.flatMap(_.productError).map { productError =>
        ETDSFailDetails(
          validTrader = exciseTraderResponse.validTrader,
          errorCode = exciseTraderResponse.errorCode.map(_.toInt),
          errorText = exciseTraderResponse.errorText,
          validateProductAuthorisationResponse = Some(
            ETDSValidateProductAuthorisationResponse(
              productError = Some(productError.map { error =>
                ETDSProductError(
                  exciseProductCode = error.exciseProductCode,
                  errorCode = error.errorCode.toInt,
                  errorText = error.errorText
                )
              })
            ))
        )
      }
    )

  }

}
