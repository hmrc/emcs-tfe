package uk.gov.hmrc.emcstfe.models.movement.ie801

import play.api.libs.json.{Json, OWrites}

import scala.xml.NodeSeq

case class DeliveryPlaceCustomsOffice(referenceNumber: String)

object DeliveryPlaceCustomsOffice {
  def fromXml(xml: NodeSeq): DeliveryPlaceCustomsOffice = DeliveryPlaceCustomsOffice(referenceNumber = (xml \\ "ReferenceNumber").text)

  implicit val writes: OWrites[DeliveryPlaceCustomsOffice] = Json.writes
}
