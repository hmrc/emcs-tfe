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

final case class MovementTemplate(ern: String,
                                  templateId: String,
                                  data: JsObject,
                                  lastUpdated: Instant)

object MovementTemplate {

  val mongoReads: Reads[MovementTemplate] = (
    (__ \ "ern").read[String] and
      (__ \ "templateId").read[String] and
      (__ \ "data").read[JsObject] and
      (__ \ "lastUpdated").read(MongoJavatimeFormats.instantFormat)
    )(MovementTemplate.apply _)

  val mongoWrites: OWrites[MovementTemplate] = (
    (__ \ "ern").write[String] and
      (__ \ "templateId").write[String] and
      (__ \ "data").write[JsObject] and
      (__ \ "lastUpdated").write(MongoJavatimeFormats.instantFormat)
    )(unlift(MovementTemplate.unapply))

  implicit val mongoFormat: OFormat[MovementTemplate] = OFormat(mongoReads, mongoWrites)

  val responseWrites: OWrites[MovementTemplate] = Json.writes[MovementTemplate]
}
