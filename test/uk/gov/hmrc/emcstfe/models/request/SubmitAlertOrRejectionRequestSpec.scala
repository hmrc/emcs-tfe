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
import uk.gov.hmrc.emcstfe.fixtures.SubmitAlertOrRejectionFixtures
import uk.gov.hmrc.emcstfe.models.common.{ConsigneeTrader, ExciseMovementModel}
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import java.util.Base64
import scala.xml.Utility.trim
import scala.xml.XML

class SubmitAlertOrRejectionRequestSpec extends TestBaseSpec with SubmitAlertOrRejectionFixtures {

  implicit val request: SubmitAlertOrRejectionRequest = SubmitAlertOrRejectionRequest(maxSubmitAlertOrRejectionModel)

  "for the MessageSender and MessageRecipient headers" when {

    val model = maxSubmitAlertOrRejectionModel.copy(exciseMovement = ExciseMovementModel("01DE0000012345", 1))

    "have the correct MessageSender" when {

      "consignee trader exists" in {
        SubmitAlertOrRejectionRequest(model.copy(consigneeTrader = Some(maxTraderModel(ConsigneeTrader).copy(traderExciseNumber = Some("FR00001"))))).messageSender shouldBe "NDEA.FR"
      }

      "consignee trader DOES NOT exist" in {
        SubmitAlertOrRejectionRequest(model.copy(consigneeTrader = None)).messageSender shouldBe "NDEA.GB"
      }
    }

    "have the correct MessageRecipient" in {
      SubmitAlertOrRejectionRequest(model).messageRecipient shouldBe "NDEA.DE"
    }
  }

  ".exciseRegistrationNumber" should {
    "be correct" in {
      request.exciseRegistrationNumber shouldBe testErn
    }
  }

  ".eisXMLBody" when {

    implicit val request = SubmitAlertOrRejectionRequest(maxSubmitAlertOrRejectionModel)

    "generate the correct XML body" in {
      val expectedRequest = wrapInControlDoc(
        <urn:IE819 xmlns:urn1="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:TMS:V3.13" xmlns:urn="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE819:V3.13">
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
            {maxSubmitAlertOrRejectionModelXML}
          </urn:Body>
        </urn:IE819>
      )

      val requestXml = XML.loadString(request.eisXMLBody())
      val expectedXml = trim(expectedRequest)

      requestXml shouldBe expectedXml
    }
  }

  ".toJson" should {
    "create the correct JSON body" in {
      val expectedResult = Json.obj(
        "user" -> testErn,
        "messageType" -> "IE819",
        "message" -> Base64.getEncoder.encodeToString(XML.loadString(request.eisXMLBody()).toString().getBytes)
      )
      request.toJson shouldBe expectedResult
    }
  }

}
