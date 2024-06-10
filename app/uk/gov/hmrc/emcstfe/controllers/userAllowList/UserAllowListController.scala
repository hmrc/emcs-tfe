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

package uk.gov.hmrc.emcstfe.controllers.userAllowList

import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.emcstfe.controllers.actions.{AuthAction, AuthActionHelper}
import uk.gov.hmrc.emcstfe.services.userAllowList.UserAllowListService
import uk.gov.hmrc.emcstfe.utils.Logging
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton()
class UserAllowListController @Inject()(cc: ControllerComponents,
                                      service: UserAllowListService,
                                      override val auth: AuthAction
                                     )(implicit ec: ExecutionContext) extends BackendController(cc) with AuthActionHelper with Logging {

  def checkEligibility(ern: String, serviceName: String): Action[AnyContent] = authorisedUserRequest(ern) { implicit request =>
    service.isEligible(ern, serviceName).map(_.fold(
      error => {
        logger.warn("[checkEligibility] An error response was returned from user-allow-list, returning 500 (ISE) back to caller")
        InternalServerError(error.message)
      },
      if(_) {
        logger.debug(s"[checkEligibility] User $ern is eligible for private/public beta")
        Ok("User is eligible for private/public beta")
      } else {
        logger.debug(s"[checkEligibility] User $ern is not eligible for private/public beta")
        NoContent
      }
    ))
  }
}