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

package uk.gov.hmrc.emcstfe.utils

import org.scalatestplus.play.PlaySpec
import uk.gov.hmrc.emcstfe.fixtures.BaseFixtures

class SHA256HashingSpec extends PlaySpec with BaseFixtures {

  ".getHash" should {

    "parse a value into the correct SHA-256 hash" in {

      val expectedValue: String = "80298ad82661b0744d95cd969f782fdde6db73e92d25f52ef0b77b803bfbf4d9"

      SHA256Hashing.getHash(testPlainTextPayload) mustBe expectedValue
    }
  }
}
