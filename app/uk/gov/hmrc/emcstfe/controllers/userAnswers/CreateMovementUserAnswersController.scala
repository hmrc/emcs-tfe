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

package uk.gov.hmrc.emcstfe.controllers.userAnswers

import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.emcstfe.controllers.actions.{AuthAction, AuthActionHelper}
import uk.gov.hmrc.emcstfe.models.createMovement.submissionFailures.MovementSubmissionFailure
import uk.gov.hmrc.emcstfe.models.mongo.CreateMovementUserAnswers
import uk.gov.hmrc.emcstfe.models.request.GetDraftMovementSearchOptions
import uk.gov.hmrc.emcstfe.services.userAnswers.CreateMovementUserAnswersService
import uk.gov.hmrc.emcstfe.utils.Logging
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton()
class CreateMovementUserAnswersController @Inject()(cc: ControllerComponents,
                                                    createMovementUserAnswersService: CreateMovementUserAnswersService,
                                                    override val auth: AuthAction
                                                   )(implicit ec: ExecutionContext) extends BackendController(cc) with AuthActionHelper with Logging {

  def get(ern: String, draftId: String): Action[AnyContent] =
    authorisedUserRequest(ern) {
      _ =>
        createMovementUserAnswersService.get(ern, draftId) map {
          case Right(Some(answers)) => Ok(Json.toJson(answers))
          case Right(None) => NoContent
          case Left(mongoError) => InternalServerError(Json.toJson(mongoError))
        }
    }

  def set(ern: String, draftId: String): Action[JsValue] =
    authorisedUserSubmissionRequest(ern) {
      implicit request =>
        withJsonBody[CreateMovementUserAnswers] {
          answers =>
            createMovementUserAnswersService.set(answers) map {
              case Right(answers) => Ok(Json.toJson(answers))
              case Left(mongoError) => InternalServerError(Json.toJson(mongoError))
            }
        }
    }

  def clear(ern: String, draftId: String): Action[AnyContent] =
    authorisedUserRequest(ern) {
      _ =>
        createMovementUserAnswersService.clear(ern, draftId) map {
          case Right(_) => NoContent
          case Left(mongoError) => InternalServerError(Json.toJson(mongoError))
        }
    }

  def checkForExistingLrn(ern: String, lrn: String): Action[AnyContent] =
    authorisedUserRequest(ern) {
      _ =>
        createMovementUserAnswersService.checkForExistingLrn(ern, lrn) map {
          case Right(exists) => Ok(Json.toJson(exists))
          case Left(mongoError) => InternalServerError(Json.toJson(mongoError))
        }
    }

  def checkForExistingDraft(ern: String, messageId: String): Action[AnyContent] =
    authorisedUserRequest(ern) {
      _ =>
        createMovementUserAnswersService.get(ern, messageId) map {
          case Right(draft) => Ok(Json.obj("draftExists" -> draft.isDefined))
          case Left(mongoError) => InternalServerError(Json.toJson(mongoError))
        }
    }

  def markMovementAsDraft(ern: String, draftId: String): Action[AnyContent] =
    authorisedUserRequest(ern) {
      _ =>
        createMovementUserAnswersService.markDraftAsUnsubmitted(ern, draftId) map {
          case Right(true) => Ok(Json.obj("draftId" -> draftId))
          case Right(false) => NotFound("The draft movement could not be found")
          case Left(mongoError) => InternalServerError(Json.toJson(mongoError))
        }
    }

  def setErrorMessages(ern: String, submittedDraftId: String): Action[JsValue] =
    authorisedUserSubmissionRequest(ern) {
      implicit request =>
        withJsonBody[Seq[MovementSubmissionFailure]] {
          errors =>
            createMovementUserAnswersService.setErrorMessagesForDraftMovement(ern, submittedDraftId, errors) map {
              case Right(Some(draftId)) => Ok(Json.obj("draftId" -> draftId))
              case Right(None) => NotFound("The draft movement could not be found")
              case Left(mongoError) =>
                logger.warn(s"[setErrorMessages] - An error occurred setting the submission failures into draft: ${mongoError.message}")
                InternalServerError(Json.toJson(mongoError))
            }
        }
    }

  def search(ern: String, searchOptions: GetDraftMovementSearchOptions): Action[AnyContent] =
    authorisedUserRequest(ern) { _ =>
      createMovementUserAnswersService.searchDrafts(ern, searchOptions) map {
        case Right(response) => Ok(Json.toJson(response))
        case Left(mongoError) =>
          logger.warn(s"[search] An error occurred when searching the draft movements: ${mongoError.message}")
          InternalServerError(Json.toJson(mongoError))
      }
    }
}
