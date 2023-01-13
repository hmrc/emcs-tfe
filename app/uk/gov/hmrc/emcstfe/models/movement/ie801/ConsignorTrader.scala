package uk.gov.hmrc.emcstfe.models.movement.ie801

import play.api.libs.json.{Json, OWrites}

import scala.xml.NodeSeq

case class ConsignorTrader(
                            traderExciseNumber: String,
                            traderName: String,
                            streetName: String,
                            streetNumber: Option[String],
                            postcode: String,
                            city: String
                          )

object ConsignorTrader {
  def fromXml(xml: NodeSeq): ConsignorTrader = {
    val traderExciseNumber: String = (xml \\ "TraderExciseNumber").text
    val traderName: String = (xml \\ "TraderName").text
    val streetName: String = (xml \\ "StreetName").text
    val streetNumber: Option[String] = (xml \\ "StreetNumber").headOption.map(_.text)
    val postcode: String = (xml \\ "Postcode").text
    val city: String = (xml \\ "City").text
    ConsignorTrader(
      traderExciseNumber = traderExciseNumber,
      traderName = traderName,
      streetName = streetName,
      streetNumber = streetNumber,
      postcode = postcode,
      city = city
    )
  }

  implicit val writes: OWrites[ConsignorTrader] = Json.writes
}
