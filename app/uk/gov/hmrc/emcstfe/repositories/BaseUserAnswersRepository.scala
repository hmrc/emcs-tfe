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
import uk.gov.hmrc.emcstfe.models.mongo.UserAnswers
import uk.gov.hmrc.emcstfe.utils.TimeMachine
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats
import uk.gov.hmrc.play.http.logging.Mdc

import java.time.Instant
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[BaseUserAnswersRepositoryImpl])
trait BaseUserAnswersRepository {
  def keepAlive(ern: String, arc: String): Future[Boolean]

  def get(ern: String, arc: String): Future[Option[UserAnswers]]

  def set(answers: UserAnswers): Future[Boolean]

  def clear(ern: String, arc: String): Future[Boolean]
}

class BaseUserAnswersRepositoryImpl(collectionName: String,
                                    ttl: Duration,
                                    replaceIndexes: Boolean)
                                   (implicit mongoComponent: MongoComponent,
                                    time: TimeMachine,
                                    ec: ExecutionContext)
  extends PlayMongoRepository[UserAnswers](
    collectionName = collectionName,
    mongoComponent = mongoComponent,
    domainFormat = UserAnswers.format,
    indexes = Seq(
      IndexModel(
        Indexes.ascending(UserAnswers.lastUpdatedKey),
        IndexOptions()
          .name("lastUpdatedIdx")
          .expireAfter(ttl.toSeconds, TimeUnit.SECONDS)
      ),
      IndexModel(
        Indexes.compoundIndex(
          Indexes.ascending(UserAnswers.ernKey),
          Indexes.ascending(UserAnswers.arcKey)
        ),
        IndexOptions().name("uniqueIdx")
      )
    ),
    replaceIndexes = replaceIndexes
  ) with BaseUserAnswersRepository {

  implicit val instantFormat: Format[Instant] = MongoJavatimeFormats.instantFormat

  private def by(ern: String, arc: String): Bson =
    Filters.and(
      Filters.equal(UserAnswers.ernKey, ern),
      Filters.equal(UserAnswers.arcKey, arc)
    )

  def keepAlive(ern: String, arc: String): Future[Boolean] =
    collection
      .updateOne(
        filter = by(ern, arc),
        update = Updates.set("lastUpdated", time.instant()),
      )
      .toFuture()
      .map(_ => true)

  def get(ern: String, arc: String): Future[Option[UserAnswers]] =
    Mdc.preservingMdc(
      collection.findOneAndUpdate(
        filter = by(ern, arc),
        update = Updates.set("lastUpdated", time.instant()),
        options = FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
      ).headOption()
    )

  def set(answers: UserAnswers): Future[Boolean] = {

    val updatedAnswers = answers copy (lastUpdated = time.instant())

    Mdc.preservingMdc(
      collection
        .replaceOne(
          filter = by(updatedAnswers.ern, updatedAnswers.arc),
          replacement = updatedAnswers,
          options = ReplaceOptions().upsert(true)
        )
        .toFuture()
        .map(_ => true)
    )
  }

  def clear(ern: String, arc: String): Future[Boolean] =
    collection
      .deleteOne(by(ern, arc))
      .toFuture()
      .map(_ => true)
}
