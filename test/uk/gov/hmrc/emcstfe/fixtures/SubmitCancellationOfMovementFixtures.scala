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

import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.emcstfe.models.cancellationOfMovement.CancellationReasonType.TypingError
import uk.gov.hmrc.emcstfe.models.cancellationOfMovement.{CancellationReasonModel, SubmitCancellationOfMovementModel}
import uk.gov.hmrc.emcstfe.models.common.DestinationType.Export
import uk.gov.hmrc.emcstfe.models.common.ExciseMovementModel

import java.time.LocalDate
import scala.xml.Elem

trait SubmitCancellationOfMovementFixtures extends BaseFixtures with ChRISResponsesFixture with TraderModelFixtures {

  val maxSubmitCancellationOfMovementModel: SubmitCancellationOfMovementModel = SubmitCancellationOfMovementModel(
    dateAndTimeOfValidationOfCancellation = LocalDate.of(2023, 1, 1),
    exciseMovement = ExciseMovementModel(testArc, 1),
    cancellationReason = CancellationReasonModel(TypingError, Some("test cancellation reason")),
    consigneeTrader = Some(maxTraderModel),
    destinationType = Export,
    memberStateCode = "GB"
  )

  val minSubmitCancellationOfMovementModel: SubmitCancellationOfMovementModel = SubmitCancellationOfMovementModel(
    dateAndTimeOfValidationOfCancellation = LocalDate.of(2023, 1, 1),
    exciseMovement = ExciseMovementModel(testArc, 1),
    cancellationReason = CancellationReasonModel(TypingError, None),
    consigneeTrader = Some(maxTraderModel),
    destinationType = Export,
    memberStateCode = "GB"
  )

  val maxSubmitCancellationOfMovementModelJson: JsObject = Json.obj(
    "dateAndTimeOfValidationOfCancellation" -> "2023-01-01",
    "exciseMovement" ->
      Json.obj(
        "arc" -> testArc,
        "sequenceNumber" -> 1
      ),
    "cancellationReason" ->
      Json.obj(
        "reason" -> TypingError.toString,
        "complementaryInformation" -> "test cancellation reason"
      ),
    "consigneeTrader" -> maxTraderModelJson,
    "destinationType" -> Export.toString,
    "memberStateCode" -> "GB"
  )

  val minSubmitCancellationOfMovementModelJson: JsObject = Json.obj(
    "dateAndTimeOfValidationOfCancellation" -> "2023-01-01",
    "exciseMovement" ->
      Json.obj(
        "arc" -> testArc,
        "sequenceNumber" -> 1
      ),
    "cancellationReason" -> Json.obj("reason" -> TypingError.toString),
    "consigneeTrader" -> maxTraderModelJson,
    "destinationType" -> Export.toString,
    "memberStateCode" -> "GB"
  )

  val maxSubmitCancellationOfMovementModelXml: Elem =
    <urn:CancellationOfEAD>
      <urn:Attributes/>
      <urn:ExciseMovementEad>
        <urn:AdministrativeReferenceCode>{testArc}</urn:AdministrativeReferenceCode>
      </urn:ExciseMovementEad>
      <urn:Cancellation>
        <urn:CancellationReasonCode>{TypingError.toString}</urn:CancellationReasonCode>
        <urn:ComplementaryInformation language="en">test cancellation reason</urn:ComplementaryInformation>
      </urn:Cancellation>
    </urn:CancellationOfEAD>

  val minSubmitCancellationOfMovementModelXml: Elem =
    <urn:CancellationOfEAD>
      <urn:Attributes/>
      <urn:ExciseMovementEad>
        <urn:AdministrativeReferenceCode>{testArc}</urn:AdministrativeReferenceCode>
      </urn:ExciseMovementEad>
      <urn:Cancellation>
        <urn:CancellationReasonCode>{TypingError.toString}</urn:CancellationReasonCode>
      </urn:Cancellation>
    </urn:CancellationOfEAD>

}
