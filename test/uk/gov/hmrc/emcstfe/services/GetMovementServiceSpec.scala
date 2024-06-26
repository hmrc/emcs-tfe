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

import com.mongodb.MongoException
import play.api.libs.json.{JsNull, JsString}
import uk.gov.hmrc.emcstfe.featureswitch.core.config.SendToEIS
import uk.gov.hmrc.emcstfe.fixtures.{GetMovementFixture, GetMovementIfChangedFixture}
import uk.gov.hmrc.emcstfe.mocks.config.MockAppConfig
import uk.gov.hmrc.emcstfe.mocks.connectors.{MockChrisConnector, MockEisConnector}
import uk.gov.hmrc.emcstfe.mocks.repository.MockGetMovementRepository
import uk.gov.hmrc.emcstfe.mocks.utils.MockXmlUtils
import uk.gov.hmrc.emcstfe.models.mongo.GetMovementMongoResponse
import uk.gov.hmrc.emcstfe.models.request.{GetMovementIfChangedRequest, GetMovementRequest}
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{GenericParseError, SoapExtractionError, UnexpectedDownstreamResponseError, XmlParseError, XmlValidationError}
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.concurrent.Future
import scala.xml.XML

class GetMovementServiceSpec extends TestBaseSpec with GetMovementFixture with GetMovementIfChangedFixture {
  trait Test extends MockChrisConnector with MockEisConnector with MockGetMovementRepository with MockXmlUtils with MockAppConfig {

    lazy val sequenceNumber: Option[Int] = None

    lazy val getMovementRequest: GetMovementRequest = GetMovementRequest(exciseRegistrationNumber = testErn, arc = testArc, sequenceNumber)
    lazy val getMovementIfChangedRequest: GetMovementIfChangedRequest = GetMovementIfChangedRequest(exciseRegistrationNumber = testErn, arc = testArc, sequenceNumber.fold("1")(_.toString), versionTransactionReference = "008")

    lazy val service: GetMovementService = new GetMovementService(
      mockChrisConnector,
      mockEisConnector,
      mockRepo,
      mockXmlUtils,
      mockAppConfig
    )
  }

  "getMovement" when {
    "when a sequenceNumber is NOT supplied" when {
      "forceFetchNew = true" should {
        "return a Right" when {
          "retrieving from mongo returns nothing so a fresh call to GetMovement is made" in new Test {

            MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(false)

            MockGetMovementRepository.get(testArc).returns(Future.successful(None))

            MockChrisConnector
              .postChrisSOAPRequest(getMovementRequest)
              .returns(Future.successful(Right(XML.loadString(getMovementResponseBody()))))

            MockXmlUtils.trimWhitespaceFromXml().returns(scala.xml.Utility.trim(XML.loadString(getMovementResponseBody())))

            MockGetMovementRepository.set().returns(Future.successful(getMovementMongoResponse()))

            await(service.getMovement(getMovementRequest, forceFetchNew = true)) shouldBe Right(getMovementResponse())
          }
          "retrieving from mongo returns a match so a fresh call to GetMovementIfChanged is made but there is no change (ChRIS)" in new Test {

            MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(false)

            MockGetMovementRepository
              .get(testArc)
              .returns(Future.successful(Some(getMovementMongoResponse())))

            MockChrisConnector
              .postChrisSOAPRequest(getMovementIfChangedRequest)
              .returns(Future.successful(Right(XML.loadString(getMovementIfChangedNoChangeSoapWrapper))))

            await(service.getMovement(getMovementRequest, forceFetchNew = true)) shouldBe Right(getMovementResponse())
          }
          "retrieving from mongo returns a match so a fresh call to GetMovementIfChanged is made and there is a change (ChRIS)" in new Test {

            MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(false)

            MockGetMovementRepository.get(testArc).returns(Future.successful(Some(getMovementMongoResponse())))

            MockChrisConnector
              .postChrisSOAPRequest(getMovementIfChangedRequest)
              .returns(Future.successful(Right(XML.loadString(getMovementIfChangedWithChangeSoapWrapper()))))

            MockXmlUtils.readXml().returns(Right(XML.loadString(getMovementIfChangedResponseBody())))

            MockXmlUtils.trimWhitespaceFromXml().returns(scala.xml.Utility.trim(XML.loadString(getMovementIfChangedResponseBody())))

            MockGetMovementRepository.set().returns(Future.successful(getMovementMongoResponse()))

            await(service.getMovement(getMovementRequest, forceFetchNew = true)) shouldBe Right(getMovementIfChangedResponse())
          }
          "retrieving from mongo returns a match so a fresh call is made to EIS (calling EIS enabled)" in new Test {

            MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(true).twice()

            MockGetMovementRepository.get(testArc).returns(Future.successful(Some(getMovementMongoResponse())))

            MockEisConnector
              .getRawMovement(getMovementRequest)
              .returns(Future.successful(Right(getRawMovementIfChangedMongoResponse())))

            MockXmlUtils.trimWhitespaceFromXml().returns(scala.xml.Utility.trim(XML.loadString(getMovementIfChangedResponseBody())))

            MockGetMovementRepository.set().returns(Future.successful(getMovementMongoResponse()))

            await(service.getMovement(getMovementRequest, forceFetchNew = true)) shouldBe Right(getMovementIfChangedResponse())
          }
        }
        "return a Left" when {
          "GetMovement call is unsuccessful" in new Test {

            MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(false)

            MockGetMovementRepository.get(testArc).returns(Future.successful(None))

            MockChrisConnector
              .postChrisSOAPRequest(getMovementRequest)
              .returns(Future.successful(Left(XmlValidationError)))

            await(service.getMovement(getMovementRequest, forceFetchNew = true)) shouldBe Left(XmlValidationError)
          }
          "GetMovement call response cannot be extracted" in new Test {

            MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(false)

            MockGetMovementRepository.get(testArc).returns(Future.successful(None))

            MockChrisConnector
              .postChrisSOAPRequest(getMovementRequest)
              .returns(Future.successful(Left(SoapExtractionError)))

            await(service.getMovement(getMovementRequest, forceFetchNew = true)) shouldBe Left(SoapExtractionError)
          }
          "repository.set fails with MongoException, still return the movement as doesn't matter if cache doesn't store" in new Test {

            MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(false)

            MockGetMovementRepository.get(testArc).returns(Future.successful(None))

            MockChrisConnector
              .postChrisSOAPRequest(getMovementRequest)
              .returns(Future.successful(Right(XML.loadString(getMovementResponseBody()))))

            MockXmlUtils.trimWhitespaceFromXml().returns(scala.xml.Utility.trim(XML.loadString(getMovementResponseBody())))

            MockGetMovementRepository.set().returns(Future.failed(new MongoException("Some error")))

            await(service.getMovement(getMovementRequest, forceFetchNew = true)) shouldBe Right(getMovementResponse())
          }
          "repository.set returns some other failed future, still return the movement as doesn't matter if cache doesn't store" in new Test {

            MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(false)

            MockGetMovementRepository.get(testArc).returns(Future.successful(None))

            MockChrisConnector
              .postChrisSOAPRequest(getMovementRequest)
              .returns(Future.successful(Right(XML.loadString(getMovementResponseBody()))))

            MockXmlUtils.trimWhitespaceFromXml().returns(scala.xml.Utility.trim(XML.loadString(getMovementResponseBody())))

            MockGetMovementRepository.set().returns(Future.failed(new Exception("Some error")))

            await(service.getMovement(getMovementRequest, forceFetchNew = true)) shouldBe Right(getMovementResponse())
          }
        }
      }
      "forceFetchNew = false" should {
        "fetch from downstream if Mongo returns no data" in new Test {

          MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(false)

          MockGetMovementRepository.get(testArc).returns(Future.successful(None))

          MockChrisConnector
            .postChrisSOAPRequest(getMovementRequest)
            .returns(Future.successful(Right(XML.loadString(getMovementResponseBody()))))

          MockXmlUtils.trimWhitespaceFromXml().returns(scala.xml.Utility.trim(XML.loadString(getMovementResponseBody())))

          MockGetMovementRepository.set().returns(Future.successful(getMovementMongoResponse()))

          await(service.getMovement(getMovementRequest, forceFetchNew = false)) shouldBe Right(getMovementResponse())
        }
        "fetch from downstream if Mongo returns no data (calling EIS)" in new Test {

          MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(true)

          MockGetMovementRepository.get(testArc).returns(Future.successful(None))

          MockEisConnector
            .getRawMovement(getMovementRequest)
            .returns(Future.successful(Right(getRawMovementResponse())))

          MockXmlUtils.trimWhitespaceFromXml().returns(scala.xml.Utility.trim(XML.loadString(getMovementResponseBody())))

          MockGetMovementRepository.set().returns(Future.successful(getMovementMongoResponse()))

          await(service.getMovement(getMovementRequest, forceFetchNew = false)) shouldBe Right(getMovementResponse())
        }
        "return the Mongo document if Mongo returns data" in new Test {
          MockGetMovementRepository
            .get(testArc)
            .returns(Future.successful(Some(getMovementMongoResponse())))

          await(service.getMovement(getMovementRequest, forceFetchNew = false)) shouldBe Right(getMovementResponse())
        }
      }
    }
    "when a sequenceNumber is supplied" when {
      Seq(false, true).foreach { forceFetchNew =>
        s"forceFetchNew = $forceFetchNew" should {
          "return a Right" when {
            "retrieving from mongo returns nothing so a fresh call to GetMovement is made" in new Test {

              override lazy val sequenceNumber: Option[Int] = Some(1)

              MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(false)

              MockGetMovementRepository.get(testArc).returns(Future.successful(None))

              MockChrisConnector
                .postChrisSOAPRequest(getMovementRequest)
                .returns(Future.successful(Right(XML.loadString(getMovementResponseBody()))))

              MockXmlUtils.trimWhitespaceFromXml().returns(scala.xml.Utility.trim(XML.loadString(getMovementResponseBody())))

              MockGetMovementRepository.set().returns(Future.successful(getMovementMongoResponse()))

              await(service.getMovement(getMovementRequest, forceFetchNew = forceFetchNew)) shouldBe Right(getMovementResponse())
            }
            if(forceFetchNew) {
              "retrieving from mongo returns a match, sequenceNumber is the same but forceFetch is true so always get latest" in new Test {

                override lazy val sequenceNumber = Some(1)

                MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(false).twice()

                MockGetMovementRepository
                  .get(testArc)
                  .returns(Future.successful(Some(getMovementMongoResponse())))

                MockChrisConnector
                  .postChrisSOAPRequest(getMovementRequest)
                  .returns(Future.successful(Right(XML.loadString(getMovementResponseBody()))))

                MockXmlUtils.trimWhitespaceFromXml().returns(scala.xml.Utility.trim(XML.loadString(getMovementResponseBody())))

                MockGetMovementRepository.set().returns(Future.successful(getMovementMongoResponse()))

                await(service.getMovement(getMovementRequest, forceFetchNew = forceFetchNew)) shouldBe Right(getMovementResponse())
              }
            } else {
              "retrieving from mongo returns a match, sequenceNumber is the same so data returned from Cache" in new Test {

                override lazy val sequenceNumber = Some(1)

                MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(false)

                MockGetMovementRepository
                  .get(testArc)
                  .returns(Future.successful(Some(getMovementMongoResponse())))

                await(service.getMovement(getMovementRequest, forceFetchNew = forceFetchNew)) shouldBe Right(getMovementResponse())
              }
            }

            "retrieving from mongo returns a match, sequenceNumber is different so a fresh call to GetMovement is made (ChRIS) without saving to Mongo" in new Test {

              override lazy val sequenceNumber = Some(1)

              MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(false).twice()

              MockGetMovementRepository.get(testArc).returns(Future.successful(Some(getMovementMongoResponse(2))))

              MockChrisConnector
                .postChrisSOAPRequest(getMovementRequest)
                .returns(Future.successful(Right(XML.loadString(getMovementResponseBody()))))

              MockXmlUtils.readXml().returns(Right(XML.loadString(getMovementResponseBody())))

              MockXmlUtils.trimWhitespaceFromXml().returns(scala.xml.Utility.trim(XML.loadString(getMovementResponseBody())))

              await(service.getMovement(getMovementRequest, forceFetchNew = forceFetchNew)) shouldBe Right(getMovementResponse())
            }
            "retrieving from mongo returns a match, sequenceNumber is different so a fresh call to GetMovement is made (EIS) without saving to Mongo" in new Test {

              override lazy val sequenceNumber = Some(1)

              MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(true).twice()

              MockGetMovementRepository.get(testArc).returns(Future.successful(Some(getMovementMongoResponse(2))))

              MockEisConnector
                .getRawMovement(getMovementRequest)
                .returns(Future.successful(Right(getRawMovementResponse())))

              MockXmlUtils.trimWhitespaceFromXml().returns(scala.xml.Utility.trim(XML.loadString(getMovementResponseBody())))

              await(service.getMovement(getMovementRequest, forceFetchNew = forceFetchNew)) shouldBe Right(getMovementResponse())
            }
          }
          "return a Left" when {
            "GetMovement call is unsuccessful" in new Test {

              override lazy val sequenceNumber = Some(1)

              MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(false)

              MockGetMovementRepository.get(testArc).returns(Future.successful(None))

              MockChrisConnector
                .postChrisSOAPRequest(getMovementRequest)
                .returns(Future.successful(Left(XmlValidationError)))

              await(service.getMovement(getMovementRequest, forceFetchNew = forceFetchNew)) shouldBe Left(XmlValidationError)
            }
            "GetMovement call response cannot be extracted" in new Test {

              override lazy val sequenceNumber = Some(1)

              MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(false)

              MockGetMovementRepository.get(testArc).returns(Future.successful(None))

              MockChrisConnector
                .postChrisSOAPRequest(getMovementRequest)
                .returns(Future.successful(Left(SoapExtractionError)))

              await(service.getMovement(getMovementRequest, forceFetchNew = forceFetchNew)) shouldBe Left(SoapExtractionError)
            }
          }
        }
      }
    }
  }

  "generateGetMovementResponse" should {
    "return a Right" when {
      "XML is valid" in new Test {
        service.generateGetMovementResponse(JsString(getMovementResponseBody())) shouldBe Right(getMovementResponse())
      }
    }
    "return a Left" when {
      "XML is invalid" in new Test {
        service.generateGetMovementResponse(JsNull) shouldBe Left(XmlParseError(Seq(GenericParseError("JsResultException(errors:List((,List(JsonValidationError(List(error.expected.jsstring),List())))))"))))
      }
    }
  }

  "storeAndReturn" should {
    "return a Right" when {
      "and update the cache" when {

        "repository returns a success and no cache value exists" in new Test {
          MockXmlUtils.trimWhitespaceFromXml().returns(scala.xml.Utility.trim(XML.loadString(getMovementResponseBody())))

          MockGetMovementRepository.set().returns(Future.successful(getMovementMongoResponse())).once()

          await(service.storeAndReturn(Right(XML.loadString(getMovementResponseBody())), None)(getMovementRequest)) shouldBe Right(getMovementResponse())
        }

        "repository returns a success and response from core is different to the cache value" in new Test {
          MockXmlUtils.trimWhitespaceFromXml().returns(scala.xml.Utility.trim(XML.loadString(getMovementResponseBody())))

          MockGetMovementRepository.set().returns(Future.successful(getMovementMongoResponse())).once()

          await(service.storeAndReturn(
            response = Right(XML.loadString(getMovementResponseBody())),
            cachedMovement = Some(Right(getMovementResponse("1day"))))(getMovementRequest)
          ) shouldBe Right(getMovementResponse())
        }
      }

      "and NOT update the cache" when {
        "repository returns a success and response from core is the same as the existing cache value" in new Test {
          MockXmlUtils.trimWhitespaceFromXml().returns(scala.xml.Utility.trim(XML.loadString(getMovementResponseBody())))

          MockGetMovementRepository.set().never()

          await(service.storeAndReturn(
            response = Right(XML.loadString(getMovementResponseBody())),
            cachedMovement = Some(Right(getMovementResponse())))(getMovementRequest)
          ) shouldBe Right(getMovementResponse())
        }
      }
    }
    "return a Left" when {
      "repository returns Mongo Exception, still return Right as doesn't matter if storage fails" in new Test {
        MockXmlUtils.trimWhitespaceFromXml().returns(scala.xml.Utility.trim(XML.loadString(getMovementResponseBody())))

        MockGetMovementRepository.set().returns(Future.failed(new MongoException("Some error")))

        await(service.storeAndReturn(Right(XML.loadString(getMovementResponseBody())), None)(getMovementRequest)) shouldBe Right(getMovementResponse())
      }
      "repository returns some other failed future, still return Right as doesn't matter if storage fails" in new Test {
        MockXmlUtils.trimWhitespaceFromXml().returns(scala.xml.Utility.trim(XML.loadString(getMovementResponseBody())))

        MockGetMovementRepository.set().returns(Future.failed(new Exception("Some error")))

        await(service.storeAndReturn(Right(XML.loadString(getMovementResponseBody())), None)(getMovementRequest)) shouldBe Right(getMovementResponse())
      }
      "chrisResponse is a Left" in new Test {
        await(service.storeAndReturn(Left(UnexpectedDownstreamResponseError), None)(getMovementRequest)) shouldBe Left(UnexpectedDownstreamResponseError)
      }
    }
  }

  "getNewMovement" should {
    "return a Right" when {

      "when calling EIS" must {

        "connector call is successful and repository call is successful" in new Test {

          MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(true)

          MockEisConnector
            .getRawMovement(getMovementRequest)
            .returns(Future.successful(Right(getRawMovementResponse())))

          MockXmlUtils.trimWhitespaceFromXml().returns(scala.xml.Utility.trim(XML.loadString(getMovementResponseBody())))

          MockGetMovementRepository.set().returns(Future.successful(getMovementMongoResponse()))

          await(service.getNewMovement(getMovementRequest, None)) shouldBe Right(getMovementResponse())
        }
      }

      "when calling ChRIS" must {

        "connector call is successful and repository call is successful" in new Test {

          MockedAppConfig.getFeatureSwitchValue(SendToEIS).returns(false)


          MockChrisConnector
            .postChrisSOAPRequest(getMovementRequest)
            .returns(Future.successful(Right(XML.loadString(getMovementResponseBody()))))

          MockXmlUtils.trimWhitespaceFromXml().returns(scala.xml.Utility.trim(XML.loadString(getMovementResponseBody())))

          MockGetMovementRepository.set().returns(Future.successful(getMovementMongoResponse()))

          await(service.getNewMovement(getMovementRequest, None)) shouldBe Right(getMovementResponse())
        }
      }
    }
  }

  "getMovementIfChanged" should {
    "return a Right" when {
      "downstream call is successful but response model is empty" in new Test {

        MockChrisConnector
          .postChrisSOAPRequest(getMovementIfChangedRequest)
          .returns(Future.successful(Right(XML.loadString(getMovementIfChangedNoChangeSoapWrapper))))

        await(service.getMovementIfChanged(getMovementRequest, getMovementMongoResponse())) shouldBe Right(getMovementResponse())
      }
      "downstream call is successful and response model is not empty" in new Test {


        MockChrisConnector
          .postChrisSOAPRequest(getMovementIfChangedRequest)
          .returns(Future.successful(Right(XML.loadString(getMovementIfChangedWithChangeSoapWrapper()))))

        MockXmlUtils.readXml().returns(Right(XML.loadString(getMovementIfChangedResponseBody())))

        MockXmlUtils.trimWhitespaceFromXml().returns(scala.xml.Utility.trim(XML.loadString(getMovementResponseBody())))

        MockGetMovementRepository.set().returns(Future.successful(getMovementMongoResponse()))

        await(service.getMovementIfChanged(getMovementRequest, getMovementIfChangedMongoResponse())) shouldBe Right(getMovementIfChangedResponse())
      }
    }

    "return a Left" when {
      "data stored in Mongo can't be converted into a String so no call to ChRIS is made and the call fails early" in new Test {
        await(service.getMovementIfChanged(getMovementRequest, GetMovementMongoResponse(testArc, sequenceNumber = 1, data = JsNull))) shouldBe Left(XmlParseError(Seq(GenericParseError("JsResultException(errors:List((,List(JsonValidationError(List(error.expected.jsstring),List())))))"))))
      }
    }
  }

  "extractVersionTransactionReferenceFromXml" should {
    "extract the correct value" in new Test {
      service.extractVersionTransactionReferenceFromXml(XML.loadString(getMovementResponseBody())) shouldBe getMovementIfChangedRequest.versionTransactionReference
    }
  }

  "extractSequenceNumberFromXml" should {
    "extract the correct value" in new Test {
      service.extractSequenceNumberFromXml(XML.loadString(getMovementResponseBody())) shouldBe getMovementIfChangedRequest.sequenceNumber
    }
  }
}
