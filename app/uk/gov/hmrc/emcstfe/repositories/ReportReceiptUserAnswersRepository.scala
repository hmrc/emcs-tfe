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
import org.mongodb.scala.result.DeleteResult
import play.api.libs.json.Format
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.models.mongo.ReportReceiptUserAnswers
import uk.gov.hmrc.emcstfe.utils.{Logging, TimeMachine}
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.Instant
import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ReportReceiptUserAnswersRepository @Inject()(
                                                    mongoComponent: MongoComponent,
                                                    appConfig: AppConfig,
                                                    time: TimeMachine
                                                  )(implicit ec: ExecutionContext)
  extends PlayMongoRepository[ReportReceiptUserAnswers](
    collectionName = "report-receipt-user-answers",
    mongoComponent = mongoComponent,
    domainFormat = ReportReceiptUserAnswers.format,
    indexes = Seq(
      IndexModel(
        Indexes.ascending("lastUpdated"),
        IndexOptions()
          .name("lastUpdatedIdx")
          .expireAfter(appConfig.reportReceiptUserAnswersTTL().toSeconds, TimeUnit.SECONDS)
      ),
      IndexModel(
        Indexes.compoundIndex(
          Indexes.ascending("internalId"),
          Indexes.ascending("ern"),
          Indexes.ascending("arc")
        ),
        IndexOptions().name("uniqueIdx")
      ),
      IndexModel(
        Indexes.compoundIndex(
          Indexes.ascending("ern"),
          Indexes.ascending("arc")
        ),
        IndexOptions().name("nonUniqueIdx").unique(false)
      )
    ),
    replaceIndexes = appConfig.reportReceiptUserAnswersReplaceIndexes()
  ) with Logging {

  implicit val instantFormat: Format[Instant] = MongoJavatimeFormats.instantFormat

  private def by(internalId: String, ern: String, arc: String): Bson =
    Filters.and(
      Filters.equal("internalId", internalId),
      Filters.equal("ern", ern),
      Filters.equal("arc", arc)
    )

  private def by(ern: String, arc: String): Bson =
    Filters.and(
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

  def get(internalId: String, ern: String, arc: String): Future[Option[ReportReceiptUserAnswers]] =
    keepAlive(internalId, ern, arc).flatMap {
      _ =>
        collection
          .find(by(internalId, ern, arc))
          .headOption()
    }

  def retrieveAllDocumentsInCollection(): Future[Seq[ReportReceiptUserAnswers]] = collection
    .find()
    .toFuture()

  def removeAllButLatestForEachErnAndArc(): Future[Boolean] = {
    logger.info("Removing all but latest document for each ERN and ARC")
    val futureResults: Future[Seq[Boolean]] = retrieveAllDocumentsInCollection().flatMap {
      documents =>
        val totalNumberOfDocuments = documents.length
        val latestAnswerForEachErnArcCombination: Seq[ReportReceiptUserAnswers] = documents
          .groupBy(item => (item.ern, item.arc))
          .map {
            case (_, userAnswers) => userAnswers.maxBy(_.lastUpdated)
          }.toSeq

        val totalNumberOfDocumentsToBeReinserted = latestAnswerForEachErnArcCombination.length

        val numberOfDocumentsDeleted = totalNumberOfDocuments - totalNumberOfDocumentsToBeReinserted

        val deleteAndReinsertResults: Seq[Future[Boolean]] = latestAnswerForEachErnArcCombination.map {
          userAnswers =>
            val deleteResultForUserAnswers: Future[DeleteResult] = collection
              .deleteMany(by(userAnswers.ern, userAnswers.arc))
              .toFuture()

            deleteResultForUserAnswers
              .flatMap {
                deleteResult =>
                  logger.info(s"Deleted ${deleteResult.getDeletedCount} item(s) for ERN: [${userAnswers.ern}] and ARC: [${userAnswers.arc}], adding latest back...")
                  collection.insertOne(userAnswers).toFuture().map(_ => true)
              }
        }

        logger.info(
          s"""Deleting duplicates
             |-------------------
             |Number of documents in database: $totalNumberOfDocuments
             |Number of documents to keep: $totalNumberOfDocumentsToBeReinserted
             |Number of documents to delete: $numberOfDocumentsDeleted
             |Actual number of documents kept: ${deleteAndReinsertResults.length}
             |""".stripMargin)

        Future.sequence(deleteAndReinsertResults)
    }

    futureResults.map(_ => true)
  }

  def set(answers: ReportReceiptUserAnswers): Future[Boolean] = {

    val updatedAnswers = answers copy (lastUpdated = time.instant())

    collection
      .replaceOne(
        filter = by(updatedAnswers.internalId, updatedAnswers.ern, updatedAnswers.arc),
        replacement = updatedAnswers,
        options = ReplaceOptions().upsert(true)
      )
      .toFuture()
      .map(_ => true)
  }

  def clear(internalId: String, ern: String, arc: String): Future[Boolean] =
    collection
      .deleteOne(by(internalId, ern, arc))
      .toFuture()
      .map(_ => true)
}
