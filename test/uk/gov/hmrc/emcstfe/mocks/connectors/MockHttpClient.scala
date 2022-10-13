/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.mocks.connectors

import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Writes
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads}

import scala.concurrent.{ExecutionContext, Future}

trait MockHttpClient extends MockFactory {

  val mockHttpClient: HttpClient = mock[HttpClient]

  object MockHttpClient extends Matchers {

    def get[T](url: String,
               parameters: Seq[(String, String)] = Seq.empty): CallHandler[Future[T]] = {
      (mockHttpClient
        .GET(_: String, _: Seq[(String, String)], _: Seq[(String, String)])(_: HttpReads[T], _: HeaderCarrier, _: ExecutionContext))
        .expects(assertArgs {
          (actualUrl: String, actualParams: Seq[(String, String)], _, _, _, _) => {
            actualUrl shouldBe url
            actualParams shouldBe parameters
          }
        })
    }

    def post[I, T](url: String,
                   body: I): CallHandler[Future[T]] = {
      (mockHttpClient
        .POST[I, T](_: String, _: I, _: Seq[(String, String)])(_: Writes[I], _: HttpReads[T], _: HeaderCarrier, _: ExecutionContext))
        .expects(assertArgs { (actualUrl, actualBody: I, _, _, _, _, _) => {
          actualUrl shouldBe url
          actualBody shouldBe body
        }
        })
    }

    def put[I, T](url: String,
                  body: I): CallHandler[Future[T]] = {
      (mockHttpClient
        .PUT[I, T](_: String, _: I, _: Seq[(String, String)])(_: Writes[I], _: HttpReads[T], _: HeaderCarrier, _: ExecutionContext))
        .expects(assertArgs { (actualUrl, actualBody: I, _, _, _, _, _) => {
          actualUrl shouldBe url
          actualBody shouldBe body
        }
        })
    }

    def delete[T](url: String): CallHandler[Future[T]] = {
      (mockHttpClient
        .DELETE(_: String, _: Seq[(String, String)])(_: HttpReads[T], _: HeaderCarrier, _: ExecutionContext))
        .expects(assertArgs { (actualUrl, _, _, _, _) => {
          actualUrl shouldBe url
        }
        })
    }

  }

}
