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

import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.connectors.{ChrisConnector, EisConnector}
import uk.gov.hmrc.emcstfe.featureswitch.core.config.{FeatureSwitching, ValidateUsingFS41Schema}
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.common.AcceptMovement
import uk.gov.hmrc.emcstfe.models.reportOfReceipt.SubmitReportOfReceiptModel
import uk.gov.hmrc.emcstfe.models.request.SubmitReportOfReceiptRequest
import uk.gov.hmrc.emcstfe.models.response.{ChRISSuccessResponse, EISSubmissionSuccessResponse, ErrorResponse}
import uk.gov.hmrc.emcstfe.utils.Logging
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmitReportOfReceiptService @Inject()(chrisConnector: ChrisConnector,
                                             eisConnector: EisConnector,
                                             val config: AppConfig,
                                             metricsService: MetricsService) extends Logging with FeatureSwitching {
  def submit(submission: SubmitReportOfReceiptModel)
            (implicit hc: HeaderCarrier, ec: ExecutionContext, request: UserRequest[_]): Future[Either[ErrorResponse, ChRISSuccessResponse]] =
    chrisConnector.submitReportOfReceiptChrisSOAPRequest[ChRISSuccessResponse](SubmitReportOfReceiptRequest(submission, isEnabled(ValidateUsingFS41Schema))).map(handleResponse(_, submission))

  def submitViaEIS(submission: SubmitReportOfReceiptModel)
            (implicit hc: HeaderCarrier, ec: ExecutionContext, request: UserRequest[_]): Future[Either[ErrorResponse, EISSubmissionSuccessResponse]] =
    eisConnector.submit[EISSubmissionSuccessResponse](SubmitReportOfReceiptRequest(submission, isEnabled(ValidateUsingFS41Schema)), "submitReportOfReceiptEISRequest").map(handleResponse(_, submission))

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
