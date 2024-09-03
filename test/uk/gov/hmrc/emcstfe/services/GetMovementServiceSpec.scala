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
import uk.gov.hmrc.emcstfe.fixtures.GetMovementFixture
import uk.gov.hmrc.emcstfe.mocks.connectors.MockEisConnector
import uk.gov.hmrc.emcstfe.mocks.repository.MockGetMovementRepository
import uk.gov.hmrc.emcstfe.models.request.GetMovementRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{EISUnknownError, GenericParseError, UnexpectedDownstreamResponseError, XmlParseError}
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.concurrent.Future
import scala.xml.XML

class GetMovementServiceSpec extends TestBaseSpec with GetMovementFixture {
  trait Test extends MockEisConnector with MockGetMovementRepository {

    lazy val sequenceNumber: Option[Int] = None

    lazy val getMovementRequest: GetMovementRequest = GetMovementRequest(exciseRegistrationNumber = testErn, arc = testArc, sequenceNumber)

    lazy val service: GetMovementService = new GetMovementService(
      mockEisConnector,
      mockRepo
    )
  }

  "getMovement" when {
    "when a sequenceNumber is NOT supplied" when {
      "forceFetchNew = true" should {
        "return a Right" when {
          "retrieving from mongo returns a match so a fresh call is made" in new Test {

            MockGetMovementRepository.get(testArc).returns(Future.successful(Some(getMovementMongoResponse())))

            MockEisConnector
              .getRawMovement(getMovementRequest)
              .returns(Future.successful(Right(getRawMovementResponse())))


            MockGetMovementRepository.set().returns(Future.successful(getMovementMongoResponse()))

            await(service.getMovement(getMovementRequest, forceFetchNew = true)) shouldBe Right(getMovementResponse())
          }
          "repository.set fails with MongoException, still return the movement as doesn't matter if cache doesn't store" in new Test {

            MockGetMovementRepository.get(testArc).returns(Future.successful(None))

            MockEisConnector
              .getRawMovement(getMovementRequest)
              .returns(Future.successful(Right(getRawMovementResponse())))


            MockGetMovementRepository.set().returns(Future.failed(new MongoException("Some error")))

            await(service.getMovement(getMovementRequest, forceFetchNew = true)) shouldBe Right(getMovementResponse())
          }
          "repository.set returns some other failed future, still return the movement as doesn't matter if cache doesn't store" in new Test {

            MockGetMovementRepository.get(testArc).returns(Future.successful(None))

            MockEisConnector
              .getRawMovement(getMovementRequest)
              .returns(Future.successful(Right(getRawMovementResponse())))


            MockGetMovementRepository.set().returns(Future.failed(new Exception("Some error")))

            await(service.getMovement(getMovementRequest, forceFetchNew = true)) shouldBe Right(getMovementResponse())
          }
        }
        "return a Left" when {
          "" in new Test {
            MockGetMovementRepository.get(testArc).returns(Future.successful(None))

            MockEisConnector
              .getRawMovement(getMovementRequest)
              .returns(Future.successful(Left(EISUnknownError("foo"))))

            await(service.getMovement(getMovementRequest, forceFetchNew = true)) shouldBe Left(EISUnknownError("foo"))
          }
        }
      }
      "forceFetchNew = false" should {
        "fetch from downstream if Mongo returns no data" in new Test {

          MockGetMovementRepository.get(testArc).returns(Future.successful(None))

          MockEisConnector
            .getRawMovement(getMovementRequest)
            .returns(Future.successful(Right(getRawMovementResponse())))


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

              MockGetMovementRepository.get(testArc).returns(Future.successful(None))

              MockEisConnector
                .getRawMovement(getMovementRequest)
                .returns(Future.successful(Right(getRawMovementResponse())))


              MockGetMovementRepository.set().returns(Future.successful(getMovementMongoResponse()))

              await(service.getMovement(getMovementRequest, forceFetchNew = forceFetchNew)) shouldBe Right(getMovementResponse())
            }
            if(forceFetchNew) {
              "retrieving from mongo returns a match, sequenceNumber is the same but forceFetch is true so always get latest" in new Test {

                override lazy val sequenceNumber = Some(1)

                MockGetMovementRepository
                  .get(testArc)
                  .returns(Future.successful(Some(getMovementMongoResponse())))

                MockEisConnector
                  .getRawMovement(getMovementRequest)
                  .returns(Future.successful(Right(getRawMovementResponse())))


                MockGetMovementRepository.set().returns(Future.successful(getMovementMongoResponse()))

                await(service.getMovement(getMovementRequest, forceFetchNew = forceFetchNew)) shouldBe Right(getMovementResponse())
              }
            } else {
              "retrieving from mongo returns a match, sequenceNumber is the same so data returned from Cache" in new Test {

                override lazy val sequenceNumber = Some(1)

                MockGetMovementRepository
                  .get(testArc)
                  .returns(Future.successful(Some(getMovementMongoResponse())))

                await(service.getMovement(getMovementRequest, forceFetchNew = forceFetchNew)) shouldBe Right(getMovementResponse())
              }
            }

            "retrieving from mongo returns a match, sequenceNumber is different so a fresh call to GetMovement is made without saving to Mongo" in new Test {

              override lazy val sequenceNumber = Some(1)

              MockGetMovementRepository.get(testArc).returns(Future.successful(Some(getMovementMongoResponse(2))))

              MockEisConnector
                .getRawMovement(getMovementRequest)
                .returns(Future.successful(Right(getRawMovementResponse())))


              await(service.getMovement(getMovementRequest, forceFetchNew = forceFetchNew)) shouldBe Right(getMovementResponse())
            }
          }
          "return a Left" when {
            "GetMovement call is unsuccessful" in new Test {

              override lazy val sequenceNumber = Some(1)

              MockGetMovementRepository.get(testArc).returns(Future.successful(None))

              MockEisConnector
                .getRawMovement(getMovementRequest)
                .returns(Future.successful(Left(EISUnknownError("foo"))))

              await(service.getMovement(getMovementRequest, forceFetchNew = forceFetchNew)) shouldBe Left(EISUnknownError("foo"))
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

          MockGetMovementRepository.set().returns(Future.successful(getMovementMongoResponse())).once()

          await(service.storeAndReturn(Right(XML.loadString(getMovementResponseBody())), None)(getMovementRequest)) shouldBe Right(getMovementResponse())
        }

        "repository returns a success and response from core is different to the cache value" in new Test {

          MockGetMovementRepository.set().returns(Future.successful(getMovementMongoResponse())).once()

          await(service.storeAndReturn(
            response = Right(XML.loadString(getMovementResponseBody())),
            cachedMovement = Some(Right(getMovementResponse("1day"))))(getMovementRequest)
          ) shouldBe Right(getMovementResponse())
        }
      }

      "and NOT update the cache" when {
        "repository returns a success and response from core is the same as the existing cache value" in new Test {

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

        MockGetMovementRepository.set().returns(Future.failed(new MongoException("Some error")))

        await(service.storeAndReturn(Right(XML.loadString(getMovementResponseBody())), None)(getMovementRequest)) shouldBe Right(getMovementResponse())
      }
      "repository returns some other failed future, still return Right as doesn't matter if storage fails" in new Test {

        MockGetMovementRepository.set().returns(Future.failed(new Exception("Some error")))

        await(service.storeAndReturn(Right(XML.loadString(getMovementResponseBody())), None)(getMovementRequest)) shouldBe Right(getMovementResponse())
      }
      "submission response is a Left" in new Test {
        await(service.storeAndReturn(Left(UnexpectedDownstreamResponseError), None)(getMovementRequest)) shouldBe Left(UnexpectedDownstreamResponseError)
      }
    }
  }

  "getNewMovement" should {
    "return a Right" when {

      "connector call is successful and repository call is successful" in new Test {

        MockEisConnector
          .getRawMovement(getMovementRequest)
          .returns(Future.successful(Right(getRawMovementResponse())))


        MockGetMovementRepository.set().returns(Future.successful(getMovementMongoResponse()))

        await(service.getNewMovement(getMovementRequest, None)) shouldBe Right(getMovementResponse())
      }
    }
  }
}
