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
import uk.gov.hmrc.emcstfe.models.common.DestinationType.TaxWarehouse
import uk.gov.hmrc.emcstfe.models.common.WrongWithMovement.Excess
import uk.gov.hmrc.emcstfe.models.common.{ConsigneeTrader, DeliveryPlaceTrader}
import uk.gov.hmrc.emcstfe.models.reportOfReceipt.SubmitReportOfReceiptModel

import java.time.LocalDate

trait SubmitReportOfReceiptFixtures extends BaseFixtures
  with TraderModelFixtures
  with ReceiptedItemsModelFixtures
    with EISResponsesFixture {

  val arrivalDate = LocalDate.now()
  val destinationOfficeId = "GB000434"

  val maxSubmitReportOfReceiptModel = SubmitReportOfReceiptModel(
    arc = testArc,
    destinationType = Some(TaxWarehouse),
    dateAndTimeOfValidationOfReportOfReceiptExport = None,
    sequenceNumber = 1,
    consigneeTrader = Some(maxTraderModel(ConsigneeTrader)),
    deliveryPlaceTrader = Some(maxTraderModel(DeliveryPlaceTrader)),
    destinationOffice = destinationOfficeId,
    dateOfArrival = arrivalDate,
    acceptMovement = PartiallyRefused,
    individualItems = Seq(
      excessReceiptedItemsModel,
      excessReceiptedItemsModel.copy(eadBodyUniqueReference = 2)
    ),
    otherInformation = Some("other")
  )

  val maxSubmitReportOfReceiptModelXML =
    <urn:AcceptedOrRejectedReportOfReceiptExport>
      <urn:Attributes/>
      <urn:ConsigneeTrader language="en">
        {maxTraderModelXML(ConsigneeTrader)}
      </urn:ConsigneeTrader>
      <urn:ExciseMovement>
        <urn:AdministrativeReferenceCode>
          {testArc}
        </urn:AdministrativeReferenceCode>
        <urn:SequenceNumber>
          1
        </urn:SequenceNumber>
      </urn:ExciseMovement>
      <urn:DeliveryPlaceTrader language="en">
        {maxTraderModelXML(DeliveryPlaceTrader)}
      </urn:DeliveryPlaceTrader>
      <urn:DestinationOffice>
        <urn:ReferenceNumber>
          {destinationOfficeId}
        </urn:ReferenceNumber>
      </urn:DestinationOffice>
      <urn:ReportOfReceiptExport>
        <urn:DateOfArrivalOfExciseProducts>
          {arrivalDate}
        </urn:DateOfArrivalOfExciseProducts>
        <urn:GlobalConclusionOfReceipt>
          {maxSubmitReportOfReceiptModel.globalConclusion}
        </urn:GlobalConclusionOfReceipt>
        <urn:ComplementaryInformation language="en">other</urn:ComplementaryInformation>
      </urn:ReportOfReceiptExport><urn:BodyReportOfReceiptExport>
        <urn:BodyRecordUniqueReference>
          1
        </urn:BodyRecordUniqueReference>
        <urn:IndicatorOfShortageOrExcess>E</urn:IndicatorOfShortageOrExcess>
        <urn:ObservedShortageOrExcess>12.145</urn:ObservedShortageOrExcess>
        <urn:ExciseProductCode>
          W300
        </urn:ExciseProductCode>
        <urn:RefusedQuantity>10</urn:RefusedQuantity>
        {maxUnsatisfactoryModelXML(Excess)}
      </urn:BodyReportOfReceiptExport>
      <urn:BodyReportOfReceiptExport>
        <urn:BodyRecordUniqueReference>
          2
        </urn:BodyRecordUniqueReference>
        <urn:IndicatorOfShortageOrExcess>
          E
        </urn:IndicatorOfShortageOrExcess>
        <urn:ObservedShortageOrExcess>
          12.145
        </urn:ObservedShortageOrExcess>
        <urn:ExciseProductCode>
          W300
        </urn:ExciseProductCode>
        <urn:RefusedQuantity>
          10
        </urn:RefusedQuantity>
        {maxUnsatisfactoryModelXML(Excess)}
      </urn:BodyReportOfReceiptExport>
    </urn:AcceptedOrRejectedReportOfReceiptExport>


  val minSubmitReportOfReceiptModel = SubmitReportOfReceiptModel(
    arc = testArc,
    destinationType = Some(TaxWarehouse),
    sequenceNumber = 1,
    consigneeTrader = None,
    deliveryPlaceTrader = None,
    destinationOffice = destinationOfficeId,
    dateOfArrival = arrivalDate,
    acceptMovement = Satisfactory,
    individualItems = Seq.empty,
    otherInformation = None,
    dateAndTimeOfValidationOfReportOfReceiptExport = None
  )

  val minSubmitReportOfReceiptModelXML =
    <urn:AcceptedOrRejectedReportOfReceiptExport>
      <urn:Attributes/>
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
          {arrivalDate}
        </urn:DateOfArrivalOfExciseProducts>
        <urn:GlobalConclusionOfReceipt>
          {minSubmitReportOfReceiptModel.globalConclusion}
        </urn:GlobalConclusionOfReceipt>
      </urn:ReportOfReceiptExport>
    </urn:AcceptedOrRejectedReportOfReceiptExport>
}
