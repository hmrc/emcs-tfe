/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.controllers

import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.emcstfe.fixtures.GetMessageFixture
import uk.gov.hmrc.emcstfe.mocks.services.MockGetMessageService
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.UnexpectedDownstreamResponseError
import uk.gov.hmrc.emcstfe.support.UnitSpec

import scala.concurrent.Future

class GetMessageControllerSpec extends UnitSpec with MockGetMessageService with GetMessageFixture {

  private val fakeRequest = FakeRequest("GET", "/get-message/:exciseRegistrationNumber/:arc")
  private val controller = new GetMessageController(Helpers.stubControllerComponents(), mockService)

  private val exciseRegistrationNumber = "My ERN"
  private val arc = "My ARC"


  "GET /get-message/:exciseRegistrationNumber/:arc" should {
    "return 200" when {
      "service returns a Right" in {

        MockService.getMessage().returns(Future.successful(Right(model)))

        val result = controller.getMessage(exciseRegistrationNumber, arc)(fakeRequest)

        status(result) shouldBe Status.OK
        contentAsJson(result) shouldBe jsonResponse
      }
    }
    "return 500" when {
      "service returns a Left" in {

        MockService.getMessage().returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        val result = controller.getMessage(exciseRegistrationNumber, arc)(fakeRequest)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.obj("message" -> UnexpectedDownstreamResponseError.message)
      }
    }
  }
}
