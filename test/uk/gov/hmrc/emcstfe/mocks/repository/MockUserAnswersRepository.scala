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

import org.scalamock.handlers.{CallHandler1, CallHandler2}
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.emcstfe.models.mongo.UserAnswers
import uk.gov.hmrc.emcstfe.repositories.BaseUserAnswersRepository

import scala.concurrent.Future

trait MockUserAnswersRepository extends MockFactory {

  lazy val mockRepo: BaseUserAnswersRepository = mock[BaseUserAnswersRepository]

  object MockRepository {
    def set(answers: UserAnswers): CallHandler1[UserAnswers, Future[Boolean]] =
      (mockRepo.set(_: UserAnswers)).expects(answers)

    def get(ern: String, arc: String): CallHandler2[String, String, Future[Option[UserAnswers]]] =
      (mockRepo.get(_: String, _: String)).expects(ern, arc)

    def clear(ern: String, arc: String): CallHandler2[String, String, Future[Boolean]] =
      (mockRepo.clear(_: String, _: String)).expects(ern, arc)
  }
}


