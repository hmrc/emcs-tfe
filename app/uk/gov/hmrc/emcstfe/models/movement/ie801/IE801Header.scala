package uk.gov.hmrc.emcstfe.models.movement.ie801

import play.api.libs.json.{Json, OWrites}

import scala.xml.NodeSeq

case class IE801Header(
                        messageSender: String,
                        messageRecipient: String,
                        dateOfPreparation: String,
                        timeOfPreparation: String,
                        messageIdentifier: String,
                        correlationIdentifier: Option[String]
                      )

object IE801Header {
  def fromXml(xml: NodeSeq): IE801Header = {
    val messageSender: String = (xml \\ "MessageSender").text
    val messageRecipient: String = (xml \\ "MessageRecipient").text
    val dateOfPreparation: String = (xml \\ "DateOfPreparation").text
    val timeOfPreparation: String = (xml \\ "TimeOfPreparation").text
    val messageIdentifier: String = (xml \\ "MessageIdentifier").text
    val correlationIdentifier: Option[String] = (xml \\ "CorrelationIdentifier").headOption.map(_.text)
    IE801Header(
      messageSender = messageSender,
      messageRecipient = messageRecipient,
      dateOfPreparation = dateOfPreparation,
      timeOfPreparation = timeOfPreparation,
      messageIdentifier = messageIdentifier,
      correlationIdentifier = correlationIdentifier
    )
  }

  implicit val writes: OWrites[IE801Header] = Json.writes
}
