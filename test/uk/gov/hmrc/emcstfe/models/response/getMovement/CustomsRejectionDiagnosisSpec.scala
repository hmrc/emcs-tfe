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
import uk.gov.hmrc.emcstfe.models.response.getMovement.CustomsRejectionDiagnosisCodeType.DestinationTypeIsNotExport
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.xml.XML

class CustomsRejectionDiagnosisSpec extends TestBaseSpec with GetMovementFixture {

  "CustomsRejectionDiagnosis" should {

    "xmlReads" should {

      "successfully read a subset of the IE839" in {
        lazy val responseBody: String =
          s"""
             |<mov:eventHistory>
             |  <ie839:IE839 xmlns="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE839:V3.13" xmlns:urn="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:TMS:V3.13">
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
             |  </ie839:IE839>
             |</mov:eventHistory>
             |""".stripMargin
        CustomsRejectionDiagnosis.xmlReads.read(XML.loadString(responseBody)) shouldBe ParseSuccess(CustomsRejectionDiagnosis(
          bodyRecordUniqueReference = "124",
          diagnosisCode = DestinationTypeIsNotExport
        ))
      }

      "fail to read a subset of the movement fields when a field is missing" in {
        lazy val responseBodyWithoutBodyRecordUniqueReference: String =
          s"""
             |<mov:eventHistory>
             |  <ie839:IE839 xmlns="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE839:V3.13" xmlns:urn="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:TMS:V3.13">
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
             |  </ie839:IE839>
             |</mov:eventHistory>
             |""".stripMargin

        CustomsRejectionDiagnosis.xmlReads.read(XML.loadString(responseBodyWithoutBodyRecordUniqueReference)) shouldBe ParseFailure(Seq(
          EmptyError(__ \\ "BodyRecordUniqueReference")
        ))
      }

    }
  }
}
