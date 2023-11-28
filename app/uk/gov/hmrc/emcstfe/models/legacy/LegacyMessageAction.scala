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

package uk.gov.hmrc.emcstfe.models.legacy

import cats.implicits.{catsSyntaxTuple2Semigroupal, catsSyntaxTuple3Semigroupal, catsSyntaxTuple5Semigroupal}
import com.lucidchart.open.xtract._
import play.api.http.HeaderNames
import play.api.mvc.Request
import uk.gov.hmrc.emcstfe.models.request._
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse

import scala.util.matching.Regex
import scala.xml.NodeSeq

trait LegacyMessageAction {
  private val parameterPath: XPath = __ \\ "Control" \\ "OperationRequest" \\ "Parameters" \\ "Parameter"
  protected val ernXmlPath: XPath = parameterPath.with_attr("Name", "ExciseRegistrationNumber")
  protected val uniqueMessageIdPath: XPath = parameterPath.with_attr("Name", "UniqueMessageId")
  protected val maxNoToReturnPath: XPath = parameterPath.with_attr("Name", "MaxNoToReturn")
  protected val sortFieldPath: XPath = parameterPath.with_attr("Name", "SortField")
  protected val startPositionPath: XPath = parameterPath.with_attr("Name", "StartPosition")
  protected val sortOrderPath: XPath = parameterPath.with_attr("Name", "SortOrder")
  protected val arcPath: XPath = parameterPath.with_attr("Name", "ARC")
  protected val sequenceNumberPath: XPath = parameterPath.with_attr("Name", "SequenceNumber")

  def readXml[T](parseResult: XmlReader[T])(implicit request: Request[NodeSeq]): Either[ErrorResponse, T] = {
    parseResult.read(request.body).fold[Either[ErrorResponse, T]](
      errors => Left(ErrorResponse.InvalidLegacyRequestProvided(errors.mkString("; ")))
    )(onSuccess => Right(onSuccess))
  }
}

object GetMessages extends LegacyMessageAction {
  def eisRequest(implicit request: Request[NodeSeq]): Either[ErrorResponse, GetMessagesRequest] = {
    readXml((
      ernXmlPath.read[String],
      sortFieldPath.read[String].map(_.toLowerCase),
      sortOrderPath.read[String].map(_.toUpperCase),
      startPositionPath.read[Int],
      maxNoToReturnPath.read[Int]
    ).mapN((ern, sortField, sortOrder, startPosition, maxNoToReturn) =>
      GetMessagesRequest(ern, sortField, sortOrder, 1, maxNoToReturn, Some(startPosition))
    ))
  }
}

object GetMessageStatistics extends LegacyMessageAction {
  def eisRequest(implicit request: Request[NodeSeq]): Either[ErrorResponse, GetMessageStatisticsRequest] =
    readXml(ernXmlPath.read[String].map(GetMessageStatisticsRequest))
}

object MarkMessagesAsRead extends LegacyMessageAction {
  def eisRequest(implicit request: Request[NodeSeq]): Either[ErrorResponse, MarkMessageAsReadRequest] = {
    readXml((
      ernXmlPath.read[String],
      uniqueMessageIdPath.read[String]
    ).mapN(MarkMessageAsReadRequest))
  }
}

object SetMessageAsLogicallyDeleted extends LegacyMessageAction {
  def eisRequest(implicit request: Request[NodeSeq]): Either[ErrorResponse, SetMessageAsLogicallyDeletedRequest] = readXml((
    ernXmlPath.read[String],
    uniqueMessageIdPath.read[String],
  ).mapN(SetMessageAsLogicallyDeletedRequest))
}
object GetSubmissionFailureMessage extends LegacyMessageAction {
  def eisRequest(implicit request: Request[NodeSeq]): Either[ErrorResponse, GetSubmissionFailureMessageRequest] = readXml((
    ernXmlPath.read[String],
    uniqueMessageIdPath.read[String],
  ).mapN(GetSubmissionFailureMessageRequest))
}

object GetMovement extends LegacyMessageAction {
  def eisRequest(implicit request: Request[NodeSeq]): Either[ErrorResponse, GetMovementRequest] = readXml((
    ernXmlPath.read[String],
    arcPath.read[String],
    sequenceNumberPath.read[Option[Int]],
  ).mapN(GetMovementRequest))
}

object LegacyMessageAction {
  val regex: Regex = ".*action=\".*EMCSApplicationService/2.0/(.*)\".*".r

  def apply(request: Request[_]): Either[ErrorResponse, LegacyMessageAction] =
    request.headers.get(HeaderNames.CONTENT_TYPE) match {
      case Some(regex("GetMessages")) => Right(GetMessages)
      case Some(regex("GetMessageStatistics")) => Right(GetMessageStatistics)
      case Some(regex("MarkMessagesAsRead")) => Right(MarkMessagesAsRead)
      case Some(regex("SetMessageAsLogicallyDeleted")) => Right(SetMessageAsLogicallyDeleted)
      case Some(regex("GetSubmissionFailureMessage")) => Right(GetSubmissionFailureMessage)
      case Some(regex("GetMovement")) => Right(GetMovement)
      case Some(regex(a)) => Left(ErrorResponse.InvalidLegacyActionProvided(a))
      case _ => Left(ErrorResponse.NoLegacyActionProvided)
    }
}
