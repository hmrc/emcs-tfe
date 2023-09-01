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

package uk.gov.hmrc.emcstfe.models.explainDelay

import play.api.libs.json.Json
import play.api.test.FakeRequest
import uk.gov.hmrc.emcstfe.fixtures.SubmitExplainDelayFixtures
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.xml.Utility.trim

class SubmitExplainDelayModelSpec extends TestBaseSpec with SubmitExplainDelayFixtures {

  implicit val request = UserRequest(FakeRequest(), testErn, testInternalId, testCredId)

  "SubmitExplainDelayModel" must {

    "for the maximum number of fields" must {

      "be possible to serialise and de-serialise to/from JSON" in {
        Json.toJson(maxSubmitExplainDelayModel).as[SubmitExplainDelayModel] shouldBe maxSubmitExplainDelayModel
      }

      "write to XML" in {
        trim(maxSubmitExplainDelayModel.toXml).toString shouldBe trim(maxSubmitExplainDelayModelXML).toString
      }
    }

    "for the minimum number of fields" must {

      "be possible to serialise and de-serialise to/from JSON" in {
        Json.toJson(minSubmitExplainDelayModel).as[SubmitExplainDelayModel] shouldBe minSubmitExplainDelayModel
      }

      "write to XML" in {
        trim(minSubmitExplainDelayModel.toXml).toString shouldBe trim(minSubmitExplainDelayModelXML).toString
      }
    }
  }
}
