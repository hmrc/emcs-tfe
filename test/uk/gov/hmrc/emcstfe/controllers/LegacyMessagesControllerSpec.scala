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

import cats.data.EitherT
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK}
import play.api.test.Helpers.{contentAsString, contentType, status}
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.emcstfe.fixtures.LegacyMessagesFixtures
import uk.gov.hmrc.emcstfe.mocks.services.MockLegacyMessagesService
import uk.gov.hmrc.emcstfe.models.legacy._
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.xml.Utility.trim
import scala.xml.{NodeSeq, XML}

class LegacyMessagesControllerSpec extends TestBaseSpec with MockLegacyMessagesService with LegacyMessagesFixtures {

  import GetMessagesResponseFixtures.getRawMessagesResponseModel
  import MessagesDataFixtures.messagesDataXmlBody

  private val fakeRequest = FakeRequest("GET", "/messages/:ern")
  private val controller = new LegacyMessagesController(Helpers.stubControllerComponents(), mockService)(ec)


  "LegacyMessageController" when {
    "called with GetMessages action" should {
      "return 200" when {
        "there is the right response from the service" in {
          val requestWithAction: FakeRequest[NodeSeq] = fakeRequest
            .withHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/GetMessages\"")
            .withBody(<message></message>)

          MockService.performMessageAction(GetMessages, requestWithAction)
            .returns(EitherT.rightT(getRawMessagesResponseModel))

          val result = controller.performMessagesOperation()(requestWithAction)

          status(result) shouldBe OK
          contentType(result) shouldBe Some("application/soap+xml")
          trim(XML.loadString(contentAsString(result))) shouldBe trim(soapEnvelope(schemaResultBody(XML.loadString(messagesDataXmlBody))))

        }
      }
      "return 500" when {
        "the action cannot be parsed" in {
          val requestWithAction: FakeRequest[NodeSeq] = fakeRequest
            .withHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/GetMessage\"")
            .withBody(<message></message>)

          val result = controller.performMessagesOperation()(requestWithAction)

          status(result) shouldBe INTERNAL_SERVER_ERROR
          contentAsString(result) shouldBe "Unknown action requested for legacy: GetMessage"
        }

        "the no action in headers" in {
          val requestWithAction: FakeRequest[NodeSeq] = fakeRequest
            .withHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8")
            .withBody(<message></message>)

          val result = controller.performMessagesOperation()(requestWithAction)

          status(result) shouldBe INTERNAL_SERVER_ERROR
          contentAsString(result) shouldBe "no action found in the request"
        }

        "the performMessageOperation returns an error" in {
          val requestWithAction: FakeRequest[NodeSeq] = fakeRequest
            .withHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/GetMessages\"")
            .withBody(<message></message>)

          MockService.performMessageAction(GetMessages, requestWithAction)
            .returns(EitherT.leftT(ErrorResponse.UnexpectedDownstreamResponseError))

          val result = controller.performMessagesOperation()(requestWithAction)

          status(result) shouldBe INTERNAL_SERVER_ERROR
          contentAsString(result) shouldBe "Unexpected downstream response status"
        }
      }
    }

    "called with GetMessageStatistics action" should {
      "return 200" when {
        "there is the right response from the service" in {
          val requestWithAction: FakeRequest[NodeSeq] = fakeRequest
            .withHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/GetMessageStatistics\"")
            .withBody(<message></message>)

          MockService.performMessageAction(GetMessageStatistics, requestWithAction)
            .returns(EitherT.rightT(getMessageStatisticsResponseModel))

          val result = controller.performMessagesOperation()(requestWithAction)

          status(result) shouldBe OK
          contentType(result) shouldBe Some("application/soap+xml")
          trim(XML.loadString(contentAsString(result))) shouldBe trim(soapEnvelope(schemaResultBody(
            XML.loadString(s"""<MessageStatisticsDataResponse xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:ns1="http://hmrc/emcs/tfe/data" xmlns="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MessageStatisticsData/3">
            |        <CountOfAllMessages>${getMessageStatisticsResponseModel.countOfAllMessages}</CountOfAllMessages>
            |        <CountOfNewMessages>${getMessageStatisticsResponseModel.countOfNewMessages}</CountOfNewMessages>
            |      </MessageStatisticsDataResponse>""".stripMargin)
          )))

        }
      }
      "return 500" when {
        "the performMessageOperation returns an error" in {
          val requestWithAction: FakeRequest[NodeSeq] = fakeRequest
            .withHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/GetMessageStatistics\"")
            .withBody(<message></message>)

          MockService.performMessageAction(GetMessageStatistics, requestWithAction)
            .returns(EitherT.leftT(ErrorResponse.UnexpectedDownstreamResponseError))

          val result = controller.performMessagesOperation()(requestWithAction)

          status(result) shouldBe INTERNAL_SERVER_ERROR
          contentAsString(result) shouldBe "Unexpected downstream response status"
        }
      }
    }

    "called with MarkMessagesAsRead action" should {
      "return 200" when {
        "there is the right response from the service" in {
          val requestWithAction: FakeRequest[NodeSeq] = fakeRequest
            .withHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/MarkMessagesAsRead\"")
            .withBody(<message></message>)

          MockService.performMessageAction(MarkMessagesAsRead, requestWithAction)
            .returns(EitherT.rightT(markMessageAsReadResponseModel))

          val result = controller.performMessagesOperation()(requestWithAction)

          status(result) shouldBe OK
          contentType(result) shouldBe Some("application/soap+xml")
          trim(XML.loadString(contentAsString(result))).toString() shouldBe trim(soapEnvelope(recordsAffectedBody(markMessageAsReadResponseModel.recordsAffected))).toString()

        }
      }
      "return 500" when {
        "the performMessageOperation returns an error" in {
          val requestWithAction: FakeRequest[NodeSeq] = fakeRequest
            .withHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/MarkMessagesAsRead\"")
            .withBody(<message></message>)

          MockService.performMessageAction(MarkMessagesAsRead, requestWithAction)
            .returns(EitherT.leftT(ErrorResponse.UnexpectedDownstreamResponseError))

          val result = controller.performMessagesOperation()(requestWithAction)

          status(result) shouldBe INTERNAL_SERVER_ERROR
          contentAsString(result) shouldBe "Unexpected downstream response status"
        }
      }
    }

    "called with SetMessageAsLogicallyDeleted action" should {
      "return 200" when {
        "there is the right response from the service" in {
          val requestWithAction: FakeRequest[NodeSeq] = fakeRequest
            .withHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/SetMessageAsLogicallyDeleted\"")
            .withBody(<message></message>)

          MockService.performMessageAction(SetMessageAsLogicallyDeleted, requestWithAction)
            .returns(EitherT.rightT(setMessageAsLogicallyDeletedResponseModel))

          val result = controller.performMessagesOperation()(requestWithAction)

          status(result) shouldBe OK
          contentType(result) shouldBe Some("application/soap+xml")
          trim(XML.loadString(contentAsString(result))).toString() shouldBe trim(soapEnvelope(recordsAffectedBody(setMessageAsLogicallyDeletedResponseModel.recordsAffected))).toString()

        }
      }
      "return 500" when {
        "the performMessageOperation returns an error" in {
          val requestWithAction: FakeRequest[NodeSeq] = fakeRequest
            .withHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/SetMessageAsLogicallyDeleted\"")
            .withBody(<message></message>)

          MockService.performMessageAction(SetMessageAsLogicallyDeleted, requestWithAction)
            .returns(EitherT.leftT(ErrorResponse.UnexpectedDownstreamResponseError))

          val result = controller.performMessagesOperation()(requestWithAction)

          status(result) shouldBe INTERNAL_SERVER_ERROR
          contentAsString(result) shouldBe "Unexpected downstream response status"
        }
      }
    }

    "called with GetSubmissionFailureMessage action" should {
      "return 200" when {
        "there is the right response from the service" in {
          val requestWithAction: FakeRequest[NodeSeq] = fakeRequest
            .withHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/GetSubmissionFailureMessage\"")
            .withBody(<message></message>)

          MockService.performMessageAction(GetSubmissionFailureMessage, requestWithAction)
            .returns(EitherT.rightT(IE704Xml.rawGetSubmissionFailureMessageResponse))

          val result = controller.performMessagesOperation()(requestWithAction)

          status(result) shouldBe OK
          contentType(result) shouldBe Some("application/soap+xml")
          trim(XML.loadString(contentAsString(result))).toString() shouldBe trim(soapEnvelope(schemaResultBody(XML.loadString(IE704Xml.fullXML)))).toString()

        }
      }
      "return 500" when {
        "the performMessageOperation returns an error" in {
          val requestWithAction: FakeRequest[NodeSeq] = fakeRequest
            .withHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/GetSubmissionFailureMessage\"")
            .withBody(<message></message>)

          MockService.performMessageAction(GetSubmissionFailureMessage, requestWithAction)
            .returns(EitherT.leftT(ErrorResponse.UnexpectedDownstreamResponseError))

          val result = controller.performMessagesOperation()(requestWithAction)

          status(result) shouldBe INTERNAL_SERVER_ERROR
          contentAsString(result) shouldBe "Unexpected downstream response status"
        }
      }
    }

    "called with GetMovement action" should {
      "return 200" when {
        "there is the right response from the service" in {
          val requestWithAction: FakeRequest[NodeSeq] = fakeRequest
            .withHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/GetMovement\"")
            .withBody(<message></message>)

          MockService.performMessageAction(GetMovement, requestWithAction)
            .returns(EitherT.rightT(getRawMovementResponse()))

          val result = controller.performMessagesOperation()(requestWithAction)

          status(result) shouldBe OK
          contentType(result) shouldBe Some("application/soap+xml")
          trim(XML.loadString(contentAsString(result))).toString() shouldBe trim(soapEnvelope(schemaResultBody(XML.loadString(getMovementResponseBody())))).toString()

        }
      }
      "return 500" when {
        "the performMessageOperation returns an error" in {
          val requestWithAction: FakeRequest[NodeSeq] = fakeRequest
            .withHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/GetMovement\"")
            .withBody(<message></message>)

          MockService.performMessageAction(GetMovement, requestWithAction)
            .returns(EitherT.leftT(ErrorResponse.UnexpectedDownstreamResponseError))

          val result = controller.performMessagesOperation()(requestWithAction)

          status(result) shouldBe INTERNAL_SERVER_ERROR
          contentAsString(result) shouldBe "Unexpected downstream response status"
        }
      }
    }
  }

}
