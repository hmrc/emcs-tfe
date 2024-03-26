/*
 * Copyright 2024 HM Revenue & Customs
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

import uk.gov.hmrc.emcstfe.fixtures.PreValidateFixtures
import uk.gov.hmrc.emcstfe.models.request.eis.preValidate.{ExciseTraderRequest, ExciseTraderValidationRequest, PreValidateRequest}
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

class ExciseTraderValidationRequestSpec extends TestBaseSpec with PreValidateFixtures {

  val request = PreValidateRequest(
    exciseTraderValidationRequest = ExciseTraderValidationRequest(
      exciseTraderRequest = ExciseTraderRequest(
        exciseRegistrationNumber = testErn,
        entityGroup = "UK Record",
        validateProductAuthorisationRequest = Seq.empty
      )
    )
  )

  ".exciseRegistrationNumber" should {
    "return the pre validate request ERN" in {
      request.exciseRegistrationNumber shouldBe testErn
    }
  }

  ".eisXMLBody" should {
    "return an empty value as pre validate doesn't send XML to EIS" in {
      request.eisXMLBody() shouldBe ""
    }
  }
}
