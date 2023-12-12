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

package uk.gov.hmrc.emcstfe.models.response.getMovementHistoryEvents

import cats.implicits.catsSyntaxTuple5Semigroupal
import com.lucidchart.open.xtract.XmlReader.strictReadSeq
import com.lucidchart.open.xtract.{XPath, XmlReader}
import play.api.libs.json.{Format, Json}

case class MovementHistoryEvent(eventType: String, eventDate: String, sequenceNumber: Int, messageRole: Int, upstreamArc: Option[String])

object MovementHistoryEvent {
  val eventTypePath: XPath = XPath \\ "EventType"
  val eventDatePath: XPath = XPath \\ "EventDate"
  val sequenceNumberPath: XPath = XPath \\ "SequenceNumber"
  val messageRolePath: XPath = XPath \\ "MessageRole"
  val upstreamArcPath: XPath = XPath \\ "UpstreamARC"
  private val eventsPath: XPath = XPath \\ "MovementHistory" \\ "Events"

  implicit val xmlReader: XmlReader[MovementHistoryEvent] = (
    eventTypePath.read[String],
    eventDatePath.read[String],
    sequenceNumberPath.read[Int],
    messageRolePath.read[Int],
    upstreamArcPath.read[String].optional
  ).mapN(MovementHistoryEvent.apply)

  implicit val xmlSeqReader: XmlReader[Seq[MovementHistoryEvent]] =
    eventsPath.read[Seq[MovementHistoryEvent]](strictReadSeq)

  implicit val format: Format[MovementHistoryEvent] = Json.format[MovementHistoryEvent]
}
