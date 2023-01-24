/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.connectors.httpParsers

import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK}
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{UnexpectedDownstreamResponseError, XmlValidationError}
import uk.gov.hmrc.emcstfe.support.UnitSpec
import uk.gov.hmrc.http.HttpResponse

import scala.xml.Elem

class RawXMLHttpParserSpec extends UnitSpec {

  object TestParser extends RawXMLHttpParser

  ".rawXMLHttpReads" when {

    s"an OK ($OK) response is received" when {

      "contains valid XML" must {

        "return a Right(NodeSeq)" in {

          val successXml: Elem = <Message>Success!</Message>

          val response = HttpResponse(OK, body = successXml.toString(), headers = Map.empty)

          val result = TestParser.rawXMLHttpReads.read("POST", "/chris/foo/bar", response)

          result shouldBe Right(successXml)
        }
      }

      "contains invalid XML" must {

        "return Left(XmlValidationError)" in {

          val response = HttpResponse(OK, body = "notValidXML", headers = Map.empty)

          val result = TestParser.rawXMLHttpReads.read("POST", "/chris/foo/bar", response)

          result shouldBe Left(XmlValidationError)
        }
      }
    }

    s"Any other Http status is received. E.g. INTERNAL_SERVER_ERROR ($INTERNAL_SERVER_ERROR)" must {

      "return Left(UnexpectedDownstreamResponseError)" in {

        val response = HttpResponse(INTERNAL_SERVER_ERROR, body = "Error Msg", headers = Map.empty)

        val result = TestParser.rawXMLHttpReads.read("POST", "/chris/foo/bar", response)

        result shouldBe Left(UnexpectedDownstreamResponseError)
      }
    }
  }
}
