/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.services

import uk.gov.hmrc.emcstfe.fixtures.GetMovementListFixture
import uk.gov.hmrc.emcstfe.mocks.connectors.MockChrisConnector
import uk.gov.hmrc.emcstfe.models.request.{GetMovementListRequest, GetMovementListSearchOptions}
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{SoapExtractionError, XmlValidationError}
import uk.gov.hmrc.emcstfe.support.UnitSpec

import scala.concurrent.Future

class GetMovementListServiceSpec extends UnitSpec with GetMovementListFixture {
  trait Test extends MockChrisConnector {
    val getMovementListRequest: GetMovementListRequest = GetMovementListRequest(exciseRegistrationNumber = "My ERN", GetMovementListSearchOptions())
    val service: GetMovementListService = new GetMovementListService(mockConnector)
  }

  "getMovementList" should {
    "return a Right" when {
      "connector call is successful and XML is the correct format" in new Test {
        MockConnector
          .postChrisSOAPRequest(getMovementListRequest)
          .returns(Future.successful(Right(getMovementListResponse)))

        await(service.getMovementList(getMovementListRequest)) shouldBe Right(getMovementListResponse)
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
          .returns(Future.successful(Left(SoapExtractionError)))

        await(service.getMovementList(getMovementListRequest)) shouldBe Left(SoapExtractionError)
      }
    }
  }
}
