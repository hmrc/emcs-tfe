/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.emcstfe.connector.ChrisConnector
import uk.gov.hmrc.emcstfe.models.request.GetMessageRequest
import uk.gov.hmrc.emcstfe.models.response.HelloWorldResponse
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton()
class GetMessageController @Inject()(cc: ControllerComponents, connector: ChrisConnector)(implicit ec: ExecutionContext)
    extends BackendController(cc) {

  def getMessage(exciseRegistrationNumber: String, arc: String): Action[AnyContent] = Action.async { implicit request =>
    connector.getMessage(GetMessageRequest(exciseRegistrationNumber = exciseRegistrationNumber, arc = arc)).map {
      case Left(value) => InternalServerError(Json.toJson(HelloWorldResponse(value.message)))
      case Right(value) => Ok(value)
    }
  }
}
