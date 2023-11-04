package uk.gov.hmrc.emcstfe.models.response.getMessages

import cats.implicits.catsSyntaxTuple10Semigroupal
import com.lucidchart.open.xtract.{XPath, XmlReader, __}
import play.api.libs.json.{Json, OWrites}

case class Message(
                    uniqueMessageIdentifier: Long,
                    dateCreatedOnCore: String,
                    arc: Option[String],
                    messageType: String,
                    relatedMessageType: Option[String],
                    sequenceNumber: Option[Int],
                    readIndicator: Boolean,
                    lrn: Option[String],
                    messageRole: Int,
                    submittedByRequestingTrader: Boolean
                  )

object Message {
  implicit val writes: OWrites[Message] = Json.writes

  private val uniqueMessageIdentifier: XPath = __ \\ "UniqueMessageIdentifier"
  private val dateCreatedOnCore: XPath = __ \\ "DateCreatedOnCore"
  private val arc: XPath = __ \\ "Arc"
  private val messageType: XPath = __ \\ "MessageType"
  private val relatedMessageType: XPath = __ \\ "RelatedMessageType"
  private val sequenceNumber: XPath = __ \\ "SequenceNumber"
  private val readIndicator: XPath = __ \\ "ReadIndicator"
  private val lrn: XPath = __ \\ "LRN"
  private val messageRole: XPath = __ \\ "MessageRole"
  private val submittedByRequestingTrader: XPath = __ \\ "SubmittedByRequestingTrader"

  implicit val xmlReader: XmlReader[Message] = (
    uniqueMessageIdentifier.read[Long],
    dateCreatedOnCore.read[String],
    arc.read[Option[String]],
    messageType.read[String],
    relatedMessageType.read[String].map(s => if (s.nonEmpty) Some(s) else None),
    sequenceNumber.read[Option[Int]],
    readIndicator.read[Boolean],
    lrn.read[Option[String]],
    messageRole.read[Int],
    submittedByRequestingTrader.read[Boolean]
  ).mapN(Message.apply)
}
