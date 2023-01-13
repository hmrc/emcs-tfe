package uk.gov.hmrc.emcstfe.models.movement.ie801

import play.api.libs.json.{Json, OWrites}

import scala.xml.NodeSeq

case class IE801Message(header: IE801Header, body: IE801Body)

object IE801Message {

  def fromXml(xml: NodeSeq): IE801Message = {
    val header = IE801Header.fromXml(xml \\ "Header")
    val body = IE801Body.fromXml(xml \\ "Body")
    IE801Message(header = header, body = body)
  }

  implicit val writes: OWrites[IE801Message] = Json.writes
}
