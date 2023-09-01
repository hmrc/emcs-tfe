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

package uk.gov.hmrc.emcstfe.utils

import uk.gov.hmrc.emcstfe.fixtures.GetMovementFixture
import uk.gov.hmrc.emcstfe.mocks.utils.MockHMRCMarkUtil
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{GenericParseError, MarkCreationError, MarkPlacementError, MinifyXmlError, SoapExtractionError, XmlParseError}
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.xml.{Elem, NodeSeq, XML}

class XmlUtilsSpec extends TestBaseSpec with GetMovementFixture with MockHMRCMarkUtil {
  trait Test {
    val xmlUtils = new XmlUtils(mockHMRCMarkUtil)
  }

  "extractFromSoap" when {
    "passed xml" should {
      "return a Success" when {
        "there is CDATA containing XML at the correct path" in new Test {
          xmlUtils.extractFromSoap(XML.loadString(getMovementSoapWrapper)) shouldBe Right(XML.loadString(getMovementResponseBody))
        }
      }
      "return a Failure" when {
        "there is CDATA containing XML at the wrong path" in new Test {
          xmlUtils.extractFromSoap(XML.loadString(
            s"""<tns:Envelope
               |	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
               |	xmlns:tns="http://www.w3.org/2003/05/soap-envelope">
               |	<tns:Body>
               |		<con:Control
               |			xmlns:con="http://www.govtalk.gov.uk/taxation/InternationalTrade/Common/ControlDocument">
               |			<con:MetaData>
               |				<con:MessageId>String</con:MessageId>
               |				<con:Source>String</con:Source>
               |				<con:Identity>String</con:Identity>
               |				<con:Partner>String</con:Partner>
               |				<con:CorrelationId>String</con:CorrelationId>
               |				<con:BusinessKey>String</con:BusinessKey>
               |				<con:MessageDescriptor>String</con:MessageDescriptor>
               |				<con:QualityOfService>String</con:QualityOfService>
               |				<con:Destination>String</con:Destination>
               |				<con:Priority>0</con:Priority>
               |			</con:MetaData>
               |			<con:OperationResponse>
               |				<con:Results>
               |					<con:MyResult Name="">
               |						<![CDATA[$getMovementResponseBody]]>
               |					</con:MyResult>
               |				</con:Results>
               |			</con:OperationResponse>
               |		</con:Control>
               |	</tns:Body>
               |</tns:Envelope>""".stripMargin)) shouldBe Left(SoapExtractionError)
        }
        "there is CDATA that doesn't contain XML" in new Test {
          xmlUtils.extractFromSoap(XML.loadString(
            s"""<tns:Envelope
               |	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
               |	xmlns:tns="http://www.w3.org/2003/05/soap-envelope">
               |	<tns:Body>
               |		<con:Control
               |			xmlns:con="http://www.govtalk.gov.uk/taxation/InternationalTrade/Common/ControlDocument">
               |			<con:MetaData>
               |				<con:MessageId>String</con:MessageId>
               |				<con:Source>String</con:Source>
               |				<con:Identity>String</con:Identity>
               |				<con:Partner>String</con:Partner>
               |				<con:CorrelationId>String</con:CorrelationId>
               |				<con:BusinessKey>String</con:BusinessKey>
               |				<con:MessageDescriptor>String</con:MessageDescriptor>
               |				<con:QualityOfService>String</con:QualityOfService>
               |				<con:Destination>String</con:Destination>
               |				<con:Priority>0</con:Priority>
               |			</con:MetaData>
               |			<con:OperationResponse>
               |				<con:Results>
               |					<con:Result Name="">
               |						<![CDATA[12345]]>
               |					</con:Result>
               |				</con:Results>
               |			</con:OperationResponse>
               |		</con:Control>
               |	</tns:Body>
               |</tns:Envelope>""".stripMargin)) shouldBe Left(SoapExtractionError)
        }
        "there is no CDATA" in new Test {
          xmlUtils.extractFromSoap(XML.loadString(
            s"""<tns:Envelope
               |	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
               |	xmlns:tns="http://www.w3.org/2003/05/soap-envelope">
               |	<tns:Body>
               |		<con:Control
               |			xmlns:con="http://www.govtalk.gov.uk/taxation/InternationalTrade/Common/ControlDocument">
               |			<con:MetaData>
               |				<con:MessageId>String</con:MessageId>
               |				<con:Source>String</con:Source>
               |				<con:Identity>String</con:Identity>
               |				<con:Partner>String</con:Partner>
               |				<con:CorrelationId>String</con:CorrelationId>
               |				<con:BusinessKey>String</con:BusinessKey>
               |				<con:MessageDescriptor>String</con:MessageDescriptor>
               |				<con:QualityOfService>String</con:QualityOfService>
               |				<con:Destination>String</con:Destination>
               |				<con:Priority>0</con:Priority>
               |			</con:MetaData>
               |			<con:OperationResponse>
               |				<con:Results>
               |					<con:MyResult Name="">
               |						<Message>Success!</Message>
               |					</con:MyResult>
               |				</con:Results>
               |			</con:OperationResponse>
               |		</con:Control>
               |	</tns:Body>
               |</tns:Envelope>""".stripMargin)) shouldBe Left(SoapExtractionError)
        }
      }
    }

    "passed a String" should {
      "return a Success" when {
        "there is CDATA containing XML at the correct path" in new Test {
          xmlUtils.readXml(getMovementResponseBody) shouldBe Right(XML.loadString(getMovementResponseBody))
        }
      }
      "return a Failure" when {
        "passed bad XML" in new Test {
          xmlUtils.readXml("{}") shouldBe Left(XmlParseError(Seq(GenericParseError("Content is not allowed in prolog."))))
        }
      }
    }
  }

  "trimWhitespaceFromXml" should {
    "return minified XML" when {
      "provided XML it can minify" in new Test {
        val inputXml: Elem = <Node1>
          <Node2>
            <Field1>123</Field1>
            <Field2>456</Field2>
          </Node2>
        </Node1>
        val res: Either[ErrorResponse, NodeSeq] = xmlUtils.trimWhitespaceFromXml(inputXml)
        res.map(_.toString()) shouldBe Right("""<Node1><Node2><Field1>123</Field1><Field2>456</Field2></Node2></Node1>""")
      }
    }
    "return an error" when {
      "provided XML it cannot minify" in new Test {
        val inputXml: NodeSeq = NodeSeq.Empty
        val res: Either[ErrorResponse, NodeSeq] = xmlUtils.trimWhitespaceFromXml(inputXml)
        res shouldBe Left(MinifyXmlError)
      }
    }
  }

  "prepareXmlForSubmission" should {
    "add a HMRC Mark and minify the XML" when {
      "provided XML is valid" in new Test {
        MockHMRCMarkUtil.createHmrcMark().returns(Right("my mark"))

        val inputXml: Elem = XML.loadString(
          """<?xml version='1.0' encoding='UTF-8'?>
            |<soapenv:Envelope xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope">
            |  <soapenv:Header>
            |    <ns:Info xmlns:ns="http://www.hmrc.gov.uk/ws/info-header/1">
            |      <Node1>Some stuff</Node1>
            |    </ns:Info>
            |    <MetaData xmlns="http://www.hmrc.gov.uk/ChRIS/SOAP/MetaData/1">
            |      <CredentialID>0000001284781216</CredentialID>
            |      <Identifier>GBWK001234569</Identifier>
            |    </MetaData>
            |  </soapenv:Header>
            |  <soapenv:Body>
            |    <Node2>Success!</Node2>
            |  </soapenv:Body>
            |</soapenv:Envelope>""".stripMargin)

        val outputXml: String =
          """<?xml version='1.0' encoding='UTF-8'?>
            |<soapenv:Envelope xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope"><soapenv:Header><ns:Info xmlns:ns="http://www.hmrc.gov.uk/ws/info-header/1"><Node1>Some stuff</Node1></ns:Info><Security xmlns="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"><BinarySecurityToken ValueType="http://www.hmrc.gov.uk#MarkToken">my mark</BinarySecurityToken></Security><MetaData xmlns="http://www.hmrc.gov.uk/ChRIS/SOAP/MetaData/1"><CredentialID>0000001284781216</CredentialID><Identifier>GBWK001234569</Identifier></MetaData></soapenv:Header><soapenv:Body><Node2>Success!</Node2></soapenv:Body></soapenv:Envelope>""".stripMargin

        val res: Either[ErrorResponse, String] = xmlUtils.prepareXmlForSubmission(inputXml)

        res shouldBe Right(outputXml)
      }
    }
    "return a Left" when {
      val inputXml = <Node1>
        <Node2>
          <Field1>123</Field1>
          <Field2>456</Field2>
        </Node2>
      </Node1>
      "provided XML doesn't have the correct node to add a mark to" in new Test {
        MockHMRCMarkUtil.createHmrcMark().returns(Right("my mark"))

        val res: Either[ErrorResponse, String] = xmlUtils.prepareXmlForSubmission(inputXml)

        res shouldBe Left(MarkPlacementError)
      }

      "the HMRC Mark Generator returns a Left" in new Test {
        MockHMRCMarkUtil.createHmrcMark().returns(Left(MarkCreationError))

        val res: Either[ErrorResponse, String] = xmlUtils.prepareXmlForSubmission(inputXml)

        res shouldBe Left(MarkCreationError)
      }
    }
  }
}
