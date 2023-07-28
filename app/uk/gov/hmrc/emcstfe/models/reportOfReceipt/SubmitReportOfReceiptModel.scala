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

package uk.gov.hmrc.emcstfe.models.reportOfReceipt

import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.common.AcceptMovement._
import uk.gov.hmrc.emcstfe.models.common.{AcceptMovement, ConsigneeTrader, DeliveryPlaceTrader, DestinationType, TraderModel, XmlBaseModel}
import uk.gov.hmrc.emcstfe.utils.XmlWriterUtils

import java.time.LocalDate
import scala.xml.Elem

case class SubmitReportOfReceiptModel(arc: String,
                                      sequenceNumber: Int,
                                      destinationType: DestinationType,
                                      consigneeTrader: Option[TraderModel],
                                      deliveryPlaceTrader: Option[TraderModel],
                                      destinationOffice: String,
                                      dateOfArrival: LocalDate,
                                      acceptMovement: AcceptMovement,
                                      individualItems: Seq[ReceiptedItemsModel],
                                      otherInformation: Option[String]) extends XmlBaseModel with XmlWriterUtils {

  val globalConclusion = acceptMovement match {
    case Satisfactory => 1
    case Unsatisfactory => 2
    case Refused => 3
    case PartiallyRefused => 4
  }

  def toXml(implicit request: UserRequest[_]): Elem =
    <urn:AcceptedOrRejectedReportOfReceiptExport>
      <urn:Attributes/>
      {consigneeTrader.mapNodeSeq { ct =>
        <urn:ConsigneeTrader language="en">
          {ct.toXml(ConsigneeTrader)}
        </urn:ConsigneeTrader>
      }}
      <urn:ExciseMovement>
        <urn:AdministrativeReferenceCode>
          {arc}
        </urn:AdministrativeReferenceCode>
        <urn:SequenceNumber>
          {sequenceNumber}
        </urn:SequenceNumber>
      </urn:ExciseMovement>
      {deliveryPlaceTrader.mapNodeSeq { dt =>
        <urn:DeliveryPlaceTrader language="en">
          {dt.toXml(DeliveryPlaceTrader)}
        </urn:DeliveryPlaceTrader>
      }}
      <urn:DestinationOffice>
        <urn:ReferenceNumber>
          {destinationOffice}
        </urn:ReferenceNumber>
      </urn:DestinationOffice>
      <urn:ReportOfReceiptExport>
        <urn:DateOfArrivalOfExciseProducts>
          {dateOfArrival.toString}
        </urn:DateOfArrivalOfExciseProducts>
        <urn:GlobalConclusionOfReceipt>
          {globalConclusion}
        </urn:GlobalConclusionOfReceipt>
        {otherInformation.mapNodeSeq(x => <urn:ComplementaryInformation language="en">{x}</urn:ComplementaryInformation>)}
      </urn:ReportOfReceiptExport>
      {individualItems.map(_.toXml)}
    </urn:AcceptedOrRejectedReportOfReceiptExport>
}

object SubmitReportOfReceiptModel {
  implicit val fmt: Format[SubmitReportOfReceiptModel] = Json.format
}
