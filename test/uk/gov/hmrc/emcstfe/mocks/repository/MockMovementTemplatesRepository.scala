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

import org.scalamock.handlers.{CallHandler1, CallHandler2, CallHandler3}
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.emcstfe.models.mongo.{MovementTemplate, MovementTemplates}
import uk.gov.hmrc.emcstfe.repositories.MovementTemplatesRepository

import scala.concurrent.Future

trait MockMovementTemplatesRepository extends MockFactory {

  lazy val mockMovementTemplatesRepository: MovementTemplatesRepository = mock[MovementTemplatesRepository]

  object MockMovementTemplatesRepository {

    def getList(ern: String): CallHandler3[String, Option[Int], Option[Int], Future[MovementTemplates]] =
      (mockMovementTemplatesRepository.getList(_: String, _: Option[Int], _: Option[Int])).expects(ern, *, *)

    def get(ern: String, templateId: String): CallHandler2[String, String, Future[Option[MovementTemplate]]] =
      (mockMovementTemplatesRepository.get(_: String, _: String)).expects(ern, templateId)

    def set(answers: MovementTemplate): CallHandler1[MovementTemplate, Future[Boolean]] =
      (mockMovementTemplatesRepository.set(_: MovementTemplate)).expects(answers)

    def delete(ern: String, templateId: String): CallHandler2[String, String, Future[Boolean]] =
      (mockMovementTemplatesRepository.delete(_: String, _: String)).expects(ern, templateId)

    def checkIfTemplateNameAlreadyExists(ern: String, templateName: String): CallHandler2[String, String, Future[Boolean]] =
      (mockMovementTemplatesRepository.checkIfTemplateNameAlreadyExists(_: String, _: String)).expects(ern, templateName)
  }
}


