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

package uk.gov.hmrc.emcstfe.models.response.getSubmissionFailureMessage

import cats.implicits.catsSyntaxTuple2Semigroupal
import com.lucidchart.open.xtract.{XPath, XmlReader}
import play.api.libs.json._
import uk.gov.hmrc.emcstfe.models.response.Base64Model

case class GetSubmissionFailureMessageResponse(ie704: IE704Model, relatedMessageType: Option[String])

object GetSubmissionFailureMessageResponse {

  private val ie704: XPath = XPath \\ "SubmissionFailureMessageDataResponse" \\ "IE704"
  private val relatedMessageType: XPath = XPath \\ "SubmissionFailureMessageDataResponse" \\ "RelatedMessageType"

  //Used by the EIS reads for the Base64 model
  implicit val xmlReads: XmlReader[GetSubmissionFailureMessageResponse] = (
    ie704.read[IE704Model],
    relatedMessageType.read[String].map(s => if (s.nonEmpty) Some(s) else None)
  ).mapN(GetSubmissionFailureMessageResponse.apply)

  def jsonWrites(draftMovementExists: Boolean): OWrites[GetSubmissionFailureMessageResponse] = (response: GetSubmissionFailureMessageResponse) => {
    Json.obj(
      "ie704" -> response.ie704,
      "draftMovementExists" -> draftMovementExists
    ).deepMerge(
      response.relatedMessageType.map(
        relatedMessageType =>
          Json.obj("relatedMessageType" -> relatedMessageType
        )
      ).getOrElse(Json.obj())
    )
  }

  implicit val eisReads: Reads[GetSubmissionFailureMessageResponse] =
    (JsPath \ "message").read[Base64Model[GetSubmissionFailureMessageResponse]].map(_.value)

}
