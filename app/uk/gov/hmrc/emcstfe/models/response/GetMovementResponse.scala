/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.models.response

import cats.implicits.catsSyntaxTuple6Semigroupal
import com.lucidchart.open.xtract.XmlReader.strictReadSeq
import com.lucidchart.open.xtract.{XPath, XmlReader, __}
import play.api.libs.json.{Json, OWrites}
import uk.gov.hmrc.emcstfe.models.common.JourneyTime

case class GetMovementResponse(
                               localReferenceNumber: String,
                               eadStatus: String,
                               consignorName: String,
                               dateOfDispatch: String,
                               journeyTime: JourneyTime,
                               numberOfItems: Int
                             )

object GetMovementResponse {

  val currentMovement: XPath = __ \ "movementView" \ "currentMovement"
  val eadStatus: XPath = currentMovement \ "status"
  val EADESADContainer: XPath = currentMovement \ "IE801" \ "Body" \ "EADESADContainer"
  val localReferenceNumber: XPath = EADESADContainer \ "EadEsad" \ "LocalReferenceNumber"
  val consignorName: XPath = EADESADContainer \ "ConsignorTrader" \ "TraderName"
  val dateOfDispatch: XPath = EADESADContainer \ "EadEsad" \ "DateOfDispatch"
  val journeyTime: XPath = EADESADContainer \ "HeaderEadEsad" \ "JourneyTime"
  val numberOfItems: XPath = EADESADContainer \\ "BodyEadEsad" \\ "CnCode"

  implicit val xmlReader: XmlReader[GetMovementResponse] = (
    localReferenceNumber.read[String],
    eadStatus.read[String],
    consignorName.read[String],
    dateOfDispatch.read[String],
    journeyTime.read[JourneyTime],
    numberOfItems.read[Seq[String]](strictReadSeq).map(_.distinct.length)
  ).mapN(GetMovementResponse.apply)

  implicit val writes: OWrites[GetMovementResponse] = Json.writes
}
