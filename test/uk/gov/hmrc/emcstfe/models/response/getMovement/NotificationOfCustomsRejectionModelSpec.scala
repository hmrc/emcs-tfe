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
import uk.gov.hmrc.emcstfe.models.common.{AddressModel, TraderModel}
import uk.gov.hmrc.emcstfe.models.response.getMovement.CustomsRejectionDiagnosisCodeType.{DestinationTypeIsNotExport, WeightMismatch}
import uk.gov.hmrc.emcstfe.models.response.getMovement.CustomsRejectionReasonCodeType.ExportDataNotFound
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import java.time.LocalDateTime
import scala.xml.XML

class NotificationOfCustomsRejectionModelSpec extends TestBaseSpec with GetMovementFixture {

  case class TestModel(notificationOfCustomsRejectionModel: NotificationOfCustomsRejectionModel)
  object TestModel {
    implicit lazy val xmlReads: XmlReader[TestModel] =
      for {
        notificationOfCustomsRejectionModel <- (__ \\ "IE839").read(NotificationOfCustomsRejectionModel.xmlReads)
      } yield TestModel(notificationOfCustomsRejectionModel)
  }


  "NotificationOfCustomsRejectionModel" should {

    "xmlReads" should {

      "successfully read an IE839" in {
        TestModel.xmlReads.read(XML.loadString(maxGetMovementResponseBody())) shouldBe ParseSuccess(TestModel(
          NotificationOfCustomsRejectionModel(
            customsOfficeReferenceNumber = Some("AT001000"),
            rejectionDateAndTime = LocalDateTime.of(2024, 1, 14, 19, 14, 20),
            rejectionReasonCode = ExportDataNotFound,
            localReferenceNumber = Some("1111"),
            documentReferenceNumber = Some("7884"),
            diagnoses = Seq(
              CustomsRejectionDiagnosis(
                bodyRecordUniqueReference = "100",
                diagnosisCode = DestinationTypeIsNotExport
              ),
              CustomsRejectionDiagnosis(
                bodyRecordUniqueReference = "101",
                diagnosisCode = DestinationTypeIsNotExport
              ),
              CustomsRejectionDiagnosis(
                bodyRecordUniqueReference = "102",
                diagnosisCode = DestinationTypeIsNotExport
              ),
              CustomsRejectionDiagnosis(
                bodyRecordUniqueReference = "103",
                diagnosisCode = DestinationTypeIsNotExport
              )
            ),
            consignee = Some(
              TraderModel(
                traderExciseNumber = Some("XIWK000000206"),
                traderName = Some("SEED TRADER NI"),
                address = Some(
                  AddressModel(
                    streetNumber = Some("1"),
                    street = Some("Catherdral"),
                    postcode = Some("BT3 7BF"),
                    city = Some("Salford")
                  )),
                vatNumber = None,
                eoriNumber = None
              )
            )
          )
        ))
      }

      "successfully read an IE839 (with an empty consignee)" in {
        lazy val responseBodyWithEmptyConsignee: String =
          s"""
             |<mov:eventHistory>
             |  <ie839:IE839 xmlns="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE839:V3.13" xmlns:urn="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:TMS:V3.13">
             |	    <ie839:Header>
             |		  <urn:MessageSender>NDEA.AT</urn:MessageSender>
             |		  <urn:MessageRecipient>NDEA.XI</urn:MessageRecipient>
             |		  <urn:DateOfPreparation>2024-01-14</urn:DateOfPreparation>
             |		  <urn:TimeOfPreparation>20:02:21.879</urn:TimeOfPreparation>
             |		  <urn:MessageIdentifier>GB10111000036818</urn:MessageIdentifier>
             |		  <urn:CorrelationIdentifier>AT10007777600036751</urn:CorrelationIdentifier>
             |	    </ie839:Header>
             |	    <ie839:Body>
             |		  <ie839:RefusalByCustoms>
             |			<ie839:Attributes>
             |				<ie839:DateAndTimeOfIssuance>2024-01-14T19:14:20</ie839:DateAndTimeOfIssuance>
             |			</ie839:Attributes>
             |   		<ie839:ConsigneeTrader language="en"/>
             |			<ie839:ExportPlaceCustomsOffice>
             |				<ie839:ReferenceNumber>AT001000</ie839:ReferenceNumber>
             |			</ie839:ExportPlaceCustomsOffice>
             |			<ie839:Rejection>
             |				<ie839:RejectionDateAndTime>2024-01-14T19:14:20</ie839:RejectionDateAndTime>
             |				<ie839:RejectionReasonCode>3</ie839:RejectionReasonCode>
             |			</ie839:Rejection>
             |			<ie839:ExportDeclarationInformation>
             |				<ie839:LocalReferenceNumber>1111</ie839:LocalReferenceNumber>
             |				<ie839:DocumentReferenceNumber>7884</ie839:DocumentReferenceNumber>
             |				<ie839:NegativeCrosscheckValidationResults>
             |					<ie839:UbrCrosscheckResult>
             |						<ie839:AdministrativeReferenceCode>3</ie839:AdministrativeReferenceCode>
             |						<ie839:SequenceNumber>11</ie839:SequenceNumber>
             |						<ie839:BodyRecordUniqueReference>124</ie839:BodyRecordUniqueReference>
             |						<ie839:DiagnosisCode>5</ie839:DiagnosisCode>
             |						<ie839:ValidationResult>b</ie839:ValidationResult>
             |						<ie839:RejectionReason>6</ie839:RejectionReason>
             |						<ie839:CombinedNomenclatureCodeCrosscheckResult>
             |							<ie839:ValidationResult>C</ie839:ValidationResult>
             |							<ie839:RejectionReason>7</ie839:RejectionReason>
             |						</ie839:CombinedNomenclatureCodeCrosscheckResult>
             |						<ie839:NetMassCrosscheckResult>
             |							<ie839:ValidationResult>D</ie839:ValidationResult>
             |							<ie839:RejectionReason>8</ie839:RejectionReason>
             |						</ie839:NetMassCrosscheckResult>
             |					</ie839:UbrCrosscheckResult>
             |				</ie839:NegativeCrosscheckValidationResults>
             |        <ie839:NegativeCrosscheckValidationResults>
             |					<ie839:UbrCrosscheckResult>
             |						<ie839:AdministrativeReferenceCode>3</ie839:AdministrativeReferenceCode>
             |						<ie839:SequenceNumber>11</ie839:SequenceNumber>
             |						<ie839:BodyRecordUniqueReference>125</ie839:BodyRecordUniqueReference>
             |						<ie839:DiagnosisCode>4</ie839:DiagnosisCode>
             |						<ie839:ValidationResult>b</ie839:ValidationResult>
             |						<ie839:RejectionReason>6</ie839:RejectionReason>
             |						<ie839:CombinedNomenclatureCodeCrosscheckResult>
             |							<ie839:ValidationResult>C</ie839:ValidationResult>
             |							<ie839:RejectionReason>7</ie839:RejectionReason>
             |						</ie839:CombinedNomenclatureCodeCrosscheckResult>
             |						<ie839:NetMassCrosscheckResult>
             |							<ie839:ValidationResult>D</ie839:ValidationResult>
             |							<ie839:RejectionReason>8</ie839:RejectionReason>
             |						</ie839:NetMassCrosscheckResult>
             |					</ie839:UbrCrosscheckResult>
             |				</ie839:NegativeCrosscheckValidationResults>
             |				<ie839:NNonDes>
             |					<ie839:DocumentReferenceNumber>9999</ie839:DocumentReferenceNumber>
             |				</ie839:NNonDes>
             |			</ie839:ExportDeclarationInformation>
             |			<ie839:CEadVal>
             |				<ie839:AdministrativeReferenceCode>24XI00000000000100271</ie839:AdministrativeReferenceCode>
             |				<ie839:SequenceNumber>1</ie839:SequenceNumber>
             |			</ie839:CEadVal>
             |		  </ie839:RefusalByCustoms>
             |	    </ie839:Body>
             |  </ie839:IE839>
             |</mov:eventHistory>
             |""".stripMargin

        TestModel.xmlReads.read(XML.loadString(responseBodyWithEmptyConsignee)) shouldBe ParseSuccess(TestModel(
          NotificationOfCustomsRejectionModel(
            customsOfficeReferenceNumber = Some("AT001000"),
            rejectionDateAndTime = LocalDateTime.of(2024, 1, 14, 19, 14, 20),
            rejectionReasonCode = ExportDataNotFound,
            localReferenceNumber = Some("1111"),
            documentReferenceNumber = Some("7884"),
            diagnoses = Seq(
              CustomsRejectionDiagnosis(
                bodyRecordUniqueReference = "124",
                diagnosisCode = DestinationTypeIsNotExport
              ),
              CustomsRejectionDiagnosis(
                bodyRecordUniqueReference = "125",
                diagnosisCode = WeightMismatch
              )
            ),
            consignee = None
          )
        ))
      }

      "fail to read a subset of the movement fields when a field is missing" in {
        lazy val responseBodyWithoutNotificationType: String =
          s"""
             |<mov:eventHistory>
             |  <ie839:IE839 xmlns="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE839:V3.13" xmlns:urn="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:TMS:V3.13">
             |	    <ie839:Header>
             |		  <urn:MessageSender>NDEA.AT</urn:MessageSender>
             |		  <urn:MessageRecipient>NDEA.XI</urn:MessageRecipient>
             |		  <urn:DateOfPreparation>2024-01-14</urn:DateOfPreparation>
             |		  <urn:TimeOfPreparation>20:02:21.879</urn:TimeOfPreparation>
             |		  <urn:MessageIdentifier>GB10111000036818</urn:MessageIdentifier>
             |		  <urn:CorrelationIdentifier>AT10007777600036751</urn:CorrelationIdentifier>
             |	    </ie839:Header>
             |	    <ie839:Body>
             |		  <ie839:RefusalByCustoms>
             |			<ie839:Attributes>
             |				<ie839:DateAndTimeOfIssuance>2024-01-14T19:14:20</ie839:DateAndTimeOfIssuance>
             |			</ie839:Attributes>
             |   		<ie839:ConsigneeTrader language="en">
             |              <ie839:Traderid>XIWK000000206</ie839:Traderid>
             |              <ie839:TraderName>SEED TRADER NI</ie839:TraderName>
             |              <ie839:StreetName>Catherdral</ie839:StreetName>
             |              <ie839:StreetNumber>1</ie839:StreetNumber>
             |              <ie839:Postcode>BT3 7BF</ie839:Postcode>
             |              <ie839:City>Salford</ie839:City>
             |			</ie839:ConsigneeTrader>
             |			<ie839:ExportPlaceCustomsOffice>
             |				<ie839:ReferenceNumber>AT001000</ie839:ReferenceNumber>
             |			</ie839:ExportPlaceCustomsOffice>
             |			<ie839:Rejection>
             |				<ie839:RejectionDateAndTime>2024-01-14T19:14:20</ie839:RejectionDateAndTime>
             |				<ie839:RejectionReasonCode>3</ie839:RejectionReasonCode>
             |			</ie839:Rejection>
             |			<ie839:ExportDeclarationInformation>
             |				<ie839:LocalReferenceNumber>1111</ie839:LocalReferenceNumber>
             |				<ie839:DocumentReferenceNumber>7884</ie839:DocumentReferenceNumber>
             |				<ie839:NegativeCrosscheckValidationResults>
             |					<ie839:UbrCrosscheckResult>
             |						<ie839:AdministrativeReferenceCode>3</ie839:AdministrativeReferenceCode>
             |						<ie839:SequenceNumber>11</ie839:SequenceNumber>
             |						<!-- <ie839:BodyRecordUniqueReference>124</ie839:BodyRecordUniqueReference> -->
             |						<ie839:DiagnosisCode>5</ie839:DiagnosisCode>
             |						<ie839:ValidationResult>b</ie839:ValidationResult>
             |						<ie839:RejectionReason>6</ie839:RejectionReason>
             |						<ie839:CombinedNomenclatureCodeCrosscheckResult>
             |							<ie839:ValidationResult>C</ie839:ValidationResult>
             |							<ie839:RejectionReason>7</ie839:RejectionReason>
             |						</ie839:CombinedNomenclatureCodeCrosscheckResult>
             |						<ie839:NetMassCrosscheckResult>
             |							<ie839:ValidationResult>D</ie839:ValidationResult>
             |							<ie839:RejectionReason>8</ie839:RejectionReason>
             |						</ie839:NetMassCrosscheckResult>
             |					</ie839:UbrCrosscheckResult>
             |				</ie839:NegativeCrosscheckValidationResults>
             |				<ie839:NNonDes>
             |					<ie839:DocumentReferenceNumber>9999</ie839:DocumentReferenceNumber>
             |				</ie839:NNonDes>
             |			</ie839:ExportDeclarationInformation>
             |			<ie839:CEadVal>
             |				<ie839:AdministrativeReferenceCode>24XI00000000000100271</ie839:AdministrativeReferenceCode>
             |				<ie839:SequenceNumber>1</ie839:SequenceNumber>
             |			</ie839:CEadVal>
             |		  </ie839:RefusalByCustoms>
             |	    </ie839:Body>
             |  </ie839:IE839>
             |</mov:eventHistory>
             |""".stripMargin

        NotificationOfCustomsRejectionModel.xmlReads.read(XML.loadString(responseBodyWithoutNotificationType)) shouldBe ParseFailure(Seq(
          EmptyError(__ \\ "UbrCrosscheckResult" \\ "BodyRecordUniqueReference")
        ))
      }

    }
  }
}
