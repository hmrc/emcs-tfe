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
import com.lucidchart.open.xtract.{XPath, XmlReader, __}
import play.api.libs.json.{Json, OWrites}

case class IE704Model(header: IE704Header, body: IE704Body)

object IE704Model {
  implicit val writes: OWrites[IE704Model] = Json.writes

  private val header: XPath = __ \\ "IE704" \\ "Header"
  private val body: XPath = __ \\ "IE704" \\ "Body"

  implicit val xmlReader: XmlReader[IE704Model] = (
    header.read[IE704Header],
    body.read[IE704Body]
  ).mapN(IE704Model.apply)
}
