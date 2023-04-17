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

package uk.gov.hmrc.emcstfe.models.request.reportOfReceipt

import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.emcstfe.models.common.AcceptMovement

import java.time.{Instant, LocalDate}
import scala.xml.{Elem, NodeSeq}

case class SubmitReportOfReceiptRequest(arc: String,
                                        sequenceNumber: Int,
                                        consigneeTrader: TraderModel,
                                        deliveryPlaceTrader: TraderModel,
                                        destinationOffice: String,
                                        dateOfArrival: LocalDate,
                                        acceptMovement: AcceptMovement,
                                        individualItems: Seq[ReceiptedItemsModel],
                                        otherInformation: Option[String]) {

  private val globalConclusion = acceptMovement match {
    case AcceptMovement.Satisfactory => 1
    case AcceptMovement.Unsatisfactory => 2
    case AcceptMovement.Refused => 3
    case AcceptMovement.PartiallyRefused => 4
  }

  def toXml: Elem =
    <AcceptedOrRejectedReportOfReceiptExport>
      <Attributes>
        <DateAndTimeOfValidationOfReportOfReceiptExport>
          {Instant.now().toString}
        </DateAndTimeOfValidationOfReportOfReceiptExport>
      </Attributes>
      <ConsigneeTrader language="en">
        {consigneeTrader.toXml}
      </ConsigneeTrader>
      <ExciseMovement>
        <AdministrativeReferenceCode>
          {arc}
        </AdministrativeReferenceCode>
        <SequenceNumber>
          {sequenceNumber}
        </SequenceNumber>
      </ExciseMovement>
      <DeliveryPlaceTrader language="en">
        {deliveryPlaceTrader.toXml}
      </DeliveryPlaceTrader>
      <DestinationOffice>
        <ReferenceNumber>
          {destinationOffice}
        </ReferenceNumber>
      </DestinationOffice>
      <ReportOfReceiptExport>
        <DateOfArrivalOfExciseProducts>
          {dateOfArrival.toString}
        </DateOfArrivalOfExciseProducts>
        <GlobalConclusionOfReceipt>
          {globalConclusion}
        </GlobalConclusionOfReceipt>
        {otherInformation.map(x => <ComplementaryInformation language="en">{x}</ComplementaryInformation>)}
      </ReportOfReceiptExport>
      {NodeSeq.fromSeq(individualItems.map(_.toXml))}
    </AcceptedOrRejectedReportOfReceiptExport>
}

object SubmitReportOfReceiptRequest {
  implicit val fmt: Format[SubmitReportOfReceiptRequest] = Json.format
}
