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

package uk.gov.hmrc.emcstfe.models.response

import cats.implicits.catsSyntaxTuple2Semigroupal
import com.lucidchart.open.xtract.{XmlReader, __}
import play.api.libs.json.{Json, OFormat}

import scala.xml.NodeSeq

case class GetMessageStatisticsResponse(countOfAllMessages: Int,
                                        countOfNewMessages: Int) extends LegacyMessage {
  override def xmlBody: NodeSeq = {
    schemaResultBody(
      <MessageStatisticsDataResponse xmlns="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MessageStatisticsData/3" xmlns:ns1="http://hmrc/emcs/tfe/data" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" >
        <CountOfAllMessages>{countOfAllMessages}</CountOfAllMessages>
        <CountOfNewMessages>{countOfNewMessages}</CountOfNewMessages>
      </MessageStatisticsDataResponse>
    )
  }
}

object GetMessageStatisticsResponse {

  implicit val xmlReader: XmlReader[GetMessageStatisticsResponse] = (
    (__ \ "CountOfAllMessages").read[Int],
    (__ \ "CountOfNewMessages").read[Int]
  ).mapN(GetMessageStatisticsResponse.apply)

  implicit val fmt: OFormat[GetMessageStatisticsResponse] = Json.format
}
