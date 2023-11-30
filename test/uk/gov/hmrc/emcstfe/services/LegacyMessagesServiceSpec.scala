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

import cats.implicits.catsSyntaxApplicativeId
import play.api.test.FakeRequest
import uk.gov.hmrc.emcstfe.fixtures.LegacyMessagesFixtures
import uk.gov.hmrc.emcstfe.mocks.connectors.MockEisConnector
import uk.gov.hmrc.emcstfe.models.legacy._
import uk.gov.hmrc.emcstfe.models.request._
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.concurrent.{ExecutionContext, Future}
import scala.xml.NodeSeq

class LegacyMessagesServiceSpec extends TestBaseSpec with LegacyMessagesFixtures with MockEisConnector {

  val service: LegacyMessagesService = new LegacyMessagesService(mockEisConnector)(ExecutionContext.global)
  private val fakeRequest = FakeRequest("GET", "/messages/:ern")

  "performMessageAction" when {
    "called with GetMessages" should {
      "return Right" when {
        "request is valid and connector call succeeds" in {
          import GetMessagesResponseFixtures.getRawMessagesResponseModel

          implicit val requestWithAction: FakeRequest[NodeSeq] = fakeRequest
            .withHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/SetMessageAsLogicallyDeleted\"")
            .withBody(validGetMessagesXMLRequest)

          val messageRequest = GetMessagesRequest(testErn, "datereceived", "D", 1, 30, Some(0))

          MockEisConnector.getRawMessages(messageRequest)
            .returns(Right(getRawMessagesResponseModel).pure[Future])

          await(service.performMessageAction(GetMessages).value) shouldBe Right(getRawMessagesResponseModel)
        }
      }

      "return left" when {
        "request is valid and connector call fails" in {
          implicit val requestWithAction: FakeRequest[NodeSeq] = fakeRequest
            .withHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/SetMessageAsLogicallyDeleted\"")
            .withBody(validGetMessagesXMLRequest)

          val messageRequest = GetMessagesRequest(testErn, "datereceived", "D", 1, 30, Some(0))

          MockEisConnector.getRawMessages(messageRequest)
            .returns(Left(ErrorResponse.UnexpectedDownstreamResponseError).pure[Future])

          await(service.performMessageAction(GetMessages).value) shouldBe Left(ErrorResponse.UnexpectedDownstreamResponseError)
        }

        "request is invalid" in {
          implicit val requestWithAction: FakeRequest[NodeSeq] = fakeRequest
            .withHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/SetMessageAsLogicallyDeleted\"")
            .withBody(getMessagesXMLRequestNoSortField)

          await(service.performMessageAction(GetMessages).value) shouldBe Left(ErrorResponse.InvalidLegacyRequestProvided("EmptyError(//Control//OperationRequest//Parameters//Parameter[@Name=SortField])"))
        }
      }
    }

    "called with GetMessageStatistics" should {
      "return Right" when {
        "request is valid and connector call succeeds" in {

          implicit val requestWithAction: FakeRequest[NodeSeq] = fakeRequest
            .withHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/GetMessageStatistics\"")
            .withBody(validGetMessageStatisticsXMLRequest)

          val messageRequest = GetMessageStatisticsRequest(testErn)

          MockEisConnector.getMessageStatistics(messageRequest)
            .returns(Right(getMessageStatisticsResponseModel).pure[Future])

          await(service.performMessageAction(GetMessageStatistics).value) shouldBe Right(getMessageStatisticsResponseModel)
        }
      }

      "return left" when {
        "request is valid and connector call fails" in {
          implicit val requestWithAction: FakeRequest[NodeSeq] = fakeRequest
            .withHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/SetMessageAsLogicallyDeleted\"")
            .withBody(validGetMessageStatisticsXMLRequest)

          val messageRequest = GetMessageStatisticsRequest(testErn)

          MockEisConnector.getMessageStatistics(messageRequest)
            .returns(Left(ErrorResponse.UnexpectedDownstreamResponseError).pure[Future])

          await(service.performMessageAction(GetMessageStatistics).value) shouldBe Left(ErrorResponse.UnexpectedDownstreamResponseError)
        }

        "request is invalid" in {
          implicit val requestWithAction: FakeRequest[NodeSeq] = fakeRequest
            .withHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/SetMessageAsLogicallyDeleted\"")
            .withBody(getMessageStatisticsXMLRequestNoERN)

          await(service.performMessageAction(GetMessageStatistics).value) shouldBe Left(ErrorResponse.InvalidLegacyRequestProvided("EmptyError(//Control//OperationRequest//Parameters//Parameter[@Name=ExciseRegistrationNumber])"))
        }
      }
    }

    "called with GetSubmissionFailureMessage" should {
      "return Right" when {
        "request is valid and connector call succeeds" in {

          implicit val requestWithAction: FakeRequest[NodeSeq] = fakeRequest
            .withHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/GetSubmissionFailureMessage\"")
            .withBody(validMessageOperationXMLRequest)

          val messageRequest = GetSubmissionFailureMessageRequest(testErn, "1110")

          MockEisConnector.getRawSubmissionFailureMessage(messageRequest)
            .returns(Right(IE704Xml.rawGetSubmissionFailureMessageResponse).pure[Future])

          await(service.performMessageAction(GetSubmissionFailureMessage).value) shouldBe Right(IE704Xml.rawGetSubmissionFailureMessageResponse)
        }
      }

      "return left" when {
        "request is valid and connector call fails" in {
          implicit val requestWithAction: FakeRequest[NodeSeq] = fakeRequest
            .withHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/SetMessageAsLogicallyDeleted\"")
            .withBody(validMessageOperationXMLRequest)

          val messageRequest = GetSubmissionFailureMessageRequest(testErn, "1110")

          MockEisConnector.getRawSubmissionFailureMessage(messageRequest)
            .returns(Left(ErrorResponse.UnexpectedDownstreamResponseError).pure[Future])

          await(service.performMessageAction(GetSubmissionFailureMessage).value) shouldBe Left(ErrorResponse.UnexpectedDownstreamResponseError)
        }

        "request is invalid" in {
          implicit val requestWithAction: FakeRequest[NodeSeq] = fakeRequest
            .withHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/SetMessageAsLogicallyDeleted\"")
            .withBody(messageOperationXMLRequestNoUniqueMessageId)

          await(service.performMessageAction(GetSubmissionFailureMessage).value) shouldBe Left(ErrorResponse.InvalidLegacyRequestProvided("EmptyError(//Control//OperationRequest//Parameters//Parameter[@Name=UniqueMessageId])"))
        }
      }
    }

    "called with SetMessageAsLogicallyDeleted" should {
      "return Right" when {
        "request is valid and connector call succeeds" in {

          implicit val requestWithAction: FakeRequest[NodeSeq] = fakeRequest
            .withHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/SetMessageAsLogicallyDeleted\"")
            .withBody(validMessageOperationXMLRequest)

          val messageRequest = SetMessageAsLogicallyDeletedRequest(testErn, "1110")

          MockEisConnector.setMessageAsLogicallyDeleted(messageRequest)
            .returns(Right(setMessageAsLogicallyDeletedResponseModel).pure[Future])

          await(service.performMessageAction(SetMessageAsLogicallyDeleted).value) shouldBe Right(setMessageAsLogicallyDeletedResponseModel)
        }
      }

      "return left" when {
        "request is valid and connector call fails" in {
          implicit val requestWithAction: FakeRequest[NodeSeq] = fakeRequest
            .withHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/SetMessageAsLogicallyDeleted\"")
            .withBody(validMessageOperationXMLRequest)

          val messageRequest = SetMessageAsLogicallyDeletedRequest(testErn, "1110")

          MockEisConnector.setMessageAsLogicallyDeleted(messageRequest)
            .returns(Left(ErrorResponse.UnexpectedDownstreamResponseError).pure[Future])

          await(service.performMessageAction(SetMessageAsLogicallyDeleted).value) shouldBe Left(ErrorResponse.UnexpectedDownstreamResponseError)
        }

        "request is invalid" in {
          implicit val requestWithAction: FakeRequest[NodeSeq] = fakeRequest
            .withHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/SetMessageAsLogicallyDeleted\"")
            .withBody(messageOperationXMLRequestNoUniqueMessageId)

          await(service.performMessageAction(SetMessageAsLogicallyDeleted).value) shouldBe Left(ErrorResponse.InvalidLegacyRequestProvided("EmptyError(//Control//OperationRequest//Parameters//Parameter[@Name=UniqueMessageId])"))
        }
      }
    }

    "called with MarkMessagesAsRead" should {
      "return Right" when {
        "request is valid and connector call succeeds" in {

          implicit val requestWithAction: FakeRequest[NodeSeq] = fakeRequest
            .withHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/MarkMessagesAsRead\"")
            .withBody(validMessageOperationXMLRequest)

          val messageRequest = MarkMessageAsReadRequest(testErn, "1110")

          MockEisConnector.markMessageAsRead(messageRequest)
            .returns(Right(markMessageAsReadResponseModel).pure[Future])

          await(service.performMessageAction(MarkMessagesAsRead).value) shouldBe Right(markMessageAsReadResponseModel)
        }
      }

      "return left" when {
        "request is valid and connector call fails" in {
          implicit val requestWithAction: FakeRequest[NodeSeq] = fakeRequest
            .withHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/SetMessageAsLogicallyDeleted\"")
            .withBody(validMessageOperationXMLRequest)

          val messageRequest = MarkMessageAsReadRequest(testErn, "1110")

          MockEisConnector.markMessageAsRead(messageRequest)
            .returns(Left(ErrorResponse.UnexpectedDownstreamResponseError).pure[Future])

          await(service.performMessageAction(MarkMessagesAsRead).value) shouldBe Left(ErrorResponse.UnexpectedDownstreamResponseError)
        }

        "request is invalid" in {
          implicit val requestWithAction: FakeRequest[NodeSeq] = fakeRequest
            .withHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/SetMessageAsLogicallyDeleted\"")
            .withBody(messageOperationXMLRequestNoErn)

          await(service.performMessageAction(MarkMessagesAsRead).value) shouldBe Left(ErrorResponse.InvalidLegacyRequestProvided("EmptyError(//Control//OperationRequest//Parameters//Parameter[@Name=ExciseRegistrationNumber])"))
        }
      }
    }

    "called with GetMovement" should {
      "return Right" when {
        "request is valid and connector call succeeds" in {

          implicit val requestWithAction: FakeRequest[NodeSeq] = fakeRequest
            .withHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/GetMovement\"")
            .withBody(validGetMovementXMLRequest)

          val messageRequest = GetMovementRequest(testErn, testArc, Some(2))

          MockEisConnector.getRawMovement(messageRequest)
            .returns(Right(getRawMovementResponse).pure[Future])

          await(service.performMessageAction(GetMovement).value) shouldBe Right(getRawMovementResponse)
        }
      }

      "return left" when {
        "request is valid and connector call fails" in {
          implicit val requestWithAction: FakeRequest[NodeSeq] = fakeRequest
            .withHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/GetMovement\"")
            .withBody(validGetMovementXMLRequest)

          val messageRequest = GetMovementRequest(testErn, testArc, Some(2))

          MockEisConnector.getRawMovement(messageRequest)
            .returns(Left(ErrorResponse.UnexpectedDownstreamResponseError).pure[Future])

          await(service.performMessageAction(GetMovement).value) shouldBe Left(ErrorResponse.UnexpectedDownstreamResponseError)
        }

        "request is invalid" in {
          implicit val requestWithAction: FakeRequest[NodeSeq] = fakeRequest
            .withHeaders("Content-Type" -> "application/soap+xml; charset=UTF-8; action=\"http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/GetMovement\"")
            .withBody(getMovementXMLRequestNoARC)

          await(service.performMessageAction(GetMovement).value) shouldBe Left(ErrorResponse.InvalidLegacyRequestProvided("EmptyError(//Control//OperationRequest//Parameters//Parameter[@Name=ARC])"))
        }
      }
    }
  }

}
