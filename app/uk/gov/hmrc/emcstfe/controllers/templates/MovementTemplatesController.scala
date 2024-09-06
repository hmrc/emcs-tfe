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

package uk.gov.hmrc.emcstfe.controllers.templates

import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.emcstfe.controllers.actions.{AuthAction, AuthActionHelper}
import uk.gov.hmrc.emcstfe.models.mongo.MovementTemplate
import uk.gov.hmrc.emcstfe.services.templates.MovementTemplatesService
import uk.gov.hmrc.emcstfe.utils.Logging
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.annotation.unused
import scala.concurrent.ExecutionContext

@Singleton()
class MovementTemplatesController @Inject()(cc: ControllerComponents,
                                            movementTemplatesService: MovementTemplatesService,
                                            override val auth: AuthAction
                                           )(implicit ec: ExecutionContext) extends BackendController(cc) with AuthActionHelper with Logging {

  def getList(ern: String): Action[AnyContent] =
    authorisedUserRequest(ern) { _ =>
      movementTemplatesService.getList(ern) map {
        case Right(Nil) => NoContent
        case Right(answers) => Ok(Json.toJson(answers))
        case Left(mongoError) => InternalServerError(Json.toJson(mongoError))
      }
    }

  def get(ern: String, templateId: String): Action[AnyContent] =
    authorisedUserRequest(ern) { _ =>
        movementTemplatesService.get(ern, templateId) map {
          case Right(Some(answers)) => Ok(Json.toJson(answers))
          case Right(_) => NoContent
          case Left(mongoError) => InternalServerError(Json.toJson(mongoError))
        }
    }

  def set(ern: String, @unused templateId: String): Action[JsValue] =
    authorisedUserSubmissionRequest(ern) {
      implicit request =>
        withJsonBody[MovementTemplate] {
          answers =>
            movementTemplatesService.set(answers) map {
              case Right(_) => Ok(Json.toJson(answers))
              case Left(mongoError) => InternalServerError(Json.toJson(mongoError))
            }
        }
    }

  def delete(ern: String, templateId: String): Action[AnyContent] =
    authorisedUserRequest(ern) {
      _ =>
        movementTemplatesService.delete(ern, templateId) map {
          case Right(_) => NoContent
          case Left(mongoError) => InternalServerError(Json.toJson(mongoError))
        }
    }

  def checkForExistingTemplate(ern: String, templateName: String): Action[AnyContent] =
    authorisedUserRequest(ern) {
      _ =>
        movementTemplatesService.checkIfTemplateNameAlreadyExists(ern, templateName) map {
          case Right(exists) => Ok(Json.toJson(exists))
          case Left(mongoError) => InternalServerError(Json.toJson(mongoError))
        }
    }

  def createDraftFromTemplate(ern: String, templateId: String): Action[AnyContent] =
    authorisedUserRequest(ern) {
      _ =>
        movementTemplatesService.createDraftMovementFromTemplate(ern, templateId) map {
          case Right(draftId) => Created(Json.obj("createdDraftId" -> draftId))
          case Left(mongoError) => InternalServerError(Json.toJson(mongoError))
        }
    }
}
