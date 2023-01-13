package uk.gov.hmrc.emcstfe.models.movement.ie801

import play.api.libs.json.{Json, OWrites}

import scala.xml.NodeSeq

case class HeaderEad(
                      sequenceNumber: String,
                      dateAndTimeOfUpdateValidation: String,
                      destinationTypeCode: String,
                      journeyTime: String,
                      transportArrangement: String
                    )

object HeaderEad {
  def fromXml(xml: NodeSeq): HeaderEad = {
    val sequenceNumber: String = (xml \\ "SequenceNumber").text
    val dateAndTimeOfUpdateValidation: String = (xml \\ "DateAndTimeOfUpdateValidation").text
    val destinationTypeCode: String = (xml \\ "DestinationTypeCode").text
    val journeyTime: String = (xml \\ "JourneyTime").text
    val transportArrangement: String = (xml \\ "TransportArrangement").text
    HeaderEad(
      sequenceNumber = sequenceNumber,
      dateAndTimeOfUpdateValidation = dateAndTimeOfUpdateValidation,
      destinationTypeCode = destinationTypeCode,
      journeyTime = journeyTime,
      transportArrangement = transportArrangement
    )
  }

  implicit val writes: OWrites[HeaderEad] = Json.writes
}
