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

import play.api.libs.json.{JsObject, JsValue, Json}
import uk.gov.hmrc.auth.core.AffinityGroup.Organisation
import uk.gov.hmrc.auth.core.ConfidenceLevel
import uk.gov.hmrc.auth.core.retrieve._
import uk.gov.hmrc.emcstfe.models.nrs.NotableEvent._
import uk.gov.hmrc.emcstfe.models.nrs.{IdentityData, NRSMetadata, NRSPayload, SearchKeys}
import uk.gov.hmrc.emcstfe.models.response.nrsBroker.NRSBrokerInsertPayloadResponse

import java.time.Instant

trait NRSBrokerFixtures extends BaseFixtures {

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

  val nrsBrokerResponseJson: JsValue = Json.obj("reference" -> "ref1")

  // Specific journey payloads

  val alertRejectNRSPayload: NRSPayload = nrsPayloadModel.copy(
    payload = "eyJhcmMiOiIyM0dCMDAwMDAwMDAwMDAzNzY5NjciLCJzZXF1ZW5jZU51bWJlciI6MSwiY29uc2lnbmVlVHJhZGVyIjp7InRyYWRlckV4Y2lzZU51bWJlciI6IkdCMDAwMDAwMDAxMjM0NiIsInRyYWRlck5hbWUiOiJuYW1lIiwiYWRkcmVzcyI6eyJzdHJlZXROdW1iZXIiOiJudW1iZXIiLCJzdHJlZXQiOiJzdHJlZXQiLCJwb3N0Y29kZSI6InBvc3Rjb2RlIiwiY2l0eSI6ImNpdHkifSwiZW9yaU51bWJlciI6ImVvcmkifSwiZXhjaXNlTW92ZW1lbnQiOnsiYXJjIjoiMjNHQjAwMDAwMDAwMDAwMzc2OTY3Iiwic2VxdWVuY2VOdW1iZXIiOjF9LCJkZXN0aW5hdGlvbk9mZmljZSI6IkdCMTIzNCIsImRhdGVPZkFsZXJ0T3JSZWplY3Rpb24iOiIyMDIzLTA3LTI0IiwiaXNSZWplY3RlZCI6dHJ1ZSwiYWxlcnRPclJlamVjdGlvblJlYXNvbnMiOlt7InJlYXNvbiI6IjEiLCJhZGRpdGlvbmFsSW5mb3JtYXRpb24iOiJmb28ifSx7InJlYXNvbiI6IjIifV19",
    metadata = nrsMetadataModel.copy(
      payloadSha256Checksum = "194faf8f248499edce72e13cdd8a6f1cd84521ea82dfb998ef34edb6bd4a5f14",
      notableEvent = AlertRejectNotableEvent,
      headerData = Json.obj("Host" -> "localhost")
    )
  )

  val shortageExcessNRSPayload: NRSPayload = nrsPayloadModel.copy(
    payload = "eyJlcm4iOiJHQldLMDAwMDAxMjM0IiwiYXJjIjoiMDFERTAwMDAwMTIzNDUiLCJzZXF1ZW5jZU51bWJlciI6MSwic3VibWl0dGVyVHlwZSI6IjEiLCJjb25zaWdub3JUcmFkZXIiOnsidHJhZGVyRXhjaXNlTnVtYmVyIjoiR0IwMDAwMDAwMDEyMzQ2IiwidHJhZGVyTmFtZSI6Im5hbWUiLCJhZGRyZXNzIjp7InN0cmVldE51bWJlciI6Im51bWJlciIsInN0cmVldCI6InN0cmVldCIsInBvc3Rjb2RlIjoicG9zdGNvZGUiLCJjaXR5IjoiY2l0eSJ9fSwiaW5kaXZpZHVhbEl0ZW1zIjpbeyJleGNpc2VQcm9kdWN0Q29kZSI6ImNvZGUiLCJib2R5UmVjb3JkVW5pcXVlUmVmZXJlbmNlIjoxLCJleHBsYW5hdGlvbiI6ImV4cGxhbmF0aW9uIiwiYWN0dWFsUXVhbnRpdHkiOjMuMn0seyJleGNpc2VQcm9kdWN0Q29kZSI6ImNvZGUiLCJib2R5UmVjb3JkVW5pcXVlUmVmZXJlbmNlIjoyLCJleHBsYW5hdGlvbiI6ImV4cGxhbmF0aW9uIn1dLCJkYXRlT2ZBbmFseXNpcyI6ImRhdGUiLCJnbG9iYWxFeHBsYW5hdGlvbiI6ImV4cGxhbmF0aW9uIn0=",
    metadata = nrsMetadataModel.copy(
      payloadSha256Checksum = "2769dee412ff9754f2f1150bdfa247a0c10e61861373b2bc71543dba81969f45",
      notableEvent = ExplainShortageOrExcessNotableEvent,
      headerData = Json.obj("Host" -> "localhost")
    )
  )

  val explainDelayNRSPayload: NRSPayload = nrsPayloadModel.copy(
    payload = "eyJhcmMiOiIyM0dCMDAwMDAwMDAwMDAzNzY5NjciLCJzZXF1ZW5jZU51bWJlciI6MSwic3VibWl0dGVyVHlwZSI6IjIiLCJkZWxheVR5cGUiOiIxIiwiZGVsYXlSZWFzb25UeXBlIjoiNSIsImFkZGl0aW9uYWxJbmZvcm1hdGlvbiI6Im90aGVyIn0=",
    metadata = nrsMetadataModel.copy(
      payloadSha256Checksum = "0e97d45bda0498e2f53d6d0f332c5c0fe69f0cf8cc038dcd2d0e7d4bbf1750da",
      notableEvent = ExplainDelayNotableEvent,
      headerData = Json.obj("Host" -> "localhost")
    )
  )
}
