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

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.emcstfe.controllers.actions.{AuthAction, AuthActionHelper, UserAllowListAction}
import uk.gov.hmrc.emcstfe.models.request.GetMovementRequest
import uk.gov.hmrc.emcstfe.services.GetMovementService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton()
class GetMovementController @Inject()(cc: ControllerComponents,
                                      service: GetMovementService,
                                      override val auth: AuthAction,
                                      override val userAllowList: UserAllowListAction
                                     )(implicit ec: ExecutionContext) extends BackendController(cc) with AuthActionHelper {

  def getMovement(exciseRegistrationNumber: String, arc: String, forceFetchNew: Boolean = false): Action[AnyContent] = authorisedUserRequest(exciseRegistrationNumber) { implicit request =>
    service.getMovement(GetMovementRequest(exciseRegistrationNumber = exciseRegistrationNumber, arc = arc), forceFetchNew = forceFetchNew).map {
      case Left(value) => InternalServerError(Json.toJson(value))
      case Right(value) => Ok(Json.toJson(value))
    }
  }
}
