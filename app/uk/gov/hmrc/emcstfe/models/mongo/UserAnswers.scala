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

final case class UserAnswers(ern: String,
                             arc: String,
                             data: JsObject,
                             lastUpdated: Instant)

object UserAnswers {

  val ernKey: String = "ern"
  val arcKey: String = "arc"
  val dataKey: String = "data"
  val lastUpdatedKey: String = "lastUpdated"

  val reads: Reads[UserAnswers] = (
    (__ \ ernKey).read[String] and
      (__ \ arcKey).read[String] and
      (__ \ dataKey).read[JsObject] and
      (__ \ lastUpdatedKey).read(MongoJavatimeFormats.instantFormat)
    )(UserAnswers.apply _)

  val writes: OWrites[UserAnswers] = (
    (__ \ ernKey).write[String] and
      (__ \ arcKey).write[String] and
      (__ \ dataKey).write[JsObject] and
      (__ \ lastUpdatedKey).write(MongoJavatimeFormats.instantFormat)
    )(unlift(UserAnswers.unapply))

  implicit val format: OFormat[UserAnswers] = OFormat(reads, writes)
}
