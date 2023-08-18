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

package uk.gov.hmrc.emcstfe.controllers.userAnswers

import play.api.mvc.ControllerComponents
import uk.gov.hmrc.emcstfe.controllers.actions.{AuthAction, UserAllowListAction}
import uk.gov.hmrc.emcstfe.services.userAnswers.ExplainDelayUserAnswersService

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class ExplainDelayUserAnswersController @Inject()(val controllerComponents: ControllerComponents,
                                                  val userAnswersService: ExplainDelayUserAnswersService,
                                                  val auth: AuthAction,
                                                  val userAllowList: UserAllowListAction)
                                                 (implicit val ec: ExecutionContext) extends BaseUserAnswersController
