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

import org.scalamock.handlers.{CallHandler2, CallHandler3, CallHandler4}
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.emcstfe.models.mongo.{MovementTemplate, MovementTemplates}
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.services.templates.MovementTemplatesService

import scala.concurrent.{ExecutionContext, Future}

trait MockMovementTemplatesService extends MockFactory  {

  lazy val mockService: MovementTemplatesService = mock[MovementTemplatesService]

  object MockMovementTemplatesService {

    def getList(ern: String): CallHandler4[String, Option[Int], Option[Int], ExecutionContext, Future[Either[ErrorResponse, MovementTemplates]]] =
      (mockService.getList(_: String, _: Option[Int], _: Option[Int])(_: ExecutionContext)).expects(ern, *, *, *)

    def get(ern: String, draftId: String): CallHandler3[String, String, ExecutionContext, Future[Either[ErrorResponse, Option[MovementTemplate]]]] =
      (mockService.get(_: String, _: String)(_: ExecutionContext)).expects(ern, draftId, *)

    def set(answers: MovementTemplate): CallHandler2[MovementTemplate, ExecutionContext, Future[Either[ErrorResponse, MovementTemplate]]] =
      (mockService.set(_: MovementTemplate)(_: ExecutionContext)).expects(answers, *)

    def delete(ern: String, draftId: String): CallHandler3[String, String, ExecutionContext, Future[Either[ErrorResponse, Boolean]]] =
      (mockService.delete(_: String, _: String)(_: ExecutionContext)).expects(ern, draftId, *)

    def checkIfTemplateNameAlreadyExists(ern: String, templateName: String): CallHandler3[String, String, ExecutionContext, Future[Either[ErrorResponse, Boolean]]] =
      (mockService.checkIfTemplateNameAlreadyExists(_: String, _: String)(_: ExecutionContext)).expects(ern, templateName, *)

    def createDraftMovementFromTemplate(ern: String, templateId: String): CallHandler3[String, String, ExecutionContext, Future[Either[ErrorResponse, String]]] =
      (mockService.createDraftMovementFromTemplate(_: String, _: String)(_: ExecutionContext)).expects(ern, templateId, *)
  }
}


