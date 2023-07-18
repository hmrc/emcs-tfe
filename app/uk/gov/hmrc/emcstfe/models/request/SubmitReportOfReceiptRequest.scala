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
import uk.gov.hmrc.emcstfe.models.common.DestinationType.{DirectDelivery, RegisteredConsignee, TaxWarehouse, TemporaryRegisteredConsignee}
import uk.gov.hmrc.emcstfe.models.reportOfReceipt.{SubmitReportOfReceiptModel, TraderModel}

import java.time.{LocalDate, LocalTime, ZoneId}
import java.util.UUID

case class SubmitReportOfReceiptRequest(body: SubmitReportOfReceiptModel)
                                       (implicit request: UserRequest[_]) extends ChrisRequest {

  private val NDEA = "NDEA."
  private val GB = "GB"
  private val arcCountryCode = body.arc.substring(2, 4)
  private val traderModelCountryCode: Option[TraderModel] => String = _.flatMap(_.countryCode).getOrElse(GB)

  val messageSender =
    NDEA ++ (if (body.destinationType == DirectDelivery) {
      traderModelCountryCode(body.consigneeTrader)
    } else {
      arcCountryCode
    })

  val messageRecipient =
    NDEA ++ (body.destinationType match {
      case TaxWarehouse => traderModelCountryCode(body.deliveryPlaceTrader)
      case TemporaryRegisteredConsignee | RegisteredConsignee => traderModelCountryCode(body.consigneeTrader)
      case DirectDelivery => arcCountryCode
      case _ => GB
    })

  override def exciseRegistrationNumber: String = request.ern

  val preparedDate = LocalDate.now(ZoneId.of("UTC"))
  val preparedTime = LocalTime.now(ZoneId.of("UTC"))
  val correlationUUID = UUID.randomUUID()
  val messageUUID = UUID.randomUUID()

  val soapRequest =
    <soapenv:Envelope xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope">
      <soapenv:Header>
        <ns:Info xmlns:ns="http://www.hmrc.gov.uk/ws/info-header/1">
          <ns:VendorName>EMCS_PORTAL_TFE</ns:VendorName>
          <ns:VendorID>1259</ns:VendorID>
          <ns:VendorProduct Version="2.0">HMRC Portal</ns:VendorProduct>
          <ns:ServiceID>1138</ns:ServiceID>
          <ns:ServiceMessageType>HMRC-EMCS-IE818-DIRECT</ns:ServiceMessageType>
        </ns:Info>
        <MetaData xmlns="http://www.hmrc.gov.uk/ChRIS/SOAP/MetaData/1">
          <CredentialID>{request.credId}</CredentialID>
          <Identifier>{exciseRegistrationNumber}</Identifier>
        </MetaData>
      </soapenv:Header>
      <soapenv:Body>
        <urn:IE818 xmlns:urn="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE818:V3.01" xmlns:urn1="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:TMS:V3.01">
          <urn:Header>
            <urn1:MessageSender>{messageSender}</urn1:MessageSender>
            <urn1:MessageRecipient>{messageRecipient}</urn1:MessageRecipient>
            <urn1:DateOfPreparation>{preparedDate.toString}</urn1:DateOfPreparation>
            <urn1:TimeOfPreparation>{preparedTime.toString}</urn1:TimeOfPreparation>
            <urn1:MessageIdentifier>{messageUUID}</urn1:MessageIdentifier>
            <urn1:CorrelationIdentifier>{correlationUUID}</urn1:CorrelationIdentifier>
          </urn:Header>
          <urn:Body>
            {body.toXml}
          </urn:Body>
        </urn:IE818>
      </soapenv:Body>
    </soapenv:Envelope>

  override def requestBody: String =
    s"""<?xml version='1.0' encoding='UTF-8'?>
       |${soapRequest.toString}""".stripMargin

  override def action: String = "http://www.hmrc.gov.uk/emcs/submitreportofreceiptportal"

  override def shouldExtractFromSoap: Boolean = false

  override def metricName: String = "submit-report-receipt"
}
