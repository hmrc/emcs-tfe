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
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.connectors.{ChrisConnector, EisConnector}
import uk.gov.hmrc.emcstfe.featureswitch.core.config.{FeatureSwitching, SendToEIS}
import uk.gov.hmrc.emcstfe.models.mongo.GetMovementMongoResponse
import uk.gov.hmrc.emcstfe.models.request.{GetMovementIfChangedRequest, GetMovementRequest}
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{GenericParseError, XmlParseError}
import uk.gov.hmrc.emcstfe.models.response.getMovement.GetMovementResponse
import uk.gov.hmrc.emcstfe.models.response.{ErrorResponse, GetMovementIfChangedResponse}
import uk.gov.hmrc.emcstfe.repositories.GetMovementRepository
import uk.gov.hmrc.emcstfe.utils.XmlResultParser.parseResult
import uk.gov.hmrc.emcstfe.utils.{Logging, XmlUtils}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}
import scala.xml.{NodeSeq, XML}

@Singleton
class GetMovementService @Inject()(
                                    chrisConnector: ChrisConnector,
                                    eisConnector: EisConnector,
                                    repository: GetMovementRepository,
                                    xmlUtils: XmlUtils,
                                    val config: AppConfig,
                                  ) extends Logging with FeatureSwitching {
  def getMovement(getMovementRequest: GetMovementRequest, forceFetchNew: Boolean)
                 (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, GetMovementResponse]] = {
    repository.get(getMovementRequest.arc).flatMap {

      case Some(cache) if shouldReturnDataFromCache(cache.sequenceNumber, getMovementRequest.sequenceNumber, forceFetchNew) =>
        logger.debug(s"[getMovement] generateGetMovementResponse from cached movement for sequenceNumber: '${cache.sequenceNumber}'")
        Future.successful(generateGetMovementResponse(cache.data))

      case Some(cache) =>
        if(isEnabled(SendToEIS) || getMovementRequest.sequenceNumber.isDefined) {
          logger.debug(s"[getMovement] generateGetMovementResponse from downstream system" + getMovementRequest.sequenceNumber.fold("")(" for sequenceNumber: " + _))
          getNewMovement(getMovementRequest, Some(generateGetMovementResponse(cache.data)))
        } else {
          logger.debug("[getMovement] generateGetMovementResponse by checking against ChRIS to see if the movement has changed")
          getMovementIfChanged(getMovementRequest, cache)
        }

      case _ =>
        logger.debug("[getMovement] generateGetMovementResponse by calling GetMovement as no matching movement found in cache")
        getNewMovement(getMovementRequest, None)
    }
  }

  private def shouldReturnDataFromCache(cacheSequenceNumber: Int, requestedSequenceNumber: Option[Int], forceFetchNew: Boolean): Boolean =
    !forceFetchNew && (requestedSequenceNumber.isEmpty || requestedSequenceNumber.contains(cacheSequenceNumber))

  private[services] def getMovementIfChanged(getMovementRequest: GetMovementRequest, repositoryResult: GetMovementMongoResponse)
                                            (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, GetMovementResponse]] = {
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
          chrisConnector.postChrisSOAPRequest(getMovementIfChangedRequest)
      }
    }

    val chrisResponseFutureModel: Future[Either[ErrorResponse, GetMovementIfChangedResponse]] =
      chrisResponseFutureRaw.map {
        case Left(value) => Left(value)
        case Right(value) => parseResult(XmlReader.of[GetMovementIfChangedResponse].read(value))
      }

    chrisResponseFutureModel.flatMap {
      chrisResponse =>
        chrisResponse.map {
          getMovementIfChangedResponse =>
            if (getMovementIfChangedResponse.result.trim.isEmpty) {
              // if Results is empty, return `value`
              logger.debug("[getMovementIfChanged] No change to movement, returning movement from mongo")
              val model: Either[ErrorResponse, GetMovementResponse] = generateGetMovementResponse(repositoryResult.data)

              Future.successful(model)
            } else {
              // if Results is not empty:
              //  - store new results
              //  - return new results
              logger.debug("[getMovementIfChanged] Change to movement found, updating and returning new movement")
              val newResult: Either[ErrorResponse, NodeSeq] = xmlUtils.readXml(getMovementIfChangedResponse.result)

              storeAndReturn(newResult, None)(getMovementRequest)
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
      parseResult(XmlReader.of[GetMovementResponse].read(value))
  }

  private[services] def getNewMovement(getMovementRequest: GetMovementRequest, cachedMovement: Option[Either[ErrorResponse, GetMovementResponse]])
                                      (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, GetMovementResponse]] = {
    logger.debug("[getNewMovement] retrieving new movement and storing in mongo")
    if (isEnabled(SendToEIS)) {
      eisConnector.getRawMovement(getMovementRequest).flatMap { eisResponse =>
        storeAndReturn(eisResponse.map(_.movementView), cachedMovement)(getMovementRequest)
      }
    } else {
      chrisConnector.postChrisSOAPRequest(getMovementRequest).flatMap { chrisResponse =>
        storeAndReturn(chrisResponse, cachedMovement)(getMovementRequest)
      }
    }
  }

  private[services] def storeAndReturn(response: Either[ErrorResponse, NodeSeq], cachedMovement: Option[Either[ErrorResponse, GetMovementResponse]])(getMovementRequest: GetMovementRequest)(implicit ec: ExecutionContext): Future[Either[ErrorResponse, GetMovementResponse]] =
    {
      for {
        res <- EitherT.fromEither[Future](response)
        movement <- EitherT.fromEither[Future](parseResult(XmlReader.of[GetMovementResponse].read(res)))
        resString = xmlUtils.trimWhitespaceFromXml(res)
        getMovementMongoResponse = response.map(_ => GetMovementMongoResponse(getMovementRequest.arc, movement.sequenceNumber, JsString(resString.toString())))
        getMovementMongoResponseRight <- EitherT.fromEither[Future](getMovementMongoResponse)
        _ = cachedMovement match {
          case Some(Right(cache)) if cache == movement || cache.sequenceNumber > movement.sequenceNumber =>
            //If the movement is the same, or the cached movement is newer than the movement retrieved then don't update Mongo.
            Future.successful(getMovementMongoResponseRight)
          case _ =>
            repository.set(getMovementMongoResponseRight)
        }
      } yield movement
    }.value
}
