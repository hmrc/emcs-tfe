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

import play.api.test.FakeRequest
import uk.gov.hmrc.emcstfe.fixtures.SubmitExplainShortageExcessFixtures
import uk.gov.hmrc.emcstfe.mocks.connectors.MockChrisConnector
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.request.SubmitExplainShortageExcessRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.XmlValidationError
import uk.gov.hmrc.emcstfe.support.UnitSpec

import scala.concurrent.Future

class SubmitExplainShortageExcessServiceSpec extends UnitSpec with SubmitExplainShortageExcessFixtures {

  import SubmitExplainShortageExcessFixtures.submitExplainShortageExcessModelMax

  trait Test extends MockChrisConnector {
    implicit val request = UserRequest(FakeRequest(), testErn, testInternalId, testCredId)
    val submitExplainShortageExcessRequest: SubmitExplainShortageExcessRequest = SubmitExplainShortageExcessRequest(submitExplainShortageExcessModelMax)
    val service: SubmitExplainShortageExcessService = new SubmitExplainShortageExcessService(mockConnector)
  }

  "submit" should {
    "return a Right" when {
      "connector call is successful and XML is the correct format" in new Test {

        MockConnector.submitExplainShortageExcessChrisSOAPRequest(submitExplainShortageExcessRequest).returns(
          Future.successful(Right(chrisSuccessResponse))
        )

        await(service.submit(submitExplainShortageExcessModelMax)) shouldBe Right(chrisSuccessResponse)
      }
    }
    "return a Left" when {
      "connector call is unsuccessful" in new Test {

        MockConnector.submitExplainShortageExcessChrisSOAPRequest(submitExplainShortageExcessRequest).returns(
          Future.successful(Left(XmlValidationError))
        )

        await(service.submit(submitExplainShortageExcessModelMax)) shouldBe Left(XmlValidationError)
      }
    }
  }
}