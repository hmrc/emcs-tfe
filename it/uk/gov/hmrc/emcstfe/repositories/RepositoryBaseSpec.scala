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
import org.mongodb.scala.result.InsertOneResult
import org.scalamock.scalatest.MockFactory
import org.scalatest.OptionValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import uk.gov.hmrc.emcstfe.fixtures.BaseFixtures
import uk.gov.hmrc.emcstfe.support.IntegrationBaseSpec
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.reflect.ClassTag

trait RepositoryBaseSpec[A] extends IntegrationBaseSpec
  with MockFactory
  with OptionValues
  with ScalaFutures
  with IntegrationPatience
  with BaseFixtures {

  override implicit val patienceConfig: PatienceConfig =
    PatienceConfig(timeout = 30.seconds, interval = 100.millis)

  protected def databaseName: String = "test-" + this.getClass.getSimpleName

  protected def mongoUri: String = s"mongodb://localhost:27017/$databaseName"

  protected lazy val mongoComponent: MongoComponent = MongoComponent(mongoUri)

  protected val repository: PlayMongoRepository[A]

  protected def find(filter: Bson)(implicit ev: ClassTag[A]): Future[Seq[A]] =
    repository.collection
      .find(filter)
      .toFuture()

  protected def insert(a: A): Future[InsertOneResult] =
    repository.collection
      .insertOne(a)
      .toFuture()

  override def beforeEach(): Unit =
    repository.collection.deleteMany(BsonDocument()).toFuture().futureValue
}
