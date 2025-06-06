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

import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.emcstfe.config.Constants
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.common.DestinationType._
import uk.gov.hmrc.emcstfe.models.common.MovementType._
import uk.gov.hmrc.emcstfe.models.common.{DestinationType, MovementType}
import uk.gov.hmrc.emcstfe.models.createMovement.SubmitCreateMovementModel
import uk.gov.hmrc.emcstfe.models.request.eis.{EisMessage, EisSubmissionRequest, Source}

import java.util.Base64

case class SubmitCreateMovementRequest(body: SubmitCreateMovementModel, draftId: String)
                                      (implicit request: UserRequest[_]) extends EisSubmissionRequest with EisMessage {
  override def exciseRegistrationNumber: String = request.ern
  private val messageNumber = 815

  val messageRecipient = Constants.NDEA ++ messageRecipientCountryCode()
  val messageSender: String = Constants.NDEA ++ messageSenderCountryCode().getOrElse(Constants.GB)

  override val correlationUUID: String = draftId

  override def metricName = "create-movement"


  private[request] def messageRecipientCountryCode(): String = {
    val movementType: MovementType = body.movementType
    val destinationType: DestinationType = body.headerEadEsad.destinationType

    ((movementType, destinationType) match {
      case (UKtoUK | ImportUK, _) | (UKtoEU | ImportEU, TaxWarehouse) =>
        body.deliveryPlaceTrader.flatMap(_.countryCode)
      case (UKtoEU | ImportEU, ExemptedOrganisations) =>
        body.complementConsigneeTrader.map(_.memberStateCode)
      case (UKtoEU, RegisteredConsignee | TemporaryRegisteredConsignee | DirectDelivery | Export) |
           (ImportEU, DirectDelivery | RegisteredConsignee | TemporaryRegisteredConsignee) =>
        body.consigneeTrader.flatMap(_.countryCode)
      case (UKtoEU, UnknownDestination) =>
        body.placeOfDispatchTrader.flatMap(_.countryCode)
      case (UKtoEU, CertifiedConsignee | TemporaryCertifiedConsignee) =>
        body.consigneeTrader.flatMap(_.countryCode)
      case (DirectExport | IndirectExport | ImportDirectExport | ImportIndirectExport, _) =>
        body.deliveryPlaceCustomsOffice.map(_.referenceNumber.substring(0,2).toUpperCase)
      case _ =>
        None
    }).getOrElse(Constants.GB)
  }

  private[request] def messageSenderCountryCode(): Option[String] =
    body.movementType match {
      case UKtoUK | UKtoEU | DirectExport | IndirectExport =>
        body.placeOfDispatchTrader.flatMap(_.countryCode).fold(body.consignorTrader.countryCode)(Some(_))
      case _ =>
        body.consignorTrader.countryCode
    }

  override def eisXMLBody(): String =
    withEisMessage(
      body = body,
      messageNumber = messageNumber,
      messageSender = messageSender,
      messageRecipient = messageRecipient
    )

  override def toJson: JsValue =
    Json.obj(
      "user" -> exciseRegistrationNumber,
      "messageType" -> s"IE$messageNumber",
      "message" -> Base64.getEncoder.encodeToString(eisXMLBody().getBytes)
    )

  override val source: Source = Source.TFE
}
