package uk.gov.hmrc.emcstfe.models.response.getMessage

import play.api.libs.json.{Json, OWrites}
import uk.gov.hmrc.emcstfe.models.movement.ie801.IE801Message

import scala.xml.NodeSeq

case class CurrentMovement(status: String, versionTransactionRef: String, body: IE801Message)

object CurrentMovement {
  def fromXml(xml: NodeSeq): CurrentMovement = {
    val status = (xml \\ "status").text
    val versionTransactionRef = (xml \\ "version_transaction_ref").text
    val body = IE801Message.fromXml(xml \\ "IE801")
    CurrentMovement(status = status, versionTransactionRef = versionTransactionRef, body = body)
  }

  implicit val writes: OWrites[CurrentMovement] = Json.writes
}
