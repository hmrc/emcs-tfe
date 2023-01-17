/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.controllers

import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.emcstfe.fixtures.GetMovementFixture
import uk.gov.hmrc.emcstfe.mocks.services.MockGetMovementService
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.UnexpectedDownstreamResponseError
import uk.gov.hmrc.emcstfe.support.UnitSpec

import scala.concurrent.Future

class GetMovementControllerSpec extends UnitSpec with MockGetMovementService with GetMovementFixture {

  private val fakeRequest = FakeRequest("GET", "/movement/:exciseRegistrationNumber/:arc")
  private val controller = new GetMovementController(Helpers.stubControllerComponents(), mockService)

  private val exciseRegistrationNumber = "My ERN"
  private val arc = "My ARC"


  "GET /movement/:exciseRegistrationNumber/:arc" should {
    "return 200" when {
      "service returns a Right" in {

        MockService.getMovement().returns(Future.successful(Right(getMovementResponse)))

        val result = controller.getMovement(exciseRegistrationNumber, arc)(fakeRequest)

        status(result) shouldBe Status.OK
        contentAsJson(result) shouldBe jsonResponse
      }
    }
    "return 500" when {
      "service returns a Left" in {

        MockService.getMovement().returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        val result = controller.getMovement(exciseRegistrationNumber, arc)(fakeRequest)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.obj("message" -> UnexpectedDownstreamResponseError.message)
      }
    }
  }
}
