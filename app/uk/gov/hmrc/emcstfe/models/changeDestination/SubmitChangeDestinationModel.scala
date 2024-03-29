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
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.common.{TraderModel, TransportDetailsModel, TransportTrader, XmlBaseModel}
import uk.gov.hmrc.emcstfe.utils.XmlWriterUtils

import scala.xml.Elem

case class SubmitChangeDestinationModel(
                                          newTransportArrangerTrader: Option[TraderModel],
                                          updateEadEsad: UpdateEadEsadModel,
                                          destinationChanged: DestinationChangedModel,
                                          newTransporterTrader: Option[TraderModel],
                                          transportDetails: Option[Seq[TransportDetailsModel]]
                                         ) extends XmlBaseModel with XmlWriterUtils {

  def toXml(implicit request: UserRequest[_]): Elem =
    <urn:ChangeOfDestination>
      <urn:Attributes/>
      {newTransportArrangerTrader.mapNodeSeq(trader => <urn:NewTransportArrangerTrader language="en">{trader.toXml(TransportTrader)}</urn:NewTransportArrangerTrader>)}
      {updateEadEsad.toXml}
      {destinationChanged.toXml}
      {newTransporterTrader.mapNodeSeq(trader => <urn:NewTransporterTrader language="en">{trader.toXml(TransportTrader)}</urn:NewTransporterTrader>)}
      {transportDetails.mapNodeSeq(_.map(_.toXml))}
    </urn:ChangeOfDestination>
}

object SubmitChangeDestinationModel {
  implicit val fmt: OFormat[SubmitChangeDestinationModel] = Json.format
}
