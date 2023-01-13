/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.services

import uk.gov.hmrc.emcstfe.fixtures.GetMessageFixture
import uk.gov.hmrc.emcstfe.mocks.connectors.MockChrisConnector
import uk.gov.hmrc.emcstfe.models.request.GetMessageRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{SoapExtractionError, XmlValidationError}
import uk.gov.hmrc.emcstfe.support.UnitSpec

import scala.concurrent.Future
import scala.xml.XML

class GetMessageServiceSpec extends UnitSpec with GetMessageFixture {
  trait Test extends MockChrisConnector {
    val request: GetMessageRequest = GetMessageRequest(exciseRegistrationNumber = "My ERN", arc = "My ARC")
    val service: GetMessageService = new GetMessageService(mockConnector)
  }

  "getMessage" should {
    "return a Right" when {
      "connector call is successful and XML is the correct format" in new Test {
        MockConnector
          .getMessage()
          .returns(Future.successful(Right(XML.loadString(getMessageSoapWrapper))))

        await(service.getMessage(request)) shouldBe Right(model)
      }
    }
    "return a Left" when {
      "connector call is unsuccessful" in new Test {
        MockConnector
          .getMessage()
          .returns(Future.successful(Left(XmlValidationError)))

        await(service.getMessage(request)) shouldBe Left(XmlValidationError)
      }
      "connector call response cannot be extracted" in new Test {
        MockConnector
          .getMessage()
          .returns(Future.successful(Right(<Message>Success!</Message>)))

        await(service.getMessage(request)) shouldBe Left(SoapExtractionError)
      }
    }
  }
}
