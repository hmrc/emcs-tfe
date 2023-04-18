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

package uk.gov.hmrc.emcstfe.models.request

import uk.gov.hmrc.emcstfe.support.UnitSpec

class SubmitDraftMovementRequestSpec extends UnitSpec {
  val request = SubmitDraftMovementRequest("My ERN", "My ARC", "")

  "action" should {
    "be correct" in {
      request.action shouldBe "http://www.hmrc.gov.uk/emcs/submitdraftmovementportal"
    }
  }

  "shouldExtractFromSoap" should {
    "be correct" in {
      request.shouldExtractFromSoap shouldBe false
    }
  }
}
