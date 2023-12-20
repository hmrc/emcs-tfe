/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.emcstfe.models.response

import cats.implicits.catsSyntaxTuple4Semigroupal
import com.lucidchart.open.xtract.{XmlReader, __}
import play.api.libs.json.{Json, Writes}
import uk.gov.hmrc.emcstfe.utils.InstantXMLReader._
import uk.gov.hmrc.emcstfe.utils.LocalDateTimeXMLReader._

import java.time.{Instant, LocalDateTime, ZoneId}


case class GetMovementListItem(arc: String,
                               dateOfDispatch: LocalDateTime,
                               movementStatus: String,
                               otherTraderID: String)

object GetMovementListItem {
  implicit val xmlReader: XmlReader[GetMovementListItem] = (
    (__ \ "Arc").read[String],
      (__ \ "DateOfDispatch").read[LocalDateTime]
        or (__ \ "DateOfDispatch").read[Instant].map(_.atZone(ZoneId.of("UTC")).toLocalDateTime),
      (__ \ "MovementStatus").read[String],
      (__ \ "OtherTraderID").read[String]
    ).mapN(GetMovementListItem.apply)

  implicit val writes: Writes[GetMovementListItem] = Json.writes
}
