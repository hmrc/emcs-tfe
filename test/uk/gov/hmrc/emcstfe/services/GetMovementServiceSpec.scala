/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.services

import uk.gov.hmrc.emcstfe.fixtures.GetMovementFixture
import uk.gov.hmrc.emcstfe.mocks.connectors.MockChrisConnector
import uk.gov.hmrc.emcstfe.models.request.GetMovementRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{SoapExtractionError, XmlValidationError}
import uk.gov.hmrc.emcstfe.support.UnitSpec

import scala.concurrent.Future
import scala.xml.XML

class GetMovementServiceSpec extends UnitSpec with GetMovementFixture {
  trait Test extends MockChrisConnector {
    val request: GetMovementRequest = GetMovementRequest(exciseRegistrationNumber = "My ERN", arc = "My ARC")
    val service: GetMovementService = new GetMovementService(mockConnector)
  }

  "getMovement" should {
    "return a Right" when {
      "connector call is successful and XML is the correct format" in new Test {
        MockConnector
          .getMovement()
          .returns(Future.successful(Right(XML.loadString(getMovementSoapWrapper))))

        await(service.getMovement(request)) shouldBe Right(model)
      }
    }
    "return a Left" when {
      "connector call is unsuccessful" in new Test {
        MockConnector
          .getMovement()
          .returns(Future.successful(Left(XmlValidationError)))

        await(service.getMovement(request)) shouldBe Left(XmlValidationError)
      }
      "connector call response cannot be extracted" in new Test {
        MockConnector
          .getMovement()
          .returns(Future.successful(Right(<Message>Success!</Message>)))

        await(service.getMovement(request)) shouldBe Left(SoapExtractionError)
      }
    }
  }
}
