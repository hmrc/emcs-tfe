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

package uk.gov.hmrc.emcstfe.models.response.getSubmissionFailureMessage

import com.lucidchart.open.xtract.ParseSuccess
import play.api.libs.json.{JsResult, Json}
import uk.gov.hmrc.emcstfe.fixtures.GetSubmissionFailureMessageFixtures
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.xml.XML

class IE704FunctionalErrorSpec extends TestBaseSpec with GetSubmissionFailureMessageFixtures {

  import IE704FunctionalErrorFixtures._

  "IE704FunctionalError" when {
    "maximum fields" should {
      "turn XML into a model" in {
        IE704FunctionalError.xmlReader.read(XML.loadString(ie704FunctionalErrorXmlBody)) shouldBe ParseSuccess(ie704FunctionalErrorModel)
      }
      "turn a model into JSON" in {
        Json.toJson(ie704FunctionalErrorModel) shouldBe ie704FunctionalErrorJson
      }
    }

    "minimum fields" should {
      "turn XML into a model" in {
        IE704FunctionalError.xmlReader.read(XML.loadString(ie704FunctionalErrorMinimumXmlBody)) shouldBe ParseSuccess(ie704FunctionalErrorMinimumModel)
      }
      "turn a model into JSON" in {
        Json.toJson(ie704FunctionalErrorMinimumModel) shouldBe ie704FunctionalErrorMinimumJson
      }
    }

    "provided an invalid ErrorType" should {
      "throw an error" in {
        val result = intercept[JsResult.Exception](IE704FunctionalError.xmlReader.read(XML.loadString(ie704FunctionalErrorInvalidErrorTypeXmlBody)))

        result.getMessage shouldBe "{\"obj\":[{\"msg\":[\"Unknown functional error code in IE704 message: 1\"],\"args\":[]}]}"
      }
    }
  }

}
