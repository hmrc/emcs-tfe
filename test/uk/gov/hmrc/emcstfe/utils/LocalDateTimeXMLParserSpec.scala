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
import uk.gov.hmrc.emcstfe.support.UnitSpec
import uk.gov.hmrc.emcstfe.utils.LocalDateTimeXMLReader.LocalDateTimeParseFailure

import java.time.LocalDateTime
import scala.xml.Elem

class LocalDateTimeXMLParserSpec extends UnitSpec {

  "LocalDateTimeXMLParser" must {

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
}
