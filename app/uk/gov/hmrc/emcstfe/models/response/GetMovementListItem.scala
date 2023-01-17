/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.models.response

import play.api.libs.json.{Json, Writes}

import java.time.Instant

case class GetMovementListItem(arc: String,
                               sequenceNumber: Int,
                               consignorName: String,
                               dateOfDispatch: Instant,
                               movementStatus: String,
                               destinationId: String,
                               consignorLanguageCode: String)

object GetMovementListItem {

  implicit val writes: Writes[GetMovementListItem] = Json.writes
}
