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
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model._
import play.api.libs.json.Format
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.models.mongo.CreateMovementUserAnswers
import uk.gov.hmrc.emcstfe.repositories.CreateMovementUserAnswersRepository.mongoIndexes
import uk.gov.hmrc.emcstfe.utils.TimeMachine
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.Instant
import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[CreateMovementUserAnswersRepositoryImpl])
trait CreateMovementUserAnswersRepository {
  def keepAlive(ern: String, lrn: String): Future[Boolean]

  def get(ern: String, lrn: String): Future[Option[CreateMovementUserAnswers]]

  def set(answers: CreateMovementUserAnswers): Future[Boolean]

  def clear(ern: String, lrn: String): Future[Boolean]
}

@Singleton
class CreateMovementUserAnswersRepositoryImpl @Inject()(mongoComponent: MongoComponent,
                                                        appConfig: AppConfig,
                                                        time: TimeMachine
                                                       )(implicit ec: ExecutionContext)
  extends PlayMongoRepository[CreateMovementUserAnswers](
    collectionName = "create-movement-user-answers",
    mongoComponent = mongoComponent,
    domainFormat = CreateMovementUserAnswers.format,
    indexes = mongoIndexes(appConfig.createMovementUserAnswersTTL()),
    replaceIndexes = appConfig.createMovementUserAnswersReplaceIndexes()
  ) with CreateMovementUserAnswersRepository {

  implicit val instantFormat: Format[Instant] = MongoJavatimeFormats.instantFormat

  private def by(ern: String, lrn: String): Bson =
    Filters.and(
      Filters.equal("ern", ern),
      Filters.equal("lrn", lrn)
    )

  def keepAlive(ern: String, lrn: String): Future[Boolean] =
    collection
      .updateOne(
        filter = by(ern: String, lrn: String),
        update = Updates.set("lastUpdated", time.instant()),
      )
      .toFuture()
      .map(_ => true)

  def get(ern: String, lrn: String): Future[Option[CreateMovementUserAnswers]] =
    keepAlive(ern, lrn).flatMap {
      _ =>
        collection
          .find(by(ern, lrn))
          .headOption()
    }

  def set(answers: CreateMovementUserAnswers): Future[Boolean] = {

    val updatedAnswers = answers copy (lastUpdated = time.instant())

    collection
      .replaceOne(
        filter = by(updatedAnswers.ern, updatedAnswers.lrn),
        replacement = updatedAnswers,
        options = ReplaceOptions().upsert(true)
      )
      .toFuture()
      .map(_ => true)
  }

  def clear(ern: String, lrn: String): Future[Boolean] =
    collection
      .deleteOne(by(ern, lrn))
      .toFuture()
      .map(_ => true)
}

object CreateMovementUserAnswersRepository {
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
