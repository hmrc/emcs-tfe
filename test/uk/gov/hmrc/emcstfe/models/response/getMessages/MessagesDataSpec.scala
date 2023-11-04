package uk.gov.hmrc.emcstfe.models.response.getMessages

import com.lucidchart.open.xtract.ParseSuccess
import play.api.libs.json.Json
import uk.gov.hmrc.emcstfe.fixtures.GetMessagesFixtures
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.xml.XML

class MessagesDataSpec extends TestBaseSpec with GetMessagesFixtures {

  import MessagesDataFixtures._

  "reads" should {
    "turn XML into a model" in {
      MessagesData.xmlReader.read(XML.loadString(messagesDataXmlBody)) shouldBe ParseSuccess(messagesDataModel)
    }

    "turn minimal XML into a model" in {
      MessagesData.xmlReader.read(XML.loadString(messagesDataMinimumXmlBody)) shouldBe ParseSuccess(messagesDataMinimumModel)
    }
  }

  "writes" should {
    "turn a model into JSON" in {
      Json.toJson(messagesDataModel) shouldBe messagesDataJson
    }

    "turn a minimal model into JSON" in {
      Json.toJson(messagesDataMinimumModel) shouldBe messagesDataMinimumJson
    }
  }

}
