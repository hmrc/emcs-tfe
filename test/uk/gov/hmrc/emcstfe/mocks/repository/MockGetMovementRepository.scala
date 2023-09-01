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

import org.scalamock.handlers.CallHandler1
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.emcstfe.models.mongo.GetMovementMongoResponse
import uk.gov.hmrc.emcstfe.repositories.GetMovementRepository

import scala.concurrent.Future

trait MockGetMovementRepository extends MockFactory {

  lazy val mockRepo: GetMovementRepository = mock[GetMovementRepository]

  object MockGetMovementRepository {
    def set(): CallHandler1[GetMovementMongoResponse, Future[GetMovementMongoResponse]] =
      (mockRepo.set(_: GetMovementMongoResponse)).expects(*)

    def get(arc: String): CallHandler1[String, Future[Option[GetMovementMongoResponse]]] =
      (mockRepo.get(_: String)).expects(arc)
  }
}


