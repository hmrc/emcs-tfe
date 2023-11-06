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

import org.scalamock.handlers.CallHandler3
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.emcstfe.models.request.SetMessageAsLogicallyDeletedRequest
import uk.gov.hmrc.emcstfe.models.response.{ErrorResponse, SetMessageAsLogicallyDeletedResponse}
import uk.gov.hmrc.emcstfe.services.SetMessageAsLogicallyDeletedService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockSetMessageAsLogicallyDeletedService extends MockFactory {
  lazy val mockService: SetMessageAsLogicallyDeletedService = mock[SetMessageAsLogicallyDeletedService]

  object MockService {
    def setMessageAsLogicallyDeleted(setMessageAsLogicallyDeletedRequest: SetMessageAsLogicallyDeletedRequest): CallHandler3[SetMessageAsLogicallyDeletedRequest, HeaderCarrier, ExecutionContext, Future[Either[ErrorResponse, SetMessageAsLogicallyDeletedResponse]]] = {
      (mockService.setMessageAsLogicallyDeleted(_: SetMessageAsLogicallyDeletedRequest)(_: HeaderCarrier, _: ExecutionContext))
        .expects(setMessageAsLogicallyDeletedRequest, *, *)
    }
  }
}


