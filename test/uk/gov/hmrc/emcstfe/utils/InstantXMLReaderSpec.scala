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
import uk.gov.hmrc.emcstfe.utils.InstantXMLReader.InstantParseFailure

import java.time.{LocalDateTime, ZoneOffset}
import scala.xml.Elem

class InstantXMLReaderSpec extends TestBaseSpec {

  "InstantXMLReader" must {

    "successfully parse an Instant" when {

      "XML response contains a valid Instant format" in {

        val xml: Elem = <ldt>2023-12-10T12:00:00.000Z</ldt>
        val expectedResultAsLocalDateTime = LocalDateTime.of(2023, 12, 10, 12, 0, 0, 0)
        InstantXMLReader.xmlInstantReads.read(xml) shouldBe ParseSuccess(expectedResultAsLocalDateTime.toInstant(ZoneOffset.UTC))
      }
    }

    "fail to parse an Instant" when {

      "XML response is NOT a valid Instant format" in {

        val xml: Elem = <ldt>2023-12-10T12:FOO</ldt>

        InstantXMLReader.xmlInstantReads.read(xml) shouldBe ParseFailure(InstantParseFailure("Text '2023-12-10T12:FOO' could not be parsed at index 14"))
      }
    }
  }
}
