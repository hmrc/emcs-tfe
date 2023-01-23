/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.emcstfe.models.request.GetMovementRequest
import uk.gov.hmrc.emcstfe.services.GetMovementService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton()
class GetMovementController @Inject()(cc: ControllerComponents, service: GetMovementService)(implicit ec: ExecutionContext)
    extends BackendController(cc) {

  def getMovement(exciseRegistrationNumber: String, arc: String): Action[AnyContent] = Action.async { implicit request =>
    service.getMovement(GetMovementRequest(exciseRegistrationNumber = exciseRegistrationNumber, arc = arc)).map {
      case Left(value) => InternalServerError(Json.toJson(value))
      case Right(value) => Ok(Json.toJson(value))
    }
  }
}
