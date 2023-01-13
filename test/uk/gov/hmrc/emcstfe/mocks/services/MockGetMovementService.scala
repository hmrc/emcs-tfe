/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.mocks.services

import org.scalamock.handlers.CallHandler3
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.emcstfe.models.request.GetMovementRequest
import uk.gov.hmrc.emcstfe.models.response.{ErrorResponse, GetMovementResponse}
import uk.gov.hmrc.emcstfe.services.GetMovementService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockGetMovementService extends MockFactory  {
  lazy val mockService: GetMovementService = mock[GetMovementService]

  object MockService {
    def getMovement(): CallHandler3[GetMovementRequest, HeaderCarrier, ExecutionContext, Future[Either[ErrorResponse, GetMovementResponse]]] = {
      (mockService.getMovement(_: GetMovementRequest)(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *, *)
    }
  }
}


