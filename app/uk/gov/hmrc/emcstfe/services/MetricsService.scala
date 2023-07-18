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

  def chrisTimer(metricName: String): Timer = metricName match {
    case "explain-delay" => explainDelayTimer
    case "report-receipt" => submitReportOfReceiptTimer
    case "create-movement" => createMovementTimer
    case "get-movement" => getMovementTimer
    case "get-movement-list" => getMovementListTimer
    case "get-movement-if-changed" => getMovementIfChangedTimer
  }
  lazy val submitReportOfReceiptTimer = metrics.defaultRegistry.timer(s"chris.report-receipt.timer")
  lazy val explainDelayTimer = metrics.defaultRegistry.timer(s"chris.explain-delay.timer")
  lazy val createMovementTimer = metrics.defaultRegistry.timer(s"chris.create-movement.timer")
  lazy val getMovementTimer = metrics.defaultRegistry.timer(s"chris.get-movement.timer")
  lazy val getMovementIfChangedTimer = metrics.defaultRegistry.timer(s"chris.get-movement-if-changed.timer")
  lazy val getMovementListTimer = metrics.defaultRegistry.timer(s"chris.get-movement-list.timer")
  lazy val userAllowListTimer: Timer = metrics.defaultRegistry.timer(s"user-allow-list.check.timer")

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