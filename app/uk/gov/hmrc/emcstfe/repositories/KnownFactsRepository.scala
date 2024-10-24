/*
 * Copyright 2024 HM Revenue & Customs
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
import uk.gov.hmrc.emcstfe.models.mongo.KnownFacts
import uk.gov.hmrc.emcstfe.models.response.TraderKnownFacts
import uk.gov.hmrc.emcstfe.repositories.KnownFactsRepository._
import uk.gov.hmrc.emcstfe.utils.TimeMachine
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.Instant
import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[KnownFactsRepositoryImpl])
trait KnownFactsRepository {
  def get(ern: String): Future[Option[KnownFacts]]
  def set(ern: String, traderKnownFacts: TraderKnownFacts): Future[Boolean]
}

@Singleton
class KnownFactsRepositoryImpl @Inject() (mongoComponent: MongoComponent, appConfig: AppConfig, time: TimeMachine)(implicit ec: ExecutionContext)
    extends PlayMongoRepository[KnownFacts](
      collectionName = KnownFactsRepository.collectionName,
      mongoComponent = mongoComponent,
      domainFormat = KnownFacts.mongoFormat,
      indexes = mongoIndexes(appConfig.knownFactsTTL()),
      replaceIndexes = appConfig.knownFactsReplaceIndexes()
    ) with KnownFactsRepository {

  implicit val instantFormat: Format[Instant] = MongoJavatimeFormats.instantFormat

  private def byErn(ern: String): Bson =
    Filters.equal(ernField, ern)

  def get(ern: String): Future[Option[KnownFacts]] =
    collection.find(byErn(ern)).headOption()

  def set(ern: String, traderKnownFacts: TraderKnownFacts): Future[Boolean] =
    collection
      .replaceOne(
        filter = byErn(ern),
        replacement = KnownFacts(ern, traderKnownFacts, time.instant()),
        options = ReplaceOptions().upsert(true)
      )
      .toFuture()
      .map(_ => true)
}

object KnownFactsRepository {

  val collectionName    = "known-facts"
  val ernField          = "ern"
  val lastUpdatedField  = "lastUpdated"

  def mongoIndexes(ttl: Duration): Seq[IndexModel] = Seq(
    IndexModel(
      Indexes.ascending(lastUpdatedField),
      IndexOptions()
        .name(s"${lastUpdatedField}Idx")
        .expireAfter(ttl.toSeconds, TimeUnit.SECONDS)
    ),
    IndexModel(
      Indexes.ascending(ernField),
      IndexOptions()
        .unique(true)
        .name(s"${ernField}Idx")
    )
  )

}
