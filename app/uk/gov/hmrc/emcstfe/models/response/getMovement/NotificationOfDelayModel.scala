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

import cats.implicits.catsSyntaxTuple6Semigroupal
import com.lucidchart.open.xtract.{XPath, XmlReader, __}
import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.emcstfe.models.common.SubmitterType
import uk.gov.hmrc.emcstfe.models.explainDelay.DelayReasonType
import uk.gov.hmrc.emcstfe.utils.DateUtils
import uk.gov.hmrc.emcstfe.utils.LocalDateTimeXMLReader._

import java.time.temporal.ChronoUnit
import java.time.{LocalDate, LocalDateTime, LocalTime}

case class NotificationOfDelayModel(submitterIdentification: String,
                                    submitterType: SubmitterType,
                                    explanationCode: DelayReasonType,
                                    complementaryInformation: Option[String],
                                    dateTime: LocalDateTime)

object NotificationOfDelayModel extends DateUtils {

  implicit val format: Format[NotificationOfDelayModel] = Json.format[NotificationOfDelayModel]

  private lazy val submitterIdentification: XPath = __ \\ "SubmitterIdentification"
  private lazy val submitterType: XPath = __ \\ "SubmitterType"
  private lazy val explanationCode: XPath = __ \\ "ExplanationCode"
  private lazy val complementaryInformation: XPath = __ \\ "ComplementaryInformation"
  private lazy val notificationDate: XPath = __ \\ "DateOfPreparation"
  private lazy val notificationTime: XPath = __ \\ "TimeOfPreparation"

  implicit lazy val xmlReads: XmlReader[NotificationOfDelayModel] = (
    submitterIdentification.read[String],
    submitterType.read[SubmitterType](SubmitterType.xmlReads("SubmitterType")(SubmitterType.enumerable)),
    explanationCode.read[DelayReasonType](DelayReasonType.xmlReads("ExplanationCode")(DelayReasonType.enumerable)),
    complementaryInformation.read[String].optional,
    notificationDate.read[LocalDate],
    notificationTime.read[LocalTime]
  ).mapN(NotificationOfDelayModel.apply)

  def apply(submitterIdentification: String,
            submitterType: SubmitterType,
            explanationCode: DelayReasonType,
            complementaryInformation: Option[String],
            notificationDate: LocalDate,
            notificationTime: LocalTime): NotificationOfDelayModel =
    NotificationOfDelayModel(
      submitterIdentification,
      submitterType,
      explanationCode,
      complementaryInformation,
      LocalDateTime.of(notificationDate, notificationTime.roundToNearestSecond())
    )
}
