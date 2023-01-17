/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.services

import uk.gov.hmrc.emcstfe.fixtures.{GetMovementFixture, GetMovementListFixture}
import uk.gov.hmrc.emcstfe.mocks.connectors.MockChrisConnector
import uk.gov.hmrc.emcstfe.models.request.{GetMovementListRequest, GetMovementRequest}
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{SoapExtractionError, XmlValidationError}
import uk.gov.hmrc.emcstfe.support.UnitSpec

import scala.concurrent.Future
import scala.xml.XML

class GetMovementServiceSpec extends UnitSpec with GetMovementFixture with GetMovementListFixture {
  trait Test extends MockChrisConnector {
    val getMovementRequest: GetMovementRequest = GetMovementRequest(exciseRegistrationNumber = "My ERN", arc = "My ARC")
    val getMovementListRequest: GetMovementListRequest = GetMovementListRequest(exciseRegistrationNumber = "My ERN")
    val service: GetMovementService = new GetMovementService(mockConnector)
  }

  "getMovement" should {
    "return a Right" when {
      "connector call is successful and XML is the correct format" in new Test {
        MockConnector
          .postChrisSOAPRequest(getMovementRequest)
          .returns(Future.successful(Right(XML.loadString(getMovementSoapWrapper))))

        await(service.getMovement(getMovementRequest)) shouldBe Right(getMovementResponse)
      }
    }
    "return a Left" when {
      "connector call is unsuccessful" in new Test {
        MockConnector
          .postChrisSOAPRequest(getMovementRequest)
          .returns(Future.successful(Left(XmlValidationError)))

        await(service.getMovement(getMovementRequest)) shouldBe Left(XmlValidationError)
      }
      "connector call response cannot be extracted" in new Test {
        MockConnector
          .postChrisSOAPRequest(getMovementRequest)
          .returns(Future.successful(Right(<Message>Success!</Message>)))

        await(service.getMovement(getMovementRequest)) shouldBe Left(SoapExtractionError)
      }
    }
  }

  "getMovementList" should {
    "return a Right" when {
      "connector call is successful and XML is the correct format" in new Test {
        MockConnector
          .postChrisSOAPRequest(getMovementListRequest)
          .returns(Future.successful(Right(XML.loadString(getMovementListSoapWrapper))))

        await(service.getMovementList(getMovementListRequest)) shouldBe Right(getMovementListModel)
      }
    }
    "return a Left" when {
      "connector call is unsuccessful" in new Test {
        MockConnector
          .postChrisSOAPRequest(getMovementListRequest)
          .returns(Future.successful(Left(XmlValidationError)))

        await(service.getMovementList(getMovementListRequest)) shouldBe Left(XmlValidationError)
      }
      "connector call response cannot be extracted" in new Test {
        MockConnector
          .postChrisSOAPRequest(getMovementListRequest)
          .returns(Future.successful(Right(<Message>Success!</Message>)))

        await(service.getMovementList(getMovementListRequest)) shouldBe Left(SoapExtractionError)
      }
    }
  }
}
