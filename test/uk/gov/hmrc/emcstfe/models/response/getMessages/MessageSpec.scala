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
