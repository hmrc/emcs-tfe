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

import com.lucidchart.open.xtract.{ParseFailure, ParseSuccess}
import uk.gov.hmrc.emcstfe.fixtures.GetMovementFixture
import uk.gov.hmrc.emcstfe.models.common.Enumerable.EnumerableXmlParseFailure
import uk.gov.hmrc.emcstfe.models.response.getMovement.NotificationOfDivertedMovementType.ChangeOfDestination
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import java.time.LocalDateTime
import scala.xml.XML

class NotificationOfDivertedMovementModelSpec extends TestBaseSpec with GetMovementFixture {

  "xmlReads" should {

    "successfully read a subset of the IE803" in {
      NotificationOfDivertedMovementModel.xmlReads.read(XML.loadString(maxGetMovementResponseBody())) shouldBe ParseSuccess(NotificationOfDivertedMovementModel(
        notificationType = ChangeOfDestination,
        notificationDateAndTime = LocalDateTime.of(2024, 6, 5, 0, 0, 1),
        downstreamArcs = Seq(testArc, testArc + "1")
      ))
    }

    "fail to read a subset of the movement fields when a field is missing" in {
      lazy val responseBodyWithoutNotificationType: String =
        s"""
           |<mov:eventHistory>
           |    <urn:IE803 xmlns:ie803="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE803:V3.13">
           |      <urn:Header>
           |        <urn:MessageSender>NDEA.GB</urn:MessageSender>
           |        <urn:MessageRecipient>NDEA.GB</urn:MessageRecipient>
           |        <urn:DateOfPreparation>2020-12-03</urn:DateOfPreparation>
           |        <urn:TimeOfPreparation>13:36:43.326</urn:TimeOfPreparation>
           |        <urn:MessageIdentifier>GB100000000289576</urn:MessageIdentifier>
           |      </urn:Header>
           |      <urn:Body>
           |        <urn:NotificationOfDivertedEADESAD>
           |          <urn:ExciseNotification>
           |            <urn:NotificationDateAndTime>2024-06-05T00:00:01</urn:NotificationDateAndTime>
           |            <urn:AdministrativeReferenceCode>20GB00000000000341760</urn:AdministrativeReferenceCode>
           |            <urn:SequenceNumber>1</urn:SequenceNumber>
           |          </urn:ExciseNotification>
           |          <urn:DownstreamArc>
           |            <urn:AdministrativeReferenceCode>$testArc</urn:AdministrativeReferenceCode>
           |          </urn:DownstreamArc>
           |          <urn:DownstreamArc>
           |            <urn:AdministrativeReferenceCode>${testArc}1</urn:AdministrativeReferenceCode>
           |          </urn:DownstreamArc>
           |        </urn:NotificationOfDivertedEADESAD>
           |      </urn:Body>
           |    </urn:IE803>
           |</mov:eventHistory>
           |""".stripMargin

      NotificationOfDivertedMovementModel.xmlReads.read(XML.loadString(responseBodyWithoutNotificationType)) shouldBe ParseFailure(Seq(
        EnumerableXmlParseFailure("Invalid enumerable value of '' for field 'NotificationType'")
      ))
    }

  }
}
