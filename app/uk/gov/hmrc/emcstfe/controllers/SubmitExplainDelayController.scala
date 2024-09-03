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

import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.emcstfe.controllers.actions.{AuthAction, AuthActionHelper}
import uk.gov.hmrc.emcstfe.models.explainDelay.SubmitExplainDelayModel
import uk.gov.hmrc.emcstfe.services.SubmitExplainDelayService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.annotation.unused
import scala.concurrent.ExecutionContext

@Singleton()
class SubmitExplainDelayController @Inject()(cc: ControllerComponents,
                                             service: SubmitExplainDelayService,
                                             override val auth: AuthAction
                                            )(implicit ec: ExecutionContext) extends BackendController(cc) with AuthActionHelper {

  def submit(ern: String, @unused arc: String): Action[JsValue] = authorisedUserSubmissionRequest(ern) { implicit request =>
    withJsonBody[SubmitExplainDelayModel] {
      service.submitViaEIS(_).map {
        case Left(value) => InternalServerError(Json.toJson(value))
        case Right(value) => Ok(Json.toJson(value))
      }
    }
  }
}
