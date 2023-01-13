/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.models.request

case class GetMessageRequest(exciseRegistrationNumber: String, arc: String) extends ChrisRequest {
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
      |          <Parameter Name="ARC">$arc</Parameter>
      |        </Parameters>
      |        <ReturnData>
      |          <Data Name="schema" />
      |        </ReturnData>
      |      </OperationRequest>
      |    </Control>
      |  </soapenv:Body>
      |</soapenv:Envelope>""".stripMargin

  override def action: String = "http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/GetMovement"
}
