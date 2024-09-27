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
import org.mongodb.scala.bson.{BsonArray, BsonDocument}
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model._
import play.api.libs.json.Format
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.models.mongo.{MovementTemplate, MovementTemplates}
import uk.gov.hmrc.emcstfe.repositories.MovementTemplatesRepository._
import uk.gov.hmrc.emcstfe.utils.TimeMachine
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.{Codecs, PlayMongoRepository}
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.Instant
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[MovementTemplatesRepositoryImpl])
trait MovementTemplatesRepository {

  def get(ern: String, templateId: String): Future[Option[MovementTemplate]]

  def getList(ern: String, page: Option[Int], pageSize: Option[Int]): Future[MovementTemplates]

  def set(answers: MovementTemplate): Future[Boolean]

  def delete(ern: String, templateId: String): Future[Boolean]

  def checkIfTemplateNameAlreadyExists(ern: String, templateName: String): Future[Boolean]
}

@Singleton
class MovementTemplatesRepositoryImpl @Inject() (mongoComponent: MongoComponent, appConfig: AppConfig, time: TimeMachine)(implicit ec: ExecutionContext)
    extends PlayMongoRepository[MovementTemplate](
      collectionName = "movement-templates",
      mongoComponent = mongoComponent,
      domainFormat = MovementTemplate.mongoFormat,
      indexes = mongoIndexes(),
      replaceIndexes = appConfig.movementTemplatesIndexes(),
      extraCodecs = Seq(Codecs.playFormatCodec(MovementTemplates.format(MovementTemplate.mongoFormat)))
    )
    with MovementTemplatesRepository {

  implicit val instantFormat: Format[Instant] = MongoJavatimeFormats.instantFormat

  private def byErn(ern: String): Bson =
    Filters.equal(ernField, ern)

  private def byTemplateId(ern: String, templateId: String): Bson =
    Filters.and(
      Filters.equal(ernField, ern),
      Filters.equal(templateIdField, templateId)
    )

  private def byTemplateName(ern: String, templateName: String): Bson =
    Filters.and(
      Filters.equal(ernField, ern),
      Filters.equal(templateNameField, templateName)
    )

  def get(ern: String, templateId: String): Future[Option[MovementTemplate]] =
    collection.find(byTemplateId(ern, templateId)).headOption()

  def getList(ern: String, page: Option[Int], pageSize: Option[Int]): Future[MovementTemplates] = {

    val _page = page.getOrElse(1)
    val _pageSize = pageSize.getOrElse(Int.MaxValue)

    val filterPipeline = Aggregates.filter(byErn(ern))
    val upperPipeline  = Aggregates.addFields(Field("templateNameUpper", BsonDocument("$toUpper" -> BsonDocument("$ifNull" -> Seq("$templateName", "")))))
    val sortPipeline   = Aggregates.sort(Sorts.ascending(templateNameField + "Upper"))

    val aggregatePipeline = Seq(
      filterPipeline,
      upperPipeline,
      sortPipeline,
      Aggregates.facet(
        Facet("metadata", Aggregates.count("count")),
        Facet("templates", Aggregates.skip((_page - 1) * _pageSize), Aggregates.limit(_pageSize))
      ),
      //This step takes the count from the metadata facet array, the ifNull is required in case no documents were found, in which case return 0
      Aggregates.addFields(
        Field(
          "count",
          BsonDocument(
            "$ifNull" -> BsonArray(BsonDocument("$arrayElemAt" -> BsonArray("$metadata.count", 0)), 0)
          ))
      )
    )

    collection
      .aggregate[MovementTemplates](aggregatePipeline)
      .head()
  }

  def set(answers: MovementTemplate): Future[Boolean] = {
    collection
      .replaceOne(
        filter = byTemplateId(answers.ern, answers.templateId),
        replacement = answers copy (lastUpdated = time.instant()),
        options = ReplaceOptions().upsert(true)
      )
      .toFuture()
      .map(_ => true)
  }

  def delete(ern: String, templateId: String): Future[Boolean] =
    collection
      .deleteOne(byTemplateId(ern, templateId))
      .toFuture()
      .map(_ => true)

  def checkIfTemplateNameAlreadyExists(ern: String, templateName: String): Future[Boolean] =
    collection.find(byTemplateName(ern, templateName)).headOption().map(_.isDefined)

}

object MovementTemplatesRepository {

  val ernField          = "ern"
  val templateIdField   = "templateId"
  val templateNameField = "templateName"
  val lastUpdatedField  = "lastUpdated"

  def mongoIndexes(): Seq[IndexModel] = Seq(
    IndexModel(
      Indexes.compoundIndex(
        Indexes.ascending(ernField),
        Indexes.ascending(templateIdField)
      ),
      IndexOptions()
        .unique(true)
        .name("ernTemplateIdIdx")
    ),
    IndexModel(
      Indexes.compoundIndex(
        Indexes.ascending(ernField),
        Indexes.ascending(templateNameField)
      ),
      IndexOptions()
        .unique(true)
        .name("ernTemplateNameIdx")
    )
  )

}
