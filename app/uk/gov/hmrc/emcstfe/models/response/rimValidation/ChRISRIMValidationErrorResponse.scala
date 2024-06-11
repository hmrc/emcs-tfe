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

import com.lucidchart.open.xtract.XmlReader.strictReadSeq
import com.lucidchart.open.xtract.{XPath, XmlReader, __}
import play.api.libs.json.{Format, Json}

case class ChRISRIMValidationErrorResponse(rimValidationErrors: Seq[RIMValidationError])

object ChRISRIMValidationErrorResponse {

  implicit val format: Format[ChRISRIMValidationErrorResponse] = Json.format[ChRISRIMValidationErrorResponse]

  val errorResponseContainer: XPath = __ \\ "ErrorResponse"

  private val error = errorResponseContainer \ "Error"

  implicit val xmlReader: XmlReader[ChRISRIMValidationErrorResponse] =
    for {
      failures <- error.read[Seq[RIMValidationError]](strictReadSeq(RIMValidationError.xmlReader))
    } yield ChRISRIMValidationErrorResponse(failures)
}