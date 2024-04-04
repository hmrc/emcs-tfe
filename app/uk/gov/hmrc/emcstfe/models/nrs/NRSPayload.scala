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

import play.api.libs.json.{JsObject, JsString, Json, Writes}
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.utils.SHA256Hashing
import uk.gov.hmrc.http.HeaderCarrier

import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.Base64

case class NRSPayload(payload: String, metadata: NRSMetadata)

object NRSPayload {

  implicit val writes: Writes[NRSPayload] = Json.writes[NRSPayload]

  def apply(payload: String, notableEvent: NotableEvent, identityData: IdentityData, ern: String)
           (implicit hc: HeaderCarrier, userRequest: UserRequest[_]): NRSPayload = {
    NRSPayload(
      payload = Base64.getEncoder.encodeToString(payload.getBytes(StandardCharsets.UTF_8)),
      metadata = NRSMetadata(
        businessId = "emcs",
        notableEvent = notableEvent,
        payloadContentType = "application/json",
        payloadSha256Checksum = SHA256Hashing.getHash(payload),
        userSubmissionTimestamp = Instant.now,
        identityData = identityData,
        userAuthToken = hc.authorization.get.value,
        headerData = JsObject(userRequest.request.headers.toMap.map(kv => kv._1 -> JsString(kv._2 mkString ","))),
        searchKeys = SearchKeys(ern)
      )
    )
  }
}
