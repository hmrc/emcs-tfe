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

import cats.data.EitherT
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.emcstfe.models.legacy.LegacyMessageAction
import uk.gov.hmrc.emcstfe.services.LegacyMessagesService
import uk.gov.hmrc.emcstfe.utils.Logging
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.xml.NodeSeq

class LegacyMessagesController @Inject()(cc: ControllerComponents,
                                         service: LegacyMessagesService,
                                        )(implicit ec: ExecutionContext) extends BackendController(cc) with Logging {

  def performMessagesOperation: Action[NodeSeq] = Action(parse.xml).async { implicit request =>
    EitherT.fromEither[Future](LegacyMessageAction(request)).flatMap(service.performMessageAction).fold(
      errorResponse => InternalServerError(errorResponse.message),
      value => Ok(value.toXml).as("application/soap+xml; charset=utf-8")
    )
  }

}
