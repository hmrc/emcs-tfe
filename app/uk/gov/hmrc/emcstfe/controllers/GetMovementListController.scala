/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.emcstfe.models.request.GetMovementListRequest
import uk.gov.hmrc.emcstfe.services.GetMovementListService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton()
class GetMovementListController @Inject()(cc: ControllerComponents, service: GetMovementListService)
                                         (implicit ec: ExecutionContext) extends BackendController(cc) {

  def getMovementList(exciseRegistrationNumber: String): Action[AnyContent] = Action.async { implicit request =>
    service.getMovementList(GetMovementListRequest(exciseRegistrationNumber = exciseRegistrationNumber)).map {
      case Left(value) => InternalServerError(Json.toJson(value))
      case Right(value) => Ok(Json.toJson(value))
    }
  }
}
