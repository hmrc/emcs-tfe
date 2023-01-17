/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.models.response

import play.api.libs.json.{Json, Writes}

import java.time.Instant
import scala.xml.NodeSeq

case class GetMovementListResponse(movements: Seq[GetMovementListItem])

object GetMovementListResponse {
  def apply(xml: NodeSeq): GetMovementListResponse = {

    val movements = xml \\ "MovementListDataResponse" \\ "Movement"

    GetMovementListResponse(
      movements.map { movement =>
        GetMovementListItem(
          (movement \\ "Arc").text,
          (movement \\ "SequenceNumber").text.toInt,
          (movement \\ "ConsignorName").text,
          Instant.parse((movement \\ "DateOfDispatch").text),
          (movement \\ "MovementStatus").text,
          (movement \\ "DestinationId").text,
          (movement \\ "ConsignorLanguageCode").text
        )
      }
    )
  }

  implicit val writes: Writes[GetMovementListResponse] = Json.writes
}