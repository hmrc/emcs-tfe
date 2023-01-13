package uk.gov.hmrc.emcstfe.models.movement.ie801

import play.api.libs.json.{Json, OWrites}

import scala.xml.NodeSeq

case class ComplementConsigneeTrader(memberStateCode: String, serialNumberOfCertificateOfExemption: Option[String])

object ComplementConsigneeTrader {
  def fromXml(xml: NodeSeq): ComplementConsigneeTrader = {
    val memberStateCode: String = (xml \\ "MemberStateCode").text
    val serialNumberOfCertificateOfExemption: Option[String] = (xml \\ "SerialNumberOfCertificateOfExemption").headOption.map(_.text)
    ComplementConsigneeTrader(memberStateCode = memberStateCode, serialNumberOfCertificateOfExemption = serialNumberOfCertificateOfExemption)
  }

  implicit val writes: OWrites[ComplementConsigneeTrader] = Json.writes
}
