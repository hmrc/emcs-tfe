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

import uk.gov.hmrc.emcstfe.models.common.WrongWithMovement._
import uk.gov.hmrc.emcstfe.models.reportOfReceipt

trait ReceiptedItemsModelFixtures extends BaseFixtures with UnsatisfactoryModelFixtures {

  val excessReceiptedItemsModel = reportOfReceipt.ReceiptedItemsModel(
    eadBodyUniqueReference = 1,
    productCode = "W300",
    excessAmount = Some(12.145),
    shortageAmount = None,
    refusedAmount = Some(10),
    unsatisfactoryReasons = Seq(maxUnsatisfactoryModel(Excess))
  )

  val excessReceiptedItemsModelXML =
    <urn:BodyReportOfReceiptExport>
      <urn:BodyRecordUniqueReference>1</urn:BodyRecordUniqueReference>
      <urn:IndicatorOfShortageOrExcess>E</urn:IndicatorOfShortageOrExcess>
      <urn:ObservedShortageOrExcess>12.145</urn:ObservedShortageOrExcess>
      <urn:ExciseProductCode>W300</urn:ExciseProductCode>
      <urn:RefusedQuantity>10</urn:RefusedQuantity>
      {maxUnsatisfactoryModelXML(Excess)}
    </urn:BodyReportOfReceiptExport>

  val shortageReceiptedItemsModel = reportOfReceipt.ReceiptedItemsModel(
    eadBodyUniqueReference = 1,
    productCode = "W300",
    excessAmount = None,
    shortageAmount = Some(12.145),
    refusedAmount = Some(10),
    unsatisfactoryReasons = Seq(
      maxUnsatisfactoryModel(Excess),
      maxUnsatisfactoryModel(BrokenSeals),
      maxUnsatisfactoryModel(Other),
      maxUnsatisfactoryModel(Damaged)
    )
  )

  val shortageReceiptedItemsModelXML =
    <urn:BodyReportOfReceiptExport>
      <urn:BodyRecordUniqueReference>1</urn:BodyRecordUniqueReference>
      <urn:IndicatorOfShortageOrExcess>S</urn:IndicatorOfShortageOrExcess>
      <urn:ObservedShortageOrExcess>12.145</urn:ObservedShortageOrExcess>
      <urn:ExciseProductCode>W300</urn:ExciseProductCode>
      <urn:RefusedQuantity>10</urn:RefusedQuantity>
      {maxUnsatisfactoryModelXML(Excess)}
      {maxUnsatisfactoryModelXML(BrokenSeals)}
      {maxUnsatisfactoryModelXML(Other)}
      {maxUnsatisfactoryModelXML(Damaged)}
    </urn:BodyReportOfReceiptExport>

  val minReceiptedItemsModel = reportOfReceipt.ReceiptedItemsModel(
    eadBodyUniqueReference = 1,
    productCode = "W300",
    excessAmount = None,
    shortageAmount = None,
    refusedAmount = None,
    unsatisfactoryReasons = Seq(
      minUnsatisfactoryModel(Damaged)
    )
  )

  val minReceiptedItemsModelXML =
    <urn:BodyReportOfReceiptExport>
      <urn:BodyRecordUniqueReference>1</urn:BodyRecordUniqueReference>
      <urn:ExciseProductCode>W300</urn:ExciseProductCode>
      {minUnsatisfactoryModelXML(Damaged)}
    </urn:BodyReportOfReceiptExport>
}
