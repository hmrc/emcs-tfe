/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.controllers

import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.emcstfe.mocks.connectors.MockChrisConnector
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.UnexpectedDownstreamResponseError
import uk.gov.hmrc.emcstfe.models.response.HelloWorldResponse
import uk.gov.hmrc.emcstfe.support.UnitSpec

import scala.concurrent.Future

class MicroserviceHelloWorldControllerSpec extends UnitSpec with MockChrisConnector {

  private val fakeRequest = FakeRequest("GET", "/hello-world")
  private val controller = new MicroserviceHelloWorldController(Helpers.stubControllerComponents(), mockConnector)


  "GET /hello-world" should {
    "return 200" when {
      "service returns a Right" in {

        MockConnector.hello().returns(Future.successful(Right(HelloWorldResponse("Success from emcs-tfe-chris-stub"))))

        val result = controller.hello()(fakeRequest)

        status(result) shouldBe Status.OK
        contentAsJson(result) shouldBe Json.obj("message" -> "Success from emcs-tfe-chris-stub")
      }
    }
    "return 500" when {
      "service returns a Left" in {

        MockConnector.hello().returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        val result = controller.hello()(fakeRequest)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }
  }
}
