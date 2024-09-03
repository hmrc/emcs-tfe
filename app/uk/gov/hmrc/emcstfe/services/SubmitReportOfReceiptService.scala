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

import uk.gov.hmrc.emcstfe.connectors.EisConnector
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.common.AcceptMovement
import uk.gov.hmrc.emcstfe.models.reportOfReceipt.SubmitReportOfReceiptModel
import uk.gov.hmrc.emcstfe.models.request.SubmitReportOfReceiptRequest
import uk.gov.hmrc.emcstfe.models.response.{EISSubmissionSuccessResponse, ErrorResponse}
import uk.gov.hmrc.emcstfe.utils.Logging
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmitReportOfReceiptService @Inject()(eisConnector: EisConnector,
                                             metricsService: MetricsService) extends Logging {

  def submitViaEIS(submission: SubmitReportOfReceiptModel)
            (implicit hc: HeaderCarrier, ec: ExecutionContext, request: UserRequest[_]): Future[Either[ErrorResponse, EISSubmissionSuccessResponse]] =
    eisConnector.submit[EISSubmissionSuccessResponse](SubmitReportOfReceiptRequest(submission), "submitReportOfReceiptEISRequest").map(handleResponse(_, submission))

  private def handleResponse[A](response: Either[ErrorResponse, A], submission: SubmitReportOfReceiptModel): Either[ErrorResponse, A] = response match {
    case r@Right(_) =>
      submission.acceptMovement match {
        case AcceptMovement.Satisfactory => metricsService.rorSatisfactoryCount.inc()
        case AcceptMovement.Unsatisfactory => metricsService.rorUnsatisfactoryCount.inc()
        case AcceptMovement.Refused => metricsService.rorRefused.inc()
        case AcceptMovement.PartiallyRefused => metricsService.rorPartiallyRefused.inc()
      }
      r
    case l@Left(_) =>
      metricsService.rorFailedSubmission.inc()
      l
  }
}
