/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.mocks.services

import org.scalamock.handlers.CallHandler3
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.emcstfe.models.request.GetMovementListRequest
import uk.gov.hmrc.emcstfe.models.response.{ErrorResponse, GetMovementListResponse}
import uk.gov.hmrc.emcstfe.services.GetMovementListService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockGetMovementListService extends MockFactory  {

  lazy val mockService: GetMovementListService = mock[GetMovementListService]

  object MockService {
    def getMovementList(getMovementListRequest: GetMovementListRequest): CallHandler3[GetMovementListRequest, HeaderCarrier, ExecutionContext, Future[Either[ErrorResponse, GetMovementListResponse]]] =
      (mockService.getMovementList(_: GetMovementListRequest)(_: HeaderCarrier, _: ExecutionContext))
        .expects(getMovementListRequest, *, *)
  }
}


