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

package uk.gov.hmrc.emcstfe.utils

import uk.gov.hmrc.emcstfe.fixtures.GetMovementFixture
import uk.gov.hmrc.emcstfe.support.UnitSpec

class HMRCMarkUtilSpec extends UnitSpec with GetMovementFixture {

  object TestHmrcMark extends HMRCMarkUtil

  "HMRCMarkUtil" should {

    "generate hash from XML" in {

      //  The test data below was taken from HMRC documentation here:
      //    - https://www.gov.uk/government/publications/hmrc-irmark-for-soap-protocol-services
      //  The HMRCMark generated below from the `canonical` XML matches the `full submission` HMRCMark in the examples

      val XML: String =
        """<SOAP-ENV:Body xmlns:SOAP-ENV="http://www.w3.org/2003/05/soap-envelope" xmlns:m0="http://emcs.dgtaxud.ec/v10/tms">
          |		<m:CD818A xmlns:m="http://emcs.dgtaxud.ec/v10/cd818/ie">
          |			<m:Header>
          |				<m0:MessageSender>SDS Team</m0:MessageSender>
          |				<m0:MessageRecipient>SDs Team</m0:MessageRecipient>
          |				<m0:DateOfPreparation>2011-03-08</m0:DateOfPreparation>
          |				<m0:TimeOfPreparation>10:43:00</m0:TimeOfPreparation>
          |				<m0:MessageIdentifier>gvdvcerhch123</m0:MessageIdentifier>
          |				<m0:CorrelationIdentifier>dwhcehfgy324dhd723e12382cdc</m0:CorrelationIdentifier>
          |			</m:Header>
          |			<m:Body>
          |				<m:AcceptedOrRejectedReportOfReceipt>
          |					<m:Attributes/>
          |					<m:ConsigneeTrader language="EN">
          |						<m:Traderid>jecbhjghj3ey3</m:Traderid>
          |						<m:TraderName>Pan European Express</m:TraderName>
          |						<m:StreetName>Tetley Street</m:StreetName>
          |						<m:StreetNumber>3</m:StreetNumber>
          |						<m:Postcode>BD1 2BX</m:Postcode>
          |						<m:City>Bradford</m:City>
          |					</m:ConsigneeTrader>
          |					<m:ExciseMovementEaad>
          |						<m:AadReferenceCode>00AA00000000000000000</m:AadReferenceCode>
          |						<m:SequenceNumber>0</m:SequenceNumber>
          |					</m:ExciseMovementEaad>
          |					<m:DeliveryPlaceTrader language="en">
          |						<m:Traderid>dchdgc237</m:Traderid>
          |						<m:TraderName>Trapist Beers to your door.com</m:TraderName>
          |						<m:StreetName>Monk Street</m:StreetName>
          |						<m:StreetNumber>21</m:StreetNumber>
          |						<m:Postcode>LS21 3DE</m:Postcode>
          |						<m:City>Leeds</m:City>
          |					</m:DeliveryPlaceTrader>
          |					<m:DestinationOffice>
          |						<m:ReferenceNumber>GB004098</m:ReferenceNumber>
          |					</m:DestinationOffice>
          |					<m:ReportOfReceipt>
          |						<m:DateOfArrivalOfExciseProducts>1967-08-13</m:DateOfArrivalOfExciseProducts>
          |						<m:GlobalConclusionOfReceipt>4</m:GlobalConclusionOfReceipt>
          |					</m:ReportOfReceipt>
          |					<m:BodyReportOfReceipt>
          |						<m:BodyRecordUniqueReference>0</m:BodyRecordUniqueReference>
          |						<m:IndicatorOfShortageOrExcess>E</m:IndicatorOfShortageOrExcess>
          |						<m:ObservedShortageOrExcess>0</m:ObservedShortageOrExcess>
          |						<m:ExciseProductCode>A000</m:ExciseProductCode>
          |						<m:RefusedQuantity>12</m:RefusedQuantity>
          |						<m:UnsatisfactoryReason>
          |							<m:UnsatisfactoryReasonCode>0</m:UnsatisfactoryReasonCode>
          |							<m:ComplementaryInformation language="EN">this packaging is dirty</m:ComplementaryInformation>
          |						</m:UnsatisfactoryReason>
          |					</m:BodyReportOfReceipt>
          |				</m:AcceptedOrRejectedReportOfReceipt>
          |			</m:Body>
          |		</m:CD818A>
          |	</SOAP-ENV:Body>""".stripMargin

      TestHmrcMark.createHmrcMark(XML.getBytes) shouldBe "tWL4cdhGrS/D7TdEMk+TvjTCESY="
    }
  }
}
