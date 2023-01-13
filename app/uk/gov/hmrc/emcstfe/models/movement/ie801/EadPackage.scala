package uk.gov.hmrc.emcstfe.models.movement.ie801

import play.api.libs.json.{Json, OWrites}

import scala.xml.NodeSeq

case class EadPackage(
                       kindOfPackages: String,
                       numberOfPackages: Option[String],
                       shippingMarks: Option[String],
                       commercialSealIdentification: Option[String],
                       sealInformation: Option[String]
                     )

object EadPackage {
  def fromXml(xml: NodeSeq): EadPackage = {
    val kindOfPackages: String = (xml \\ "KindOfPackages").text
    val numberOfPackages: Option[String] = (xml \\ "NumberOfPackages").headOption.map(_.text)
    val shippingMarks: Option[String] = (xml \\ "ShippingMarks").headOption.map(_.text)
    val commercialSealIdentification: Option[String] = (xml \\ "CommercialSealIdentification").headOption.map(_.text)
    val sealInformation: Option[String] = (xml \\ "SealInformation").headOption.map(_.text)
    EadPackage(
      kindOfPackages = kindOfPackages,
      numberOfPackages = numberOfPackages,
      shippingMarks = shippingMarks,
      commercialSealIdentification = commercialSealIdentification,
      sealInformation = sealInformation
    )
  }

  implicit val writes: OWrites[EadPackage] = Json.writes
}
