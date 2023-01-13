package uk.gov.hmrc.emcstfe.models.movement.ie801

import play.api.libs.json.{Json, OWrites}

import scala.xml.NodeSeq

case class DispatchImportOffice(referenceNumber: String)

object DispatchImportOffice {
  def fromXml(xml: NodeSeq): DispatchImportOffice = DispatchImportOffice(referenceNumber = (xml \\ "ReferenceNumber").text)

  implicit val writes: OWrites[DispatchImportOffice] = Json.writes
}
