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

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents, Result}
import uk.gov.hmrc.emcstfe.controllers.actions.{AuthAction, AuthActionHelper}
import uk.gov.hmrc.emcstfe.models.request.GetSubmissionFailureMessageRequest
import uk.gov.hmrc.emcstfe.models.response.getSubmissionFailureMessage.GetSubmissionFailureMessageResponse
import uk.gov.hmrc.emcstfe.repositories.CreateMovementUserAnswersRepository
import uk.gov.hmrc.emcstfe.services.GetSubmissionFailureMessageService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetSubmissionFailureMessageController @Inject()(cc: ControllerComponents,
                                                      service: GetSubmissionFailureMessageService,
                                                      createMovementUserAnswersRepository: CreateMovementUserAnswersRepository,
                                                      override val auth: AuthAction
                                                     )(implicit ec: ExecutionContext) extends BackendController(cc) with AuthActionHelper {

  def getSubmissionFailureMessage(exciseRegistrationNumber: String, messageId: String): Action[AnyContent] = authorisedUserRequest(exciseRegistrationNumber) { implicit request =>
    service.getSubmissionFailureMessage(GetSubmissionFailureMessageRequest(exciseRegistrationNumber = exciseRegistrationNumber, messageId = messageId)).flatMap {
      case Left(error) => Future(InternalServerError(Json.toJson(error)))
      case Right(response) =>
        val correlationID = response.ie704.header.correlationIdentifier.getOrElse("")
        if(correlationID.startsWith("PORTAL")) {
          Future(returnResponse(response, isTFESubmission = true))
        } else {
          createMovementUserAnswersRepository.get(exciseRegistrationNumber, correlationID).map {
            optDraftMovement => {
              returnResponse(response, isTFESubmission = optDraftMovement.isDefined)
            }
          }
        }
    }
  }

  private def returnResponse(response: GetSubmissionFailureMessageResponse, isTFESubmission: Boolean): Result = {
    Ok(Json.toJson(response)(GetSubmissionFailureMessageResponse.jsonWrites(isTFESubmission)))
  }
}
