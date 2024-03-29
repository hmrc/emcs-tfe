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

package uk.gov.hmrc.emcstfe.mocks.services

import cats.data.EitherT
import org.scalamock.handlers.CallHandler3
import org.scalamock.scalatest.MockFactory
import play.api.mvc.Request
import uk.gov.hmrc.emcstfe.models.legacy.LegacyMessageAction
import uk.gov.hmrc.emcstfe.models.response.{ErrorResponse, LegacyMessage}
import uk.gov.hmrc.emcstfe.services.LegacyMessagesService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future
import scala.xml.NodeSeq

trait MockLegacyMessagesService extends MockFactory {
  lazy val mockService: LegacyMessagesService = mock[LegacyMessagesService]

  object MockService {
    def performMessageAction(action: LegacyMessageAction, request: Request[NodeSeq]): CallHandler3[LegacyMessageAction, HeaderCarrier, Request[NodeSeq], EitherT[Future, ErrorResponse, LegacyMessage]] = {
      (mockService.performMessageAction(_: LegacyMessageAction)(_: HeaderCarrier, _: Request[NodeSeq]))
        .expects(action, *, request)
    }
  }
}


