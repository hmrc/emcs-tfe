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

package uk.gov.hmrc.emcstfe.mocks.repository

import org.scalamock.handlers.CallHandler3
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.emcstfe.models.response.rimValidation.RIMValidationError
import uk.gov.hmrc.emcstfe.repositories.ChangeDestinationUserAnswersRepository

import scala.concurrent.Future

trait MockChangeDestinationUserAnswersRepository extends MockFactory {
  lazy val mockChangeDestinationUserAnswersRepository: ChangeDestinationUserAnswersRepository = mock[ChangeDestinationUserAnswersRepository]

  object MockChangeDestinationUserAnswersRepository {

    def setValidationErrorMessagesForDraftMovement(ern: String, draftId: String, validationErrors: Seq[RIMValidationError]): CallHandler3[String, String, Seq[RIMValidationError], Future[Boolean]] =
      (mockChangeDestinationUserAnswersRepository.setValidationErrorMessagesForDraftMovement(_: String, _: String, _: Seq[RIMValidationError])).expects(ern, draftId, validationErrors)
  }
}


