/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.models.response.getMessage

import play.api.libs.json.{Json, OWrites}

import scala.xml.NodeSeq

case class GetMessageResponse(currentMovement: CurrentMovement, eventHistory: EventHistory)

object GetMessageResponse {
  private def fromXml(xml: NodeSeq): GetMessageResponse = {
    GetMessageResponse(
      currentMovement = CurrentMovement.fromXml(xml \\ "MovementDataResponse" \\ "movementView" \\ "currentMovement"),
      eventHistory = EventHistory.fromXml(xml \\ "MovementDataResponse" \\ "movementView" \\ "eventHistory")
    )
  }

  implicit val writes: OWrites[GetMessageResponse] = Json.writes
}
