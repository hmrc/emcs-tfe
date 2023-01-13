package uk.gov.hmrc.emcstfe.models.movement.ie801

import play.api.libs.json.{Json, OWrites}

import scala.xml.NodeSeq

case class TransportDetails(
                             transportUnitCode: String,
                             identityOfTransportUnits: Option[String],
                             commercialSealIdentification: Option[String],
                             complementaryInformation: Option[String],
                             sealInformation: Option[String]
                           )

object TransportDetails {
  def fromXml(xml: NodeSeq): TransportDetails = {
    val transportUnitCode: String = (xml \\ "TransportUnitCode").text
    val identityOfTransportUnits: Option[String] = (xml \\ "IdentityOfTransportUnits").headOption.map(_.text)
    val commercialSealIdentification: Option[String] = (xml \\ "CommercialSealIdentification").headOption.map(_.text)
    val complementaryInformation: Option[String] = (xml \\ "ComplementaryInformation").headOption.map(_.text)
    val sealInformation: Option[String] = (xml \\ "SealInformation").headOption.map(_.text)
    TransportDetails(
      transportUnitCode = transportUnitCode,
      identityOfTransportUnits = identityOfTransportUnits,
      commercialSealIdentification = commercialSealIdentification,
      complementaryInformation = complementaryInformation,
      sealInformation = sealInformation
    )
  }

  implicit val writes: OWrites[TransportDetails] = Json.writes
}
