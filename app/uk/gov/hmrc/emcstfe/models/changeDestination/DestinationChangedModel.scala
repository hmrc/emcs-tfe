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
import uk.gov.hmrc.emcstfe.models.common.{DestinationType, MovementGuaranteeModel, TraderModel, XmlBaseModel}
import uk.gov.hmrc.emcstfe.utils.XmlWriterUtils

import scala.xml.Elem

case class DestinationChangedModel(
                                    destinationTypeCode: DestinationType,
                                    newConsigneeTrader: Option[TraderModel],
                                    deliveryPlaceTrader: Option[TraderModel],
                                    deliveryPlaceCustomsOffice: Option[DeliveryPlaceCustomsOfficeModel],
                                    movementGuarantee: Option[MovementGuaranteeModel]
                                  ) extends XmlBaseModel with XmlWriterUtils {
  def toXml(implicit request: UserRequest[_]): Elem = <urn:DestinationChanged>
    <urn:DestinationTypeCode>{destinationTypeCode.toString}</urn:DestinationTypeCode>
    {newConsigneeTrader.mapNodeSeq(trader => <urn:NewConsigneeTrader language="en">{trader.toXml}</urn:NewConsigneeTrader>)}
    {deliveryPlaceTrader.mapNodeSeq(trader => <urn:DeliveryPlaceTrader language="en">{trader.toXml}</urn:DeliveryPlaceTrader>)}
    {deliveryPlaceCustomsOffice.mapNodeSeq(_.toXml)}
    {movementGuarantee.mapNodeSeq(_.toXml)}
  </urn:DestinationChanged>
}

object DestinationChangedModel {
  implicit val fmt: OFormat[DestinationChangedModel] = Json.format
}
