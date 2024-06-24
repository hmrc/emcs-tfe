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

package uk.gov.hmrc.emcstfe.models.request

import uk.gov.hmrc.emcstfe.models.request.chris.ChrisRequest
import uk.gov.hmrc.emcstfe.models.request.eis.EisConsumptionRequest

case class GetMessagesRequest(exciseRegistrationNumber: String,
                              sortField: String,
                              sortOrder: String,
                              page: Int,
                              maxNoToReturn: Int = 10,
                              startPosition: Option[Int] = None) extends EisConsumptionRequest with ChrisRequest {
  require(page >= 1, "page cannot be less than 1")
  require(GetMessagesRequest.validSortFields.contains(sortField), s"sortField of $sortField is invalid. Valid sort fields: ${GetMessagesRequest.validSortFields}")
  require(GetMessagesRequest.validSortOrders.contains(sortOrder), s"sortOrder of $sortOrder is invalid. Valid sort orders: ${GetMessagesRequest.validSortOrders}")



  // page 1 -> start at 0
  // page 2 -> start at 10
  // page 3 -> start at 20
  private def startPos: BigInt = startPosition.getOrElse[Int]((page - 1) * maxNoToReturn)

  override def metricName: String = "messages"

  override def requestBody: String =
    withGetRequestSoapEnvelope(
      <Parameters>
        <Parameter Name="ExciseRegistrationNumber">{exciseRegistrationNumber}</Parameter>
        <Parameter Name="SortField">{GetMessagesRequest.toChRISSortField(sortField)}</Parameter>
        <Parameter Name="SortOrder">{sortOrder}</Parameter>
        <Parameter Name="StartPosition">{startPos}</Parameter>
        <Parameter Name="MaxNoToReturn">{maxNoToReturn}</Parameter>
      </Parameters>
    )

  override def action: String = "http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/GetMessages"

  override def shouldExtractFromSoap: Boolean = true

  override val queryParams: Seq[(String, String)] = Seq(
    "exciseregistrationnumber" -> exciseRegistrationNumber,
    "sortfield" -> sortField,
    "sortorder" -> sortOrder,
    "startposition" -> startPos.toString,
    "maxnotoreturn" -> maxNoToReturn.toString
  )
}

object GetMessagesRequest {

  val toChRISSortField: String => String = {
    //Note this is exhaustive due to the `require` on the class checking - ideally this could be more typesafe in future with enum
    case "messagetype" => "MessageType"
    case "datereceived" => "DateReceived"
    case "arc" => "ARC"
    case "readindicator" => "ReadIndicator"
  }

  val validSortFields: Seq[String] = Seq(
    "messagetype", "datereceived", "arc", "readindicator"
  )
  val validSortOrders: Seq[String] = Seq(
    "A", "D"
  )
}
