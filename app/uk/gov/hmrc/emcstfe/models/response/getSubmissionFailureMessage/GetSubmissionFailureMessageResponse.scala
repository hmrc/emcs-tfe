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

import com.lucidchart.open.xtract.XmlReader
import play.api.libs.json._
import play.api.libs.functional.syntax._
import uk.gov.hmrc.emcstfe.utils.XmlResultParser

import java.nio.charset.StandardCharsets
import java.util.Base64
import scala.util.{Failure, Success, Try}
import scala.xml.XML

case class GetSubmissionFailureMessageResponse(dateTime: String, exciseRegistrationNumber: String, submissionFailureMessageData: SubmissionFailureMessageData)

object GetSubmissionFailureMessageResponse {
  implicit val writes: OWrites[GetSubmissionFailureMessageResponse] = Json.writes

  implicit val reads: Reads[GetSubmissionFailureMessageResponse] = (
    (__ \ "dateTime").read[String] and
      (__ \ "exciseRegistrationNumber").read[String] and
      (__ \ "message").read[String].map {
        message =>
          Try {
            val decodedMessage: String = new String(Base64.getDecoder.decode(message), StandardCharsets.UTF_8)
            XmlResultParser.handleParseResult(XmlReader.of[SubmissionFailureMessageData].read(XML.loadString(decodedMessage))) match {
              case Left(value) => throw JsResult.Exception(JsError(value.message))
              case Right(value) => value
            }
          } match {
            case Failure(exception) => throw JsResult.Exception(JsError(exception.getMessage))
            case Success(value) => value
          }
      }
    )(GetSubmissionFailureMessageResponse.apply _)
}