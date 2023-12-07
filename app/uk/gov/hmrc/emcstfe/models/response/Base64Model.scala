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

import com.lucidchart.open.xtract.XmlReader

import scala.xml.XML
import play.api.libs.json.{JsError, JsResult, Json, Reads, Writes, __}
import uk.gov.hmrc.emcstfe.utils.XmlResultParser

import java.nio.charset.StandardCharsets
import java.util.Base64
import scala.util.{Failure, Success, Try}

case class Base64Model[T](value: T)

object Base64Model {

  implicit def modelReads[T](implicit xmlReader: XmlReader[T]): Reads[Base64Model[T]] = (__.read[String].map{
    message => Try {
      val decodedMessage: String = new String(Base64.getDecoder.decode(message), StandardCharsets.UTF_8)
      XmlResultParser.handleParseResult(xmlReader.read(XML.loadString(decodedMessage))) match {
        case Left(value) => throw JsResult.Exception(JsError(value.message))
        case Right(value) => Base64Model(value)
      }
    } match {
      case Failure(exception) => throw JsResult.Exception(JsError(exception.getMessage))
      case Success(value) => value
    }
  })

  implicit def modelWrites[T](implicit valueWrites: Writes[T]): Writes[Base64Model[T]] = (o: Base64Model[T]) => Json.toJson(o.value)
}
