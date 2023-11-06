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
import com.lucidchart.open.xtract.{XPath, XmlReader, __}
import play.api.libs.json.{Json, OWrites}

case class SubmissionFailureMessageData(ie704: IE704Model, relatedMessageType: Option[String])

object SubmissionFailureMessageData {
  implicit val writes: OWrites[SubmissionFailureMessageData] = Json.writes

  private val ie704: XPath = __ \\ "SubmissionFailureMessageDataResponse" \\ "IE704"
  private val relatedMessageType: XPath = __ \\ "SubmissionFailureMessageDataResponse" \\ "RelatedMessageType"

  implicit val xmlReader: XmlReader[SubmissionFailureMessageData] = (
    ie704.read[IE704Model],
    relatedMessageType.read[String].map(s => if (s.nonEmpty) Some(s) else None)
  ).mapN(SubmissionFailureMessageData.apply)
}
