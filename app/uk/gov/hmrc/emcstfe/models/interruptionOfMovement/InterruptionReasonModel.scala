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

package uk.gov.hmrc.emcstfe.models.interruptionOfMovement

import cats.implicits.catsSyntaxTuple3Semigroupal
import com.lucidchart.open.xtract.{XPath, XmlReader, __}
import play.api.libs.json.{Format, Json}

case class InterruptionReasonModel(
                                    reasonCode: InterruptionReasonType,
                                    referenceNumberOfExciseOffice: String,
                                    complementaryInformation: Option[String]
                                  )

object InterruptionReasonModel {
  implicit val format: Format[InterruptionReasonModel] = Json.format[InterruptionReasonModel]

  private lazy val reasonCode: XPath = __ \\ "Attributes" \\ "ReasonForInterruptionCode"

  private lazy val complementaryInformation: XPath = __ \\ "Attributes" \\ "ComplementaryInformation"

  private lazy val referenceNumberOfExciseOffice: XPath = __ \\ "Attributes" \\ "ReferenceNumberOfExciseOffice"

  implicit lazy val xmlReads: XmlReader[InterruptionReasonModel] = (
    reasonCode.read[InterruptionReasonType](InterruptionReasonType.xmlReads("ReasonForInterruptionCode")(InterruptionReasonType.enumerable)),
    referenceNumberOfExciseOffice.read[String],
    complementaryInformation.read[String].optional,
  ).mapN(InterruptionReasonModel.apply)

}
