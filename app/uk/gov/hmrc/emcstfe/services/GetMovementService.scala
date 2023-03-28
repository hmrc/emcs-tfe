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
import com.google.inject.{Inject, Singleton}
import uk.gov.hmrc.emcstfe.connectors.ChrisConnector
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.mongo.GetMovementMongoResponse
import uk.gov.hmrc.emcstfe.models.request.GetMovementRequest
import uk.gov.hmrc.emcstfe.models.response.{ErrorResponse, GetMovementResponse}
import uk.gov.hmrc.emcstfe.repositories.GetMovementRepository
import uk.gov.hmrc.emcstfe.utils.Logging
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetMovementService @Inject()(
                                    connector: ChrisConnector,
                                    repository: GetMovementRepository
                                  ) extends Logging {
  def getMovement(getMovementRequest: GetMovementRequest)(implicit hc: HeaderCarrier, ec: ExecutionContext, request: UserRequest[_]): Future[Either[ErrorResponse, GetMovementResponse]] = {
    val chrisResponseF: Future[Either[ErrorResponse, GetMovementResponse]] = connector.postChrisSOAPRequest[GetMovementResponse](getMovementRequest)

    chrisResponseF.flatMap {
      chrisResponse =>
        val getMovementMongoResponse: Either[ErrorResponse, GetMovementMongoResponse] = chrisResponse.map {
          GetMovementMongoResponse(request.internalId, request.ern, getMovementRequest.arc, _)
        }

        val responseAfterStoringInMongo: EitherT[Future, ErrorResponse, GetMovementResponse] = {
          for {
            getMovementMongoResponseRight <- EitherT.fromEither[Future](getMovementMongoResponse)
            _ <- EitherT(repository.set(getMovementMongoResponseRight).recover(recovery))
            res <- EitherT.fromEither[Future](chrisResponse)
          } yield {
            res
          }
        }

        responseAfterStoringInMongo.value
    }
  }
}
