package uk.gov.hmrc.emcstfe.models.response.getMessages

import com.lucidchart.open.xtract.ParseSuccess
import play.api.libs.json.{JsError, JsPath, JsResult, JsSuccess, Json, JsonValidationError}
import uk.gov.hmrc.emcstfe.fixtures.GetMessagesFixtures
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.xml.XML

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
      "XML cannot be mapped to a valid model" in {
        val result = intercept[JsResult.Exception](GetMessagesResponse.reads.reads(getMessagesResponseDownstreamJsonBadXml))

        result.cause shouldBe JsError("""{"obj":[{"msg":["XML failed to parse, with the following errors:\n - EmptyError(//MessagesDataResponse//TotalNumberOfMessagesAvailable)"],"args":[]}]}""")
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
