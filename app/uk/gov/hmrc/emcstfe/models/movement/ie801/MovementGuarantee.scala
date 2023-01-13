package uk.gov.hmrc.emcstfe.models.movement.ie801

import play.api.libs.json.{Json, OWrites}

import scala.xml.NodeSeq

case class MovementGuarantee(
                              guarantorTypeCode: String,
                              guarantorTrader: Option[Seq[GuarantorTrader]]
                            )

object MovementGuarantee {
  def fromXml(xml: NodeSeq): MovementGuarantee = {
    val guarantorTypeCode: String = (xml \\ "GuarantorTypeCode").text
    val guarantorTrader: Option[Seq[GuarantorTrader]] = {
      val nodeSeq = (xml \\ "GuarantorTrader")
      if(nodeSeq.length > 0) {
        Some(nodeSeq.map(GuarantorTrader.fromXml))
      } else None
    }
    MovementGuarantee(
      guarantorTypeCode = guarantorTypeCode,
      guarantorTrader = guarantorTrader
    )
  }

  implicit val writes: OWrites[MovementGuarantee] = Json.writes
}
