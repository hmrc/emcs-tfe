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
import uk.gov.hmrc.emcstfe.models.mongo.CreateMovementUserAnswers
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.services.userAnswers.CreateMovementUserAnswersService

import scala.concurrent.{ExecutionContext, Future}

trait MockCreateMovementUserAnswersService extends MockFactory  {

  lazy val mockService: CreateMovementUserAnswersService = mock[CreateMovementUserAnswersService]

  object MockUserAnswers {
    def set(answers: CreateMovementUserAnswers): CallHandler2[CreateMovementUserAnswers, ExecutionContext, Future[Either[ErrorResponse, CreateMovementUserAnswers]]] =
      (mockService.set(_: CreateMovementUserAnswers)(_: ExecutionContext)).expects(answers, *)

    def get(ern: String, lrn: String): CallHandler3[String, String, ExecutionContext, Future[Either[ErrorResponse, Option[CreateMovementUserAnswers]]]] =
      (mockService.get(_: String, _: String)(_: ExecutionContext)).expects(ern, lrn, *)

    def clear(ern: String, lrn: String): CallHandler3[String, String, ExecutionContext, Future[Either[ErrorResponse, Boolean]]] =
      (mockService.clear(_: String, _: String)(_: ExecutionContext)).expects(ern, lrn, *)
  }
}


