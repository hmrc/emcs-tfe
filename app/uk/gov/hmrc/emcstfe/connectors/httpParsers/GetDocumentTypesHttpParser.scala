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

import play.api.http.Status.OK
import play.api.libs.json.Reads
import uk.gov.hmrc.emcstfe.connectors.BaseConnectorUtils
import uk.gov.hmrc.emcstfe.models.createMovement.DocumentType
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{JsonValidationError, UnexpectedDownstreamResponseError}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

trait GetDocumentTypesHttpParser extends BaseConnectorUtils[Seq[DocumentType]] {

  implicit val reads: Reads[Seq[DocumentType]] = Reads.seq(DocumentType.format)
  def http: HttpClient

  class GetDocumentTypesReads() extends HttpReads[Either[ErrorResponse, Seq[DocumentType]]] {
    override def read(method: String, url: String, response: HttpResponse): Either[ErrorResponse, Seq[DocumentType]] = {
      response.status match {
        case OK =>
          response.validateJson match {
            case Some(valid: Seq[DocumentType]) if valid.isDefinedAt(0) => Right(valid)
            case _ =>
              logger.warn(s"[read] Bad JSON response from emcs-tfe-reference-data")
              Left(JsonValidationError)
          }
        case status =>
          logger.warn(s"[read] Unexpected status from emcs-tfe-reference-data: $status")
          Left(UnexpectedDownstreamResponseError)
      }
    }
  }

  def get(url: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, Seq[DocumentType]]] =
    http.GET[Either[ErrorResponse, Seq[DocumentType]]](url = url)(new GetDocumentTypesReads(), hc, ec)

}
