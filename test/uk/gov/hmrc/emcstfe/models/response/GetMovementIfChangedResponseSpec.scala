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
import uk.gov.hmrc.emcstfe.fixtures.GetMovementFixture
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.xml.XML

class GetMovementIfChangedResponseSpec extends TestBaseSpec with GetMovementFixture {

  ".xmlReads" should {

    "successfully read a movement" when {

      "Results is not empty" in {
        val result = GetMovementIfChangedResponse.xmlReader.read(XML.loadString(getMovementSoapWrapper))
          .map(res => res.copy(result = res.result.trim)) // remove whitespace for the test, doesn't matter in practice as will be converted into Scala XML

        result shouldBe ParseSuccess(GetMovementIfChangedResponse(getMovementResponseBody))
      }

      "Results is empty" in {

        GetMovementIfChangedResponse.xmlReader.read(XML.loadString(
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
             |				<con:Results/>
             |			</con:OperationResponse>
             |		</con:Control>
             |	</tns:Body>
             |</tns:Envelope>""".stripMargin)) shouldBe ParseSuccess(GetMovementIfChangedResponse(""))
      }
    }

    "return a ParseError" when {
      "Results is missing" in {
        GetMovementIfChangedResponse.xmlReader.read(XML.loadString(
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
             |			</con:OperationResponse>
             |		</con:Control>
             |	</tns:Body>
             |</tns:Envelope>""".stripMargin)) shouldBe ParseFailure(EmptyError(GetMovementIfChangedResponse.results))
      }
    }
  }
}
