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
import play.api.libs.json.Json
import uk.gov.hmrc.emcstfe.fixtures.GetSubmissionFailureMessageFixtures
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.xml.XML

class IE704HeaderSpec extends TestBaseSpec with GetSubmissionFailureMessageFixtures {

  import IE704HeaderFixtures._

  "IE704Header" when {
    "maximum fields" should {
      "turn XML into a model" in {
        IE704Header.xmlReader.read(XML.loadString(ie704HeaderXmlBody)) shouldBe ParseSuccess(ie704HeaderModel)
      }
      "turn a model into JSON" in {
        Json.toJson(ie704HeaderModel) shouldBe ie704HeaderJson
      }
    }

    "minimum fields" should {
      "turn XML into a model" in {
        IE704Header.xmlReader.read(XML.loadString(ie704HeaderMinimumXmlBody)) shouldBe ParseSuccess(ie704HeaderMinimumModel)
      }
      "turn a model into JSON" in {
        Json.toJson(ie704HeaderMinimumModel) shouldBe ie704HeaderMinimumJson
      }
    }
  }

}
