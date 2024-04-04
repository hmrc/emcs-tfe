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

package uk.gov.hmrc.emcstfe.models.nrs

import play.api.libs.json.{Format, JsObject, Json}

import java.time.Instant

case class NRSMetadata(
                        businessId: String,
                        notableEvent: NotableEvent,
                        payloadContentType: String,
                        payloadSha256Checksum: String,
                        userSubmissionTimestamp: Instant = Instant.now,
                        identityData: IdentityData,
                        userAuthToken: String,
                        headerData: JsObject,
                        searchKeys: SearchKeys
                      )

object NRSMetadata {
  implicit val format: Format[NRSMetadata] = Json.format[NRSMetadata]
}
