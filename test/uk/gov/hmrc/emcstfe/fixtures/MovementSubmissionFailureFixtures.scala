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

package uk.gov.hmrc.emcstfe.fixtures

import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.emcstfe.models.createMovement.submissionFailures.MovementSubmissionFailure

trait MovementSubmissionFailureFixtures {

  val movementSubmissionFailureModel: MovementSubmissionFailure = MovementSubmissionFailure(
    errorType = "4401",
    errorReason = "Incorrect (code) value. Value of an element in a message is outside the predefined domain or not part of the applicable code list.",
    errorLocation = Some("/IE813[1]/Body[1]/SubmittedDraftOfEADESAD[1]/EadEsadDraft[1]/LocalReferenceNumber[1]"),
    originalAttributeValue = Some("lrnie8155639253"),
    hasBeenFixed = true
  )

  val movementSubmissionFailureJson: JsValue = Json.obj(
    "errorType" -> "4401",
    "errorReason" -> "Incorrect (code) value. Value of an element in a message is outside the predefined domain or not part of the applicable code list.",
    "errorLocation" -> "/IE813[1]/Body[1]/SubmittedDraftOfEADESAD[1]/EadEsadDraft[1]/LocalReferenceNumber[1]",
    "originalAttributeValue" -> "lrnie8155639253",
    "hasBeenFixed" -> true
  )

}
