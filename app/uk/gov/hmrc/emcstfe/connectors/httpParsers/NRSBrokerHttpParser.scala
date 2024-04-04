/*
 * Copyright 2024 HM Revenue & Customs
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

import play.api.http.Status.ACCEPTED
import play.api.libs.json.{Json, JsonValidationError}
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{NRSBrokerJsonParsingError, UnexpectedDownstreamResponseError}
import uk.gov.hmrc.emcstfe.models.response.nrsBroker.NRSBrokerInsertPayloadResponse
import uk.gov.hmrc.emcstfe.utils.Logging
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

import scala.util.{Failure, Success, Try}

trait NRSBrokerHttpParser extends Logging {

  implicit object NRSBrokerReads extends HttpReads[Either[ErrorResponse, NRSBrokerInsertPayloadResponse]] {
    override def read(method: String, url: String, response: HttpResponse): Either[ErrorResponse, NRSBrokerInsertPayloadResponse] = {
      response.status match {
        case ACCEPTED => Try {
          Json.fromJson(response.json)(NRSBrokerInsertPayloadResponse.reads).fold(
            errors => Left(NRSBrokerJsonParsingError(errors.flatMap(_._2).toSeq)),
            Right(_)
          )
        } match {
          case Failure(exception) =>
            logger.error(exception.getMessage.take(10000), exception)
            Left(NRSBrokerJsonParsingError(Seq(JsonValidationError(exception.getMessage))))
          case Success(value) => value
        }
        case status =>
          logger.warn(s"[read] Unexpected status from NRS broker: $status")
          Left(UnexpectedDownstreamResponseError)
      }
    }
  }
}