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

import cats.implicits.catsSyntaxTuple2Semigroupal
import com.lucidchart.open.xtract.XmlReader.strictReadSeq
import com.lucidchart.open.xtract.{XPath, XmlReader, __}
import play.api.libs.json.{Json, OWrites}

case class IE704Body(attributes: Option[IE704Attributes], functionalError: Seq[IE704FunctionalError])

object IE704Body {
  implicit val writes: OWrites[IE704Body] = Json.writes

  private val attributes: XPath = __ \\ "GenericRefusalMessage" \\ "Attributes"
  private val functionalError: XPath = __ \\ "GenericRefusalMessage" \\ "FunctionalError"

  implicit val xmlReader: XmlReader[IE704Body] = (
    attributes.read[Option[IE704Attributes]].map {
      case Some(value) if !value.isEmpty => Some(value)
      case _ => None
    },
    functionalError.read[Seq[IE704FunctionalError]](strictReadSeq)
  ).mapN(IE704Body.apply)
}
