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
import uk.gov.hmrc.emcstfe.services.userAnswers.ExplainShortageOrExcessUserAnswersService
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

class ExplainShortageOrExcessUserAnswersControllerSpec extends TestBaseSpec with GuiceOneAppPerSuite {

  "ExplainShortageOrExcessUserAnswersController" must {

    "have the correct service injected" in {

      val controller = app.injector.instanceOf[ExplainShortageOrExcessUserAnswersController]
      val service = app.injector.instanceOf[ExplainShortageOrExcessUserAnswersService]

      controller.userAnswersService.getClass shouldBe service.getClass
    }
  }
}
