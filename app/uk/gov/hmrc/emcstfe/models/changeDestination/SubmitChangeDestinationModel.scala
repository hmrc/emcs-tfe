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

package uk.gov.hmrc.emcstfe.models.changeDestination

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.emcstfe.models.common.TraderModel

import scala.xml.{Elem, NodeSeq}

case class SubmitChangeDestinationModel(
                                          newTransportArrangerTrader: Option[TraderModel],
                                          updateEadEsad: UpdateEadEsadModel,
                                          destinationChanged: DestinationChangedModel,
                                          newTransporterTrader: Option[TraderModel],
                                          transportDetails: Option[Seq[TransportDetailsModel]]
                                         ) extends ChangeDestinationModel {

  def toXml: Elem =
    <urn:ChangeOfDestination>
      <urn:Attributes/>
      {newTransportArrangerTrader.map(trader =>
        <urn:NewTransportArrangerTrader language="en">{trader.toXml}</urn:NewTransportArrangerTrader>
      ).getOrElse(NodeSeq.Empty)}
      {updateEadEsad.toXml}
      {destinationChanged.toXml}
      {newTransportArrangerTrader.map(trader =>
        <urn:NewTransporterTrader language="en">{trader.toXml}</urn:NewTransporterTrader>
      ).getOrElse(NodeSeq.Empty)}
      {transportDetails.map(_.map(_.toXml)).getOrElse(NodeSeq.Empty)}
    </urn:ChangeOfDestination>
}

object SubmitChangeDestinationModel {
  implicit val fmt: OFormat[SubmitChangeDestinationModel] = Json.format
}
