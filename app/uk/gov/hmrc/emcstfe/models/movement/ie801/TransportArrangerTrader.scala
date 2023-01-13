package uk.gov.hmrc.emcstfe.models.movement.ie801

import play.api.libs.json.{Json, OWrites}

import scala.xml.NodeSeq

case class TransportArrangerTrader(
                                    vatNumber: Option[String],
                                    traderName: String,
                                    streetName: String,
                                    streetNumber: Option[String],
                                    postcode: String,
                                    city: String
                                  )

object TransportArrangerTrader {
  def fromXml(xml: NodeSeq): TransportArrangerTrader = {
    val vatNumber: Option[String] = (xml \\ "VatNumber").headOption.map(_.text)
    val traderName: String = (xml \\ "TraderName").text
    val streetName: String = (xml \\ "StreetName").text
    val streetNumber: Option[String] = (xml \\ "StreetNumber").headOption.map(_.text)
    val postcode: String = (xml \\ "Postcode").text
    val city: String = (xml \\ "City").text
    TransportArrangerTrader(
      vatNumber = vatNumber,
      traderName = traderName,
      streetName = streetName,
      streetNumber = streetNumber,
      postcode = postcode,
      city = city
    )
  }

  implicit val writes: OWrites[TransportArrangerTrader] = Json.writes
}
