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

package uk.gov.hmrc.emcstfe.models.explainShortage

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.emcstfe.models.common.TraderModel

import scala.xml.{Elem, NodeSeq}

case class SubmitExplainShortageModel(
                                       attributes: AttributesModel,
                                       consigneeTrader: Option[TraderModel],
                                       exciseMovement: ExciseMovementModel,
                                       consignorTrader: Option[TraderModel],
                                       analysis: Option[AnalysisModel],
                                       bodyAnalysis: Option[Seq[BodyAnalysisModel]]
                                     ) extends ExplainShortageModel {

  def toXml: Elem =
    <urn:ExplanationOnReasonForShortage>
      {attributes.toXml}
      {consigneeTrader.map(trader =>
        <urn:ConsigneeTrader language="en">
          {trader.toXml}
        </urn:ConsigneeTrader>
      ).getOrElse(NodeSeq.Empty)}
      {exciseMovement.toXml}
      {consignorTrader.map(trader =>
        <urn:ConsignorTrader language="en">
          {trader.toXml}
        </urn:ConsignorTrader>
      ).getOrElse(NodeSeq.Empty)}
      {analysis.map(_.toXml).getOrElse(NodeSeq.Empty)}
      {bodyAnalysis.map(_.map(_.toXml)).getOrElse(NodeSeq.Empty)}
    </urn:ExplanationOnReasonForShortage>
}

object SubmitExplainShortageModel {
  implicit val fmt: OFormat[SubmitExplainShortageModel] = Json.format
}
