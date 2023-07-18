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

import com.codahale.metrics.Timer
import org.scalamock.handlers.CallHandler1
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.emcstfe.services.MetricsService

import scala.concurrent.{ExecutionContext, Future}

trait MockMetricsService extends MockFactory  {

  lazy val mockMetricsService: MetricsService = mock[MetricsService]
  lazy val mockTimer: Timer = new Timer()

  object MockMetricsService {
    def processWithTimer[T]() =
      (mockMetricsService.processWithTimer(_: Timer.Context)(_: Future[T])(_: ExecutionContext))
        .expects(*, *, *)
        .onCall { handler =>
          val f = handler.productElement(1).asInstanceOf[() => Future[T]]
          f()
        }

    def chrisTimer(metricName: String): CallHandler1[String, Timer] =
      (mockMetricsService.chrisTimer(_: String)).expects(metricName).returns(mockTimer)
  }
}


