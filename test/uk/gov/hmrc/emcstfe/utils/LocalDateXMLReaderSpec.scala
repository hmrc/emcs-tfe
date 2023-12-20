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
import uk.gov.hmrc.emcstfe.utils.LocalDateXMLReader.LocalDateParseFailure

import java.time.LocalDate
import scala.xml.Elem

class LocalDateXMLReaderSpec extends TestBaseSpec {

  "LocalDateXMLReader" must {

    "successfully parse a LocalDate" when {

      "XML response contains a valid LocalDate format" in {

        val xml: Elem = <ldt>2020-06-04</ldt>

        LocalDateXMLReader.xmlLocalDateReads.read(xml) shouldBe ParseSuccess(LocalDate.of(2020, 6, 4))
      }
    }

    "fail to parse a LocalDate" when {

      "XML response is NOT a valid LocalDate format" in {

        val xml: Elem = <ldt>2020-06-04BANGFOO</ldt>

        LocalDateXMLReader.xmlLocalDateReads.read(xml) shouldBe ParseFailure(LocalDateParseFailure("Text '2020-06-04BANGFOO' could not be parsed, unparsed text found at index 10"))
      }
    }
  }
}
