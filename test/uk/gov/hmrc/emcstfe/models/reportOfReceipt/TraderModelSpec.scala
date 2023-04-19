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

package uk.gov.hmrc.emcstfe.models.reportOfReceipt

import play.api.libs.json.Json
import uk.gov.hmrc.emcstfe.fixtures.TraderModelFixtures
import uk.gov.hmrc.emcstfe.models.reportOfReceipt.TraderModel
import uk.gov.hmrc.emcstfe.support.UnitSpec

class TraderModelSpec extends UnitSpec with TraderModelFixtures {

  "TraderModel" must {

    "for the maximum number of fields" must {

      "be possible to serialise and de-serialise to/from JSON" in {
        Json.toJson(maxTraderModel).as[TraderModel] shouldBe maxTraderModel
      }

      "write to XML" in {
        maxTraderModel.toXml shouldBe maxTraderModelXML
      }
    }

    "for the minimum number of fields" must {

      "be possible to serialise and de-serialise to/from JSON" in {
        Json.toJson(minTraderModel).as[TraderModel] shouldBe minTraderModel
      }

      "write to XML" in {
        minTraderModel.toXml shouldBe minTraderModelXML
      }
    }
  }
}
