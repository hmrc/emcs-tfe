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

import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model._
import play.api.libs.json.Format
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.models.mongo.CancelMovementUserAnswers
import uk.gov.hmrc.emcstfe.repositories.CancelMovementUserAnswersRepository.mongoIndexes
import uk.gov.hmrc.emcstfe.utils.TimeMachine
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.Instant
import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CancelMovementUserAnswersRepository @Inject()(mongoComponent: MongoComponent,
                                                    appConfig: AppConfig,
                                                    time: TimeMachine
                                                   )(implicit ec: ExecutionContext)
  extends PlayMongoRepository[CancelMovementUserAnswers](
    collectionName = "cancel-movement-user-answers",
    mongoComponent = mongoComponent,
    domainFormat = CancelMovementUserAnswers.format,
    indexes = mongoIndexes(appConfig.cancelAMovementUserAnswersTTL()),
    replaceIndexes = appConfig.cancelAMovementUserAnswersReplaceIndexes()
  ) {

  implicit val instantFormat: Format[Instant] = MongoJavatimeFormats.instantFormat

  private def by(ern: String, arc: String): Bson =
    Filters.and(
      Filters.equal("ern", ern),
      Filters.equal("arc", arc)
    )

  def keepAlive(ern: String, arc: String): Future[Boolean] =
    collection
      .updateOne(
        filter = by(ern, arc),
        update = Updates.set("lastUpdated", time.instant()),
      )
      .toFuture()
      .map(_ => true)

  def get(ern: String, arc: String): Future[Option[CancelMovementUserAnswers]] =
    keepAlive(ern, arc).flatMap {
      _ =>
        collection
          .find(by(ern, arc))
          .headOption()
    }

  def set(answers: CancelMovementUserAnswers): Future[Boolean] = {

    val updatedAnswers = answers copy (lastUpdated = time.instant())

    collection
      .replaceOne(
        filter = by(updatedAnswers.ern, updatedAnswers.arc),
        replacement = updatedAnswers,
        options = ReplaceOptions().upsert(true)
      )
      .toFuture()
      .map(_ => true)
  }

  def clear(ern: String, arc: String): Future[Boolean] =
    collection
      .deleteOne(by(ern, arc))
      .toFuture()
      .map(_ => true)
}

object CancelMovementUserAnswersRepository {
  def mongoIndexes(timeToLive: Duration): Seq[IndexModel] = Seq(
    IndexModel(
      Indexes.ascending("lastUpdated"),
      IndexOptions()
        .name("lastUpdatedIdx")
        .expireAfter(timeToLive.toSeconds, TimeUnit.SECONDS)
    ),
    IndexModel(
      Indexes.compoundIndex(
        Indexes.ascending("ern"),
        Indexes.ascending("lrn")
      ),
      IndexOptions().name("uniqueIdx")
    )
  )
}
