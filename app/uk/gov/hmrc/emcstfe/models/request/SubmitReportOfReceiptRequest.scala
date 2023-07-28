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

import uk.gov.hmrc.emcstfe.config.Constants
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.common.DestinationType.{DirectDelivery, RegisteredConsignee, TaxWarehouse, TemporaryRegisteredConsignee}
import uk.gov.hmrc.emcstfe.models.common.TraderModel
import uk.gov.hmrc.emcstfe.models.reportOfReceipt.SubmitReportOfReceiptModel

case class SubmitReportOfReceiptRequest(body: SubmitReportOfReceiptModel)
                                       (implicit request: UserRequest[_]) extends ChrisRequest with SoapEnvelope {

  private val arcCountryCode = body.arc.substring(2, 4)
  private val traderModelCountryCode: Option[TraderModel] => String = _.flatMap(_.countryCode).getOrElse(Constants.GB)

  val messageSender =
    Constants.NDEA ++ (if (body.destinationType == DirectDelivery) {
      traderModelCountryCode(body.consigneeTrader)
    } else {
      arcCountryCode
    })

  val messageRecipient =
    Constants.NDEA ++ (body.destinationType match {
      case TaxWarehouse => traderModelCountryCode(body.deliveryPlaceTrader)
      case TemporaryRegisteredConsignee | RegisteredConsignee => traderModelCountryCode(body.consigneeTrader)
      case DirectDelivery => arcCountryCode
      case _ => Constants.GB
    })

  override def exciseRegistrationNumber: String = request.ern

  override def requestBody: String =
    withSoapEnvelope(
      body = body,
      messageNumber = 818,
      messageSender = messageSender,
      messageRecipient = messageRecipient
    ).toString()

  override def action: String = "http://www.hmrc.gov.uk/emcs/submitreportofreceiptportal"

  override def shouldExtractFromSoap: Boolean = false

  override def metricName: String = "report-receipt"
}
