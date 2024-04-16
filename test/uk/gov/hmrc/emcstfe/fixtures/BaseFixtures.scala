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

import java.time.LocalDate
import scala.xml.{Node, NodeSeq, PCData}

trait BaseFixtures {

  val testAuthToken = "Bearer token"
  val testErn = "GBWK000001234"
  val testArc: String = "23GB00000000000376967"
  val testLrn: String = "LRN"
  val testMessageId = "1234"
  val testDraftId: String = "1234-5678-9012"
  val testCredId = "cred1234567891"
  val testInternalId = "int1234567891"
  val testDestinationOffice = "GB1234"

  val now: String = LocalDate.now().toString

  def soapEnvelope(node: NodeSeq): Node = <env:Envelope xmlns:env="http://www.w3.org/2003/05/soap-envelope" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <env:Body>
      <con:Control xmlns:con="http://www.govtalk.gov.uk/taxation/InternationalTrade/Common/ControlDocument">
        <con:MetaData>
          <con:MessageId>messageUUID</con:MessageId>
          <con:Source>TFE</con:Source>
          <con:CorrelationId>PORTAL123</con:CorrelationId>
        </con:MetaData>
        <con:OperationResponse>
          <con:Results>
            {node}
          </con:Results>
        </con:OperationResponse>
      </con:Control>
    </env:Body>
  </env:Envelope>

  def schemaResultBody(nodeSeq: NodeSeq): NodeSeq = <con:Result Name="schema">
    {PCData(nodeSeq.toString())}
  </con:Result>

  def recordsAffectedBody(records: Int): NodeSeq = <con:Result Name="recordsAffected">
    {records}
  </con:Result>

}
