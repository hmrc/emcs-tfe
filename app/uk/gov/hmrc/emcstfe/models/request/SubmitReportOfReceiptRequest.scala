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

import uk.gov.hmrc.emcstfe.models.reportOfReceipt.SubmitReportOfReceiptModel

import java.time.{LocalDate, LocalTime, ZoneId}
import java.util.UUID

case class SubmitReportOfReceiptRequest(exciseRegistrationNumber: String, body: SubmitReportOfReceiptModel) extends ChrisRequest {

  val preparedDate = LocalDate.now(ZoneId.of("UTC"))
  val preparedTime = LocalTime.now(ZoneId.of("UTC"))
  val correlationUUID = UUID.randomUUID()
  val messageUUID = UUID.randomUUID()

  val soapRequest =
    <soapenv:Envelope xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope">
      <soapenv:Header>
        <VersionNo>2.1</VersionNo>
      </soapenv:Header>
      <soapenv:Body>
        <urn:IE818>
          <urn:Header>
            <urn1:MessageSender>NDEA.GB</urn1:MessageSender>
            <urn1:MessageRecipient>NDEA.GB</urn1:MessageRecipient>
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
}
