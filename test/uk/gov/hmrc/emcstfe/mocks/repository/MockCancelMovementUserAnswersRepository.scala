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

import org.mockito.ArgumentMatchers.{eq => eqTo}
import org.mockito.Mockito.when
import org.mockito.stubbing.OngoingStubbing
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.emcstfe.models.mongo.CancelMovementUserAnswers
import uk.gov.hmrc.emcstfe.repositories.CancelMovementUserAnswersRepository

import scala.concurrent.Future

trait MockCancelMovementUserAnswersRepository extends MockitoSugar  {

  lazy val mockRepo: CancelMovementUserAnswersRepository = mock[CancelMovementUserAnswersRepository]

  object MockUserAnswers {
    def set(answers: CancelMovementUserAnswers): OngoingStubbing[Future[Boolean]] =
      when(mockRepo.set(eqTo(answers)))

    def get(ern: String, arc: String): OngoingStubbing[Future[Option[CancelMovementUserAnswers]]] =
      when(mockRepo.get(eqTo(ern), eqTo(arc)))

    def clear(ern: String, arc: String): OngoingStubbing[Future[Boolean]] =
      when(mockRepo.clear(eqTo(ern), eqTo(arc)))
  }
}


