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

import uk.gov.hmrc.emcstfe.models.mongo.CreateMovementUserAnswers
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.repositories.CreateMovementUserAnswersRepository
import uk.gov.hmrc.emcstfe.utils.Logging

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CreateMovementUserAnswersService @Inject()(repo: CreateMovementUserAnswersRepository) extends Logging {

  def get(internalId: String, ern: String, lrn: String)(implicit ec: ExecutionContext): Future[Either[ErrorResponse, Option[CreateMovementUserAnswers]]] =
    repo.get(internalId, ern, lrn).map(answers => Right(answers)).recover(recovery)

  def set(answers: CreateMovementUserAnswers)(implicit ec: ExecutionContext): Future[Either[ErrorResponse, CreateMovementUserAnswers]] =
    repo.set(answers).map(_ => Right(answers)).recover(recovery)

  def clear(internalId: String, ern: String, arc: String)(implicit ec: ExecutionContext): Future[Either[ErrorResponse, Boolean]] =
    repo.clear(internalId, ern, arc).map(Right(_)).recover(recovery)
}
