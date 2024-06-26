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

package uk.gov.hmrc.emcstfe.models.explainShortageExcess

import cats.implicits.catsSyntaxTuple4Semigroupal
import com.lucidchart.open.xtract.{XPath, XmlReader, __}
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.common.XmlBaseModel
import uk.gov.hmrc.emcstfe.utils.XmlWriterUtils

import scala.xml.Elem

case class BodyAnalysisModel(
                              exciseProductCode: String,
                              bodyRecordUniqueReference: Int,
                              explanation: String,
                              actualQuantity: Option[BigDecimal]
                            ) extends XmlBaseModel with XmlWriterUtils {
  def toXml(implicit request: UserRequest[_]): Elem = <urn:BodyAnalysis>
    <urn:ExciseProductCode>{exciseProductCode}</urn:ExciseProductCode>
    <urn:BodyRecordUniqueReference>{bodyRecordUniqueReference}</urn:BodyRecordUniqueReference>
    <urn:Explanation language="en">{explanation}</urn:Explanation>
    {actualQuantity.mapNodeSeq(quantity => <urn:ActualQuantity>{quantity}</urn:ActualQuantity>)}
  </urn:BodyAnalysis>
}

object BodyAnalysisModel {

  implicit val fmt: OFormat[BodyAnalysisModel] = Json.format

  private lazy val exciseProductCode: XPath = __ \\ "ExciseProductCode"
  private lazy val bodyRecordUniqueReference: XPath = __ \\ "BodyRecordUniqueReference"
  private lazy val explanation: XPath = __ \\ "Explanation"
  private lazy val actualQuantity: XPath = __ \\ "ActualQuantity"

  implicit lazy val xmlReads: XmlReader[BodyAnalysisModel] = (
    exciseProductCode.read[String],
    bodyRecordUniqueReference.read[Int],
    explanation.read[String],
    actualQuantity.read[String].map(BigDecimal(_)).optional
  ).mapN(BodyAnalysisModel.apply)
}