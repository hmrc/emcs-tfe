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

import org.scalamock.handlers.CallHandler3
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.emcstfe.models.preValidate.PreValidateTraderModel
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.models.response.prevalidate.PreValidateTraderETDSResponse
import uk.gov.hmrc.emcstfe.services.PreValidateTraderService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockPreValidateTraderService extends MockFactory {

  lazy val mockService: PreValidateTraderService = mock[PreValidateTraderService]

  object MockService extends Matchers {

    def preValidateTrader(preValidateTraderRequest: PreValidateTraderModel): CallHandler3[PreValidateTraderModel, HeaderCarrier, ExecutionContext, Future[Either[ErrorResponse, PreValidateTraderETDSResponse]]] =
        (mockService.preValidateTrader(_: PreValidateTraderModel)(_: HeaderCarrier, _: ExecutionContext)).expects(preValidateTraderRequest, *, *)

  }

}
