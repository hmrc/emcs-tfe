package uk.gov.hmrc.emcstfe.models.movement.ie801

import play.api.libs.json.{Json, OWrites}

import scala.xml.NodeSeq

case class GuarantorTrader(
                            traderExciseNumber: Option[String],
                            traderName: Option[String],
                            streetName: Option[String],
                            streetNumber: Option[String],
                            city: Option[String],
                            postcode: Option[String],
                            vatNumber: Option[String]
                          )

object GuarantorTrader {
  def fromXml(xml: NodeSeq): GuarantorTrader = {
    val traderExciseNumber: Option[String] = (xml \\ "TraderExciseNumber").headOption.map(_.text)
    val traderName: Option[String] = (xml \\ "TraderName").headOption.map(_.text)
    val streetName: Option[String] = (xml \\ "StreetName").headOption.map(_.text)
    val streetNumber: Option[String] = (xml \\ "StreetNumber").headOption.map(_.text)
    val city: Option[String] = (xml \\ "City").headOption.map(_.text)
    val postcode: Option[String] = (xml \\ "Postcode").headOption.map(_.text)
    val vatNumber: Option[String] = (xml \\ "VatNumber").headOption.map(_.text)
    GuarantorTrader(
      traderExciseNumber = traderExciseNumber,
      traderName = traderName,
      streetName = streetName,
      streetNumber = streetNumber,
      city = city,
      postcode = postcode,
      vatNumber = vatNumber
    )
  }

  implicit val writes: OWrites[GuarantorTrader] = Json.writes
}
