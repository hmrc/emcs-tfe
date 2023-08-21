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

package uk.gov.hmrc.emcstfe.config


import uk.gov.hmrc.emcstfe.repositories._
import uk.gov.hmrc.emcstfe.utils.Logging

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class Startup @Inject()(appStartupJobs: AppStartupJobs,
                        appConfig: AppConfig
                       )(implicit val ec: ExecutionContext) extends Logging {
  if (appConfig.clearDownDuplicatesOnStartup) {
    logger.info("clearDownDuplicatesOnStartup set to true - removing duplicate records from mongo")
    appStartupJobs.clearDownDuplicatesOnStartup()
  } else {
    logger.info("clearDownDuplicatesOnStartup set to false")
  }
}

class AppStartupJobsImpl @Inject()(
                                    val alertRejectionUserAnswersRepository: AlertRejectionUserAnswersRepository,
                                    val cancelMovementUserAnswersRepository: CancelMovementUserAnswersRepository,
                                    val changeDestinationUserAnswersRepository: ChangeDestinationUserAnswersRepository,
                                    val explainDelayUserAnswersRepository: ExplainDelayUserAnswersRepository,
                                    val explainShortageOrExcessUserAnswersRepository: ExplainShortageOrExcessUserAnswersRepository,
                                    val reportReceiptUserAnswersRepository: ReportReceiptUserAnswersRepository
                                  )(implicit val ec: ExecutionContext) extends AppStartupJobs

trait AppStartupJobs extends Logging {

  implicit def ec: ExecutionContext

  val alertRejectionUserAnswersRepository: AlertRejectionUserAnswersRepository
  val cancelMovementUserAnswersRepository: CancelMovementUserAnswersRepository
  val changeDestinationUserAnswersRepository: ChangeDestinationUserAnswersRepository
  val explainDelayUserAnswersRepository: ExplainDelayUserAnswersRepository
  val explainShortageOrExcessUserAnswersRepository: ExplainShortageOrExcessUserAnswersRepository
  val reportReceiptUserAnswersRepository: ReportReceiptUserAnswersRepository

  def removeDuplicatesFrom(repository: BaseUserAnswersRepository): Future[Boolean] = {
    logger.info(s"Removing duplicates from collection [${repository.collectionName}]")
    repository.removeAllButLatestForEachErnAndArc()
  }

  def clearDownDuplicatesOnStartup(): Unit = {
    Future.sequence(Seq(
      removeDuplicatesFrom(alertRejectionUserAnswersRepository),
      removeDuplicatesFrom(cancelMovementUserAnswersRepository),
      removeDuplicatesFrom(changeDestinationUserAnswersRepository),
      removeDuplicatesFrom(explainDelayUserAnswersRepository),
      removeDuplicatesFrom(explainShortageOrExcessUserAnswersRepository),
      removeDuplicatesFrom(reportReceiptUserAnswersRepository)
    ))
  }
}
