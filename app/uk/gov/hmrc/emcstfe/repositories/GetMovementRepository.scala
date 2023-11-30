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
import uk.gov.hmrc.emcstfe.models.mongo.GetMovementMongoResponse
import uk.gov.hmrc.emcstfe.repositories.GetMovementRepository.mongoIndexes
import uk.gov.hmrc.emcstfe.utils.{Logging, TimeMachine}
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats
import uk.gov.hmrc.play.http.logging.Mdc

import java.time.Instant
import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[GetMovementRepositoryImpl])
trait GetMovementRepository {
  def get(arc: String): Future[Option[GetMovementMongoResponse]]

  def set(answers: GetMovementMongoResponse): Future[GetMovementMongoResponse]
}

@Singleton
class GetMovementRepositoryImpl @Inject()(mongoComponent: MongoComponent,
                                          appConfig: AppConfig,
                                          time: TimeMachine
                                         )(implicit ec: ExecutionContext)
  extends PlayMongoRepository[GetMovementMongoResponse](
    collectionName = "get-movement-response",
    mongoComponent = mongoComponent,
    domainFormat = GetMovementMongoResponse.format,
    indexes = mongoIndexes(appConfig.getMovementTTL()),
    replaceIndexes = appConfig.getMovementReplaceIndexes()
  ) with GetMovementRepository with Logging {

  implicit val instantFormat: Format[Instant] = MongoJavatimeFormats.instantFormat

  private def by(arc: String): Bson =
    Filters.equal("arc", arc)

  def get(arc: String): Future[Option[GetMovementMongoResponse]] =
    Mdc.preservingMdc(
      collection
        .findOneAndUpdate(
          filter = by(arc),
          update = Updates.set("lastUpdated", time.instant()),
          options = FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        )
        .headOption()
    )

  def set(answers: GetMovementMongoResponse): Future[GetMovementMongoResponse] =
    Mdc.preservingMdc(
      collection
        .findOneAndReplace(
          filter = by(answers.arc),
          replacement = answers copy (lastUpdated = time.instant()),
          options = FindOneAndReplaceOptions().upsert(true).returnDocument(ReturnDocument.AFTER)
        )
        .toFuture()
    )

}

object GetMovementRepository {
  def mongoIndexes(ttl: Duration): Seq[IndexModel] =
    Seq(
      IndexModel(
        Indexes.ascending("lastUpdated"),
        IndexOptions()
          .name("lastUpdatedIdx")
          .expireAfter(ttl.toSeconds, TimeUnit.SECONDS)
      ),
      IndexModel(
        Indexes.compoundIndex(
          Indexes.ascending("arc")
        ),
        IndexOptions().name("uniqueIdx")
      )
    )
}