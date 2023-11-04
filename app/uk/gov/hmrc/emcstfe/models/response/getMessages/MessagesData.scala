package uk.gov.hmrc.emcstfe.models.response.getMessages

import cats.implicits.catsSyntaxTuple2Semigroupal
import com.lucidchart.open.xtract.XmlReader.strictReadSeq
import com.lucidchart.open.xtract.{XPath, XmlReader, __}
import play.api.libs.json.{Json, OWrites}

case class MessagesData(
                         messages: Seq[Message],
                         totalNumberOfMessagesAvailable: Long
                       )

object MessagesData {
  implicit val writes: OWrites[MessagesData] = Json.writes

  private val message: XPath = __ \\ "MessagesDataResponse" \\ "Message"
  private val totalNumberOfMessagesAvailable: XPath = __ \\ "MessagesDataResponse" \\ "TotalNumberOfMessagesAvailable"

  implicit val xmlReader: XmlReader[MessagesData] = (
    message.read[Seq[Message]](strictReadSeq),
    totalNumberOfMessagesAvailable.read[Long],
  ).mapN(MessagesData.apply)
}
