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
import uk.gov.hmrc.emcstfe.models.common.SubmitterType
import uk.gov.hmrc.emcstfe.models.explainShortageExcess.SubmitExplainShortageExcessModel
import uk.gov.hmrc.emcstfe.models.request.chris.ChrisRequest

case class SubmitExplainShortageExcessRequest(body: SubmitExplainShortageExcessModel)
                                       (implicit request: UserRequest[_]) extends ChrisRequest with SoapEnvelope {
  override def exciseRegistrationNumber: String = request.ern

  val messageRecipient: String = {
    val countryCode: String = body.attributes.submitterType match {
      case SubmitterType.Consignor => body.consigneeTrader.flatMap(_.countryCode).getOrElse(Constants.GB)
      case SubmitterType.Consignee => body.exciseMovement.arc.substring(2, 4)
    }

    Constants.NDEA ++ countryCode
  }
  val messageSender: String = {
    val countryCode: String = body.attributes.submitterType match {
      case SubmitterType.Consignor => body.exciseMovement.arc.substring(2, 4)
      case SubmitterType.Consignee => body.consigneeTrader.flatMap(_.countryCode).getOrElse(Constants.GB)
    }

    Constants.NDEA ++ countryCode
  }

  override def requestBody: String =
    withSoapEnvelope(
      body = body,
      messageNumber = 871,
      messageSender = messageSender,
      messageRecipient = messageRecipient
    ).toString()

  override def action: String = "http://www.hmrc.gov.uk/emcs/submitreasonforshortageportal"

  override def shouldExtractFromSoap: Boolean = false

  override def metricName = "explain-shortage-excess"
}
