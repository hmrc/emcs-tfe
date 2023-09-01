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

package uk.gov.hmrc.emcstfe.models.cancellationOfMovement

import uk.gov.hmrc.emcstfe.models.cancellationOfMovement.CancellationReasonType.TypingError
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.xml.Utility.trim

class CancellationReasonModelSpec extends TestBaseSpec {

  "CancellationReasonModel" must {
    ".toXML" when {
      "Complementary information is included" must {
        "Output the correct XML" in {
          trim(CancellationReasonModel(TypingError, Some("testComplementaryInformation")).toXml).toString shouldBe
            trim {
              <urn:Cancellation>
                <urn:CancellationReasonCode>1</urn:CancellationReasonCode>
                <urn:ComplementaryInformation language="en">testComplementaryInformation</urn:ComplementaryInformation>
              </urn:Cancellation>
            }.toString
        }
      }

      "Complementary information is NOT included" must {
        "Output the correct XML" in {
          trim(CancellationReasonModel(TypingError, None).toXml).toString shouldBe
            trim {
              <urn:Cancellation>
                <urn:CancellationReasonCode>1</urn:CancellationReasonCode>
              </urn:Cancellation>
            }.toString
        }
      }
    }
  }
}