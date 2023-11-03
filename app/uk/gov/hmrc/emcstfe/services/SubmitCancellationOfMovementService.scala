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

package uk.gov.hmrc.emcstfe.services

import uk.gov.hmrc.emcstfe.connectors.{ChrisConnector, EisConnector}
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.cancellationOfMovement.SubmitCancellationOfMovementModel
import uk.gov.hmrc.emcstfe.models.request.SubmitCancellationOfMovementRequest
import uk.gov.hmrc.emcstfe.models.response.{ChRISSuccessResponse, EISSuccessResponse, ErrorResponse}
import uk.gov.hmrc.emcstfe.utils.Logging
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmitCancellationOfMovementService @Inject()(connector: ChrisConnector, eisConnector: EisConnector) extends Logging {
  def submit(submission: SubmitCancellationOfMovementModel)
            (implicit hc: HeaderCarrier,
             ec: ExecutionContext,
             request: UserRequest[_]): Future[Either[ErrorResponse, ChRISSuccessResponse]] =
    connector.submitCancellationOfMovementChrisSOAPRequest[ChRISSuccessResponse](SubmitCancellationOfMovementRequest(submission))

  def submitViaEIS(submission: SubmitCancellationOfMovementModel)
                  (implicit hc: HeaderCarrier,
                   ec: ExecutionContext,
                   request: UserRequest[_]): Future[Either[ErrorResponse, EISSuccessResponse]] =
    eisConnector.submitCancellationOfMovementEISRequest[EISSuccessResponse](SubmitCancellationOfMovementRequest(submission))

}
