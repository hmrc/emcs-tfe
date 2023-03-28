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

package uk.gov.hmrc.emcstfe.models.mongo

import play.api.libs.json.Json
import uk.gov.hmrc.emcstfe.fixtures.GetMovementFixture
import uk.gov.hmrc.emcstfe.support.UnitSpec
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.Instant
import java.time.temporal.ChronoUnit

class GetMovementMongoResponseSpec extends UnitSpec with GetMovementFixture {

  val instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

  val model = GetMovementMongoResponse(
    internalId = testInternalId,
    ern = testErn,
    arc = testArc,
    data = getMovementResponse,
    lastUpdated = instant
  )

  val json = Json.obj(
    "internalId" -> testInternalId,
    "ern" -> testErn,
    "arc" -> testArc,
    "data" -> getMovementJson,
    "lastUpdated" -> Json.toJson(instant)(MongoJavatimeFormats.instantWrites)
  )

  "GetMovementMongoResponse" should {

    "serialise to JSON as expected" in {
      Json.toJson(model) shouldBe json
    }

    "deserialise from JSON as expected" in {
      json.as[GetMovementMongoResponse] shouldBe model
    }
  }
}
