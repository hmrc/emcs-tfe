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
import uk.gov.hmrc.emcstfe.models.createMovement.SubmitCreateMovementModel
import uk.gov.hmrc.emcstfe.models.request.SubmitCreateMovementRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.EISRIMValidationError
import uk.gov.hmrc.emcstfe.services.SubmitCreateMovementService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class SubmitCreateMovementController @Inject()(cc: ControllerComponents,
                                               service: SubmitCreateMovementService,
                                               val config: AppConfig,
                                               override val auth: AuthAction
                                              )(implicit ec: ExecutionContext) extends BackendController(cc) with AuthActionHelper with FeatureSwitching {

  def submit(ern: String, draftId: String): Action[JsValue] = authorisedUserSubmissionRequest(ern) { implicit request =>
    withJsonBody[SubmitCreateMovementModel] { submission =>

      val submissionRequest = SubmitCreateMovementRequest(submission, draftId)
      val correlationId = if(isEnabled(DefaultDraftMovementCorrelationId)) DEFAULT_CORRELATION_ID else submissionRequest.correlationUUID

      service.submitViaEIS(submissionRequest).flatMap(responseModel => handleResponse(responseModel.map(_.copy(
        submittedDraftId = Some(correlationId))), ern, draftId, correlationId
      ))
    }
  }

  def handleResponse[A](response: Either[ErrorResponse, A], ern: String, draftId: String, correlationId: String)(implicit writes: Writes[A]): Future[Result] =
    response match {
      case Left(value: EISRIMValidationError) => Future(UnprocessableEntity(Json.toJson(value)))
      case Left(value) => Future(InternalServerError(Json.toJson(value)))
      case Right(value) =>
        service.setSubmittedDraftId(ern, draftId, correlationId).map {
          _ => Ok(Json.toJson(value))
        }
    }

}
