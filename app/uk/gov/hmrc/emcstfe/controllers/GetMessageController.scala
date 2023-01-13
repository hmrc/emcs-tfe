/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.emcstfe.models.request.GetMessageRequest
import uk.gov.hmrc.emcstfe.services.GetMessageService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton()
class GetMessageController @Inject()(cc: ControllerComponents, service: GetMessageService)(implicit ec: ExecutionContext)
    extends BackendController(cc) {

  def getMessage(exciseRegistrationNumber: String, arc: String): Action[AnyContent] = Action.async { implicit request =>
    service.getMessage(GetMessageRequest(exciseRegistrationNumber = exciseRegistrationNumber, arc = arc)).map {
      case Left(value) => InternalServerError(Json.toJson(value))
      case Right(value) => Ok(Json.toJson(value))
    }
  }
}
