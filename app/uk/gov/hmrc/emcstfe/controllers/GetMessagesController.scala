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
import uk.gov.hmrc.emcstfe.controllers.actions.{AuthAction, AuthActionHelper}
import uk.gov.hmrc.emcstfe.models.request.GetMessagesRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.QueryParameterError
import uk.gov.hmrc.emcstfe.services.GetMessagesService
import uk.gov.hmrc.emcstfe.utils.Logging
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

@Singleton()
class GetMessagesController @Inject()(cc: ControllerComponents,
                                      service: GetMessagesService,
                                      override val auth: AuthAction
                                     )(implicit ec: ExecutionContext) extends BackendController(cc) with AuthActionHelper with Logging {

  def getMessages(ern: String, sortField: String, sortOrder: String, page: Int): Action[AnyContent] =
    authorisedUserRequest(ern) { implicit request =>
      (Try {
        GetMessagesRequest(exciseRegistrationNumber = ern, sortField = sortField, sortOrder = sortOrder, page = page)
      } match {
        case Failure(exception) =>
          val queryParams: Seq[(String, String)] = Seq("sortField" -> sortField, "sortOrder" -> sortOrder, "page" -> page.toString)
          logger.warn(QueryParameterError(queryParams).message, exception)
          Future.successful(Left(QueryParameterError(queryParams)))
        case Success(value) => service.getMessages(value)
      }).map {
        case Left(value) => InternalServerError(Json.toJson(value))
        case Right(value) => Ok(Json.toJson(value))
      }
    }
}
