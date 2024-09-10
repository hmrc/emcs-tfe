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

package uk.gov.hmrc.emcstfe.models.request.eis.preValidate

import play.api.libs.json.{Format, JsValue, Json}
import uk.gov.hmrc.emcstfe.models.request.eis.EisSubmissionRequest


case class Product(exciseProductCode: String)

case class ValidateProductAuthorisationRequest(product: Product)

case class ExciseTraderRequest(
                                exciseRegistrationNumber: String,
                                entityGroup: String,
                                validateProductAuthorisationRequest: Seq[ValidateProductAuthorisationRequest]
                              )
case class ExciseTraderValidationRequest(exciseTraderRequest: ExciseTraderRequest)


case class PreValidateRequest(exciseTraderValidationRequest: ExciseTraderValidationRequest) extends EisSubmissionRequest {

  override def metricName: String = "pre-validate-trader"

  override def eisXMLBody(): String = "" // deliberate as EIS does not support XML, only JSON for pre-validate

  override def toJson: JsValue = Json.toJson(this)

  override def exciseRegistrationNumber: String = exciseTraderValidationRequest.exciseTraderRequest.exciseRegistrationNumber
}

case class PreValidateETDS12Request(exciseId: String, entityGroup: Option[String], products: Option[Seq[Product]]) extends EisSubmissionRequest {

  override def metricName: String = "pre-validate-trader"

  override def eisXMLBody(): String = "" // deliberate as EIS does not support XML, only JSON for pre-validate

  override def toJson: JsValue = Json.toJson(this)

  override def exciseRegistrationNumber: String = exciseId
}

object Product {
  implicit val format: Format[Product] = Json.format
}

object ValidateProductAuthorisationRequest {
  implicit val format: Format[ValidateProductAuthorisationRequest] = Json.format
}

object ExciseTraderRequest {
  implicit val format: Format[ExciseTraderRequest] = Json.format
}

object ExciseTraderValidationRequest {
  implicit val format: Format[ExciseTraderValidationRequest] = Json.format
}

object PreValidateRequest {
  implicit val format: Format[PreValidateRequest] = Json.format
}

object PreValidateETDS12Request {
  implicit val format: Format[PreValidateETDS12Request] = Json.format
}
