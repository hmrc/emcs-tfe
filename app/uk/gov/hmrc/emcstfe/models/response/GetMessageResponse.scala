/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.models.response

import play.api.libs.json.{Json, OWrites}
import uk.gov.hmrc.emcstfe.models.common.JourneyTime.{Days, Hours}
import uk.gov.hmrc.emcstfe.models.common.JourneyTime

import scala.xml.NodeSeq

case class GetMessageResponse(
                               localReferenceNumber: String,
                               eadStatus: String,
                               consignorName: String,
                               dateOfDispatch: String,
                               journeyTime: JourneyTime,
                               numberOfItems: Int
                             )

object GetMessageResponse {
  def fromXml(xml: NodeSeq): GetMessageResponse = {
    val localReferenceNumber: String = (xml \\ "MovementDataResponse" \\ "movementView" \\ "currentMovement" \\ "IE801" \\ "Body" \\ "EADContainer" \\ "Ead" \\ "LocalReferenceNumber").text
    val eadStatus: String = (xml \\ "MovementDataResponse" \\ "movementView" \\ "currentMovement" \\ "status").text
    val consignorName: String = (xml \\ "MovementDataResponse" \\ "movementView" \\ "currentMovement" \\ "IE801" \\ "Body" \\ "EADContainer" \\ "ConsignorTrader" \\ "TraderName").text
    val dateOfDispatch: String = (xml \\ "MovementDataResponse" \\ "movementView" \\ "currentMovement" \\ "IE801" \\ "Body" \\ "EADContainer" \\ "Ead" \\ "DateOfDispatch").text
    val journeyTime: JourneyTime = {
      //rawTime format is Pdd, where P is either "H" (hours) or "D" (days) and dd is a two digit number
      //it should match the regex H([01][0-9]|2[0-4])|D([0-8][0-9]|9[0-2])
      val rawTime = (xml \\ "MovementDataResponse" \\ "movementView" \\ "currentMovement" \\ "IE801" \\ "Body" \\ "EADContainer" \\ "HeaderEad" \\ "JourneyTime").text
      val (unit, number) = rawTime.splitAt(1)
      unit match {
        case "H" => Hours(number)
        case "D" => Days(number)
      }
    }
    val numberOfItems: Int = (xml \\ "MovementDataResponse" \\ "movementView" \\ "currentMovement" \\ "IE801" \\ "Body" \\ "EADContainer" \\ "BodyEad" \\ "CnCode").map(_.text).distinct.length
    GetMessageResponse(
      localReferenceNumber = localReferenceNumber,
      eadStatus = eadStatus,
      consignorName = consignorName,
      dateOfDispatch = dateOfDispatch,
      journeyTime = journeyTime,
      numberOfItems = numberOfItems
    )
  }

  implicit val writes: OWrites[GetMessageResponse] = Json.writes
}
