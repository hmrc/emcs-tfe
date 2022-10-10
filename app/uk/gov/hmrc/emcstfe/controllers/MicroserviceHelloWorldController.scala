/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.controllers

import play.api.libs.json.Json
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.emcstfe.models.response.HelloWorldResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton()
class MicroserviceHelloWorldController @Inject()(cc: ControllerComponents)
    extends BackendController(cc) {

  def hello(): Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(Json.toJson[HelloWorldResponse](HelloWorldResponse("Success from emcs-tfe"))))
  }
}
