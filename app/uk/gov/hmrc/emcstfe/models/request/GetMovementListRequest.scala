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

import uk.gov.hmrc.emcstfe.models.request.GetMovementListSearchOptions.{DEFAULT_SORT_FIELD, DEFAULT_START_POSITION, DEFAULT_TRADER_ROLE, EIS_DEFAULT_SORT_FIELD, EIS_DEFAULT_START_POSITION, EIS_DEFAULT_TRADER_ROLE}
import uk.gov.hmrc.emcstfe.models.request.chris.ChrisRequest
import uk.gov.hmrc.emcstfe.models.request.eis.EisConsumptionRequest

case class GetMovementListRequest(exciseRegistrationNumber: String,
                                  searchOptions: GetMovementListSearchOptions,
                                  isEISFeatureEnabled: Boolean) extends ChrisRequest with EisConsumptionRequest {
  override def requestBody: String =
    s"""<?xml version='1.0' encoding='UTF-8'?>
      |<soapenv:Envelope xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope">
      |  <soapenv:Header>
      |    <VersionNo>2.1</VersionNo>
      |  </soapenv:Header>
      |  <soapenv:Body>
      |    <Control xmlns="http://www.govtalk.gov.uk/taxation/InternationalTrade/Common/ControlDocument">
      |      <MetaData>
      |        <MessageId>$uuid</MessageId>
      |        <Source>emcs_tfe</Source>
      |        <Identity>portal</Identity>
      |        <Partner>UK</Partner>
      |      </MetaData>
      |      <OperationRequest>
      |        <Parameters>
      |          <Parameter Name="ExciseRegistrationNumber">$exciseRegistrationNumber</Parameter>
      |          <Parameter Name="TraderRole">${searchOptions.traderRole.getOrElse(DEFAULT_TRADER_ROLE)}</Parameter>
      |          <Parameter Name="SortField">${searchOptions.sortField.getOrElse(DEFAULT_SORT_FIELD)}</Parameter>
      |          <Parameter Name="SortOrder">${searchOptions.sortOrder}</Parameter>
      |          <Parameter Name="StartPosition">${searchOptions.startPosition.getOrElse(DEFAULT_START_POSITION)}</Parameter>
      |          <Parameter Name="MaxNoToReturn">${searchOptions.maxRows}</Parameter>
      |        </Parameters>
      |        <ReturnData>
      |          <Data Name="schema" />
      |        </ReturnData>
      |      </OperationRequest>
      |    </Control>
      |  </soapenv:Body>
      |</soapenv:Envelope>""".stripMargin

  override def action: String = "http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/GetMovementList"

  override def shouldExtractFromSoap: Boolean = true

  override def metricName = "get-movement-list"

  override val queryParams: Seq[(String, String)] = Seq(
    "exciseregistrationnumber" -> Some(exciseRegistrationNumber),
    "traderrole" -> Some(if(isEISFeatureEnabled) searchOptions.traderRole.getOrElse(EIS_DEFAULT_TRADER_ROLE) else searchOptions.traderRole.getOrElse(DEFAULT_TRADER_ROLE)),
    "sortfield" -> Some(if(isEISFeatureEnabled) searchOptions.sortField.getOrElse(EIS_DEFAULT_SORT_FIELD) else searchOptions.sortField.getOrElse(DEFAULT_SORT_FIELD)),
    "sortorder" -> Some(searchOptions.sortOrder),
    "startposition" -> Some(if(isEISFeatureEnabled) searchOptions.startPosition.getOrElse(EIS_DEFAULT_START_POSITION).toString else searchOptions.startPosition.getOrElse(DEFAULT_START_POSITION).toString),
    "maxnotoreturn" -> Some(searchOptions.maxRows.toString),
    "arc" -> searchOptions.arc,
    "othertraderid" -> searchOptions.otherTraderId,
    "localreferencenumber" -> searchOptions.lrn,
    "dateofdispatchfrom" -> searchOptions.dateOfDispatchFrom,
    "dateofdispatchto" -> searchOptions.dateOfDispatchTo,
    "dateofreceiptfrom" -> searchOptions.dateOfReceiptFrom,
    "dateofreceiptto" -> searchOptions.dateOfReceiptTo,
    "countryoforigin" -> searchOptions.countryOfOrigin,
    "movementstatus" -> searchOptions.movementStatus,
    "transportertradername" -> searchOptions.transporterTraderName,
    "undischargedmovements" -> searchOptions.undischargedMovements,
    "exciseproductcode" -> searchOptions.exciseProductCode
  ).collect { case (key, Some(value)) => key -> value }
}
