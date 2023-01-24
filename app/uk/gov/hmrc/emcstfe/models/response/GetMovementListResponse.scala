/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.models.response

import play.api.libs.json.{Json, Writes}

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDateTime}
import scala.xml.NodeSeq

case class GetMovementListResponse(movements: Seq[GetMovementListItem], count: Int)

object GetMovementListResponse {

  def apply(xml: NodeSeq): GetMovementListResponse = {

    val movements = xml \\ "Movement"

    GetMovementListResponse(
      movements.map { movement =>
        GetMovementListItem(
          (movement \\ "Arc").text,
          LocalDateTime.parse((movement \\ "DateOfDispatch").text),
          (movement \\ "MovementStatus").text,
          (movement \\ "OtherTraderID").text
        )
      },
      (xml \\ "CountOfMovementsAvailable").text.toInt
    )
  }

  implicit val writes: Writes[GetMovementListResponse] = Json.writes
}