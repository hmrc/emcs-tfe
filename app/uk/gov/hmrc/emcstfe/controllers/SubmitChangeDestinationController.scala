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
import uk.gov.hmrc.emcstfe.controllers.actions.{AuthAction, AuthActionHelper}
import uk.gov.hmrc.emcstfe.featureswitch.core.config.{FeatureSwitching, SendToEIS, ValidateUsingFS41Schema}
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.changeDestination.SubmitChangeDestinationModel
import uk.gov.hmrc.emcstfe.models.request.{GetMovementRequest, SubmitChangeDestinationRequest}
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{ChRISRIMValidationError, EISRIMValidationError}
import uk.gov.hmrc.emcstfe.services.{GetMovementService, SubmitChangeDestinationService}
import uk.gov.hmrc.emcstfe.utils.Logging
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class SubmitChangeDestinationController @Inject()(cc: ControllerComponents,
                                                  submitChangeDestinationService: SubmitChangeDestinationService,
                                                  getMovementService: GetMovementService,
                                                  val config: AppConfig,
                                                  override val auth: AuthAction
                                                 )(implicit ec: ExecutionContext) extends BackendController(cc) with AuthActionHelper with Logging with FeatureSwitching {

  def submit(ern: String, arc: String): Action[JsValue] = authorisedUserSubmissionRequest(ern) { implicit request =>
    withJsonBody[SubmitChangeDestinationModel] { submission =>
      getMovementService.getMovement(GetMovementRequest(ern, arc, None), forceFetchNew = true).flatMap {
        case Left(error) =>
          logger.error(s"[submit] Failed to retrieve movement for $ern and $arc")
          Future.successful(InternalServerError(Json.toJson(error)))
        case Right(movement) =>
          val requestModel = SubmitChangeDestinationRequest(submission, movement, isEnabled(ValidateUsingFS41Schema))
            handleSubmission(requestModel)
      }
    }
  }

  private def handleSubmission(requestModel: SubmitChangeDestinationRequest)(implicit hc: HeaderCarrier, ec: ExecutionContext, req: UserRequest[_]): Future[Result] = {
    if (isEnabled(SendToEIS)) {
      submitChangeDestinationService.submitViaEIS(requestModel).flatMap(result => handleResponse(result))
    } else {
      submitChangeDestinationService.submit(requestModel).flatMap(result => handleResponse(result))
    }
  }

  private def handleResponse[A](response: Either[ErrorResponse, A])(implicit writes: Writes[A]): Future[Result] =
    response match {
      case Left(value: EISRIMValidationError) => Future(UnprocessableEntity(Json.toJson(value)))
      case Left(value: ChRISRIMValidationError) => Future(UnprocessableEntity(Json.toJson(value)))
      case Left(value) => Future(InternalServerError(Json.toJson(value)))
      case Right(value) => Future(Ok(Json.toJson(value)))
    }
}
