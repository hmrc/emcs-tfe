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

trait EISResponsesFixture {

  lazy val eisSuccessResponse: EISSubmissionSuccessResponse = EISSubmissionSuccessResponse(
    status = "OK",
    message = "Great success",
    emcsCorrelationId = "3e8dae97-b586-4cef-8511-68ac12da9028"
  )

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
