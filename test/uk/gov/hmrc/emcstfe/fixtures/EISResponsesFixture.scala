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

package uk.gov.hmrc.emcstfe.fixtures

import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.emcstfe.models.response.EISSubmissionSuccessResponse
import uk.gov.hmrc.emcstfe.models.response.rimValidation.{RIMValidationError, RIMValidationErrorResponse}

trait EISResponsesFixture {

  lazy val eisSuccessResponse: EISSubmissionSuccessResponse = EISSubmissionSuccessResponse(
    status = "OK",
    message = "Great success",
    emcsCorrelationId = "3e8dae97-b586-4cef-8511-68ac12da9028"
  )

  lazy val eisRimValidationResponse: RIMValidationErrorResponse = RIMValidationErrorResponse(
    emcsCorrelationId = "7be1db16-e8fb-4e81-97e5-3d3e2d21f6c4",
    validatorResults = Some(eisRimValidationResults)
  )

  lazy val eisRimValidationResults: Seq[RIMValidationError] = Seq(RIMValidationError(errorCategory = Some("business"), errorType = Some(12), errorReason = Some("some error"), errorLocation = Some("some location")))

  lazy val eisRimValidationResultsJson: JsValue = Json.parse(
    """
      |{
      |    "errorType": 12,
      |    "errorCategory": "business",
      |    "errorLocation": "some location",
      |    "errorReason": "some error"
      |}
      |""".stripMargin
  )

  val eisRimValidationJsonResponse: JsValue = Json.parse(
    s"""
      |{
      |    "emcsCorrelationId": "7be1db16-e8fb-4e81-97e5-3d3e2d21f6c4",
      |    "message": [
      |        "Rim Validation Failures"
      |    ],
      |    "validatorResults": [
      |        $eisRimValidationResultsJson
      |    ]
      |}""".stripMargin)

  def eisSuccessJson(withSubmittedDraftId: Boolean = false, submittedDraftId: Option[String] = None): JsValue = Json.parse(
    s"""{
      | "status": "OK",
      | "message": "Great success",
      | "emcsCorrelationId": "3e8dae97-b586-4cef-8511-68ac12da9028"
      | ${if(withSubmittedDraftId) s",\"submittedDraftId\": \"${submittedDraftId.getOrElse("1234-5678-9012")}\"" else ""}
      |}""".stripMargin)

  lazy val incompleteEisSuccessJson: JsValue = Json.parse(
    """{
      | "status": "OK",
      | "message": "Great success"
      |}""".stripMargin)

}
