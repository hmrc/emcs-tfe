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

import play.api.test.Helpers
import uk.gov.hmrc.emcstfe.controllers.actions.FakeUserAllowListAction
import uk.gov.hmrc.emcstfe.services.userAnswers.CancelMovementUserAnswersService

class CancelMovementUserAnswersControllerSpec extends BaseUserAnswersControllerSpec {

  override val route = s"/user-answers/cancel-movement/$testErn/$testArc"

  override val mockService: CancelMovementUserAnswersService = mock[CancelMovementUserAnswersService]
  override val controller = new CancelMovementUserAnswersController(
    Helpers.stubControllerComponents(),
    mockService,
    FakeSuccessAuthAction,
    FakeUserAllowListAction
  )
}
