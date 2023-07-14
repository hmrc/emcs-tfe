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

package uk.gov.hmrc.emcstfe.models.changeOfDestination

import play.api.libs.json.{Json, Reads}

import scala.xml.{Elem, NodeSeq}

case class SubmitChangeOfDestinationModel(
                                          attributes: AttributesModel,
                                          newTransportArrangerTrader: Option[NewTransportArrangerTraderModel],
                                          updateEadEsad: UpdateEadEsadModel,
                                          destinationChanged: DestinationChangedModel,
                                          newTransporterTrader: Option[NewTransporterTraderModel],
                                          transportDetails: Option[Seq[TransportDetailsModel]]
                                         ) extends ChangeOfDestinationModel {

  def toXml: Elem =
    <urn:ChangeOfDestination>
      {attributes.toXml}
      {newTransportArrangerTrader.map(_.toXml).getOrElse(NodeSeq.Empty)}
      {updateEadEsad.toXml}
      {destinationChanged.toXml}
      {newTransporterTrader.map(_.toXml).getOrElse(NodeSeq.Empty)}
      {transportDetails.map(_.map(_.toXml)).getOrElse(NodeSeq.Empty)}
    </urn:ChangeOfDestination>
}

object SubmitChangeOfDestinationModel {
  implicit val reads: Reads[SubmitChangeOfDestinationModel] = Json.reads
}
