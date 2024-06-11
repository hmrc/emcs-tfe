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
import uk.gov.hmrc.emcstfe.models.response.ChRISSuccessResponse
import uk.gov.hmrc.emcstfe.models.response.rimValidation.{ChRISRIMValidationErrorResponse, RIMValidationError}

import scala.xml.Elem

trait ChRISResponsesFixture {

  lazy val chrisSuccessSOAPResponseBody: String =
    """<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope"
      |  xmlns:ns="http://www.inlandrevenue.gov.uk/SOAP/Response/2">
      |  <soap:Body>
      |    <ns:HMRCSOAPResponse>
      |      <SuccessResponse>
      |        <IRmarkReceipt>
      |          <dsig:Signature xmlns:dsig="http://www.w3.org/2000/09/xmldsig#">
      |            <dsig:SignedInfo>
      |              <dsig:Reference>
      |                <dsig:DigestValue>KWrqNXggsCEMgbOr3wiozyfrdU0=</dsig:DigestValue>
      |              </dsig:Reference>
      |            </dsig:SignedInfo>
      |          </dsig:Signature>
      |        </IRmarkReceipt>
      |        <Message code="077001">Thank you for your submission</Message>
      |        <AcceptedTime>2009-01-01T10:10:10.000</AcceptedTime>
      |      </SuccessResponse>
      |    </ns:HMRCSOAPResponse>
      |  </soap:Body>
      |</soap:Envelope>""".stripMargin

  val chrisRimValidationErrorXml: Elem =
    <Error>
      <RaisedBy>ChRIS</RaisedBy>
      <Number>8712</Number>
      <Type>business</Type>
      <Text>The submission type must be one of:  - Standard submission  - Submission for export (local clearance)  Please amend your entry and resubmit.</Text>
      <Location>/tns:Envelope[1]/tns:Body[1]/ie:IE815[1]/ie:Body[1]/ie:SubmittedDraftOfEADESAD[1]/ie:Attributes[1]/ie:SubmissionMessageType[1]</Location>
      <ErrorData>3</ErrorData>
    </Error>

  val chrisRimValidationError: RIMValidationError = RIMValidationError(
    errorCategory = Some("business"),
    errorType = Some(8712),
    errorReason = Some("The submission type must be one of:  - Standard submission  - Submission for export (local clearance)  Please amend your entry and resubmit."),
    errorLocation = Some("/tns:Envelope[1]/tns:Body[1]/ie:IE815[1]/ie:Body[1]/ie:SubmittedDraftOfEADESAD[1]/ie:Attributes[1]/ie:SubmissionMessageType[1]")
  )

  val chrisRimValidationResponseBody: String =
    """<Envelope xmlns="http://www.w3.org/2003/05/soap-envelope">
      |	<Header/>
      |	<Body>
      |		<HMRCSOAPResponse xmlns="http://www.inlandrevenue.gov.uk/SOAP/Response/2">
      |			<ErrorResponse SchemaVersion="2.0" xmlns="http://www.govtalk.gov.uk/CM/errorresponse">
      |				<Application>
      |					<MessageCount>7</MessageCount>
      |				</Application>
      |				<Error>
      |					<RaisedBy>ChRIS</RaisedBy>
      |					<Number>8712</Number>
      |					<Type>business</Type>
      |					<Text>The submission type must be one of:  - Standard submission  - Submission for export (local clearance)  Please amend your entry and resubmit.</Text>
      |					<Location>/tns:Envelope[1]/tns:Body[1]/ie:IE815[1]/ie:Body[1]/ie:SubmittedDraftOfEADESAD[1]/ie:Attributes[1]/ie:SubmissionMessageType[1]</Location>
      |					<ErrorData>3</ErrorData>
      |				</Error>
      |				<Error>
      |					<RaisedBy>ChRIS</RaisedBy>
      |					<Number>8044</Number>
      |					<Type>business</Type>
      |					<Text>The submission message type must start at 1. Please contact your software supplier for further advice.</Text>
      |					<Location>/tns:Envelope[1]/tns:Body[1]/ie:IE815[1]/ie:Body[1]/ie:SubmittedDraftOfEADESAD[1]/ie:Attributes[1]/ie:SubmissionMessageType[1]</Location>
      |					<ErrorData>3</ErrorData>
      |				</Error>
      |				<Error>
      |					<RaisedBy>ChRIS</RaisedBy>
      |					<Number>8713</Number>
      |					<Type>business</Type>
      |					<Text>The origin type must be one of:- Tax warehouse- Import Please amend your entry and resubmit.</Text>
      |					<Location>/tns:Envelope[1]/tns:Body[1]/ie:IE815[1]/ie:Body[1]/ie:SubmittedDraftOfEADESAD[1]/ie:EadEsadDraft[1]/ie:OriginTypeCode[1]</Location>
      |					<ErrorData>3</ErrorData>
      |				</Error>
      |    </ErrorResponse>
      |		</HMRCSOAPResponse>
      |	</Body>
      |</Envelope>""".stripMargin

  val chrisRIMValidationErrorResponse: ChRISRIMValidationErrorResponse = ChRISRIMValidationErrorResponse(
    rimValidationErrors = Seq(
      RIMValidationError(
        errorCategory = Some("business"),
        errorType = Some(8712),
        errorReason = Some("The submission type must be one of:  - Standard submission  - Submission for export (local clearance)  Please amend your entry and resubmit."),
        errorLocation = Some("/tns:Envelope[1]/tns:Body[1]/ie:IE815[1]/ie:Body[1]/ie:SubmittedDraftOfEADESAD[1]/ie:Attributes[1]/ie:SubmissionMessageType[1]")
      ),
      RIMValidationError(
        errorCategory = Some("business"),
        errorType = Some(8044),
        errorReason = Some("The submission message type must start at 1. Please contact your software supplier for further advice."),
        errorLocation = Some("/tns:Envelope[1]/tns:Body[1]/ie:IE815[1]/ie:Body[1]/ie:SubmittedDraftOfEADESAD[1]/ie:Attributes[1]/ie:SubmissionMessageType[1]")
      ),
      RIMValidationError(
        errorCategory = Some("business"),
        errorType = Some(8713),
        errorReason = Some("The origin type must be one of:- Tax warehouse- Import Please amend your entry and resubmit."),
        errorLocation = Some("/tns:Envelope[1]/tns:Body[1]/ie:IE815[1]/ie:Body[1]/ie:SubmittedDraftOfEADESAD[1]/ie:EadEsadDraft[1]/ie:OriginTypeCode[1]")
      )
    )
  )

  lazy val chrisSuccessResponse: ChRISSuccessResponse = ChRISSuccessResponse(
    receipt = "FFVOUNLYECYCCDEBWOV56CFIZ4T6W5KN",
    receiptDate = "2009-01-01T10:10:10.000",
    lrn = Some("EN")
  )

  def chrisSuccessJson(withSubmittedDraftId: Boolean = false): JsValue = Json.parse(
    s"""{
      |    "receipt": "FFVOUNLYECYCCDEBWOV56CFIZ4T6W5KN",
      |    "receiptDate": "2009-01-01T10:10:10.000",
      |    "lrn": "EN"
      |    ${if(withSubmittedDraftId) ",\"submittedDraftId\": \"PORTAL123456789012\"" else ""}
      |}""".stripMargin)

  def chrisSuccessJsonNoLRN(withSubmittedDraftId: Boolean = false): JsValue = Json.parse(
    s"""{
      |    "receipt": "FFVOUNLYECYCCDEBWOV56CFIZ4T6W5KN",
      |    "receiptDate": "2009-01-01T10:10:10.000"
      |    ${if(withSubmittedDraftId) ",\"submittedDraftId\": \"PORTAL123\"" else ""}
      |}""".stripMargin)

  val invalidRimXmlBody: String = """<Body>
                         |  <ErrorResponse SchemaVersion="2.0" xmlns="http://www.govtalk.gov.uk/CM/errorresponse">
                         |    <Error>
                         |		  <Number>invalid</Number>
                         |	  </Error>
                         |  </ErrorResponse>
                         |</Body>
                         |""".stripMargin
}
