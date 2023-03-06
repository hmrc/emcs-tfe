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

import com.google.inject.{Inject, Singleton}
import uk.gov.hmrc.emcstfe.connectors.ChrisConnector
import uk.gov.hmrc.emcstfe.models.mongo.ReportReceiptUserAnswers
import uk.gov.hmrc.emcstfe.models.request.GetMovementRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.MongoError
import uk.gov.hmrc.emcstfe.models.response.{ErrorResponse, GetMovementResponse}
import uk.gov.hmrc.emcstfe.repositories.ReportReceiptUserAnswersRepository
import uk.gov.hmrc.emcstfe.utils.Logging
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ReportReceiptUserAnswersService @Inject()(repo: ReportReceiptUserAnswersRepository) extends Logging {

  def get(internalId: String, ern: String, arc: String)(implicit ec: ExecutionContext): Future[Either[ErrorResponse, Option[ReportReceiptUserAnswers]]] =
    repo.get(internalId: String, ern: String, arc: String).map(answers => Right(answers)).recover(recovery)

  def set(answers: ReportReceiptUserAnswers)(implicit ec: ExecutionContext): Future[Either[ErrorResponse, ReportReceiptUserAnswers]] =
    repo.set(answers).map(_ => Right(answers)).recover(recovery)

  def clear(answers: ReportReceiptUserAnswers)(implicit ec: ExecutionContext): Future[Either[ErrorResponse, Boolean]] =
    repo.clear(answers).map(Right(_)).recover(recovery)

  private def recovery[A]: PartialFunction[Throwable, Either[ErrorResponse, A]] = {
    case e: Throwable => Left(MongoError(e.getMessage))
  }
}
