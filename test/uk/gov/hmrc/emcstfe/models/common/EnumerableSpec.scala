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

package uk.gov.hmrc.emcstfe.models.common

import com.lucidchart.open.xtract.{ParseFailure, ParseSuccess, XPath, XmlReader}
import play.api.libs.json.{JsError, JsNumber, JsString, JsSuccess}
import uk.gov.hmrc.emcstfe.models.common.Enumerable.EnumerableXmlParseFailure
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

class EnumerableSpec extends TestBaseSpec with Enumerable.Implicits {

  implicit val enumerable: Enumerable[Int] = Enumerable.apply(
    "a" -> 1,
    "b" -> 2
  )

  "Enumerable" should {
    "read from JSON when JSON is valid" in {
      reads[Int].reads(JsString("a")) shouldBe JsSuccess(1)
    }

    "fail when JSON key is not valid entry for the Enumerable" in {
      reads[Int].reads(JsString("c")) shouldBe JsError(s"Invalid enumerable value of 'c'")
    }

    "fail when JSON key is not of type JsString" in {
      reads[Int].reads(JsNumber(1)) shouldBe JsError("Enumerable value was not of type JsString")
    }
  }

  "xmlReads" when {
    case class TestClass(beans: Int)

    implicit val xmlReader: XmlReader[TestClass] =
      (XPath \\ "Beans").read[Int](xmlReads("Beans")(enumerable)).map(TestClass)

    "input is valid" should {
      "return the result" in {
        val xml = <Beans>a</Beans>

        xmlReader.read(xml) shouldBe ParseSuccess(TestClass(1))
      }
    }
    "input is invalid" should {
      "return an error" in {
        val xml = <Beans>c</Beans>

        xmlReader.read(xml) shouldBe ParseFailure(Seq(EnumerableXmlParseFailure("Invalid enumerable value of 'c' for field 'Beans'")))
      }
    }
  }
}
