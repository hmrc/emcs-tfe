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

package uk.gov.hmrc.emcstfe.testOnly.controllers

import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.emcstfe.controllers.actions.{AuthAction, AuthActionHelper}
import uk.gov.hmrc.emcstfe.models.mongo.MovementTemplate
import uk.gov.hmrc.emcstfe.services.templates.MovementTemplatesService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.annotation.unused
import scala.concurrent.ExecutionContext

class InsertMovementTemplateController @Inject()(cc: ControllerComponents,
                                                 templatesService: MovementTemplatesService,
                                                 override val auth: AuthAction
                                                 )(implicit ec: ExecutionContext) extends BackendController(cc) with AuthActionHelper {

  def set(@unused ern: String, @unused templateId: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    withJsonBody[MovementTemplate] {
      answers =>
        templatesService.set(answers) map {
          case Right(answers) => Ok(Json.toJson(answers))
          case Left(mongoError) => InternalServerError(Json.toJson(mongoError))
        }
    }
  }

}
