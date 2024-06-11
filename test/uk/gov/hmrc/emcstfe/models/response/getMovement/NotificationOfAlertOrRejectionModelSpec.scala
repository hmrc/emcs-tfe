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
import uk.gov.hmrc.emcstfe.models.alertOrRejection.AlertOrRejectionReasonType.{EADNotConcernRecipient, ProductDoesNotMatchOrder, QuantityDoesNotMatchOrder}
import uk.gov.hmrc.emcstfe.models.alertOrRejection.AlertOrRejectionType.{Alert, Rejection}
import uk.gov.hmrc.emcstfe.models.common.Enumerable.EnumerableXmlParseFailure
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import java.time.LocalDateTime
import scala.xml.XML

class NotificationOfAlertOrRejectionModelSpec extends TestBaseSpec with GetMovementFixture {

  case class TestModel(alertReject: Seq[NotificationOfAlertOrRejectionModel])
  object TestModel {
    implicit lazy val xmlReads: XmlReader[TestModel] =
      for {
        alertReject <- (__ \\ "IE819").read(strictReadSeq(NotificationOfAlertOrRejectionModel.xmlReads))
      } yield TestModel(alertReject)
  }

  "xmlReads" should {

    "successfully read a subset of the IE803" in {
      TestModel.xmlReads.read(XML.loadString(maxGetMovementResponseBody())) shouldBe ParseSuccess(
        TestModel(Seq(
          NotificationOfAlertOrRejectionModel(
            notificationType = Alert,
            notificationDateAndTime = LocalDateTime.of(2023, 12, 18, 9, 0, 0),
            alertRejectReason = ProductDoesNotMatchOrder
          ),
          NotificationOfAlertOrRejectionModel(
            notificationType = Alert,
            notificationDateAndTime = LocalDateTime.of(2023, 12, 18, 10, 0, 0),
            alertRejectReason = EADNotConcernRecipient
          ),
          NotificationOfAlertOrRejectionModel(
            notificationType = Rejection,
            notificationDateAndTime = LocalDateTime.of(2023, 12, 19, 9, 0, 0),
            alertRejectReason = QuantityDoesNotMatchOrder
          )
        )
      ))
    }

    "fail to read a subset of the movement fields when a field is missing" in {
      lazy val responseBodyWithoutNotificationType: String =
        s"""
           |<mov:eventHistory>
           |    <ie819:IE819 xmlns:ie819="ie819:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE819:V3.13">
           |        <ie819:Header>
           |          <urn:MessageSender>NDEA.GB</urn:MessageSender>
           |          <urn:MessageRecipient>NDEA.GB</urn:MessageRecipient>
           |          <urn:DateOfPreparation>2023-12-18</urn:DateOfPreparation>
           |          <urn:TimeOfPreparation>08:59:59.441503</urn:TimeOfPreparation>
           |          <urn:MessageIdentifier>9de3f13e-7559-4f4d-8851-b954b01210c0</urn:MessageIdentifier>
           |          <urn:CorrelationIdentifier>e8803427-c7e5-4539-83b7-d174f511e70c</urn:CorrelationIdentifier>
           |        </ie819:Header>
           |        <ie819:Body>
           |          <ie819:AlertOrRejectionOfEADESAD>
           |            <ie819:Attributes>
           |              <ie819:DateAndTimeOfValidationOfAlertRejection>2023-12-18T09:00:00</ie819:DateAndTimeOfValidationOfAlertRejection>
           |            </ie819:Attributes>
           |            <ie819:ConsigneeTrader language="en">
           |              <ie819:Traderid>GBWK123456789</ie819:Traderid>
           |              <ie819:TraderName>Bizz</ie819:TraderName>
           |              <ie819:StreetName>GRANGE CENTRAL</ie819:StreetName>
           |              <ie819:Postcode>tf3 4er</ie819:Postcode>
           |              <ie819:City>Shropshire</ie819:City>
           |            </ie819:ConsigneeTrader>
           |            <ie819:ExciseMovement>
           |              <ie819:AdministrativeReferenceCode>18GB00000000000232361</ie819:AdministrativeReferenceCode>
           |              <ie819:SequenceNumber>1</ie819:SequenceNumber>
           |            </ie819:ExciseMovement>
           |            <ie819:DestinationOffice>
           |              <ie819:ReferenceNumber>GB004098</ie819:ReferenceNumber>
           |            </ie819:DestinationOffice>
           |            <ie819:AlertOrRejection>
           |              <ie819:DateOfAlertOrRejection>2023-12-18</ie819:DateOfAlertOrRejection>
           |            </ie819:AlertOrRejection>
           |            <ie819:AlertOrRejectionOfEadEsadReason>
           |              <ie819:AlertOrRejectionOfMovementReasonCode>2</ie819:AlertOrRejectionOfMovementReasonCode>
           |            </ie819:AlertOrRejectionOfEadEsadReason>
           |          </ie819:AlertOrRejectionOfEADESAD>
           |        </ie819:Body>
           |      </ie819:IE819>
           |</mov:eventHistory>
           |""".stripMargin

      NotificationOfAlertOrRejectionModel.xmlReads.read(XML.loadString(responseBodyWithoutNotificationType)) shouldBe ParseFailure(Seq(
        EnumerableXmlParseFailure("Invalid enumerable value of '' for field 'EadEsadRejectedFlag'")
      ))
    }

  }
}
