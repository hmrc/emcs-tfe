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
    <AcceptedOrRejectedReportOfReceiptExport>
      <Attributes>
        <DateAndTimeOfValidationOfReportOfReceiptExport>
          {maxSubmitReportOfReceiptRequest.creationTimestamp.toString}
        </DateAndTimeOfValidationOfReportOfReceiptExport>
      </Attributes>
      <ConsigneeTrader language="en">
        <Traderid>id</Traderid>
        <TraderName>name</TraderName>
        <StreetName>street</StreetName>
        <StreetNumber>number</StreetNumber>
        <Postcode>postcode</Postcode>
        <City>city</City>
        <EoriNumber>eori</EoriNumber>
      </ConsigneeTrader>
      <ExciseMovement>
        <AdministrativeReferenceCode>{testArc}</AdministrativeReferenceCode>
        <SequenceNumber>1</SequenceNumber>
      </ExciseMovement>
      <DeliveryPlaceTrader language="en">
        <Traderid>id</Traderid>
        <TraderName>name</TraderName>
        <StreetName>street</StreetName>
        <StreetNumber>number</StreetNumber>
        <Postcode>postcode</Postcode>
        <City>city</City>
      </DeliveryPlaceTrader>
      <DestinationOffice>
        <ReferenceNumber>{destinationOfficeId}</ReferenceNumber>
      </DestinationOffice>
      <ReportOfReceiptExport>
        <DateOfArrivalOfExciseProducts>{arrivalDate.toString}</DateOfArrivalOfExciseProducts>
        <GlobalConclusionOfReceipt>{maxSubmitReportOfReceiptRequest.globalConclusion}</GlobalConclusionOfReceipt>
        <ComplementaryInformation language="en">
          other
        </ComplementaryInformation>
      </ReportOfReceiptExport>
      <BodyReportOfReceiptExport>
        <BodyRecordUniqueReference>1</BodyRecordUniqueReference>
        <IndicatorOfShortageOrExcess>E</IndicatorOfShortageOrExcess>
        <ObservedShortageOrExcess>12.145</ObservedShortageOrExcess>
        <ExciseProductCode>W300</ExciseProductCode>
        <RefusedQuantity>10</RefusedQuantity>
        {maxUnsatisfactoryModelXML(Excess)}
      </BodyReportOfReceiptExport>
      <BodyReportOfReceiptExport>
        <BodyRecordUniqueReference>2</BodyRecordUniqueReference>
        <IndicatorOfShortageOrExcess>E</IndicatorOfShortageOrExcess>
        <ObservedShortageOrExcess>12.145</ObservedShortageOrExcess>
        <ExciseProductCode>W300</ExciseProductCode>
        <RefusedQuantity>10</RefusedQuantity>
        {maxUnsatisfactoryModelXML(Excess)}
      </BodyReportOfReceiptExport>
    </AcceptedOrRejectedReportOfReceiptExport>


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
    <AcceptedOrRejectedReportOfReceiptExport>
      <Attributes>
        <DateAndTimeOfValidationOfReportOfReceiptExport>
          {minSubmitReportOfReceiptRequest.creationTimestamp.toString}
        </DateAndTimeOfValidationOfReportOfReceiptExport>
      </Attributes>
      <ExciseMovement>
        <AdministrativeReferenceCode>
          {testArc}
        </AdministrativeReferenceCode>
        <SequenceNumber>1</SequenceNumber>
      </ExciseMovement>
      <DestinationOffice>
        <ReferenceNumber>
          {destinationOfficeId}
        </ReferenceNumber>
      </DestinationOffice>
      <ReportOfReceiptExport>
        <DateOfArrivalOfExciseProducts>
          {arrivalDate.toString}
        </DateOfArrivalOfExciseProducts>
        <GlobalConclusionOfReceipt>
          {maxSubmitReportOfReceiptRequest.globalConclusion}
        </GlobalConclusionOfReceipt>
      </ReportOfReceiptExport>
    </AcceptedOrRejectedReportOfReceiptExport>

}
