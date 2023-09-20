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

package uk.gov.hmrc.emcstfe.models.common

import play.api.libs.json.Json
import uk.gov.hmrc.emcstfe.fixtures.AddressModelFixtures
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

class AddressModelSpec extends TestBaseSpec with AddressModelFixtures {

  "AddressModel" must {

    Seq(
      ConsigneeTrader,
      ConsignorTrader,
      PlaceOfDispatchTrader,
      DeliveryPlaceTrader,
      TransportTrader,
      GuarantorTrader
    ).foreach { traderType =>

      s"for trader type of $traderType" when {

        "for the maximum number of fields" must {

          "be possible to serialise and de-serialise to/from JSON" in {
            Json.toJson(maxAddressModel).as[AddressModel] shouldBe maxAddressModel
          }

          "write to XML" in {
            maxAddressModel.toXml(traderType) shouldBe maxAddressModelXML(traderType)
          }
        }

        "for the minimum number of fields" must {

          "be possible to serialise and de-serialise to/from JSON" in {
            Json.toJson(minAddressModel).as[AddressModel] shouldBe minAddressModel
          }

          "write to XML" in {
            minAddressModel.toXml(traderType) shouldBe minAddressModelXML
          }
        }
      }
    }
  }
}
