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
import uk.gov.hmrc.emcstfe.fixtures.{ChRISResponsesFixture, GetMovementFixture}
import uk.gov.hmrc.emcstfe.mocks.utils.MockXmlUtils
import uk.gov.hmrc.emcstfe.models.common.Enumerable.EnumerableXmlParseFailure
import uk.gov.hmrc.emcstfe.models.common.JourneyTime.JourneyTimeParseFailure
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{ChRISRIMValidationError, SoapExtractionError, UnexpectedDownstreamResponseError, XmlParseError, XmlValidationError}
import uk.gov.hmrc.emcstfe.models.response.getMovement.GetMovementResponse
import uk.gov.hmrc.emcstfe.models.response.getMovement.GetMovementResponse.EADESADContainer
import uk.gov.hmrc.emcstfe.support.TestBaseSpec
import uk.gov.hmrc.http.HttpResponse

import scala.xml.XML

class ChrisXMLHttpParserSpec extends TestBaseSpec with MockXmlUtils with GetMovementFixture with ChRISResponsesFixture {

  object TestParser extends ChrisXMLHttpParser(mockXmlUtils)

  ".modelFromXmlHttpReads" when {

    s"an OK ($OK) response is received" when {

      "The response is a SuccessResponse from ChRIS (does not include `ErrorResponse` tag in the XML)" must {

        "return a Right" when {

          "it contains valid XML" in {

            MockXmlUtils.extractFromSoap(XML.loadString(getMovementSoapWrapper()))
              .returns(Right(XML.loadString(getMovementResponseBody())))

            val response = HttpResponse(OK, body = getMovementSoapWrapper(), headers = Map.empty)

            val result = TestParser.modelFromXmlHttpReads[GetMovementResponse](shouldExtractFromSoap = true).read("POST", "/chris/foo/bar", response)

            result shouldBe Right(getMovementResponse())
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
              EmptyError(EADESADContainer \ "EadEsad" \\ "LocalReferenceNumber"),
              EmptyError(EADESADContainer \ "EadEsad" \\ "InvoiceNumber"),
              EnumerableXmlParseFailure(s"Invalid enumerable value of '' for field 'EadEsad/OriginTypeCode'"),
              EmptyError(EADESADContainer \ "EadEsad" \\ "DateOfDispatch"),
              EmptyError(EADESADContainer \ "HeaderEadEsad" \\ "SequenceNumber"),
              EmptyError(EADESADContainer \ "HeaderEadEsad" \\ "DateAndTimeOfUpdateValidation"),
              EnumerableXmlParseFailure(s"Invalid enumerable value of '' for field 'HeaderEadEsad/DestinationTypeCode'"),
              JourneyTimeParseFailure("Could not parse JourneyTime from XML, received: ''"),
              EnumerableXmlParseFailure(s"Invalid enumerable value of '' for field 'HeaderEadEsad/TransportArrangement'"),
              EmptyError(EADESADContainer \ "TransportMode" \\ "TransportModeCode"),
              EnumerableXmlParseFailure(s"Invalid enumerable value of '' for field 'MovementGuarantee/GuarantorTypeCode'"),
              EmptyError(GetMovementResponse.arc),
              EmptyError(GetMovementResponse.eadStatus),
              EmptyError(EADESADContainer \ "ExciseMovement" \ "DateAndTimeOfValidationOfEadEsad")
            )))
          }
        }
      }

      "The response is an ErrorResponse from ChRIS (includes `ErrorResponse` tag in the XML)" must {

        "return a Left(ChRISRIMValidationErrorResponse)" when {

          "it contains valid XML of RIM failures" in {

            val response = HttpResponse(OK, body = chrisRimValidationResponseBody, headers = Map.empty)

            val result = TestParser.modelFromXmlHttpReads[GetMovementResponse](shouldExtractFromSoap = true).read("POST", "/chris/foo/bar", response)

            result shouldBe Left(ChRISRIMValidationError(chrisRIMValidationErrorResponse))
          }
        }

        "return a Left(ErrorResponse)" when {

          "body contains ErrorResponse XML tag but can't be read to the RIM Validation model" in {

            val response = HttpResponse(OK, body = invalidRimXmlBody, headers = Map.empty)

            val result = TestParser.modelFromXmlHttpReads[GetMovementResponse](shouldExtractFromSoap = true).read("POST", "/chris/foo/bar", response)

            result match {
              case Left(XmlParseError(errors)) => errors.nonEmpty shouldBe true
              case Right(_) => fail("Result was of type 'Right' when expecting 'Left'")
              case Left(_) => fail("Result was not a Left[XmlParseError] response")
            }
          }
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

          MockXmlUtils.extractFromSoap(XML.loadString(getMovementSoapWrapper()))
            .returns(Right(XML.loadString(getMovementResponseBody())))

          val response = HttpResponse(OK, body = getMovementSoapWrapper(), headers = Map.empty)

          val result = TestParser.rawXMLHttpReads(shouldExtractFromSoap = true).read("POST", "/chris/foo/bar", response)

          result shouldBe Right(XML.loadString(getMovementResponseBody()))
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
