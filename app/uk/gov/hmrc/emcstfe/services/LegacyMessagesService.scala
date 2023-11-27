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

import cats.data.EitherT
import play.api.mvc.Request
import uk.gov.hmrc.emcstfe.connectors.EisConnector
import uk.gov.hmrc.emcstfe.models.legacy._
import uk.gov.hmrc.emcstfe.models.response.{ErrorResponse, LegacyMessage}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.xml.NodeSeq

@Singleton
class LegacyMessagesService @Inject()(
                                       eisConnector: EisConnector
                                     )(implicit ec: ExecutionContext) {

  private def handleEither[T](request: Either[ErrorResponse, T], transform: T => Future[Either[ErrorResponse, LegacyMessage]]): EitherT[Future, ErrorResponse, LegacyMessage] =
    EitherT.fromEither[Future](request) flatMapF transform

  def performMessageAction(action: LegacyMessageAction)(implicit hc: HeaderCarrier, request: Request[NodeSeq]): EitherT[Future, ErrorResponse, LegacyMessage] = {
    action match {
      case GetMessages => handleEither(GetMessages.eisRequest, eisConnector.getRawMessages)
      case GetMessageStatistics => handleEither(GetMessageStatistics.eisRequest, eisConnector.getMessageStatistics)
      case GetSubmissionFailureMessage => handleEither(GetSubmissionFailureMessage.eisRequest, eisConnector.getRawSubmissionFailureMessage)
      case SetMessageAsLogicallyDeleted => handleEither(SetMessageAsLogicallyDeleted.eisRequest, eisConnector.setMessageAsLogicallyDeleted)
      case MarkMessagesAsRead => handleEither(MarkMessagesAsRead.eisRequest, eisConnector.markMessageAsRead)
      case GetMovement => handleEither(GetMovement.eisRequest, eisConnector.getRawMovement)
    }
  }

}
