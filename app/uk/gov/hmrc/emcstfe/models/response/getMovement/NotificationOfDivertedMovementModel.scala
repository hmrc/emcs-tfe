/*
 * Copyright 2024 HM Revenue & Customs
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

package uk.gov.hmrc.emcstfe.models.response.getMovement

import cats.implicits.catsSyntaxTuple3Semigroupal
import com.lucidchart.open.xtract.XmlReader.strictReadSeq
import com.lucidchart.open.xtract.{XPath, XmlReader, __}
import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.emcstfe.utils.LocalDateTimeXMLReader._

import java.time.LocalDateTime

case class NotificationOfDivertedMovementModel(
                                                notificationType: NotificationOfDivertedMovementType,
                                                notificationDateAndTime: LocalDateTime,
                                                downstreamArcs: Seq[String]
                                              )

object NotificationOfDivertedMovementModel {

  implicit val format: Format[NotificationOfDivertedMovementModel] = Json.format[NotificationOfDivertedMovementModel]

  private lazy val notificationType: XPath = __ \\ "NotificationType"

  private lazy val notificationDateTime: XPath = __ \\ "NotificationDateAndTime"

  private lazy val downstreamArcs: XPath = __ \\ "DownstreamArc" \ "AdministrativeReferenceCode"

  implicit lazy val xmlReads: XmlReader[NotificationOfDivertedMovementModel] = (
    notificationType.read[NotificationOfDivertedMovementType](NotificationOfDivertedMovementType.xmlReads("NotificationType")(NotificationOfDivertedMovementType.enumerable)),
    notificationDateTime.read[LocalDateTime],
    downstreamArcs.read[Seq[String]](strictReadSeq).optional.map(_.getOrElse(Seq.empty))
  ).mapN(NotificationOfDivertedMovementModel.apply)
}
