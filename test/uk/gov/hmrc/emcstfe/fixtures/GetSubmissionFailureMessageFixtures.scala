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

import play.api.libs.json.{JsArray, JsValue, Json}
import uk.gov.hmrc.emcstfe.models.response.getSubmissionFailureMessage._
import uk.gov.hmrc.emcstfe.utils.SoapXmlFactory

import java.nio.charset.StandardCharsets
import java.util.Base64
import scala.xml.Utility.trim
import scala.xml.XML

trait GetSubmissionFailureMessageFixtures extends BaseFixtures with SoapXmlFactory {

  object IE704Xml {
    val fullXML: String =
      """
        |<p:SubmissionFailureMessageDataResponse xsi:schemaLocation="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/SubmissionFailureMessage/3 SubmissionFailureMessageData.xsd " xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:tms="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:TMS:V2.02" xmlns:p2="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/Types/3" xmlns:p1="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/EmcsUkCodes/3" xmlns:p="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/SubmissionFailureMessage/3" xmlns:ie="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/ie704uk/3" xmlns:emcs="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:EMCS:V2.02">
        |      <ie:IE704>
        |      <ie:Header>
        |      <tms:MessageSender>NDEA.XI</tms:MessageSender>
        |      <tms:MessageRecipient>NDEA.XI</tms:MessageRecipient>
        |      <tms:DateOfPreparation>2001-01-01</tms:DateOfPreparation>
        |      <tms:TimeOfPreparation>12:00:00</tms:TimeOfPreparation>
        |      <tms:MessageIdentifier>XI000001</tms:MessageIdentifier>
        |      <tms:CorrelationIdentifier>corr123</tms:CorrelationIdentifier>
        |    </ie:Header>
        |      <ie:Body>
        |      <ie:GenericRefusalMessage>
        |        <ie:Attributes>
        |      <ie:AdministrativeReferenceCode>22XI00000000000366000</ie:AdministrativeReferenceCode>
        |      <ie:SequenceNumber>1</ie:SequenceNumber>
        |      <ie:LocalReferenceNumber>lrnie8155639253</ie:LocalReferenceNumber>
        |    </ie:Attributes>
        |        <ie:FunctionalError>
        |      <ie:ErrorType>4402</ie:ErrorType>
        |      <ie:ErrorReason>Boooo! Duplicate LRN The LRN is already known and is therefore not unique according to the specified rules</ie:ErrorReason>
        |      <ie:ErrorLocation>/IE813[1]/Body[1]/SubmittedDraftOfEADESAD[1]/EadEsadDraft[1]/LocalReferenceNumber[1]</ie:ErrorLocation>
        |      <ie:OriginalAttributeValue>lrnie8155639253</ie:OriginalAttributeValue>
        |    </ie:FunctionalError><ie:FunctionalError>
        |      <ie:ErrorType>4403</ie:ErrorType>
        |      <ie:ErrorReason>Oh no! Duplicate LRN The LRN is already known and is therefore not unique according to the specified rules</ie:ErrorReason>
        |
        |
        |    </ie:FunctionalError>
        |      </ie:GenericRefusalMessage>
        |    </ie:Body>
        |    </ie:IE704>
        |      <ie:RelatedMessageType>IE815</ie:RelatedMessageType>
        |</p:SubmissionFailureMessageDataResponse>""".stripMargin

    val rawGetSubmissionFailureMessageResponse: RawGetSubmissionFailureMessageResponse = RawGetSubmissionFailureMessageResponse("dateTime", testErn, XML.loadString(fullXML))

    val rawGetSubmissionFailureMessageResponseJson: JsValue = Json.obj(
      "dateTime" -> "dateTime",
      "exciseRegistrationNumber" -> testErn,
      "message" -> Base64.getEncoder.encodeToString(fullXML.getBytes)
    )

    val rawGetSubmissionFailureMessageResponseInvalidJson: JsValue = Json.obj(
      "dateTime" -> "dateTime",
      "exciseRegistrationNumber" -> testErn,
      "message" -> "AB123456ZZ"
    )
  }

  object IE704HeaderFixtures {
    val ie704HeaderXmlBody: String =
      s"""
        |<ie:Header>
        |  <tms:MessageSender>NDEA.XI</tms:MessageSender>
        |  <tms:MessageRecipient>NDEA.XI</tms:MessageRecipient>
        |  <tms:DateOfPreparation>2001-01-01</tms:DateOfPreparation>
        |  <tms:TimeOfPreparation>12:00:00</tms:TimeOfPreparation>
        |  <tms:MessageIdentifier>XI000001</tms:MessageIdentifier>
        |  <tms:CorrelationIdentifier>$testDraftId</tms:CorrelationIdentifier>
        |</ie:Header>
        |""".stripMargin

    val ie704HeaderModel: IE704Header = IE704Header(
      messageSender = "NDEA.XI",
      messageRecipient = "NDEA.XI",
      dateOfPreparation = "2001-01-01",
      timeOfPreparation = "12:00:00",
      messageIdentifier = "XI000001",
      correlationIdentifier = Some(testDraftId)
    )

    val ie704HeaderJson: JsValue = Json.obj(
      "messageSender" -> "NDEA.XI",
      "messageRecipient" -> "NDEA.XI",
      "dateOfPreparation" -> "2001-01-01",
      "timeOfPreparation" -> "12:00:00",
      "messageIdentifier" -> "XI000001",
      "correlationIdentifier" -> testDraftId
    )

    val ie704HeaderMinimumXmlBody: String =
      """
        |<ie:Header>
        |  <tms:MessageSender>NDEA.XI</tms:MessageSender>
        |  <tms:MessageRecipient>NDEA.XI</tms:MessageRecipient>
        |  <tms:DateOfPreparation>2001-01-01</tms:DateOfPreparation>
        |  <tms:TimeOfPreparation>12:00:00</tms:TimeOfPreparation>
        |  <tms:MessageIdentifier>XI000001</tms:MessageIdentifier>
        |</ie:Header>
        |""".stripMargin

    val ie704HeaderMinimumModel: IE704Header = IE704Header(
      messageSender = "NDEA.XI",
      messageRecipient = "NDEA.XI",
      dateOfPreparation = "2001-01-01",
      timeOfPreparation = "12:00:00",
      messageIdentifier = "XI000001",
      correlationIdentifier = None
    )

    val ie704HeaderMinimumJson: JsValue = Json.obj(
      "messageSender" -> "NDEA.XI",
      "messageRecipient" -> "NDEA.XI",
      "dateOfPreparation" -> "2001-01-01",
      "timeOfPreparation" -> "12:00:00",
      "messageIdentifier" -> "XI000001"
    )
  }

  object IE704AttributesFixtures {
    val ie704AttributesXmlBody: String =
      """
        |<ie:Attributes>
        |  <ie:AdministrativeReferenceCode>22XI00000000000366000</ie:AdministrativeReferenceCode>
        |  <ie:SequenceNumber>1</ie:SequenceNumber>
        |  <ie:LocalReferenceNumber>lrnie8155639253</ie:LocalReferenceNumber>
        |</ie:Attributes>
        |""".stripMargin

    val ie704AttributesModel: IE704Attributes = IE704Attributes(
      arc = Some("22XI00000000000366000"),
      sequenceNumber = Some(1),
      lrn = Some("lrnie8155639253")
    )

    val ie704AttributesJson: JsValue = Json.obj(
      "arc" -> "22XI00000000000366000",
      "sequenceNumber" -> 1,
      "lrn" -> "lrnie8155639253",
    )

    val ie704AttributesMinimumModel: IE704Attributes = IE704Attributes(
      arc = None,
      sequenceNumber = None,
      lrn = None
    )
  }

  object IE704FunctionalErrorFixtures {
    val ie704FunctionalErrorXmlBody: String =
      """
        |<ie:FunctionalError>
        |  <ie:ErrorType>4402</ie:ErrorType>
        |  <ie:ErrorReason>Boooo! Duplicate LRN The LRN is already known and is therefore not unique according to the specified rules</ie:ErrorReason>
        |  <ie:ErrorLocation>/IE813[1]/Body[1]/SubmittedDraftOfEADESAD[1]/EadEsadDraft[1]/LocalReferenceNumber[1]</ie:ErrorLocation>
        |  <ie:OriginalAttributeValue>lrnie8155639253</ie:OriginalAttributeValue>
        |</ie:FunctionalError>
        |""".stripMargin

    val ie704FunctionalErrorInvalidErrorTypeXmlBody: String =
      """
        |<ie:FunctionalError>
        |  <ie:ErrorType>1</ie:ErrorType>
        |  <ie:ErrorReason>Boooo! Duplicate LRN The LRN is already known and is therefore not unique according to the specified rules</ie:ErrorReason>
        |  <ie:ErrorLocation>/IE813[1]/Body[1]/SubmittedDraftOfEADESAD[1]/EadEsadDraft[1]/LocalReferenceNumber[1]</ie:ErrorLocation>
        |  <ie:OriginalAttributeValue>lrnie8155639253</ie:OriginalAttributeValue>
        |</ie:FunctionalError>
        |""".stripMargin

    val ie704FunctionalErrorModel: IE704FunctionalError = IE704FunctionalError(
      errorType = "4402",
      errorReason = "Boooo! Duplicate LRN The LRN is already known and is therefore not unique according to the specified rules",
      errorLocation = Some("/IE813[1]/Body[1]/SubmittedDraftOfEADESAD[1]/EadEsadDraft[1]/LocalReferenceNumber[1]"),
      originalAttributeValue = Some("lrnie8155639253")
    )

    val ie704FunctionalErrorJson: JsValue = Json.obj(
      "errorType" -> "4402",
      "errorReason" -> "Boooo! Duplicate LRN The LRN is already known and is therefore not unique according to the specified rules",
      "errorLocation" -> "/IE813[1]/Body[1]/SubmittedDraftOfEADESAD[1]/EadEsadDraft[1]/LocalReferenceNumber[1]",
      "originalAttributeValue" -> "lrnie8155639253"
    )

    val ie704FunctionalErrorMinimumXmlBody: String =
      """
        |<ie:FunctionalError>
        |  <ie:ErrorType>4402</ie:ErrorType>
        |  <ie:ErrorReason>Boooo! Duplicate LRN The LRN is already known and is therefore not unique according to the specified rules</ie:ErrorReason>
        |</ie:FunctionalError>
        |""".stripMargin

    val ie704FunctionalErrorMinimumModel: IE704FunctionalError = IE704FunctionalError(
      errorType = "4402",
      errorReason = "Boooo! Duplicate LRN The LRN is already known and is therefore not unique according to the specified rules",
      errorLocation = None,
      originalAttributeValue = None
    )

    val ie704FunctionalErrorMinimumJson: JsValue = Json.obj(
      "errorType" -> "4402",
      "errorReason" -> "Boooo! Duplicate LRN The LRN is already known and is therefore not unique according to the specified rules"
    )
  }

  object IE704BodyFixtures {
    val ie704BodyXmlBody: String =
      """
        |<ie:Body>
        |  <ie:GenericRefusalMessage>
        |    <ie:Attributes>
        |      <ie:AdministrativeReferenceCode>22XI00000000000366000</ie:AdministrativeReferenceCode>
        |      <ie:SequenceNumber>1</ie:SequenceNumber>
        |      <ie:LocalReferenceNumber>lrnie8155639253</ie:LocalReferenceNumber>
        |    </ie:Attributes>
        |    <ie:FunctionalError>
        |      <ie:ErrorType>4402</ie:ErrorType>
        |      <ie:ErrorReason>Boooo! Duplicate LRN The LRN is already known and is therefore not unique according to the specified rules</ie:ErrorReason>
        |      <ie:ErrorLocation>/IE813[1]/Body[1]/SubmittedDraftOfEADESAD[1]/EadEsadDraft[1]/LocalReferenceNumber[1]</ie:ErrorLocation>
        |      <ie:OriginalAttributeValue>lrnie8155639253</ie:OriginalAttributeValue>
        |    </ie:FunctionalError>
        |    <ie:FunctionalError>
        |      <ie:ErrorType>4403</ie:ErrorType>
        |      <ie:ErrorReason>Oh no! Duplicate LRN The LRN is already known and is therefore not unique according to the specified rules</ie:ErrorReason>
        |      <ie:ErrorLocation>/IE813[1]/Body[1]/SubmittedDraftOfEADESAD[1]/EadEsadDraft[1]/LocalReferenceNumber[1]</ie:ErrorLocation>
        |      <ie:OriginalAttributeValue>lrnie8155639254</ie:OriginalAttributeValue>
        |    </ie:FunctionalError>
        |  </ie:GenericRefusalMessage>
        |</ie:Body>
        |""".stripMargin
        
    val ie704BodyModel: IE704Body = IE704Body(
      attributes = Some(IE704AttributesFixtures.ie704AttributesModel),
      functionalError = Seq(
        IE704FunctionalErrorFixtures.ie704FunctionalErrorModel,
        IE704FunctionalError(
          errorType = "4403",
          errorReason = "Oh no! Duplicate LRN The LRN is already known and is therefore not unique according to the specified rules",
          errorLocation = Some("/IE813[1]/Body[1]/SubmittedDraftOfEADESAD[1]/EadEsadDraft[1]/LocalReferenceNumber[1]"),
          originalAttributeValue = Some("lrnie8155639254")
        )
      )
    )
    
    val ie704BodyJson: JsValue = Json.obj(
      "attributes" -> IE704AttributesFixtures.ie704AttributesJson,
      "functionalError" -> JsArray(Seq(
        IE704FunctionalErrorFixtures.ie704FunctionalErrorJson,
        Json.obj(
          "errorType" -> "4403",
          "errorReason" -> "Oh no! Duplicate LRN The LRN is already known and is therefore not unique according to the specified rules",
          "errorLocation" -> "/IE813[1]/Body[1]/SubmittedDraftOfEADESAD[1]/EadEsadDraft[1]/LocalReferenceNumber[1]",
          "originalAttributeValue" -> "lrnie8155639254"
        )
      ))
    )
    
    val ie704BodyEmptyAttributesXmlBody: String =
      """
        |<ie:Body>
        |  <ie:GenericRefusalMessage>
        |    <ie:Attributes>
        |    </ie:Attributes>
        |    <ie:FunctionalError>
        |      <ie:ErrorType>4402</ie:ErrorType>
        |      <ie:ErrorReason>Boooo! Duplicate LRN The LRN is already known and is therefore not unique according to the specified rules</ie:ErrorReason>
        |      <ie:ErrorLocation>/IE813[1]/Body[1]/SubmittedDraftOfEADESAD[1]/EadEsadDraft[1]/LocalReferenceNumber[1]</ie:ErrorLocation>
        |      <ie:OriginalAttributeValue>lrnie8155639253</ie:OriginalAttributeValue>
        |    </ie:FunctionalError>
        |    <ie:FunctionalError>
        |      <ie:ErrorType>4403</ie:ErrorType>
        |      <ie:ErrorReason>Oh no! Duplicate LRN The LRN is already known and is therefore not unique according to the specified rules</ie:ErrorReason>
        |      <ie:ErrorLocation>/IE813[1]/Body[1]/SubmittedDraftOfEADESAD[1]/EadEsadDraft[1]/LocalReferenceNumber[1]</ie:ErrorLocation>
        |      <ie:OriginalAttributeValue>lrnie8155639254</ie:OriginalAttributeValue>
        |    </ie:FunctionalError>
        |  </ie:GenericRefusalMessage>
        |</ie:Body>
        |""".stripMargin
        
    val ie704BodyEmptyAttributesModel: IE704Body = IE704Body(
      attributes = None,
      functionalError = Seq(
        IE704FunctionalErrorFixtures.ie704FunctionalErrorModel,
        IE704FunctionalError(
          errorType = "4403",
          errorReason = "Oh no! Duplicate LRN The LRN is already known and is therefore not unique according to the specified rules",
          errorLocation = Some("/IE813[1]/Body[1]/SubmittedDraftOfEADESAD[1]/EadEsadDraft[1]/LocalReferenceNumber[1]"),
          originalAttributeValue = Some("lrnie8155639254")
        )
      )
    )
    
    val ie704BodyEmptyAttributesJson: JsValue = Json.obj(
      "functionalError" -> JsArray(Seq(
        IE704FunctionalErrorFixtures.ie704FunctionalErrorJson,
        Json.obj(
          "errorType" -> "4403",
          "errorReason" -> "Oh no! Duplicate LRN The LRN is already known and is therefore not unique according to the specified rules",
          "errorLocation" -> "/IE813[1]/Body[1]/SubmittedDraftOfEADESAD[1]/EadEsadDraft[1]/LocalReferenceNumber[1]",
          "originalAttributeValue" -> "lrnie8155639254"
        )
      ))
    )
  }

  object IE704ModelFixtures {
    val ie704ModelXmlBody: String =
      s"""
        |<ie:IE704>
        |  ${IE704HeaderFixtures.ie704HeaderXmlBody}
        |  ${IE704BodyFixtures.ie704BodyXmlBody}
        |</ie:IE704>
        |""".stripMargin
        
    val ie704ModelModel: IE704Model = IE704Model(
      header = IE704HeaderFixtures.ie704HeaderModel,
      body = IE704BodyFixtures.ie704BodyModel
    )
        
    val ie704ModelJson: JsValue = Json.obj(
      "header" -> IE704HeaderFixtures.ie704HeaderJson,
      "body" -> IE704BodyFixtures.ie704BodyJson
    )
  }

  object GetSubmissionFailureMessageResponseFixtures {

    val submissionFailureMessageDataXmlBody: String =
      s"""
         |<p:SubmissionFailureMessageDataResponse xmlns:emcs="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:EMCS:V2.02" xmlns:ie="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/ie704uk/3" xmlns:p="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/SubmissionFailureMessage/3" xmlns:p1="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/EmcsUkCodes/3" xmlns:p2="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/Types/3" xmlns:tms="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:TMS:V2.02" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/SubmissionFailureMessage/3 SubmissionFailureMessageData.xsd ">
         |  ${IE704ModelFixtures.ie704ModelXmlBody}
         |  <ie:RelatedMessageType>IE815</ie:RelatedMessageType>
         |</p:SubmissionFailureMessageDataResponse>
         |""".stripMargin

    val submissionFailureMessageDataNoRelatedMessageTypeXmlBody: String =
      s"""
         |<p:SubmissionFailureMessageDataResponse xmlns:emcs="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:EMCS:V2.02" xmlns:ie="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/ie704uk/3" xmlns:p="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/SubmissionFailureMessage/3" xmlns:p1="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/EmcsUkCodes/3" xmlns:p2="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/Types/3" xmlns:tms="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:TMS:V2.02" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/SubmissionFailureMessage/3 SubmissionFailureMessageData.xsd ">
         |  ${IE704ModelFixtures.ie704ModelXmlBody}
         |  <ie:RelatedMessageType></ie:RelatedMessageType>
         |</p:SubmissionFailureMessageDataResponse>
         |""".stripMargin

    val getSubmissionFailureMessageResponseDownstreamJson: JsValue = Json.obj(
      "dateTime" -> "now",
      "exciseRegistrationNumber" -> testErn,
      "message" ->
        Base64.getEncoder.encodeToString(trim(XML.loadString(submissionFailureMessageDataXmlBody)).toString().getBytes(StandardCharsets.UTF_8))
    )

    val getSubmissionFailureMessageResponseDownstreamJsonWrongEncoding: JsValue = Json.obj(
      "dateTime" -> "now",
      "exciseRegistrationNumber" -> testErn,
      "message" ->
        Base64.getEncoder.encodeToString(trim(XML.loadString(submissionFailureMessageDataXmlBody)).toString().getBytes(StandardCharsets.UTF_16))
    )

    val getSubmissionFailureMessageResponseDownstreamJsonNotEncoded: JsValue = Json.obj(
      "dateTime" -> "now",
      "exciseRegistrationNumber" -> testErn,
      "message" ->
        trim(XML.loadString(submissionFailureMessageDataXmlBody)).toString()
    )

    val getSubmissionFailureMessageResponseDownstreamJsonBadXml: JsValue = Json.obj(
      "dateTime" -> "now",
      "exciseRegistrationNumber" -> testErn,
      "message" -> Base64.getEncoder.encodeToString(trim(<Message>Success!</Message>).toString().getBytes(StandardCharsets.UTF_8))
    )

    val getSubmissionFailureMessageResponseModel: GetSubmissionFailureMessageResponse = GetSubmissionFailureMessageResponse(
      ie704 = IE704ModelFixtures.ie704ModelModel,
      relatedMessageType = Some("IE815")
    )

    def getSubmissionFailureMessageResponseJson(isTFESubmission: Boolean): JsValue = Json.obj(
      "ie704" -> IE704ModelFixtures.ie704ModelJson,
      "relatedMessageType" -> "IE815",
      "isTFESubmission" -> isTFESubmission
    )

    val getSubmissionFailureMessageResponseMinimumDownstreamJson: JsValue = Json.obj(
      "dateTime" -> "now",
      "exciseRegistrationNumber" -> testErn,
      "message" -> Base64.getEncoder.encodeToString(trim(XML.loadString(submissionFailureMessageDataNoRelatedMessageTypeXmlBody)).toString().getBytes(StandardCharsets.UTF_8))
    )

    val getSubmissionFailureMessageResponseMinimumModel: GetSubmissionFailureMessageResponse = GetSubmissionFailureMessageResponse(
      ie704 = IE704ModelFixtures.ie704ModelModel,
      relatedMessageType = None
    )

    def getSubmissionFailureMessageResponseMinimumJson(isTFESubmission: Boolean): JsValue = Json.obj(
      "ie704" -> IE704ModelFixtures.ie704ModelJson,
      "isTFESubmission" -> isTFESubmission
    )
  }
}
