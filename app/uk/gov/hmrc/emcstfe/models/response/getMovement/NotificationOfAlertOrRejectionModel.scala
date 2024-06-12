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
import uk.gov.hmrc.emcstfe.models.alertOrRejection.AlertOrRejectionType
import uk.gov.hmrc.emcstfe.utils.LocalDateTimeXMLReader._

import java.time.LocalDateTime

case class NotificationOfAlertOrRejectionModel(notificationType: AlertOrRejectionType,
                                               notificationDateAndTime: LocalDateTime,
                                               alertRejectReason: Seq[AlertOrRejectionReasonModel])

object NotificationOfAlertOrRejectionModel {

  implicit val format: Format[NotificationOfAlertOrRejectionModel] = Json.format[NotificationOfAlertOrRejectionModel]

  private lazy val notificationType: XPath = __ \\ "EadEsadRejectedFlag"

  private lazy val notificationDateTime: XPath = __ \\ "DateAndTimeOfValidationOfAlertRejection"

  private lazy val alertRejectReason: XPath = __ \\ "AlertOrRejectionOfEadEsadReason"

  implicit lazy val xmlReads: XmlReader[NotificationOfAlertOrRejectionModel] = (
    notificationType.read[AlertOrRejectionType](AlertOrRejectionType.xmlReads("EadEsadRejectedFlag")(AlertOrRejectionType.enumerable)),
    notificationDateTime.read[LocalDateTime],
    alertRejectReason.read[Seq[AlertOrRejectionReasonModel]](strictReadSeq(AlertOrRejectionReasonModel.xmlReads))
  ).mapN(NotificationOfAlertOrRejectionModel.apply)
}
