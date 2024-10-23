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
import uk.gov.hmrc.emcstfe.models.response.TraderKnownFacts
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.Instant

final case class KnownFacts(ern: String,
                            knownFacts: TraderKnownFacts,
                            lastUpdated: Instant)

object KnownFacts {

  private val mongoReads: Reads[KnownFacts] = (
    (__ \ "ern").read[String] and
      (__ \ "knownFacts").read[TraderKnownFacts] and
      (__ \ "lastUpdated").read(MongoJavatimeFormats.instantFormat)
    )(KnownFacts.apply _)

  private val mongoWrites: OWrites[KnownFacts] = (
    (__ \ "ern").write[String] and
      (__ \ "knownFacts").write[TraderKnownFacts] and
      (__ \ "lastUpdated").write(MongoJavatimeFormats.instantFormat)
    )(unlift(KnownFacts.unapply))

  val mongoFormat: OFormat[KnownFacts] = OFormat(mongoReads, mongoWrites)
}
