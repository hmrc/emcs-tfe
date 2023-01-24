/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.mocks.connectors

import org.scalamock.handlers.{CallHandler2, CallHandler3}
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.emcstfe.connectors.ChrisConnector
import uk.gov.hmrc.emcstfe.models.request.ChrisRequest
import uk.gov.hmrc.emcstfe.models.response.{ErrorResponse, HelloWorldResponse}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}
import scala.xml.NodeSeq

trait MockChrisConnector extends MockFactory  {
  lazy val mockConnector: ChrisConnector = mock[ChrisConnector]

  object MockConnector {
    def hello(): CallHandler2[HeaderCarrier, ExecutionContext, Future[Either[ErrorResponse, HelloWorldResponse]]] = {
      (mockConnector.hello()(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *)
    }
    def postChrisSOAPRequest(chrisRequest: ChrisRequest): CallHandler3[ChrisRequest, HeaderCarrier, ExecutionContext, Future[Either[ErrorResponse, NodeSeq]]] = {
      (mockConnector.postChrisSOAPRequest(_: ChrisRequest)(_: HeaderCarrier, _: ExecutionContext))
        .expects(chrisRequest, *, *)
    }
  }
}


