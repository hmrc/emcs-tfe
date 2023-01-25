/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.models.response

import cats.implicits.catsSyntaxTuple4Semigroupal
import com.lucidchart.open.xtract.{XmlReader, __}
import play.api.libs.json.{Json, Writes}
import uk.gov.hmrc.emcstfe.utils.LocalDateTimeXMLReader._

import java.time.LocalDateTime


case class GetMovementListItem(arc: String,
                               dateOfDispatch: LocalDateTime,
                               movementStatus: String,
                               otherTraderID: String)

object GetMovementListItem {

  implicit val xmlReader: XmlReader[GetMovementListItem] = (
    (__ \ "Arc").read[String],
      (__ \ "DateOfDispatch").read[LocalDateTime],
      (__ \ "MovementStatus").read[String],
      (__ \ "OtherTraderID").read[String]
    ).mapN(GetMovementListItem.apply)

  implicit val writes: Writes[GetMovementListItem] = Json.writes
}
