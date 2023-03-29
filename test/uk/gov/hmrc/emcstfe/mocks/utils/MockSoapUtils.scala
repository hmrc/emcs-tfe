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

package uk.gov.hmrc.emcstfe.mocks.utils

import org.scalamock.handlers.{CallHandler1, CallHandler2}
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.utils.SoapUtils

import scala.xml.{Elem, NodeSeq}

trait MockSoapUtils extends MockFactory {
  lazy val mockSoapUtils: SoapUtils = mock[SoapUtils]

  sealed trait ExtractFromSoapType
  case class ElemType() extends ExtractFromSoapType
  case class StringType() extends ExtractFromSoapType

  object MockSoapUtils {
    def extractFromSoap(t: ElemType): CallHandler1[Elem, Either[ErrorResponse, NodeSeq]] =
      (mockSoapUtils.extractFromSoap(_: Elem))
        .expects(*)

    def extractFromSoap(t: StringType): CallHandler2[String, String, Either[ErrorResponse, NodeSeq]] =
      (mockSoapUtils.extractFromSoap(_: String, _: String))
        .expects(*, *)

    def prepareXmlForSubmission(): CallHandler1[Elem, Either[ErrorResponse, String]] =
      (mockSoapUtils.prepareXmlForSubmission(_: Elem))
        .expects(*)
  }
}
