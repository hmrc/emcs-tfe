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

import play.api.libs.json.{Json, OFormat}

import scala.xml.{Elem, NodeSeq}

case class BodyAnalysisModel(
                              exciseProductCode: String,
                              bodyRecordUniqueReference: Int,
                              explanation: String,
                              actualQuantity: Option[BigDecimal]
                            ) extends ExplainShortageExcessModel {
  def toXml: Elem = <urn:BodyAnalysis>
    <urn:ExciseProductCode>{exciseProductCode}</urn:ExciseProductCode>
    <urn:BodyRecordUniqueReference>{bodyRecordUniqueReference}</urn:BodyRecordUniqueReference>
    <urn:Explanation language="en">{explanation}</urn:Explanation>
    {actualQuantity.map(quantity => <urn:ActualQuantity>{quantity}</urn:ActualQuantity>).getOrElse(NodeSeq.Empty)}
  </urn:BodyAnalysis>
}

object BodyAnalysisModel {
  implicit val fmt: OFormat[BodyAnalysisModel] = Json.format
}