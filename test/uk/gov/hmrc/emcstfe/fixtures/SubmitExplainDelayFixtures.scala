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

import uk.gov.hmrc.emcstfe.models.common.SubmitterType.Consignee
import uk.gov.hmrc.emcstfe.models.explainDelay.DelayReasonType.Strikes
import uk.gov.hmrc.emcstfe.models.explainDelay.DelayType.DelayWithReportReceipt
import uk.gov.hmrc.emcstfe.models.explainDelay.SubmitExplainDelayModel

trait SubmitExplainDelayFixtures extends BaseFixtures with ChRISResponsesFixture {

  val maxSubmitExplainDelayModel = SubmitExplainDelayModel(
    arc = testArc,
    sequenceNumber = 1,
    submitterType = Consignee,
    delayType = DelayWithReportReceipt,
    delayReasonType = Strikes,
    additionalInformation = Some("other")
  )

  val maxSubmitExplainDelayModelXML =
    <urn:ExplanationOnDelayForDelivery>
      <urn:Attributes>
        <urn:SubmitterIdentification>
          {testErn}
        </urn:SubmitterIdentification>
        <urn:SubmitterType>
          {Consignee.toString}
        </urn:SubmitterType>
        <urn:ExplanationCode>
          {Strikes.toString}
        </urn:ExplanationCode>
        <urn:ComplementaryInformation language="en">
          {"other"}
        </urn:ComplementaryInformation>
        <urn:MessageRole>
          {DelayWithReportReceipt.toString}
        </urn:MessageRole>
      </urn:Attributes>
      <urn:ExciseMovement>
        <urn:AdministrativeReferenceCode>
          {testArc}
        </urn:AdministrativeReferenceCode>
        <urn:SequenceNumber>
          1
        </urn:SequenceNumber>
      </urn:ExciseMovement>
    </urn:ExplanationOnDelayForDelivery>


  val minSubmitExplainDelayModel = SubmitExplainDelayModel(
    arc = testArc,
    sequenceNumber = 1,
    submitterType = Consignee,
    delayType = DelayWithReportReceipt,
    delayReasonType = Strikes,
    additionalInformation = None
  )

  val minSubmitExplainDelayModelXML =
    <urn:ExplanationOnDelayForDelivery>
      <urn:Attributes>
        <urn:SubmitterIdentification>
          {testErn}
        </urn:SubmitterIdentification>
        <urn:SubmitterType>
          {Consignee.toString}
        </urn:SubmitterType>
        <urn:ExplanationCode>
          {Strikes.toString}
        </urn:ExplanationCode>
        <urn:MessageRole>
          {DelayWithReportReceipt.toString}
        </urn:MessageRole>
      </urn:Attributes>
      <urn:ExciseMovement>
        <urn:AdministrativeReferenceCode>
          {testArc}
        </urn:AdministrativeReferenceCode>
        <urn:SequenceNumber>
          1
        </urn:SequenceNumber>
      </urn:ExciseMovement>
    </urn:ExplanationOnDelayForDelivery>


}
