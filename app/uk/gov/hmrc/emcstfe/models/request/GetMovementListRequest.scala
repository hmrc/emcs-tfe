/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.models.request

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
}
