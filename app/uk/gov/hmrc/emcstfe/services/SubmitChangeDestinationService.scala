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
import uk.gov.hmrc.emcstfe.connectors.EisConnector
import uk.gov.hmrc.emcstfe.featureswitch.core.config.FeatureSwitching
import uk.gov.hmrc.emcstfe.models.request.SubmitChangeDestinationRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.EISRIMValidationError
import uk.gov.hmrc.emcstfe.models.response.rimValidation.RIMValidationError
import uk.gov.hmrc.emcstfe.models.response.{EISSubmissionSuccessResponse, ErrorResponse}
import uk.gov.hmrc.emcstfe.repositories.ChangeDestinationUserAnswersRepository
import uk.gov.hmrc.emcstfe.utils.Logging
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmitChangeDestinationService @Inject()(eisConnector: EisConnector,
                                               changeDestinationUserAnswersRepository: ChangeDestinationUserAnswersRepository,
                                               val config: AppConfig) extends Logging with FeatureSwitching {

  def submitViaEIS(request: SubmitChangeDestinationRequest)
                  (implicit hc: HeaderCarrier,
                   ec: ExecutionContext): Future[Either[ErrorResponse, EISSubmissionSuccessResponse]] =
    eisConnector.submit[EISSubmissionSuccessResponse](request, "submitChangeOfDestinationEISRequest").flatMap(handleResponse(request, _))

  private def handleResponse[A](requestModel: SubmitChangeDestinationRequest, response: Either[ErrorResponse, A])
                               (implicit ec: ExecutionContext): Future[Either[ErrorResponse, A]] = {
    val arc = requestModel.body.updateEadEsad.administrativeReferenceCode
    response match {
      //If the submission fails due to RIM validation errors, store the errors in Mongo for persistence
      case Left(rimError: EISRIMValidationError) =>
        logger.warn(s"[handleResponse][${requestModel.exciseRegistrationNumber}] - RIM validation error codes for ARC - $arc: ${rimError.errorResponse.validatorResults.map(_.map(formatErrorForLogging))}")
        changeDestinationUserAnswersRepository.setValidationErrorMessagesForDraftMovement(
          requestModel.exciseRegistrationNumber, arc, rimError.errorResponse.validatorResults.getOrElse(Seq.empty)
        ).map {
          _ => Left(rimError)
        }
      //Clear any existing RIM validation errors when the movement is submitted successfully
      case Right(value) =>
        changeDestinationUserAnswersRepository.setValidationErrorMessagesForDraftMovement(
          requestModel.exciseRegistrationNumber, arc, Seq.empty
        ).map {
          _ => Right(value)
        }
      case response => Future.successful(response)
    }
  }

  private[services] def formatErrorForLogging(error: RIMValidationError): String = error match {
    case RIMValidationError(_, Some(errorType), reason, _) if (errorType == 12) | (errorType == 13) => s"Some($errorType) (errorReason: $reason)"
    case RIMValidationError(_, errorType, _, _) => s"$errorType"
  }
}
