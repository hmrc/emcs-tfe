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

package uk.gov.hmrc.emcstfe.models.response.rimValidation

import play.api.libs.json.{Json, Reads, Writes}
import uk.gov.hmrc.emcstfe.utils.JsonUtils

case class RIMValidationError(
                               errorCategory: Option[String],
                               errorType: Option[Int],
                               errorReason: Option[String],
                               errorLocation: Option[String]
                             )

object RIMValidationError extends JsonUtils {

  implicit val jsonReads: Reads[RIMValidationError] = Json.reads[RIMValidationError]

  implicit val jsonWrites: Writes[RIMValidationError] = (error: RIMValidationError) => {
    //Multiple errors exist under code 12 and 13, so if the error code is 12 or 13 then return the message
    //with 'amend entry and resubmit' removed, otherwise return None
    val transformedErrorReason = error.errorType match {
      case Some(12) | Some(13) => error.errorReason
      case _ => None
    }
    Json.obj(
      "errorCategory" -> error.errorCategory,
      "errorType" -> error.errorType,
      "errorReason" -> transformedErrorReason,
      "errorLocation" -> error.errorLocation
    ).removeNullValues()
  }
}