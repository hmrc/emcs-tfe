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
import play.api.http.{HeaderNames, Status}
import play.api.libs.json.{JsString, JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Injecting
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.featureswitch.core.config.{FeatureSwitching, SendToEIS}
import uk.gov.hmrc.emcstfe.fixtures.{GetMovementFixture, GetMovementIfChangedFixture}
import uk.gov.hmrc.emcstfe.models.mongo.GetMovementMongoResponse
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse._
import uk.gov.hmrc.emcstfe.repositories.GetMovementRepositoryImpl
import uk.gov.hmrc.emcstfe.stubs.{AuthStub, DownstreamStub}
import uk.gov.hmrc.emcstfe.support.IntegrationBaseSpec

import scala.xml.XML

class GetMovementIntegrationSpec extends IntegrationBaseSpec with GetMovementFixture with GetMovementIfChangedFixture with Injecting with FeatureSwitching {

  override val config: AppConfig = app.injector.instanceOf[AppConfig]

  val repository: GetMovementRepositoryImpl = inject[GetMovementRepositoryImpl]

  override def beforeEach(): Unit = {
    disable(SendToEIS)
    await(repository.collection.deleteMany(BsonDocument()).toFuture())
    super.beforeEach()
  }

  override def afterAll(): Unit = {
    sys.props -= SendToEIS.configName
    super.afterAll()
  }

  private trait Test {
    def setupStubs(): StubMapping

    def uri: String = s"/movement/$testErn/$testArc"

    def downstreamUri: String = "/ChRISOSB/EMCS/EMCSApplicationService/2"

    def downstreamEisUri: String = "/emcs/movements/v1/movement"

    def downstreamEisGetMovementQueryParam: Map[String, String] =
      Seq(
        Some("exciseregistrationnumber" -> testErn),
        Some("arc" -> testArc),
        sequenceNumber.map(seq => "sequencenumber" -> seq.toString)
      ).flatten.toMap

    def generateHeaders(action: String) = Map(HeaderNames.CONTENT_TYPE -> s"""application/soap+xml; charset=UTF-8; action="$action"""")

    def getMovementIfChangedHeaders: Map[String, String] = generateHeaders("http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/GetMovementIfChanged")

    def getMovementHeaders: Map[String, String] = generateHeaders("http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/GetMovement")

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
          DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Status.OK, XML.loadString(getMovementSoapWrapper()))
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
                DownstreamStub.onSuccessWithHeaders(DownstreamStub.POST, downstreamUri, getMovementHeaders, Status.OK, XML.loadString(getMovementSoapWrapper()))
              }

              val response: WSResponse = await(request().get())
              response.status shouldBe Status.OK
              response.header("Content-Type") shouldBe Some("application/json")
              response.json shouldBe getMovementJson()
            }
            "no movement exists in mongo so GetMovement is called (calling EIS)" in new Test {
              override def forceFetchNew: Boolean = true

              override def setupStubs(): StubMapping = {
                enable(SendToEIS)
                AuthStub.authorised()
                DownstreamStub.onSuccess(DownstreamStub.GET, downstreamEisUri, downstreamEisGetMovementQueryParam, Status.OK, getRawMovementJson())
              }

              val response: WSResponse = await(request().get())
              response.status shouldBe Status.OK
              response.header("Content-Type") shouldBe Some("application/json")
              response.json shouldBe getMovementJson()
            }
            "a movement exists in mongo so GetMovementIfChanged is called, but the GetMovementIfChanged call returns no differences" in new Test {
              override def forceFetchNew: Boolean = true

              override def setupStubs(): StubMapping = {
                AuthStub.authorised()
                DownstreamStub.onSuccessWithHeaders(DownstreamStub.POST, downstreamUri, getMovementIfChangedHeaders, Status.OK, XML.loadString(
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
                     |</tns:Envelope>""".stripMargin))
              }

              await(repository.set(GetMovementMongoResponse(testArc, sequenceNumber = 1,  data = JsString(getMovementResponseBody()))))

              val response: WSResponse = await(request().get())
              response.status shouldBe Status.OK
              response.header("Content-Type") shouldBe Some("application/json")
              response.json shouldBe getMovementJson()
            }
            "a movement exists in mongo so GetMovementIfChanged is called, and the GetMovementIfChanged call returns differences" in new Test {
              override def forceFetchNew: Boolean = true

              override def setupStubs(): StubMapping = {
                AuthStub.authorised()
                DownstreamStub.onSuccessWithHeaders(DownstreamStub.POST, downstreamUri, getMovementIfChangedHeaders, Status.OK, XML.loadString(getMovementIfChangedWithChangeSoapWrapper()))
              }

              await(repository.set(GetMovementMongoResponse(testArc, sequenceNumber = 1, JsString(getMovementIfChangedResponseBody()))))

              val response: WSResponse = await(request().get())
              response.status shouldBe Status.OK
              response.header("Content-Type") shouldBe Some("application/json")
              response.json shouldBe Json.toJson(getMovementIfChangedResponse())
            }

            "a movement exists in mongo (calling EIS)" in new Test {
              override def forceFetchNew: Boolean = true

              override def setupStubs(): StubMapping = {
                enable(SendToEIS)
                AuthStub.authorised()
                DownstreamStub.onSuccess(DownstreamStub.GET, downstreamEisUri, downstreamEisGetMovementQueryParam, Status.OK, getRawMovementIfChangedJson())
              }

              await(repository.set(GetMovementMongoResponse(testArc, sequenceNumber = 1, data = JsString(getMovementIfChangedResponseBody()))))

              val response: WSResponse = await(request().get())
              response.status shouldBe Status.OK
              response.header("Content-Type") shouldBe Some("application/json")
              response.json shouldBe Json.toJson(getMovementIfChangedResponse())
            }
          }
          "return an error" when {
            "downstream call returns unexpected XML" in new Test {
              override def forceFetchNew: Boolean = true

              override def setupStubs(): StubMapping = {
                AuthStub.authorised()
                DownstreamStub.onSuccessWithHeaders(DownstreamStub.POST, downstreamUri, getMovementHeaders, Status.OK, <Message>Success!</Message>)
              }

              val response: WSResponse = await(request().get())
              response.status shouldBe Status.INTERNAL_SERVER_ERROR
              response.header("Content-Type") shouldBe Some("application/json")
              response.json shouldBe Json.toJson(SoapExtractionError)
            }
            "downstream call returns something other than XML" in new Test {
              override def forceFetchNew: Boolean = true

              val referenceDataResponseBody: JsValue = Json.obj("message" -> "Success!")

              override def setupStubs(): StubMapping = {
                AuthStub.authorised()
                DownstreamStub.onSuccessWithHeaders(DownstreamStub.POST, downstreamUri, getMovementHeaders, Status.OK, referenceDataResponseBody)
              }

              val response: WSResponse = await(request().get())
              response.status shouldBe Status.INTERNAL_SERVER_ERROR
              response.header("Content-Type") shouldBe Some("application/json")
              response.json shouldBe Json.toJson(XmlValidationError)
            }
            "downstream call returns a non-200 HTTP response" in new Test {
              override def forceFetchNew: Boolean = true

              val referenceDataResponseBody: JsValue = Json.parse(
                s"""
                   |{
                   |   "message": "test message"
                   |}
                   |""".stripMargin
              )

              override def setupStubs(): StubMapping = {
                AuthStub.authorised()
                DownstreamStub.onSuccessWithHeaders(DownstreamStub.POST, downstreamUri, getMovementHeaders, Status.INTERNAL_SERVER_ERROR, referenceDataResponseBody)
              }

              val response: WSResponse = await(request().get())
              response.status shouldBe Status.INTERNAL_SERVER_ERROR
              response.header("Content-Type") shouldBe Some("application/json")
              response.json shouldBe Json.toJson(UnexpectedDownstreamResponseError)
            }
          }
        }

        "forceFetchNew = false" must {
          "return a success" when {
            "no movement exists in mongo so GetMovement is called (calling ChRIS)" in new Test {
              override def forceFetchNew: Boolean = false

              override def setupStubs(): StubMapping = {
                AuthStub.authorised()
                DownstreamStub.onSuccessWithHeaders(DownstreamStub.POST, downstreamUri, getMovementHeaders, Status.OK, XML.loadString(getMovementSoapWrapper()))
              }

              val response: WSResponse = await(request().get())
              response.status shouldBe Status.OK
              response.header("Content-Type") shouldBe Some("application/json")
              response.json shouldBe getMovementJson()
            }
            "no movement exists in mongo so GetMovement is called (calling EIS)" in new Test {
              override def forceFetchNew: Boolean = false

              override def setupStubs(): StubMapping = {
                enable(SendToEIS)
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
            "downstream call returns unexpected XML" in new Test {
              override def forceFetchNew: Boolean = false

              override def setupStubs(): StubMapping = {
                AuthStub.authorised()
                DownstreamStub.onSuccessWithHeaders(DownstreamStub.POST, downstreamUri, getMovementHeaders, Status.OK, <Message>Success!</Message>)
              }

              val response: WSResponse = await(request().get())
              response.status shouldBe Status.INTERNAL_SERVER_ERROR
              response.header("Content-Type") shouldBe Some("application/json")
              response.json shouldBe Json.toJson(SoapExtractionError)
            }
            "downstream call returns something other than XML" in new Test {
              override def forceFetchNew: Boolean = false

              val referenceDataResponseBody: JsValue = Json.obj("message" -> "Success!")

              override def setupStubs(): StubMapping = {
                AuthStub.authorised()
                DownstreamStub.onSuccessWithHeaders(DownstreamStub.POST, downstreamUri, getMovementHeaders, Status.OK, referenceDataResponseBody)
              }

              val response: WSResponse = await(request().get())
              response.status shouldBe Status.INTERNAL_SERVER_ERROR
              response.header("Content-Type") shouldBe Some("application/json")
              response.json shouldBe Json.toJson(XmlValidationError)
            }
            "downstream call returns a non-200 HTTP response" in new Test {
              override def forceFetchNew: Boolean = false

              val referenceDataResponseBody: JsValue = Json.parse(
                s"""
                   |{
                   |   "message": "test message"
                   |}
                   |""".stripMargin
              )

              override def setupStubs(): StubMapping = {
                AuthStub.authorised()
                DownstreamStub.onSuccessWithHeaders(DownstreamStub.POST, downstreamUri, getMovementHeaders, Status.INTERNAL_SERVER_ERROR, referenceDataResponseBody)
              }

              val response: WSResponse = await(request().get())
              response.status shouldBe Status.INTERNAL_SERVER_ERROR
              response.header("Content-Type") shouldBe Some("application/json")
              response.json shouldBe Json.toJson(UnexpectedDownstreamResponseError)
            }
          }
        }
      }

      "sequenceNumber is provided" when {

        //forceFetchNew has no effect when a sequenceNumber is supplied, so always behave the same
        Seq(true, false).foreach { forceFetchEnabled =>

          s"forceFetchNew = $forceFetchEnabled" must {
            "return a success" when {
              "no movement exists in mongo so GetMovement is called (calling ChRIS)" in new Test {

                override def forceFetchNew: Boolean = forceFetchEnabled
                override def sequenceNumber: Option[Int] = Some(1)

                override def setupStubs(): StubMapping = {
                  AuthStub.authorised()
                  DownstreamStub.onSuccessWithHeaders(DownstreamStub.POST, downstreamUri, getMovementHeaders, Status.OK, XML.loadString(getMovementSoapWrapper()))
                }

                val response: WSResponse = await(request().get())
                response.status shouldBe Status.OK
                response.header("Content-Type") shouldBe Some("application/json")
                response.json shouldBe getMovementJson()
              }
              "no movement exists in mongo so GetMovement is called (calling EIS)" in new Test {

                override def forceFetchNew: Boolean = forceFetchEnabled
                override def sequenceNumber: Option[Int] = Some(1)

                override def setupStubs(): StubMapping = {
                  enable(SendToEIS)
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
              "a movement exists in mongo, the sequence number is different so GetMovement is called (calling ChRIS)" in new Test {
                override def forceFetchNew: Boolean = forceFetchEnabled
                override def sequenceNumber: Option[Int] = Some(1)

                override def setupStubs(): StubMapping = {
                  AuthStub.authorised()
                  DownstreamStub.onSuccessWithHeaders(DownstreamStub.POST, downstreamUri, getMovementHeaders, Status.OK, XML.loadString(getMovementSoapWrapper()))
                }

                await(repository.set(GetMovementMongoResponse(testArc, sequenceNumber = 2, data = JsString(getMovementResponseBody(2)))))

                val response: WSResponse = await(request().get())
                response.status shouldBe Status.OK
                response.header("Content-Type") shouldBe Some("application/json")
                response.json shouldBe Json.toJson(getMovementResponse())
              }
              "a movement exists in mongo, the sequence number is different so GetMovement is called (calling EIS)" in new Test {
                override def forceFetchNew: Boolean = forceFetchEnabled
                override def sequenceNumber: Option[Int] = Some(1)

                override def setupStubs(): StubMapping = {
                  enable(SendToEIS)
                  AuthStub.authorised()
                  DownstreamStub.onSuccess(DownstreamStub.GET, downstreamEisUri, downstreamEisGetMovementQueryParam, Status.OK, getRawMovementIfChangedJson())
                }

                await(repository.set(GetMovementMongoResponse(testArc, sequenceNumber = 2, data = JsString(getMovementIfChangedResponseBody(2)))))

                val response: WSResponse = await(request().get())
                response.status shouldBe Status.OK
                response.header("Content-Type") shouldBe Some("application/json")
                response.json shouldBe Json.toJson(getMovementIfChangedResponse())
              }
            }
            "return an error" when {
              "downstream call returns unexpected XML" in new Test {
                override def forceFetchNew: Boolean = forceFetchEnabled
                override def sequenceNumber: Option[Int] = Some(1)

                override def setupStubs(): StubMapping = {
                  AuthStub.authorised()
                  DownstreamStub.onSuccessWithHeaders(DownstreamStub.POST, downstreamUri, getMovementHeaders, Status.OK, <Message>Success!</Message>)
                }

                val response: WSResponse = await(request().get())
                response.status shouldBe Status.INTERNAL_SERVER_ERROR
                response.header("Content-Type") shouldBe Some("application/json")
                response.json shouldBe Json.toJson(SoapExtractionError)
              }
              "downstream call returns something other than XML" in new Test {
                override def forceFetchNew: Boolean = forceFetchEnabled

                override def sequenceNumber: Option[Int] = Some(1)

                val referenceDataResponseBody: JsValue = Json.obj("message" -> "Success!")

                override def setupStubs(): StubMapping = {
                  AuthStub.authorised()
                  DownstreamStub.onSuccessWithHeaders(DownstreamStub.POST, downstreamUri, getMovementHeaders, Status.OK, referenceDataResponseBody)
                }

                val response: WSResponse = await(request().get())
                response.status shouldBe Status.INTERNAL_SERVER_ERROR
                response.header("Content-Type") shouldBe Some("application/json")
                response.json shouldBe Json.toJson(XmlValidationError)
              }
              "downstream call returns a non-200 HTTP response" in new Test {
                override def forceFetchNew: Boolean = forceFetchEnabled

                override def sequenceNumber: Option[Int] = Some(1)

                val referenceDataResponseBody: JsValue = Json.parse(
                  s"""
                     |{
                     |   "message": "test message"
                     |}
                     |""".stripMargin
                )

                override def setupStubs(): StubMapping = {
                  AuthStub.authorised()
                  DownstreamStub.onSuccessWithHeaders(DownstreamStub.POST, downstreamUri, getMovementHeaders, Status.INTERNAL_SERVER_ERROR, referenceDataResponseBody)
                }

                val response: WSResponse = await(request().get())
                response.status shouldBe Status.INTERNAL_SERVER_ERROR
                response.header("Content-Type") shouldBe Some("application/json")
                response.json shouldBe Json.toJson(UnexpectedDownstreamResponseError)
              }
            }
          }
        }
      }
    }
  }
}
