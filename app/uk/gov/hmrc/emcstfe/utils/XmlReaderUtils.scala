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

package uk.gov.hmrc.emcstfe.utils

import com.lucidchart.open.xtract.{ParseError, ParseFailure, ParseSuccess, XmlReader}

import scala.util.{Failure, Success, Try}
import scala.xml.NodeSeq

trait XmlReaderUtils {

  case class BigDecimalParseFailure(message: String) extends ParseError

  implicit class SeqExtensions[A](reader: XmlReader[Seq[A]]) {
    def seqToOptionSeq: XmlReader[Option[Seq[A]]] = reader.map {
      case Nil => None
      case other => Some(other)
    }
  }

  implicit val xmlBigDecimalReads: XmlReader[BigDecimal] = (xml: NodeSeq) =>
    Try(BigDecimal.apply(xml.text)) match {
      case Success(value) => ParseSuccess(value)
      case Failure(e)     => ParseFailure(BigDecimalParseFailure(e.getMessage))
    }
}
