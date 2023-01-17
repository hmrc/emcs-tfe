/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.controllers

import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.emcstfe.fixtures.{GetMovementFixture, GetMovementListFixture}
import uk.gov.hmrc.emcstfe.mocks.services.MockGetMovementService
import uk.gov.hmrc.emcstfe.models.request.{GetMovementListRequest, GetMovementRequest}
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.UnexpectedDownstreamResponseError
import uk.gov.hmrc.emcstfe.support.UnitSpec

import scala.concurrent.Future

class GetMovementControllerSpec extends UnitSpec with MockGetMovementService with GetMovementFixture with GetMovementListFixture {

  private val fakeRequest = FakeRequest("GET", "/movement/:exciseRegistrationNumber/:arc")
  private val controller = new GetMovementController(Helpers.stubControllerComponents(), mockService)

  private val exciseRegistrationNumber = "My ERN"
  private val arc = "My ARC"
  private val getMovementRequest = GetMovementRequest(exciseRegistrationNumber, arc)
  private val getMovementListRequest = GetMovementListRequest(exciseRegistrationNumber)


  "GET /movement/:exciseRegistrationNumber/:arc" should {
    "return 200" when {
      "service returns a Right" in {

        MockService.getMovement(getMovementRequest).returns(Future.successful(Right(getMovementResponse)))

        val result = controller.getMovement(exciseRegistrationNumber, arc)(fakeRequest)

        status(result) shouldBe Status.OK
        contentAsJson(result) shouldBe getMovementJson
      }
    }
    "return 500" when {
      "service returns a Left" in {

        MockService.getMovement(getMovementRequest).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        val result = controller.getMovement(exciseRegistrationNumber, arc)(fakeRequest)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.obj("message" -> UnexpectedDownstreamResponseError.message)
      }
    }
  }

  "GET /movements/:exciseRegistrationNumber" should {
    "return 200" when {
      "service returns a Right" in {

        MockService.getMovementList(getMovementListRequest).returns(Future.successful(Right(getMovementListResponse)))

        val result = controller.getMovementList(exciseRegistrationNumber)(fakeRequest)

        status(result) shouldBe Status.OK
        contentAsJson(result) shouldBe getMovementListJson
      }
    }
    "return 500" when {
      "service returns a Left" in {

        MockService.getMovementList(getMovementListRequest).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        val result = controller.getMovementList(exciseRegistrationNumber)(fakeRequest)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.obj("message" -> UnexpectedDownstreamResponseError.message)
      }
    }
  }
}
