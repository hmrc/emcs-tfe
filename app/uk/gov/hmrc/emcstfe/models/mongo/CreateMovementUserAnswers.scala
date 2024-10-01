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
import uk.gov.hmrc.emcstfe.models.createMovement.submissionFailures.MovementSubmissionFailure
import uk.gov.hmrc.emcstfe.models.response.rimValidation.RIMValidationError
import uk.gov.hmrc.emcstfe.utils.{Sha256Util, TimeMachine, UUIDGenerator}
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.Instant

final case class CreateMovementUserAnswers(ern: String,
                                           draftId: String,
                                           data: JsObject,
                                           submissionFailures: Seq[MovementSubmissionFailure],
                                           validationErrors: Seq[RIMValidationError],
                                           lastUpdated: Instant,
                                           hasBeenSubmitted: Boolean,
                                           submittedDraftId: Option[String],
                                           createdFromTemplateId: Option[String] = None,
                                           createdFromTemplateName: Option[String] = None,
                                           templateDataHash: Option[String] = None)

object CreateMovementUserAnswers extends Sha256Util {

  val mongoReads: Reads[CreateMovementUserAnswers] = (
    (__ \ "ern").read[String] and
      (__ \ "draftId").read[String] and
      (__ \ "data").read[JsObject] and
      (__ \ "submissionFailures").readNullable[Seq[MovementSubmissionFailure]].map(_.getOrElse(Seq.empty)) and
      (__ \ "validationErrors").readNullable[Seq[RIMValidationError]].map(_.getOrElse(Seq.empty)) and
      (__ \ "lastUpdated").read(MongoJavatimeFormats.instantFormat) and
      (__ \ "hasBeenSubmitted").read[Boolean] and
      (__ \ "submittedDraftId").readNullable[String] and
      (__ \ "createdFromTemplateId").readNullable[String] and
      (__ \ "createdFromTemplateName").readNullable[String] and
      (__ \ "templateDataHash").readNullable[String]
    )(CreateMovementUserAnswers.apply _)

  val mongoWrites: OWrites[CreateMovementUserAnswers] = (
    (__ \ "ern").write[String] and
      (__ \ "draftId").write[String] and
      (__ \ "data").write[JsObject] and
      (__ \ "submissionFailures").write[Seq[MovementSubmissionFailure]] and
      (__ \ "validationErrors").write[Seq[RIMValidationError]] and
      (__ \ "lastUpdated").write(MongoJavatimeFormats.instantFormat) and
      (__ \ "hasBeenSubmitted").write[Boolean] and
      (__ \ "submittedDraftId").writeNullable[String] and
      (__ \ "createdFromTemplateId").writeNullable[String] and
      (__ \ "createdFromTemplateName").writeNullable[String] and
      (__ \ "templateDataHash").writeNullable[String]
    )(unlift(CreateMovementUserAnswers.unapply))

  implicit val mongoFormat: OFormat[CreateMovementUserAnswers] = OFormat(mongoReads, mongoWrites)

  val responseWrites: OWrites[CreateMovementUserAnswers] = Json.writes[CreateMovementUserAnswers]

  def applyFromTemplate(template: MovementTemplate)
                       (implicit uuidGenerator: UUIDGenerator, timeMachine: TimeMachine): CreateMovementUserAnswers =
    CreateMovementUserAnswers(
      ern = template.ern,
      draftId = uuidGenerator.randomUUID,
      data = template.data,
      submissionFailures = Seq(),
      validationErrors = Seq(),
      lastUpdated = timeMachine.instant(),
      hasBeenSubmitted = false,
      submittedDraftId = None,
      createdFromTemplateId = Some(template.templateId),
      createdFromTemplateName = Some(template.templateName),
      templateDataHash = Some(sha256Hash(template.data.toString()))
    )

}
