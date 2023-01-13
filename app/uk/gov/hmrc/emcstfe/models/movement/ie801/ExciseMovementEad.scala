package uk.gov.hmrc.emcstfe.models.movement.ie801

import play.api.libs.json.{Json, OWrites}

import scala.xml.NodeSeq

case class ExciseMovementEad(arc: String, dateAndTimeOfValidationOfEad: String)

object ExciseMovementEad {
  def fromXml(xml: NodeSeq): ExciseMovementEad = {
    val arc = (xml \\ "AdministrativeReferenceCode").text
    val dateAndTimeOfValidationOfEad = (xml \\ "DateAndTimeOfValidationOfEad").text
    ExciseMovementEad(arc = arc, dateAndTimeOfValidationOfEad = dateAndTimeOfValidationOfEad)
  }
  implicit val writes: OWrites[ExciseMovementEad] = Json.writes
}
