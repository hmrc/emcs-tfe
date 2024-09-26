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
import uk.gov.hmrc.emcstfe.models.cancellationOfMovement.SubmitCancellationOfMovementModel
import uk.gov.hmrc.emcstfe.models.common.DestinationType.{ExemptedOrganisations, Export}
import uk.gov.hmrc.emcstfe.models.request.eis.{EisMessage, EisSubmissionRequest, Source}

import java.util.Base64

case class SubmitCancellationOfMovementRequest(body: SubmitCancellationOfMovementModel)
                                              (implicit request: UserRequest[_]) extends EisSubmissionRequest with EisMessage {
  override def exciseRegistrationNumber: String = request.ern

  private val arcCountryCode: String = body.exciseMovement.arc.substring(2, 4)

  private val messageRecipientSuffix: String =
    body.destinationType match {
      case Export => arcCountryCode
      case ExemptedOrganisations => body.memberStateCode.getOrElse(Constants.GB)
      case _ => body.consigneeTrader.flatMap(_.countryCode).getOrElse(Constants.GB)
    }
  private val messageNumber = 810


  val messageRecipient: String = Constants.NDEA ++ messageRecipientSuffix

  val messageSender: String = Constants.NDEA ++ arcCountryCode

  override def metricName: String = "cancellation-of-movement"

  override def eisXMLBody(): String =
    withEisMessage(
      body = body,
      messageNumber = messageNumber,
      messageSender = messageSender,
      messageRecipient = messageRecipient
    )

  override def toJson: JsObject =
    Json.obj(
      "user" -> exciseRegistrationNumber,
      "messageType" -> s"IE$messageNumber",
      "message" -> Base64.getEncoder.encodeToString(eisXMLBody().getBytes)
    )

  override val source: Source = Source.TFE
}
