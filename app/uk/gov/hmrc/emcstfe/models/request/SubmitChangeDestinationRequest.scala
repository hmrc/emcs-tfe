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

import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.emcstfe.config.Constants
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.changeDestination.SubmitChangeDestinationModel
import uk.gov.hmrc.emcstfe.models.common.DestinationType.{CertifiedConsignee, Export, ReturnToThePlaceOfDispatchOfTheConsignor, TaxWarehouse, TemporaryCertifiedConsignee}
import uk.gov.hmrc.emcstfe.models.request.chris.ChrisRequest
import uk.gov.hmrc.emcstfe.models.request.eis.{EisMessage, EisSubmissionRequest}
import uk.gov.hmrc.emcstfe.models.response.getMovement.GetMovementResponse

import java.util.Base64

case class SubmitChangeDestinationRequest(body: SubmitChangeDestinationModel, movement: GetMovementResponse, useFS41SchemaVersion: Boolean)
                                         (implicit request: UserRequest[_]) extends ChrisRequest with SoapEnvelope with EisSubmissionRequest with EisMessage {

  private val arcCountryCode = body.updateEadEsad.administrativeReferenceCode.substring(2, 4)
  private val countryCode: Option[String] => String = _.map(_.substring(0, 2)).getOrElse(Constants.GB)

  val messageSender: String = Constants.NDEA ++ arcCountryCode

  val messageRecipient: String =
    Constants.NDEA ++ (body.destinationChanged.destinationTypeCode match {
      case TaxWarehouse =>
        val ern = body.destinationChanged.deliveryPlaceTrader.flatMap(_.traderExciseNumber).fold(
          body.destinationChanged.newConsigneeTrader.flatMap(_.traderExciseNumber)
        )(Some(_))
        countryCode(ern)
      case Export =>
        countryCode(body.destinationChanged.deliveryPlaceCustomsOffice.map(_.referenceNumber))
      case CertifiedConsignee | TemporaryCertifiedConsignee =>
        // I want to keep the consignee the same and change the place of destination
        // so we need to use the country code from the existing consignee of the movement
        countryCode(movement.consigneeTrader.flatMap(_.traderExciseNumber))
      case ReturnToThePlaceOfDispatchOfTheConsignor =>
        arcCountryCode
      case _ => Constants.GB
    })

  override def exciseRegistrationNumber: String = request.ern
  private val messageNumber: Int = 813

  override def requestBody: String =
    withSubmissionRequestSoapEnvelope(
      body = body,
      messageNumber = messageNumber,
      messageSender = messageSender,
      messageRecipient = messageRecipient,
      isFS41SchemaVersion = useFS41SchemaVersion
    ).toString()

  override def action: String = "http://www.hmrc.gov.uk/emcs/submitchangeofdestinationportal"

  override def shouldExtractFromSoap: Boolean = false

  override def metricName: String = "change-of-destination"

  override def eisXMLBody(): String =
    withEisMessage(
      body = body,
      messageNumber = messageNumber,
      messageSender = messageSender,
      messageRecipient = messageRecipient,
      isFS41SchemaVersion = useFS41SchemaVersion
    )

  override def toJson: JsObject =
    Json.obj(
      "user" -> exciseRegistrationNumber,
      "messageType" -> s"IE$messageNumber",
      "message" -> Base64.getEncoder.encodeToString(eisXMLBody().getBytes)
    )
}
