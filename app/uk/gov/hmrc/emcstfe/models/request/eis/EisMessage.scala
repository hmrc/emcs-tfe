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

package uk.gov.hmrc.emcstfe.models.request.eis

import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.common.XmlBaseModel
import uk.gov.hmrc.emcstfe.utils.XmlWriterUtils

import scala.xml.{Elem, Node, PCData, XML}

trait EisMessage extends XmlWriterUtils {
  _: EisSubmissionRequest =>

  def withEisMessage[T <: XmlBaseModel](body: T,
                                        messageNumber: Int,
                                        messageSender: String,
                                        messageRecipient: String)(implicit request: UserRequest[_]): String =
    trimWhitespaceFromXml(controlDocument(
      XML.loadString(
        s"""<urn:IE$messageNumber xmlns:urn="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE$messageNumber:V3.01" xmlns:urn1="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:TMS:V3.01">
           |  <urn:Header>
           |    <urn1:MessageSender>$messageSender</urn1:MessageSender>
           |    <urn1:MessageRecipient>$messageRecipient</urn1:MessageRecipient>
           |    <urn1:DateOfPreparation>${preparedDate.toString}</urn1:DateOfPreparation>
           |    <urn1:TimeOfPreparation>${preparedTime.toString}</urn1:TimeOfPreparation>
           |    <urn1:MessageIdentifier>$messageUUID</urn1:MessageIdentifier>
           |    <urn1:CorrelationIdentifier>$correlationUUID</urn1:CorrelationIdentifier>
           |  </urn:Header>
           |  <urn:Body>${body.toXml}</urn:Body>
           |</urn:IE$messageNumber>""".stripMargin)
    )).toString()

  //TODO when we have the updated spec update
  private def controlDocument(xml: Node): Elem = {
    <con:Control xmlns:con="http://www.govtalk.gov.uk/taxation/InternationalTrade/Common/ControlDocument">
      <con:MetaData>
        <con:MessageId>{messageUUID}</con:MessageId>
        <con:Source>TFE</con:Source>
        <con:CorrelationId>{correlationUUID}</con:CorrelationId>
      </con:MetaData>
      <con:OperationRequest>
        <con:Parameters>
          <con:Parameter Name="ExciseRegistrationNumber">{exciseRegistrationNumber}</con:Parameter>
          <con:Parameter Name="message">{PCData(xml.toString())}</con:Parameter>
        </con:Parameters>
        <con:ReturnData/>
      </con:OperationRequest>
    </con:Control>
  }
}
