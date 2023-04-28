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
import cats.implicits._
import com.lucidchart.open.xtract.XmlReader
import play.api.libs.json.{JsString, JsValue}
import uk.gov.hmrc.emcstfe.connectors.ChrisConnector
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.mongo.GetMovementMongoResponse
import uk.gov.hmrc.emcstfe.models.request.{GetMovementIfChangedRequest, GetMovementRequest}
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{GenericParseError, XmlParseError}
import uk.gov.hmrc.emcstfe.models.response.{ErrorResponse, GetMovementIfChangedResponse, GetMovementResponse}
import uk.gov.hmrc.emcstfe.repositories.GetMovementRepository
import uk.gov.hmrc.emcstfe.utils.XmlResultParser.handleParseResult
import uk.gov.hmrc.emcstfe.utils.{Logging, XmlUtils}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}
import scala.xml.{NodeSeq, XML}

@Singleton
class GetMovementService @Inject()(
                                    connector: ChrisConnector,
                                    repository: GetMovementRepository,
                                    xmlUtils: XmlUtils
                                  ) extends Logging {
  def getMovement(getMovementRequest: GetMovementRequest, forceFetchNew: Boolean)(implicit hc: HeaderCarrier, ec: ExecutionContext, request: UserRequest[_]): Future[Either[ErrorResponse, GetMovementResponse]] = {
    repository.get(request.internalId, request.ern, getMovementRequest.arc).flatMap {
      case Some(value) =>
        logger.info("[getMovement] Matching movement found, calling GetMovementIfChanged")
        if(forceFetchNew) {
          getMovementIfChanged(getMovementRequest, value)
        } else {
          Future.successful(generateGetMovementResponse(value.data))
        }
      case None =>
        logger.info("[getMovement] No matching movement found, calling GetMovement")
        getNewMovement(getMovementRequest)
    }
  }

  private[services] def getMovementIfChanged(getMovementRequest: GetMovementRequest, repositoryResult: GetMovementMongoResponse)(implicit hc: HeaderCarrier, ec: ExecutionContext, request: UserRequest[_]): Future[Either[ErrorResponse, GetMovementResponse]] = {



    val chrisResponseFutureRaw: Future[Either[ErrorResponse, NodeSeq]] = {
      Try {
        XML.loadString(repositoryResult.data.as[String])
      } match {
        case Failure(e) =>
          Future.successful(Left(XmlParseError(Seq(GenericParseError(e.getMessage)))))
        case Success(value) =>
          val getMovementIfChangedRequest = GetMovementIfChangedRequest(
            exciseRegistrationNumber = getMovementRequest.exciseRegistrationNumber,
            arc = getMovementRequest.arc,
            sequenceNumber = extractSequenceNumberFromXml(value),
            versionTransactionReference = extractVersionTransactionReferenceFromXml(value)
          )
          connector.postChrisSOAPRequest(getMovementIfChangedRequest)
      }
    }

    val chrisResponseFutureModel: Future[Either[ErrorResponse, GetMovementIfChangedResponse]] =
      chrisResponseFutureRaw.map {
        case Left(value) =>
          logger.debug(s"Received Left(error) from ChRIS: $value")
          Left(value)
        case Right(value) =>
          logger.debug(s"Received Right(value) from ChRIS: $value")
          handleParseResult(XmlReader.of[GetMovementIfChangedResponse].read(value))
      }

    chrisResponseFutureModel.flatMap {
      chrisResponse =>
        chrisResponse.map {
          getMovementIfChangedResponse =>
            if (getMovementIfChangedResponse.result.trim.isEmpty) {
              // if Results is empty, return `value`
              logger.info("[getMovementIfChanged] No change to movement, returning movement from mongo")
              val model: Either[ErrorResponse, GetMovementResponse] = generateGetMovementResponse(repositoryResult.data)

              Future.successful(model)
            } else {
              // if Results is not empty:
              //  - store new results
              //  - return new results
              logger.info("[getMovementIfChanged] Change to movement found, updating and returning new movement")
              val newResult: Either[ErrorResponse, NodeSeq] = xmlUtils.readXml(getMovementIfChangedResponse.result)

              storeAndReturn(newResult)(getMovementRequest)
            }
        }.sequence.map(_.flatten)
    }
  }

  private[services] def extractVersionTransactionReferenceFromXml(xml: NodeSeq): String =
    (xml \\ "movementView" \\ "currentMovement" \\ "version_transaction_ref").text

  private[services] def extractSequenceNumberFromXml(xml: NodeSeq): String =
    (xml \\ "movementView" \\ "currentMovement" \\ "IE801" \\ "Body" \\ "EADESADContainer" \\ "HeaderEadEsad" \\ "SequenceNumber").text

  private[services] def generateGetMovementResponse(data: JsValue): Either[ErrorResponse, GetMovementResponse] = Try {
    XML.loadString(data.as[String])
  } match {
    case Failure(e) =>
      Left(XmlParseError(Seq(GenericParseError(e.getMessage))))
    case Success(value) =>
      handleParseResult(XmlReader.of[GetMovementResponse].read(value))
  }

  private[services] def getNewMovement(getMovementRequest: GetMovementRequest)(implicit hc: HeaderCarrier, ec: ExecutionContext, request: UserRequest[_]): Future[Either[ErrorResponse, GetMovementResponse]] = {
    val chrisResponseF: Future[Either[ErrorResponse, NodeSeq]] = connector.postChrisSOAPRequest(getMovementRequest)

    chrisResponseF.flatMap {
      chrisResponse =>
        storeAndReturn(chrisResponse)(getMovementRequest)
    }
  }

  private[services] def storeAndReturn(chrisResponse: Either[ErrorResponse, NodeSeq])(getMovementRequest: GetMovementRequest)(implicit ec: ExecutionContext, request: UserRequest[_]): Future[Either[ErrorResponse, GetMovementResponse]] = {

    val responseAfterStoringInMongo: EitherT[Future, ErrorResponse, GetMovementResponse] = {
      for {
        res <- EitherT.fromEither[Future](chrisResponse)

        resString <- EitherT.fromEither[Future](xmlUtils.trimWhitespaceFromXml(res))

        getMovementMongoResponse = chrisResponse.map(_ => GetMovementMongoResponse(request.internalId, request.ern, getMovementRequest.arc, JsString(resString.toString())))

        getMovementMongoResponseRight <- EitherT.fromEither[Future](getMovementMongoResponse)

        _ <- EitherT(repository.set(getMovementMongoResponseRight).recover(recovery))

        value <- EitherT.fromEither[Future](handleParseResult(XmlReader.of[GetMovementResponse].read(res)))
      } yield {
        value
      }
    }

    responseAfterStoringInMongo.value
  }
}
