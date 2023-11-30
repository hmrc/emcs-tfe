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

import scala.xml.{NodeSeq, PCData}

trait LegacyMessage {

  private def soapEnvelope(node: NodeSeq): NodeSeq = <env:Envelope xmlns:env="http://www.w3.org/2003/05/soap-envelope" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <env:Body>
      <con:Control xmlns:con="http://www.govtalk.gov.uk/taxation/InternationalTrade/Common/ControlDocument">
        <con:MetaData>
          <con:MessageId>messageUUID</con:MessageId>
          <con:Source>TFE</con:Source>
          <con:CorrelationId>PORTAL123</con:CorrelationId>
        </con:MetaData>
        <con:OperationResponse>
          <con:Results>
            { node }
          </con:Results>
        </con:OperationResponse>
      </con:Control>
    </env:Body>
  </env:Envelope>

  protected def schemaResultBody(nodeSeq: NodeSeq): NodeSeq = <con:Result Name="schema">
    {PCData(nodeSeq.toString())}
  </con:Result>

  protected def recordsAffectedBody(records: Int): NodeSeq = <con:Result Name="recordsAffected">
    {records}
  </con:Result>

  protected def xmlBody: NodeSeq

  def toXml: NodeSeq = soapEnvelope(xmlBody)

}
