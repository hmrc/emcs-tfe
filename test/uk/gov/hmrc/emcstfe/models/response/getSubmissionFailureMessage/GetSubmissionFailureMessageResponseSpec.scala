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

import play.api.libs.json.{JsError, JsResult, JsSuccess, Json}
import uk.gov.hmrc.emcstfe.fixtures.GetSubmissionFailureMessageFixtures
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

class GetSubmissionFailureMessageResponseSpec extends TestBaseSpec with GetSubmissionFailureMessageFixtures {

  import GetSubmissionFailureMessageResponseFixtures._

  "reads" should {
    "turn JSON into a model" in {
      GetSubmissionFailureMessageResponse.reads.reads(getSubmissionFailureMessageResponseDownstreamJson) shouldBe JsSuccess(getSubmissionFailureMessageResponseModel)
    }

    "turn minimal JSON into a model" in {
      GetSubmissionFailureMessageResponse.reads.reads(getSubmissionFailureMessageResponseMinimumDownstreamJson) shouldBe JsSuccess(getSubmissionFailureMessageResponseMinimumModel)
    }

    "fail" when {
      "XML is encoded with the wrong encoding" in {
        val result = intercept[JsResult.Exception](GetSubmissionFailureMessageResponse.reads.reads(getSubmissionFailureMessageResponseDownstreamJsonWrongEncoding))

        result.cause shouldBe JsError("Content is not allowed in prolog.")
      }
      "XML is not encoded" in {
        val result = intercept[JsResult.Exception](GetSubmissionFailureMessageResponse.reads.reads(getSubmissionFailureMessageResponseDownstreamJsonNotEncoded))

        result.cause shouldBe JsError("Illegal base64 character 3c")
      }
      "XML cannot be mapped to a valid model - complete nonsense" in {
        val result = intercept[JsResult.Exception](GetSubmissionFailureMessageResponse.reads.reads(getSubmissionFailureMessageResponseDownstreamJsonBadXml))

        result.cause shouldBe JsError("""{"obj":[{"msg":["XML failed to parse, with the following errors:\n - EmptyError(//SubmissionFailureMessageDataResponse//IE704//IE704//Header//MessageSender)\n - EmptyError(//SubmissionFailureMessageDataResponse//IE704//IE704//Header//MessageRecipient)\n - EmptyError(//SubmissionFailureMessageDataResponse//IE704//IE704//Header//DateOfPreparation)\n - EmptyError(//SubmissionFailureMessageDataResponse//IE704//IE704//Header//TimeOfPreparation)\n - EmptyError(//SubmissionFailureMessageDataResponse//IE704//IE704//Header//MessageIdentifier)\n - EmptyError(//SubmissionFailureMessageDataResponse//RelatedMessageType)"],"args":[]}]}""")
      }
    }
  }

  "writes" should {
    "turn a model into JSON" in {
      Json.toJson(getSubmissionFailureMessageResponseModel) shouldBe getSubmissionFailureMessageResponseJson
    }

    "turn a minimal model into JSON" in {
      Json.toJson(getSubmissionFailureMessageResponseMinimumModel) shouldBe getSubmissionFailureMessageResponseMinimumJson
    }
  }

}