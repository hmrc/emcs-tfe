/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.mocks.connectors

import org.scalamock.handlers.CallHandler2
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.emcstfe.connector.ChrisConnector
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
  }
}


