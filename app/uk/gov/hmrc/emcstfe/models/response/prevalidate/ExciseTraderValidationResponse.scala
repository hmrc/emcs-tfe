/*
 * Copyright 2024 HM Revenue & Customs
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

package uk.gov.hmrc.emcstfe.models.response.prevalidate

import play.api.libs.json.{Format, Json}


case class ProductErrors(
                          exciseProductCode: String,
                          errorCode: String,
                          errorText: String
                        )

case class ValidateProductAuthorisationResponse(
                                                 valid: Boolean,
                                                 productError: Option[Seq[ProductErrors]] = None
                                               )

case class ExciseTraderResponse(
                                 exciseRegistrationNumber: String,
                                 entityGroup: String,
                                 validTrader: Boolean,
                                 errorCode: Option[String] = None,
                                 errorText: Option[String] = None,
                                 traderType: Option[String] = None,
                                 validateProductAuthorisationResponse: Option[ValidateProductAuthorisationResponse] = None
                               )

case class ExciseTraderValidationResponse (
                                            validationTimestamp: String,
                                            exciseTraderResponse: Seq[ExciseTraderResponse]
                                          )

case class PreValidateTraderApiResponse(exciseTraderValidationResponse: ExciseTraderValidationResponse)


object ProductErrors {
  implicit val format: Format[ProductErrors] = Json.format
}

object ValidateProductAuthorisationResponse {
  implicit val format: Format[ValidateProductAuthorisationResponse] = Json.format
}

object ExciseTraderResponse {
  implicit val format: Format[ExciseTraderResponse] = Json.format
}

object ExciseTraderValidationResponse {
  implicit val format: Format[ExciseTraderValidationResponse] = Json.format
}

object PreValidateTraderApiResponse {
  implicit val format: Format[PreValidateTraderApiResponse] = Json.format
}