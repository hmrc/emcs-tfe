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
import uk.gov.hmrc.emcstfe.models.explainDelay.SubmitExplainDelayModel
import uk.gov.hmrc.emcstfe.models.request.eis.{EisMessage, EisSubmissionRequest, Source}

import java.util.Base64

case class SubmitExplainDelayRequest(body: SubmitExplainDelayModel)
                                    (implicit request: UserRequest[_]) extends EisSubmissionRequest with EisMessage {
  override def exciseRegistrationNumber: String = request.ern

  private val arcCountryCode = body.arc.substring(2, 4)
  private val ernCountryCode = exciseRegistrationNumber.substring(0, 2)
  private val messageNumber = 837

  val messageRecipient = Constants.NDEA ++ arcCountryCode
  val messageSender: String = Constants.NDEA ++ ernCountryCode

  override def metricName = "explain-delay"

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
