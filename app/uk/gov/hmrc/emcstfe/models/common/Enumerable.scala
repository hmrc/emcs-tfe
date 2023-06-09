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

package uk.gov.hmrc.emcstfe.models.common

import com.lucidchart.open.xtract.{ParseError, ParseFailure, ParseSuccess, XmlReader}
import play.api.libs.json._

import scala.xml.NodeSeq

trait Enumerable[A] {

  def withName(str: String): Option[A]
}

object Enumerable {

  case class EnumerableXmlParseFailure(message: String) extends ParseError

  def apply[A](entries: (String, A)*): Enumerable[A] =
    (str: String) => entries.toMap.get(str)

  trait Implicits {

    implicit def xmlReads[A](implicit ev: Enumerable[A]): XmlReader[A] = (xml: NodeSeq) => {
      ev.withName(xml.text) match {
        case Some(value) => ParseSuccess(value)
        case None => ParseFailure(EnumerableXmlParseFailure(s"Invalid enumerable value of '${xml.text}'"))
      }
    }

    implicit def reads[A](implicit ev: Enumerable[A]): Reads[A] = {
      Reads {
        case JsString(str) =>
          ev.withName(str).map {
            s => JsSuccess(s)
          }.getOrElse(JsError(s"Invalid enumerable value of '$str'"))
        case _ =>
          JsError("Enumerable value was not of type JsString")
       }
    }

    implicit def writes[A : Enumerable]: Writes[A] = {
      Writes(value => JsString(value.toString))
    }
  }
}
