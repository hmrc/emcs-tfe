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

package uk.gov.hmrc.emcstfe.services

import uk.gov.hmrc.emcstfe.fixtures.GetMovementFixture
import uk.gov.hmrc.emcstfe.mocks.connectors.MockChrisConnector
import uk.gov.hmrc.emcstfe.models.request.GetMovementRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{SoapExtractionError, XmlValidationError}
import uk.gov.hmrc.emcstfe.support.UnitSpec

import scala.concurrent.Future

class GetMovementServiceSpec extends UnitSpec with GetMovementFixture {
  trait Test extends MockChrisConnector {
    val getMovementRequest: GetMovementRequest = GetMovementRequest(exciseRegistrationNumber = "My ERN", arc = "My ARC")
    val service: GetMovementService = new GetMovementService(mockConnector)
  }

  "getMovement" should {
    "return a Right" when {
      "connector call is successful and XML is the correct format" in new Test {
        MockConnector
          .postChrisSOAPRequest(getMovementRequest)
          .returns(Future.successful(Right(getMovementResponse)))

        await(service.getMovement(getMovementRequest)) shouldBe Right(getMovementResponse)
      }
    }
    "return a Left" when {
      "connector call is unsuccessful" in new Test {
        MockConnector
          .postChrisSOAPRequest(getMovementRequest)
          .returns(Future.successful(Left(XmlValidationError)))

        await(service.getMovement(getMovementRequest)) shouldBe Left(XmlValidationError)
      }
      "connector call response cannot be extracted" in new Test {
        MockConnector
          .postChrisSOAPRequest(getMovementRequest)
          .returns(Future.successful(Left(SoapExtractionError)))

        await(service.getMovement(getMovementRequest)) shouldBe Left(SoapExtractionError)
      }
    }
  }
}
