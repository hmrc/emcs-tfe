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

package uk.gov.hmrc.emcstfe.models.createMovement.submissionFailures

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class MovementSubmissionFailure(
                                      errorType: String,
                                      errorReason: String,
                                      errorLocation: Option[String],
                                      originalAttributeValue: Option[String],
                                      hasBeenFixed: Boolean = false
                                    )

object MovementSubmissionFailure {

  val reads: Reads[MovementSubmissionFailure] = (
    (__ \ "errorType").read[String] and
      (__ \ "errorReason").read[String] and
      (__ \ "errorLocation").readNullable[String] and
      (__ \ "originalAttributeValue").readNullable[String] and
      (__ \ "hasBeenFixed").readNullable[Boolean].map(_.getOrElse(false))
    )(MovementSubmissionFailure.apply _)

  val writes: Writes[MovementSubmissionFailure] = (
    (__ \ "errorType").write[String] and
      (__ \ "errorReason").write[String] and
      (__ \ "errorLocation").writeNullable[String] and
      (__ \ "originalAttributeValue").writeNullable[String] and
      (__ \ "hasBeenFixed").write[Boolean]
    )(unlift(MovementSubmissionFailure.unapply))

  implicit val format: Format[MovementSubmissionFailure] = Format(reads, writes)

}
