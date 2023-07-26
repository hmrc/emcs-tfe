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
import uk.gov.hmrc.emcstfe.models.common.MovementType
import uk.gov.hmrc.emcstfe.models.common.DestinationType
import uk.gov.hmrc.emcstfe.models.common.MovementType._
import uk.gov.hmrc.emcstfe.models.common.DestinationType._
import uk.gov.hmrc.emcstfe.models.createMovement.CreateMovementModel

case class SubmitCreateMovementRequest(body: CreateMovementModel)
                                      (implicit request: UserRequest[_]) extends ChrisRequest with SoapEnvelope {
  override def exciseRegistrationNumber: String = request.ern

  val messageRecipient = NDEA ++ messageRecipientCountryCode().getOrElse(GB)
  val messageSender: String = NDEA ++ messageSenderCountryCode().getOrElse(GB)

  val soapRequest = envelope(body, 815, messageSender, messageRecipient)

  override def action: String = "http://www.hmrc.gov.uk/emcs/submitdraftmovementportal"

  override def shouldExtractFromSoap: Boolean = false

  override def requestBody: String =
    s"""<?xml version='1.0' encoding='UTF-8'?>
       |${soapRequest.toString}""".stripMargin

  override def metricName = "create-movement"


  private[request] def messageRecipientCountryCode(): Option[String] = {
    val movementType: MovementType = body.movementType
    val destinationType: DestinationType = body.headerEadEsad.destinationType

    (movementType, destinationType) match {
      case (UKtoUK | ImportUK, _) | (UKtoEU | ImportEU, TaxWarehouse) =>
        body.deliveryPlaceTrader.flatMap(_.countryCode)
      case (UKtoEU | ImportEU, ExemptedOrganisations) =>
        body.complementConsigneeTrader.map(_.memberStateCode)
      case (UKtoEU, RegisteredConsignee | TemporaryRegisteredConsignee | DirectDelivery | Export) |
           (ImportEU, DirectDelivery | RegisteredConsignee | TemporaryRegisteredConsignee) =>
        body.consigneeTrader.flatMap(_.countryCode)
      case (UKtoEU, UnknownDestination) =>
        body.placeOfDispatchTrader.flatMap(_.countryCode)
      case (DirectExport | IndirectExport | ImportDirectExport | ImportIndirectExport, _) =>
        body.deliveryPlaceCustomsOffice.map(_.referenceNumber.substring(0,2).toUpperCase)
      case _ =>
        Some(GB)
    }
  }

  private[request] def messageSenderCountryCode(): Option[String] =
    body.movementType match {
      case UKtoUK | UKtoEU | DirectExport | IndirectExport =>
        body.placeOfDispatchTrader.flatMap(_.countryCode)
      case _ =>
        body.consignorTrader.countryCode
    }
}
