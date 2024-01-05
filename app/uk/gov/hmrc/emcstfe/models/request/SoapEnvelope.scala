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

import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.common.XmlBaseModel
import uk.gov.hmrc.emcstfe.models.request.chris.ChrisRequest

import scala.xml.{Elem, XML}

trait SoapEnvelope { _: ChrisRequest =>
  def withSoapEnvelope[T <: XmlBaseModel](body: T,
                                          messageNumber: Int,
                                          messageSender: String,
                                          messageRecipient: String)(implicit request: UserRequest[_]): Elem =
    XML.loadString(
      s"""<soapenv:Envelope xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope">
         |  <soapenv:Header>
         |    <ns:Info xmlns:ns="http://www.hmrc.gov.uk/ws/info-header/1">
         |      <ns:VendorName>EMCS_PORTAL_TFE</ns:VendorName>
         |      <ns:VendorID>1259</ns:VendorID>
         |      <ns:VendorProduct Version="2.0">HMRC Portal</ns:VendorProduct>
         |      <ns:ServiceID>1138</ns:ServiceID>
         |      <ns:ServiceMessageType>HMRC-EMCS-IE$messageNumber-DIRECT</ns:ServiceMessageType>
         |    </ns:Info>
         |    <MetaData xmlns="http://www.hmrc.gov.uk/ChRIS/SOAP/MetaData/1">
         |      <CredentialID>${request.credId}</CredentialID>
         |      <Identifier>${request.ern}</Identifier>
         |    </MetaData>
         |  </soapenv:Header>
         |  <soapenv:Body>
         |    <urn:IE$messageNumber xmlns:urn="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE$messageNumber:V3.01" xmlns:urn1="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:TMS:V3.01">
         |      <urn:Header>
         |        <urn1:MessageSender>$messageSender</urn1:MessageSender>
         |        <urn1:MessageRecipient>$messageRecipient</urn1:MessageRecipient>
         |        <urn1:DateOfPreparation>${preparedDate.toString}</urn1:DateOfPreparation>
         |        <urn1:TimeOfPreparation>${preparedTime.toString}</urn1:TimeOfPreparation>
         |        <urn1:MessageIdentifier>$messageUUID</urn1:MessageIdentifier>
         |        <urn1:CorrelationIdentifier>$legacyCorrelationUUID</urn1:CorrelationIdentifier>
         |      </urn:Header>
         |      <urn:Body>${body.toXml}</urn:Body>
         |    </urn:IE$messageNumber>
         |  </soapenv:Body>
         |</soapenv:Envelope>""".stripMargin)
}
