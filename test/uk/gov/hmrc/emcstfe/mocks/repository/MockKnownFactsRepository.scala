/*
 * Copyright 2024 HM Revenue & Customs
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

import org.scalamock.handlers.{CallHandler1, CallHandler2}
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.emcstfe.models.mongo.KnownFacts
import uk.gov.hmrc.emcstfe.models.response.TraderKnownFacts
import uk.gov.hmrc.emcstfe.repositories.KnownFactsRepository

import scala.concurrent.Future

trait MockKnownFactsRepository extends MockFactory {

  lazy val mockKnownFactsRepository: KnownFactsRepository = mock[KnownFactsRepository]

  object MockKnownFactsRepository {
    def set(ern: String, traderKnownFacts: TraderKnownFacts): CallHandler2[String, TraderKnownFacts, Future[Boolean]] =
      (mockKnownFactsRepository.set(_: String, _: TraderKnownFacts)).expects(ern, traderKnownFacts)

    def get(ern: String): CallHandler1[String, Future[Option[KnownFacts]]] =
      (mockKnownFactsRepository.get(_: String)).expects(ern)
  }
}


