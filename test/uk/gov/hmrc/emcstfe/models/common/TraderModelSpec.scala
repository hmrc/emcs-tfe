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

import com.lucidchart.open.xtract.{ParseSuccess, XmlReader}
import uk.gov.hmrc.emcstfe.fixtures.TraderModelFixtures
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

class TraderModelSpec extends TestBaseSpec with TraderModelFixtures {

  "TraderModel" when {

    Seq(
      ConsigneeTrader,
      ConsignorTrader,
      PlaceOfDispatchTrader,
      DeliveryPlaceTrader,
      TransportTrader,
      GuarantorTrader
    ).foreach { traderType =>

      s".toXml($traderType)" must {

        "for the Max model" must {

          "output the expected XML" in {
            maxTraderModel(traderType).toXml(traderType) shouldBe maxTraderModelXML(traderType)
          }
        }

        "for the Min model" must {

          "output the expected XML" in {
            minTraderModel.toXml(traderType) shouldBe minTraderModelXML
          }
        }
      }

      s"when reading from XML for $traderType" must {

        "construct the model as expected" in {
          XmlReader.of[TraderModel](TraderModel.xmlReads(traderType)).read(maxTraderModelXML(traderType)) shouldBe ParseSuccess(maxTraderModel(traderType))
        }
      }
    }

    "calculate countryCode" when {
      "traderId.length >= 2" in {
        maxTraderModel(ConsigneeTrader).countryCode shouldBe Some("GB")
      }
    }

    "return for countryCode None" when {
      "traderId is None" in {
        minTraderModel.countryCode shouldBe None
      }
      "traderId.length < 2" in {
        maxTraderModel(ConsigneeTrader).copy(traderExciseNumber = Some("a")).countryCode shouldBe None
      }
    }

    ".reportOfReceiptXMLReads" must {
      "be able to read valid XML and parse to a TraderModel" in {
        TraderModel.reportOfReceiptXMLReads.read(maxTraderModelXML(ConsigneeTrader)) shouldBe ParseSuccess(maxTraderModel(ConsigneeTrader))
      }
    }
  }
}
