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

package uk.gov.hmrc.emcstfe.controllers

import play.api.libs.json.{JsValue, Json, Writes}
import play.api.mvc.{Action, ControllerComponents, Result}
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.config.Constants.DEFAULT_CORRELATION_ID
import uk.gov.hmrc.emcstfe.controllers.actions.{AuthAction, AuthActionHelper}
import uk.gov.hmrc.emcstfe.featureswitch.core.config._
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.createMovement.SubmitCreateMovementModel
import uk.gov.hmrc.emcstfe.models.nrs.createMovement.CreateMovementNRSSubmission
import uk.gov.hmrc.emcstfe.models.request.SubmitCreateMovementRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{ChRISRIMValidationError, EISRIMValidationError}
import uk.gov.hmrc.emcstfe.services.SubmitCreateMovementService
import uk.gov.hmrc.emcstfe.services.nrs.NRSBrokerService
import uk.gov.hmrc.emcstfe.utils.Logging
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class SubmitCreateMovementController @Inject()(cc: ControllerComponents,
                                               service: SubmitCreateMovementService,
                                               nrsBrokerService: NRSBrokerService,
                                               val config: AppConfig,
                                               override val auth: AuthAction
                                              )(implicit ec: ExecutionContext) extends BackendController(cc) with AuthActionHelper with Logging with FeatureSwitching {

  def submit(ern: String, draftId: String): Action[JsValue] = authorisedUserSubmissionRequest(ern) { implicit request =>
    withJsonBody[SubmitCreateMovementModel] { submission =>
      if (isEnabled(EnableNRS)) {
        nrsBrokerService.submitPayload(CreateMovementNRSSubmission(ern, submission), ern).flatMap(_ => handleSubmission(ern, draftId, submission))
      } else {
        handleSubmission(ern, draftId, submission)
      }
    }
  }

  private def handleSubmission(ern: String, draftId: String, submission: SubmitCreateMovementModel)(implicit hc: HeaderCarrier, ec: ExecutionContext, req: UserRequest[_]): Future[Result] = {

    val sendToEIS = isEnabled(SendToEIS)
    val request = SubmitCreateMovementRequest(submission, draftId, isEnabled(ValidateUsingFS41Schema), isChRISSubmission = !sendToEIS)
    val correlationId = getCorrelationId(sendToEIS, isEnabled(DefaultDraftMovementCorrelationId), request)

    if (sendToEIS) {
      service.submitViaEIS(request).flatMap(responseModel => handleResponse(responseModel.map(_.copy(
        submittedDraftId = Some(correlationId))), ern, draftId, correlationId
      ))
    } else {
      service.submit(request).flatMap(responseModel => handleResponse(responseModel.map(_.copy(
        submittedDraftId = Some(correlationId))), ern, draftId, correlationId
      ))
    }
  }

  def handleResponse[A](response: Either[ErrorResponse, A], ern: String, draftId: String, correlationId: String)(implicit writes: Writes[A]): Future[Result] =
    response match {
      case Left(value: EISRIMValidationError) => Future(UnprocessableEntity(Json.toJson(value)))
      case Left(value: ChRISRIMValidationError) => Future(UnprocessableEntity(Json.toJson(value)))
      case Left(value) => Future(InternalServerError(Json.toJson(value)))
      case Right(value) =>
        service.setSubmittedDraftId(ern, draftId, correlationId).map {
          _ => Ok(Json.toJson(value))
        }
    }

  private def getCorrelationId(isEISFeatureEnabled: Boolean,
                               isDefaultDraftMovementCorrelationIdEnabled: Boolean,
                               requestModel: SubmitCreateMovementRequest): String = {
    (isDefaultDraftMovementCorrelationIdEnabled, isEISFeatureEnabled) match {
      case (true, _) => DEFAULT_CORRELATION_ID
      case (_, true) => requestModel.correlationUUID
      case (_, false) => requestModel.legacyCorrelationUUID
    }
  }
}
