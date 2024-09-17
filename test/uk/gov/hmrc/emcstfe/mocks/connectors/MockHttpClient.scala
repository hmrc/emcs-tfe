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

package uk.gov.hmrc.emcstfe.mocks.connectors

import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Writes
import uk.gov.hmrc.emcstfe.models.request.eis.EisHeaders
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads}

import java.time.Instant
import java.time.temporal.ChronoUnit
import scala.concurrent.{ExecutionContext, Future}

trait MockHttpClient extends MockFactory {

  val mockHttpClient: HttpClient = mock[HttpClient]

  object MockHttpClient extends Matchers {

    def get[T](url: String,
               bearerToken: String,
               parameters: Seq[(String, String)] = Seq.empty,
               headers: Seq[(String, String)] = Seq.empty): CallHandler[Future[T]] = {
      (mockHttpClient
        .GET(_: String, _: Seq[(String, String)], _: Seq[(String, String)])(_: HttpReads[T], _: HeaderCarrier, _: ExecutionContext))
        .expects(assertArgs {
          (actualUrl: String, actualParams: Seq[(String, String)], actualHeaders, _, hc, _) => {
            actualUrl shouldBe url
            actualParams shouldBe parameters
            actualHeaders.filterNot(_._1 == "datetime") shouldBe headers.filterNot(_._1 == "datetime")
            bearerToken shouldBe hc.authorization.get.value
          }
        })
    }

    def get[T](url: String): CallHandler[Future[T]] = {
      (mockHttpClient
        .GET(_: String, _: Seq[(String, String)], _: Seq[(String, String)])(_: HttpReads[T], _: HeaderCarrier, _: ExecutionContext))
        .expects(url, *, *, *, *, *)
    }


    def postString[T](url: String,
                      body: String,
                      headers: Seq[(String, String)] = Seq.empty): CallHandler[Future[T]] = {
      (mockHttpClient
        .POSTString[T](_: String, _: String, _: Seq[(String, String)])(_: HttpReads[T], _: HeaderCarrier, _: ExecutionContext))
        .expects(assertArgs { (actualUrl, actualBody, actualHeaders, _, _, _) => {
          actualUrl shouldBe url
          actualHeaders shouldBe headers
        }
        })
    }

    def postJson[I, T](url: String,
                       bearerToken: String,
                       body: I,
                       headers: Seq[(String, String)] = Seq.empty): CallHandler[Future[T]] = {
      (mockHttpClient
        .POST[I, T](_: String, _: I, _: Seq[(String, String)])(_: Writes[I], _: HttpReads[T], _: HeaderCarrier, _: ExecutionContext))
        .expects(assertArgs { (actualUrl, actualBody, actualHeaders, _, _, hc, _) => {
          actualUrl shouldBe url
          actualBody shouldBe body
          actualHeaders.filterNot(_._1 == EisHeaders.dateTime) shouldBe headers.filterNot(_._1 == EisHeaders.dateTime)
          //Can't compare the original timestamps so truncate to days instead
          Instant.parse(actualHeaders.find(_._1 == EisHeaders.dateTime).get._2).truncatedTo(ChronoUnit.DAYS) shouldBe Instant.parse(headers.find(_._1 == EisHeaders.dateTime).get._2).truncatedTo(ChronoUnit.DAYS)
          bearerToken shouldBe hc.authorization.get.value
        }
        })
    }

    def post[I, T](url: String, body: I): CallHandler[Future[T]] = {
      (mockHttpClient
        .POST[I, T](_: String, _: I, _: Seq[(String, String)])(_: Writes[I], _: HttpReads[T], _: HeaderCarrier, _: ExecutionContext))
        .expects(assertArgs { (actualUrl, actualBody: I, _, _, _, _, _) => {
          actualUrl shouldBe url
          actualBody shouldBe body
        }
        })
    }

    def put[I, T](url: String, body: I): CallHandler[Future[T]] = {
      (mockHttpClient
        .PUT[I, T](_: String, _: I, _: Seq[(String, String)])(_: Writes[I], _: HttpReads[T], _: HeaderCarrier, _: ExecutionContext))
        .expects(assertArgs { (actualUrl, actualBody: I, _, _, _, _, _) => {
          actualUrl shouldBe url
          actualBody shouldBe body
        }
        })
    }

    def putEmpty[T](url: String,
                    bearerToken: String,
                    headers: Seq[(String, String)]): CallHandler[Future[T]] = {
      (mockHttpClient
        .PUTString[T](_: String, _: String, _: Seq[(String, String)])(_: HttpReads[T], _: HeaderCarrier, _: ExecutionContext))
        .expects(assertArgs { (actualUrl, _, actualHeaders, _, hc, _) => {
          actualUrl shouldBe url
          actualHeaders.filterNot(_._1 == "datetime") shouldBe headers.filterNot(_._1 == "datetime")
          bearerToken shouldBe hc.authorization.get.value
        }
        })
    }

    def delete[T](url: String,
                  bearerToken: String,
                  headers: Seq[(String, String)]): CallHandler[Future[T]] = {
      (mockHttpClient
        .DELETE(_: String, _: Seq[(String, String)])(_: HttpReads[T], _: HeaderCarrier, _: ExecutionContext))
        .expects(assertArgs { (actualUrl, actualHeaders, _, hc, _) => {
          actualUrl shouldBe url
          actualHeaders.filterNot(_._1 == "datetime") shouldBe headers.filterNot(_._1 == "datetime")
          bearerToken shouldBe hc.authorization.get.value
        }
        })
    }

  }

}
