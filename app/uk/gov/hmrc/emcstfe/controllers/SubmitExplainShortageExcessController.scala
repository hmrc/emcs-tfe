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

import play.api.libs.json.{JsValue, Json, Writes}
import play.api.mvc.{Action, ControllerComponents, Result}
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.controllers.actions.{AuthAction, AuthActionHelper}
import uk.gov.hmrc.emcstfe.featureswitch.core.config.{EnableNRS, FeatureSwitching, SendToEIS}
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.explainShortageExcess.SubmitExplainShortageExcessModel
import uk.gov.hmrc.emcstfe.models.nrs.NotableEvent.ExplainShortageOrExcessNotableEvent
import uk.gov.hmrc.emcstfe.models.nrs.explainShortageExcess.ExplainShortageExcessNRSSubmission
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.services.SubmitExplainShortageExcessService
import uk.gov.hmrc.emcstfe.services.nrs.NRSBrokerService
import uk.gov.hmrc.emcstfe.utils.Logging
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class SubmitExplainShortageExcessController @Inject()(cc: ControllerComponents,
                                                      service: SubmitExplainShortageExcessService,
                                                      nrsBrokerService: NRSBrokerService,
                                                      override val auth: AuthAction,
                                                      val config: AppConfig
                                                     )(implicit ec: ExecutionContext) extends BackendController(cc) with AuthActionHelper with Logging with FeatureSwitching {

  def submit(ern: String, arc: String): Action[JsValue] = authorisedUserSubmissionRequest(ern) {
    implicit request =>
      withJsonBody[SubmitExplainShortageExcessModel] {
        submission =>
          val nrsSubmissionModel = ExplainShortageExcessNRSSubmission.apply(submission, ern)
          if(isEnabled(EnableNRS)) {
            nrsBrokerService.submitPayload(nrsSubmissionModel, ern, ExplainShortageOrExcessNotableEvent).flatMap(_ => handleSubmission(submission))
          } else {
            handleSubmission(submission)
          }
      }
  }

  private def handleSubmission(submission: SubmitExplainShortageExcessModel)(implicit hc: HeaderCarrier, ec: ExecutionContext, request: UserRequest[_]): Future[Result] = {
    if (isEnabled(SendToEIS)) {
      service.submitViaEIS(submission).map(handleResponse(_))
    } else {
      service.submit(submission).map(handleResponse(_))
    }
  }

  def handleResponse[A](response: Either[ErrorResponse, A])(implicit writes: Writes[A]): Result =
    response match {
      case Left(value) => InternalServerError(Json.toJson(value))
      case Right(value) => Ok(Json.toJson(value))
    }

}
