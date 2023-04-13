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

import play.api.libs.functional.syntax._
import play.api.libs.json._
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.Instant

case class GetMovementMongoResponse(
                                internalId: String,
                                ern: String,
                                arc: String,
                                data: JsValue,
                                lastUpdated: Instant = Instant.now
                             )

object GetMovementMongoResponse {


  val reads: Reads[GetMovementMongoResponse] = (
    (__ \ "internalId").read[String] and
      (__ \ "ern").read[String] and
      (__ \ "arc").read[String] and
      (__ \ "data").read[JsValue] and
      (__ \ "lastUpdated").read(MongoJavatimeFormats.instantFormat)
    )(GetMovementMongoResponse.apply _)

  val writes: OWrites[GetMovementMongoResponse] = (
    (__ \ "internalId").write[String] and
      (__ \ "ern").write[String] and
      (__ \ "arc").write[String] and
      (__ \ "data").write[JsValue] and
      (__ \ "lastUpdated").write(MongoJavatimeFormats.instantFormat)
    )(unlift(GetMovementMongoResponse.unapply))

  implicit val format: OFormat[GetMovementMongoResponse] = OFormat(reads, writes)
}
