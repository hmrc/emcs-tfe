package uk.gov.hmrc.emcstfe.models.movement.ie801

import play.api.libs.json.{Json, OWrites}

import scala.xml.NodeSeq

case class CompetentAuthorityDispatchOffice(referenceNumber: String)

object CompetentAuthorityDispatchOffice {
  def fromXml(xml: NodeSeq): CompetentAuthorityDispatchOffice = CompetentAuthorityDispatchOffice(referenceNumber = (xml \\ "ReferenceNumber").text)

  implicit val writes: OWrites[CompetentAuthorityDispatchOffice] = Json.writes
}
