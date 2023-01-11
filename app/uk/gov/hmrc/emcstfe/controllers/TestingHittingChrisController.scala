/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.emcstfe.connector.ChrisConnector
import uk.gov.hmrc.emcstfe.models.response.HelloWorldResponse
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton()
class TestingHittingChrisController @Inject()(cc: ControllerComponents, connector: ChrisConnector)(implicit ec: ExecutionContext)
    extends BackendController(cc) {

  def handleGet(): Action[AnyContent] = Action.async { implicit request =>
    connector.getMovementHistoryEvents().map {
      case Left(value) => InternalServerError(Json.toJson(HelloWorldResponse(value)))
      case Right(value) => Ok(value)
    }
  }
}
