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

import uk.gov.hmrc.emcstfe.fixtures.SubmitChangeDestinationFixtures
import uk.gov.hmrc.emcstfe.support.UnitSpec

import scala.xml.Utility.trim

class TransportDetailsModelSpec extends UnitSpec with SubmitChangeDestinationFixtures {
  import TransportDetailsFixtures._

  s"TransportDetailsModel with max fields" should {
    "convert JSON to a model correctly" in {
      transportDetailsJsonMax.as[TransportDetailsModel] shouldBe transportDetailsModelMax
    }
    "convert a model to XML correctly" in {
      trim(transportDetailsModelMax.toXml) shouldBe trim(transportDetailsXmlMax)
    }
  }

  s"TransportDetailsModel with min fields" should {
    "convert JSON to a model correctly" in {
      transportDetailsJsonMin.as[TransportDetailsModel] shouldBe transportDetailsModelMin
    }
    "convert a model to XML correctly" in {
      trim(transportDetailsModelMin.toXml) shouldBe trim(transportDetailsXmlMin)
    }
  }
}
