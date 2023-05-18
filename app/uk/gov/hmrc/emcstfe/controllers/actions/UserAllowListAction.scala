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

package uk.gov.hmrc.emcstfe.controllers.actions

import play.api.libs.json.Json
import play.api.mvc.Results.InternalServerError
import play.api.mvc.{ActionRefiner, Result}
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.connectors.userAllowList.UserAllowListConnector
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.request.CheckUserAllowListRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{UnexpectedDownstreamResponseError, UserAllowListError}
import uk.gov.hmrc.emcstfe.utils.Logging
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UserAllowListActionImpl @Inject()(userAllowListConnector: UserAllowListConnector,
                                        config: AppConfig)
                                      (implicit val executionContext: ExecutionContext) extends UserAllowListAction {

  override protected def refine[A](request: UserRequest[A]): Future[Either[Result, UserRequest[A]]] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    if(config.allowListEnabled) {
      userAllowListConnector.check(CheckUserAllowListRequest(request.ern)) map {
        case Right(true) => Right(request)
        case Right(false) =>
          logger.info(s"[refine] User with ern: '${request.ern}' was not on the allow-list")
          Left(InternalServerError(Json.toJson(UserAllowListError(request.ern))))
        case Left(_) =>
          logger.warn(s"[refine] Unable to check if User is on allow-list as unexpected error returned from user-allow-list")
          Left(InternalServerError(Json.toJson(UnexpectedDownstreamResponseError)))
      }
    } else {
      Future.successful(Right(request))
    }
  }
}

trait UserAllowListAction extends ActionRefiner[UserRequest, UserRequest] with Logging
