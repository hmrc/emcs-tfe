/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.models.response

import cats.implicits.catsSyntaxTuple2Semigroupal
import com.lucidchart.open.xtract.XmlReader.strictReadSeq
import com.lucidchart.open.xtract.{XmlReader, __}
import play.api.libs.json.{Json, Writes}

case class GetMovementListResponse(movements: Seq[GetMovementListItem], count: Int)

object GetMovementListResponse {

  implicit val xmlReader: XmlReader[GetMovementListResponse] = (
    (__ \ "Movement").read[Seq[GetMovementListItem]](strictReadSeq),
    (__ \ "CountOfMovementsAvailable").read[Int]
  ).mapN(GetMovementListResponse.apply)

  implicit val writes: Writes[GetMovementListResponse] = Json.writes
}