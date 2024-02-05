/*
 * Copyright 2024 HM Revenue & Customs
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

import org.scalamock.handlers.CallHandler4
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.services.userAllowList.UserAllowListService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

trait MockUserAllowListService extends MockFactory {

  lazy val mockUserAllowListService: UserAllowListService = mock[UserAllowListService]

  object MockedUserAllowListService extends Matchers {
    def isEligible(ern: String, service: String): CallHandler4[String, String, HeaderCarrier, UserRequest[_], Future[Either[ErrorResponse, Boolean]]] = {
      (mockUserAllowListService.isEligible(_: String, _: String)(_: HeaderCarrier, _: UserRequest[_]))
        .expects(ern, service, *, *)
    }
  }
}
