/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.mocks.connectors

import com.lucidchart.open.xtract.XmlReader
import org.scalamock.handlers.{CallHandler2, CallHandler4}
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.emcstfe.connectors.ChrisConnector
import uk.gov.hmrc.emcstfe.models.request.ChrisRequest
import uk.gov.hmrc.emcstfe.models.response.{ErrorResponse, HelloWorldResponse}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockChrisConnector extends MockFactory  {
  lazy val mockConnector: ChrisConnector = mock[ChrisConnector]

  object MockConnector {
    def hello(): CallHandler2[HeaderCarrier, ExecutionContext, Future[Either[ErrorResponse, HelloWorldResponse]]] = {
      (mockConnector.hello()(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *)
    }
    def postChrisSOAPRequest[A](chrisRequest: ChrisRequest): CallHandler4[ChrisRequest, HeaderCarrier, ExecutionContext, XmlReader[A], Future[Either[ErrorResponse, A]]] = {
      (mockConnector.postChrisSOAPRequest[A](_: ChrisRequest)(_: HeaderCarrier, _: ExecutionContext, _: XmlReader[A]))
        .expects(chrisRequest, *, *, *)
    }
  }
}


