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

package uk.gov.hmrc.emcstfe.services

import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.connectors.ChrisConnector
import uk.gov.hmrc.emcstfe.models.request.SubmitDraftMovementRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.NoLrnError
import uk.gov.hmrc.emcstfe.models.response.{ChRISSuccessResponse, ErrorResponse}
import uk.gov.hmrc.emcstfe.utils.Logging
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.xml.XML

@Singleton
class SubmitDraftMovementService @Inject()(connector: ChrisConnector, val appConfig: AppConfig) extends Logging {
  def submitDraftMovement(submitDraftMovementRequest: SubmitDraftMovementRequest)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, ChRISSuccessResponse]] = {
    extractLrn(submitDraftMovementRequest.requestBody) match {
      case None => Future.successful(Left(NoLrnError))
      case Some(lrn) =>
        connector.submitDraftMovementChrisSOAPRequest[ChRISSuccessResponse](submitDraftMovementRequest)
          .map(
            connectorResponse => connectorResponse.map(
              submitDraftMovementResponse => submitDraftMovementResponse.copy(lrn = Some(lrn))
            )
          )
    }

  }

  private def extractLrn(requestBody: String): Option[String] =
    (XML.loadString(requestBody) \\ "LocalReferenceNumber").headOption.map(_.text)
}
