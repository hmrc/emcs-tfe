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

package uk.gov.hmrc.emcstfe.models.response

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json._

import java.nio.charset.StandardCharsets
import java.util.Base64
import scala.util.{Failure, Success, Try}
import scala.xml.{NodeSeq, XML}

case class RawGetMovementResponse(dateTime: String, exciseRegistrationNumber: String, movementView: NodeSeq) extends LegacyMessage {
  override protected def xmlBody: NodeSeq = schemaResultBody(movementView)
}

object RawGetMovementResponse {
  implicit val reads: Reads[RawGetMovementResponse] = ((__ \ "dateTime").read[String] and
    (__ \ "exciseRegistrationNumber").read[String] and
    (__ \ "message").read[String].map {
      message =>
        Try {
          val decodedMessage: String = new String(Base64.getDecoder.decode(message), StandardCharsets.UTF_8)
          XML.loadString(decodedMessage)
        } match {
          case Failure(exception) => throw JsResult.Exception(JsError(exception.getMessage))
          case Success(value) => value
        }
    }
  )(RawGetMovementResponse.apply _)
}