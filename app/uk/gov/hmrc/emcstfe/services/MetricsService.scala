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

import com.codahale.metrics.Timer
import com.kenshoo.play.metrics.Metrics
import uk.gov.hmrc.emcstfe.utils.Logging

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class MetricsService @Inject()(metrics: Metrics) extends Logging {

  def requestTimer(metricName: String): Timer = metrics.defaultRegistry.timer(s"$metricName.timer")

  def rorStatusCounter(status: String) = metrics.defaultRegistry.counter(s"report-receipt.status-count.$status")

  lazy val rorSatisfactoryCount = rorStatusCounter("satisfactory")
  lazy val rorUnsatisfactoryCount = rorStatusCounter("unsatisfactory")
  lazy val rorPartiallyRefused = rorStatusCounter("partially-refused")
  lazy val rorRefused = rorStatusCounter("refused")
  lazy val rorFailedSubmission = rorStatusCounter("failed-submission")

  def processWithTimer[T](timer: Timer.Context)(f: => Future[T])(implicit ec: ExecutionContext): Future[T] = {
    f map { data =>
      timer.stop()
      data
    } recover {
      case e =>
        timer.stop()
        throw e
    }
  }
}