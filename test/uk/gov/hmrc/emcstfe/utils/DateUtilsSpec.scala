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

package uk.gov.hmrc.emcstfe.utils

import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import java.time.LocalTime

class DateUtilsSpec extends TestBaseSpec with DateUtils {

  "LocalTimeExtensions" when {

    "calling .roundToNearestSecond" must {

      "handle .000000 by not changing the time" in {
        LocalTime.parse("12:01:01.000000").roundToNearestSecond() shouldBe LocalTime.of(12, 1, 1)
      }

      "handle .499999 by rounding down" in {
        LocalTime.parse("12:01:01.499999").roundToNearestSecond() shouldBe LocalTime.of(12, 1, 1)
      }

      "handle .500000 by rounding up" in {
        LocalTime.parse("12:01:01.500000").roundToNearestSecond() shouldBe LocalTime.of(12, 1, 2)
      }

      "handle .999999 by rounding up" in {
        LocalTime.parse("12:01:01.999999").roundToNearestSecond() shouldBe LocalTime.of(12, 1, 2)
      }
    }
  }
}
