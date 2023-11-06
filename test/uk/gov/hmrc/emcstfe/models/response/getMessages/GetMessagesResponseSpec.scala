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

import play.api.libs.json.{JsError, JsResult, JsSuccess, Json}
import uk.gov.hmrc.emcstfe.fixtures.GetMessagesFixtures
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

class GetMessagesResponseSpec extends TestBaseSpec with GetMessagesFixtures {

  import GetMessagesResponseFixtures._

  "reads" should {
    "turn JSON into a model" in {
      GetMessagesResponse.reads.reads(getMessagesResponseDownstreamJson) shouldBe JsSuccess(getMessagesResponseModel)
    }

    "turn minimal JSON into a model" in {
      GetMessagesResponse.reads.reads(getMessagesResponseMinimumDownstreamJson) shouldBe JsSuccess(getMessagesResponseMinimumModel)
    }

    "fail" when {
      "XML is encoded with the wrong encoding" in {
        val result = intercept[JsResult.Exception](GetMessagesResponse.reads.reads(getMessagesResponseDownstreamJsonWrongEncoding))

        result.cause shouldBe JsError("Content is not allowed in prolog.")
      }
      "XML is not encoded" in {
        val result = intercept[JsResult.Exception](GetMessagesResponse.reads.reads(getMessagesResponseDownstreamJsonNotEncoded))

        result.cause shouldBe JsError("Illegal base64 character 3c")
      }
      "XML cannot be mapped to a valid model - complete nonsense" in {
        val result = intercept[JsResult.Exception](GetMessagesResponse.reads.reads(getMessagesResponseDownstreamJsonBadXml))

        result.cause shouldBe JsError("""{"obj":[{"msg":["XML failed to parse, with the following errors:\n - EmptyError(//MessagesDataResponse//TotalNumberOfMessagesAvailable)"],"args":[]}]}""")
      }
      "XML cannot be mapped to a valid model - partial failure" in {
        val result = intercept[JsResult.Exception](GetMessagesResponse.reads.reads(getMessagesResponseDownstreamJsonPartiallyBadXml))

        result.cause shouldBe JsError("""{"obj":[{"msg":["XML failed to parse, with the following errors:\n - EmptyError(//MessagesDataResponse//Message//DateCreatedOnCore)\n - EmptyError(//MessagesDataResponse//Message//MessageType)\n - EmptyError(//MessagesDataResponse//Message//RelatedMessageType)\n - EmptyError(//MessagesDataResponse//Message//ReadIndicator)\n - EmptyError(//MessagesDataResponse//Message//MessageRole)\n - EmptyError(//MessagesDataResponse//Message//SubmittedByRequestingTrader)"],"args":[]}]}""")
      }
    }
  }

  "writes" should {
    "turn a model into JSON" in {
      Json.toJson(getMessagesResponseModel) shouldBe getMessagesResponseJson
    }

    "turn a minimal model into JSON" in {
      Json.toJson(getMessagesResponseMinimumModel) shouldBe getMessagesResponseMinimumJson
    }
  }

}
