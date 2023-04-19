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

package uk.gov.hmrc.emcstfe.fixtures

import uk.gov.hmrc.emcstfe.models.common.AcceptMovement.{PartiallyRefused, Satisfactory}
import uk.gov.hmrc.emcstfe.models.common.WrongWithMovement.Excess
import uk.gov.hmrc.emcstfe.models.request.reportOfReceipt.SubmitReportOfReceiptRequest

import java.time.LocalDate

trait SubmitReportOfReceiptRequestFixtures extends BaseFixtures
  with TraderModelFixtures
  with ReceiptedItemsModelFixtures {

  val arrivalDate = LocalDate.now()
  val destinationOfficeId = "GB000434"

  val maxSubmitReportOfReceiptRequest = SubmitReportOfReceiptRequest(
    arc = testArc,
    sequenceNumber = 1,
    consigneeTrader = maxTraderModel,
    deliveryPlaceTrader = maxTraderModel.copy(eoriNumber = None),
    destinationOffice = destinationOfficeId,
    dateOfArrival = arrivalDate,
    acceptMovement = PartiallyRefused,
    individualItems = Seq(
      excessReceiptedItemsModel,
      excessReceiptedItemsModel.copy(eadBodyUniqueReference = 2)
    ),
    otherInformation = Some("other")
  )

  val maxSubmitReportOfReceiptRequestXML =
    <urn:AcceptedOrRejectedReportOfReceiptExport>
      <urn:Attributes>
        <urn:DateAndTimeOfValidationOfReportOfReceiptExport>
          {maxSubmitReportOfReceiptRequest.creationTimestamp.toString}
        </urn:DateAndTimeOfValidationOfReportOfReceiptExport>
      </urn:Attributes>
      <urn:ConsigneeTrader language="en">
        <urn:Traderid>id</urn:Traderid>
        <urn:TraderName>name</urn:TraderName>
        <urn:StreetName>street</urn:StreetName>
        <urn:StreetNumber>number</urn:StreetNumber>
        <urn:Postcode>postcode</urn:Postcode>
        <urn:City>city</urn:City>
        <urn:EoriNumber>eori</urn:EoriNumber>
      </urn:ConsigneeTrader>
      <urn:ExciseMovement>
        <urn:AdministrativeReferenceCode>{testArc}</urn:AdministrativeReferenceCode>
        <urn:SequenceNumber>1</urn:SequenceNumber>
      </urn:ExciseMovement>
      <urn:DeliveryPlaceTrader language="en">
        <urn:Traderid>id</urn:Traderid>
        <urn:TraderName>name</urn:TraderName>
        <urn:StreetName>street</urn:StreetName>
        <urn:StreetNumber>number</urn:StreetNumber>
        <urn:Postcode>postcode</urn:Postcode>
        <urn:City>city</urn:City>
      </urn:DeliveryPlaceTrader>
      <urn:DestinationOffice>
        <urn:ReferenceNumber>{destinationOfficeId}</urn:ReferenceNumber>
      </urn:DestinationOffice>
      <urn:ReportOfReceiptExport>
        <urn:DateOfArrivalOfExciseProducts>{arrivalDate.toString}</urn:DateOfArrivalOfExciseProducts>
        <urn:GlobalConclusionOfReceipt>{maxSubmitReportOfReceiptRequest.globalConclusion}</urn:GlobalConclusionOfReceipt>
        <urn:ComplementaryInformation language="en">
          other
        </urn:ComplementaryInformation>
      </urn:ReportOfReceiptExport>
      <urn:BodyReportOfReceiptExport>
        <urn:BodyRecordUniqueReference>1</urn:BodyRecordUniqueReference>
        <urn:IndicatorOfShortageOrExcess>E</urn:IndicatorOfShortageOrExcess>
        <urn:ObservedShortageOrExcess>12.145</urn:ObservedShortageOrExcess>
        <urn:ExciseProductCode>W300</urn:ExciseProductCode>
        <urn:RefusedQuantity>10</urn:RefusedQuantity>
        {maxUnsatisfactoryModelXML(Excess)}
      </urn:BodyReportOfReceiptExport>
      <urn:BodyReportOfReceiptExport>
        <urn:BodyRecordUniqueReference>2</urn:BodyRecordUniqueReference>
        <urn:IndicatorOfShortageOrExcess>E</urn:IndicatorOfShortageOrExcess>
        <urn:ObservedShortageOrExcess>12.145</urn:ObservedShortageOrExcess>
        <urn:ExciseProductCode>W300</urn:ExciseProductCode>
        <urn:RefusedQuantity>10</urn:RefusedQuantity>
        {maxUnsatisfactoryModelXML(Excess)}
      </urn:BodyReportOfReceiptExport>
    </urn:AcceptedOrRejectedReportOfReceiptExport>


  val minSubmitReportOfReceiptRequest = SubmitReportOfReceiptRequest(
    arc = testArc,
    sequenceNumber = 1,
    consigneeTrader = minTraderModel,
    deliveryPlaceTrader = minTraderModel,
    destinationOffice = destinationOfficeId,
    dateOfArrival = arrivalDate,
    acceptMovement = Satisfactory,
    individualItems = Seq(),
    otherInformation = None
  )

  val minSubmitReportOfReceiptRequestXML =
    <urn:AcceptedOrRejectedReportOfReceiptExport>
      <urn:Attributes>
        <urn:DateAndTimeOfValidationOfReportOfReceiptExport>
          {minSubmitReportOfReceiptRequest.creationTimestamp.toString}
        </urn:DateAndTimeOfValidationOfReportOfReceiptExport>
      </urn:Attributes>
      <urn:ExciseMovement>
        <urn:AdministrativeReferenceCode>
          {testArc}
        </urn:AdministrativeReferenceCode>
        <urn:SequenceNumber>1</urn:SequenceNumber>
      </urn:ExciseMovement>
      <urn:DestinationOffice>
        <urn:ReferenceNumber>
          {destinationOfficeId}
        </urn:ReferenceNumber>
      </urn:DestinationOffice>
      <urn:ReportOfReceiptExport>
        <urn:DateOfArrivalOfExciseProducts>
          {arrivalDate.toString}
        </urn:DateOfArrivalOfExciseProducts>
        <urn:GlobalConclusionOfReceipt>
          {minSubmitReportOfReceiptRequest.globalConclusion}
        </urn:GlobalConclusionOfReceipt>
      </urn:ReportOfReceiptExport>
    </urn:AcceptedOrRejectedReportOfReceiptExport>

}
