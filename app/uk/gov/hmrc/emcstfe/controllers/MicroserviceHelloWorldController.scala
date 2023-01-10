/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.emcstfe.connector.ChrisStubConnector
import uk.gov.hmrc.emcstfe.models.response.HelloWorldResponse
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton()
class MicroserviceHelloWorldController @Inject()(cc: ControllerComponents, connector: ChrisStubConnector)(implicit ec: ExecutionContext)
    extends BackendController(cc) {

  def hello(): Action[AnyContent] = Action.async { implicit request =>
    connector.hello().map {
      case Left(value) => InternalServerError(Json.toJson(HelloWorldResponse(value)))
      case Right(value) => Ok(Json.toJson(HelloWorldResponse(value.message)))
    }
  }
}
