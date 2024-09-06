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
import uk.gov.hmrc.emcstfe.models.mongo.MovementTemplate
import uk.gov.hmrc.emcstfe.repositories.MovementTemplatesRepository._
import uk.gov.hmrc.emcstfe.utils.TimeMachine
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.Instant
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[MovementTemplatesRepositoryImpl])
trait MovementTemplatesRepository {

  def get(ern: String, templateId: String): Future[Option[MovementTemplate]]

  def getList(ern: String): Future[Seq[MovementTemplate]]

  def set(answers: MovementTemplate): Future[Boolean]

  def delete(ern: String, templateId: String): Future[Boolean]

  def checkIfTemplateNameAlreadyExists(ern: String, templateName: String): Future[Boolean]
}

@Singleton
class MovementTemplatesRepositoryImpl @Inject()(mongoComponent: MongoComponent,
                                                appConfig: AppConfig,
                                                time: TimeMachine
                                               )(implicit ec: ExecutionContext)
  extends PlayMongoRepository[MovementTemplate](
    collectionName = "movement-templates",
    mongoComponent = mongoComponent,
    domainFormat = MovementTemplate.mongoFormat,
    indexes = mongoIndexes(),
    replaceIndexes = appConfig.movementTemplatesIndexes()
  ) with MovementTemplatesRepository {

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

  def getList(ern: String): Future[Seq[MovementTemplate]] =
    collection.find(byErn(ern)).toFuture()

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

  val ernField = "ern"
  val templateIdField = "templateId"
  val templateNameField = "templateName"
  val lastUpdatedField = "lastUpdated"

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
