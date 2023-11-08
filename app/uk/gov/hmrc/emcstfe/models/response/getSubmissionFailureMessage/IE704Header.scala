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

import cats.implicits.catsSyntaxTuple6Semigroupal
import com.lucidchart.open.xtract.{XmlReader, __}
import play.api.libs.json._

case class IE704Header(
                        messageSender: String,
                        messageRecipient: String,
                        dateOfPreparation: String,
                        timeOfPreparation: String,
                        messageIdentifier: String,
                        correlationIdentifier: Option[String]
                      )

object IE704Header {
  implicit val writes: OWrites[IE704Header] = Json.writes

  private val messageSender = __ \\ "MessageSender"
  private val messageRecipient = __ \\ "MessageRecipient"
  private val dateOfPreparation = __ \\ "DateOfPreparation"
  private val timeOfPreparation = __ \\ "TimeOfPreparation"
  private val messageIdentifier = __ \\ "MessageIdentifier"
  private val correlationIdentifier = __ \\ "CorrelationIdentifier"

  implicit val xmlReader: XmlReader[IE704Header] = (
    messageSender.read[String],
    messageRecipient.read[String],
    dateOfPreparation.read[String],
    timeOfPreparation.read[String],
    messageIdentifier.read[String],
    correlationIdentifier.read[Option[String]]
  ).mapN(IE704Header.apply)
}
