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

package uk.gov.hmrc.emcstfe.models.alertOrRejection

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.emcstfe.models.common.{ExciseMovementModel, TraderModel, XmlBaseModel}

import java.time.LocalDate
import scala.xml.{Elem, NodeSeq}

case class SubmitAlertOrRejectionModel(consigneeTrader: Option[TraderModel],
                                       exciseMovement: ExciseMovementModel,
                                       destinationOffice: String,
                                       dateOfAlertOrRejection: LocalDate,
                                       isRejected: Boolean,
                                       alertOrRejectionReasons: Option[Seq[AlertOrRejectionReasonModel]]
                                      ) extends XmlBaseModel {

  def toXml: Elem =
    <urn:AlertOrRejectionOfEADESAD>
      <urn:Attributes/>
      {consigneeTrader.map(trader =>
        <urn:ConsigneeTrader language="en">
          {trader.toXml}
        </urn:ConsigneeTrader>
      ).getOrElse(NodeSeq.Empty)}
      {exciseMovement.toXml}
      <urn:DestinationOffice>
        <urn:ReferenceNumber>
          {destinationOffice}
        </urn:ReferenceNumber>
      </urn:DestinationOffice>
      <urn:AlertOrRejection>
        <urn:DateOfAlertOrRejection>
          {dateOfAlertOrRejection.toString}
        </urn:DateOfAlertOrRejection>
        <urn:EadEsadRejectedFlag>
          {if(isRejected) 1 else 0}
        </urn:EadEsadRejectedFlag>
      </urn:AlertOrRejection>
      {alertOrRejectionReasons.map(_.map(_.toXml)).getOrElse(NodeSeq.Empty)}
    </urn:AlertOrRejectionOfEADESAD>
}

object SubmitAlertOrRejectionModel {
  implicit val fmt: OFormat[SubmitAlertOrRejectionModel] = Json.format
}
