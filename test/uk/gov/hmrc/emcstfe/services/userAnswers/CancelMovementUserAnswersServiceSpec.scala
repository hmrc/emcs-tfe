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

package uk.gov.hmrc.emcstfe.services.userAnswers

import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import uk.gov.hmrc.emcstfe.repositories.CancelMovementUserAnswersRepository
import uk.gov.hmrc.emcstfe.support.UnitSpec

class CancelMovementUserAnswersServiceSpec extends UnitSpec with GuiceOneAppPerSuite {

  "CancelMovementUserAnswersService" must {

    "have the correct repo injected" in {

      val service = app.injector.instanceOf[CancelMovementUserAnswersService]
      val repo = app.injector.instanceOf[CancelMovementUserAnswersRepository]

      service.repo shouldBe repo
    }
  }
}
