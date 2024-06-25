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
import uk.gov.hmrc.emcstfe.featureswitch.core.config.FeatureSwitching
import uk.gov.hmrc.emcstfe.models.request.SubmitCreateMovementRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{ChRISRIMValidationError, EISRIMValidationError}
import uk.gov.hmrc.emcstfe.models.response.rimValidation.RIMValidationError
import uk.gov.hmrc.emcstfe.models.response.{ChRISSuccessResponse, EISSubmissionSuccessResponse, ErrorResponse}
import uk.gov.hmrc.emcstfe.repositories.CreateMovementUserAnswersRepository
import uk.gov.hmrc.emcstfe.utils.Logging
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmitCreateMovementService @Inject()(chrisConnector: ChrisConnector,
                                            eisConnector: EisConnector,
                                            createMovementUserAnswersRepository: CreateMovementUserAnswersRepository,
                                            val config: AppConfig) extends Logging with FeatureSwitching {
  def submit(requestModel: SubmitCreateMovementRequest)
            (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, ChRISSuccessResponse]] =
    chrisConnector.submitCreateMovementChrisSOAPRequest[ChRISSuccessResponse](requestModel).flatMap(handleResponse(requestModel, _))

  def submitViaEIS(requestModel: SubmitCreateMovementRequest)
                  (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, EISSubmissionSuccessResponse]] =
    eisConnector.submit[EISSubmissionSuccessResponse](requestModel, "submitCreateMovementEISRequest").flatMap(handleResponse(requestModel, _))

  def setSubmittedDraftId(ern: String, draftId: String, submittedDraftId: String): Future[Boolean] =
    createMovementUserAnswersRepository.setSubmittedDraftId(ern, draftId, submittedDraftId)

  private def handleResponse[A](requestModel: SubmitCreateMovementRequest, response: Either[ErrorResponse, A])
                               (implicit ec: ExecutionContext): Future[Either[ErrorResponse, A]] = {
    response match {
    //If the submission fails due to RIM validation errors, store the errors in Mongo for persistence
    case Left(rimError: EISRIMValidationError) =>
      logger.warn(s"[handleResponse][${requestModel.exciseRegistrationNumber}] - RIM validation error codes for correlation ID - ${rimError.errorResponse.emcsCorrelationId}: ${rimError.errorResponse.validatorResults.map(_.flatMap(formatErrorForLogging))}")
      createMovementUserAnswersRepository.setValidationErrorMessagesForDraftMovement(
        requestModel.exciseRegistrationNumber, requestModel.draftId, rimError.errorResponse.validatorResults.getOrElse(Seq.empty)
      ).map {
        _ => Left(rimError)
      }
    case Left(rimError: ChRISRIMValidationError) =>
      logger.warn(s"[handleResponse][${requestModel.exciseRegistrationNumber}] - RIM validation error codes for correlation ID - ${requestModel.correlationUUID}: ${rimError.errorResponse.rimValidationErrors.map(formatErrorForLogging)}")
      createMovementUserAnswersRepository.setValidationErrorMessagesForDraftMovement(
        requestModel.exciseRegistrationNumber, requestModel.draftId, rimError.errorResponse.rimValidationErrors
      ).map {
        _ => Left(rimError)
      }
    //Clear any existing RIM validation errors when the movement is submitted successfully
    case Right(value) =>
      createMovementUserAnswersRepository.setValidationErrorMessagesForDraftMovement(
        requestModel.exciseRegistrationNumber, requestModel.draftId, Seq.empty
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
