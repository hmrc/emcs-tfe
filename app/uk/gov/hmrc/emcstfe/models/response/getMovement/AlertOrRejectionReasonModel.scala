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

import cats.implicits.catsSyntaxTuple2Semigroupal
import com.lucidchart.open.xtract.{XPath, XmlReader, __}
import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.emcstfe.models.alertOrRejection.AlertOrRejectionReasonType

case class AlertOrRejectionReasonModel(reason: AlertOrRejectionReasonType,
                                       additionalInformation: Option[String])

object AlertOrRejectionReasonModel {

  implicit val format: Format[AlertOrRejectionReasonModel] = Json.format[AlertOrRejectionReasonModel]

  private lazy val alertRejectReason: XPath = __ \\ "AlertOrRejectionOfMovementReasonCode"

  private lazy val alertRejectReasonInformation: XPath = __ \\ "ComplementaryInformation"

  implicit lazy val xmlReads: XmlReader[AlertOrRejectionReasonModel] = (
    alertRejectReason.read[AlertOrRejectionReasonType](AlertOrRejectionReasonType.xmlReads("AlertOrRejectionOfMovementReasonCode")(AlertOrRejectionReasonType.enumerable)),
    alertRejectReasonInformation.read[String].optional
  ).mapN(AlertOrRejectionReasonModel.apply)
}
