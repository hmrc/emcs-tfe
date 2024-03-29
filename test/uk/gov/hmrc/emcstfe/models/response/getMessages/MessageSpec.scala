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

package uk.gov.hmrc.emcstfe.models.response.getMessages

import com.lucidchart.open.xtract.ParseSuccess
import play.api.libs.json.Json
import uk.gov.hmrc.emcstfe.fixtures.GetMessagesFixtures
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.xml.XML

class MessageSpec extends TestBaseSpec with GetMessagesFixtures {

  import MessageFixtures._

  "reads" should {
    "turn XML into a model" in {
      Message.xmlReader.read(XML.loadString(messageXmlBody)) shouldBe ParseSuccess(messageModel)
    }

    "turn minimal XML into a model" in {
      Message.xmlReader.read(XML.loadString(messageMinimumXmlBody)) shouldBe ParseSuccess(messageMinimumModel)
    }
  }

  "writes" should {
    "turn a model into JSON" in {
      Json.toJson(messageModel) shouldBe messageJson
    }

    "turn a minimal model into JSON" in {
      Json.toJson(messageMinimumModel) shouldBe messageMinimumJson
    }
  }

}
