package uk.gov.hmrc.emcstfe.models.movement.ie801

import play.api.libs.json.{Json, OWrites}

import scala.xml.NodeSeq

case class WineProduct(
                        wineProductCategory: String,
                        wineGrowingZoneCode: Option[String],
                        thirdCountryOfOrigin: Option[String],
                        otherInformation: Option[String],
                        wineOperationCode: Option[Seq[String]]
                      )

object WineProduct {
  def fromXml(xml: NodeSeq): WineProduct = {
    val wineProductCategory: String = (xml \\ "WineProductCategory").text
    val wineGrowingZoneCode: Option[String] = (xml \\ "WineGrowingZoneCode").headOption.map(_.text)
    val thirdCountryOfOrigin: Option[String] = (xml \\ "ThirdCountryOfOrigin").headOption.map(_.text)
    val otherInformation: Option[String] = (xml \\ "OtherInformation").headOption.map(_.text)
    val wineOperationCode: Option[Seq[String]] = {
      val nodeSeq = xml \\ "WineOperation"
      if(nodeSeq.length > 0) {
        Some(nodeSeq.map(el => (el \\ "WineOperationCode").text))
      } else None
    }
    WineProduct(
      wineProductCategory = wineProductCategory,
      wineGrowingZoneCode = wineGrowingZoneCode,
      thirdCountryOfOrigin = thirdCountryOfOrigin,
      otherInformation = otherInformation,
      wineOperationCode = wineOperationCode
    )
  }

  implicit val writes: OWrites[WineProduct] = Json.writes
}
