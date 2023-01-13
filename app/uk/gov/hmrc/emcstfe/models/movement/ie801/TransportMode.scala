package uk.gov.hmrc.emcstfe.models.movement.ie801

import play.api.libs.json.{Json, OWrites}

import scala.xml.NodeSeq

case class TransportMode(
                          transportModeCode: String,
                          complementaryInformation: Option[String]
                        )

object TransportMode {
  def fromXml(xml: NodeSeq): TransportMode = {
    val transportModeCode: String = (xml \\ "TransportModeCode").text
    val complementaryInformation: Option[String] = (xml \\ "ComplementaryInformation").headOption.map(_.text)
    TransportMode(
      transportModeCode = transportModeCode,
      complementaryInformation = complementaryInformation
    )
  }

  implicit val writes: OWrites[TransportMode] = Json.writes
}
