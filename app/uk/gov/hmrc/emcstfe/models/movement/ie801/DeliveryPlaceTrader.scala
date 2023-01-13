package uk.gov.hmrc.emcstfe.models.movement.ie801

import play.api.libs.json.{Json, OWrites}

import scala.xml.NodeSeq

case class DeliveryPlaceTrader(
                                traderId: Option[String],
                                traderName: Option[String],
                                streetName: Option[String],
                                streetNumber: Option[String],
                                postcode: Option[String],
                                city: Option[String]
                              )

object DeliveryPlaceTrader {
  def fromXml(xml: NodeSeq): DeliveryPlaceTrader = {
    val traderId: Option[String] = (xml \\ "Traderid").headOption.map(_.text)
    val traderName: Option[String] = (xml \\ "TraderName").headOption.map(_.text)
    val streetName: Option[String] = (xml \\ "StreetName").headOption.map(_.text)
    val streetNumber: Option[String] = (xml \\ "StreetNumber").headOption.map(_.text)
    val postcode: Option[String] = (xml \\ "Postcode").headOption.map(_.text)
    val city: Option[String] = (xml \\ "City").headOption.map(_.text)
    DeliveryPlaceTrader(
      traderId = traderId,
      traderName = traderName,
      streetName = streetName,
      streetNumber = streetNumber,
      postcode = postcode,
      city = city
    )
  }

  implicit val writes: OWrites[DeliveryPlaceTrader] = Json.writes
}
