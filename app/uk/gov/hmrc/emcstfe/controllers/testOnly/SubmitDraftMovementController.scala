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

package uk.gov.hmrc.emcstfe.controllers.testOnly

import play.api.libs.json.Json
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.emcstfe.controllers.actions.AuthAction
import uk.gov.hmrc.emcstfe.models.request.SubmitDraftMovementRequest
import uk.gov.hmrc.emcstfe.services.SubmitDraftMovementService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import scala.xml.NodeSeq

@Singleton()
class SubmitDraftMovementController @Inject()(cc: ControllerComponents,
                                              service: SubmitDraftMovementService,
                                              authAction: AuthAction
                                             )(implicit ec: ExecutionContext) extends BackendController(cc) {

  def submitDraftMovement(): Action[NodeSeq] = Action.async(parse.xml) { implicit request =>
    service.submitDraftMovement(SubmitDraftMovementRequest(requestBody = request.body.toString())).map {
      case Left(value) => InternalServerError(Json.toJson(value))
      case Right(value) => Ok(Json.toJson(value))
    }
  }
}
