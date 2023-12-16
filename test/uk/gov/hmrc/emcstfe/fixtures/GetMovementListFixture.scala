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

import play.api.libs.json.{JsObject, JsValue, Json}
import uk.gov.hmrc.emcstfe.models.response.getMovement.GetMovementListResponse
import uk.gov.hmrc.emcstfe.models.response.{GetMovementList, GetMovementListItem}

import java.time.LocalDateTime
import scala.xml.Elem

trait GetMovementListFixture extends BaseFixtures {

  lazy val noMovements: Elem =
    <MovementListDataResponse xmlns="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementListData/3" xmlns:ns1="http://hmrc/emcs/tfe/data" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
      <CountOfMovementsAvailable>0</CountOfMovementsAvailable>
    </MovementListDataResponse>

  lazy val getMovementListXMLResponseBody: String = """<MovementListDataResponse xmlns="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementListData/3" xmlns:ns1="http://hmrc/emcs/tfe/data" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                                                   |	<Movement>
                                                   |		<Arc>18GB00000000000232361</Arc>
                                                   |		<DateOfDispatch>2009-01-26T14:11:00</DateOfDispatch>
                                                   |		<MovementStatus>Accepted</MovementStatus>
                                                   |		<OtherTraderID>ABCD1234</OtherTraderID>
                                                   |	</Movement>
                                                   |	<Movement>
                                                   |		<Arc>GBTR000000EMCS1000040</Arc>
                                                   |		<DateOfDispatch>2009-01-26T14:12:00</DateOfDispatch>
                                                   |		<MovementStatus>Accepted</MovementStatus>
                                                   |		<OtherTraderID>ABCD1234</OtherTraderID>
                                                   |	</Movement>
                                                   |	<CountOfMovementsAvailable>2</CountOfMovementsAvailable>
                                                   |</MovementListDataResponse>""".stripMargin

  lazy val getMovementListSoapWrapper: String = s"""<tns:Envelope
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
                                               |						<![CDATA[$getMovementListXMLResponseBody]]>
                                               |					</con:Result>
                                               |				</con:Results>
                                               |			</con:OperationResponse>
                                               |		</con:Control>
                                               |	</tns:Body>
                                               |</tns:Envelope>""".stripMargin

  lazy val movement1 = GetMovementListItem(
    arc = "18GB00000000000232361",
    dateOfDispatch = LocalDateTime.parse("2009-01-26T14:11:00"),
    movementStatus = "Accepted",
    otherTraderID = "ABCD1234"
  )

  lazy val movement1XML: Elem =
    <Movement>
      <Arc>18GB00000000000232361</Arc>
      <DateOfDispatch>2009-01-26T14:11:00</DateOfDispatch>
      <MovementStatus>Accepted</MovementStatus>
      <OtherTraderID>ABCD1234</OtherTraderID>
    </Movement>

  lazy val movement1Json = Json.obj(
    "arc" -> "18GB00000000000232361",
    "dateOfDispatch" -> LocalDateTime.parse("2009-01-26T14:11:00"),
    "movementStatus" -> "Accepted",
    "otherTraderID" -> "ABCD1234"
  )

  lazy val movement2 = GetMovementListItem(
    arc = "GBTR000000EMCS1000040",
    dateOfDispatch = LocalDateTime.parse("2009-01-26T14:12:00"),
    movementStatus = "Accepted",
    otherTraderID = "ABCD1234"
  )

  lazy val movement2Json = Json.obj(
    "arc" -> "GBTR000000EMCS1000040",
    "dateOfDispatch" -> "2009-01-26T14:12:00",
    "movementStatus" -> "Accepted",
    "otherTraderID" -> "ABCD1234"
  )

  lazy val getMovementList: GetMovementList = GetMovementList(Seq(movement1, movement2), 2)

  lazy val getMovementListJson: JsValue = Json.obj(
    "movements" -> Json.arr(
      movement1Json,
      movement2Json
    ),
    "count" -> 2
  )

  lazy val getMovementListResponse: GetMovementListResponse = GetMovementListResponse(
    exciseRegistrationNumber = testErn,
    dateTime = "2023-09-07T12:39:20.354Z",
    movementList = getMovementList
  )

  lazy val getMovementListJsonResponse: JsObject = Json.obj(
"exciseRegistrationNumber" -> testErn,
    "dateTime" -> "2023-09-07T12:39:20.354Z",
    "message" -> "PE1vdmVtZW50TGlzdERhdGFSZXNwb25zZSB4bWxucz0iaHR0cDovL3d3dy5nb3Z0YWxrLmdvdi51ay90YXhhdGlvbi9JbnRlcm5hdGlvbmFsVHJhZGUvRXhjaXNlL01vdmVtZW50TGlzdERhdGEvMyIgeG1sbnM6bnMxPSJodHRwOi8vaG1yYy9lbWNzL3RmZS9kYXRhIiB4bWxuczp4c2k9Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvWE1MU2NoZW1hLWluc3RhbmNlIj4KCTxNb3ZlbWVudD4KCQk8QXJjPjE4R0IwMDAwMDAwMDAwMDIzMjM2MTwvQXJjPgoJCTxEYXRlT2ZEaXNwYXRjaD4yMDA5LTAxLTI2VDE0OjExOjAwPC9EYXRlT2ZEaXNwYXRjaD4KCQk8TW92ZW1lbnRTdGF0dXM+QWNjZXB0ZWQ8L01vdmVtZW50U3RhdHVzPgoJCTxPdGhlclRyYWRlcklEPkFCQ0QxMjM0PC9PdGhlclRyYWRlcklEPgoJPC9Nb3ZlbWVudD4KCTxNb3ZlbWVudD4KCQk8QXJjPkdCVFIwMDAwMDBFTUNTMTAwMDA0MDwvQXJjPgoJCTxEYXRlT2ZEaXNwYXRjaD4yMDA5LTAxLTI2VDE0OjEyOjAwPC9EYXRlT2ZEaXNwYXRjaD4KCQk8TW92ZW1lbnRTdGF0dXM+QWNjZXB0ZWQ8L01vdmVtZW50U3RhdHVzPgoJCTxPdGhlclRyYWRlcklEPkFCQ0QxMjM0PC9PdGhlclRyYWRlcklEPgoJPC9Nb3ZlbWVudD4KCTxDb3VudE9mTW92ZW1lbnRzQXZhaWxhYmxlPjI8L0NvdW50T2ZNb3ZlbWVudHNBdmFpbGFibGU+CjwvTW92ZW1lbnRMaXN0RGF0YVJlc3BvbnNlPg=="
  )

}
