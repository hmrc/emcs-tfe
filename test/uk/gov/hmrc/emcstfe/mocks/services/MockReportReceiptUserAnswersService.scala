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
import uk.gov.hmrc.emcstfe.models.mongo.ReportReceiptUserAnswers
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.services.ReportReceiptUserAnswersService

import scala.concurrent.{ExecutionContext, Future}

trait MockReportReceiptUserAnswersService extends MockFactory  {

  lazy val mockService: ReportReceiptUserAnswersService = mock[ReportReceiptUserAnswersService]

  object MockUserAnswers {
    def set(answers: ReportReceiptUserAnswers): CallHandler2[ReportReceiptUserAnswers, ExecutionContext, Future[Either[ErrorResponse, ReportReceiptUserAnswers]]] =
      (mockService.set(_: ReportReceiptUserAnswers)(_: ExecutionContext)).expects(answers, *)

    def get(ern: String, arc: String): CallHandler3[String, String, ExecutionContext, Future[Either[ErrorResponse, Option[ReportReceiptUserAnswers]]]] =
      (mockService.get(_: String, _: String)(_: ExecutionContext)).expects(ern, arc, *)

    def clear(ern: String, arc: String): CallHandler3[String, String, ExecutionContext, Future[Either[ErrorResponse, Boolean]]] =
      (mockService.clear(_: String, _: String)(_: ExecutionContext)).expects(ern, arc, *)
  }
}


