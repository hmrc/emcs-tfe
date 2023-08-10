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

import org.scalamock.handlers.{CallHandler2, CallHandler4}
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.emcstfe.models.mongo.CancelMovementUserAnswers
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.services.CancelMovementUserAnswersService

import scala.concurrent.{ExecutionContext, Future}

trait MockCancelMovementUserAnswersService extends MockFactory {

  lazy val mockService: CancelMovementUserAnswersService = mock[CancelMovementUserAnswersService]

  object MockUserAnswers {
    def set(answers: CancelMovementUserAnswers): CallHandler2[CancelMovementUserAnswers, ExecutionContext, Future[Either[ErrorResponse, CancelMovementUserAnswers]]] =
      (mockService.set(_: CancelMovementUserAnswers)(_: ExecutionContext)).expects(answers, *)

    def get(ern: String, arc: String): CallHandler4[String, String, String, ExecutionContext, Future[Either[ErrorResponse, Option[CancelMovementUserAnswers]]]] =
      (mockService.get(_: String, _: String, _: String)(_: ExecutionContext)).expects(*, ern, arc, *)

    def clear(ern: String, arc: String): CallHandler4[String, String, String, ExecutionContext, Future[Either[ErrorResponse, Boolean]]] =
      (mockService.clear(_: String, _: String, _: String)(_: ExecutionContext)).expects(*, ern, arc, *)
  }
}


