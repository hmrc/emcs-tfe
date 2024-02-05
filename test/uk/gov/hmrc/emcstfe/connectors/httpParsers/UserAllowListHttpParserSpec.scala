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

import play.api.http.Status
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.UnexpectedDownstreamResponseError
import uk.gov.hmrc.emcstfe.support.TestBaseSpec
import uk.gov.hmrc.http.HttpResponse

class UserAllowListHttpParserSpec extends TestBaseSpec with UserAllowListHttpParser {

  "UserAllowListReads.read(method: String, url: String, response: HttpResponse)" should {

    "return 'true'" when {

      s"an OK (${Status.OK}) response is retrieved" in {
        UserAllowListReads.read("", "", HttpResponse(Status.OK, "")) shouldBe Right(true)
      }
    }

    "return 'false'" when {

      s"a NOT_FOUND (${Status.NOT_FOUND}) response is retrieved" in {
        UserAllowListReads.read("", "", HttpResponse(Status.NOT_FOUND, "")) shouldBe Right(false)
      }
    }

    "return UnexpectedDownstreamError" when {

      "status is anything else" in {
        UserAllowListReads.read("", "", HttpResponse(Status.INTERNAL_SERVER_ERROR, "")) shouldBe Left(UnexpectedDownstreamResponseError)
      }
    }
  }
}