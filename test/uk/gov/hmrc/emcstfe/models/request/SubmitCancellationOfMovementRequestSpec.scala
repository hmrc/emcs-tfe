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

package uk.gov.hmrc.emcstfe.models.request

import play.api.libs.json.Json
import uk.gov.hmrc.emcstfe.fixtures.SubmitCancellationOfMovementFixtures
import uk.gov.hmrc.emcstfe.models.common.DestinationType.{ExemptedOrganisations, TaxWarehouse}
import uk.gov.hmrc.emcstfe.models.common.{ConsigneeTrader, ExciseMovementModel}
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import java.util.Base64
import scala.xml.Utility.trim
import scala.xml.XML

class SubmitCancellationOfMovementRequestSpec extends TestBaseSpec with SubmitCancellationOfMovementFixtures {

  implicit val request: SubmitCancellationOfMovementRequest = SubmitCancellationOfMovementRequest(maxSubmitCancellationOfMovementModel)

  "for the MessageSender and MessageRecipient headers" when {

    "have the correct MessageRecipient" when {

      "the destination type is Export" in {
        val testModel = maxSubmitCancellationOfMovementModel.copy(exciseMovement = ExciseMovementModel("01DE0000012345", 1))

        SubmitCancellationOfMovementRequest(testModel).messageRecipient shouldBe "NDEA.DE"
      }

      "the destination type is ExemptedOrganisations" when {

        "memberStateCode exists" in {
          val testModel = maxSubmitCancellationOfMovementModel.copy(destinationType = ExemptedOrganisations, memberStateCode = Some("FR"))

          SubmitCancellationOfMovementRequest(testModel).messageRecipient shouldBe "NDEA.FR"
        }

        "memberStateCode does NOT exists" in {
          val testModel = maxSubmitCancellationOfMovementModel.copy(destinationType = ExemptedOrganisations, memberStateCode = None)

          SubmitCancellationOfMovementRequest(testModel).messageRecipient shouldBe "NDEA.GB"
        }
      }

      "the destination type is any other type" when {

        "consignee trader exists, use traderId country code" in {
          val testModel = maxSubmitCancellationOfMovementModel.copy(
            destinationType = TaxWarehouse,
            consigneeTrader = Some(maxTraderModel(ConsigneeTrader).copy(traderExciseNumber = Some("FR00001")))
          )

          SubmitCancellationOfMovementRequest(testModel).messageRecipient shouldBe "NDEA.FR"
        }

        "consignee trader is None, default to GB" in {
          val testModel = maxSubmitCancellationOfMovementModel.copy(
            destinationType = TaxWarehouse,
            consigneeTrader = None
          )

          SubmitCancellationOfMovementRequest(testModel).messageRecipient shouldBe "NDEA.GB"
        }
      }
    }

    "have the correct MessageSender" in {
      SubmitCancellationOfMovementRequest(maxSubmitCancellationOfMovementModel).messageSender shouldBe "NDEA.GB"
    }
  }

  ".eisXMLBody" when {

    implicit val request = SubmitCancellationOfMovementRequest(maxSubmitCancellationOfMovementModel)

    "generate the correct XML body" in {
      val expectedRequest = wrapInControlDoc(
        <urn:IE810 xmlns:urn1="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:TMS:V3.13" xmlns:urn="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE810:V3.13">
          <urn:Header>
            <urn1:MessageSender>
              {request.messageSender}
            </urn1:MessageSender>
            <urn1:MessageRecipient>
              {request.messageRecipient}
            </urn1:MessageRecipient>
            <urn1:DateOfPreparation>
              {request.preparedDate.toString}
            </urn1:DateOfPreparation>
            <urn1:TimeOfPreparation>
              {request.preparedTime.toString}
            </urn1:TimeOfPreparation>
            <urn1:MessageIdentifier>
              {request.messageUUID}
            </urn1:MessageIdentifier>
            <urn1:CorrelationIdentifier>
              {request.correlationUUID}
            </urn1:CorrelationIdentifier>
          </urn:Header>
          <urn:Body>
            {maxSubmitCancellationOfMovementModelXml}
          </urn:Body>
        </urn:IE810>)

      val requestXml = XML.loadString(request.eisXMLBody())
      val expectedXml = trim(expectedRequest)

      requestXml shouldBe expectedXml
    }
  }

  ".toJson" should {
    "create the correct JSON body" in {
      val expectedResult = Json.obj(
        "user" -> testErn,
        "messageType" -> "IE810",
        "message" -> Base64.getEncoder.encodeToString(XML.loadString(request.eisXMLBody()).toString().getBytes)
      )
      request.toJson shouldBe expectedResult
    }
  }
}
