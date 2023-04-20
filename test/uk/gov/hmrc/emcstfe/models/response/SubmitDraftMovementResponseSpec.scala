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

package uk.gov.hmrc.emcstfe.models.response

import com.lucidchart.open.xtract.{EmptyError, ParseFailure, ParseSuccess}
import play.api.libs.json.Json
import uk.gov.hmrc.emcstfe.fixtures.SubmitDraftMovementFixture
import uk.gov.hmrc.emcstfe.support.UnitSpec

import scala.xml.XML

class SubmitDraftMovementResponseSpec extends UnitSpec with SubmitDraftMovementFixture {

  "writes" should {
    "write a model to JSON" in {
      Json.toJson(chrisSuccessResponse) shouldBe chrisSuccessJson
    }
  }

  "xmlReads" should {

    "successfully read a movement" when {

      "all fields are valid" in {
        ChRISSuccessResponse.xmlReader.read(XML.loadString(chrisSuccessSOAPResponseBody)) shouldBe ParseSuccess(chrisSuccessResponse.copy(lrn = None))
      }
    }

    "fail to read a movement" when {

      "missing DigestValue" in {

        val noDigestValueXML = XML.loadString(
          """<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope"
            |  xmlns:ns="http://www.inlandrevenue.gov.uk/SOAP/Response/2">
            |  <soap:Body>
            |    <ns:HMRCSOAPResponse>
            |      <SuccessResponse>
            |        <IRmarkReceipt>
            |          <dsig:Signature xmlns:dsig="http://www.w3.org/2000/09/xmldsig#">
            |            <dsig:SignedInfo>
            |              <dsig:Reference>
            |              </dsig:Reference>
            |            </dsig:SignedInfo>
            |          </dsig:Signature>
            |        </IRmarkReceipt>
            |        <Message code="077001">Thank you for your submission</Message>
            |        <AcceptedTime>2009-01-01T10:10:10.000</AcceptedTime>
            |      </SuccessResponse>
            |    </ns:HMRCSOAPResponse>
            |  </soap:Body>
            |</soap:Envelope>""".stripMargin)

        ChRISSuccessResponse.xmlReader.read(noDigestValueXML) shouldBe ParseFailure(Seq(EmptyError(ChRISSuccessResponse.digestValue), EmptyError(ChRISSuccessResponse.digestValue)))
      }

      "DigestValue is in the wrong place" in {

        val noDigestValueXML = XML.loadString(
          """<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope"
            |  xmlns:ns="http://www.inlandrevenue.gov.uk/SOAP/Response/2">
            |  <soap:Body>
            |    <ns:HMRCSOAPResponse>
            |      <SuccessResponse>
            |        <IRmarkReceipt>
            |          <dsig:Signature xmlns:dsig="http://www.w3.org/2000/09/xmldsig#">
            |            <dsig:SignedInfo>
            |              <dsig:DigestValue>KWrqNXggsCEMgbOr3wiozyfrdU0=</dsig:DigestValue>
            |            </dsig:SignedInfo>
            |          </dsig:Signature>
            |        </IRmarkReceipt>
            |        <Message code="077001">Thank you for your submission</Message>
            |        <AcceptedTime>2009-01-01T10:10:10.000</AcceptedTime>
            |      </SuccessResponse>
            |    </ns:HMRCSOAPResponse>
            |  </soap:Body>
            |</soap:Envelope>""".stripMargin)

        ChRISSuccessResponse.xmlReader.read(noDigestValueXML) shouldBe ParseFailure(Seq(EmptyError(ChRISSuccessResponse.digestValue), EmptyError(ChRISSuccessResponse.digestValue)))
      }
    }
  }

  "digestValueToReceipt" should {
    "correctly convert a digestValue to receipt value" in {
      ChRISSuccessResponse.digestValueToReceipt("KWrqNXggsCEMgbOr3wiozyfrdU0=") shouldBe "FFVOUNLYECYCCDEBWOV56CFIZ4T6W5KN"
    }
  }
}
