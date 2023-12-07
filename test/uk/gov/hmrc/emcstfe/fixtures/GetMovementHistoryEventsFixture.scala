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

package uk.gov.hmrc.emcstfe.fixtures

import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.emcstfe.models.response.getMovementHistoryEvents.{GetMovementHistoryEventsResponse, MovementHistoryEvent}

import java.util.Base64
import scala.xml.Elem

trait GetMovementHistoryEventsFixture extends BaseFixtures {

  val emptyGetMovementHistoryEventsResponseXml: Elem = <MovementHistory xmlns="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementHistoryEvents/3"
                                                                        xmlns:ns1="http://hmrc/emcs/tfe/data"
                                                                        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  </MovementHistory>

  val getMovementHistoryEventsResponseXml: Elem = <MovementHistory xmlns="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementHistoryEvents/3"
                                                                   xmlns:ns1="http://hmrc/emcs/tfe/data"
                                                                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <Events>
      <EventType>IE801</EventType>
      <EventDate>2010-06-02T10:22:53</EventDate>
      <SequenceNumber>2</SequenceNumber>
      <MessageRole>1</MessageRole>
    </Events>
    <Events>
      <EventType>IE818</EventType>
      <EventDate>2010-06-02T10:22:53</EventDate>
      <SequenceNumber>2</SequenceNumber>
      <MessageRole>1</MessageRole>
    </Events>
  </MovementHistory>

  val invalidGetMovementHistoryEventsResponseXml: Elem = <MovementHistory xmlns="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementHistoryEvents/3"
                                                                          xmlns:ns1="http://hmrc/emcs/tfe/data"
                                                                          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <Events>
      <Eventype>IE801</Eventype>
      <EventDate>2010-06-02T10:22:53</EventDate>
      <SequenceNumber>2</SequenceNumber>
      <MessageRole>1</MessageRole>
    </Events>
  </MovementHistory>

  val emptyGetMovementHistoryEventsResponseJson: JsObject = Json.obj(
    "dateTime" -> "now",
    "exciseRegistrationNumber" -> testErn,
    "message" -> Base64.getEncoder.encodeToString(emptyGetMovementHistoryEventsResponseXml.toString().getBytes())
  )

  val getMovementHistoryEventsResponseJson: JsObject = Json.obj(
    "dateTime" -> "now",
    "exciseRegistrationNumber" -> testErn,
    "message" -> Base64.getEncoder.encodeToString(getMovementHistoryEventsResponseXml.toString().getBytes())
  )

  val notEncodedGetMovementHistoryEventsJson: JsObject  = Json.obj(
    "dateTime" -> "now",
    "exciseRegistrationNumber" -> testErn,
    "message" -> getMovementHistoryEventsResponseXml.toString()
  )

  val invalidGetMovementHistoryEventsResponseJson: JsObject = Json.obj(
    "dateTime" -> "now",
    "exciseRegistrationNumber" -> testErn,
    "message" -> Base64.getEncoder.encodeToString(invalidGetMovementHistoryEventsResponseXml.toString().getBytes())
  )

  val emptyGetMovementHistoryEventsResponseModel: GetMovementHistoryEventsResponse = GetMovementHistoryEventsResponse(
    "now",
    testErn,
    Nil
  )

  val getMovementHistoryEventsResponseModel: GetMovementHistoryEventsResponse = GetMovementHistoryEventsResponse(
    "now",
    testErn,
    Seq(MovementHistoryEvent("IE801", "2010-06-02T10:22:53", 2, 1, None), MovementHistoryEvent("IE818", "2010-06-02T10:22:53", 2, 1, None))
  )

  val getMovementHistoryEventsControllerResponseJson: JsObject = Json.obj(
    "dateTime" -> "now",
    "exciseRegistrationNumber" -> testErn,
    "movementHistory" -> Json.arr(
      Json.obj("eventType" -> "IE801", "eventDate" -> "2010-06-02T10:22:53", "sequenceNumber" -> 2, "messageRole" -> 1),
      Json.obj("eventType" -> "IE818", "eventDate" -> "2010-06-02T10:22:53", "sequenceNumber" -> 2, "messageRole" -> 1),
    )
  )
}
