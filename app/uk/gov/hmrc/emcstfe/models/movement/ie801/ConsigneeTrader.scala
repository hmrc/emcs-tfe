package uk.gov.hmrc.emcstfe.models.movement.ie801

import play.api.libs.json.{Json, OWrites}

import scala.xml.NodeSeq

case class ConsigneeTrader(
                            traderId: Option[String],
                            traderName: String,
                            streetName: String,
                            streetNumber: Option[String],
                            postcode: String,
                            city: String,
                            eoriNumber: Option[String]
                          )

object ConsigneeTrader {
  def fromXml(xml: NodeSeq): ConsigneeTrader = {
    val traderId: Option[String] = (xml \\ "Traderid").headOption.map(_.text)
    val traderName: String = (xml \\ "TraderName").text
    val streetName: String = (xml \\ "StreetName").text
    val streetNumber: Option[String] = (xml \\ "StreetNumber").headOption.map(_.text)
    val postcode: String = (xml \\ "Postcode").text
    val city: String = (xml \\ "City").text
    val eoriNumber: Option[String] = (xml \\ "EoriNumber").headOption.map(_.text)
    ConsigneeTrader(
      traderId = traderId,
      traderName = traderName,
      streetName = streetName,
      streetNumber = streetNumber,
      postcode = postcode,
      city = city,
      eoriNumber = eoriNumber
    )
  }

  implicit val writes: OWrites[ConsigneeTrader] = Json.writes
}
