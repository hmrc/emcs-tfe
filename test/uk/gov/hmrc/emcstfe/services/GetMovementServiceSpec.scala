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

import play.api.test.FakeRequest
import uk.gov.hmrc.emcstfe.fixtures.GetMovementFixture
import uk.gov.hmrc.emcstfe.mocks.connectors.MockChrisConnector
import uk.gov.hmrc.emcstfe.mocks.repository.MockGetMovementRepository
import uk.gov.hmrc.emcstfe.mocks.utils.MockSoapUtils
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.mongo.GetMovementMongoResponse
import uk.gov.hmrc.emcstfe.models.request.{GetMovementIfChangedRequest, GetMovementRequest}
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{MongoError, SoapExtractionError, XmlValidationError}
import uk.gov.hmrc.emcstfe.support.UnitSpec

import scala.concurrent.Future

class GetMovementServiceSpec extends UnitSpec with GetMovementFixture {
  trait Test extends MockChrisConnector with MockGetMovementRepository with MockSoapUtils {
    lazy val getMovementRequest: GetMovementRequest = GetMovementRequest(exciseRegistrationNumber = testErn, arc = testArc)
    lazy val getMovementIfChangedRequest: GetMovementIfChangedRequest = GetMovementIfChangedRequest(exciseRegistrationNumber = testErn, arc = testArc)

    lazy val service: GetMovementService = new GetMovementService(
      mockConnector,
      mockRepo,
      mockSoapUtils
    )

    lazy implicit val userRequest: UserRequest[_] = UserRequest(FakeRequest(), testErn, testInternalId, testCredId)
  }

  "getMovement" should {
    "return a Right" when {
      "retrieving from mongo returns nothing so a fresh call to GetMovement is made" in new Test {
        MockGetMovementRepository.get(testErn, testArc).thenReturn(Future.successful(None))

        MockConnector
          .postChrisSOAPRequest(getMovementRequest)
          .returns(Future.successful(Right(getMovementResponse)))

        MockGetMovementRepository.set().thenReturn(Future.successful(Right(true)))

        await(service.getMovement(getMovementRequest)) shouldBe Right(getMovementResponse)
      }
      "retrieving from mongo returns a match so a fresh call to GetMovementIfChanged is made but there is no change" in new Test {
        MockGetMovementRepository.get(testErn, testArc).thenReturn(Future.successful(Some(GetMovementMongoResponse(testInternalId, testErn, testArc, getMovementResponse))))

        MockConnector
          .postChrisSOAPRequest(getMovementIfChangedRequest)
          .returns(Future.successful(Right(
            s"""<tns:Envelope
               |	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
               |	xmlns:tns="http://www.w3.org/2003/05/soap-envelope">
               |	<tns:Body>
               |		<con:Control
               |			xmlns:con="http://www.govtalk.gov.uk/taxation/InternationalTrade/Common/ControlDocument">
               |			<con:MetaData>
               |				<con:MessageId>String</con:MessageId>
               |				<con:Source>String</con:Source>
               |				<con:Identity>String</con:Identity>
               |				<con:Partner>String</con:Partner>
               |				<con:CorrelationId>String</con:CorrelationId>
               |				<con:BusinessKey>String</con:BusinessKey>
               |				<con:MessageDescriptor>String</con:MessageDescriptor>
               |				<con:QualityOfService>String</con:QualityOfService>
               |				<con:Destination>String</con:Destination>
               |				<con:Priority>0</con:Priority>
               |			</con:MetaData>
               |			<con:OperationResponse>
               |				<con:Results/>
               |			</con:OperationResponse>
               |		</con:Control>
               |	</tns:Body>
               |</tns:Envelope>""".stripMargin)))

        await(service.getMovement(getMovementRequest)) shouldBe Right(getMovementResponse)
      }
      "retrieving from mongo returns a match so a fresh call to GetMovementIfChanged is made and there is a change" in new Test {

        MockGetMovementRepository.get(testErn, testArc).thenReturn(Future.successful(Some(GetMovementMongoResponse(testInternalId, testErn, testArc, getMovementResponse))))

        MockConnector
          .postChrisSOAPRequest(getMovementIfChangedRequest)
          .returns(Future.successful(Right(getMovementSoapWrapper)))

        MockGetMovementRepository.set().thenReturn(Future.successful(Right(true)))

        await(service.getMovement(getMovementRequest)) shouldBe Right(getMovementResponse)
      }
      "connector call is successful and XML is the correct format" in new Test {
        MockConnector
          .postChrisSOAPRequest(getMovementRequest)
          .returns(Future.successful(Right(getMovementResponse)))

        MockGetMovementRepository.set().thenReturn(Future.successful(Right(true)))

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
      "repository returns a Left" in new Test {
        MockConnector
          .postChrisSOAPRequest(getMovementRequest)
          .returns(Future.successful(Right(getMovementResponse)))

        MockGetMovementRepository.set().thenReturn(Future.successful(Left(MongoError("Some error"))))

        await(service.getMovement(getMovementRequest)) shouldBe Left(MongoError("Some error"))
      }
      "repository returns a failed future" in new Test {
        MockConnector
          .postChrisSOAPRequest(getMovementRequest)
          .returns(Future.successful(Right(getMovementResponse)))

        MockGetMovementRepository.set().thenReturn(Future.failed(new Exception("Some error")))

        await(service.getMovement(getMovementRequest)) shouldBe Left(MongoError("Some error"))
      }
    }
  }
}
