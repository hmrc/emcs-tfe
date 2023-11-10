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

import play.api.libs.json.Json
import uk.gov.hmrc.emcstfe.models.alertOrRejection.AlertOrRejectionReasonType._
import uk.gov.hmrc.emcstfe.models.alertOrRejection.{AlertOrRejectionReasonModel, SubmitAlertOrRejectionModel}
import uk.gov.hmrc.emcstfe.models.common.{ConsigneeTrader, ExciseMovementModel}

import java.time.LocalDate

trait SubmitAlertOrRejectionFixtures extends BaseFixtures with TraderModelFixtures with ChRISResponsesFixture with EISResponsesFixture {

  val maxSubmitAlertOrRejectionModel = SubmitAlertOrRejectionModel(
    consigneeTrader = Some(maxTraderModel(ConsigneeTrader)),
    exciseMovement = ExciseMovementModel(testArc, 1),
    destinationOffice = testDestinationOffice,
    dateOfAlertOrRejection = LocalDate.of(2023, 7, 24),
    isRejected = true,
    alertOrRejectionReasons = Some(Seq(
      AlertOrRejectionReasonModel(
        reason = EADNotConcernRecipient,
        additionalInformation = Some("foo")
      ),
      AlertOrRejectionReasonModel(
        reason = ProductDoesNotMatchOrder,
        additionalInformation = None
      )
    ))
  )

  val maxSubmitAlertOrRejectionModelJson =
    Json.obj(
      "consigneeTrader" -> maxTraderModelJson(ConsigneeTrader),
      "exciseMovement" -> Json.obj(
        "arc" -> testArc,
        "sequenceNumber" -> 1
      ),
      "destinationOffice" -> testDestinationOffice,
      "dateOfAlertOrRejection" -> "2023-07-24",
      "isRejected" -> true,
      "alertOrRejectionReasons" -> Json.arr(
        Json.obj(
          "reason" -> EADNotConcernRecipient.toString,
          "additionalInformation" -> "foo"
        ),
        Json.obj(
          "reason" -> ProductDoesNotMatchOrder.toString
        )
      )
    )

  val maxSubmitAlertOrRejectionModelXML =
    <urn:AlertOrRejectionOfEADESAD>
      <urn:Attributes/>
      <urn:ConsigneeTrader language="en">
        {maxTraderModelXML(ConsigneeTrader)}
      </urn:ConsigneeTrader>
      <urn:ExciseMovement>
        <urn:AdministrativeReferenceCode>{testArc}</urn:AdministrativeReferenceCode>
        <urn:SequenceNumber>{1}</urn:SequenceNumber>
      </urn:ExciseMovement>
      <urn:DestinationOffice>
        <urn:ReferenceNumber>
          GB1234
        </urn:ReferenceNumber>
      </urn:DestinationOffice>
      <urn:AlertOrRejection>
        <urn:DateOfAlertOrRejection>
          2023-07-24
        </urn:DateOfAlertOrRejection>
        <urn:EadEsadRejectedFlag>
          1
        </urn:EadEsadRejectedFlag>
      </urn:AlertOrRejection>
      <urn:AlertOrRejectionOfEadEsadReason>
        <urn:AlertOrRejectionOfMovementReasonCode>
          {EADNotConcernRecipient.toString}
        </urn:AlertOrRejectionOfMovementReasonCode>
        <urn:ComplementaryInformation language="en">
          foo
        </urn:ComplementaryInformation>
      </urn:AlertOrRejectionOfEadEsadReason>
      <urn:AlertOrRejectionOfEadEsadReason>
        <urn:AlertOrRejectionOfMovementReasonCode>
          {ProductDoesNotMatchOrder.toString}
        </urn:AlertOrRejectionOfMovementReasonCode>
      </urn:AlertOrRejectionOfEadEsadReason>
    </urn:AlertOrRejectionOfEADESAD>

  val minSubmitAlertOrRejectionModel = SubmitAlertOrRejectionModel(
    consigneeTrader = None,
    exciseMovement = ExciseMovementModel(testArc, 1),
    destinationOffice = "GB1234",
    dateOfAlertOrRejection = LocalDate.of(2023, 7, 24),
    isRejected = false,
    alertOrRejectionReasons = None
  )

  val minSubmitAlertOrRejectionModelJson =
    Json.obj(
      "exciseMovement" -> Json.obj(
        "arc" -> testArc,
        "sequenceNumber" -> 1
      ),
      "destinationOffice" -> testDestinationOffice,
      "dateOfAlertOrRejection" -> "2023-07-24",
      "isRejected" -> false
    )

  val minSubmitAlertOrRejectionModelXML =
    <urn:AlertOrRejectionOfEADESAD>
      <urn:Attributes/>
      <urn:ExciseMovement>
        <urn:AdministrativeReferenceCode>
          {testArc}
        </urn:AdministrativeReferenceCode>
        <urn:SequenceNumber>
          {1}
        </urn:SequenceNumber>
      </urn:ExciseMovement>
      <urn:DestinationOffice>
        <urn:ReferenceNumber>
          GB1234
        </urn:ReferenceNumber>
      </urn:DestinationOffice>
      <urn:AlertOrRejection>
        <urn:DateOfAlertOrRejection>
          2023-07-24
        </urn:DateOfAlertOrRejection>
        <urn:EadEsadRejectedFlag>
          0
        </urn:EadEsadRejectedFlag>
      </urn:AlertOrRejection>
    </urn:AlertOrRejectionOfEADESAD>

}
