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

package uk.gov.hmrc.emcstfe.mocks.repository

import org.scalamock.handlers.{CallHandler1, CallHandler2, CallHandler3}
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.emcstfe.models.createMovement.submissionFailures.MovementSubmissionFailure
import uk.gov.hmrc.emcstfe.models.mongo.CreateMovementUserAnswers
import uk.gov.hmrc.emcstfe.repositories.CreateMovementUserAnswersRepository

import scala.concurrent.Future

trait MockCreateMovementUserAnswersRepository extends MockFactory {
  lazy val mockCreateMovementUserAnswersRepository: CreateMovementUserAnswersRepository = mock[CreateMovementUserAnswersRepository]

  object MockCreateMovementUserAnswersRepository {
    def set(answers: CreateMovementUserAnswers): CallHandler1[CreateMovementUserAnswers, Future[Boolean]] =
      (mockCreateMovementUserAnswersRepository.set(_: CreateMovementUserAnswers)).expects(answers)

    def get(ern: String, draftId: String): CallHandler2[String, String, Future[Option[CreateMovementUserAnswers]]] =
      (mockCreateMovementUserAnswersRepository.get(_: String, _: String)).expects(ern, draftId)

    def clear(ern: String, draftId: String): CallHandler2[String, String, Future[Boolean]] =
      (mockCreateMovementUserAnswersRepository.clear(_: String, _: String)).expects(ern, draftId)

    def checkForExistingLrn(ern: String, lrn: String): CallHandler2[String, String, Future[Boolean]] =
      (mockCreateMovementUserAnswersRepository.checkForExistingLrn(_: String, _: String)).expects(ern, lrn)

    def markDraftAsUnsubmitted(ern: String, draftId: String): CallHandler2[String, String, Future[Boolean]] =
      (mockCreateMovementUserAnswersRepository.markDraftAsUnsubmitted(_: String, _: String)).expects(ern, draftId)

    def setErrorMessagesForDraftMovement(ern: String, submittedDraftId: String, movementSubmissionFailures: Seq[MovementSubmissionFailure]): CallHandler3[String, String, Seq[MovementSubmissionFailure], Future[Option[String]]] =
      (mockCreateMovementUserAnswersRepository.setErrorMessagesForDraftMovement(_: String, _: String, _: Seq[MovementSubmissionFailure])).expects(ern, submittedDraftId, movementSubmissionFailures)

    def setSubmittedDraftId(ern: String, draftId: String, submittedDraftId: String): CallHandler3[String, String, String, Future[Boolean]] =
      (mockCreateMovementUserAnswersRepository.setSubmittedDraftId(_: String, _: String, _: String))
        .expects(ern, draftId, submittedDraftId)
  }
}


