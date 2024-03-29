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
import uk.gov.hmrc.emcstfe.repositories.ReportReceiptUserAnswersRepository
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

class ReportReceiptUserAnswersServiceSpec extends TestBaseSpec with GuiceOneAppPerSuite {

  "ReportReceiptUserAnswersService" must {

    "have the correct repo injected" in {

      val service = app.injector.instanceOf[ReportReceiptUserAnswersService]
      val repo = app.injector.instanceOf[ReportReceiptUserAnswersRepository]

      service.repo shouldBe repo
    }
  }
}
