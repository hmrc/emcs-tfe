/*
 * Copyright 2024 HM Revenue & Customs
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

package uk.gov.hmrc.emcstfe.models.response

import com.lucidchart.open.xtract.ParseSuccess
import play.api.libs.json.Json
import uk.gov.hmrc.emcstfe.fixtures.{GetMessagesFixtures, MarkMessageAsReadFixtures}
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.xml.XML

class MarkMessageAsReadResponseSpec extends TestBaseSpec with MarkMessageAsReadFixtures {

  "xmlReader" should {
    "turn XML into a model" in {
      MarkMessageAsReadResponse.xmlReader.read(XML.loadString(markMessageAsReadChrisXml)) shouldBe ParseSuccess(markMessageAsReadResponseModel)
    }
  }

  "writes" should {
    "turn a model into JSON" in {
      Json.toJson(markMessageAsReadResponseModel) shouldBe markMessageAsReadJson
    }
  }

}