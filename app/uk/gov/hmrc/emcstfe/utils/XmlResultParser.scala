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

import com.lucidchart.open.xtract.{ParseFailure, ParseResult, ParseSuccess, PartialParseSuccess, XmlReader}
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{ChRISRIMValidationError, XmlParseError}
import uk.gov.hmrc.emcstfe.models.response.rimValidation.ChRISRIMValidationErrorResponse

import scala.xml.NodeSeq

object XmlResultParser extends Logging {

  def parseResult[A]: ParseResult[A] => Either[ErrorResponse, A] = {
    case ParseSuccess(model) => Right(model)
    case ParseFailure(errors) =>
      logger.warn(s"[handleParseResult] XML Response from ChRIS could not be parsed to model. Errors: \n\n - ${errors.mkString("\n - ")}")
      Left(XmlParseError(errors))
    case PartialParseSuccess(_, errors) =>
      logger.warn(s"[handleParseResult] PartialParseSuccess - XML Response from ChRIS could not be fully parsed to model. Errors: \n\n - ${errors.mkString("\n - ")}")
      Left(XmlParseError(errors))
  }

  def parseErrorResponse(xml: NodeSeq): ErrorResponse =
    XmlReader.of[ChRISRIMValidationErrorResponse].read(xml) match {
      case ParseSuccess(rimFailures) => ChRISRIMValidationError(rimFailures)
      case parseError =>
        logger.warn(s"[parseErrorResponse] XML Response from ChRIS could not be parsed to ChRISRIMValidationErrorResponse model. Errors: \n\n - ${parseError.errors.mkString("\n - ")}")
        XmlParseError(parseError.errors)
    }
}
