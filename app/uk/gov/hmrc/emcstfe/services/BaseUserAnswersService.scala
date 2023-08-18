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

import uk.gov.hmrc.emcstfe.models.mongo.UserAnswers
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.repositories.BaseUserAnswersRepository
import uk.gov.hmrc.emcstfe.utils.Logging

import scala.concurrent.{ExecutionContext, Future}

trait BaseUserAnswersService extends Logging {

  val repo: BaseUserAnswersRepository

  def get(ern: String, arc: String)(implicit ec: ExecutionContext): Future[Either[ErrorResponse, Option[UserAnswers]]] =
    repo.get(ern: String, arc: String).map(answers => Right(answers)).recover(recovery)

  def set(answers: UserAnswers)(implicit ec: ExecutionContext): Future[Either[ErrorResponse, UserAnswers]] =
    repo.set(answers).map(_ => Right(answers)).recover(recovery)

  def clear(ern: String, arc: String)(implicit ec: ExecutionContext): Future[Either[ErrorResponse, Boolean]] =
    repo.clear(ern: String, arc: String).map(Right(_)).recover(recovery)
}
