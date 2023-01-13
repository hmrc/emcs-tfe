/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.utils

import uk.gov.hmrc.emcstfe.fixtures.GetMessageFixture
import uk.gov.hmrc.emcstfe.support.UnitSpec

import scala.util.{Failure, Success}
import scala.xml.XML

class SoapUtilsSpec extends UnitSpec with GetMessageFixture {
  "extractFromSoap" should {
    "return a Success" when {
      "there is CDATA containing XML at the correct path" in {
        SoapUtils.extractFromSoap(XML.loadString(getMessageSoapWrapper)) shouldBe a[Success[_]]
      }
    }
    "return a Failure" when {
      "there is CDATA containing XML at the wrong path" in {
        SoapUtils.extractFromSoap(XML.loadString(
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
             |						<![CDATA[$getMessageResponseBody]]>
             |					</con:MyResult>
             |				</con:Results>
             |			</con:OperationResponse>
             |		</con:Control>
             |	</tns:Body>
             |</tns:Envelope>""".stripMargin)) shouldBe a[Failure[_]]
      }
      "there is CDATA that doesn't contain XML" in {
        SoapUtils.extractFromSoap(XML.loadString(
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
             |</tns:Envelope>""".stripMargin)) shouldBe a[Failure[_]]
      }
      "there is no CDATA" in {
        SoapUtils.extractFromSoap(XML.loadString(
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
             |</tns:Envelope>""".stripMargin)) shouldBe a[Failure[_]]
      }
    }
  }
}
