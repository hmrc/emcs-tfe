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

import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.when
import org.mockito.stubbing.OngoingStubbing
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.emcstfe.models.mongo.ReportReceiptUserAnswers
import uk.gov.hmrc.emcstfe.repositories.ReportReceiptUserAnswersRepository

import scala.concurrent.Future

trait MockReportReceiptUserAnswersRepository extends MockitoSugar  {

  lazy val mockRepo: ReportReceiptUserAnswersRepository = mock[ReportReceiptUserAnswersRepository]

  object MockUserAnswers {
    def set(answers: ReportReceiptUserAnswers): OngoingStubbing[Future[Boolean]] =
      when(mockRepo.set(eqTo(answers)))

    def get(ern: String, arc: String): OngoingStubbing[Future[Option[ReportReceiptUserAnswers]]] =
      when(mockRepo.get(any(), eqTo(ern), eqTo(arc)))

    def clear(ern: String, arc: String): OngoingStubbing[Future[Boolean]] =
      when(mockRepo.clear(any(), eqTo(ern), eqTo(arc)))
  }
}


