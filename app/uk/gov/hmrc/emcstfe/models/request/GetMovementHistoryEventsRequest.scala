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

import uk.gov.hmrc.emcstfe.models.request.chris.ChrisRequest
import uk.gov.hmrc.emcstfe.models.request.eis.EisConsumptionRequest

case class GetMovementHistoryEventsRequest(exciseRegistrationNumber: String,
                                           arc: String) extends ChrisRequest with EisConsumptionRequest {

  override def requestBody: String =
    withGetRequestSoapEnvelope(
      <Parameters>
        <Parameter Name="ExciseRegistrationNumber">{exciseRegistrationNumber}</Parameter>
        <Parameter Name="ARC">{arc}</Parameter>
      </Parameters>
    )

  override def action: String = "http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/GetMovementHistoryEvents"

  override def shouldExtractFromSoap: Boolean = true

  override def metricName: String = "getMovementHistoryEvents"

  override val queryParams: Seq[(String, String)] = Seq(
    "exciseregistrationnumber" -> exciseRegistrationNumber,
    "arc" -> arc
  )
}
