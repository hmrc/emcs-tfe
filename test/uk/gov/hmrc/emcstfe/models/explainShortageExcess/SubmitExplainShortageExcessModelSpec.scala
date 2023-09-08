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

package uk.gov.hmrc.emcstfe.models.explainShortageExcess

import uk.gov.hmrc.emcstfe.fixtures.SubmitExplainShortageExcessFixtures
import uk.gov.hmrc.emcstfe.models.XmlModelBaseSpec
import uk.gov.hmrc.emcstfe.models.common.SubmitterType.{Consignee, Consignor}

class SubmitExplainShortageExcessModelSpec extends XmlModelBaseSpec with SubmitExplainShortageExcessFixtures {
  import SubmitExplainShortageExcessFixtures._

  testJsonToModelToXml(
    scenario = "Consignor max fields",
    json = submitExplainShortageExcessJsonMax(Consignor),
    model = submitExplainShortageExcessModelMax(Consignor),
    xml = submitExplainShortageExcessXmlMax(Consignor)
  )

  testJsonToModelToXml(
    scenario = "Consignee max fields",
    json = submitExplainShortageExcessJsonMax(Consignee),
    model = submitExplainShortageExcessModelMax(Consignee),
    xml = submitExplainShortageExcessXmlMax(Consignee)
  )

  testJsonToModelToXml(
    scenario = "Consignor min fields",
    json = submitExplainShortageExcessJsonMin(Consignor),
    model = submitExplainShortageExcessModelMin(Consignor),
    xml = submitExplainShortageExcessXmlMin(Consignor)
  )

  testJsonToModelToXml(
    scenario = "Consignee min fields",
    json = submitExplainShortageExcessJsonMin(Consignee),
    model = submitExplainShortageExcessModelMin(Consignee),
    xml = submitExplainShortageExcessXmlMin(Consignee)
  )
}
