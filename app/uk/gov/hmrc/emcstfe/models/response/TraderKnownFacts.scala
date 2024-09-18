/*
 * Copyright 2024 HM Revenue & Customs
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

package uk.gov.hmrc.emcstfe.models.response

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class TraderKnownFacts(traderName: String, addressLine1: Option[String], addressLine2: Option[String], addressLine3: Option[String], addressLine4: Option[String], addressLine5: Option[String], postcode: Option[String])

object TraderKnownFacts {
  implicit val reads: Reads[TraderKnownFacts] = (
    ((__ \ "traderName").read[String] or (__ \ "businessName").read[String]) and
      (__ \ "addressLine1").readNullable[String] and
      (__ \ "addressLine2").readNullable[String] and
      (__ \ "addressLine3").readNullable[String] and
      (__ \ "addressLine4").readNullable[String] and
      (__ \ "addressLine5").readNullable[String] and
      ((__ \ "postcode").read[String].map(Some(_)) or (__ \ "postCode").readNullable[String])
    )(TraderKnownFacts.apply _)

    implicit val writes: OWrites[TraderKnownFacts] = Json.writes[TraderKnownFacts]
}
