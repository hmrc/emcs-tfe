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

final case class CreateMovementUserAnswers(ern: String,
                                           draftId: String,
                                           data: JsObject,
                                           lastUpdated: Instant)

object CreateMovementUserAnswers {

  val reads: Reads[CreateMovementUserAnswers] = (
      (__ \ "ern").read[String] and
      (__ \ "draftId").read[String] and
      (__ \ "data").read[JsObject] and
      (__ \ "lastUpdated").read(MongoJavatimeFormats.instantFormat)
    )(CreateMovementUserAnswers.apply _)

  val writes: OWrites[CreateMovementUserAnswers] = (
      (__ \ "ern").write[String] and
      (__ \ "draftId").write[String] and
      (__ \ "data").write[JsObject] and
      (__ \ "lastUpdated").write(MongoJavatimeFormats.instantFormat)
    )(unlift(CreateMovementUserAnswers.unapply))

  implicit val format: OFormat[CreateMovementUserAnswers] = OFormat(reads, writes)
}
