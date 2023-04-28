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

import play.api.libs.json.{JsNull, JsString}
import play.api.test.FakeRequest
import uk.gov.hmrc.emcstfe.fixtures.{GetMovementFixture, GetMovementIfChangedFixture}
import uk.gov.hmrc.emcstfe.mocks.connectors.MockChrisConnector
import uk.gov.hmrc.emcstfe.mocks.repository.MockGetMovementRepository
import uk.gov.hmrc.emcstfe.mocks.utils.MockXmlUtils
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.mongo.GetMovementMongoResponse
import uk.gov.hmrc.emcstfe.models.request.{GetMovementIfChangedRequest, GetMovementRequest}
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{GenericParseError, MongoError, SoapExtractionError, UnexpectedDownstreamResponseError, XmlParseError, XmlValidationError}
import uk.gov.hmrc.emcstfe.support.UnitSpec

import scala.concurrent.Future
import scala.xml.XML

class GetMovementServiceSpec extends UnitSpec with GetMovementFixture with GetMovementIfChangedFixture {
  trait Test extends MockChrisConnector with MockGetMovementRepository with MockXmlUtils {
    lazy val getMovementRequest: GetMovementRequest = GetMovementRequest(exciseRegistrationNumber = testErn, arc = testArc)
    lazy val getMovementIfChangedRequest: GetMovementIfChangedRequest = GetMovementIfChangedRequest(exciseRegistrationNumber = testErn, arc = testArc, sequenceNumber = "1", versionTransactionReference = "008")

    lazy val service: GetMovementService = new GetMovementService(
      mockConnector,
      mockRepo,
      mockXmlUtils
    )

    lazy implicit val userRequest: UserRequest[_] = UserRequest(FakeRequest(), testErn, testInternalId, testCredId)
  }

  "getMovement" when {
    "forceFetchNew = true" should {
      "return a Right" when {
        "retrieving from mongo returns nothing so a fresh call to GetMovement is made" in new Test {
          MockGetMovementRepository.get(testErn, testArc).thenReturn(Future.successful(None))

          MockConnector
            .postChrisSOAPRequest(getMovementRequest)
            .returns(Future.successful(Right(XML.loadString(getMovementResponseBody))))

          MockXmlUtils.trimWhitespaceFromXml().returns(Right(scala.xml.Utility.trim(XML.loadString(getMovementResponseBody))))

          MockGetMovementRepository.set().thenReturn(Future.successful(Right(true)))

          await(service.getMovement(getMovementRequest, forceFetchNew = true)) shouldBe Right(getMovementResponse)
        }
        "retrieving from mongo returns a match so a fresh call to GetMovementIfChanged is made but there is no change" in new Test {
          MockGetMovementRepository
            .get(testErn, testArc)
            .thenReturn(Future.successful(Some(GetMovementMongoResponse(testInternalId, testErn, testArc, JsString(getMovementResponseBody)))))

          MockConnector
            .postChrisSOAPRequest(getMovementIfChangedRequest)
            .returns(Future.successful(Right(XML.loadString(getMovementIfChangedNoChangeSoapWrapper))))

          await(service.getMovement(getMovementRequest, forceFetchNew = true)) shouldBe Right(getMovementResponse)
        }
        "retrieving from mongo returns a match so a fresh call to GetMovementIfChanged is made and there is a change" in new Test {
          MockGetMovementRepository.get(testErn, testArc).thenReturn(Future.successful(Some(GetMovementMongoResponse(testInternalId, testErn, testArc, JsString(getMovementResponseBody)))))

          MockConnector
            .postChrisSOAPRequest(getMovementIfChangedRequest)
            .returns(Future.successful(Right(XML.loadString(getMovementIfChangedWithChangeSoapWrapper))))

          MockXmlUtils.readXml().returns(Right(XML.loadString(getMovementIfChangedResponseBody)))

          MockXmlUtils.trimWhitespaceFromXml().returns(Right(scala.xml.Utility.trim(XML.loadString(getMovementIfChangedResponseBody))))

          MockGetMovementRepository.set().thenReturn(Future.successful(Right(true)))

          await(service.getMovement(getMovementRequest, forceFetchNew = true)) shouldBe Right(getMovementIfChangedResponse)
        }
      }
      "return a Left" when {
        "GetMovement call is unsuccessful" in new Test {
          MockGetMovementRepository.get(testErn, testArc).thenReturn(Future.successful(None))
          MockConnector
            .postChrisSOAPRequest(getMovementRequest)
            .returns(Future.successful(Left(XmlValidationError)))

          await(service.getMovement(getMovementRequest, forceFetchNew = true)) shouldBe Left(XmlValidationError)
        }
        "GetMovement call response cannot be extracted" in new Test {
          MockGetMovementRepository.get(testErn, testArc).thenReturn(Future.successful(None))

          MockConnector
            .postChrisSOAPRequest(getMovementRequest)
            .returns(Future.successful(Left(SoapExtractionError)))

          await(service.getMovement(getMovementRequest, forceFetchNew = true)) shouldBe Left(SoapExtractionError)
        }
        "repository.set returns a Left" in new Test {
          MockGetMovementRepository.get(testErn, testArc).thenReturn(Future.successful(None))

          MockConnector
            .postChrisSOAPRequest(getMovementRequest)
            .returns(Future.successful(Right(XML.loadString(getMovementResponseBody))))

          MockXmlUtils.trimWhitespaceFromXml().returns(Right(scala.xml.Utility.trim(XML.loadString(getMovementResponseBody))))

          MockGetMovementRepository.set().thenReturn(Future.successful(Left(MongoError("Some error"))))

          await(service.getMovement(getMovementRequest, forceFetchNew = true)) shouldBe Left(MongoError("Some error"))
        }
        "repository.set returns a failed future" in new Test {
          MockGetMovementRepository.get(testErn, testArc).thenReturn(Future.successful(None))

          MockConnector
            .postChrisSOAPRequest(getMovementRequest)
            .returns(Future.successful(Right(XML.loadString(getMovementResponseBody))))

          MockXmlUtils.trimWhitespaceFromXml().returns(Right(scala.xml.Utility.trim(XML.loadString(getMovementResponseBody))))

          MockGetMovementRepository.set().thenReturn(Future.failed(new Exception("Some error")))

          await(service.getMovement(getMovementRequest, forceFetchNew = true)) shouldBe Left(MongoError("Some error"))
        }
      }
    }

    "forceFetchNew = false" should {
      "fetch from downstream if Mongo returns no data" in new Test {
        MockGetMovementRepository.get(testErn, testArc).thenReturn(Future.successful(None))

        MockConnector
          .postChrisSOAPRequest(getMovementRequest)
          .returns(Future.successful(Right(XML.loadString(getMovementResponseBody))))

        MockXmlUtils.trimWhitespaceFromXml().returns(Right(scala.xml.Utility.trim(XML.loadString(getMovementResponseBody))))

        MockGetMovementRepository.set().thenReturn(Future.successful(Right(true)))

        await(service.getMovement(getMovementRequest, forceFetchNew = false)) shouldBe Right(getMovementResponse)
      }

      "return the Mongo document if Mongo returns data" in new Test {
        MockGetMovementRepository
          .get(testErn, testArc)
          .thenReturn(Future.successful(Some(GetMovementMongoResponse(testInternalId, testErn, testArc, JsString(getMovementResponseBody)))))

        await(service.getMovement(getMovementRequest, forceFetchNew = false)) shouldBe Right(getMovementResponse)
      }
    }
  }

  "generateGetMovementResponse" should {
    "return a Right" when {
      "XML is valid" in new Test {
        service.generateGetMovementResponse(JsString(getMovementResponseBody)) shouldBe Right(getMovementResponse)
      }
    }
    "return a Left" when {
      "XML is invalid" in new Test {
        service.generateGetMovementResponse(JsNull) shouldBe Left(XmlParseError(Seq(GenericParseError("JsResultException(errors:List((,List(JsonValidationError(List(error.expected.jsstring),ArraySeq())))))"))))
      }
    }
  }

  "storeAndReturn" should {
    "return a Right" when {
      "repository returns a success" in new Test {
        MockXmlUtils.trimWhitespaceFromXml().returns(Right(scala.xml.Utility.trim(XML.loadString(getMovementResponseBody))))

        MockGetMovementRepository.set().thenReturn(Future.successful(Right(true)))

        await(service.storeAndReturn(Right(XML.loadString(getMovementResponseBody)))(getMovementRequest)) shouldBe Right(getMovementResponse)
      }
    }
    "return a Left" when {
      "trimWhitespaceFromXml returns a Left" in new Test {
        MockXmlUtils.trimWhitespaceFromXml().returns(Left(MongoError("Some error")))

        await(service.storeAndReturn(Right(XML.loadString(getMovementResponseBody)))(getMovementRequest)) shouldBe Left(MongoError("Some error"))
      }
      "repository returns a Left" in new Test {
        MockXmlUtils.trimWhitespaceFromXml().returns(Right(scala.xml.Utility.trim(XML.loadString(getMovementResponseBody))))

        MockGetMovementRepository.set().thenReturn(Future.successful(Left(MongoError("Some error"))))

        await(service.storeAndReturn(Right(XML.loadString(getMovementResponseBody)))(getMovementRequest)) shouldBe Left(MongoError("Some error"))
      }
      "repository returns a failed future" in new Test {
        MockXmlUtils.trimWhitespaceFromXml().returns(Right(scala.xml.Utility.trim(XML.loadString(getMovementResponseBody))))

        MockGetMovementRepository.set().thenReturn(Future.failed(new Exception("Some error")))

        await(service.storeAndReturn(Right(XML.loadString(getMovementResponseBody)))(getMovementRequest)) shouldBe Left(MongoError("Some error"))
      }
      "chrisResponse is a Left" in new Test {
        await(service.storeAndReturn(Left(UnexpectedDownstreamResponseError))(getMovementRequest)) shouldBe Left(UnexpectedDownstreamResponseError)
      }
    }
  }

  "getNewMovement" should {
    "return a Right" when {
      "connector call is successful and repository call is successful" in new Test {
        MockConnector
          .postChrisSOAPRequest(getMovementRequest)
          .returns(Future.successful(Right(XML.loadString(getMovementResponseBody))))

        MockXmlUtils.trimWhitespaceFromXml().returns(Right(scala.xml.Utility.trim(XML.loadString(getMovementResponseBody))))

        MockGetMovementRepository.set().thenReturn(Future.successful(Right(true)))

        await(service.getNewMovement(getMovementRequest)) shouldBe Right(getMovementResponse)
      }
    }
  }

  "getMovementIfChanged" should {
    "return a Right" when {
      "downstream call is successful but response model is empty" in new Test {
        MockConnector
          .postChrisSOAPRequest(getMovementIfChangedRequest)
          .returns(Future.successful(Right(XML.loadString(getMovementIfChangedNoChangeSoapWrapper))))

        await(service.getMovementIfChanged(getMovementRequest, GetMovementMongoResponse(testInternalId, testErn, testArc, JsString(getMovementResponseBody)))) shouldBe Right(getMovementResponse)
      }
      "downstream call is successful and response model is not empty" in new Test {

        MockConnector
          .postChrisSOAPRequest(getMovementIfChangedRequest)
          .returns(Future.successful(Right(XML.loadString(getMovementIfChangedWithChangeSoapWrapper))))

        MockXmlUtils.readXml().returns(Right(XML.loadString(getMovementIfChangedResponseBody)))

        MockXmlUtils.trimWhitespaceFromXml().returns(Right(scala.xml.Utility.trim(XML.loadString(getMovementResponseBody))))

        MockGetMovementRepository.set().thenReturn(Future.successful(Right(true)))

        await(service.getMovementIfChanged(getMovementRequest, GetMovementMongoResponse(testInternalId, testErn, testArc, JsString(getMovementIfChangedResponseBody)))) shouldBe Right(getMovementIfChangedResponse)
      }
    }
  }

  "extractVersionTransactionReferenceFromXml" should {
    "extract the correct value" in new Test {
      service.extractVersionTransactionReferenceFromXml(XML.loadString(getMovementResponseBody)) shouldBe getMovementIfChangedRequest.versionTransactionReference
    }
  }

  "extractSequenceNumberFromXml" should {
    "extract the correct value" in new Test {
      service.extractSequenceNumberFromXml(XML.loadString(getMovementResponseBody)) shouldBe getMovementIfChangedRequest.sequenceNumber
    }
  }
}
