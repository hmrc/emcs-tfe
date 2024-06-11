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

import com.lucidchart.open.xtract._
import uk.gov.hmrc.emcstfe.fixtures.ChRISResponsesFixture
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{ChRISRIMValidationError, XmlParseError}
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.xml.XML

class XmlResultParserSpec extends TestBaseSpec with ChRISResponsesFixture {

  ".handleParseResult" must {

    "return Right(model)" when {

      "XML parsing is successful" in {

        XmlResultParser.parseResult(ParseSuccess("Success")) shouldBe Right("Success")
      }
    }

    "return Left(XmlParseError)" when {

      "XML parsing fails" in {
        val error = EmptyError(__ \ "tagName")
        XmlResultParser.parseResult(ParseFailure(error)) shouldBe Left(XmlParseError(Seq(error)))
      }

      "XML parsing partially fails" in {
        val error = EmptyError(__ \ "tagName")
        XmlResultParser.parseResult(PartialParseSuccess("PartialData", Seq(error))) shouldBe Left(XmlParseError(Seq(error)))
      }
    }
  }

  ".parseErrorResponse" must {

    "return ChRISRIMValidationError" when {

      "XML parsing is successful" in {

        XmlResultParser.parseErrorResponse(XML.loadString(chrisRimValidationResponseBody)) shouldBe ChRISRIMValidationError(chrisRIMValidationErrorResponse)
      }
    }

    "return XmlParseError" when {

      "XML parsing fails" in {

        XmlResultParser.parseErrorResponse(XML.loadString(invalidRimXmlBody)) match {
          case XmlParseError(errors) => errors.nonEmpty shouldBe true
          case _ => fail("Result was not of type 'XmlParseError'")
        }
      }
    }
  }
}
