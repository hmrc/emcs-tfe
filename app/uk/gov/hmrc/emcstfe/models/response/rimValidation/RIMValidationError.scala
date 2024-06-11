/*
 * Copyright 2024 HM Revenue & Customs
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

package uk.gov.hmrc.emcstfe.models.response.rimValidation

import cats.implicits.catsSyntaxTuple4Semigroupal
import com.lucidchart.open.xtract.{XmlReader, __}
import play.api.libs.json.{Format, Json}

case class RIMValidationError(
                               errorCategory: Option[String],
                               errorType: Option[Int],
                               errorReason: Option[String],
                               errorLocation: Option[String]
                             )

object RIMValidationError {

  implicit val format: Format[RIMValidationError] = Json.format[RIMValidationError]

  private lazy val errorTypeXmlField = __ \ "Number"

  private lazy val errorCategoryXmlField = __ \ "Type"

  private lazy val errorReasonXmlField = __ \ "Text"

  private lazy val errorLocationXmlField = __ \ "Location"

  val xmlReader: XmlReader[RIMValidationError] = (
    errorCategoryXmlField.read[String].optional,
    errorTypeXmlField.read[Int],
    errorReasonXmlField.read[String].optional,
    errorLocationXmlField.read[String].optional
  ).mapN {
    case (category, errorType, reason, location) => RIMValidationError(category, Some(errorType), reason, location)
  }
}