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
import uk.gov.hmrc.emcstfe.fixtures.SubmitReportOfReceiptModelFixtures
import uk.gov.hmrc.emcstfe.models.common.AcceptMovement._
import uk.gov.hmrc.emcstfe.models.reportOfReceipt.SubmitReportOfReceiptModel
import uk.gov.hmrc.emcstfe.support.UnitSpec

import scala.xml.Utility.trim

class SubmitReportOfReceiptModelSpec extends UnitSpec with SubmitReportOfReceiptModelFixtures {

  "SubmitReportOfReceiptModel" must {

    "have the correct values for the global conclusion" in {
      maxSubmitReportOfReceiptModel.copy(acceptMovement = Satisfactory).globalConclusion shouldBe 1
      maxSubmitReportOfReceiptModel.copy(acceptMovement = Unsatisfactory).globalConclusion shouldBe 2
      maxSubmitReportOfReceiptModel.copy(acceptMovement = Refused).globalConclusion shouldBe 3
      maxSubmitReportOfReceiptModel.copy(acceptMovement = PartiallyRefused).globalConclusion shouldBe 4
    }

    "for the maximum number of fields" must {

      "be possible to serialise and de-serialise to/from JSON" in {
        Json.toJson(maxSubmitReportOfReceiptModel).as[SubmitReportOfReceiptModel] shouldBe maxSubmitReportOfReceiptModel
      }

      "write to XML" in {
        trim(maxSubmitReportOfReceiptModel.toXml).toString shouldBe trim(maxSubmitReportOfReceiptModelXML).toString
      }
    }

    "for the minimum number of fields" must {

      "be possible to serialise and de-serialise to/from JSON" in {
        Json.toJson(minSubmitReportOfReceiptModel).as[SubmitReportOfReceiptModel] shouldBe minSubmitReportOfReceiptModel
      }

      "write to XML" in {
        trim(minSubmitReportOfReceiptModel.toXml).toString shouldBe trim(minSubmitReportOfReceiptModelXML).toString
      }
    }
  }
}
