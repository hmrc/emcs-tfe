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

package uk.gov.hmrc.emcstfe.fixtures

import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.emcstfe.models.response.MarkMessageAsReadResponse

trait MarkMessageAsReadFixtures extends BaseFixtures {

  val markMessageAsReadEisJson: JsValue = Json.obj(
    "dateTime" -> now,
    "exciseRegistrationNumber" -> testErn,
    "recordsAffected" -> 1
  )
  val markMessageAsReadResponseModel: MarkMessageAsReadResponse = MarkMessageAsReadResponse(
    recordsAffected = 1
  )
  val markMessageAsReadJson: JsValue = Json.obj(
    "recordsAffected" -> 1
  )
}
