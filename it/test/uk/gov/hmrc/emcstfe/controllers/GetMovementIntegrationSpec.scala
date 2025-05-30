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

package uk.gov.hmrc.emcstfe.controllers

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import org.mongodb.scala.bson.BsonDocument
import play.api.http.Status
import play.api.libs.json.{JsString, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Injecting
import uk.gov.hmrc.emcstfe.stubs.{AuthStub, DownstreamStub}
import uk.gov.hmrc.emcstfe.support.IntegrationBaseSpec
import uk.gov.hmrc.emcstfe.fixtures.GetMovementFixture
import uk.gov.hmrc.emcstfe.models.mongo.GetMovementMongoResponse
import uk.gov.hmrc.emcstfe.repositories.GetMovementRepositoryImpl

import scala.xml.XML

class GetMovementIntegrationSpec extends IntegrationBaseSpec with GetMovementFixture with Injecting {

  val repository: GetMovementRepositoryImpl = inject[GetMovementRepositoryImpl]

  override def beforeEach(): Unit = {
    await(repository.collection.deleteMany(BsonDocument()).toFuture())
    super.beforeEach()
  }

  private trait Test {
    def setupStubs(): StubMapping

    def uri: String = s"/movement/$testErn/$testArc"

    def downstreamEisUri: String = "/emcs/movements/v1/movement"

    def downstreamEisGetMovementQueryParam: Map[String, String] =
      Seq(
        Some("exciseregistrationnumber" -> testErn),
        Some("arc" -> testArc),
        sequenceNumber.map(seq => "sequencenumber" -> seq.toString)
      ).flatten.toMap

    def forceFetchNew: Boolean
    def sequenceNumber: Option[Int] = None

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri).withQueryStringParameters(Seq(
        Some("forceFetchNew" -> forceFetchNew.toString),
        sequenceNumber.map(seq => "sequenceNumber" -> seq.toString)
      ).flatten:_*)
    }
  }

  "Calling the get movement endpoint" when {

    "user is unauthorised" must {
      "return Forbidden" in new Test {
        override def forceFetchNew: Boolean = true

        override def setupStubs(): StubMapping = {
          AuthStub.unauthorised()
          DownstreamStub.onSuccess(DownstreamStub.GET, downstreamEisUri, Status.OK, XML.loadString(getMovementSoapWrapper()))
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.FORBIDDEN
      }
    }

    "user is unauthorised" must {

      "return forbidden" when {
        "the ERN requested does not match the ERN of the credential" in new Test {
          override def forceFetchNew: Boolean = true

          override def setupStubs(): StubMapping = {
            AuthStub.authorised("WrongERN")
          }

          val response: WSResponse = await(request().get())
          response.status shouldBe Status.FORBIDDEN
        }
      }
    }

    "user is authorised" when {

      "sequenceNumber is not provided" when {
        "forceFetchNew = true" must {
          "return a success" when {
            "no movement exists in mongo so GetMovement is called" in new Test {
              override def forceFetchNew: Boolean = true

              override def setupStubs(): StubMapping = {
                AuthStub.authorised()
                DownstreamStub.onSuccess(DownstreamStub.GET, downstreamEisUri, downstreamEisGetMovementQueryParam, Status.OK, getRawMovementJson())
              }

              val response: WSResponse = await(request().get())
              response.status shouldBe Status.OK
              response.header("Content-Type") shouldBe Some("application/json")
              response.json shouldBe getMovementJson()
            }

            "a movement exists in mongo" in new Test {
              override def forceFetchNew: Boolean = true

              override def setupStubs(): StubMapping = {
                AuthStub.authorised()
                DownstreamStub.onSuccess(DownstreamStub.GET, downstreamEisUri, downstreamEisGetMovementQueryParam, Status.OK, getRawMovementJson())
              }

              await(repository.set(GetMovementMongoResponse(testArc, sequenceNumber = 1, data = JsString(getMovementResponseBody()))))

              val response: WSResponse = await(request().get())
              response.status shouldBe Status.OK
              response.header("Content-Type") shouldBe Some("application/json")
              response.json shouldBe Json.toJson(getMovementResponse())
            }
          }
          "return an error" when {
            "downstream call returns a non-200 HTTP response" in new Test {
              override def forceFetchNew: Boolean = true

              override def setupStubs(): StubMapping = {
                AuthStub.authorised()
                DownstreamStub.onError(DownstreamStub.GET, downstreamEisUri, Status.INTERNAL_SERVER_ERROR, "bad things")
              }

              val response: WSResponse = await(request().get())
              response.status shouldBe Status.INTERNAL_SERVER_ERROR
              response.header("Content-Type") shouldBe Some("application/json")
              response.json shouldBe Json.obj(
                "message" -> "Request not processed returned by EIS, error response: bad things"
              )
            }
          }
        }

        "forceFetchNew = false" must {
          "return a success" when {
            "no movement exists in mongo so GetMovement is called" in new Test {
              override def forceFetchNew: Boolean = false

              override def setupStubs(): StubMapping = {
                AuthStub.authorised()
                DownstreamStub.onSuccess(DownstreamStub.GET, downstreamEisUri, downstreamEisGetMovementQueryParam, Status.OK, getRawMovementJson())
              }

              val response: WSResponse = await(request().get())
              response.status shouldBe Status.OK
              response.header("Content-Type") shouldBe Some("application/json")
              response.json shouldBe getMovementJson()
            }
            "a movement exists in mongo so the data from Mongo is returned without any calls downstream" in new Test {
              override def forceFetchNew: Boolean = false

              override def setupStubs(): StubMapping = {
                AuthStub.authorised()
              }

              await(repository.set(GetMovementMongoResponse(testArc, sequenceNumber = 1, data = JsString(getMovementResponseBody()))))

              val response: WSResponse = await(request().get())
              response.status shouldBe Status.OK
              response.header("Content-Type") shouldBe Some("application/json")
              response.json shouldBe getMovementJson()
            }
          }
          "return an error" when {
            "downstream call returns a non-200 HTTP response" in new Test {
              override def forceFetchNew: Boolean = false

              override def setupStubs(): StubMapping = {
                AuthStub.authorised()
                DownstreamStub.onError(DownstreamStub.GET, downstreamEisUri, Status.INTERNAL_SERVER_ERROR, "bad things")
              }

              val response: WSResponse = await(request().get())
              response.status shouldBe Status.INTERNAL_SERVER_ERROR
              response.header("Content-Type") shouldBe Some("application/json")
              response.json shouldBe Json.obj(
                "message" -> "Request not processed returned by EIS, error response: bad things"
              )
            }
          }
        }
      }

      "sequenceNumber is provided" when {

        //forceFetchNew has no effect when a sequenceNumber is supplied, so always behave the same
        Seq(true, false).foreach { forceFetchEnabled =>

          s"forceFetchNew = $forceFetchEnabled" must {
            "return a success" when {
              "no movement exists in mongo so GetMovement is called" in new Test {

                override def forceFetchNew: Boolean = forceFetchEnabled
                override def sequenceNumber: Option[Int] = Some(1)

                override def setupStubs(): StubMapping = {
                  AuthStub.authorised()
                  DownstreamStub.onSuccess(DownstreamStub.GET, downstreamEisUri, downstreamEisGetMovementQueryParam, Status.OK, getRawMovementJson())
                }

                val response: WSResponse = await(request().get())
                response.status shouldBe Status.OK
                response.header("Content-Type") shouldBe Some("application/json")
                response.json shouldBe getMovementJson()
              }
              "a movement exists in mongo, the sequence number of that movement is the same as the requested sequenceNumber (return from cache)" in new Test {
                override def forceFetchNew: Boolean = forceFetchEnabled

                override def sequenceNumber: Option[Int] = Some(1)

                override def setupStubs(): StubMapping = {
                  AuthStub.authorised()
                }

                await(repository.set(GetMovementMongoResponse(testArc, sequenceNumber = 1, data = JsString(getMovementResponseBody()))))

                val response: WSResponse = await(request().get())
                response.status shouldBe Status.OK
                response.header("Content-Type") shouldBe Some("application/json")
                response.json shouldBe getMovementJson()
              }
              "a movement exists in mongo, the sequence number is different so GetMovement is called" in new Test {
                override def forceFetchNew: Boolean = forceFetchEnabled
                override def sequenceNumber: Option[Int] = Some(1)

                override def setupStubs(): StubMapping = {
                  AuthStub.authorised()
                  DownstreamStub.onSuccess(DownstreamStub.GET, downstreamEisUri, downstreamEisGetMovementQueryParam, Status.OK, getRawMovementJson())
                }

                await(repository.set(GetMovementMongoResponse(testArc, sequenceNumber = 2, data = JsString(getMovementResponseBody(2)))))

                val response: WSResponse = await(request().get())
                response.status shouldBe Status.OK
                response.header("Content-Type") shouldBe Some("application/json")
                response.json shouldBe Json.toJson(getMovementResponse())
              }
            }
            "return an error" when {
              "downstream call returns a non-200 HTTP response" in new Test {
                override def forceFetchNew: Boolean = forceFetchEnabled

                override def sequenceNumber: Option[Int] = Some(1)

                override def setupStubs(): StubMapping = {
                  AuthStub.authorised()
                  DownstreamStub.onError(DownstreamStub.GET, downstreamEisUri, Status.INTERNAL_SERVER_ERROR, "bad things")
                }

                val response: WSResponse = await(request().get())
                response.status shouldBe Status.INTERNAL_SERVER_ERROR
                response.header("Content-Type") shouldBe Some("application/json")
                response.json shouldBe Json.obj(
                  "message" -> "Request not processed returned by EIS, error response: bad things"
                )
              }
            }
          }
        }
      }
    }
  }
}
