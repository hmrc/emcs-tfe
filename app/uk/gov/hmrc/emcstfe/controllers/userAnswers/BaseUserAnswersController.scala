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
import uk.gov.hmrc.emcstfe.controllers.actions.{AuthAction, AuthActionHelper, UserAllowListAction}
import uk.gov.hmrc.emcstfe.models.mongo.UserAnswers
import uk.gov.hmrc.emcstfe.services.userAnswers.BaseUserAnswersService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendBaseController

import scala.concurrent.ExecutionContext

trait BaseUserAnswersController extends BackendBaseController with AuthActionHelper {

  val controllerComponents: ControllerComponents
  val userAnswersService: BaseUserAnswersService
  val auth: AuthAction
  val userAllowList: UserAllowListAction
  implicit val ec: ExecutionContext

  def get(ern: String, arc: String): Action[AnyContent] = authorisedUserRequest(ern) { _ =>
    userAnswersService.get(ern, arc) map {
      case Right(Some(answers)) => Ok(Json.toJson(answers))
      case Right(None) => NoContent
      case Left(mongoError) => InternalServerError(Json.toJson(mongoError))
    }
  }

  def set(ern: String, arc: String): Action[JsValue] = authorisedUserSubmissionRequest(ern) { implicit request =>
    withJsonBody[UserAnswers] { answers =>
      userAnswersService.set(answers) map {
        case Right(answers) => Ok(Json.toJson(answers))
        case Left(mongoError) => InternalServerError(Json.toJson(mongoError))
      }
    }
  }

  def clear(ern: String, arc: String): Action[AnyContent] = authorisedUserRequest(ern) { _ =>
    userAnswersService.clear(ern, arc) map {
      case Right(_) => NoContent
      case Left(mongoError) => InternalServerError(Json.toJson(mongoError))
    }
  }
}
