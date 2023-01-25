/*
 * Copyright 2023 HM Revenue & Customs
 *
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
