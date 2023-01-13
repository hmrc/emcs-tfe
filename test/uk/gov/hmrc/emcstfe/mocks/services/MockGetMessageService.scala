/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.mocks.services

import org.scalamock.handlers.CallHandler3
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.emcstfe.models.request.GetMessageRequest
import uk.gov.hmrc.emcstfe.models.response.{ErrorResponse, GetMessageResponse}
import uk.gov.hmrc.emcstfe.services.GetMessageService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockGetMessageService extends MockFactory  {
  lazy val mockService: GetMessageService = mock[GetMessageService]

  object MockService {
    def getMessage(): CallHandler3[GetMessageRequest, HeaderCarrier, ExecutionContext, Future[Either[ErrorResponse, GetMessageResponse]]] = {
      (mockService.getMessage(_: GetMessageRequest)(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *, *)
    }
  }
}


