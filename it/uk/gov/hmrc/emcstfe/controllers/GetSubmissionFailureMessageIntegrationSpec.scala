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
import org.mongodb.scala.Document
import play.api.http.Status
import play.api.http.Status.FORBIDDEN
import play.api.libs.json.{Json, JsonValidationError}
import play.api.libs.ws.{WSRequest, WSResponse}
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.featureswitch.core.config.{FeatureSwitching, SendToEIS}
import uk.gov.hmrc.emcstfe.fixtures.GetSubmissionFailureMessageFixtures
import uk.gov.hmrc.emcstfe.models.mongo.CreateMovementUserAnswers
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse._
import uk.gov.hmrc.emcstfe.repositories.CreateMovementUserAnswersRepositoryImpl
import uk.gov.hmrc.emcstfe.stubs.{AuthStub, DownstreamStub}
import uk.gov.hmrc.emcstfe.support.IntegrationBaseSpec

import java.time.Instant
import scala.xml.XML

class GetSubmissionFailureMessageIntegrationSpec extends IntegrationBaseSpec with GetSubmissionFailureMessageFixtures with FeatureSwitching {

  override lazy val config = app.injector.instanceOf[AppConfig]

  lazy val createMovementUserAnswersRepository: CreateMovementUserAnswersRepositoryImpl = app.injector.instanceOf[CreateMovementUserAnswersRepositoryImpl]

  import GetSubmissionFailureMessageResponseFixtures._

  private abstract class Test(sendToEIS: Boolean = true) {
    val userAnswers: CreateMovementUserAnswers = CreateMovementUserAnswers(testErn, testDraftId, Json.obj("foo" -> "bar"), submissionFailures = Seq.empty, Instant.ofEpochSecond(1), hasBeenSubmitted = true, submittedDraftId = Some(testDraftId))

    def setupStubs(): StubMapping

    def uri: String = s"/submission-failure-message/$testErn/$testMessageId"

    def eisUri: String = "/emcs/messages/v1/submission-failure-message"
    def chrisUri: String = "/ChRISOSB/EMCS/EMCSApplicationService/2"

    def eisQueryParams: Map[String, String] = Map(
      "exciseregistrationnumber" -> testErn,
      "uniquemessageid" -> testMessageId
    )

    def request(): WSRequest = {
      if(sendToEIS) enable(SendToEIS) else disable(SendToEIS)
      setupStubs()
      buildRequest(uri)
    }
  }

  override protected def afterEach(): Unit = {
    super.afterEach()
    await(createMovementUserAnswersRepository.collection.deleteMany(Document()).toFuture())
  }

  "Calling get submission failure message endpoint" when {

    "user is unauthorised" must {
      s"return FORBIDDEN ($FORBIDDEN)" in new Test {
        override def setupStubs(): StubMapping = {
          AuthStub.unauthorised()
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe FORBIDDEN
      }
    }

    "user is authorised" when {

      "return forbidden" when {
        "the ERN requested does not match the ERN of the credential" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised("WrongERN")
          }

          val response: WSResponse = await(request().get())
          response.status shouldBe Status.FORBIDDEN
        }
      }

      "sending data to EIS" when {
        "return a success" when {
          "all downstream calls are successful - returning isTFESubmission = true when the correlation ID is in Mongo" in new Test {
            override def setupStubs(): StubMapping = {
              AuthStub.authorised()
              DownstreamStub.onSuccess(DownstreamStub.GET, eisUri, eisQueryParams, Status.OK, getSubmissionFailureMessageResponseDownstreamJson)
            }

            await(createMovementUserAnswersRepository.set(userAnswers))

            val response: WSResponse = await(request().get())
            response.status shouldBe Status.OK
            response.header("Content-Type") shouldBe Some("application/json")
            response.json shouldBe getSubmissionFailureMessageResponseJson(isTFESubmission = true)
          }

          "all downstream calls are successful - returning isTFESubmission = false when the correlation ID is not in Mongo" in new Test {
            override def setupStubs(): StubMapping = {
              AuthStub.authorised()
              DownstreamStub.onSuccess(DownstreamStub.GET, eisUri, eisQueryParams, Status.OK, getSubmissionFailureMessageResponseDownstreamJson)
            }

            val response: WSResponse = await(request().get())
            response.status shouldBe Status.OK
            response.header("Content-Type") shouldBe Some("application/json")
            response.json shouldBe getSubmissionFailureMessageResponseJson(isTFESubmission = false)
          }
        }
        "return an error" when {
          "downstream call returns XML with the wrong encoding" in new Test {
            override def setupStubs(): StubMapping = {
              AuthStub.authorised()
              DownstreamStub.onSuccess(DownstreamStub.GET, eisUri, eisQueryParams, Status.OK, getSubmissionFailureMessageResponseDownstreamJsonWrongEncoding)
            }

            val response: WSResponse = await(request().get())
            response.status shouldBe Status.INTERNAL_SERVER_ERROR
            response.header("Content-Type") shouldBe Some("application/json")
            response.json shouldBe Json.toJson(EISJsonParsingError(Seq(JsonValidationError("{\"obj\":[{\"msg\":[\"Content is not allowed in prolog.\"],\"args\":[]}]}"))))
          }
          "downstream call returns unencoded XML" in new Test {
            override def setupStubs(): StubMapping = {
              AuthStub.authorised()
              DownstreamStub.onSuccess(DownstreamStub.GET, eisUri, eisQueryParams, Status.OK, getSubmissionFailureMessageResponseDownstreamJsonNotEncoded)
            }

            val response: WSResponse = await(request().get())
            response.status shouldBe Status.INTERNAL_SERVER_ERROR
            response.header("Content-Type") shouldBe Some("application/json")
            response.json shouldBe Json.toJson(EISJsonParsingError(Seq(JsonValidationError("{\"obj\":[{\"msg\":[\"Illegal base64 character 3c\"],\"args\":[]}]}"))))
          }
          "downstream call returns XML which can't be parsed to JSON" in new Test {
            override def setupStubs(): StubMapping = {
              AuthStub.authorised()
              DownstreamStub.onSuccess(DownstreamStub.GET, eisUri, eisQueryParams, Status.OK, getSubmissionFailureMessageResponseDownstreamJsonBadXml)
            }

            val response: WSResponse = await(request().get())
            response.status shouldBe Status.INTERNAL_SERVER_ERROR
            response.header("Content-Type") shouldBe Some("application/json")
            response.json shouldBe Json.toJson(EISJsonParsingError(Seq(JsonValidationError("{\"obj\":[{\"msg\":[\"{\\\"obj\\\":[{\\\"msg\\\":[\\\"XML failed to parse, with the following errors:\\\\n - EmptyError(//SubmissionFailureMessageDataResponse//IE704//IE704//Header//MessageSender)\\\\n - EmptyError(//SubmissionFailureMessageDataResponse//IE704//IE704//Header//MessageRecipient)\\\\n - EmptyError(//SubmissionFailureMessageDataResponse//IE704//IE704//Header//DateOfPreparation)\\\\n - EmptyError(//SubmissionFailureMessageDataResponse//IE704//IE704//Header//TimeOfPreparation)\\\\n - EmptyError(//SubmissionFailureMessageDataResponse//IE704//IE704//Header//MessageIdentifier)\\\\n - EmptyError(//SubmissionFailureMessageDataResponse//RelatedMessageType)\\\"],\\\"args\\\":[]}]}\"],\"args\":[]}]}"))))
          }
          "downstream call returns an unexpected HTTP response" in new Test {
            override def setupStubs(): StubMapping = {
              AuthStub.authorised()
              DownstreamStub.onSuccess(DownstreamStub.GET, eisUri, eisQueryParams, Status.NO_CONTENT, Json.obj())
            }

            val response: WSResponse = await(request().get())
            response.status shouldBe Status.INTERNAL_SERVER_ERROR
            response.header("Content-Type") shouldBe Some("application/json")
            response.json shouldBe Json.toJson(EISUnknownError(""))
          }
        }
      }

      "sending data to ChRIS" when {
        "return a success" when {
          "all downstream calls are successful - returning isTFESubmission = true when the correlation ID starts with PORTAL" in new Test(sendToEIS = false) {

            val submissionFailureMessageDataXmlBody: String =
              s"""
                 |<p:SubmissionFailureMessageDataResponse xmlns:emcs="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:EMCS:V2.02" xmlns:ie="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/ie704uk/3" xmlns:p="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/SubmissionFailureMessage/3" xmlns:p1="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/EmcsUkCodes/3" xmlns:p2="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/Types/3" xmlns:tms="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:TMS:V2.02" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/SubmissionFailureMessage/3 SubmissionFailureMessageData.xsd ">
                 |  <ie:IE704>
                 |    <ie:Header>
                 |    <tms:MessageSender>NDEA.XI</tms:MessageSender>
                 |    <tms:MessageRecipient>NDEA.XI</tms:MessageRecipient>
                 |    <tms:DateOfPreparation>2001-01-01</tms:DateOfPreparation>
                 |    <tms:TimeOfPreparation>12:00:00</tms:TimeOfPreparation>
                 |    <tms:MessageIdentifier>XI000001</tms:MessageIdentifier>
                 |    <tms:CorrelationIdentifier>PORTAL$testDraftId</tms:CorrelationIdentifier>
                 |  </ie:Header>
                 |  ${IE704BodyFixtures.ie704BodyXmlBody}
                 |  </ie:IE704>
                 |  <ie:RelatedMessageType>IE815</ie:RelatedMessageType>
                 |</p:SubmissionFailureMessageDataResponse>
                 |""".stripMargin

            override def setupStubs(): StubMapping = {
              AuthStub.authorised()
              DownstreamStub.onSuccess(DownstreamStub.POST, chrisUri, Status.OK, XML.loadString(responseSoapEnvelopeWithCDATA(XML.loadString(submissionFailureMessageDataXmlBody))))
            }

            val response: WSResponse = await(request().get())
            response.status shouldBe Status.OK
            response.header("Content-Type") shouldBe Some("application/json")
            response.json shouldBe Json.obj(
              "ie704" -> Json.obj(
                "header" -> Json.obj(
                  "messageSender" -> "NDEA.XI",
                  "messageRecipient" -> "NDEA.XI",
                  "dateOfPreparation" -> "2001-01-01",
                  "timeOfPreparation" -> "12:00:00",
                  "messageIdentifier" -> "XI000001",
                  "correlationIdentifier" -> s"PORTAL$testDraftId"
                ),
                "body" -> IE704BodyFixtures.ie704BodyJson
              ),
              "relatedMessageType" -> "IE815",
              "isTFESubmission" -> true
            )
          }

          "all downstream calls are successful - returning isTFESubmission = true when the correlation ID is in Mongo" in new Test(sendToEIS = false) {
            override def setupStubs(): StubMapping = {
              AuthStub.authorised()
              DownstreamStub.onSuccess(DownstreamStub.POST, chrisUri, Status.OK, XML.loadString(responseSoapEnvelopeWithCDATA(XML.loadString(submissionFailureMessageDataXmlBody))))
            }

            await(createMovementUserAnswersRepository.set(userAnswers))

            val response: WSResponse = await(request().get())
            response.status shouldBe Status.OK
            response.header("Content-Type") shouldBe Some("application/json")
            response.json shouldBe getSubmissionFailureMessageResponseJson(isTFESubmission = true)
          }

          "all downstream calls are successful - returning isTFESubmission = false when the correlation ID is not in Mongo" in new Test(sendToEIS = false) {
            override def setupStubs(): StubMapping = {
              AuthStub.authorised()
              DownstreamStub.onSuccess(DownstreamStub.POST, chrisUri, Status.OK, XML.loadString(responseSoapEnvelopeWithCDATA(XML.loadString(submissionFailureMessageDataXmlBody))))
            }

            val response: WSResponse = await(request().get())
            response.status shouldBe Status.OK
            response.header("Content-Type") shouldBe Some("application/json")
            response.json shouldBe getSubmissionFailureMessageResponseJson(isTFESubmission = false)
          }
        }
        "return an error" when {
          "downstream call returns unexpected XML" in new Test(sendToEIS = false) {
            override def setupStubs(): StubMapping = {
              AuthStub.authorised()
              DownstreamStub.onSuccess(
                DownstreamStub.POST,
                chrisUri,
                Status.OK,
                <Errors>
                  <Error>Something went wrong</Error>
                </Errors>
              )
            }

            val response: WSResponse = await(request().get())
            response.status shouldBe Status.INTERNAL_SERVER_ERROR
            response.header("Content-Type") shouldBe Some("application/json")
            response.json shouldBe Json.toJson(SoapExtractionError)
          }
          "downstream call returns something other than XML" in new Test(sendToEIS = false) {

            override def setupStubs(): StubMapping = {
              AuthStub.authorised()
              DownstreamStub.onSuccess(DownstreamStub.POST, chrisUri, Status.OK, Json.obj())
            }

            val response: WSResponse = await(request().get())
            response.status shouldBe Status.INTERNAL_SERVER_ERROR
            response.header("Content-Type") shouldBe Some("application/json")
            response.json shouldBe Json.toJson(XmlValidationError)
          }
          "downstream call returns a non-200 HTTP response" in new Test(sendToEIS = false) {

            override def setupStubs(): StubMapping = {
              AuthStub.authorised()
              DownstreamStub.onSuccess(DownstreamStub.POST, chrisUri, Status.INTERNAL_SERVER_ERROR, Json.obj())
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
