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
import org.mongodb.scala.bson.{BsonArray, BsonDocument}
import org.mongodb.scala.model._
import play.api.libs.json.Format
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.models.common.Descending
import uk.gov.hmrc.emcstfe.models.createMovement.submissionFailures.MovementSubmissionFailure
import uk.gov.hmrc.emcstfe.models.mongo.CreateMovementUserAnswers
import uk.gov.hmrc.emcstfe.models.request.{GetDraftMovementSearchOptions, LRN, LastUpdatedDate}
import uk.gov.hmrc.emcstfe.models.response.SearchDraftMovementsResponse
import uk.gov.hmrc.emcstfe.repositories.CreateMovementUserAnswersRepository._
import uk.gov.hmrc.emcstfe.utils.TimeMachine
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats
import uk.gov.hmrc.mongo.play.json.{Codecs, PlayMongoRepository}

import java.time.Instant
import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[CreateMovementUserAnswersRepositoryImpl])
trait CreateMovementUserAnswersRepository {
  def keepAlive(ern: String, draftId: String): Future[Boolean]

  def get(ern: String, draftId: String): Future[Option[CreateMovementUserAnswers]]

  def set(answers: CreateMovementUserAnswers): Future[Boolean]

  def clear(ern: String, draftId: String): Future[Boolean]

  def checkForExistingLrn(ern: String, lrn: String): Future[Boolean]

  def markDraftAsUnsubmitted(ern: String, draftId: String): Future[Boolean]

  def setErrorMessagesForDraftMovement(ern: String, submittedDraftId: String, errors: Seq[MovementSubmissionFailure]): Future[Option[String]]

  def setSubmittedDraftId(ern: String, draftId: String, submittedDraftId: String): Future[Boolean]

  def searchDrafts(ern: String, searchOptions: GetDraftMovementSearchOptions): Future[SearchDraftMovementsResponse]
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
    replaceIndexes = appConfig.createMovementUserAnswersReplaceIndexes(),
    extraCodecs = Seq(Codecs.playFormatCodec(SearchDraftMovementsResponse.format))
  ) with CreateMovementUserAnswersRepository {

  implicit val instantFormat: Format[Instant] = MongoJavatimeFormats.instantFormat

  private def byDraftId(ern: String, draftId: String): Bson =
    Filters.and(
      Filters.equal(ernField, ern),
      Filters.equal(draftIdField, draftId)
    )

  private def byLrn(ern: String, lrn: String): Bson =
    Filters.and(
      Filters.equal(ernField, ern),
      Filters.equal(lrnField, lrn)
    )

  private def bySubmittedDraftId(ern: String, submittedDraftId: String): Bson =
    Filters.and(
      Filters.equal(ernField, ern),
      Filters.equal(submittedDraftIdField, submittedDraftId)
    )

  def keepAlive(ern: String, draftId: String): Future[Boolean] =
    collection
      .updateOne(
        filter = Filters.or(byDraftId(ern, draftId), bySubmittedDraftId(ern, draftId)),
        update = Updates.set(lastUpdatedField, time.instant()),
      )
      .toFuture()
      .map(_ => true)

  def get(ern: String, draftId: String): Future[Option[CreateMovementUserAnswers]] =
    keepAlive(ern, draftId).flatMap {
      _ =>
        collection
          .find(Filters.or(byDraftId(ern, draftId), bySubmittedDraftId(ern, draftId)))
          .headOption()
    }

  def set(answers: CreateMovementUserAnswers): Future[Boolean] = {

    val updatedAnswers = answers copy (lastUpdated = time.instant())

    collection
      .replaceOne(
        filter = byDraftId(updatedAnswers.ern, updatedAnswers.draftId),
        replacement = updatedAnswers,
        options = ReplaceOptions().upsert(true)
      )
      .toFuture()
      .map(_ => true)
  }

  def clear(ern: String, draftId: String): Future[Boolean] =
    collection
      .deleteOne(byDraftId(ern, draftId))
      .toFuture()
      .map(_ => true)

  def checkForExistingLrn(ern: String, lrn: String): Future[Boolean] =
    collection
      .find(byLrn(ern, lrn))
      .headOption()
      .map(_.isDefined)


  def markDraftAsUnsubmitted(ern: String, draftId: String): Future[Boolean] =
    keepAlive(ern, draftId).flatMap {
      _ =>
        collection
          .updateOne(
            filter = byDraftId(ern, draftId),
            update = Updates.set(hasBeenSubmittedField, false))
          .headOption()
          .map(_.exists(_.getModifiedCount == 1L))
    }

  def setErrorMessagesForDraftMovement(ern: String, submittedDraftId: String, errors: Seq[MovementSubmissionFailure]): Future[Option[String]] =
    collection
      .findOneAndUpdate(
        filter = bySubmittedDraftId(ern, submittedDraftId),
        update = Updates.set(submissionFailuresField, Codecs.toBson(errors))
      )
      .headOption()
      .map(_.map(_.draftId))

  def setSubmittedDraftId(ern: String, draftId: String, submittedDraftId: String): Future[Boolean] =
    collection
      .updateOne(
        filter = byDraftId(ern, draftId),
        update = Updates.set(submittedDraftIdField, submittedDraftId)
      )
      .toFuture()
      .map(_ => true)

  def searchDrafts(ern: String, searchOptions: GetDraftMovementSearchOptions): Future[SearchDraftMovementsResponse] = {

    val findFilter = {
      Filters.and(
        Seq(
          //Drafts for this ERN
          Some(Filters.equal(ernField, ern)),

          //Drafts which match any search text across LRN, Consignee Name, Consignee ERN or Destination Warehouse ERN
          searchOptions.searchString.map { searchKey =>
            Filters.or(
              Seq(lrnField, consigneeNameField, consigneeErnField, destinationErnField, dispatchErnField).map(field =>
                Filters.regex(field, searchKey)
              ):_*
            )
          },

          //Filter by Destination Type(s) (used the underlying associated MovementScenario(s) to match against Drafts)
          searchOptions.destinationTypes.map(destinationTypes =>
            Filters.or(
              destinationTypes.flatMap(_.movementScenarios).map(Filters.equal(destinationTypeField, _)): _*
            )
          ),

          //Filter by Dispatch from/to
          (searchOptions.dateOfDispatchFrom, searchOptions.dateOfDispatchTo) match {
            case (Some(from), Some(to)) =>
              Some(Filters.and(
                Filters.gte(dateOfDispatchDateField, Codecs.toBson(from)),
                Filters.lte(dateOfDispatchDateField, Codecs.toBson(to))
              ))
            case (Some(from), _) =>
              Some(Filters.gte(dateOfDispatchDateField, Codecs.toBson(from)))
            case (_, Some(to)) =>
              Some(Filters.lte(dateOfDispatchDateField, Codecs.toBson(to)))
            case _ =>
              None
          },

          //Filter by EPC, where at least one of the added items in the array matches the selected EPC code
          searchOptions.exciseProductCode.map { epc =>
            Filters.equal(itemProductCode, epc)
          },

          //Filter for drafts which have submission failure body
          searchOptions.draftHasErrors.flatMap { hasErrors =>
            Option.when(hasErrors)(Filters.eq(submissionFailuresErrorFixedField, false))
          }
        ).flatten: _*
      )
    }

    val sortFilter = {
      val sortField = searchOptions.sortField match {
        case LRN => lrnField
        case LastUpdatedDate => lastUpdatedField
      }
      if (searchOptions.sortOrder == Descending) Sorts.descending(sortField) else Sorts.ascending(sortField)
    }

    val aggregatePipeline = {
      Seq(
        Aggregates.filter(findFilter),
        Aggregates.sort(sortFilter),
        Aggregates.facet(
          Facet("metadata", Aggregates.count("count")),
          Facet("paginatedDrafts",
            Aggregates.skip(searchOptions.startPosition),
            Aggregates.limit(searchOptions.maxRows)
          )
        ),
        //This step takes the count from the metadata facet array, the ifNull is required in case no documents were found, in which case return 0
        Aggregates.addFields(
          Field("count", BsonDocument(
            "$ifNull" -> BsonArray(BsonDocument("$arrayElemAt" -> BsonArray("$metadata.count", 0)), 0)
          ))
        )
      )
    }

    collection
      .aggregate[SearchDraftMovementsResponse](aggregatePipeline)
      .head()
  }
}

object CreateMovementUserAnswersRepository {

  val ernField = "ern"

  val draftIdField = "draftId"

  val submittedDraftIdField = "submittedDraftId"

  val lrnField = "data.info.localReferenceNumber"
  val consigneeNameField = "data.consignee.businessName"
  val consigneeErnField = "data.consignee.exciseRegistrationNumber"
  val destinationErnField = "data.destination.destinationWarehouseExcise"
  val dispatchErnField = "data.dispatch.dispatchWarehouseExcise"
  val destinationTypeField = "data.info.destinationType"
  val dateOfDispatchDateField = "data.info.dispatchDetails.date"
  val itemProductCode = "data.items.addedItems.itemExciseProductCode"

  val lastUpdatedField = "lastUpdated"

  val submissionFailuresField = "submissionFailures"
  val submissionFailuresErrorFixedField = s"$submissionFailuresField.hasBeenFixed"

  val hasBeenSubmittedField = "hasBeenSubmitted"

  def mongoIndexes(timeToLive: Duration): Seq[IndexModel] = Seq(
    IndexModel(
      Indexes.ascending(lastUpdatedField),
      IndexOptions()
        .name("lastUpdatedIdx")
        .expireAfter(timeToLive.toSeconds, TimeUnit.SECONDS)
    ),
    IndexModel(
      Indexes.compoundIndex(
        Indexes.ascending(ernField),
        Indexes.ascending(draftIdField)
      ),
      IndexOptions()
        .unique(true)
        .name("ernDraftIdIdx")
    ),
    IndexModel(
      Indexes.compoundIndex(
        Indexes.ascending(ernField),
        Indexes.ascending(submittedDraftIdField)
      ),
      IndexOptions()
        .sparse(true)
        .name("ernSubmittedDraftIdIdx")
    ),
    IndexModel(
      Indexes.compoundIndex(
        Indexes.ascending(ernField),
        Indexes.ascending(lrnField)
      ),
      IndexOptions().name("ernLrnIdx")
    )
  )
}
