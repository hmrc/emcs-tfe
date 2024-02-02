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

import play.api.libs.json.{JsArray, JsObject, Json}
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

  def responseWithSoapWrapper(xmlBody: Elem): String =
    s"""<tns:Envelope
      |	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      |	xmlns:tns="http://www.w3.org/2003/05/soap-envelope">
      |	<tns:Body>
      |		<con:Control
      |			xmlns:con="http://www.govtalk.gov.uk/taxation/InternationalTrade/Common/ControlDocument">
      |			<con:MetaData>
      |				<con:MessageId>String</con:MessageId>
      |				<con:Source>String</con:Source>
      |				<con:Identity>String</con:Identity>
      |				<con:Partner>String</con:Partner>
      |				<con:CorrelationId>String</con:CorrelationId>
      |				<con:BusinessKey>String</con:BusinessKey>
      |				<con:MessageDescriptor>String</con:MessageDescriptor>
      |				<con:QualityOfService>String</con:QualityOfService>
      |				<con:Destination>String</con:Destination>
      |				<con:Priority>0</con:Priority>
      |			</con:MetaData>
      |			<con:OperationResponse>
      |				<con:Results>
      |					<con:Result Name="">
      |						<![CDATA[$xmlBody]]>
      |					</con:Result>
      |				</con:Results>
      |			</con:OperationResponse>
      |		</con:Control>
      |	</tns:Body>
      |</tns:Envelope>
      |""".stripMargin

  val emptyGetMovementHistoryEventsEISResponseJson: JsObject = Json.obj(
    "dateTime" -> "now",
    "exciseRegistrationNumber" -> testErn,
    "message" -> Base64.getEncoder.encodeToString(emptyGetMovementHistoryEventsResponseXml.toString().getBytes())
  )

  val getMovementHistoryEventsEISResponseJson: JsObject = Json.obj(
    "dateTime" -> "now",
    "exciseRegistrationNumber" -> testErn,
    "message" -> Base64.getEncoder.encodeToString(getMovementHistoryEventsResponseXml.toString().getBytes())
  )

  val notEncodedGetMovementHistoryEISEventsJson: JsObject  = Json.obj(
    "dateTime" -> "now",
    "exciseRegistrationNumber" -> testErn,
    "message" -> getMovementHistoryEventsResponseXml.toString()
  )

  val invalidGetMovementHistoryEventsEISResponseJson: JsObject = Json.obj(
    "dateTime" -> "now",
    "exciseRegistrationNumber" -> testErn,
    "message" -> Base64.getEncoder.encodeToString(invalidGetMovementHistoryEventsResponseXml.toString().getBytes())
  )

  val emptyGetMovementHistoryEventsEISResponseModel: GetMovementHistoryEventsResponse = GetMovementHistoryEventsResponse(
    "now",
    testErn,
    Nil
  )

  val getMovementHistoryEvents: Seq[MovementHistoryEvent] =
    Seq(MovementHistoryEvent("IE801", "2010-06-02T10:22:53", 2, 1, None), MovementHistoryEvent("IE818", "2010-06-02T10:22:53", 2, 1, None))

  val getMovementHistoryEventsResponseModel: GetMovementHistoryEventsResponse = GetMovementHistoryEventsResponse(
    "now",
    testErn,
    getMovementHistoryEvents
  )

  val getMovementHistoryEventsControllerResponseJson: JsArray = Json.arr(
    Json.obj("eventType" -> "IE801", "eventDate" -> "2010-06-02T10:22:53", "sequenceNumber" -> 2, "messageRole" -> 1),
    Json.obj("eventType" -> "IE818", "eventDate" -> "2010-06-02T10:22:53", "sequenceNumber" -> 2, "messageRole" -> 1),
  )
}
