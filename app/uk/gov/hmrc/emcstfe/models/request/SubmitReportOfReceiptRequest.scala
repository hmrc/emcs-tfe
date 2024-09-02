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
import uk.gov.hmrc.emcstfe.models.common.DestinationType.TaxWarehouse
import uk.gov.hmrc.emcstfe.models.common.TraderModel
import uk.gov.hmrc.emcstfe.models.reportOfReceipt.SubmitReportOfReceiptModel
import uk.gov.hmrc.emcstfe.models.request.eis.{EisMessage, EisSubmissionRequest}

import java.util.Base64

case class SubmitReportOfReceiptRequest(body: SubmitReportOfReceiptModel)
                                       (implicit request: UserRequest[_]) extends EisSubmissionRequest with EisMessage {

  private val arcCountryCode = body.arc.substring(2, 4)
  private val traderModelCountryCode: Option[TraderModel] => String = _.flatMap(_.countryCode).getOrElse(exciseRegistrationNumber.substring(0, 2))
    private val messageNumber = 818

  val messageRecipient: String = Constants.NDEA ++ arcCountryCode

  val messageSender: String =
    Constants.NDEA ++ (body.destinationType match {
      case Some(TaxWarehouse) => traderModelCountryCode(body.deliveryPlaceTrader)
      case _ => traderModelCountryCode(body.consigneeTrader)
    })

  override def exciseRegistrationNumber: String = request.ern

  override def metricName: String = "report-receipt"

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
}
