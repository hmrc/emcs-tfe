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

package uk.gov.hmrc.emcstfe.connectors.httpParsers

import com.lucidchart.open.xtract.{EmptyError, ParseFailure, ParseSuccess, PartialParseSuccess, __}
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK}
import uk.gov.hmrc.emcstfe.fixtures.GetMovementFixture
import uk.gov.hmrc.emcstfe.models.common.JourneyTime.JourneyTimeParseFailure
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{UnexpectedDownstreamResponseError, XmlParseError, XmlValidationError}
import uk.gov.hmrc.emcstfe.models.response.GetMovementResponse
import uk.gov.hmrc.emcstfe.support.UnitSpec
import uk.gov.hmrc.http.HttpResponse

class ChrisXMLHttpParserSpec extends UnitSpec with GetMovementFixture {

  object TestParser extends ChrisXMLHttpParser

  ".rawXMLHttpReads" when {

    s"an OK ($OK) response is received" when {

      "contains valid XML" must {

        "return a Right(GetMovementResponse)" in {

          val response = HttpResponse(OK, body = getMovementSoapWrapper, headers = Map.empty)

          val result = TestParser.rawXMLHttpReads[GetMovementResponse].read("POST", "/chris/foo/bar", response)

          result shouldBe Right(getMovementResponse)
        }
      }

      "body is not XML" must {

        "return Left(XmlValidationError)" in {

          val response = HttpResponse(OK, body = "notValidXML", headers = Map.empty)

          val result = TestParser.rawXMLHttpReads[GetMovementResponse].read("POST", "/chris/foo/bar", response)

          result shouldBe Left(XmlValidationError)
        }
      }

      "body is XML but can't be parsed to the Model expected" must {

        "return Left(XmlParseError)" in {

          val badXml = """<tns:Envelope
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
             |						<![CDATA[<Message></Message>]]>
             |					</con:Result>
             |				</con:Results>
             |			</con:OperationResponse>
             |		</con:Control>
             |	</tns:Body>
             |</tns:Envelope>""".stripMargin

          val response = HttpResponse(OK, body = badXml, headers = Map.empty)

          val result = TestParser.rawXMLHttpReads[GetMovementResponse].read("POST", "/chris/foo/bar", response)

          result shouldBe Left(XmlParseError(Seq(
            EmptyError(GetMovementResponse.localReferenceNumber),
            EmptyError(GetMovementResponse.eadStatus),
            EmptyError(GetMovementResponse.consignorName),
            EmptyError(GetMovementResponse.dateOfDispatch),
            JourneyTimeParseFailure("Could not parse JourneyTime, received: ''")
          )))
        }
      }
    }

    s"Any other Http status is received. E.g. INTERNAL_SERVER_ERROR ($INTERNAL_SERVER_ERROR)" must {

      "return Left(UnexpectedDownstreamResponseError)" in {

        val response = HttpResponse(INTERNAL_SERVER_ERROR, body = "Error Msg", headers = Map.empty)

        val result = TestParser.rawXMLHttpReads[GetMovementResponse].read("POST", "/chris/foo/bar", response)

        result shouldBe Left(UnexpectedDownstreamResponseError)
      }
    }
  }

  ".handleParseResult" must {

    "return Right(model)" when {

      "XML parsing is successful" in {

        TestParser.handleParseResult(ParseSuccess("Success")) shouldBe Right("Success")
      }
    }

    "return Left(XmlParseError)" when {

      "XML parsing fails" in {
        val error = EmptyError(__ \ "tagName")
        TestParser.handleParseResult(ParseFailure(error)) shouldBe Left(XmlParseError(Seq(error)))
      }

      "XML parsing partially fails" in {
        val error = EmptyError(__ \ "tagName")
        TestParser.handleParseResult(PartialParseSuccess("PartialData", Seq(error))) shouldBe Left(XmlParseError(Seq(error)))
      }
    }
  }
}
