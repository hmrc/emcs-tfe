/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.emcstfe.mocks.connectors

import com.lucidchart.open.xtract.XmlReader
import org.scalamock.handlers.{CallHandler3, CallHandler4}
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.emcstfe.connectors.ChrisConnector
import uk.gov.hmrc.emcstfe.models.request.chris.ChrisRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}
import scala.xml.NodeSeq

trait MockChrisConnector extends MockFactory  {
  lazy val mockChrisConnector: ChrisConnector = mock[ChrisConnector]

  object MockChrisConnector {

    def postChrisSOAPRequestAndExtractToModel[A](chrisRequest: ChrisRequest): CallHandler4[ChrisRequest, HeaderCarrier, ExecutionContext, XmlReader[A], Future[Either[ErrorResponse, A]]] = {
      (mockChrisConnector.postChrisSOAPRequestAndExtractToModel[A](_: ChrisRequest)(_: HeaderCarrier, _: ExecutionContext, _: XmlReader[A]))
        .expects(chrisRequest, *, *, *)
    }

    def postChrisSOAPRequest(chrisRequest: ChrisRequest): CallHandler3[ChrisRequest, HeaderCarrier, ExecutionContext, Future[Either[ErrorResponse, NodeSeq]]] = {
      (mockChrisConnector.postChrisSOAPRequest(_: ChrisRequest)(_: HeaderCarrier, _: ExecutionContext))
        .expects(chrisRequest, *, *)
    }

    def submitCreateMovementChrisSOAPRequest[A](chrisRequest: ChrisRequest): CallHandler4[ChrisRequest, HeaderCarrier, ExecutionContext, XmlReader[A], Future[Either[ErrorResponse, A]]] = {
      (mockChrisConnector.submitCreateMovementChrisSOAPRequest[A](_: ChrisRequest)(_: HeaderCarrier, _: ExecutionContext, _: XmlReader[A]))
        .expects(chrisRequest, *, *, *)
    }

    def submitReportOfReceiptChrisSOAPRequest[A](chrisRequest: ChrisRequest): CallHandler4[ChrisRequest, HeaderCarrier, ExecutionContext, XmlReader[A], Future[Either[ErrorResponse, A]]] = {
      (mockChrisConnector.submitReportOfReceiptChrisSOAPRequest[A](_: ChrisRequest)(_: HeaderCarrier, _: ExecutionContext, _: XmlReader[A]))
        .expects(chrisRequest, *, *, *)
    }

    def submitExplainDelayChrisSOAPRequest[A](chrisRequest: ChrisRequest): CallHandler4[ChrisRequest, HeaderCarrier, ExecutionContext, XmlReader[A], Future[Either[ErrorResponse, A]]] = {
      (mockChrisConnector.submitExplainDelayChrisSOAPRequest[A](_: ChrisRequest)(_: HeaderCarrier, _: ExecutionContext, _: XmlReader[A]))
        .expects(chrisRequest, *, *, *)
    }

    def submitChangeDestinationChrisSOAPRequest[A](chrisRequest: ChrisRequest): CallHandler4[ChrisRequest, HeaderCarrier, ExecutionContext, XmlReader[A], Future[Either[ErrorResponse, A]]] = {
      (mockChrisConnector.submitChangeDestinationChrisSOAPRequest[A](_: ChrisRequest)(_: HeaderCarrier, _: ExecutionContext, _: XmlReader[A]))
        .expects(chrisRequest, *, *, *)
    }

    def submitExplainShortageExcessChrisSOAPRequest[A](chrisRequest: ChrisRequest): CallHandler4[ChrisRequest, HeaderCarrier, ExecutionContext, XmlReader[A], Future[Either[ErrorResponse, A]]] = {
      (mockChrisConnector.submitExplainShortageExcessChrisSOAPRequest[A](_: ChrisRequest)(_: HeaderCarrier, _: ExecutionContext, _: XmlReader[A]))
        .expects(chrisRequest, *, *, *)
    }

    def submitAlertOrRejectionChrisSOAPRequest[A](chrisRequest: ChrisRequest): CallHandler4[ChrisRequest, HeaderCarrier, ExecutionContext, XmlReader[A], Future[Either[ErrorResponse, A]]] = {
      (mockChrisConnector.submitAlertOrRejectionChrisSOAPRequest[A](_: ChrisRequest)(_: HeaderCarrier, _: ExecutionContext, _: XmlReader[A]))
        .expects(chrisRequest, *, *, *)
    }
    def submitCancellationOfMovementChrisSOAPRequest[A](chrisRequest: ChrisRequest): CallHandler4[ChrisRequest, HeaderCarrier, ExecutionContext, XmlReader[A], Future[Either[ErrorResponse, A]]] = {
      (mockChrisConnector.submitCancellationOfMovementChrisSOAPRequest[A](_: ChrisRequest)(_: HeaderCarrier, _: ExecutionContext, _: XmlReader[A]))
        .expects(chrisRequest, *, *, *)
    }
  }
}


