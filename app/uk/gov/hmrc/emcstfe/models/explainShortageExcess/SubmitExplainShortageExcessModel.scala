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
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.common.SubmitterType.{Consignee, Consignor}
import uk.gov.hmrc.emcstfe.models.common._
import uk.gov.hmrc.emcstfe.utils.XmlWriterUtils

import scala.xml.Elem

case class SubmitExplainShortageExcessModel(attributes: AttributesModel,
                                            consigneeTrader: Option[TraderModel],
                                            exciseMovement: ExciseMovementModel,
                                            consignorTrader: Option[TraderModel],
                                            analysis: Option[AnalysisModel],
                                            bodyAnalysis: Option[Seq[BodyAnalysisModel]]
                                           ) extends XmlBaseModel with XmlWriterUtils {

  def toXml(implicit request: UserRequest[_]): Elem =
      <urn:ExplanationOnReasonForShortage>
        {attributes.toXml}
        {if (attributes.submitterType == Consignee)consigneeTrader.mapNodeSeq(trader => <urn:ConsigneeTrader language="en">{trader.toXml(ConsigneeTrader)}</urn:ConsigneeTrader>)}
        {exciseMovement.toXml}
        {if (attributes.submitterType == Consignor) consignorTrader.mapNodeSeq(trader => <urn:ConsignorTrader language="en">{trader.toXml(ConsignorTrader)}</urn:ConsignorTrader>)}
        {analysis.mapNodeSeq(_.toXml)}{bodyAnalysis.mapNodeSeq(_.map(_.toXml))}
    </urn:ExplanationOnReasonForShortage>
}

object SubmitExplainShortageExcessModel {
  implicit val fmt: OFormat[SubmitExplainShortageExcessModel] = Json.format
}
