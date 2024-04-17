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

import play.api.libs.json.{JsObject, JsValue, Json, Writes}
import uk.gov.hmrc.auth.core.AffinityGroup.Organisation
import uk.gov.hmrc.auth.core.ConfidenceLevel
import uk.gov.hmrc.auth.core.retrieve._
import uk.gov.hmrc.emcstfe.models.nrs.NotableEvent._
import uk.gov.hmrc.emcstfe.models.nrs.{IdentityData, NRSMetadata, NRSPayload, NRSSubmission, SearchKeys}
import uk.gov.hmrc.emcstfe.models.response.nrsBroker.NRSBrokerInsertPayloadResponse
import uk.gov.hmrc.emcstfe.utils.SHA256Hashing

import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.Base64

trait NRSBrokerFixtures extends CreateMovementFixtures with BaseFixtures {

  val identityDataModel: IdentityData = IdentityData(
    internalId = Some(testInternalId),
    externalId = Some("externalId"),
    confidenceLevel = ConfidenceLevel.L200,
    agentInformation = AgentInformation(Some("agentId"), Some("agentCode"), Some("agentFriendlyName")),
    itmpName = None,
    itmpAddress = None,
    loginTimes = LoginTimes(Instant.ofEpochMilli(1L), None),
    credentialRole = None,
    affinityGroup = Some(Organisation),
    credentials = Some(Credentials(providerId = testCredId, providerType = "gg"))
  )

  val predicateRetrieval = new ~(new ~(new ~(new ~(new ~(new ~(new ~(new ~(new ~(new ~(new ~(new ~(new ~(new ~(new ~(new ~(new ~(
    Some(Organisation),
    Some(testInternalId)),
    Some("externalId")),
    None),
    Some(Credentials(providerId = testCredId, providerType = "gg"))),
    ConfidenceLevel.L200),
    None),
    None),
    None),
    None),
    AgentInformation(Some("agentId"), Some("agentCode"), Some("agentFriendlyName"))),
    None),
    None),
    None),
    None),
    None),
    None),
    LoginTimes(Instant.ofEpochMilli(1L), None)
  )

  val testJsonPayload: JsObject = Json.obj(
    "testing" -> "emcs-tfe",
    "version" -> "1"
  )

  val testPlainTextPayload: String =
    """{
      |    "testing": "emcs-tfe",
      |    "version": "1"
      |}""".stripMargin

  val nrsMetadataModel: NRSMetadata = NRSMetadata(
    businessId = "emcs",
    notableEvent = CreateMovementNotableEvent,
    payloadContentType = "application/json",
    payloadSha256Checksum = "80298ad82661b0744d95cd969f782fdde6db73e92d25f52ef0b77b803bfbf4d9",
    userSubmissionTimestamp = Instant.ofEpochMilli(1L),
    identityData = identityDataModel,
    userAuthToken = testAuthToken,
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
    "userAuthToken" -> testAuthToken,
    "headerData" -> Json.obj("key" -> "value"),
    "searchKeys" -> Json.obj("ern" -> testErn)
  )

  val nrsPayloadModel: NRSPayload = NRSPayload(payload = "ewogICAgInRlc3RpbmciOiAiZW1jcy10ZmUiLAogICAgInZlcnNpb24iOiAiMSIKfQ==", metadata = nrsMetadataModel)

  val nrsPayloadJson: JsValue = Json.obj(
    "payload" -> "ewogICAgInRlc3RpbmciOiAiZW1jcy10ZmUiLAogICAgInZlcnNpb24iOiAiMSIKfQ==",
    "metadata" -> nrsMetadataJson
  )

  val nrsBrokerResponseModel: NRSBrokerInsertPayloadResponse = NRSBrokerInsertPayloadResponse("ref1")

  val nrsBrokerResponseJson: JsValue = Json.obj("reference" -> "ref1")

  def createNRSPayload[A <: NRSSubmission](model: A)(implicit writes: Writes[A]): NRSPayload = {
    val payload = Json.stringify(Json.toJson(model))
    nrsPayloadModel.copy(
      payload = Base64.getEncoder.encodeToString(payload.getBytes(StandardCharsets.UTF_8)),
      metadata = nrsMetadataModel.copy(
        payloadSha256Checksum = SHA256Hashing.getHash(payload),
        notableEvent = model.notableEvent,
        headerData = Json.obj("Host" -> "localhost"),
        userAuthToken = testAuthToken
      )
    )
  }
}
