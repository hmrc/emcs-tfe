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
import uk.gov.hmrc.emcstfe.connectors.ChrisConnector
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.mongo.GetMovementMongoResponse
import uk.gov.hmrc.emcstfe.models.request.{GetMovementIfChangedRequest, GetMovementRequest}
import uk.gov.hmrc.emcstfe.models.response.{ErrorResponse, GetMovementIfChangedResponse, GetMovementResponse}
import uk.gov.hmrc.emcstfe.repositories.GetMovementRepository
import uk.gov.hmrc.emcstfe.utils.XmlResultParser.handleParseResult
import uk.gov.hmrc.emcstfe.utils.{Logging, XmlUtils}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetMovementService @Inject()(
                                    connector: ChrisConnector,
                                    repository: GetMovementRepository,
                                    soapUtils: XmlUtils
                                  ) extends Logging {
  def getMovement(getMovementRequest: GetMovementRequest)(implicit hc: HeaderCarrier, ec: ExecutionContext, request: UserRequest[_]): Future[Either[ErrorResponse, GetMovementResponse]] = {
    repository.get(request.internalId, request.ern, getMovementRequest.arc).flatMap {
      case Some(value) =>
        logger.info("[getMovement] Matching movement found, calling GetMovementIfChanged")
        getMovementIfChanged(getMovementRequest, value)
      case None =>
        logger.info("[getMovement] No matching movement found, calling GetMovement")
        getNewMovement(getMovementRequest)
    }
  }

  private[services] def getMovementIfChanged(getMovementRequest: GetMovementRequest, repositoryResult: GetMovementMongoResponse)(implicit hc: HeaderCarrier, ec: ExecutionContext, request: UserRequest[_]): Future[Either[ErrorResponse, GetMovementResponse]] = {
    val chrisResponseF: Future[Either[ErrorResponse, GetMovementIfChangedResponse]] = connector.postChrisSOAPRequest[GetMovementIfChangedResponse](GetMovementIfChangedRequest(getMovementRequest.exciseRegistrationNumber, getMovementRequest.arc))
    chrisResponseF.flatMap {
      chrisResponse =>
        chrisResponse.map {
          getMovementIfChangedResponse =>
            if (getMovementIfChangedResponse.result.trim.isEmpty) {
              // if Results is empty, return `value`
              logger.info("[getMovementIfChanged] No change to movement, returning movement from mongo")
              Future.successful(Right(repositoryResult.data))
            } else {
              // if Results is not empty:
              //  - store new results
              //  - return new results
              logger.info("[getMovementIfChanged] Change to movement found, updating and returning new movement")
              val newResult: Either[ErrorResponse, GetMovementResponse] = soapUtils.readXml(getMovementIfChangedResponse.result).flatMap {
                xml => handleParseResult(XmlReader.of[GetMovementResponse].read(xml))
              }

              storeAndReturn(newResult)(getMovementRequest)
            }
        }.sequence.map(_.flatten)
    }
  }

  private[services] def getNewMovement(getMovementRequest: GetMovementRequest)(implicit hc: HeaderCarrier, ec: ExecutionContext, request: UserRequest[_]): Future[Either[ErrorResponse, GetMovementResponse]] = {
    val chrisResponseF: Future[Either[ErrorResponse, GetMovementResponse]] = connector.postChrisSOAPRequest[GetMovementResponse](getMovementRequest)

    chrisResponseF.flatMap {
      chrisResponse =>
        storeAndReturn(chrisResponse)(getMovementRequest)
    }
  }

  private[services] def storeAndReturn(chrisResponse: Either[ErrorResponse, GetMovementResponse])(getMovementRequest: GetMovementRequest)(implicit ec: ExecutionContext, request: UserRequest[_]): Future[Either[ErrorResponse, GetMovementResponse]] = {

    val responseAfterStoringInMongo: EitherT[Future, ErrorResponse, GetMovementResponse] = {
      for {
        res <- EitherT.fromEither[Future](chrisResponse)

        getMovementMongoResponse = chrisResponse.map(GetMovementMongoResponse(request.internalId, request.ern, getMovementRequest.arc, _))

        getMovementMongoResponseRight <- EitherT.fromEither[Future](getMovementMongoResponse)

        _ <- EitherT(repository.set(getMovementMongoResponseRight).recover(recovery))
      } yield {
        res
      }
    }

    responseAfterStoringInMongo.value
  }
}
