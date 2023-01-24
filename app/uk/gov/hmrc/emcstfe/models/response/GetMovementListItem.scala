/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.models.response

import play.api.libs.json.{Json, Writes}

import java.time.{Instant, LocalDateTime}

case class GetMovementListItem(arc: String,
                               dateOfDispatch: LocalDateTime,
                               movementStatus: String,
                               otherTraderID: String)

object GetMovementListItem {

  implicit val writes: Writes[GetMovementListItem] = Json.writes
}
