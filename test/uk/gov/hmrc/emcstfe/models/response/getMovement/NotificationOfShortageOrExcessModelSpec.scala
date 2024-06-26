/*
 * Copyright 2024 HM Revenue & Customs
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

package uk.gov.hmrc.emcstfe.models.response.getMovement

import com.lucidchart.open.xtract._
import uk.gov.hmrc.emcstfe.fixtures.GetMovementFixture
import uk.gov.hmrc.emcstfe.models.common.Enumerable.EnumerableXmlParseFailure
import uk.gov.hmrc.emcstfe.models.common.SubmitterType
import uk.gov.hmrc.emcstfe.models.explainShortageExcess.BodyAnalysisModel
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.xml.XML

class NotificationOfShortageOrExcessModelSpec extends TestBaseSpec with GetMovementFixture {

  case class TestModel(model: NotificationOfShortageOrExcessModel)
  object TestModel {
    implicit lazy val xmlReads: XmlReader[TestModel] =
      for {
        model <- (__ \\ "IE871").read(NotificationOfShortageOrExcessModel.xmlReads)
      } yield TestModel(model)
  }


  "NotificationOfShortageOrExcessModel" should {

    "xmlReads" should {

      "successfully read an IE871" in {
        TestModel.xmlReads.read(XML.loadString(maxGetMovementResponseBody())) shouldBe ParseSuccess(TestModel(
          NotificationOfShortageOrExcessModel(
            submitterType = SubmitterType.Consignee,
            globalExplanation = None,
            individualItemReasons = Some(Seq(
              BodyAnalysisModel(
                exciseProductCode = "B000",
                bodyRecordUniqueReference = 1,
                explanation = "4 more than I expected",
                actualQuantity = Some(5)
              )
            ))
          )
        ))
      }

      "successfully read an IE871 (for whole movement)" in {
        lazy val response: String =
          s"""
             |<mov:eventHistory>
             |  <ie871:IE871 xmlns:ie871="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE871:V3.13">
             |    <ie871:Header>
             |        <urn:MessageSender>NDEA.GB</urn:MessageSender>
             |        <urn:MessageRecipient>NDEA.GB</urn:MessageRecipient>
             |        <urn:DateOfPreparation>2024-06-24</urn:DateOfPreparation>
             |        <urn:TimeOfPreparation>12:34:43.545475</urn:TimeOfPreparation>
             |        <urn:MessageIdentifier>e49b3d24-808e-4922-96b8-a3b74283b8e3</urn:MessageIdentifier>
             |        <urn:CorrelationIdentifier>PORTAL5402a4dfa60c4abdb06056313e08bf54</urn:CorrelationIdentifier>
             |    </ie871:Header>
             |    <ie871:Body>
             |        <ie871:ExplanationOnReasonForShortage>
             |            <ie871:Attributes>
             |                <ie871:SubmitterType>1</ie871:SubmitterType>
             |            </ie871:Attributes>
             |            <ie871:ExciseMovement>
             |                <ie871:AdministrativeReferenceCode>23GB00000000000380611</ie871:AdministrativeReferenceCode>
             |                <ie871:SequenceNumber>1</ie871:SequenceNumber>
             |            </ie871:ExciseMovement>
             |            <ie871:ConsignorTrader language="en">
             |                <ie871:TraderExciseNumber>GBWK001234569</ie871:TraderExciseNumber>
             |                <ie871:TraderName>TEST COMPANY 1</ie871:TraderName>
             |                <ie871:StreetName>Joke Street</ie871:StreetName>
             |                <ie871:StreetNumber>1</ie871:StreetNumber>
             |                <ie871:Postcode>JO11KE</ie871:Postcode>
             |                <ie871:City>Joke town</ie871:City>
             |            </ie871:ConsignorTrader>
             |            <ie871:Analysis>
             |                <ie871:DateOfAnalysis>2024-06-24</ie871:DateOfAnalysis>
             |                <ie871:GlobalExplanation language="en">Shortage</ie871:GlobalExplanation>
             |            </ie871:Analysis>
             |        </ie871:ExplanationOnReasonForShortage>
             |    </ie871:Body>
             |  </ie871:IE871>
             |</mov:eventHistory>
             |""".stripMargin

        TestModel.xmlReads.read(XML.loadString(response)) shouldBe ParseSuccess(TestModel(
          NotificationOfShortageOrExcessModel(
            submitterType = SubmitterType.Consignor,
            globalExplanation = Some("Shortage"),
            individualItemReasons = None
          )
        ))
      }

      "fail to read a subset of the movement fields when a mandatory field is missing" in {
        lazy val response: String =
          s"""
             |<mov:eventHistory>
             |  <!-- Explanation of Shortage or Excess (Excess - Individual items) -->
             |      <ie871:IE871 xmlns:ie871="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE871:V3.13">
             |          <ie871:Header>
             |              <urn:MessageSender>NDEA.GB</urn:MessageSender>
             |              <urn:MessageRecipient>NDEA.GB</urn:MessageRecipient>
             |              <urn:DateOfPreparation>2024-06-24</urn:DateOfPreparation>
             |              <urn:TimeOfPreparation>08:46:49.496924</urn:TimeOfPreparation>
             |              <urn:MessageIdentifier>69c710d7-abb6-4e3c-a571-9555f4a5c7f5</urn:MessageIdentifier>
             |              <urn:CorrelationIdentifier>PORTAL8515fc03cc404bc2ab201cfa4996fb2f</urn:CorrelationIdentifier>
             |          </ie871:Header>
             |          <ie871:Body>
             |              <ie871:ExplanationOnReasonForShortage>
             |                  <ie871:ConsigneeTrader language="en">
             |                      <ie871:Traderid>GBWK345678990</ie871:Traderid>
             |                      <ie871:TraderName>consignee business</ie871:TraderName>
             |                      <ie871:StreetName>Joke Street</ie871:StreetName>
             |                      <ie871:StreetNumber>1</ie871:StreetNumber>
             |                      <ie871:Postcode>JO11KE</ie871:Postcode>
             |                      <ie871:City>Joke town</ie871:City>
             |                  </ie871:ConsigneeTrader>
             |                  <ie871:ExciseMovement>
             |                      <ie871:AdministrativeReferenceCode>23GB00000000000380611</ie871:AdministrativeReferenceCode>
             |                      <ie871:SequenceNumber>1</ie871:SequenceNumber>
             |                  </ie871:ExciseMovement>
             |                  <ie871:BodyAnalysis>
             |                      <ie871:ExciseProductCode>B000</ie871:ExciseProductCode>
             |                      <ie871:BodyRecordUniqueReference>1</ie871:BodyRecordUniqueReference>
             |                      <ie871:Explanation language="en">4 more than I expected</ie871:Explanation>
             |                      <ie871:ActualQuantity>5</ie871:ActualQuantity>
             |                  </ie871:BodyAnalysis>
             |              </ie871:ExplanationOnReasonForShortage>
             |          </ie871:Body>
             |      </ie871:IE871>
             |</mov:eventHistory>
             |""".stripMargin

        NotificationOfShortageOrExcessModel.xmlReads.read(XML.loadString(response)) shouldBe ParseFailure(Seq(
          EnumerableXmlParseFailure("Invalid enumerable value of '' for field 'SubmitterType'")
        ))
      }

    }
  }
}
