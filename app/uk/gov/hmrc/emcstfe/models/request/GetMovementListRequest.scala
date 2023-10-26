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

case class GetMovementListRequest(exciseRegistrationNumber: String,
                                  searchOptions: GetMovementListSearchOptions) extends ChrisRequest {
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
      |          <Parameter Name="TraderRole">${searchOptions.traderRole}</Parameter>
      |          <Parameter Name="SortField">${searchOptions.sortField}</Parameter>
      |          <Parameter Name="SortOrder">${searchOptions.sortOrder}</Parameter>
      |          <Parameter Name="StartPosition">${searchOptions.startPosition}</Parameter>
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
}
