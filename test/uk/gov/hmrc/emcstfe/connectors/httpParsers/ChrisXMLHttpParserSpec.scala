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
import uk.gov.hmrc.emcstfe.mocks.utils.MockXmlUtils
import uk.gov.hmrc.emcstfe.models.common.Enumerable.EnumerableXmlParseFailure
import uk.gov.hmrc.emcstfe.models.common.JourneyTime.JourneyTimeParseFailure
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{SoapExtractionError, UnexpectedDownstreamResponseError, XmlParseError, XmlValidationError}
import uk.gov.hmrc.emcstfe.models.response.GetMovementResponse
import uk.gov.hmrc.emcstfe.support.UnitSpec
import uk.gov.hmrc.http.HttpResponse

import scala.xml.XML

class ChrisXMLHttpParserSpec extends UnitSpec with MockXmlUtils with GetMovementFixture {

  object TestParser extends ChrisXMLHttpParser(mockXmlUtils)

  ".modelFromXmlHttpReads" when {

    s"an OK ($OK) response is received" must {

      "return a Right" when {

        "it contains valid XML" in {

          MockXmlUtils.extractFromSoap(XML.loadString(getMovementSoapWrapper))
            .returns(Right(XML.loadString(getMovementResponseBody)))

          val response = HttpResponse(OK, body = getMovementSoapWrapper, headers = Map.empty)

          val result = TestParser.modelFromXmlHttpReads[GetMovementResponse](shouldExtractFromSoap = true).read("POST", "/chris/foo/bar", response)

          result shouldBe Right(getMovementResponse)
        }
      }

      "return a Left" when {
        val notXmlBody = "notValidXML"
        val invalidXmlBody = "<Message></Message>"

        "body is not XML" in {

          val response = HttpResponse(OK, body = notXmlBody, headers = Map.empty)

          val result = TestParser.modelFromXmlHttpReads[GetMovementResponse](shouldExtractFromSoap = true).read("POST", "/chris/foo/bar", response)

          result shouldBe Left(XmlValidationError)
        }

        "extractFromSoap returns a Left" in {

          MockXmlUtils.extractFromSoap(XML.loadString(invalidXmlBody))
            .returns(Left(SoapExtractionError))

          val response = HttpResponse(OK, body = invalidXmlBody, headers = Map.empty)

          val result = TestParser.modelFromXmlHttpReads[GetMovementResponse](shouldExtractFromSoap = true).read("POST", "/chris/foo/bar", response)

          result shouldBe Left(SoapExtractionError)
        }

        "extractFromSoap returns a Right but the result can't be parsed to the Model expected" in {

          MockXmlUtils.extractFromSoap(XML.loadString(invalidXmlBody))
            .returns(Right(XML.loadString(invalidXmlBody)))

          val response = HttpResponse(OK, body = invalidXmlBody, headers = Map.empty)

          val result = TestParser.modelFromXmlHttpReads[GetMovementResponse](shouldExtractFromSoap = true).read("POST", "/chris/foo/bar", response)

          result shouldBe Left(XmlParseError(Seq(
            EmptyError(GetMovementResponse.arc),
            EmptyError(GetMovementResponse.sequenceNumber),
            EnumerableXmlParseFailure(s"Invalid enumerable value of ''"),
            EmptyError(GetMovementResponse.localReferenceNumber),
            EmptyError(GetMovementResponse.eadStatus),
            EmptyError(GetMovementResponse.consignorTrader \\ "TraderExciseNumber"),
            EmptyError(GetMovementResponse.consignorTrader \\ "TraderName"),
            EmptyError(GetMovementResponse.dateOfDispatch),
            JourneyTimeParseFailure("Could not parse JourneyTime, received: ''")
          )))
        }
      }
    }

    s"Any other Http status is received. E.g. INTERNAL_SERVER_ERROR ($INTERNAL_SERVER_ERROR)" must {

      "return Left(UnexpectedDownstreamResponseError)" in {

        val response = HttpResponse(INTERNAL_SERVER_ERROR, body = "Error Msg", headers = Map.empty)

        val result = TestParser.modelFromXmlHttpReads[GetMovementResponse](shouldExtractFromSoap = true).read("POST", "/chris/foo/bar", response)

        result shouldBe Left(UnexpectedDownstreamResponseError)
      }
    }
  }

  ".rawXMLHttpReads" when {

    s"an OK ($OK) response is received" must {

      "return a Right" when {

        "it contains valid XML" in {

          MockXmlUtils.extractFromSoap(XML.loadString(getMovementSoapWrapper))
            .returns(Right(XML.loadString(getMovementResponseBody)))

          val response = HttpResponse(OK, body = getMovementSoapWrapper, headers = Map.empty)

          val result = TestParser.rawXMLHttpReads(shouldExtractFromSoap = true).read("POST", "/chris/foo/bar", response)

          result shouldBe Right(XML.loadString(getMovementResponseBody))
        }
      }

      "return a Left" when {
        val notXmlBody = "notValidXML"
        val invalidXmlBody = "<Message></Message>"

        "body is not XML" in {

          val response = HttpResponse(OK, body = notXmlBody, headers = Map.empty)

          val result = TestParser.rawXMLHttpReads(shouldExtractFromSoap = true).read("POST", "/chris/foo/bar", response)

          result shouldBe Left(XmlValidationError)
        }

        "extractFromSoap returns a Left" in {

          MockXmlUtils.extractFromSoap(XML.loadString(invalidXmlBody))
            .returns(Left(SoapExtractionError))

          val response = HttpResponse(OK, body = invalidXmlBody, headers = Map.empty)

          val result = TestParser.rawXMLHttpReads(shouldExtractFromSoap = true).read("POST", "/chris/foo/bar", response)

          result shouldBe Left(SoapExtractionError)
        }
      }
    }

    s"Any other Http status is received. E.g. INTERNAL_SERVER_ERROR ($INTERNAL_SERVER_ERROR)" must {

      "return Left(UnexpectedDownstreamResponseError)" in {

        val response = HttpResponse(INTERNAL_SERVER_ERROR, body = "Error Msg", headers = Map.empty)

        val result = TestParser.rawXMLHttpReads(shouldExtractFromSoap = true).read("POST", "/chris/foo/bar", response)

        result shouldBe Left(UnexpectedDownstreamResponseError)
      }
    }
  }
}
