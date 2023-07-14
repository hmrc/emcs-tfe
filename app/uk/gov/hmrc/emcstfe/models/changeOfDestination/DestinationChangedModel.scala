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
import uk.gov.hmrc.emcstfe.models.common.DestinationType

import scala.xml.{Elem, NodeSeq}

case class DestinationChangedModel(
                                    destinationTypeCode: DestinationType,
                                    newConsigneeTrader: Option[NewConsigneeTraderModel],
                                    deliveryPlaceTrader: Option[DeliveryPlaceTraderModel],
                                    deliveryPlaceCustomsOffice: Option[DeliveryPlaceCustomsOfficeModel],
                                    movementGuarantee: Option[MovementGuaranteeModel]
                                  ) extends ChangeOfDestinationModel {
  def toXml: Elem = <urn:DestinationChanged>
    <urn:DestinationTypeCode>{destinationTypeCode.toString}</urn:DestinationTypeCode>
    {newConsigneeTrader.map(_.toXml).getOrElse(NodeSeq.Empty)}
    {deliveryPlaceTrader.map(_.toXml).getOrElse(NodeSeq.Empty)}
    {deliveryPlaceCustomsOffice.map(_.toXml).getOrElse(NodeSeq.Empty)}
    {movementGuarantee.map(_.toXml).getOrElse(NodeSeq.Empty)}
  </urn:DestinationChanged>
}

object DestinationChangedModel {
  implicit val reads: Reads[DestinationChangedModel] = Json.reads
}
