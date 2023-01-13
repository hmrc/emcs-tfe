package uk.gov.hmrc.emcstfe.models.response.getMessage

import play.api.libs.json.{Json, OWrites}

import scala.xml.NodeSeq

case class EventHistory(thing: String)

object EventHistory {
  def fromXml(xml: NodeSeq): EventHistory = EventHistory("")

  implicit val writes: OWrites[EventHistory] = Json.writes
}
