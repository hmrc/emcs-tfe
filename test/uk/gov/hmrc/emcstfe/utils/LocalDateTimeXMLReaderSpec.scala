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

import com.lucidchart.open.xtract.{ParseFailure, ParseSuccess}
import uk.gov.hmrc.emcstfe.support.TestBaseSpec
import uk.gov.hmrc.emcstfe.utils.LocalDateTimeXMLReader.LocalDateTimeParseFailure

import java.time.{LocalDate, LocalDateTime, LocalTime}
import scala.xml.Elem

class LocalDateTimeXMLReaderSpec extends TestBaseSpec {

  "LocalDateTimeXMLReader" must {

    "successfully parse a LDT" when {

      "XML response contains a valid LocalDateTime format" in {

        val xml: Elem = <ldt>2020-06-04T14:56:21</ldt>

        LocalDateTimeXMLReader.xmlLocalDateTimeReads.read(xml) shouldBe ParseSuccess(LocalDateTime.of(2020, 6, 4, 14, 56, 21))
      }
    }

    "fail to parse a LDT" when {

      "XML response is NOT a valid LocalDateTime format" in {

        val xml: Elem = <ldt>2020-06-04T14:56:21BANGFOO</ldt>

        LocalDateTimeXMLReader.xmlLocalDateTimeReads.read(xml) shouldBe ParseFailure(LocalDateTimeParseFailure("Text '2020-06-04T14:56:21BANGFOO' could not be parsed, unparsed text found at index 19"))
      }
    }
  }

  "LocalDateXMLReader" must {

    "successfully parse a LD" when {

      "XML response contains a valid LocalDateTime format" in {

        val xml: Elem = <ld>2020-06-04</ld>

        LocalDateTimeXMLReader.xmlLocalDateReads.read(xml) shouldBe ParseSuccess(LocalDate.of(2020, 6, 4))
      }
    }

    "fail to parse a LD" when {

      "XML response is NOT a valid LocalDate format" in {

        val xml: Elem = <ld>2020-06-BANGFOO</ld>

        LocalDateTimeXMLReader.xmlLocalDateReads.read(xml) shouldBe ParseFailure(LocalDateTimeParseFailure("Text '2020-06-BANGFOO' could not be parsed at index 8"))
      }
    }
  }

  "LocalTimeXMLReader" must {

    "successfully parse a LT" when {

      "XML response contains a valid LocalTime format" in {

        val xml: Elem = <lt>04:06:22.123456789</lt>

        LocalDateTimeXMLReader.xmlLocalTimeReads.read(xml) shouldBe ParseSuccess(LocalTime.of(4, 6, 22, 123456789))
      }
    }

    "fail to parse a LT" when {

      "XML response is NOT a valid LocalTime format" in {

        val xml: Elem = <lt>04:06:BANGFOO</lt>

        LocalDateTimeXMLReader.xmlLocalTimeReads.read(xml) shouldBe ParseFailure(LocalDateTimeParseFailure("Text '04:06:BANGFOO' could not be parsed, unparsed text found at index 5"))
      }
    }
  }
}
