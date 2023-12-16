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

import scala.xml.Elem

class XmlReaderUtilsSpec extends TestBaseSpec {

  object TestXmlReaderUtils extends XmlReaderUtils

  "xmlBigDecimalReads" should {
    "read in a BigDecimal successfully" in {
      val xml: Elem = <bigdecimal>3.34567</bigdecimal>
      TestXmlReaderUtils.xmlBigDecimalReads.read(xml) shouldBe ParseSuccess(BigDecimal(3.34567))
    }

    "return a BigDecimalParseFailure when the input is not in a valid BigDecimal format" in {
      val xml: Elem = <bigdecimal>fake</bigdecimal>
      TestXmlReaderUtils.xmlBigDecimalReads.read(xml) shouldBe ParseFailure(TestXmlReaderUtils.BigDecimalParseFailure("Character f is neither a decimal digit number, decimal point, nor \"e\" notation exponential mark."))
    }
  }

}
