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

package uk.gov.hmrc.emcstfe.models.response

import play.api.libs.json.Json
import uk.gov.hmrc.emcstfe.fixtures.TraderKnownFactsFixtures
import uk.gov.hmrc.emcstfe.support.UnitSpec

class TraderKnownFactsSpec extends UnitSpec with TraderKnownFactsFixtures {

  "reads" should {
    "read from emcs-tfe-reference-data" in {
      Json.parse(traderKnownFactsCandEJson).as[TraderKnownFacts] shouldBe testTraderKnownFactsModel
    }
    "read from ETDS" in {
      Json.parse(traderKnownFactsETDSJson).as[TraderKnownFacts] shouldBe testTraderKnownFactsModel
    }
  }

  "writes" should {
    "parse a model to JSON" in {
      Json.toJson(testTraderKnownFactsModel) shouldBe Json.parse(testTraderKnownFactsJson)
    }
  }

}
