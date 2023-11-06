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

import cats.implicits.catsSyntaxTuple3Semigroupal
import com.lucidchart.open.xtract.{XPath, XmlReader, __}
import play.api.libs.json._

case class IE704Attributes(arc: Option[String], sequenceNumber: Option[Int], lrn: Option[String]) {
  def isEmpty: Boolean = arc.isEmpty && sequenceNumber.isEmpty && lrn.isEmpty
}

object IE704Attributes {
  implicit val writes: OWrites[IE704Attributes] = Json.writes

  private val arc: XPath = __ \\ "AdministrativeReferenceCode"
  private val sequenceNumber: XPath = __ \\ "SequenceNumber"
  private val lrn: XPath = __ \\ "LocalReferenceNumber"

  implicit val xmlReader: XmlReader[IE704Attributes] = (
    arc.read[Option[String]],
    sequenceNumber.read[Option[Int]],
    lrn.read[Option[String]]
  ).mapN(IE704Attributes.apply)
}
