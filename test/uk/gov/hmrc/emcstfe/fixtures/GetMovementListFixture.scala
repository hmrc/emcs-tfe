/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.fixtures

import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.emcstfe.models.response.{GetMovementListItem, GetMovementListResponse}

import java.time.LocalDateTime
import scala.xml.Elem

trait GetMovementListFixture {

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

  lazy val getMovementListResponse: GetMovementListResponse = GetMovementListResponse(Seq(movement1, movement2), 2)

  lazy val getMovementListJson: JsValue = Json.obj(
    "movements" -> Json.arr(
      movement1Json,
      movement2Json
    ),
    "count" -> 2
  )

}
