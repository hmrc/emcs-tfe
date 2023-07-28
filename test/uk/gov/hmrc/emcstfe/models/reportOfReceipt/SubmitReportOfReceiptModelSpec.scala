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

import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.emcstfe.fixtures.SubmitReportOfReceiptFixtures
import uk.gov.hmrc.emcstfe.models.XmlModelBaseSpec
import uk.gov.hmrc.emcstfe.models.common.AcceptMovement._

class SubmitReportOfReceiptModelSpec extends XmlModelBaseSpec with SubmitReportOfReceiptFixtures {

  "SubmitReportOfReceiptModel" must {

    "have the correct values for the global conclusion" in {
      maxSubmitReportOfReceiptModel.copy(acceptMovement = Satisfactory).globalConclusion shouldBe 1
      maxSubmitReportOfReceiptModel.copy(acceptMovement = Unsatisfactory).globalConclusion shouldBe 2
      maxSubmitReportOfReceiptModel.copy(acceptMovement = Refused).globalConclusion shouldBe 3
      maxSubmitReportOfReceiptModel.copy(acceptMovement = PartiallyRefused).globalConclusion shouldBe 4
    }

    testJsonToModelToXml(
      "max fields",
      Json.toJson(maxSubmitReportOfReceiptModel).as[JsObject],
      maxSubmitReportOfReceiptModel,
      maxSubmitReportOfReceiptModelXML
    )

    testJsonToModelToXml(
      "min fields",
      Json.toJson(minSubmitReportOfReceiptModel).as[JsObject],
      minSubmitReportOfReceiptModel,
      minSubmitReportOfReceiptModelXML
    )
  }
}
