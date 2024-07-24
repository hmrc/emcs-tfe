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

import java.time.{LocalDate, LocalDateTime, LocalTime}
import scala.util.{Failure, Success, Try}
import scala.xml.NodeSeq

object LocalDateTimeXMLReader {

  case class LocalDateTimeParseFailure(message: String) extends ParseError

  implicit val xmlLocalDateTimeReads: XmlReader[LocalDateTime] = (xml: NodeSeq) =>
    Try(LocalDateTime.parse(xml.text)) match {
      case Success(value) => ParseSuccess(value)
      case Failure(e)     => ParseFailure(LocalDateTimeParseFailure(e.getMessage))
    }

  implicit val xmlLocalDateReads: XmlReader[LocalDate] = (xml: NodeSeq) =>
    Try(LocalDate.parse(xml.text)) match {
      case Success(value) => ParseSuccess(value)
      case Failure(e)     => ParseFailure(LocalDateTimeParseFailure(e.getMessage))
    }

  implicit val xmlLocalTimeReads: XmlReader[LocalTime] = (xml: NodeSeq) =>
    Try(LocalTime.parse(xml.text)) match {
      case Success(value) => ParseSuccess(value)
      case Failure(e)     => ParseFailure(LocalDateTimeParseFailure(e.getMessage))
    }

}