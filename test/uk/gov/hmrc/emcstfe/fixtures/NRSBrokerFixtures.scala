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

package uk.gov.hmrc.emcstfe.fixtures

import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.auth.core.AffinityGroup.Organisation
import uk.gov.hmrc.auth.core.retrieve.{AgentInformation, ItmpAddress, ItmpName, LoginTimes}
import uk.gov.hmrc.auth.core.{ConfidenceLevel, User}
import uk.gov.hmrc.emcstfe.models.nrs.NotableEvent.CreateMovementNotableEvent
import uk.gov.hmrc.emcstfe.models.nrs.{IdentityData, NRSMetadata, NRSPayload, SearchKeys}
import uk.gov.hmrc.emcstfe.models.response.nrsBroker.NRSBrokerInsertPayloadResponse

import java.time.Instant

trait NRSBrokerFixtures extends BaseFixtures {

  val identityDataModel: IdentityData = IdentityData(
    confidenceLevel = ConfidenceLevel.L200,
    agentInformation = AgentInformation(Some("agentId"), Some("agentCode"), Some("agentFriendlyName")),
    itmpName = ItmpName(None, None, None),
    itmpAddress = ItmpAddress(None, None, None, None, None, None, None, None),
    loginTimes = LoginTimes(Instant.ofEpochMilli(1L), None),
    credentialRole = Some(User),
    affinityGroup = Some(Organisation)
  )

  /* Payload:
    {
      "testing": "emcs-tfe",
      "version": "1"
    }
 */

  val nrsMetadataModel: NRSMetadata = NRSMetadata(
    businessId = "emcs",
    notableEvent = CreateMovementNotableEvent,
    payloadContentType = "application/json",
    payloadSha256Checksum = "80298ad82661b0744d95cd969f782fdde6db73e92d25f52ef0b77b803bfbf4d9",
    userSubmissionTimestamp = Instant.ofEpochMilli(1L),
    identityData = identityDataModel,
    userAuthToken = "Bearer token",
    headerData = Json.obj("key" -> "value"),
    searchKeys = SearchKeys(testErn)
  )

  val nrsMetadataJson: JsValue = Json.obj(
    "businessId" -> "emcs",
    "notableEvent" -> "emcs-create-a-movement-ui",
    "payloadContentType" -> "application/json",
    "payloadSha256Checksum" -> "80298ad82661b0744d95cd969f782fdde6db73e92d25f52ef0b77b803bfbf4d9",
    "userSubmissionTimestamp" -> "1970-01-01T00:00:00.001Z",
    "identityData" -> Json.toJson(identityDataModel),
    "userAuthToken" -> "Bearer token",
    "headerData" -> Json.obj("key" -> "value"),
    "searchKeys" -> Json.obj("ern" -> testErn)
  )

  val nrsPayloadModel: NRSPayload = NRSPayload(payload = "ewogICAgInRlc3RpbmciOiAiZW1jcy10ZmUiLAogICAgInZlcnNpb24iOiAiMSIKfQ==", metadata = nrsMetadataModel)

  val nrsPayloadJson: JsValue = Json.obj(
    "payload" -> "ewogICAgInRlc3RpbmciOiAiZW1jcy10ZmUiLAogICAgInZlcnNpb24iOiAiMSIKfQ==",
    "metadata" -> nrsMetadataJson
  )

  val nrsBrokerResponseModel: NRSBrokerInsertPayloadResponse = NRSBrokerInsertPayloadResponse("ref1")
}
