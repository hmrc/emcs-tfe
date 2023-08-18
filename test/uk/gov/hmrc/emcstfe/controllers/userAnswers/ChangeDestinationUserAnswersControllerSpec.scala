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

import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import uk.gov.hmrc.emcstfe.services.userAnswers.ChangeDestinationUserAnswersService
import uk.gov.hmrc.emcstfe.support.UnitSpec

class ChangeDestinationUserAnswersControllerSpec extends UnitSpec with GuiceOneAppPerSuite {

  "ChangeDestinationUserAnswersController" must {

    "have the correct service injected" in {

      val controller = app.injector.instanceOf[ChangeDestinationUserAnswersController]
      val service = app.injector.instanceOf[ChangeDestinationUserAnswersService]

      controller.userAnswersService.getClass shouldBe service.getClass
    }
  }
}
