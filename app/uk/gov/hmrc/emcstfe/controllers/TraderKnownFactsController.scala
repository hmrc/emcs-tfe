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

package uk.gov.hmrc.emcstfe.controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.emcstfe.controllers.actions.{AuthAction, AuthActionHelper}
import uk.gov.hmrc.emcstfe.services.TraderKnownFactsService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class TraderKnownFactsController @Inject() (cc: ControllerComponents, service: TraderKnownFactsService, override val auth: AuthAction)(implicit ec: ExecutionContext) extends BackendController(cc) with AuthActionHelper {

  def getTraderKnownFacts(exciseRegistrationId: String): Action[AnyContent] = authorisedUserRequest(exciseRegistrationId) { implicit request =>
    service.getTraderKnownFacts(exciseRegistrationId).map {
      case Left(value)        => InternalServerError(Json.toJson(value))
      case Right(None)        => NoContent
      case Right(Some(value)) => Ok(Json.toJson(value))
    }
  }

}
