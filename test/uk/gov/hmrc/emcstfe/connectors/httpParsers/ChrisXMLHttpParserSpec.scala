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

import com.lucidchart.open.xtract._
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK}
import uk.gov.hmrc.emcstfe.fixtures.GetMovementFixture
import uk.gov.hmrc.emcstfe.mocks.utils.MockSoapUtils
import uk.gov.hmrc.emcstfe.models.common.JourneyTime.JourneyTimeParseFailure
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{SoapExtractionError, UnexpectedDownstreamResponseError, XmlParseError, XmlValidationError}
import uk.gov.hmrc.emcstfe.models.response.GetMovementResponse
import uk.gov.hmrc.emcstfe.support.UnitSpec
import uk.gov.hmrc.http.HttpResponse

import scala.xml.XML

class ChrisXMLHttpParserSpec extends UnitSpec with MockSoapUtils with GetMovementFixture {

  object TestParser extends ChrisXMLHttpParser(mockSoapUtils)

  ".rawXMLHttpReads" when {

    s"an OK ($OK) response is received" must {

      "return a Right" when {

        "it contains valid XML" in {

          MockSoapUtils.extractFromSoap()
            .returns(Right(XML.loadString(getMovementResponseBody)))

          val response = HttpResponse(OK, body = getMovementSoapWrapper, headers = Map.empty)

          val result = TestParser.rawXMLHttpReads[GetMovementResponse](shouldExtractFromSoap = true).read("POST", "/chris/foo/bar", response)

          result shouldBe Right(getMovementResponse)
        }
      }

      "return a Left" when {
        val notXmlBody = "notValidXML"
        val invalidXmlBody = "<Message></Message>"

        "body is not XML" in {

          val response = HttpResponse(OK, body = notXmlBody, headers = Map.empty)

          val result = TestParser.rawXMLHttpReads[GetMovementResponse](shouldExtractFromSoap = true).read("POST", "/chris/foo/bar", response)

          result shouldBe Left(XmlValidationError)
        }

        "extractFromSoap returns a Left" in {

          MockSoapUtils.extractFromSoap()
            .returns(Left(SoapExtractionError))

          val response = HttpResponse(OK, body = invalidXmlBody, headers = Map.empty)

          val result = TestParser.rawXMLHttpReads[GetMovementResponse](shouldExtractFromSoap = true).read("POST", "/chris/foo/bar", response)

          result shouldBe Left(SoapExtractionError)
        }

        "extractFromSoap returns a Right but the result can't be parsed to the Model expected" in {

          MockSoapUtils.extractFromSoap()
            .returns(Right(XML.loadString(invalidXmlBody)))

          val response = HttpResponse(OK, body = invalidXmlBody, headers = Map.empty)

          val result = TestParser.rawXMLHttpReads[GetMovementResponse](shouldExtractFromSoap = true).read("POST", "/chris/foo/bar", response)

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

        val result = TestParser.rawXMLHttpReads[GetMovementResponse](shouldExtractFromSoap = true).read("POST", "/chris/foo/bar", response)

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
