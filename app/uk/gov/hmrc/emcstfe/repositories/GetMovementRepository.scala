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

import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model._
import play.api.libs.json.Format
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.models.mongo.GetMovementMongoResponse
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.utils.{Logging, TimeMachine}
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.Instant
import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetMovementRepository @Inject()(
                                                    mongoComponent: MongoComponent,
                                                    appConfig: AppConfig,
                                                    time: TimeMachine
                                                  )(implicit ec: ExecutionContext)
  extends PlayMongoRepository[GetMovementMongoResponse](
    collectionName = "get-movement-response",
    mongoComponent = mongoComponent,
    domainFormat   = GetMovementMongoResponse.format,
    indexes        = Seq(
      IndexModel(
        Indexes.ascending("lastUpdated"),
        IndexOptions()
          .name("lastUpdatedIdx")
          .expireAfter(appConfig.getMovementTTL().toSeconds, TimeUnit.SECONDS)
      ),
      IndexModel(
        Indexes.compoundIndex(
          Indexes.ascending("internalId"),
          Indexes.ascending("ern"),
          Indexes.ascending("arc")
        ),
        IndexOptions().name("uniqueIdx")
      )
    ),
    replaceIndexes = true
  ) with Logging {

  implicit val instantFormat: Format[Instant] = MongoJavatimeFormats.instantFormat

  private def by(internalId: String, ern: String, arc: String): Bson =
    Filters.and(
      Filters.equal("internalId", internalId),
      Filters.equal("ern", ern),
      Filters.equal("arc", arc)
    )

  def keepAlive(internalId: String, ern: String, arc: String): Future[Boolean] =
    collection
      .updateOne(
        filter = by(internalId: String, ern: String, arc: String),
        update = Updates.set("lastUpdated", time.instant()),
      )
      .toFuture()
      .map(_ => true)

  def get(internalId: String, ern: String, arc: String): Future[Option[GetMovementMongoResponse]] =
    keepAlive(internalId, ern, arc).flatMap {
      _ =>
        collection
          .find(by(internalId, ern, arc))
          .headOption()
    }

  def set(answers: GetMovementMongoResponse): Future[Either[ErrorResponse, Boolean]] = {

    val updatedAnswers = answers copy (lastUpdated = time.instant())

    collection
      .replaceOne(
        filter      = by(updatedAnswers.internalId, updatedAnswers.ern, updatedAnswers.arc),
        replacement = updatedAnswers,
        options     = ReplaceOptions().upsert(true)
      )
      .toFuture()
      .map(_ => Right(true))
  }

  def clear(internalId: String, ern: String, arc: String): Future[Boolean] =
    collection
      .deleteOne(by(internalId, ern, arc))
      .toFuture()
      .map(_ => true)

  def removeAll(): Future[Unit] = {
    logger.info("Clearing all movements")
    collection
      .deleteMany(BsonDocument())
      .toFuture()
      .map(_ => logger.info("Clearing all movements succeeded"))
  }
}
