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
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK}
import play.api.libs.ws.{WSRequest, WSResponse}
import uk.gov.hmrc.emcstfe.fixtures.LegacyMessagesFixtures
import uk.gov.hmrc.emcstfe.stubs.DownstreamStub
import uk.gov.hmrc.emcstfe.support.IntegrationBaseSpec

import scala.xml.Utility.trim
import scala.xml.XML

class LegacyMessagesControllerIntegrationSpec extends IntegrationBaseSpec with LegacyMessagesFixtures {

  private trait Test {
    def setupStubs(): StubMapping

    def uri: String = s"/legacy/messages"

    lazy val downstreamGetMessagesUrl: String = "/emcs/messages/v1/messages"

    lazy val downstreamGetMessagesQueryParam: Map[String, String] = Map(
      "exciseregistrationnumber" -> testErn,
      "sortfield" -> "datereceived",
      "sortorder" -> "D",
      "startposition" -> "0",
      "maxnotoreturn" -> "30"
    )

    lazy val downstreamMovementUri: String = "/emcs/movements/v1/movement"

    lazy val downstreamGetMovementQueryParam: Map[String, String] = Map(
      "exciseregistrationnumber" -> testErn,
      "arc" -> testArc,
      "sequencenumber" -> "2"
    )

    lazy val getMessageStatisticsUrl: String = "/emcs/messages/v1/message-statistics"

    lazy val downstreamGetMessageStatisticsQueryParam: Map[String, String] = Map(
      "exciseregistrationnumber" -> testErn
    )

    lazy val getSubmissionFailureUrl: String = "/emcs/messages/v1/submission-failure-message"

    lazy val messageUrl: String = "/emcs/messages/v1/message"

    lazy val downstreamMessageOperationQueryParams: Map[String, String] = Map(
      "exciseregistrationnumber" -> testErn,
      "uniquemessageid" -> "1110"
    )

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri)
    }
  }

  "LegacyMessagesController" when {
    "calling GetMovement" should {
      "return the response" when {
        "all is successful" in new  Test {
          override def setupStubs(): StubMapping = {
            DownstreamStub.onSuccess(DownstreamStub.GET, downstreamMovementUri, downstreamGetMovementQueryParam, OK, getRawMovementJson)
          }

          val result: WSResponse = await(request().withHttpHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/GetMovement\"").post(validGetMovementXMLRequest))
          result.status shouldBe OK
          result.body shouldBe soapEnvelope(schemaResultBody(XML.loadString(getMovementResponseBody))).toString()
        }
      }

      "return a 500" when {
        "message cannot be parsed from json" in new Test {
          override def setupStubs(): StubMapping = {
            DownstreamStub.onSuccess(DownstreamStub.GET, downstreamMovementUri, downstreamGetMovementQueryParam, OK, getRawMovementInvalidJson)
          }

          val result: WSResponse = await(request().withHttpHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/GetMovement\"").post(validGetMovementXMLRequest))
          result.status shouldBe INTERNAL_SERVER_ERROR
          result.body shouldBe "Errors parsing JSON, errors: List(JsonValidationError(List({\"obj\":[{\"msg\":[\"Illegal base64 character 3c\"],\"args\":[]}]}),List()))"
        }
      }
    }

    "calling GetMessages" should {
      "return the response" when {
        "all is successful" in new Test {
          import GetMessagesResponseFixtures.getMessagesResponseDownstreamJson
          import MessagesDataFixtures.messagesDataXmlBody

          override def setupStubs(): StubMapping = {
            DownstreamStub.onSuccess(DownstreamStub.GET, downstreamGetMessagesUrl, downstreamGetMessagesQueryParam, OK, getMessagesResponseDownstreamJson)
          }

          val result: WSResponse = await(request().withHttpHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/GetMessages\"").post(validGetMessagesXMLRequest))
          result.status shouldBe OK
          result.body shouldBe soapEnvelope(schemaResultBody(trim(XML.loadString(messagesDataXmlBody)))).toString()
        }
      }

      "return a 500" when {
        "message cannot be parsed from json" in new Test {

          import GetMessagesResponseFixtures.getMessagesResponseDownstreamJsonNotEncoded

          override def setupStubs(): StubMapping = {
            DownstreamStub.onSuccess(DownstreamStub.GET, downstreamGetMessagesUrl, downstreamGetMessagesQueryParam, OK, getMessagesResponseDownstreamJsonNotEncoded)
          }

          val result: WSResponse = await(request().withHttpHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/GetMessages\"").post(validGetMessagesXMLRequest))
          result.status shouldBe INTERNAL_SERVER_ERROR
          result.body shouldBe "Errors parsing JSON, errors: List(JsonValidationError(List({\"obj\":[{\"msg\":[\"Illegal base64 character 3c\"],\"args\":[]}]}),List()))"
        }
      }
    }

    "calling GetSubmissionFailureMessages" should {
      "return the response" when {
        "all is successful" in new Test {
          override def setupStubs(): StubMapping = {
            DownstreamStub.onSuccess(DownstreamStub.GET, getSubmissionFailureUrl, downstreamMessageOperationQueryParams, OK, IE704Xml.rawGetSubmissionFailureMessageResponseJson)
          }

          val result: WSResponse = await(request().withHttpHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/GetSubmissionFailureMessage\"").post(validMessageOperationXMLRequest))
          result.status shouldBe OK
          result.body shouldBe soapEnvelope(schemaResultBody(XML.loadString(IE704Xml.fullXML))).toString()
        }
      }

      "return a 500" when {
        "message cannot be parsed from json" in new Test {

          override def setupStubs(): StubMapping = {
            DownstreamStub.onSuccess(DownstreamStub.GET, getSubmissionFailureUrl, downstreamMessageOperationQueryParams, OK, IE704Xml.rawGetSubmissionFailureMessageResponseInvalidJson)
          }

          val result: WSResponse = await(request().withHttpHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/GetSubmissionFailureMessage\"").post(validMessageOperationXMLRequest))
          result.status shouldBe INTERNAL_SERVER_ERROR
          result.body shouldBe "Errors parsing JSON, errors: List(JsonValidationError(List({\"obj\":[{\"msg\":[\"Content is not allowed in prolog.\"],\"args\":[]}]}),List()))"
        }
      }
    }

    "calling SetMessageAsLogicallyDeleted" should {
      "return the response" when {
        "all is successful" in new Test {
          override def setupStubs(): StubMapping = {
            DownstreamStub.onSuccess(DownstreamStub.DELETE, messageUrl, downstreamMessageOperationQueryParams, OK, setMessageAsLogicallyDeletedDownstreamJson)
          }

          val result: WSResponse = await(request().withHttpHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/SetMessageAsLogicallyDeleted\"").post(validMessageOperationXMLRequest))
          result.status shouldBe OK
          result.body shouldBe soapEnvelope(recordsAffectedBody(1)).toString()
        }
      }
    }

    "calling MarkMessagesAsRead" should {
      "return the response" when {
        "all is successful" in new Test {
          override def setupStubs(): StubMapping = {
            DownstreamStub.onSuccess(DownstreamStub.PUT, messageUrl, downstreamMessageOperationQueryParams, OK, markMessageAsReadDownstreamJson)
          }

          val result: WSResponse = await(request().withHttpHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/MarkMessagesAsRead\"").post(validMessageOperationXMLRequest))
          result.status shouldBe OK
          result.body shouldBe soapEnvelope(recordsAffectedBody(1)).toString()
        }
      }
    }

    "calling GetMessageStatistics" should {
      "return the response" when {
        "all is successful" in new Test {

          override def setupStubs(): StubMapping = {
            DownstreamStub.onSuccess(DownstreamStub.GET, getMessageStatisticsUrl, downstreamGetMessageStatisticsQueryParam, OK, getMessageStatisticsDownstreamJson)
          }

          val result: WSResponse = await(request().withHttpHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/GetMessageStatistics\"").post(validGetMessageStatisticsXMLRequest))
          result.status shouldBe OK
          result.body shouldBe soapEnvelope(schemaResultBody(XML.loadString(
            s"""<MessageStatisticsDataResponse xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:ns1="http://hmrc/emcs/tfe/data" xmlns="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MessageStatisticsData/3">
               |        <CountOfAllMessages>${getMessageStatisticsResponseModel.countOfAllMessages}</CountOfAllMessages>
               |        <CountOfNewMessages>${getMessageStatisticsResponseModel.countOfNewMessages}</CountOfNewMessages>
               |      </MessageStatisticsDataResponse>""".stripMargin))).toString()
        }
      }
    }
  }
}
