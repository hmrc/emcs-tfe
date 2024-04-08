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

package uk.gov.hmrc.emcstfe.models.nrs

import play.api.libs.json.Json
import uk.gov.hmrc.emcstfe.fixtures.NRSBrokerFixtures
import uk.gov.hmrc.emcstfe.models.nrs.NotableEvent.CreateMovementNotableEvent
import uk.gov.hmrc.emcstfe.support.TestBaseSpec
import uk.gov.hmrc.http.Authorization

import java.time.Instant

class NRSPayloadSpec extends TestBaseSpec with NRSBrokerFixtures {

  ".apply" should {

    "generate the correct payload by encoding, hashing the payload and applying the correct attributes" in {

      val result = NRSPayload.apply(testPlainTextPayload, CreateMovementNotableEvent, identityDataModel, testErn, Instant.ofEpochMilli(1L))(hc.copy(authorization = Some(Authorization("Bearer token"))), implicitly)

      result shouldBe nrsPayloadModel.copy(metadata = nrsPayloadModel.metadata.copy(headerData = Json.obj("Host" -> "localhost")))
    }

    "throw an exception when an auth token is not in the HeaderCarrier" in {

      intercept[NoSuchElementException](NRSPayload.apply(testPlainTextPayload, CreateMovementNotableEvent, identityDataModel, testErn, Instant.ofEpochMilli(1L))(hc.copy(authorization = None), implicitly))
    }
  }

  ".writes" should {

    "generate the correct JSON" in {

      Json.toJson(nrsPayloadModel)(NRSPayload.writes) shouldBe nrsPayloadJson
    }
  }
}
