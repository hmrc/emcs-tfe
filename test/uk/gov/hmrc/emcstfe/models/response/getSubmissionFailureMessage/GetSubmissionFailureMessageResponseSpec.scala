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
import play.api.libs.json.{JsError, JsPath, JsResult, JsSuccess, Json}
import uk.gov.hmrc.emcstfe.fixtures.GetSubmissionFailureMessageFixtures
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.xml.XML

class GetSubmissionFailureMessageResponseSpec extends TestBaseSpec with GetSubmissionFailureMessageFixtures {

  import GetSubmissionFailureMessageResponseFixtures._

  "eisReads" should {
    "turn JSON into a model" in {
      GetSubmissionFailureMessageResponse.eisReads.reads(getSubmissionFailureMessageResponseDownstreamJson) shouldBe JsSuccess(getSubmissionFailureMessageResponseModel, JsPath \ "message")
    }

    "turn minimal JSON into a model" in {
      GetSubmissionFailureMessageResponse.eisReads.reads(getSubmissionFailureMessageResponseMinimumDownstreamJson) shouldBe JsSuccess(getSubmissionFailureMessageResponseMinimumModel, JsPath \ "message")
    }

    "fail" when {
      "XML is encoded with the wrong encoding" in {
        val result = intercept[JsResult.Exception](GetSubmissionFailureMessageResponse.eisReads.reads(getSubmissionFailureMessageResponseDownstreamJsonWrongEncoding))

        result.cause shouldBe JsError("Content is not allowed in prolog.")
      }
      "XML is not encoded" in {
        val result = intercept[JsResult.Exception](GetSubmissionFailureMessageResponse.eisReads.reads(getSubmissionFailureMessageResponseDownstreamJsonNotEncoded))

        result.cause shouldBe JsError("Illegal base64 character 3c")
      }
      "XML cannot be mapped to a valid model - complete nonsense" in {
        val result = intercept[JsResult.Exception](GetSubmissionFailureMessageResponse.eisReads.reads(getSubmissionFailureMessageResponseDownstreamJsonBadXml))

        result.cause shouldBe JsError("""{"obj":[{"msg":["XML failed to parse, with the following errors:\n - EmptyError(//SubmissionFailureMessageDataResponse//IE704//IE704//Header//MessageSender)\n - EmptyError(//SubmissionFailureMessageDataResponse//IE704//IE704//Header//MessageRecipient)\n - EmptyError(//SubmissionFailureMessageDataResponse//IE704//IE704//Header//DateOfPreparation)\n - EmptyError(//SubmissionFailureMessageDataResponse//IE704//IE704//Header//TimeOfPreparation)\n - EmptyError(//SubmissionFailureMessageDataResponse//IE704//IE704//Header//MessageIdentifier)\n - EmptyError(//SubmissionFailureMessageDataResponse//RelatedMessageType)"],"args":[]}]}""")
      }
    }
  }

  "chrisReads" should {
    "turn XML into a model (related message type present)" in {
      GetSubmissionFailureMessageResponse.chrisReads.read(XML.loadString(submissionFailureMessageDataXmlBody)) shouldBe ParseSuccess(getSubmissionFailureMessageResponseModel)
    }

    "turn XML into a model (without related message type present)" in {
      GetSubmissionFailureMessageResponse.chrisReads.read(XML.loadString(submissionFailureMessageDataNoRelatedMessageTypeXmlBody)) shouldBe ParseSuccess(getSubmissionFailureMessageResponseMinimumModel)
    }
  }

  "writes" should {

    Seq(true, false).foreach {
      draftMovementExists =>
        s"turn a model into JSON (is TFE Submission = $draftMovementExists)" in {
          Json.toJson(getSubmissionFailureMessageResponseModel)(GetSubmissionFailureMessageResponse.jsonWrites(draftMovementExists)) shouldBe getSubmissionFailureMessageResponseJson(draftMovementExists)
        }

        s"turn a minimal model into JSON (is TFE Submission = $draftMovementExists)" in {
          Json.toJson(getSubmissionFailureMessageResponseMinimumModel)(GetSubmissionFailureMessageResponse.jsonWrites(draftMovementExists)) shouldBe getSubmissionFailureMessageResponseMinimumJson(draftMovementExists)
        }
    }

  }

}
