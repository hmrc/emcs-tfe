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

package uk.gov.hmrc.emcstfe.models.nrs.explainDelay

import play.api.libs.json.{Json, Writes}
import uk.gov.hmrc.emcstfe.models.common.SubmitterType
import uk.gov.hmrc.emcstfe.models.explainDelay.{DelayReasonType, DelayType, SubmitExplainDelayModel}

case class ExplainDelayNRSSubmission(arc: String,
                                     sequenceNumber: Int,
                                     submitterType: SubmitterType,
                                     delayType: DelayType,
                                     delayReasonType: DelayReasonType,
                                     additionalInformation: Option[String])

object ExplainDelayNRSSubmission {
  def apply(submission: SubmitExplainDelayModel): ExplainDelayNRSSubmission = ExplainDelayNRSSubmission(
    arc = submission.arc,
    sequenceNumber = submission.sequenceNumber,
    submitterType = submission.submitterType,
    delayType = submission.delayType,
    delayReasonType = submission.delayReasonType,
    additionalInformation = submission.additionalInformation
  )

  implicit val writes: Writes[ExplainDelayNRSSubmission] = Json.writes[ExplainDelayNRSSubmission]
}

