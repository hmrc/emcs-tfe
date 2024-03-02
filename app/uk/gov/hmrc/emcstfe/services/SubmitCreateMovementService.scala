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
import uk.gov.hmrc.emcstfe.featureswitch.core.config.FeatureSwitching
import uk.gov.hmrc.emcstfe.models.request.SubmitCreateMovementRequest
import uk.gov.hmrc.emcstfe.models.response.{ChRISSuccessResponse, EISSubmissionSuccessResponse, ErrorResponse}
import uk.gov.hmrc.emcstfe.repositories.CreateMovementUserAnswersRepository
import uk.gov.hmrc.emcstfe.utils.Logging
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmitCreateMovementService @Inject()(chrisConnector: ChrisConnector,
                                            eisConnector: EisConnector,
                                            createMovementUserAnswersRepository: CreateMovementUserAnswersRepository,
                                            val config: AppConfig) extends Logging with FeatureSwitching {
  def submit(requestModel: SubmitCreateMovementRequest)
            (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, ChRISSuccessResponse]] =
    chrisConnector.submitCreateMovementChrisSOAPRequest[ChRISSuccessResponse](requestModel)

  def submitViaEIS(requestModel: SubmitCreateMovementRequest)
                  (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, EISSubmissionSuccessResponse]] =
    eisConnector.submit[EISSubmissionSuccessResponse](requestModel, "submitCreateMovementEISRequest")

  def setSubmittedDraftId(ern: String, draftId: String, submittedDraftId: String): Future[Boolean] =
    createMovementUserAnswersRepository.setSubmittedDraftId(ern, draftId, submittedDraftId)


}
