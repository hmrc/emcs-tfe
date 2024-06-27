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

import cats.implicits.catsSyntaxTuple4Semigroupal
import com.lucidchart.open.xtract.XmlReader.strictReadSeq
import com.lucidchart.open.xtract.{XPath, XmlReader, __}
import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.emcstfe.models.common.SubmitterType
import uk.gov.hmrc.emcstfe.models.explainShortageExcess.BodyAnalysisModel
import uk.gov.hmrc.emcstfe.utils.XmlReaderUtils

import java.time.LocalDate

case class NotificationOfShortageOrExcessModel(submitterType: SubmitterType,
                                               globalDateOfAnalysis: Option[LocalDate],
                                               globalExplanation: Option[String],
                                               individualItemReasons: Option[Seq[BodyAnalysisModel]])

object NotificationOfShortageOrExcessModel extends XmlReaderUtils {

  implicit val format: Format[NotificationOfShortageOrExcessModel] = Json.format[NotificationOfShortageOrExcessModel]

  private lazy val submitterType: XPath = __ \\ "SubmitterType"
  private lazy val globalDate: XPath = __ \\ "DateOfAnalysis"
  private lazy val globalExplanation: XPath = __ \\ "GlobalExplanation"
  private lazy val bodyAnalysis: XPath = __ \\ "BodyAnalysis"

  implicit lazy val xmlReads: XmlReader[NotificationOfShortageOrExcessModel] = (
    submitterType.read[SubmitterType](SubmitterType.xmlReads("SubmitterType")(SubmitterType.enumerable)),
    globalDate.read[String].map(LocalDate.parse).optional,
    globalExplanation.read[String].optional,
    bodyAnalysis.read[Seq[BodyAnalysisModel]](strictReadSeq).seqToOptionSeq
  ).mapN(NotificationOfShortageOrExcessModel.apply)
}
