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
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.emcstfe.controllers.predicates.AuthAction
import uk.gov.hmrc.emcstfe.models.mongo.ReportReceiptUserAnswers
import uk.gov.hmrc.emcstfe.services.ReportReceiptUserAnswersService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton()
class ReportReceiptUserAnswersController @Inject()(cc: ControllerComponents,
                                                   userAnswersRepo: ReportReceiptUserAnswersService,
                                                   authAction: AuthAction
                                                  )(implicit ec: ExecutionContext) extends BackendController(cc) {

  def get(ern: String, arc: String): Action[AnyContent] = authAction(ern).async { implicit request =>
    userAnswersRepo.get(request.internalId, ern, arc) map {
      case Right(Some(answers)) => Ok(Json.toJson(answers))
      case Right(None) => NoContent
      case Left(mongoError) => InternalServerError(Json.toJson(mongoError))
    }
  }

  def set(ern: String, arc: String): Action[JsValue] = authAction(ern).async(parse.json) { implicit request =>
    withJsonBody[ReportReceiptUserAnswers] { answers =>
      userAnswersRepo.set(answers) map {
        case Right(answers) => Ok(Json.toJson(answers))
        case Left(mongoError) => InternalServerError(Json.toJson(mongoError))
      }
    }
  }

  def clear(ern: String, arc: String): Action[AnyContent] = authAction(ern).async { implicit request =>
    userAnswersRepo.clear(request.internalId, ern, arc) map {
      case Right(_) => NoContent
      case Left(mongoError) => InternalServerError(Json.toJson(mongoError))
    }
  }
}
