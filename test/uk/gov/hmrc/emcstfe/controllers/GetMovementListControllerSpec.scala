/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.controllers

import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.emcstfe.fixtures.GetMovementListFixture
import uk.gov.hmrc.emcstfe.mocks.services.MockGetMovementListService
import uk.gov.hmrc.emcstfe.models.request.{GetMovementListRequest, GetMovementListSearchOptions}
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.UnexpectedDownstreamResponseError
import uk.gov.hmrc.emcstfe.support.UnitSpec

import scala.concurrent.Future

class GetMovementListControllerSpec extends UnitSpec with MockGetMovementListService with GetMovementListFixture {

  private val fakeRequest = FakeRequest("GET", "/movement/:exciseRegistrationNumber/:arc")
  private val controller = new GetMovementListController(Helpers.stubControllerComponents(), mockService)

  private val exciseRegistrationNumber = "My ERN"
  private val searchOptions = GetMovementListSearchOptions()
  private val getMovementListRequest = GetMovementListRequest(exciseRegistrationNumber, searchOptions)

  "GET /movements/:exciseRegistrationNumber" should {
    "return 200" when {
      "service returns a Right" in {

        MockService.getMovementList(getMovementListRequest).returns(Future.successful(Right(getMovementListResponse)))

        val result = controller.getMovementList(exciseRegistrationNumber, searchOptions)(fakeRequest)

        status(result) shouldBe Status.OK
        contentAsJson(result) shouldBe getMovementListJson
      }
    }
    "return 500" when {
      "service returns a Left" in {

        MockService.getMovementList(getMovementListRequest).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        val result = controller.getMovementList(exciseRegistrationNumber, searchOptions)(fakeRequest)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.obj("message" -> UnexpectedDownstreamResponseError.message)
      }
    }
  }
}
