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
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.XmlParseError
import uk.gov.hmrc.emcstfe.support.UnitSpec

class XmlResultParserSpec extends UnitSpec {

  ".handleParseResult" must {

    "return Right(model)" when {

      "XML parsing is successful" in {

        XmlResultParser.handleParseResult(ParseSuccess("Success")) shouldBe Right("Success")
      }
    }

    "return Left(XmlParseError)" when {

      "XML parsing fails" in {
        val error = EmptyError(__ \ "tagName")
        XmlResultParser.handleParseResult(ParseFailure(error)) shouldBe Left(XmlParseError(Seq(error)))
      }

      "XML parsing partially fails" in {
        val error = EmptyError(__ \ "tagName")
        XmlResultParser.handleParseResult(PartialParseSuccess("PartialData", Seq(error))) shouldBe Left(XmlParseError(Seq(error)))
      }
    }
  }
}
