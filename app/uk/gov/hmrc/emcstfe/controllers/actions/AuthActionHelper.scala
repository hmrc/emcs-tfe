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

package uk.gov.hmrc.emcstfe.controllers.actions

import play.api.libs.json.JsValue
import play.api.mvc.{Action, AnyContent, BaseControllerHelpers, Result}
import uk.gov.hmrc.emcstfe.models.auth.UserRequest

import scala.concurrent.Future

trait AuthActionHelper extends BaseControllerHelpers {

  val auth: AuthAction

  def authorisedUserRequest(ern: String)(block: UserRequest[_] => Future[Result]): Action[AnyContent] =
    auth(ern).async(block)

  def authorisedUserSubmissionRequest(ern: String)(block: UserRequest[JsValue] => Future[Result]): Action[JsValue] =
    auth(ern).async(parse.json)(block)
}
