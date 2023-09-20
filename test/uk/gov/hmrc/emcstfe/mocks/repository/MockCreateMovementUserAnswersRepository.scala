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
import uk.gov.hmrc.emcstfe.models.mongo.CreateMovementUserAnswers
import uk.gov.hmrc.emcstfe.repositories.CreateMovementUserAnswersRepository

import scala.concurrent.Future

trait MockCreateMovementUserAnswersRepository extends MockFactory {
  lazy val mockRepo: CreateMovementUserAnswersRepository = mock[CreateMovementUserAnswersRepository]

  object MockRepository {
    def set(answers: CreateMovementUserAnswers): CallHandler1[CreateMovementUserAnswers, Future[Boolean]] =
      (mockRepo.set(_: CreateMovementUserAnswers)).expects(answers)

    def get(ern: String, lrn: String): CallHandler2[String, String, Future[Option[CreateMovementUserAnswers]]] =
      (mockRepo.get(_: String, _: String)).expects(ern, lrn)

    def clear(ern: String, lrn: String): CallHandler2[String, String, Future[Boolean]] =
      (mockRepo.clear(_: String, _: String)).expects(ern, lrn)
  }
}


