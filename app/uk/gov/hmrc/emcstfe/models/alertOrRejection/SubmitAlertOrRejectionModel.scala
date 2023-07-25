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
import uk.gov.hmrc.emcstfe.utils.XmlWriterUtils

import java.time.LocalDate
import scala.xml.Elem

case class SubmitAlertOrRejectionModel(consigneeTrader: Option[TraderModel],
                                       exciseMovement: ExciseMovementModel,
                                       destinationOffice: String,
                                       dateOfAlertOrRejection: LocalDate,
                                       isRejected: Boolean,
                                       alertOrRejectionReasons: Option[Seq[AlertOrRejectionReasonModel]]
                                      ) extends XmlBaseModel with XmlWriterUtils {

  def toXml: Elem =
    <urn:AlertOrRejectionOfEADESAD>
      <urn:Attributes/>
      {consigneeTrader.mapNodeSeq(trader =>
        <urn:ConsigneeTrader language="en">
          {trader.toXml}
        </urn:ConsigneeTrader>
      )}
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
          {isRejected.toFlag}
        </urn:EadEsadRejectedFlag>
      </urn:AlertOrRejection>
      {alertOrRejectionReasons.mapNodeSeq(_.map(_.toXml))}
    </urn:AlertOrRejectionOfEADESAD>
}

object SubmitAlertOrRejectionModel {
  implicit val fmt: OFormat[SubmitAlertOrRejectionModel] = Json.format
}
