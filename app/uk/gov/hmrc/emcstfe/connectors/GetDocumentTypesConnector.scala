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

package uk.gov.hmrc.emcstfe.connectors

import play.api.libs.json.JsResultException
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.connectors.httpParsers.GetDocumentTypesHttpParser
import uk.gov.hmrc.emcstfe.models.createMovement.DocumentType
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{JsonValidationError, UnexpectedDownstreamResponseError}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

trait GetDocumentTypesConnector {
  def baseUrl: String

  def getDocumentTypes()(implicit headerCarrier: HeaderCarrier, executionContext: ExecutionContext): Future[Either[ErrorResponse, Seq[DocumentType]]]
}

@Singleton
class GetDocumentTypesConnectorImpl @Inject()(val http: HttpClient,
                                          config: AppConfig) extends GetDocumentTypesHttpParser with GetDocumentTypesConnector {

  def baseUrl: String = config.referenceDataBaseUrl

  def getDocumentTypes()(implicit headerCarrier: HeaderCarrier, executionContext: ExecutionContext): Future[Either[ErrorResponse, Seq[DocumentType]]] =
    get(baseUrl + "/oracle/type-of-document")
      .recover {
        case JsResultException(errors) =>
          logger.warn(s"[getDocumentTypes] Bad JSON response from emcs-tfe-reference-data: " + errors)
          Left(JsonValidationError)
        case error =>
          logger.warn(s"[getDocumentTypes] Unexpected error from reference-data: ${error.getClass} ${error.getMessage}")
          Left(UnexpectedDownstreamResponseError)
      }
}
