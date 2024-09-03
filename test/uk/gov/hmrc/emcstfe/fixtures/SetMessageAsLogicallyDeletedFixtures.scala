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
import uk.gov.hmrc.emcstfe.models.response.SetMessageAsLogicallyDeletedResponse

trait SetMessageAsLogicallyDeletedFixtures extends BaseFixtures {

  val setMessageAsLogicallyDeletedDownstreamJson: JsValue = Json.obj(
    "recordsAffected" -> 1
  )
  val setMessageAsLogicallyDeletedResponseModel: SetMessageAsLogicallyDeletedResponse = SetMessageAsLogicallyDeletedResponse(
    recordsAffected = 1
  )
  val setMessageAsLogicallyDeletedJson: JsValue = Json.obj(
    "recordsAffected" -> 1
  )
}
