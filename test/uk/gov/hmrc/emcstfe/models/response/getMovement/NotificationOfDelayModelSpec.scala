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

import com.lucidchart.open.xtract.XmlReader.strictReadSeq
import com.lucidchart.open.xtract.{ParseFailure, ParseSuccess, XmlReader, __}
import uk.gov.hmrc.emcstfe.fixtures.GetMovementFixture
import uk.gov.hmrc.emcstfe.models.common.Enumerable.EnumerableXmlParseFailure
import uk.gov.hmrc.emcstfe.models.common.SubmitterType
import uk.gov.hmrc.emcstfe.models.explainDelay.DelayReasonType
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import java.time.LocalDateTime
import scala.xml.XML

class NotificationOfDelayModelSpec extends TestBaseSpec with GetMovementFixture {

  case class TestModel(explainDelay: Seq[NotificationOfDelayModel])
  object TestModel {
    implicit lazy val xmlReads: XmlReader[TestModel] =
      for {
        alertReject <- (__ \\ "IE837").read(strictReadSeq(NotificationOfDelayModel.xmlReads))
      } yield TestModel(alertReject)
  }

  "xmlReads" should {

    "successfully read a subset of the IE837" in {
      TestModel.xmlReads.read(XML.loadString(maxGetMovementResponseBody())) shouldBe ParseSuccess(
        TestModel(Seq(
          NotificationOfDelayModel(
            submitterIdentification = "GBWK001234569",
            submitterType = SubmitterType.Consignor,
            explanationCode = DelayReasonType.Accident,
            complementaryInformation = Some("Lorry crashed off cliff"),
            dateTime = LocalDateTime.parse("2024-06-18T08:11:33")
          ),
          NotificationOfDelayModel(
            submitterIdentification = "GBWK001234569",
            submitterType = SubmitterType.Consignor,
            explanationCode = DelayReasonType.Strikes,
            complementaryInformation = None,
            dateTime = LocalDateTime.parse("2024-06-18T08:18:56")
          )
        ))
      )
    }

    "fail to read a subset of the movement fields when a field is missing" in {
      lazy val responseBodyWithoutNotificationType: String =
        s"""
           |<mov:eventHistory>
           |  <ie837:IE837 xmlns:ie837="ie837:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE837:V3.13">
           |     <ie837:Header>
           |        <urn:MessageSender>NDEA.GB</urn:MessageSender>
           |        <urn:MessageRecipient>NDEA.GB</urn:MessageRecipient>
           |        <urn:DateOfPreparation>2024-06-18</urn:DateOfPreparation>
           |        <urn:TimeOfPreparation>07:18:54.852159</urn:TimeOfPreparation>
           |        <urn:MessageIdentifier>GB100000000305527</urn:MessageIdentifier>
           |        <urn:CorrelationIdentifier>PORTAL07498cf951004becbc3c73c14c103b13</urn:CorrelationIdentifier>
           |     </ie837:Header>
           |     <ie837:Body>
           |        <ie837:ExplanationOnDelayForDelivery>
           |           <ie837:Attributes>
           |              <ie837:SubmitterIdentification>GBWK001234569</ie837:SubmitterIdentification>
           |              <ie837:ExplanationCode>5</ie837:ExplanationCode>
           |              <ie837:MessageRole>2</ie837:MessageRole>
           |              <ie837:DateAndTimeOfValidationOfExplanationOnDelay>2024-06-18T08:18:56</ie837:DateAndTimeOfValidationOfExplanationOnDelay>
           |           </ie837:Attributes>
           |           <ie837:ExciseMovement>
           |              <ie837:AdministrativeReferenceCode>18GB00000000000232361</ie837:AdministrativeReferenceCode>
           |              <ie837:SequenceNumber>1</ie837:SequenceNumber>
           |           </ie837:ExciseMovement>
           |        </ie837:ExplanationOnDelayForDelivery>
           |     </ie837:Body>
           |  </ie837:IE837>
           |</mov:eventHistory>
           |""".stripMargin

      NotificationOfDelayModel.xmlReads.read(XML.loadString(responseBodyWithoutNotificationType)) shouldBe ParseFailure(Seq(
        EnumerableXmlParseFailure("Invalid enumerable value of '' for field 'SubmitterType'")
      ))
    }

  }
}
