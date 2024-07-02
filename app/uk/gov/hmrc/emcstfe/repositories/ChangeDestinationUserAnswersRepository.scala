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

package uk.gov.hmrc.emcstfe.repositories

import com.google.inject.ImplementedBy
import org.mongodb.scala.model.Updates
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.models.response.rimValidation.RIMValidationError
import uk.gov.hmrc.emcstfe.repositories.ChangeDestinationUserAnswersRepository._
import uk.gov.hmrc.emcstfe.utils.TimeMachine
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.Codecs

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[ChangeDestinationUserAnswersRepositoryImpl])
trait ChangeDestinationUserAnswersRepository {

  def setValidationErrorMessagesForDraftMovement(ern: String, draftId: String, errors: Seq[RIMValidationError]): Future[Boolean]

}

@Singleton
class ChangeDestinationUserAnswersRepositoryImpl @Inject()(implicit mongoComponent: MongoComponent,
                                                       appConfig: AppConfig,
                                                       time: TimeMachine,
                                                       ec: ExecutionContext)
  extends BaseUserAnswersRepositoryImpl(
    collectionName = "change-destination-user-answers",
    ttl = appConfig.changeDestinationUserAnswersTTL(),
    replaceIndexes = appConfig.changeDestinationUserAnswersReplaceIndexes()
  ) with ChangeDestinationUserAnswersRepository {

  def setValidationErrorMessagesForDraftMovement(ern: String, arc: String, errors: Seq[RIMValidationError]): Future[Boolean] =
    collection
      .findOneAndUpdate(
        filter = by(ern = ern, arc = arc),
        update = Updates.set(validationErrorsField, Codecs.toBson(errors))
      )
      .toFuture()
      .map(_ => true)
}

object ChangeDestinationUserAnswersRepository {
  val validationErrorsField = "validationErrors"
}
