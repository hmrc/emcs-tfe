package uk.gov.hmrc.emcstfe.models.movement.ie801

import play.api.libs.json.{Json, OWrites}

import scala.xml.NodeSeq

case class PlaceOfDispatchTrader(
                                  referenceOfTaxWarehouse: String,
                                  traderName: Option[String],
                                  streetName: Option[String],
                                  streetNumber: Option[String],
                                  postcode: Option[String],
                                  city: Option[String]
                                )

object PlaceOfDispatchTrader {
  def fromXml(xml: NodeSeq): PlaceOfDispatchTrader = {
    val referenceOfTaxWarehouse: String = (xml \\ "ReferenceOfTaxWarehouse").text
    val traderName: Option[String] = (xml \\ "TraderName").headOption.map(_.text)
    val streetName: Option[String] = (xml \\ "StreetName").headOption.map(_.text)
    val streetNumber: Option[String] = (xml \\ "StreetNumber").headOption.map(_.text)
    val postcode: Option[String] = (xml \\ "Postcode").headOption.map(_.text)
    val city: Option[String] = (xml \\ "City").headOption.map(_.text)
    PlaceOfDispatchTrader(
      referenceOfTaxWarehouse = referenceOfTaxWarehouse,
      traderName = traderName,
      streetName = streetName,
      streetNumber = streetNumber,
      postcode = postcode,
      city = city
    )
  }

  implicit val writes: OWrites[PlaceOfDispatchTrader] = Json.writes
}
