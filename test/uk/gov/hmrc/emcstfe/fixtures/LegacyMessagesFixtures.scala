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

import scala.xml.{Elem, NodeSeq}

trait LegacyMessagesFixtures extends GetMessageStatisticsFixtures with GetMessagesFixtures with MarkMessageAsReadFixtures with SetMessageAsLogicallyDeletedFixtures with GetSubmissionFailureMessageFixtures with GetMovementFixture {

  private def xmlWrapper(request: NodeSeq): Elem = <soapenv:Envelope xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope">
    <soapenv:Header>
      <VersionNo>2.1</VersionNo>
    </soapenv:Header> <soapenv:Body>
      <Control xmlns="http://www.govtalk.gov.uk/taxation/InternationalTrade/Common/ControlDocument">
        <MetaData>
          <MessageId>a1812b66-f0cd-413d-b615-dc9ae2ec7d6e</MessageId>
          <Source>emcs_tfe</Source>
          <Identity>portal</Identity>
          <Partner>UK</Partner>
        </MetaData>
        <OperationRequest>
          {request}
          <ReturnData>
            <Data Name="schema"/>
          </ReturnData>
        </OperationRequest>
      </Control>
    </soapenv:Body>
  </soapenv:Envelope>

  protected val validGetMessagesXMLRequest: Elem = xmlWrapper(<Parameters>
    <Parameter Name="ExciseRegistrationNumber">{testErn}</Parameter>
    <Parameter Name="StartPosition">0</Parameter>
    <Parameter Name="SortField">DateReceived</Parameter>
    <Parameter Name="SortOrder">D</Parameter>
    <Parameter Name="MaxNoToReturn">30</Parameter>
  </Parameters>)

  protected val getMessagesXMLRequestNoSortField: Elem = xmlWrapper(
    <Parameters>
      <Parameter Name="ExciseRegistrationNumber">{testErn}</Parameter>
      <Parameter Name="StartPosition">0</Parameter>
      <Parameter Name="SortOrder">D</Parameter>
      <Parameter Name="MaxNoToReturn">30</Parameter>
    </Parameters>)

  protected val validGetMessageStatisticsXMLRequest: Elem = xmlWrapper(
    <Parameters>
      <Parameter Name="ExciseRegistrationNumber">{testErn}</Parameter>
    </Parameters>
  )

  protected val getMessageStatisticsXMLRequestNoERN: Elem = xmlWrapper(
    <Parameters>
      <Parameter Name="ExciseRegistrationNumber2">{testErn}</Parameter>
    </Parameters>
  )

  protected val validMessageOperationXMLRequest: Elem = xmlWrapper(
    <Parameters>
      <Parameter Name="ExciseRegistrationNumber">{testErn}</Parameter>
      <Parameter Name="UniqueMessageId">1110</Parameter>
    </Parameters>
  )

  protected val messageOperationXMLRequestNoUniqueMessageId: Elem = xmlWrapper(
    <Parameters>
      <Parameter Name="ExciseRegistrationNumber">{testErn}</Parameter>
      <Parameter Name="UniqueMessageId2">1110</Parameter>
    </Parameters>
  )

  protected val messageOperationXMLRequestNoErn: Elem = xmlWrapper(
    <Parameters>
      <Parameter Name="ExciseRegistrationNumber2">{testErn}</Parameter>
      <Parameter Name="UniqueMessageId">1110</Parameter>
    </Parameters>
  )

  protected val validGetMovementXMLRequest: Elem = xmlWrapper(
    <Parameters>
      <con:Parameter Name="ExciseRegistrationNumber">{testErn}</con:Parameter>
      <con:Parameter Name="SequenceNumber">2</con:Parameter>
      <con:Parameter Name="ARC">{testArc}</con:Parameter>
    </Parameters>
  )

  protected val getMovementXMLRequestNoARC: Elem = xmlWrapper(
    <Parameters>
      <con:Parameter Name="ExciseRegistrationNumber">{testErn}</con:Parameter>
      <con:Parameter Name="SequenceNumber">2</con:Parameter>
    </Parameters>
  )



}
