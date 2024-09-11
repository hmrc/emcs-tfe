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

package uk.gov.hmrc.emcstfe.services.templates

import uk.gov.hmrc.emcstfe.models.mongo.{CreateMovementUserAnswers, MovementTemplate, MovementTemplates}
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.TemplateDoesNotExist
import uk.gov.hmrc.emcstfe.repositories.{CreateMovementUserAnswersRepository, MovementTemplatesRepository}
import uk.gov.hmrc.emcstfe.services.recovery
import uk.gov.hmrc.emcstfe.utils.{Logging, TimeMachine, UUIDGenerator}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MovementTemplatesService @Inject()(templateRepo: MovementTemplatesRepository,
                                         draftMovementRepo: CreateMovementUserAnswersRepository
                                        )(implicit uuidGenerator: UUIDGenerator, timeMachine: TimeMachine) extends Logging {

  def getList(ern: String, page: Int, pageSize: Int)
             (implicit ec: ExecutionContext): Future[Either[ErrorResponse, MovementTemplates]] =
    templateRepo.getList(ern, page, pageSize).map(Right(_)).recover(recovery)

  def get(ern: String, templateId: String)
         (implicit ec: ExecutionContext): Future[Either[ErrorResponse, Option[MovementTemplate]]] =
    templateRepo.get(ern, templateId).map(Right(_)).recover(recovery)

  def set(answers: MovementTemplate)
         (implicit ec: ExecutionContext): Future[Either[ErrorResponse, MovementTemplate]] =
    templateRepo.set(answers).map(_ => Right(answers)).recover(recovery)

  def delete(ern: String, templateId: String)
            (implicit ec: ExecutionContext): Future[Either[ErrorResponse, Boolean]] =
    templateRepo.delete(ern, templateId).map(Right(_)).recover(recovery)

  def checkIfTemplateNameAlreadyExists(ern: String, templateName: String)
                                      (implicit ec: ExecutionContext): Future[Either[ErrorResponse, Boolean]] =
    templateRepo.checkIfTemplateNameAlreadyExists(ern, templateName).map(Right(_)).recover(recovery)

  def createDraftMovementFromTemplate(ern: String, templateId: String)
                                     (implicit ec: ExecutionContext): Future[Either[ErrorResponse, String]] =
    templateRepo.get(ern, templateId).flatMap {
      case Some(template) =>
        val userAnswers = CreateMovementUserAnswers.applyFromTemplate(template)
        draftMovementRepo.set(userAnswers).map(_ => Right(userAnswers.draftId))
      case None =>
        Future.successful(Left(TemplateDoesNotExist(templateId)))
    }.recover(recovery)
}
