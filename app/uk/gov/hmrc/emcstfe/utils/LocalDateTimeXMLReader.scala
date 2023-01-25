/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.utils

import com.lucidchart.open.xtract.{ParseError, ParseFailure, ParseSuccess, XmlReader}

import java.time.LocalDateTime
import scala.util.{Failure, Success, Try}
import scala.xml.NodeSeq

object LocalDateTimeXMLReader {

  case class LocalDateTimeParseFailure(message: String) extends ParseError

  implicit val xmlLocalDateTimeReads: XmlReader[LocalDateTime] = (xml: NodeSeq) =>
    Try(LocalDateTime.parse(xml.text)) match {
      case Success(value) => ParseSuccess(value)
      case Failure(e)     => ParseFailure(LocalDateTimeParseFailure(e.getMessage))
    }

}