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

package uk.gov.hmrc.emcstfe.mocks.services

import org.scalamock.handlers.{CallHandler2, CallHandler3}
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.emcstfe.models.mongo.UserAnswers
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.services.userAnswers.BaseUserAnswersService

import scala.concurrent.{ExecutionContext, Future}

trait MockUserAnswersService extends MockFactory {

  val mockService: BaseUserAnswersService = mock[BaseUserAnswersService]

  object MockUserAnswers {
    def set(answers: UserAnswers): CallHandler2[UserAnswers, ExecutionContext, Future[Either[ErrorResponse, UserAnswers]]] =
      (mockService.set(_: UserAnswers)(_: ExecutionContext)).expects(answers, *)

    def get(ern: String, arc: String): CallHandler3[String, String, ExecutionContext, Future[Either[ErrorResponse, Option[UserAnswers]]]] =
      (mockService.get( _: String, _: String)(_: ExecutionContext)).expects(ern, arc, *)

    def clear(ern: String, arc: String): CallHandler3[String, String, ExecutionContext, Future[Either[ErrorResponse, Boolean]]] =
      (mockService.clear( _: String, _: String)(_: ExecutionContext)).expects(ern, arc, *)
  }
}


