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

import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.emcstfe.models.response.GetMessageStatisticsResponse

import scala.xml.Elem

trait GetMessageStatisticsFixtures extends BaseFixtures {

  lazy val getMessageStatisticsXMLResponse: String = responseWithSoapWrapper(
    <MessageStatisticsDataResponse xmlns="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MessageStatisticsData/3"
                                   targetNamespace="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MessageStatisticsData/3"
                                   xmlns:xs="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified" attributeFormDefault="unqualified">
      <CountOfAllMessages>10</CountOfAllMessages>
      <CountOfNewMessages>5</CountOfNewMessages>
    </MessageStatisticsDataResponse>
  )

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

  val getMessageStatisticsDownstreamJson: JsValue = Json.obj(
    "dateTime" -> now,
    "exciseRegistrationNumber" -> testErn,
    "countOfAllMessages" -> 10,
    "countOfNewMessages" -> 5
  )
  val getMessageStatisticsResponseModel: GetMessageStatisticsResponse = GetMessageStatisticsResponse(
    countOfAllMessages = 10, countOfNewMessages = 5
  )
  val getMessageStatisticsJson: JsValue = Json.obj(
    "countOfAllMessages" -> 10,
    "countOfNewMessages" -> 5
  )
}
