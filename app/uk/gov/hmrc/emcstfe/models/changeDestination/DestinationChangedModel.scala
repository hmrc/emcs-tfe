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
import uk.gov.hmrc.emcstfe.models.common.{DestinationType, TraderModel}
import uk.gov.hmrc.emcstfe.models.common.DestinationType

import scala.xml.{Elem, NodeSeq}

case class DestinationChangedModel(
                                    destinationTypeCode: DestinationType,
                                    newConsigneeTrader: Option[TraderModel],
                                    deliveryPlaceTrader: Option[TraderModel],
                                    deliveryPlaceCustomsOffice: Option[DeliveryPlaceCustomsOfficeModel],
                                    movementGuarantee: Option[MovementGuaranteeModel]
                                  ) extends ChangeDestinationModel {
  def toXml: Elem = <urn:DestinationChanged>
    <urn:DestinationTypeCode>{destinationTypeCode.toString}</urn:DestinationTypeCode>
    {newConsigneeTrader.map(trader =>
      <urn:NewConsigneeTrader language="en">{trader.toXml}</urn:NewConsigneeTrader>
    ).getOrElse(NodeSeq.Empty)}
    {deliveryPlaceTrader.map(trader =>
      <urn:DeliveryPlaceTrader language="en">{trader.toXml}</urn:DeliveryPlaceTrader>
    ).getOrElse(NodeSeq.Empty)}
    {deliveryPlaceCustomsOffice.map(_.toXml).getOrElse(NodeSeq.Empty)}
    {movementGuarantee.map(_.toXml).getOrElse(NodeSeq.Empty)}
  </urn:DestinationChanged>
}

object DestinationChangedModel {
  implicit val fmt: OFormat[DestinationChangedModel] = Json.format
}
